package thaumcraft.common.entities.construct;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.event.level.BlockDropsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBoreDig;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.world.aura.AuraHandler;


@net.neoforged.fml.common.EventBusSubscriber(modid = "thaumcraft")
public class EntityArcaneBore extends EntityOwnedConstruct
{
    BlockPos digTarget;
    BlockPos digTargetPrev;
    float digCost;
    int paused;
    int maxPause;
    long soundDelay;
    Object beam1;
    double beamLength;
    private static final HashMap<Integer, ArrayList<ItemStack>> drops = new HashMap<>();
    int breakCounter;
    int digDelay;
    int digDelayMax;
    float radInc;
    public int spiral;
    public float currentRadius;
    private float charge;
    private static final EntityDataAccessor<Direction> FACING =
        SynchedEntityData.defineId(EntityArcaneBore.class, EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Boolean> ACTIVE =
        SynchedEntityData.defineId(EntityArcaneBore.class, EntityDataSerializers.BOOLEAN);
    public boolean clientDigging;

    @SuppressWarnings("unchecked")
    public EntityArcaneBore(Level worldIn, BlockPos pos, net.minecraft.core.Direction facing) {
        this((EntityType<? extends EntityArcaneBore>) null, worldIn, pos, facing);
    }

    public EntityArcaneBore(EntityType<? extends EntityArcaneBore> type, Level worldIn) {
        super(type, worldIn);
        digTarget = null;
        digTargetPrev = null;
        digCost = 0.25f;
        paused = 100;
        maxPause = 100;
        soundDelay = 0L;
        beam1 = null;
        beamLength = 0.0;
        breakCounter = 0;
        digDelay = 0;
        digDelayMax = 0;
        radInc = 0.0f;
        spiral = 0;
        currentRadius = 0.0f;
        charge = 0.0f;
        clientDigging = false;
    }

    public EntityArcaneBore(EntityType<? extends EntityArcaneBore> type, Level worldIn, BlockPos pos, Direction facing) {
        this(type, worldIn);
        setFacing(facing);
        setPos(pos.getX() + 0.5, (double)pos.getY(), pos.getZ() + 0.5);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.Mob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 50.0)
            .add(Attributes.FOLLOW_RANGE, 32.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FACING, Direction.DOWN);
        builder.define(ACTIVE, false);
    }

