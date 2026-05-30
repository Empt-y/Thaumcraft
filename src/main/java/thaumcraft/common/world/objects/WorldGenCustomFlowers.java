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
        return false; // TODO: port to modern world gen feature
    }
}
