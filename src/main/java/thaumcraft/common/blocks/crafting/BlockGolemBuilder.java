package thaumcraft.common.blocks.crafting;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockFacingHorizontal;


import thaumcraft.common.tiles.crafting.TileGolemBuilder;


public class BlockGolemBuilder extends BlockTCDevice implements IBlockFacingHorizontal
{
    public static boolean ignore;

    public BlockGolemBuilder() {
        super(null /*  null   Material removed    */, TileGolemBuilder.class, "golem_builder", true);
        setSoundType(SoundType.STONE);
    }

    @Override
    public boolean rotateBlock(Level world, BlockPos pos, Direction axis) {
        return false;
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        if (worldIn instanceof Level level) {
            destroyGolem(level, pos, state, pos);
        }
        super.destroy(worldIn, pos, state);
    }

    public static void destroyGolem(Level worldIn, BlockPos pos, BlockState state, BlockPos startpos) {
        if (BlockGolemBuilder.ignore || worldIn.isClientSide()) {
            return;
        }
        BlockGolemBuilder.ignore = true;
        for (int a = -1; a <= 1; ++a) {
            for (int b = 0; b <= 1; ++b) {
                for (int c = -1; c <= 1; ++c) {
                    BlockPos target = pos.offset(a, b, c);
                    if (!target.equals(startpos)) {
                        BlockState bs = worldIn.getBlockState(target);
                        if (bs.getBlock() == BlocksTC.placeholderBars) {
                            worldIn.setBlock(target, Blocks.IRON_BARS.defaultBlockState(), 3);
                        }
                        if (bs.getBlock() == BlocksTC.placeholderAnvil) {
                            worldIn.setBlock(target, Blocks.ANVIL.defaultBlockState(), 3);
                        }
                        if (bs.getBlock() == BlocksTC.placeholderCauldron) {
                            worldIn.setBlock(target, Blocks.CAULDRON.defaultBlockState(), 3);
                        }
                        if (bs.getBlock() == BlocksTC.placeholderTable) {
                            worldIn.setBlock(target, BlocksTC.tableStone.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
        if (!pos.equals(startpos)) {
            worldIn.setBlock(pos, Blocks.PISTON.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.UP), 3);
        }
        BlockGolemBuilder.ignore = false;
    }

    @Override
    protected net.minecraft.world.InteractionResult useWithoutItem(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos, net.minecraft.world.entity.player.Player player, net.minecraft.world.phys.BlockHitResult hit) {
        if (!world.isClientSide() && !player.isShiftKeyDown()) {
            net.minecraft.world.level.block.entity.BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof net.minecraft.world.MenuProvider) {
                ((net.minecraft.server.level.ServerPlayer) player).openMenu((net.minecraft.world.MenuProvider) te, buf -> buf.writeBlockPos(pos));
            }
        }
        return net.minecraft.world.InteractionResult.SUCCESS;
    }

    static {
        BlockGolemBuilder.ignore = false;
    }
}
