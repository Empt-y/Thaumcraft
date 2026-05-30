package thaumcraft.common.blocks.basic;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.blocks.BlockTC;


public class BlockTranslucent extends BlockTC
{
    public BlockTranslucent(String name) {
        super(null /*  null   Material removed    */, name);
        setHardness(0.5f);
        setSoundType(SoundType.STONE);
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
    
    @OnlyIn(Dist.CLIENT)
    public boolean shouldSideBeRendered(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        BlockState iblockstate = blockAccess.getBlockState(pos.relative(side));
        Block block = iblockstate.getBlock();
        return block != this;
    }
    
    @OnlyIn(Dist.CLIENT)
    public Object /* BlockRenderLayer removed */ getBlockLayer_removed() {
        return null; // Object /* BlockRenderLayer removed */ removed
    }
    
    public boolean isOpaqueCube(BlockState iblockstate) {
        return false;
    }
}
