package thaumcraft.common.blocks;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.core.Direction;

public interface IBlockFacingHorizontal
{
    EnumProperty<Direction> FACING = EnumProperty.create("facing", Direction.class,
        Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
}
