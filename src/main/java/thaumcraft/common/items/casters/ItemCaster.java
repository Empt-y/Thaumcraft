package thaumcraft.common.items.casters;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.casters.CasterTriggerRegistry;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.ICaster;
import thaumcraft.api.casters.IFocusBlockPicker;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.casters.IInteractWithCaster;
import thaumcraft.api.items.IArchitect;
import net.minecraft.util.Mth;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketAuraToClient;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.util.Mth;


public class ItemCaster extends ItemTCBase implements IArchitect, ICaster
{
    int area;
    DecimalFormat myFormatter;
    ArrayList<BlockPos> checked;
    
    public ItemCaster(String name, int area) {
        super(name);
        // ItemTCBase constructor
        this.area = 0;
        myFormatter = new DecimalFormat("#######.#");
        checked = new ArrayList<BlockPos>();
        this.area = area;
        // maxStackSize removed - set in Item.Properties
        /* setMaxDamage removed - use Item.Properties */;
        /* addPropertyOverride removed */

    }
    
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (!oldStack.isEmpty() && oldStack.getItem() == this && !newStack.isEmpty() && newStack.getItem() == this) {
            ItemFocus oldf = ((ItemCaster)oldStack.getItem()).getFocus(oldStack);
            ItemFocus newf = ((ItemCaster)newStack.getItem()).getFocus(newStack);
            int s1 = 0;
            int s2 = 0;
            if (oldf != null && oldf.getSortingHelper(((ItemCaster)oldStack.getItem()).getFocusStack(oldStack)) != null) {
                s1 = oldf.getSortingHelper(((ItemCaster)oldStack.getItem()).getFocusStack(oldStack)).hashCode();
            }
            if (newf != null && newf.getSortingHelper(((ItemCaster)newStack.getItem()).getFocusStack(newStack)) != null) {
                s2 = newf.getSortingHelper(((ItemCaster)newStack.getItem()).getFocusStack(newStack)).hashCode();
            }
            return s1 != s2;
        }
        return newStack.getItem() != oldStack.getItem();
    }
    
    public boolean isDamageable() {
        return false;
    }
    
    @OnlyIn(Dist.CLIENT)
    public boolean isFull3D() {
        return true;
    }
    
    private float getAuraPool(Player player) {
        float tot = 0.0f;
        switch (area) {
            default: {
                tot = AuraHandler.getVis(player.level(), player.blockPosition());
                break;
            }
            case 1: {
                tot = AuraHandler.getVis(player.level(), player.blockPosition());
                for (Direction face : Direction.Plane.HORIZONTAL) {
                    tot += AuraHandler.getVis(player.level(), player.blockPosition().relative(face, 16));
                }
                break;
            }
            case 2: {
                tot = 0.0f;
                for (int xx = -1; xx <= 1; ++xx) {
                    for (int zz = -1; zz <= 1; ++zz) {
                        tot += AuraHandler.getVis(player.level(), new net.minecraft.core.BlockPos(player.blockPosition().getX() + xx * 16, player.blockPosition().getY() + 0, player.blockPosition().getZ() + zz * 16));
                    }
                }
                break;
            }
        }
        return tot;
    }
    
    @Override
    public boolean consumeVis(ItemStack is, Player player, float amount, boolean crafting, boolean sim) {
        amount *= getConsumptionModifier(is, player, crafting);
        float tot = getAuraPool(player);
        if (tot < amount) {
            return false;
        }
        if (sim) {
            return true;
        }
        Label_0309: {
            switch (area) {
                default: {
                    amount -= AuraHandler.drainVis(player.level(), player.blockPosition(), amount, sim);
                    break;
                }
                case 1: {
                    float i = amount / 5.0f;
                    while (amount > 0.0f) {
                        if (i > amount) {
                            i = amount;
                        }
                        amount -= AuraHandler.drainVis(player.level(), player.blockPosition(), i, sim);
                        if (amount <= 0.0f) {
                            break;
                        }
                        if (i > amount) {
                            i = amount;
                        }
                        for (Direction face : Direction.Plane.HORIZONTAL) {
                            amount -= AuraHandler.drainVis(player.level(), player.blockPosition().relative(face, 16), i, sim);
                            if (amount <= 0.0f) {
                                break Label_0309;
                            }
                        }
                    }
                    break;
                }
                case 2: {
                    float i = amount / 9.0f;
                    while (amount > 0.0f) {
                        if (i > amount) {
                            i = amount;
                        }
                        for (int xx = -1; xx <= 1; ++xx) {
                            for (int zz = -1; zz <= 1; ++zz) {
                                amount -= AuraHandler.drainVis(player.level(), new net.minecraft.core.BlockPos(player.blockPosition().getX() + xx * 16, player.blockPosition().getY() + 0, player.blockPosition().getZ() + zz * 16), i, sim);
                                if (amount <= 0.0f) {
                                    break Label_0309;
                                }
                            }
                        }
                    }
                    break;
                }
            }
        }
        return amount <= 0.0f;
    }
    
    @Override
    public float getConsumptionModifier(ItemStack is, Player player, boolean crafting) {
        float consumptionModifier = 1.0f;
        if (player != null) {
            consumptionModifier -= CasterManager.getTotalVisDiscount(player);
        }
        return Math.max(consumptionModifier, 0.1f);
    }
    
    @Override
    public ItemFocus getFocus(ItemStack stack) {
        if (!stack.isEmpty() && stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("focus")) {
            CompoundTag nbt = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getCompoundOrEmpty("focus");
            ItemStack fs = ItemStack.OPTIONAL_CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, nbt).result().orElse(ItemStack.EMPTY);
            if (fs != null && !fs.isEmpty()) {
                return (ItemFocus)fs.getItem();
            }
        }
        return null;
    }
    
    @Override
    public ItemStack getFocusStack(ItemStack stack) {
        if (!stack.isEmpty() && stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("focus")) {
            CompoundTag nbt = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getCompoundOrEmpty("focus");
            return ItemStack.OPTIONAL_CODEC.parse(net.minecraft.nbt.NbtOps.INSTANCE, nbt).result().orElse(ItemStack.EMPTY);
        }
        return null;
    }
    
    @Override
    public void setFocus(ItemStack stack, ItemStack focus) {
        if (focus == null || focus.isEmpty()) {
            { net.minecraft.nbt.CompoundTag _t = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag(); _t.remove("focus"); if (!_t.isEmpty()) stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(_t)); else stack.remove(net.minecraft.core.component.DataComponents.CUSTOM_DATA); }
        }
        else {
            net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, stack, t -> t.put("focus", focus.saveAdditional(new CompoundTag())));
        }
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.UNCOMMON;
    }
    
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        if (!stack.isEmpty()) {
            String text = "";
            ItemStack focus = getFocusStack(stack);
            if (focus != null && !focus.isEmpty()) {
                float amt = ((ItemFocus)focus.getItem()).getVisCost(focus);
                if (amt > 0.0f) {
                    text = "§r" + myFormatter.format(amt) + " " + I18n.get("item.Focus.cost1");
                }
            }
            tooltip.accept(net.minecraft.network.chat.Component.literal("" + ChatFormatting.ITALIC + "" + ChatFormatting.AQUA + I18n.get("tc.vis.cost") + " " + text));
        }
        if (getFocus(stack) != null) {
            tooltip.accept(net.minecraft.network.chat.Component.literal("" + ChatFormatting.BOLD + "" + ChatFormatting.ITALIC + "" + ChatFormatting.GREEN + getFocus(stack).getDescriptionId(getFocusStack(stack))));
            getFocus(stack).addFocusInformation(getFocusStack(stack), null, tooltip, flagIn);
        }
    }
    
    public void onArmorTick(Level world, Player player, ItemStack itemStack) {
        super.onArmorTick(world, player, itemStack);
    }
    
    public void onUpdate(ItemStack is, Level w, Entity e, int slot, boolean currentItem) {
        if (!w.isClientSide() && e.tickCount % 10 == 0 && e instanceof net.minecraft.server.level.ServerPlayer) {
            for (ItemStack h : e.getHeldEquipment()) {
                if (h != null && !h.isEmpty() && h.getItem() instanceof ICaster) {
                    updateAura(is, w, (net.minecraft.server.level.ServerPlayer)e);
                    break;
                }
            }
        }
    }
    
    private void updateAura(ItemStack stack, Level world, net.minecraft.server.level.ServerPlayer player) {
        float cv = 0.0f;
        float cf = 0.0f;
        short bv = 0;
        switch (area) {
            default: {
                AuraChunk ac = AuraHandler.getAuraChunk((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), (int)player.getX() >> 4, (int)player.getZ() >> 4);
                if (ac == null) {
                    break;
                }
                cv = ac.getVis();
                cf = ac.getFlux();
                bv = ac.getBase();
                break;
            }
            case 1: {
                AuraChunk ac = AuraHandler.getAuraChunk((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), (int)player.getX() >> 4, (int)player.getZ() >> 4);
                if (ac == null) {
                    break;
                }
                cv = ac.getVis();
                cf = ac.getFlux();
                bv = ac.getBase();
                for (Direction face : Direction.Plane.HORIZONTAL) {
                    ac = AuraHandler.getAuraChunk((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), ((int)player.getX() >> 4) + face.getStepX(), ((int)player.getZ() >> 4) + face.getStepZ());
                    if (ac != null) {
                        cv += ac.getVis();
                        cf += ac.getFlux();
                        bv += ac.getBase();
                    }
                }
                break;
            }
            case 2: {
                for (int xx = -1; xx <= 1; ++xx) {
                    for (int zz = -1; zz <= 1; ++zz) {
                        AuraChunk ac = AuraHandler.getAuraChunk((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), ((int)player.getX() >> 4) + xx, ((int)player.getZ() >> 4) + zz);
                        if (ac != null) {
                            cv += ac.getVis();
                            cf += ac.getFlux();
                            bv += ac.getBase();
                        }
                    }
                }
                break;
            }
        }
        PacketHandler.sendToPlayer(new PacketAuraToClient(new AuraChunk(null, bv, cv, cf)), player);
    }
    
    public InteractionResult onItemUseFirst(Player player, Level world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, InteractionHand hand) {
        BlockState bs = world.getBlockState(pos);
        if (bs.getBlock() instanceof IInteractWithCaster && ((IInteractWithCaster)bs.getBlock()).onCasterRightClick(world, player.getItemInHand(hand), player, pos, side, hand)) {
            return InteractionResult.PASS;
        }
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile != null && tile instanceof IInteractWithCaster && ((IInteractWithCaster)tile).onCasterRightClick(world, player.getItemInHand(hand), player, pos, side, hand)) {
            return InteractionResult.PASS;
        }
        if (CasterTriggerRegistry.hasTrigger(bs)) {
            return CasterTriggerRegistry.performTrigger(world, player.getItemInHand(hand), player, pos, side, bs) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }
        ItemStack fb = getFocusStack(player.getItemInHand(hand));
        if (fb != null && !fb.isEmpty()) {
            FocusPackage core = ItemFocus.getPackage(fb);
            for (IFocusElement fe : core.nodes) {
                if (fe instanceof IFocusBlockPicker && player.isCrouching() && world.getBlockEntity(pos) == null) {
                    if (!world.isClientSide()) {
                        ItemStack isout = new ItemStack(bs.getBlock());
                        try {
                            if (bs.getBlock() != Blocks.AIR) {
                                ItemStack is = BlockUtils.getSilkTouchDrop(bs);
                                if (is != null && !is.isEmpty()) {
                                    isout = is.copy();
                                }
                            }
                        }
                        catch (Exception ex) {}
                        storePickedBlock(player.getItemInHand(hand), isout);
                        return InteractionResult.SUCCESS;
                    }
                    player.swing(hand);
                    return InteractionResult.PASS;
                }
            }
        }
        return InteractionResult.PASS;
    }
    
    private HitResult generateSourceVector(Entity e) {
        Vec3 v = e.position();
        boolean mainhand = true;
        if (e instanceof Player) {
            if (((Player)e).getMainHandItem() != null && ((Player)e).getMainHandItem().getItem() instanceof ICaster) {
                mainhand = true;
            }
            else if (((Player)e).getOffhandItem() != null && ((Player)e).getOffhandItem().getItem() instanceof ICaster) {
                mainhand = false;
            }
        }
        double x = -Mth.cos((e.getYRot() - 0.5f) / 180.0f * 3.141593f) * 0.20000000298023224 * (mainhand ? 1 : -1);
        double z = -Mth.sin((e.getYRot() - 0.5f) / 180.0f * 3.141593f) * 0.30000001192092896 * (mainhand ? 1 : -1);
        Vec3 vl = e.getLookAngle();
        v = v.add(x, e.getEyeHeight() - 0.4000000014901161, z);
        v = v.add(vl);
        return null /* new HitResult removed */;
    }
    
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        ItemStack focusStack = getFocusStack(player.getItemInHand(hand));
        ItemFocus focus = getFocus(player.getItemInHand(hand));
        if (focus == null || CasterManager.isOnCooldown(player)) {
            return super.use(world, player, hand);
        }
        CasterManager.setCooldown(player, focus.getActivationTime(focusStack));
        FocusPackage core = ItemFocus.getPackage(focusStack);
        if (player.isCrouching()) {
            for (IFocusElement fe : core.nodes) {
                if (fe instanceof IFocusBlockPicker && player.isCrouching()) {
                    return InteractionResult.PASS;
                }
            }
        }
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (consumeVis(player.getItemInHand(hand), player, focus.getVisCost(focusStack), false, false)) {
            FocusEngine.castFocusPackage(player, core);
            player.swing(hand);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.SUCCESS;
    }
    
    public int getMaxItemUseDuration(ItemStack itemstack) {
        return 72000;
    }
    
    public ItemUseAnimation getItemUseAction(ItemStack stack1) {
        return ItemUseAnimation.BOW;
    }
    
    @Override
    public ArrayList<BlockPos> getArchitectBlocks(ItemStack stack, Level world, BlockPos pos, Direction side, Player player) {
        ItemFocus focus = getFocus(stack);
        if (focus != null) {
            FocusPackage fp = ItemFocus.getPackage(getFocusStack(stack));
            if (fp != null) {
                for (IFocusElement fe : fp.nodes) {
                    if (fe instanceof IArchitect) {
                        return ((IArchitect)fe).getArchitectBlocks(stack, world, pos, side, player);
                    }
                }
            }
        }
        return null;
    }
    
    @Override
    public boolean showAxis(ItemStack stack, Level world, Player player, Direction side, EnumAxis axis) {
        ItemFocus focus = getFocus(stack);
        if (focus != null) {
            FocusPackage fp = ItemFocus.getPackage(getFocusStack(stack));
            if (fp != null) {
                for (IFocusElement fe : fp.nodes) {
                    if (fe instanceof IArchitect) {
                        return ((IArchitect)fe).showAxis(stack, world, player, side, axis);
                    }
                }
            }
        }
        return false;
    }
    
    @Override
    public HitResult getArchitectMOP(ItemStack stack, Level world, LivingEntity player) {
        ItemFocus focus = getFocus(stack);
        if (focus != null) {
            FocusPackage fp = ItemFocus.getPackage(getFocusStack(stack));
            if (fp != null && FocusEngine.doesPackageContainElement(fp, "thaumcraft.PLAN")) {
                return ((IArchitect)FocusEngine.getElement("thaumcraft.PLAN")).getArchitectMOP(getFocusStack(stack), world, player);
            }
        }
        return null;
    }
    
    @Override
    public boolean useBlockHighlight(ItemStack stack) {
        return false;
    }
    
    public void storePickedBlock(ItemStack stack, ItemStack stackout) {
        CompoundTag item = new CompoundTag();
        net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, stack, t -> t.put("picked", stackout.saveAdditional(item)));
    }
    
    @Override
    public ItemStack getPickedBlock(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack out = null;
        ItemFocus focus = getFocus(stack);
        if (focus != null && !stack.isEmpty() && stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("picked")) {
            FocusPackage fp = ItemFocus.getPackage(getFocusStack(stack));
            if (fp != null) {
                for (IFocusElement fe : fp.nodes) {
                    if (fe instanceof IFocusBlockPicker) {
                        out = new ItemStack(Blocks.AIR);
                        try {
                            out = new ItemStack(stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getCompoundOrEmpty("picked"));
                        }
                        catch (Exception ex) {}
                        break;
                    }
                }
            }
        }
        return out;
    }
}
