package thaumcraft.common.world.objects;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class WorldGenMound {
    public boolean generate(Level world, Random random, BlockPos pos) {
        return false; // world gen not yet ported to BiomeModifier
    }
}
