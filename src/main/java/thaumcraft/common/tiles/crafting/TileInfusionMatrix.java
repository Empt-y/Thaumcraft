package thaumcraft.common.tiles.crafting;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
// ITickable removed - use BlockEntityTicker<T>
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
// FML FMLCommonHandler removed
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.casters.IInteractWithCaster;
import thaumcraft.api.crafting.IInfusionStabiliser;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.IGogglesDisplayExtended;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.api.potions.PotionVisExhaust;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.basic.BlockPillar;
import thaumcraft.common.blocks.devices.BlockPedestal;
import thaumcraft.common.container.InventoryFake;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.lib.events.EssentiaHandler;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockArc;
import thaumcraft.common.lib.network.fx.PacketFXInfusionSource;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TileThaumcraft;
import thaumcraft.common.tiles.devices.TileStabilizer;


public class TileInfusionMatrix extends TileThaumcraft implements IInteractWithCaster, IAspectContainer, IGogglesDisplayExtended
{
    private ArrayList<BlockPos> pedestals;
    private int dangerCount;
    public boolean active;
    public boolean crafting;
    public boolean checkSurroundings;
    public float costMult;
    private int cycleTime;
    public int stabilityCap;
    public float stability;
    public float stabilityReplenish;
    private AspectList recipeEssentia;
    private ArrayList<ItemStack> recipeIngredients;
    private Object recipeOutput;
    private String recipePlayer;
    private String recipeOutputLabel;
    private ItemStack recipeInput;
    private int recipeInstability;
    private int recipeXP;
    private int recipeType;
    public HashMap<String, SourceFX> sourceFX;
    public int count;
    public int craftCount;
    public float startUp;
    private int countDelay;
    ArrayList<ItemStack> ingredients;
    int itemCount;
    private ArrayList<BlockPos> problemBlocks;
    HashMap<Block, Integer> tempBlockCount;
    static DecimalFormat myFormatter;
    
