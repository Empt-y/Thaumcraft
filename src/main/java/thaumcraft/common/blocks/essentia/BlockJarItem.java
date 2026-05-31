package thaumcraft.common.blocks.essentia;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.tiles.essentia.TileAlembic;
import thaumcraft.common.tiles.essentia.TileJarFillable;


public class BlockJarItem extends BlockItem implements IEssentiaContainerItem
{
    public BlockJarItem(Block block, Item.Properties props) {
        super(block, props);
    }
    public BlockJarItem(Block block) { this(block, new Item.Properties()); }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getAspects(stack) != null;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        AspectList al = getAspects(stack);
        if (al == null) return 0;
        return Math.round(al.visSize() / 250.0f * 13);
    }

    
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;
        Block bi = world.getBlockState(pos).getBlock();
        if (bi == BlocksTC.alembic && !world.isClientSide()) {
            TileAlembic tile = (TileAlembic)world.getBlockEntity(pos);
            if (tile != null && tile.amount > 0) {
                if (getFilter(stack) != null && getFilter(stack) != tile.aspect) {
                    return InteractionResult.FAIL;
                }
                if (getAspects(stack) != null && getAspects(stack).getAspects()[0] != tile.aspect) {
                    return InteractionResult.FAIL;
                }
                int amt = tile.amount;
                if (getAspects(stack) != null && getAspects(stack).visSize() + amt > 250) {
                    amt = Math.abs(getAspects(stack).visSize() - 250);
                }
                if (amt <= 0) {
                    return InteractionResult.FAIL;
                }
                Aspect a = tile.aspect;
                if (tile.takeFromContainer(tile.aspect, amt)) {
                    int base = (getAspects(stack) == null) ? 0 : getAspects(stack).visSize();
                    if (stack.getCount() > 1) {
                        ItemStack copy = stack.copy();
                        setAspects(copy, new AspectList().add(a, base + amt));
                        stack.shrink(1);
                        copy.setCount(1);
                        if (!player.getInventory().add(copy)) {
                            world.addFreshEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(), copy));
                        }
                    }
                    else {
                        setAspects(stack, new AspectList().add(a, base + amt));
                    }
                    player.playSound(SoundEvents.BOTTLE_FILL, 0.25f, 1.0f);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult place(BlockPlaceContext context) {
        ItemStack stack = context.getItemInHand();
        InteractionResult result = super.place(context);
        if (result.consumesAction() && !context.getLevel().isClientSide()) {
            BlockPos pos = context.getClickedPos();
            Level world = context.getLevel();
            BlockState newState = world.getBlockState(pos);
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof TileJarFillable jar) {
                jar.setAspects(getAspects(stack));
                CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
                if (customData != null) {
                    CompoundTag tag = customData.copyTag();
                    if (tag.contains("AspectFilter")) {
                        jar.aspectFilter = Aspect.getAspect(tag.getStringOr("AspectFilter", ""));
                    }
                }
                te.setChanged();
                world.sendBlockUpdated(pos, newState, newState, 3);
            }
        }
        return result;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> adder, TooltipFlag flagIn) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("AspectFilter")) {
                String tf = tag.getStringOr("AspectFilter", "");
                Aspect asp = Aspect.getAspect(tf);
                if (asp != null) {
                    adder.accept(Component.literal("§5" + asp.getName()));
                }
            }
        }
        super.appendHoverText(stack, context, tooltipDisplay, adder, flagIn);
    }

    public AspectList getAspects(ItemStack itemstack) {
        CustomData customData = itemstack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            AspectList aspects = new AspectList();
            aspects.loadAdditional(customData.copyTag());
            return (aspects.size() > 0) ? aspects : null;
        }
        return null;
    }

    public Aspect getFilter(ItemStack itemstack) {
        CustomData customData = itemstack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            CompoundTag tag = customData.copyTag();
            if (tag.contains("AspectFilter")) {
                return Aspect.getAspect(tag.getStringOr("AspectFilter", ""));
            }
        }
        return null;
    }

    public void setAspects(ItemStack itemstack, AspectList aspects) {
        CustomData existing = itemstack.get(DataComponents.CUSTOM_DATA);
        CompoundTag tag = (existing != null) ? existing.copyTag() : new CompoundTag();
        aspects.saveAdditional(tag);
        itemstack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public boolean ignoreContainedAspects() {
        return false;
    }
}
