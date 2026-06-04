package thaumcraft.common.world.objects;
import java.util.Random;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class WorldGenCustomFlowers {
    private Block plantBlock;
    private int plantBlockMeta;

    public WorldGenCustomFlowers(Block bi, int md) {
        plantBlock = bi;
        plantBlockMeta = md;
    }

    public boolean generate(Level world, Random random, BlockPos pos) {
        for (int i = 0; i < 18; i++) {
            int x = pos.getX() + random.nextInt(8) - random.nextInt(8);
            int y = pos.getY() + random.nextInt(4) - random.nextInt(4);
            int z = pos.getZ() + random.nextInt(8) - random.nextInt(8);
            BlockPos bp = new BlockPos(x, y, z);
            if (world.isEmptyBlock(bp) && plantBlock.defaultBlockState().canSurvive(world, bp)) {
                world.setBlock(bp, plantBlock.defaultBlockState(), 3);
            }
        }
        return true;
    }
}
