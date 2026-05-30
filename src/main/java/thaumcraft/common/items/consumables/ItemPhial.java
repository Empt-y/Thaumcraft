package thaumcraft.common.items.consumables;
import java.util.Iterator;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemTCEssentiaContainer;
import thaumcraft.common.tiles.essentia.TileAlembic;
import thaumcraft.common.tiles.essentia.TileJarFillable;


public class ItemPhial extends ItemTCEssentiaContainer
{
    public ItemPhial() {
        super("phial", 10, "empty", "filled");
    }
    
    public static ItemStack makePhial(Aspect aspect, int amt) {
        ItemStack i = new ItemStack(ItemsTC.phial, 1);
        ((IEssentiaContainerItem)i.getItem()).setAspects(i, new AspectList().add(aspect, amt));
        return i;
    }
    
    public static ItemStack makeFilledPhial(Aspect aspect) {
        return makePhial(aspect, 10);
    }
    
    
    public void getSubItems(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == null /* SEARCH tab removed */) {
            items.add(new ItemStack(this, 1));
            for (Aspect tag : Aspect.aspects.values()) {
                ItemStack i = new ItemStack(this, 1);
                setAspects(i, new AspectList().add(tag, base));
                items.add(i);
            }
        }
    }
    
    @Override
    public net.minecraft.network.chat.Component getName(ItemStack stack) {
        if (getAspects(stack) != null && !getAspects(stack).aspects.isEmpty()) {
            return super.getName(stack).copy().append(" (" + getAspects(stack).getAspects()[0].getName() + ")");
        }
        return super.getName(stack);
    }
    
    @Override
    public void inventoryTick(ItemStack stack, net.minecraft.server.level.ServerLevel world, Entity entity, @javax.annotation.Nullable net.minecraft.world.entity.EquipmentSlot par4) {
        if (!stack.isEmpty() && stack.getDamageValue() == 1) {
            stack.setDamageValue(0);
        }
    }
    
    public boolean doesSneakBypassUse(ItemStack stack, BlockGetter world, BlockPos pos, Player player) {
        return true;
    }
    
    public InteractionResult onItemUseFirst(Player player, Level world, BlockPos pos, Direction side, float f1, float f2, float f3, InteractionHand hand) {
        BlockState bi = world.getBlockState(pos);
        if (player.getItemInHand(hand).getDamageValue() == 0 && bi.getBlock() == BlocksTC.alembic) {
            TileAlembic tile = (TileAlembic)world.getBlockEntity(pos);
            if (tile.amount >= base) {
                if (world.isClientSide()) {
                    player.swing(hand);
                    return InteractionResult.PASS;
                }
                ItemStack phial = new ItemStack(this, 1);
                setAspects(phial, new AspectList().add(tile.aspect, base));
                if (tile.takeFromContainer(tile.aspect, base)) {
                    player.getItemInHand(hand).shrink(1);
                    if (!player.getInventory().add(phial)) {
                        world.addFreshEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(), phial));
                    }
                    player.playSound(SoundEvents.BOTTLE_FILL, 0.25f, 1.0f);
                    player.containerMenu.broadcastChanges();
                    return InteractionResult.SUCCESS;
                }
            }
        }
        if (player.getItemInHand(hand).getDamageValue() == 0 && (bi.getBlock() == BlocksTC.jarNormal || bi.getBlock() == BlocksTC.jarVoid)) {
            TileJarFillable tile2 = (TileJarFillable)world.getBlockEntity(pos);
            if (tile2.amount >= base) {
                if (world.isClientSide()) {
                    player.swing(hand);
                    return InteractionResult.PASS;
                }
                Aspect asp = Aspect.getAspect(tile2.aspect.getTag());
                if (tile2.takeFromContainer(asp, base)) {
                    player.getItemInHand(hand).shrink(1);
                    ItemStack phial2 = new ItemStack(this, 1);
                    setAspects(phial2, new AspectList().add(asp, base));
                    if (!player.getInventory().add(phial2)) {
                        world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, phial2));
                    }
                    player.playSound(SoundEvents.BOTTLE_FILL, 0.25f, 1.0f);
                    player.containerMenu.broadcastChanges();
                    return InteractionResult.SUCCESS;
                }
            }
        }
        AspectList al = getAspects(player.getItemInHand(hand));
        if (al != null && al.size() == 1) {
            Aspect aspect = al.getAspects()[0];
            if (player.getItemInHand(hand).getDamageValue() != 0 && (bi.getBlock() == BlocksTC.jarNormal || bi.getBlock() == BlocksTC.jarVoid)) {
                TileJarFillable tile3 = (TileJarFillable)world.getBlockEntity(pos);
                if (tile3.amount <= 250 - base && tile3.doesContainerAccept(aspect)) {
                    if (world.isClientSide()) {
                        player.swing(hand);
                        return InteractionResult.PASS;
                    }
                    if (tile3.addToContainer(aspect, base) == 0) {
                        world.sendBlockUpdated(pos, bi, bi, 3);
                        tile3.setChanged();
                        player.getItemInHand(hand).shrink(1);
                        if (!player.getInventory().add(new ItemStack(this, 1))) {
                            world.addFreshEntity(new ItemEntity(world, pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f, new ItemStack(this, 1)));
                        }
                        player.playSound(SoundEvents.BOTTLE_FILL, 0.25f, 1.0f);
                        player.containerMenu.broadcastChanges();
                        return InteractionResult.SUCCESS;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }
}
