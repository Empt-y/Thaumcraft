package thaumcraft.common.items.tools;
import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import thaumcraft.Thaumcraft;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.devices.TileMirror;
import net.minecraft.world.item.TooltipFlag;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;


public class ItemHandMirror extends ItemTCBase
{
    public ItemHandMirror() {
        super("hand_mirror");
    }
    
    public boolean getShareTag() {
        return true;
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.UNCOMMON;
    }
    
    public boolean hasEffect(ItemStack stack1) {
        return !stack1.isEmpty();
    }
    
    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, net.minecraft.world.item.context.UseOnContext context) {
        Player player = context.getPlayer();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        InteractionHand hand = context.getHand();
        if (player == null) return InteractionResult.PASS;
        Block bi = world.getBlockState(pos).getBlock();
        if (bi != BlocksTC.mirror) {
            return InteractionResult.PASS;
        }
        if (world.isClientSide()) {
            player.swing(hand);
            return InteractionResult.SUCCESS;
        }
        BlockEntity tm = world.getBlockEntity(pos);
        if (tm instanceof TileMirror) {
            int dimHash = (world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0);
            CustomData.update(DataComponents.CUSTOM_DATA, player.getItemInHand(hand), nbt -> {
                nbt.putInt("linkX", pos.getX());
                nbt.putInt("linkY", pos.getY());
                nbt.putInt("linkZ", pos.getZ());
                nbt.putInt("linkDim", dimHash);
            });
            world.playLocalSound(pos, SoundsTC.jar, SoundSource.BLOCKS, 1.0f, 2.0f, false);
            player.sendSystemMessage(Component.translatable("tc.handmirrorlinked"));
            player.containerMenu.broadcastChanges();
        }
        return InteractionResult.SUCCESS;
    }
    
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (!world.isClientSide()) {
            ItemStack heldStack = player.getItemInHand(hand);
            CompoundTag nbt = heldStack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            if (nbt.contains("linkX")) {
                int lx = nbt.getIntOr("linkX", 0);
                int ly = nbt.getIntOr("linkY", 0);
                int lz = nbt.getIntOr("linkZ", 0);
                // DimensionManager removed - only works in current dimension
                BlockEntity te = world.getBlockEntity(new BlockPos(lx, ly, lz));
                if (te == null || !(te instanceof TileMirror)) {
                    CustomData.update(DataComponents.CUSTOM_DATA, heldStack, tag -> {
                        tag.remove("linkX"); tag.remove("linkY"); tag.remove("linkZ"); tag.remove("linkDim");
                    });
                    player.playSound(SoundsTC.zap, 1.0f, 0.8f);
                    player.sendSystemMessage(Component.translatable("tc.handmirrorerror"));
                    return InteractionResult.FAIL;
                }
                // GUI open removed - DimensionManager and openGui not available in 1.21.5
            }
        }
        return super.use(world, player, hand);
    }
    
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        if (!stack.isEmpty()) {
            CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            if (nbt.contains("linkX")) {
                int lx = nbt.getIntOr("linkX", 0);
                int ly = nbt.getIntOr("linkY", 0);
                int lz = nbt.getIntOr("linkZ", 0);
                int ldim = nbt.getIntOr("linkDim", 0);
                tooltip.accept(Component.literal(I18n.get("tc.handmirrorlinkedto") + " " + lx + "," + ly + "," + lz + " in " + ldim));
            }
        }
    }
    
    public static boolean transport(ItemStack mirror, ItemStack items, Player player, Level worldObj) {
        if (mirror.isEmpty()) {
            return false;
        }
        CompoundTag nbt = mirror.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (!nbt.contains("linkX")) {
            return false;
        }
        int lx = nbt.getIntOr("linkX", 0);
        int ly = nbt.getIntOr("linkY", 0);
        int lz = nbt.getIntOr("linkZ", 0);
        // DimensionManager removed - only works in current dimension
        BlockEntity te = worldObj.getBlockEntity(new BlockPos(lx, ly, lz));
        if (te == null || !(te instanceof TileMirror)) {
            CustomData.update(DataComponents.CUSTOM_DATA, mirror, tag -> {
                tag.remove("linkX"); tag.remove("linkY"); tag.remove("linkZ"); tag.remove("linkDim");
            });
            player.playSound(SoundsTC.zap, 1.0f, 0.8f);
            player.sendSystemMessage(Component.translatable("tc.handmirrorerror"));
            return false;
        }
        TileMirror tm = (TileMirror)te;
        if (tm.transportDirect(items)) {
            items = ItemStack.EMPTY;
            player.playSound(SoundEvents.ENDERMAN_TELEPORT, 0.1f, 1.0f);
        }
        return true;
    }
}
