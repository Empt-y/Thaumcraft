package thaumcraft.common.blocks.devices;
import java.util.Random;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.tiles.devices.TileWaterJug;


public class BlockWaterJug extends BlockTCDevice
{
    public BlockWaterJug() {
        super(null /*  null   Material removed    */, TileWaterJug.class, "everfull_urn");
        setSoundType(SoundType.STONE);
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return new AABB(0.1875, 0.0, 0.1875, 0.8125, 1.0, 0.8125);
    }

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (!world.isClientSide()) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te != null && te instanceof TileWaterJug) {
                TileWaterJug tile = (TileWaterJug)te;
                if (FluidUtil.interactWithFluidHandler(player, hand, tile.tank)) {
                    te.setChanged();
                    tile.syncTile(false);
                    world.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.33f, 1.0f + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.3f);
                }
                else if (player.getItemInHand(hand).getItem() == Items.GLASS_BOTTLE && tile.tank.getFluidAmount() >= 333) {
                    ItemStack itemstack = player.getItemInHand(hand);
                    ItemStack itemstack2 = PotionContents.createItemStack(Items.POTION, Potions.WATER);
                    if (!player.getAbilities().instabuild) {
                        itemstack.shrink(1);
                    }
                    if (itemstack.isEmpty()) {
                        player.setItemInHand(hand, itemstack2);
                    }
                    else if (!player.getInventory().add(itemstack2)) {
                        player.drop(itemstack2, false);
                    }
                    tile.drain(new FluidStack(Fluids.WATER, 333), IFluidHandler.FluidAction.EXECUTE);
                    world.playSound(null, pos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 0.33f, 1.0f + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.3f);
                }
            }
        }
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public void randomDisplayTick(BlockState state, Level world, BlockPos pos, Random rand) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof TileWaterJug) {
            TileWaterJug tile = (TileWaterJug)te;
            if (tile.tank.getFluidAmount() >= tile.tank.getCapacity()) {
                FXDispatcher.INSTANCE.jarSplashFx(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            }
        }
    }
}
