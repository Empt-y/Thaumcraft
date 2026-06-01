package thaumcraft.common.blocks.basic;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import thaumcraft.common.blocks.BlockTC;


public class BlockTranslucent extends BlockTC
{
    public BlockTranslucent(String name) {
        super(BlockBehaviour.Properties.of()
                .sound(SoundType.STONE)
                .noOcclusion()
                .setId(net.minecraft.resources.ResourceKey.create(
                        net.minecraft.core.registries.Registries.BLOCK,
                        net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", name))));
    }
    
    public boolean isBeaconBase(BlockGetter world, BlockPos pos, BlockPos beacon) {
        return true;
    }
    
    public boolean canHarvestBlock(BlockGetter world, BlockPos pos, Player player) {
        return true;
    }
    
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.NORMAL;
    }
    
    public boolean shouldSideBeRendered(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        BlockState iblockstate = blockAccess.getBlockState(pos.relative(side));
        Block block = iblockstate.getBlock();
        return block != this;
    }
    
    public boolean isOpaqueCube(BlockState iblockstate) {
        return false;
    }
}
