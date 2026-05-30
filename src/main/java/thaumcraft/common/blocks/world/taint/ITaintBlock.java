package thaumcraft.common.blocks.world.taint;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;


public interface ITaintBlock
{
    void die(Level p0, BlockPos p1, BlockState p2);
}
