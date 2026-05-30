package thaumcraft.common.blocks.basic;
import java.util.Random;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.DyeColor;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.common.blocks.BlockTC;


public class BlockCandle extends BlockTC implements IInfusionStabiliserExt
{
    public DyeColor dye;

    public BlockCandle(String name, DyeColor dye) {
        super(null /*  null   Material removed    */, name);
        setHardness(0.1f);
        setSoundType(SoundType.WOOL);
        setLightLevel(0.9375f);
        this.dye = dye;
    }

    public MapColor getMapColor(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return dyeToMapColor(dye);
    }

    public static MapColor dyeToMapColor(DyeColor dye) {
        if (dye == null) return MapColor.WOOL;
        switch (dye) {
            case WHITE: return MapColor.WOOL;
            case ORANGE: return MapColor.COLOR_ORANGE;
            case MAGENTA: return MapColor.COLOR_MAGENTA;
            case LIGHT_BLUE: return MapColor.COLOR_LIGHT_BLUE;
            case YELLOW: return MapColor.COLOR_YELLOW;
            case LIME: return MapColor.COLOR_LIGHT_GREEN;
            case PINK: return MapColor.COLOR_PINK;
            case GRAY: return MapColor.COLOR_GRAY;
            case LIGHT_GRAY: return MapColor.COLOR_LIGHT_GRAY;
            case CYAN: return MapColor.COLOR_CYAN;
            case PURPLE: return MapColor.COLOR_PURPLE;
            case BLUE: return MapColor.COLOR_BLUE;
            case BROWN: return MapColor.COLOR_BROWN;
            case GREEN: return MapColor.COLOR_GREEN;
            case RED: return MapColor.COLOR_RED;
            case BLACK: return MapColor.COLOR_BLACK;
            default: return MapColor.WOOL;
        }
    }

    public Object getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return null;
    }

    public boolean canPlaceBlockAt(Level par1World, BlockPos pos) {
        return par1World.getBlockState(pos.below()).isFaceSturdy(par1World, pos.below(), Direction.UP);
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos pos2, boolean isMoving) {
        if (!canPlaceBlockAt(worldIn, pos)) {
            Block.popResource(worldIn, pos, new net.minecraft.world.item.ItemStack(this));
            worldIn.removeBlock(pos, false);
        }
    }

    public boolean canPlaceBlockOnSide(Level par1World, BlockPos pos, Direction par5) {
        return canPlaceBlockAt(par1World, pos);
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return new AABB(0.375, 0.0, 0.375, 0.625, 0.5, 0.625);
    }

    public boolean isSideSolid(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return false;
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

    public void randomDisplayTick(BlockState state, Level par1World, BlockPos pos, Random par5Random) {
        // Particle spawning removed — par1World.spawnParticle removed in MC 26
    }

    public boolean canStabaliseInfusion(Level world, BlockPos pos) {
        return true;
    }

    @Override
    public float getStabilizationAmount(Level world, BlockPos pos) {
        return 0.1f;
    }

    @Override
    public boolean hasSymmetryPenalty(Level world, BlockPos pos1, BlockPos pos2) {
        return false;
    }

    @Override
    public float getSymmetryPenalty(Level world, BlockPos pos) {
        return 0.0f;
    }
}