    @SubscribeEvent
    public static void harvestBlockEvent(BlockDropsEvent event) {
        // stub — FakePlayer drop collection removed
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()) {
            setYRot(getYHeadRot());
            if (tickCount % 50 == 0) heal(1.0f);
            if (tickCount % 10 == 0 && getCharge() < 10.0f) rechargeVis();
            if (!isActive()) {
                digTarget = null;
            }
            if (!isPassenger()) {
                setActive(level().hasNeighborSignal(blockPosition().below()));
            }
        }
    }

    public boolean validInventory() {
        ItemStack stack = getMainHandItem();
        if (stack.isEmpty()) return false;
        if (stack.getMaxDamage() > 0 && stack.getDamageValue() + 1 >= stack.getMaxDamage()) return false;
        return stack.is(net.minecraft.tags.ItemTags.PICKAXES);
    }

    public int getDigRadius() {
        int r = 0;
        if (validInventory()) {
            r = 0; // enchantment value API removed; tier-based radius handled via infusion enchants
            r += EnumInfusionEnchantment.getInfusionEnchantmentLevel(getMainHandItem(), EnumInfusionEnchantment.DESTRUCTIVE) * 2;
        }
        return (r <= 1) ? 2 : r;
    }

    public int getDigDepth() {
        int r = getDigRadius() * 8;
        r += EnumInfusionEnchantment.getInfusionEnchantmentLevel(getMainHandItem(), EnumInfusionEnchantment.BURROWING) * 16;
        return r;
    }

    public int getFortune() {
        int r = 0;
        if (validInventory()) {
            // TODO: update to new enchantment API when Enchantments.FORTUNE holder is available
            r = EnumInfusionEnchantment.getInfusionEnchantmentLevel(getMainHandItem(), EnumInfusionEnchantment.SOUNDING);
        }
        return r;
    }

    public int getDigSpeed(BlockState blockState) {
        int speed = 0;
        if (validInventory()) {
            speed += (int)(getMainHandItem().getDestroySpeed(blockState) / 2.0f);
        }
        return speed;
    }

    public int getRefining() {
        if (!getMainHandItem().isEmpty()) {
            return EnumInfusionEnchantment.getInfusionEnchantmentLevel(getMainHandItem(), EnumInfusionEnchantment.REFINING);
        }
        return 0;
    }

    public boolean hasSilkTouch() {
        // TODO: update to new enchantment API
        return false;
    }

    @Override
    public void die(DamageSource cause) {
        super.die(cause);
        if (level() instanceof ServerLevel sl && !getMainHandItem().isEmpty()) {
            ItemEntity ie = new ItemEntity(sl, getX(), getY(), getZ(), getMainHandItem().copy());
            sl.addFreshEntity(ie);
        }
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource cause, boolean recentlyHit) {
        if (getRandom().nextFloat() < 0.2f) spawnAtLocation((net.minecraft.server.level.ServerLevel)level(), new net.minecraft.world.item.ItemStack(ItemsTC.mind));
        if (getRandom().nextFloat() < 0.2f) spawnAtLocation((net.minecraft.server.level.ServerLevel)level(), new net.minecraft.world.item.ItemStack(ItemsTC.morphicResonator));
        if (getRandom().nextFloat() < 0.2f) spawnAtLocation((net.minecraft.server.level.ServerLevel)level(), new net.minecraft.world.item.ItemStack(BlocksTC.crystalAir));
        if (getRandom().nextFloat() < 0.2f) spawnAtLocation((net.minecraft.server.level.ServerLevel)level(), new net.minecraft.world.item.ItemStack(BlocksTC.crystalEarth));
        if (getRandom().nextFloat() < 0.5f) spawnAtLocation((net.minecraft.server.level.ServerLevel)level(), new net.minecraft.world.item.ItemStack(ItemsTC.mechanismSimple));
        if (getRandom().nextFloat() < 0.5f) spawnAtLocation((net.minecraft.server.level.ServerLevel)level(), new net.minecraft.world.item.ItemStack(ItemsTC.plate));
        if (getRandom().nextFloat() < 0.5f) spawnAtLocation((net.minecraft.server.level.ServerLevel)level(), new net.minecraft.world.item.ItemStack(BlocksTC.plankGreatwood));
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (isRemoved()) return InteractionResult.PASS;
        if (player.getMainHandItem().getItem() instanceof net.minecraft.world.item.NameTagItem) {
            return InteractionResult.PASS;
        }
        if (isOwner(player)) {
            if (player.isCrouching()) {
                if (level() instanceof ServerLevel sl) {
                    playSound(SoundsTC.zap, 1.0f, 1.0f);
                    if (!getMainHandItem().isEmpty()) {
                        ItemEntity ie = new ItemEntity(sl, getX(), getY(), getZ(), getMainHandItem().copy());
                        sl.addFreshEntity(ie);
                    }
                    spawnAtLocation((net.minecraft.server.level.ServerLevel)level(), new net.minecraft.world.item.ItemStack(ItemsTC.turretPlacer));
                    discard();
                }
                player.swing(hand);
                return InteractionResult.SUCCESS;
            }
            // TODO: open arcane bore GUI
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    private void rechargeVis() {
        setCharge(getCharge() + AuraHandler.drainVis(level(), blockPosition(), 10.0f, false));
    }

    public boolean isActive() {
        return this.entityData.get(ACTIVE);
    }

    public void setActive(boolean active) {
        this.entityData.set(ACTIVE, active);
    }

    public Direction getFacing() {
        return this.entityData.get(FACING);
    }

    public void setFacing(Direction face) {
        this.entityData.set(FACING, face);
    }

    public float getCharge() {
        return charge;
    }

    public void setCharge(float c) {
        charge = c;
    }

    public int getVerticalFaceSpeed() {
        return 10;
    }

    @Override
    public void move(MoverType type, Vec3 movement) {
        super.move(type, new Vec3(movement.x / 5.0, movement.y, movement.z / 5.0));
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 16) {
            clientDigging = true;
        } else if (id == 17) {
            clientDigging = false;
        } else {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putFloat("charge", getCharge());
        output.putByte("faceing", (byte) getFacing().get3DDataValue());
        output.putBoolean("active", isActive());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setCharge(input.getFloatOr("charge", 0.0f));
        setFacing(Direction.from3DDataValue(input.getByteOr("faceing", (byte)0)));
        setActive(input.getBooleanOr("active", false));
    }
}