    public TileInfusionMatrix(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        pedestals = new ArrayList<BlockPos>();
        dangerCount = 0;
        active = false;
        crafting = false;
        checkSurroundings = true;
        costMult = 0.0f;
        cycleTime = 20;
        stabilityCap = 25;
        stability = 0.0f;
        stabilityReplenish = 0.0f;
        recipeEssentia = new AspectList();
        recipeIngredients = null;
        recipeOutput = null;
        recipePlayer = null;
        recipeOutputLabel = null;
        recipeInput = null;
        recipeInstability = 0;
        recipeXP = 0;
        recipeType = 0;
        sourceFX = new HashMap<String, SourceFX>();
        count = 0;
        craftCount = 0;
        countDelay = cycleTime / 2;
        ingredients = new ArrayList<ItemStack>();
        itemCount = 0;
        problemBlocks = new ArrayList<BlockPos>();
        tempBlockCount = new HashMap<Block, Integer>();
    }
    
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos().getX() - 0.1, getBlockPos().getY() - 0.1, getBlockPos().getZ() - 0.1, getBlockPos().getX() + 1.1, getBlockPos().getY() + 1.1, getBlockPos().getZ() + 1.1);
    }
    
    @Override
    public void readSyncNBT(CompoundTag nbtCompound) {
        active = nbtCompound.getBooleanOr("active", false);
        crafting = nbtCompound.getBooleanOr("crafting", false);
        stability = nbtCompound.getFloatOr("stability", 0.0f);
        recipeInstability = nbtCompound.getIntOr("recipeinst", 0);
        recipeEssentia.loadAdditional(nbtCompound);
    }
    
    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbtCompound) {
        nbtCompound.putBoolean("active", active);
        nbtCompound.putBoolean("crafting", crafting);
        nbtCompound.putFloat("stability", stability);
        nbtCompound.putInt("recipeinst", recipeInstability);
        recipeEssentia.saveAdditional(nbtCompound);
        return nbtCompound;
    }
    
    @Override
    protected void loadAdditional(ValueInput input) {
        recipeIngredients = new ArrayList<ItemStack>();
        input.listOrEmpty("recipein", ItemStack.OPTIONAL_CODEC).forEach(recipeIngredients::add);
        String rot = input.getStringOr("rotype", "");
        if (rot.equals("@")) {
            recipeOutput = input.read("recipeout", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        } else if (!rot.isEmpty()) {
            recipeOutputLabel = rot;
            recipeOutput = null; // complex tag output; simplified in modern port
        }
        recipeInput = input.read("recipeinput", ItemStack.OPTIONAL_CODEC).orElse(ItemStack.EMPTY);
        recipeType = input.getIntOr("recipetype", 0);
        recipeXP = input.getIntOr("recipexp", 0);
        recipePlayer = input.getStringOr("recipeplayer", "");
        if (recipePlayer.isEmpty()) {
            recipePlayer = null;
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        if (recipeIngredients != null && !recipeIngredients.isEmpty()) {
            var list = output.list("recipein", ItemStack.OPTIONAL_CODEC);
            for (ItemStack stack : recipeIngredients) {
                if (!stack.isEmpty()) list.add(stack);
            }
        }
        if (recipeOutput instanceof ItemStack) {
            output.putString("rotype", "@");
            output.store("recipeout", ItemStack.OPTIONAL_CODEC, (ItemStack) recipeOutput);
        }
        if (recipeInput != null && !recipeInput.isEmpty()) {
            output.store("recipeinput", ItemStack.OPTIONAL_CODEC, recipeInput);
        }
        output.putInt("recipetype", recipeType);
        output.putInt("recipexp", recipeXP);
        output.putString("recipeplayer", recipePlayer != null ? recipePlayer : "");
    }
    
    private EnumStability getStability() {
        return (stability > stabilityCap / 2) ? EnumStability.VERY_STABLE : ((stability >= 0.0f) ? EnumStability.STABLE : ((stability > -25.0f) ? EnumStability.UNSTABLE : EnumStability.VERY_UNSTABLE));
    }
    
    private float getModFromCurrentStability() {
        switch (getStability()) {
            case VERY_STABLE: {
                return 5.0f;
            }
            case STABLE: {
                return 6.0f;
            }
            case UNSTABLE: {
                return 7.0f;
            }
            case VERY_UNSTABLE: {
                return 8.0f;
            }
            default: {
                return 1.0f;
            }
        }
    }
    
    public void update() {
        ++count;
        if (checkSurroundings) {
            checkSurroundings = false;
            getSurroundings();
        }
        if (getLevel().isClientSide()) {
            doEffects();
        }
        else {
            if (count % (crafting ? 20 : 100) == 0 && !validLocation()) {
                active = false;
                setChanged();
                syncTile(false);
                return;
            }
            if (active && !crafting && stability < stabilityCap && count % Math.max(5, countDelay) == 0) {
                stability += Math.max(0.1f, stabilityReplenish);
                if (stability > stabilityCap) {
                    stability = (float) stabilityCap;
                }
                setChanged();
                syncTile(false);
            }
            if (active && crafting && count % countDelay == 0) {
                craftCycle();
                setChanged();
            }
        }
    }
    
    public boolean validLocation() {
        return getLevel().getBlockState(getBlockPos().offset(0, -2, 0)).getBlock() instanceof BlockPedestal && getLevel().getBlockState(getBlockPos().offset(1, -2, 1)).getBlock() instanceof BlockPillar && getLevel().getBlockState(getBlockPos().offset(-1, -2, 1)).getBlock() instanceof BlockPillar && getLevel().getBlockState(getBlockPos().offset(1, -2, -1)).getBlock() instanceof BlockPillar && getLevel().getBlockState(getBlockPos().offset(-1, -2, -1)).getBlock() instanceof BlockPillar;
    }
    
    public void craftingStart(Player player) {
        if (!validLocation()) {
            active = false;
            setChanged();
            syncTile(false);
            return;
        }
        getSurroundings();
        BlockEntity te = null;
        recipeInput = ItemStack.EMPTY;
        te = getLevel().getBlockEntity(getBlockPos().below(2));
        if (te != null && te instanceof TilePedestal) {
            TilePedestal ped = (TilePedestal)te;
            if (!ped.getItem(0).isEmpty()) {
                recipeInput = ped.getItem(0).copy();
            }
        }
        if (recipeInput == null || recipeInput.isEmpty()) {
            return;
        }
        ArrayList<ItemStack> components = new ArrayList<ItemStack>();
        for (BlockPos cc : pedestals) {
            te = getLevel().getBlockEntity(cc);
            if (te != null && te instanceof TilePedestal) {
                TilePedestal ped2 = (TilePedestal)te;
                if (ped2.getItem(0).isEmpty()) {
                    continue;
                }
                components.add(ped2.getItem(0).copy());
            }
        }
        if (components.size() == 0) {
            return;
        }
        InfusionRecipe recipe = null /* CraftingManager removed */;
        if (costMult < 0.5) {
            costMult = 0.5f;
        }
        if (recipe != null) {
            recipeType = 0;
            recipeIngredients = components;
            if (recipe.getRecipeOutput(player, recipeInput, components) instanceof Object[]) {
                Object[] obj = (Object[])recipe.getRecipeOutput(player, recipeInput, components);
                recipeOutputLabel = (String)obj[0];
                recipeOutput = obj[1];
            }
            else {
                recipeOutput = recipe.getRecipeOutput(player, recipeInput, components);
            }
            recipeInstability = recipe.getInstability(player, recipeInput, components);
            AspectList al = recipe.getAspects(player, recipeInput, components);
            AspectList al2 = new AspectList();
            for (Aspect as : al.getAspects()) {
                if ((int)(al.getAmount(as) * costMult) > 0) {
                    al2.add(as, (int)(al.getAmount(as) * costMult));
                }
            }
            recipeEssentia = al2;
            recipePlayer = player.getName().getString();
            crafting = true;
            getLevel().playSound(null, getBlockPos(), SoundsTC.craftstart, SoundSource.BLOCKS, 0.5f, 1.0f);
            syncTile(false);
            setChanged();
        }
    }
    
    private float getLossPerCycle() {
        return recipeInstability / getModFromCurrentStability();
    }
    
    public void craftCycle() {
        boolean valid = false;
        float ff = net.minecraft.util.RandomSource.create().nextFloat() * getLossPerCycle();
        stability -= ff;
        stability += stabilityReplenish;
        if (stability < -100.0f) {
            stability = -100.0f;
        }
        if (stability > stabilityCap) {
            stability = (float) stabilityCap;
        }
        BlockEntity te = getLevel().getBlockEntity(getBlockPos().below(2));
        if (te != null && te instanceof TilePedestal) {
            TilePedestal ped = (TilePedestal)te;
            if (!ped.getItem(0).isEmpty()) {
                ItemStack i2 = ped.getItem(0).copy();
                if (recipeInput.getDamageValue() == 32767) {
                    i2.setDamageValue(32767);
                }
                if (ThaumcraftInvHelper.areItemStacksEqualForCrafting(i2, recipeInput)) {
                    valid = true;
                }
            }
        }
        if (!valid || (stability < 0.0f && net.minecraft.util.RandomSource.create().nextInt(1500) <= Math.abs(stability))) {
            switch (net.minecraft.util.RandomSource.create().nextInt(24)) {
                case 0:
                case 1:
                case 2:
                case 3: {
                    inEvEjectItem(0);
                    break;
                }
                case 4:
                case 5:
                case 6: {
                    inEvWarp();
                    break;
                }
                case 7:
                case 8:
                case 9: {
                    inEvZap(false);
                    break;
                }
                case 10:
                case 11: {
                    inEvZap(true);
                    break;
                }
                case 12:
                case 13: {
                    inEvEjectItem(1);
                    break;
                }
                case 14:
                case 15: {
                    inEvEjectItem(2);
                    break;
                }
                case 16: {
                    inEvEjectItem(3);
                    break;
                }
                case 17: {
                    inEvEjectItem(4);
                    break;
                }
                case 18:
                case 19: {
                    inEvHarm(false);
                    break;
                }
                case 20:
                case 21: {
                    inEvEjectItem(5);
                    break;
                }
                case 22: {
                    inEvHarm(true);
                    break;
                }
                case 23: {
                    getLevel().explode(null, getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, 1.5f + net.minecraft.util.RandomSource.create().nextFloat(), Level.ExplosionInteraction.NONE);
                    break;
                }
            }
            stability += 5.0f + net.minecraft.util.RandomSource.create().nextFloat() * 5.0f;
            inResAdd();
            if (valid) {
                return;
            }
        }
        if (!valid) {
            crafting = false;
            recipeEssentia = new AspectList();
            recipeInstability = 0;
            syncTile(false);
            getLevel().playSound(null, getBlockPos(), SoundsTC.craftfail, SoundSource.BLOCKS, 1.0f, 0.6f);
            setChanged();
            return;
        }
        if (recipeType == 1 && recipeXP > 0) {
            List<Player> targets = getLevel().getEntitiesOfClass(Player.class, new AABB(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), getBlockPos().getX() + 1, getBlockPos().getY() + 1, getBlockPos().getZ() + 1).inflate(10.0, 10.0, 10.0));
            if (targets != null && targets.size() > 0) {
                for (Player target : targets) {
                    if (target.getAbilities().instabuild || target.experienceLevel > 0) {
                        if (!target.getAbilities().instabuild) {
                            target.giveExperienceLevels(-1);
                        }
                        --recipeXP;
                        target.hurt(getLevel().damageSources().magic(), (float) net.minecraft.util.RandomSource.create().nextInt(2));
                        if (getLevel() instanceof net.minecraft.server.level.ServerLevel sl) {
                            BlockPos pos = getBlockPos();
                            PacketHandler.sendToAllAround(new PacketFXInfusionSource(pos, pos, target.getId()), sl, pos.getX(), pos.getY(), pos.getZ(), 32.0);
                        }
                        target.playSound(SoundEvents.LAVA_EXTINGUISH, 1.0f, 2.0f + net.minecraft.util.RandomSource.create().nextFloat() * 0.4f);
                        countDelay = cycleTime;
                        return;
                    }
                }
                Aspect[] ingEss = recipeEssentia.getAspects();
                if (ingEss != null && ingEss.length > 0 && net.minecraft.util.RandomSource.create().nextInt(3) == 0) {
                    Aspect as = ingEss[this.level.getRandom().nextInt(ingEss.length)];
                    recipeEssentia.add(as, 1);
                    stability -= 0.25f;
                    syncTile(false);
                    setChanged();
                }
            }
            return;
        }
        if (recipeType == 1 && recipeXP == 0) {
            countDelay = cycleTime / 2;
        }
        if (countDelay < 1) {
            countDelay = 1;
        }
        if (recipeEssentia.visSize() > 0) {
            for (Aspect aspect : recipeEssentia.getAspects()) {
                int na = recipeEssentia.getAmount(aspect);
                if (na > 0) {
                    if (EssentiaHandler.drainEssentia(this, aspect, null, 12, (na > 1) ? countDelay : 0)) {
                        recipeEssentia.reduce(aspect, 1);
                        syncTile(false);
                        setChanged();
                        return;
                    }
                    stability -= 0.25f;
                    syncTile(false);
                    setChanged();
                }
            }
            checkSurroundings = true;
            return;
        }
        if (recipeIngredients.size() > 0) {
            for (int a = 0; a < recipeIngredients.size(); ++a) {
                for (BlockPos cc : pedestals) {
                    te = getLevel().getBlockEntity(cc);
                    if (te != null && te instanceof TilePedestal && ((TilePedestal)te).getItem(0) != null && !((TilePedestal)te).getItem(0).isEmpty() && ThaumcraftInvHelper.areItemStacksEqualForCrafting(((TilePedestal)te).getItem(0), recipeIngredients.get(a))) {
                        if (itemCount == 0) {
                            itemCount = 5;
                            if (getLevel() instanceof net.minecraft.server.level.ServerLevel sl) {
                                BlockPos pos = getBlockPos();
                                PacketHandler.sendToAllAround(new PacketFXInfusionSource(pos, cc, 0), sl, pos.getX(), pos.getY(), pos.getZ(), 32.0);
                            }
                        }
                        else if (itemCount-- <= 1) {
                            net.minecraft.world.item.ItemStackTemplate _rem = ((TilePedestal)te).getItem(0).getCraftingRemainder();
                            ItemStack is = (_rem != null) ? _rem.create() : ItemStack.EMPTY;
                            ((TilePedestal)te).setItem(0, (is == null || is.isEmpty()) ? ItemStack.EMPTY : is.copy());
                            te.setChanged();
                            ((TilePedestal)te).syncTile(false);
                            recipeIngredients.remove(a);
                            setChanged();
                        }
                        return;
                    }
                }
                Aspect[] ingEss = recipeEssentia.getAspects();
                if (ingEss != null && ingEss.length > 0 && this.level.getRandom().nextInt(1 + a) == 0) {
                    Aspect as = ingEss[this.level.getRandom().nextInt(ingEss.length)];
                    recipeEssentia.add(as, 1);
                    stability -= 0.25f;
                    syncTile(false);
                    setChanged();
                }
            }
            return;
        }
        crafting = false;
        craftingFinish(recipeOutput, recipeOutputLabel);
        recipeOutput = null;
        syncTile(false);
        setChanged();
    }
    
    private void inEvZap(boolean all) {
        List<LivingEntity> targets = getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), getBlockPos().getX() + 1, getBlockPos().getY() + 1, getBlockPos().getZ() + 1).inflate(10.0, 10.0, 10.0));
        if (targets != null && targets.size() > 0) {
            for (LivingEntity target : targets) {
                if (getLevel() instanceof net.minecraft.server.level.ServerLevel sl) {
                    BlockPos pos = getBlockPos();
                    PacketHandler.sendToAllAround(new PacketFXBlockArc(pos, target.blockPosition(), 0.3f - getLevel().getRandom().nextFloat() * 0.1f, 0.0f, 0.3f - getLevel().getRandom().nextFloat() * 0.1f), sl, pos.getX(), pos.getY(), pos.getZ(), 32.0);
                }
                target.hurt(getLevel().damageSources().magic(), (float)(4 + net.minecraft.util.RandomSource.create().nextInt(4)));
                if (!all) {
                    break;
                }
            }
        }
    }
    
    private void inEvHarm(boolean all) {
        List<LivingEntity> targets = getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), getBlockPos().getX() + 1, getBlockPos().getY() + 1, getBlockPos().getZ() + 1).inflate(10.0, 10.0, 10.0));
        if (targets != null && targets.size() > 0) {
            for (LivingEntity target : targets) {
                if (this.level.getRandom().nextBoolean()) {
                    target.addEffect(new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionFluxTaint.instance), 120, 0, false, true));
                }
                else {
                    MobEffectInstance pe = new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionVisExhaust.instance), 2400, 0, true, true);
                    // pe.getCurativeItems removed;
                    target.addEffect(pe);
                }
                if (!all) {
                    break;
                }
            }
        }
    }
    
    private void inResAdd() {
        List<Player> targets = getLevel().getEntitiesOfClass(Player.class, new AABB(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), getBlockPos().getX() + 1, getBlockPos().getY() + 1, getBlockPos().getZ() + 1).inflate(10.0));
        if (targets != null && targets.size() > 0) {
            for (Player player : targets) {
                IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
                if (!knowledge.isResearchKnown("!INSTABILITY")) {
                    knowledge.addResearch("!INSTABILITY");
                    knowledge.sync((net.minecraft.server.level.ServerPlayer)player);
                    player.sendOverlayMessage(net.minecraft.network.chat.Component.literal(ChatFormatting.DARK_PURPLE + I18n.get("got.instability")));
                }
            }
        }
    }
    
    private void inEvWarp() {
        List<Player> targets = getLevel().getEntitiesOfClass(Player.class, new AABB(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), getBlockPos().getX() + 1, getBlockPos().getY() + 1, getBlockPos().getZ() + 1).inflate(10.0));
        if (targets != null && targets.size() > 0) {
            Player target = targets.get(this.level.getRandom().nextInt(targets.size()));
            if (net.minecraft.util.RandomSource.create().nextFloat() < 0.25f) {
                ThaumcraftApi.internalMethods.addWarpToPlayer(target, 1, IPlayerWarp.EnumWarpType.NORMAL);
            }
            else {
                ThaumcraftApi.internalMethods.addWarpToPlayer(target, 2 + net.minecraft.util.RandomSource.create().nextInt(4), IPlayerWarp.EnumWarpType.TEMPORARY);
            }
        }
    }
    
    private void inEvEjectItem(int type) {
        for (int retries = 0; retries < 25 && pedestals.size() > 0; ++retries) {
            BlockPos cc = pedestals.get(this.level.getRandom().nextInt(pedestals.size()));
            BlockEntity te = getLevel().getBlockEntity(cc);
            if (te != null && te instanceof TilePedestal && ((TilePedestal)te).getItem(0) != null && !((TilePedestal)te).getItem(0).isEmpty()) {
                BlockPos stabPos = ((TilePedestal)te).findInstabilityMitigator();
                if (stabPos != null) {
                    BlockEntity ste = getLevel().getBlockEntity(stabPos);
                    if (ste != null && ste instanceof TileStabilizer) {
                        TileStabilizer tste = (TileStabilizer)ste;
                        if (tste.mitigate(this.level.getRandom().nextIntBetweenInclusive(5, 10))) {
                            getLevel().blockEvent(cc, getLevel().getBlockState(cc).getBlock(), 5, 0);
                            if (getLevel() instanceof net.minecraft.server.level.ServerLevel sl) {
                                BlockPos pos = getBlockPos();
                                PacketHandler.sendToAllAround(new PacketFXBlockArc(pos, cc.above(), 0.3f - sl.getRandom().nextFloat() * 0.1f, 0.0f, 0.3f - sl.getRandom().nextFloat() * 0.1f), sl, pos.getX(), pos.getY(), pos.getZ(), 32.0);
                                PacketHandler.sendToAllAround(new PacketFXBlockArc(cc.above(), stabPos, 0.3f - sl.getRandom().nextFloat() * 0.1f, 0.0f, 0.3f - sl.getRandom().nextFloat() * 0.1f), sl, stabPos.getX(), stabPos.getY(), stabPos.getZ(), 32.0);
                            }
                            return;
                        }
                    }
                }
                if (type <= 3 || type == 5) {
                    InventoryUtils.dropItems(getLevel(), cc);
                }
                else {
                    ((TilePedestal)te).setItem(0, ItemStack.EMPTY);
                }
                te.setChanged();
                ((TilePedestal)te).syncTile(false);
                if (type == 1 || type == 3) {
                    getLevel().setBlockAndUpdate(cc.above(), BlocksTC.fluxGoo.defaultBlockState());
                    getLevel().playSound(null, cc, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.3f, 1.0f);
                }
                else if (type == 2 || type == 4) {
                    int a = 5 + net.minecraft.util.RandomSource.create().nextInt(5);
                    AuraHelper.polluteAura(getLevel(), cc, (float)a, true);
                }
                else if (type == 5) {
                    getLevel().explode(null, cc.getX() + 0.5f, cc.getY() + 0.5f, cc.getZ() + 0.5f, 1.0f, Level.ExplosionInteraction.NONE);
                }
                getLevel().blockEvent(cc, getLevel().getBlockState(cc).getBlock(), 11, 0);
                if (getLevel() instanceof net.minecraft.server.level.ServerLevel sl) {
                    BlockPos pos = getBlockPos();
                    PacketHandler.sendToAllAround(new PacketFXBlockArc(pos, cc.above(), 0.3f - sl.getRandom().nextFloat() * 0.1f, 0.0f, 0.3f - sl.getRandom().nextFloat() * 0.1f), sl, cc.getX(), cc.getY(), cc.getZ(), 32.0);
                }
                return;
            }
        }
    }
    
    public void craftingFinish(Object out, String label) {
        BlockEntity te = getLevel().getBlockEntity(getBlockPos().below(2));
        if (te != null && te instanceof TilePedestal) {
            float dmg = 1.0f;
            if (out instanceof ItemStack) {
                ItemStack qs = ((ItemStack)out).copy();
                if (((TilePedestal)te).getItem(0).isDamageableItem() && ((TilePedestal)te).getItem(0).getDamageValue() > 0) {
                    dmg = ((TilePedestal)te).getItem(0).getDamageValue() / (float)((TilePedestal)te).getItem(0).getMaxDamage();
                    if (qs.isDamageableItem() && !(qs.getDamageValue() > 0)) {
                        qs.setDamageValue((int)(qs.getMaxDamage() * dmg));
                    }
                }
                ((TilePedestal)te).setInventorySlotContentsFromInfusion(0, qs);
            }
            else if (out instanceof Tag) {
                // Tag-based infusion output (e.g. NBT enchantment) simplified in modern port
                syncTile(false);
                te.setChanged();
            }
            if (recipePlayer != null) {
                // getPlayerEntityByName removed; skip XP grant
            }
            recipeEssentia = new AspectList();
            recipeInstability = 0;
            syncTile(false);
            setChanged();
            getLevel().blockEvent(getBlockPos().below(2), getLevel().getBlockState(getBlockPos().below(2)).getBlock(), 12, 0);
            getLevel().playSound(null, getBlockPos(), SoundsTC.wand, SoundSource.BLOCKS, 0.5f, 1.0f);
        }
    }
    
    private void getSurroundings() {
        Set<Long> stuff = new HashSet<Long>();
        pedestals.clear();
        tempBlockCount.clear();
        problemBlocks.clear();
        cycleTime = 10;
        stabilityReplenish = 0.0f;
        costMult = 1.0f;
        try {
            for (int xx = -8; xx <= 8; ++xx) {
                for (int zz = -8; zz <= 8; ++zz) {
                    boolean skip = false;
                    for (int yy = -3; yy <= 7; ++yy) {
                        if (xx != 0 || zz != 0) {
                            int x = getBlockPos().getX() + xx;
                            int y = getBlockPos().getY() - yy;
                            int z = getBlockPos().getZ() + zz;
                            BlockPos bp = new BlockPos(x, y, z);
                            Block bi = getLevel().getBlockState(bp).getBlock();
                            if (bi instanceof BlockPedestal) {
                                pedestals.add(bp);
                            }
                            try {
                                if (bi == Blocks.SKELETON_SKULL || bi == Blocks.SKELETON_WALL_SKULL || (bi instanceof IInfusionStabiliser && ((IInfusionStabiliser)bi).canStabaliseInfusion(getLevel(), bp))) {
                                    stuff.add(bp.asLong());
                                }
                            }
                            catch (Exception ex) {}
                        }
                    }
                }
            }
            while (!stuff.isEmpty()) {
                Long[] posArray = stuff.toArray(new Long[stuff.size()]);
                if (posArray == null) {
                    break;
                }
                if (posArray[0] == null) {
                    break;
                }
                long lp = posArray[0];
                try {
                    BlockPos c1 = BlockPos.of(lp);
                    int x2 = getBlockPos().getX() - c1.getX();
                    int z2 = getBlockPos().getZ() - c1.getZ();
                    int x3 = getBlockPos().getX() + x2;
                    int z3 = getBlockPos().getZ() + z2;
                    BlockPos c2 = new BlockPos(x3, c1.getY(), z3);
                    Block sb1 = getLevel().getBlockState(c1).getBlock();
                    Block sb2 = getLevel().getBlockState(c2).getBlock();
                    float amt1 = 0.1f;
                    float amt2 = 0.1f;
                    if (sb1 instanceof IInfusionStabiliserExt) {
                        amt1 = ((IInfusionStabiliserExt)sb1).getStabilizationAmount(getLevel(), c1);
                    }
                    if (sb2 instanceof IInfusionStabiliserExt) {
                        amt2 = ((IInfusionStabiliserExt)sb2).getStabilizationAmount(getLevel(), c2);
                    }
                    if (sb1 == sb2 && amt1 == amt2) {
                        if (sb1 instanceof IInfusionStabiliserExt && ((IInfusionStabiliserExt)sb1).hasSymmetryPenalty(getLevel(), c1, c2)) {
                            stabilityReplenish -= ((IInfusionStabiliserExt)sb1).getSymmetryPenalty(getLevel(), c1);
                            problemBlocks.add(c1);
                        }
                        else {
                            stabilityReplenish += calcDeminishingReturns(sb1, amt1);
                        }
                    }
                    else {
                        stabilityReplenish -= Math.max(amt1, amt2);
                        problemBlocks.add(c1);
                    }
                    stuff.remove(c2.asLong());
                }
                catch (Exception ex2) {}
                stuff.remove(lp);
            }
            if (getLevel().getBlockState(getBlockPos().offset(-1, -2, -1)).getBlock() instanceof BlockPillar && getLevel().getBlockState(getBlockPos().offset(1, -2, -1)).getBlock() instanceof BlockPillar && getLevel().getBlockState(getBlockPos().offset(1, -2, 1)).getBlock() instanceof BlockPillar && getLevel().getBlockState(getBlockPos().offset(-1, -2, 1)).getBlock() instanceof BlockPillar) {
                if (getLevel().getBlockState(getBlockPos().offset(-1, -2, -1)).getBlock() == BlocksTC.pillarAncient && getLevel().getBlockState(getBlockPos().offset(1, -2, -1)).getBlock() == BlocksTC.pillarAncient && getLevel().getBlockState(getBlockPos().offset(1, -2, 1)).getBlock() == BlocksTC.pillarAncient && getLevel().getBlockState(getBlockPos().offset(-1, -2, 1)).getBlock() == BlocksTC.pillarAncient) {
                    --cycleTime;
                    costMult -= 0.1f;
                    stabilityReplenish -= 0.1f;
                }
                if (getLevel().getBlockState(getBlockPos().offset(-1, -2, -1)).getBlock() == BlocksTC.pillarEldritch && getLevel().getBlockState(getBlockPos().offset(1, -2, -1)).getBlock() == BlocksTC.pillarEldritch && getLevel().getBlockState(getBlockPos().offset(1, -2, 1)).getBlock() == BlocksTC.pillarEldritch && getLevel().getBlockState(getBlockPos().offset(-1, -2, 1)).getBlock() == BlocksTC.pillarEldritch) {
                    cycleTime -= 3;
                    costMult += 0.05f;
                    stabilityReplenish += 0.2f;
                }
            }
            int[] xm = { -1, 1, 1, -1 };
            int[] zm = { -1, -1, 1, 1 };
            for (int a = 0; a < 4; ++a) {
                Block b = getLevel().getBlockState(getBlockPos().offset(xm[a], -3, zm[a])).getBlock();
                if (b == BlocksTC.matrixSpeed) {
                    --cycleTime;
                    costMult += 0.01f;
                }
                if (b == BlocksTC.matrixCost) {
                    ++cycleTime;
                    costMult -= 0.02f;
                }
            }
            countDelay = cycleTime / 2;
            int apc = 0;
            for (BlockPos cc : pedestals) {
                boolean items = false;
                int x4 = getBlockPos().getX() - cc.getX();
                int z4 = getBlockPos().getZ() - cc.getZ();
                Block bb = getLevel().getBlockState(cc).getBlock();
                if (bb == BlocksTC.pedestalEldritch) {
                    costMult += 0.0025f;
                }
                if (bb == BlocksTC.pedestalAncient) {
                    costMult -= 0.01f;
                }
            }
        }
        catch (Exception ex3) {}
    }
    
    private float calcDeminishingReturns(Block b, float base) {
        float bb = base;
        int c = tempBlockCount.containsKey(b) ? tempBlockCount.get(b) : 0;
        if (c > 0) {
            bb *= (float)Math.pow(0.75, c);
        }
        tempBlockCount.put(b, c + 1);
        return bb;
    }
    
    @Override
    public boolean onCasterRightClick(Level world, ItemStack wandstack, Player player, BlockPos pos, Direction side, InteractionHand hand) {
        if (this.level.isClientSide() && active && !crafting) {
            checkSurroundings = true;
        }
        if (!this.level.isClientSide() && active && !crafting) {
            craftingStart(player);
            return false;
        }
        if (!this.level.isClientSide() && !active && validLocation()) {
            this.level.playSound(null, getBlockPos(), SoundsTC.craftstart, SoundSource.BLOCKS, 0.5f, 1.0f);
            active = true;
            syncTile(false);
            setChanged();
            return false;
        }
        return false;
    }
    
    private void doEffects() {
        if (crafting) {
            if (craftCount == 0) {
                getLevel().playLocalSound(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), SoundsTC.infuserstart, SoundSource.BLOCKS, 0.5f, 1.0f, false);
            }
            else if (craftCount == 0 || craftCount % 65 == 0) {
                getLevel().playLocalSound(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), SoundsTC.infuser, SoundSource.BLOCKS, 0.5f, 1.0f, false);
            }
            ++craftCount;
            FXDispatcher.INSTANCE.blockRunes(getBlockPos().getX(), getBlockPos().getY() - 2, getBlockPos().getZ(), 0.5f + net.minecraft.util.RandomSource.create().nextFloat() * 0.2f, 0.1f, 0.7f + net.minecraft.util.RandomSource.create().nextFloat() * 0.3f, 25, -0.03f);
        }
        else if (craftCount > 0) {
            craftCount -= 2;
            if (craftCount < 0) {
                craftCount = 0;
            }
            if (craftCount > 50) {
                craftCount = 50;
            }
        }
        if (active && startUp != 1.0f) {
            if (startUp < 1.0f) {
                startUp += Math.max(startUp / 10.0f, 0.001f);
            }
            if (startUp > 0.999) {
                startUp = 1.0f;
            }
        }
        if (!active && startUp > 0.0f) {
            if (startUp > 0.0f) {
                startUp -= startUp / 10.0f;
            }
            if (startUp < 0.001) {
                startUp = 0.0f;
            }
        }
        for (String fxk : sourceFX.keySet().toArray(new String[0])) {
            SourceFX fx = sourceFX.get(fxk);
            if (fx.ticks <= 0) {
                sourceFX.remove(fxk);
            }
            else {
                if (fx.loc.equals(getBlockPos())) {
                    Entity player = getLevel().getEntity(fx.color);
                    if (player != null) {
                        for (int a = 0; a < 4; ++a) {
                            FXDispatcher.INSTANCE.drawInfusionParticles4(player.getX() + (net.minecraft.util.RandomSource.create().nextFloat() - net.minecraft.util.RandomSource.create().nextFloat()) * player.getBbWidth(), player.getBoundingBox().minY + net.minecraft.util.RandomSource.create().nextFloat() * player.getBbHeight(), player.getZ() + (net.minecraft.util.RandomSource.create().nextFloat() - net.minecraft.util.RandomSource.create().nextFloat()) * player.getBbWidth(), getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
                        }
                    }
                }
                else {
                    BlockEntity tile = getLevel().getBlockEntity(fx.loc);
                    if (tile instanceof TilePedestal) {
                        ItemStack is = ((TilePedestal)tile).getSyncedStackInSlot(0);
                        if (is != null && !is.isEmpty()) {
                            if (net.minecraft.util.RandomSource.create().nextInt(3) == 0) {
                                FXDispatcher.INSTANCE.drawInfusionParticles3(fx.loc.getX() + net.minecraft.util.RandomSource.create().nextFloat(), fx.loc.getY() + net.minecraft.util.RandomSource.create().nextFloat() + 1.0f, fx.loc.getZ() + net.minecraft.util.RandomSource.create().nextFloat(), getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ());
                            }
                            else {
                                Item bi = is.getItem();
                                if (bi instanceof BlockItem) {
                                    for (int a2 = 0; a2 < 4; ++a2) {
                                        FXDispatcher.INSTANCE.drawInfusionParticles2(fx.loc.getX() + net.minecraft.util.RandomSource.create().nextFloat(), fx.loc.getY() + net.minecraft.util.RandomSource.create().nextFloat() + 1.0f, fx.loc.getZ() + net.minecraft.util.RandomSource.create().nextFloat(), getBlockPos(), Block.byItem(bi).defaultBlockState(), is.getDamageValue());
                                    }
                                }
                                else {
                                    for (int a2 = 0; a2 < 4; ++a2) {
                                        FXDispatcher.INSTANCE.drawInfusionParticles1(fx.loc.getX() + 0.4f + net.minecraft.util.RandomSource.create().nextFloat() * 0.2f, fx.loc.getY() + 1.23f + net.minecraft.util.RandomSource.create().nextFloat() * 0.2f, fx.loc.getZ() + 0.4f + net.minecraft.util.RandomSource.create().nextFloat() * 0.2f, getBlockPos(), is);
                                    }
                                }
                            }
                        }
                    }
                    else {
                        fx.ticks = 0;
                    }
                }
                SourceFX sourceFX = fx;
                --sourceFX.ticks;
                this.sourceFX.put(fxk, fx);
            }
        }
        if (crafting && stability < 0.0f && net.minecraft.util.RandomSource.create().nextInt(250) <= Math.abs(stability)) {
            FXDispatcher.INSTANCE.spark(getBlockPos().getX() + net.minecraft.util.RandomSource.create().nextFloat(), getBlockPos().getY() + net.minecraft.util.RandomSource.create().nextFloat(), getBlockPos().getZ() + net.minecraft.util.RandomSource.create().nextFloat(), 3.0f + net.minecraft.util.RandomSource.create().nextFloat() * 2.0f, 0.7f + net.minecraft.util.RandomSource.create().nextFloat() * 0.1f, 0.1f, 0.65f + net.minecraft.util.RandomSource.create().nextFloat() * 0.1f, 0.8f);
        }
        if (active && !problemBlocks.isEmpty() && net.minecraft.util.RandomSource.create().nextInt(25) == 0) {
            BlockPos p = problemBlocks.get(this.level.getRandom().nextInt(problemBlocks.size()));
            FXDispatcher.INSTANCE.spark(p.getX() + net.minecraft.util.RandomSource.create().nextFloat(), p.getY() + net.minecraft.util.RandomSource.create().nextFloat(), p.getZ() + net.minecraft.util.RandomSource.create().nextFloat(), 2.0f + net.minecraft.util.RandomSource.create().nextFloat(), 0.7f + net.minecraft.util.RandomSource.create().nextFloat() * 0.1f, 0.1f, 0.65f + net.minecraft.util.RandomSource.create().nextFloat() * 0.1f, 0.8f);
        }
    }
    
    @Override
    public AspectList getAspects() {
        return recipeEssentia;
    }
    
    @Override
    public void setAspects(AspectList aspects) {
    }
    
    @Override
    public int addToContainer(Aspect tag, int amount) {
        return 0;
    }
    
    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        return false;
    }
    
    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return false;
    }
    
    @Override
    public boolean doesContainerContain(AspectList ot) {
        return false;
    }
    
    @Override
    public int containerContains(Aspect tag) {
        return 0;
    }
    
    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }
    
    public boolean canRenderBreaking() {
        return true;
    }
    
    public String[] getIGogglesText() {
        float lpc = getLossPerCycle();
        if (lpc != 0.0f) {
            return new String[] { ChatFormatting.BOLD + I18n.get("stability." + getStability().name()), ChatFormatting.GOLD + "" + ChatFormatting.ITALIC + TileInfusionMatrix.myFormatter.format(stabilityReplenish) + " " + I18n.get("stability.gain"), ChatFormatting.RED + "" + I18n.get("stability.range") + ChatFormatting.ITALIC + TileInfusionMatrix.myFormatter.format(lpc) + " " + I18n.get("stability.loss") };
        }
        return new String[] { ChatFormatting.BOLD + I18n.get("stability." + getStability().name()), ChatFormatting.GOLD + "" + ChatFormatting.ITALIC + TileInfusionMatrix.myFormatter.format(stabilityReplenish) + " " + I18n.get("stability.gain") };
    }
    
    static {
        TileInfusionMatrix.myFormatter = new DecimalFormat("#######.##");
    }
    
    public class SourceFX
    {
        public BlockPos loc;
        public int ticks;
        public int color;
        public int entity;
        
        public SourceFX(BlockPos loc, int ticks, int color) {
            this.loc = loc;
            this.ticks = ticks;
            this.color = color;
        }
    }
    
    private enum EnumStability
    {
        VERY_STABLE, 
        STABLE, 
        UNSTABLE, 
        VERY_UNSTABLE;
    }
}
