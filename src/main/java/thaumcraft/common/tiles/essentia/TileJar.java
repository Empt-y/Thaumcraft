package thaumcraft.common.tiles.essentia;
import java.util.Random;
// ITickable removed - use BlockEntityTicker<T>
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileJar extends TileThaumcraft
{
    protected static Random rand;

    public TileJar(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), getBlockPos().getX() + 1, getBlockPos().getY() + 1, getBlockPos().getZ() + 1);
    }

    public void update() {
    }

    static {
    }
}
