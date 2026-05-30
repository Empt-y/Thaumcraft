package thaumcraft.common.blocks.devices;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.devices.TileMirror;
import thaumcraft.common.tiles.devices.TileMirrorEssentia;


public class BlockMirrorItem extends BlockItem
{
    public BlockMirrorItem(Block par1) {
        super(par1, new Item.Properties());
    }

    
    public InteractionResult useOn(UseOnContext ctx) {
        ItemStack stack = ctx.getItemInHand();
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        Player player = ctx.getPlayer();
        InteractionHand hand = ctx.getHand();
        if (player == null) return InteractionResult.PASS;
        if (!(world.getBlockState(pos).getBlock() instanceof BlockMirror)) {
            return InteractionResult.PASS;
        }
        if (world.isClientSide()) {
            player.swing(hand);
            return InteractionResult.SUCCESS;
        }
        if (getBlock() == BlocksTC.mirror) {
            BlockEntity tm = world.getBlockEntity(pos);
            if (tm instanceof TileMirror && !((TileMirror)tm).isLinkValid()) {
                ItemStack st = stack.copy();
                st.setCount(1);
                st.setDamageValue(1);
                CompoundTag nbt = new CompoundTag();
                nbt.putInt("linkX", tm.getBlockPos().getX());
                nbt.putInt("linkY", tm.getBlockPos().getY());
                nbt.putInt("linkZ", tm.getBlockPos().getZ());
                nbt.putInt("linkDim", world instanceof net.minecraft.server.level.ServerLevel
                    ? world.dimension().identifier().hashCode() : 0);
                st.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
                world.playSound(null, pos, SoundsTC.jar, SoundSource.BLOCKS, 1.0f, 2.0f);
                if (!player.getInventory().add(st)) {
                    world.addFreshEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(), st));
                }
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            } else if (tm instanceof TileMirror) {
                player.sendSystemMessage(Component.translatable("§5§oThat mirror is already linked to a valid destination."));
            }
        } else {
            BlockEntity tm = world.getBlockEntity(pos);
            if (tm instanceof TileMirrorEssentia && !((TileMirrorEssentia)tm).isLinkValid()) {
                ItemStack st = stack.copy();
                st.setCount(1);
                st.setDamageValue(1);
                CompoundTag nbt = new CompoundTag();
                nbt.putInt("linkX", tm.getBlockPos().getX());
                nbt.putInt("linkY", tm.getBlockPos().getY());
                nbt.putInt("linkZ", tm.getBlockPos().getZ());
                nbt.putInt("linkDim", world instanceof net.minecraft.server.level.ServerLevel
                    ? world.dimension().identifier().hashCode() : 0);
                st.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
                world.playSound(null, pos, SoundsTC.jar, SoundSource.BLOCKS, 1.0f, 2.0f);
                if (!player.getInventory().add(st)) {
                    world.addFreshEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(), st));
                }
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
            } else if (tm instanceof TileMirrorEssentia) {
                player.sendSystemMessage(Component.translatable("§5§oThat mirror is already linked to a valid destination."));
            }
        }
        return InteractionResult.SUCCESS;
    }

    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.UNCOMMON;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack item, Item.TooltipContext ctx, TooltipDisplay tooltipDisplay, Consumer<Component> adder, TooltipFlag flagIn) {
        CustomData data = item.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            CompoundTag nbt = data.copyTag();
            int lx = nbt.getIntOr("linkX", 0);
            int ly = nbt.getIntOr("linkY", 0);
            int lz = nbt.getIntOr("linkZ", 0);
            adder.accept(Component.literal("Linked to " + lx + "," + ly + "," + lz));
        }
    }
}
