package thaumcraft.common.entities.monster;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.common.entities.ai.pech.AINearestAttackableTargetPech;
import thaumcraft.common.entities.ai.pech.AIPechItemEntityGoto;
import thaumcraft.common.entities.ai.pech.AIPechTradePlayer;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.utils.InventoryUtils;


public class EntityPech extends Monster implements RangedAttackMob
{
    public NonNullList<ItemStack> loot;
    public boolean trading;
    private RangedAttackGoal aiArrowAttack;
    private RangedAttackGoal aiBlastAttack;
    private MeleeAttackGoal aiMeleeAttack;
    private AvoidEntityGoal<Player> aiAvoidPlayer;
    private static final EntityDataAccessor<Byte> TYPE =
        SynchedEntityData.defineId(EntityPech.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> ANGER =
        SynchedEntityData.defineId(EntityPech.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> TAMED =
        SynchedEntityData.defineId(EntityPech.class, EntityDataSerializers.BOOLEAN);
    public static Identifier LOOT = Identifier.fromNamespaceAndPath("thaumcraft", "pech");
    public float mumble;
    int chargecount;
    public static HashMap<Integer, Integer> valuedItems = new HashMap<>();
    public static HashMap<Integer, ArrayList<List>> tradeInventory = new HashMap<>();

    public EntityPech(EntityType<? extends EntityPech> type, Level world) {
        super(type, world);
        loot = NonNullList.withSize(9, ItemStack.EMPTY);
        trading = false;
        aiArrowAttack = new RangedAttackGoal(this, 0.6, 20, 50, 15.0f);
        aiBlastAttack = new RangedAttackGoal(this, 0.6, 20, 50, 15.0f);
        aiMeleeAttack = new MeleeAttackGoal(this, 0.6, false);
        aiAvoidPlayer = new AvoidEntityGoal<>(this, Player.class, 8.0f, 0.5, 0.6);
        mumble = 0.0f;
        chargecount = 0;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TYPE, (byte) 0);
        builder.define(ANGER, 0);
        builder.define(TAMED, false);
    }

    @Override
    protected void registerGoals() {
        getNavigation().setCanFloat(false);
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(1, new AIPechTradePlayer(this));
        goalSelector.addGoal(3, new AIPechItemEntityGoto(this));
        goalSelector.addGoal(6, new MoveTowardsRestrictionGoal(this, 0.5));
        goalSelector.addGoal(9, new RandomStrollGoal(this, 0.6));
        goalSelector.addGoal(10, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(11, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new AINearestAttackableTargetPech(this, Player.class, true));
    }

    public void setCombatTask() {
        if (!level().isClientSide()) {
            goalSelector.removeGoal(aiMeleeAttack);
            goalSelector.removeGoal(aiArrowAttack);
            goalSelector.removeGoal(aiBlastAttack);
            ItemStack itemstack = getMainHandItem();
            if (isTamed()) {
                goalSelector.removeGoal(aiAvoidPlayer);
            } else {
                goalSelector.addGoal(4, aiAvoidPlayer);
            }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float f) {
        // stub - would shoot arrows/spells
    }

    public int getPechType() {
        return this.entityData.get(TYPE);
    }

    public int getAnger() {
        return this.entityData.get(ANGER);
    }

    public boolean isTamed() {
        return this.entityData.get(TAMED);
    }

    public void setPechType(int par1) {
        this.entityData.set(TYPE, (byte) par1);
    }

    public void setAnger(int par1) {
        this.entityData.set(ANGER, par1);
    }

    public void setTamed(boolean par1) {
        this.entityData.set(TAMED, par1);
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putByte("PechType", (byte) getPechType());
        output.putShort("Anger", (short) getAnger());
        output.putBoolean("Tamed", isTamed());
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        setPechType(input.getByteOr("PechType", (byte)0));
        setAnger(input.getShortOr("Anger", (short)0));
        setTamed(input.getBooleanOr("Tamed", false));
        setCombatTask();
    }

    @Override
    public void tick() {
        super.tick();
        if (mumble > 0.0f) {
            mumble *= 0.75f;
        }
        if (getAnger() > 0) {
            setAnger(getAnger() - 1);
        }
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundsTC.pech_idle;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundsTC.pech_hit;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsTC.pech_death;
    }

    public boolean isValued(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return false;
        }
        boolean value = EntityPech.valuedItems.containsKey(Item.getId(item.getItem()));
        if (!value) {
            AspectList al = null /* CraftingManager removed */;
            if (al.getAmount(Aspect.DESIRE) > 1) {
                value = true;
            }
        }
        return value;
    }

    public int getValue(ItemStack item) {
        if (item == null || item.isEmpty()) {
            return 0;
        }
        int value = EntityPech.valuedItems.containsKey(Item.getId(item.getItem()))
            ? EntityPech.valuedItems.get(Item.getId(item.getItem())) : 0;
        if (value == 0) {
            AspectList al = null /* CraftingManager removed */;
            value = Math.min(32, al.getAmount(Aspect.DESIRE) / 2);
        }
        return value;
    }

    public boolean canPickup(ItemStack entityItem) {
        if (entityItem == null) return false;
        if (!isTamed() && EntityPech.valuedItems.containsKey(Item.getId(entityItem.getItem()))) {
            return true;
        }
        for (int a = 0; a < loot.size(); ++a) {
            if (!loot.get(a).isEmpty() && loot.get(a).getCount() <= 0) {
                loot.set(a, ItemStack.EMPTY);
            }
            if (loot.get(a).isEmpty()) return true;
            if (InventoryUtils.areItemStacksEqualStrict(entityItem, loot.get(a))
                && entityItem.getCount() + loot.get(a).getCount() <= loot.get(a).getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    public ItemStack pickupItem(ItemStack entityItem) {
        if (entityItem == null || entityItem.isEmpty()) return ItemStack.EMPTY;
        if (isTamed() || !isValued(entityItem)) {
            for (int a = 0; a < loot.size(); ++a) {
                if (loot.get(a) != null && loot.get(a).getCount() <= 0) loot.set(a, ItemStack.EMPTY);
                if (!entityItem.isEmpty() && !loot.get(a).isEmpty()
                    && InventoryUtils.areItemStacksEqualStrict(entityItem, loot.get(a))) {
                    if (entityItem.getCount() + loot.get(a).getCount() <= loot.get(a).getMaxStackSize()) {
                        loot.get(a).inflate(entityItem.getCount());
                        return ItemStack.EMPTY;
                    }
                    int sz = Math.min(entityItem.getCount(), loot.get(a).getMaxStackSize() - loot.get(a).getCount());
                    loot.get(a).inflate(sz);
                    entityItem.shrink(sz);
                }
                if (!entityItem.isEmpty() && entityItem.getCount() <= 0) entityItem = ItemStack.EMPTY;
            }
            for (int a = 0; a < loot.size(); ++a) {
                if (!loot.get(a).isEmpty() && loot.get(a).getCount() <= 0) loot.set(a, ItemStack.EMPTY);
                if (entityItem != null && entityItem.getCount() > 0 && loot.get(a).isEmpty()) {
                    loot.set(a, entityItem.copy());
                    return ItemStack.EMPTY;
                }
            }
            if (!entityItem.isEmpty() && entityItem.getCount() <= 0) entityItem = ItemStack.EMPTY;
            return entityItem;
        }
        if (random.nextInt(10) < getValue(entityItem)) {
            setTamed(true);
            setCombatTask();
        }
        entityItem.shrink(1);
        return entityItem.getCount() <= 0 ? ItemStack.EMPTY : entityItem;
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (player.isCrouching()) return InteractionResult.PASS;
        return super.mobInteract(player, hand);
    }
}
