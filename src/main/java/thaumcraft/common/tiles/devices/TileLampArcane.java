package thaumcraft.common.tiles.devices;
import net.minecraft.world.level.LightLayer;
// ITickable removed - use BlockEntityTicker<T>
import net.minecraft.core.BlockPos;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileLampArcane extends TileThaumcraft 
{
    public int rad;
    public int rad1;
    
    public TileLampArcane(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        rad1 = 0;
    }
    
    public void update() {
        if (!getLevel().isClientSide() && getLevel().getGameTime() % 5L == 0L && !gettingPower()) {
            int x = net.minecraft.util.RandomSource.create().nextInt(16) - net.minecraft.util.RandomSource.create().nextInt(16);
            int y = net.minecraft.util.RandomSource.create().nextInt(16) - net.minecraft.util.RandomSource.create().nextInt(16);
            int z = net.minecraft.util.RandomSource.create().nextInt(16) - net.minecraft.util.RandomSource.create().nextInt(16);
            BlockPos bp = getBlockPos().offset(x, y, z);
            if (bp.getY() > getLevel().getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, bp).getY() + 4) {
                bp = getLevel().getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, bp).above(4);
            }
            if (bp.getY() < 5) {
                bp = new BlockPos(bp.getX(), 5, bp.getZ());
            }
            if (getLevel().isEmptyBlock(bp) && getLevel().getBlockState(bp) != BlocksTC.effectGlimmer.defaultBlockState() && getLevel().getBrightness(LightLayer.BLOCK, bp) < 11 && BlockUtils.hasLOS(getLevel(), getBlockPos(), bp)) {
                getLevel().setBlock(bp, BlocksTC.effectGlimmer.defaultBlockState(), 3);
            }
        }
    }
    
    public void removeLights() {
        for (int x = -15; x <= 15; ++x) {
            for (int y = -15; y <= 15; ++y) {
                for (int z = -15; z <= 15; ++z) {
                    BlockPos bp = getBlockPos().offset(x, y, z);
                    if (getLevel().getBlockState(bp) == BlocksTC.effectGlimmer.defaultBlockState()) {
                        getLevel().removeBlock(bp, false);
                    }
                }
            }
        }
    }
}
