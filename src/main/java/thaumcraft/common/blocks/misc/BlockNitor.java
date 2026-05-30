package thaumcraft.common.blocks.misc;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.tiles.misc.TileNitor;
import javax.annotation.Nonnull;


public class BlockNitor extends BlockTC implements EntityBlock
{
    public DyeColor dye;
    
    public BlockNitor(String name, DyeColor dye) {
        super(null /*  null   Material removed    */, name);
        setHardness(0.1f);
        setSoundType(SoundType.WOOL);
        setLightLevel(1.0f);
        this.dye = dye;
    }

    @Override
    public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
        return new TileNitor(null, pos, state);
    }
    
    public boolean hasBlockEntity(BlockState state) {
        return true;
    }
    
    public MapColor getMapColor(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return MapColor.NONE;
    }
    
    public Object /* BlockFaceShape removed */ getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return null; // Object /* BlockFaceShape removed */ removed
    }
    
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }
    
    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return new AABB(0.33000001311302185, 0.33000001311302185, 0.33000001311302185, 0.6600000262260437, 0.6600000262260437, 0.6600000262260437);
    }
    
    public AABB getCollisionBoundingBox(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return null;
    }
    
    public boolean isFullCube(BlockState state) {
        return false;
    }
    
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }
}
