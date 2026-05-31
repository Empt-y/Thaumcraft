package thaumcraft.common.blocks.crafting;
import java.util.Random;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.tiles.crafting.TileResearchTable;


public class BlockResearchTable extends BlockTCDevice implements IBlockFacingHorizontal
{
    public BlockResearchTable() {
        super(null /*  null   Material removed    */, TileResearchTable.class, "research_table");
        setSoundType(SoundType.WOOD);
    }

    public int damageDropped(BlockState state) {
        return 0;
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public boolean isSideSolid(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return false;
    }

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (world.isClientSide()) {
            return true;
        }
        // TODO: open GUI id=10
        return true;
    }

    @Override
    public BlockState getStateForPlacement(Level worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        BlockState bs = defaultBlockState();
        bs = bs.setValue((Property)IBlockFacingHorizontal.FACING, placer.getDirection());
        return bs;
    }

    public void randomDisplayTick(BlockState state, Level world, BlockPos pos, Random rand) {
        BlockEntity te = world.getBlockEntity(pos);
        if (rand.nextInt(5) == 0 && te != null && ((TileResearchTable)te).data != null) {
            double xx = rand.nextGaussian() / 2.0;
            double zz = rand.nextGaussian() / 2.0;
            double yy = 1.5 + rand.nextFloat();
            int a = 40 + rand.nextInt(20);
            FXGeneric fb = new FXGeneric(world, pos.getX() + 0.5 + xx, pos.getY() + yy, pos.getZ() + 0.5 + zz, -xx / a, -(yy - 0.85) / a, -zz / a);
            fb.setMaxAge(a);
            fb.setRBGColorF(0.5f + rand.nextFloat() * 0.5f, 0.5f + rand.nextFloat() * 0.5f, 0.5f + rand.nextFloat() * 0.5f);
            fb.setAlphaF(0.0f, 0.25f, 0.5f, 0.75f, 0.0f);
            fb.setParticles(384 + rand.nextInt(16), 1, 1);
            fb.setScale(0.8f + rand.nextFloat() * 0.3f, 0.3f);
            fb.setLayer(0);
            ParticleEngine.addEffect(world, fb);
        }
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
}
