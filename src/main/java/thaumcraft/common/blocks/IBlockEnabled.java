package thaumcraft.common.blocks;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public interface IBlockEnabled
{
    BooleanProperty ENABLED = BooleanProperty.create("enabled");
}
