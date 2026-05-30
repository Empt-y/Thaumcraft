package thaumcraft.common.blocks;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.core.Direction;

public interface IBlockFacing
{
    EnumProperty<Direction> FACING = EnumProperty.create("facing", Direction.class);
}
