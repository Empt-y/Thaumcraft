package thaumcraft.common.blocks.misc;
import java.util.Random;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.blocks.BlockTC;


public class BlockHole extends BlockTC
{
    public BlockHole() {
        super(null, "hole");
        setBlockUnbreakable();
        setResistance(6000000.0f);
        setSoundType(SoundType.WOOL);
        setLightLevel(0.7f);
        setTickRandomly(true);
    }

    public ItemStack getPickBlock(BlockState state, HitResult target, Level world, BlockPos pos, Player player) {
        return ItemStack.EMPTY;
    }

    public Object /* BlockFaceShape removed */ getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public void getSubBlocks(CreativeModeTab par2CreativeTabs, NonNullList<ItemStack> par3List) {
    }

    public boolean isSideSolid(BlockState state, BlockGetter world, BlockPos pos, Direction o) {
        return true;
    }

    public AABB getCollisionBoundingBox(BlockState state, BlockGetter worldIn, BlockPos pos) {
        return null;
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return FULL_BLOCK_AABB.move(pos);
    }

    public AABB getSelectedBoundingBox(BlockState blockState, Level worldIn, BlockPos pos) {
        return new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    public boolean isFullCube(BlockState blockState) {
        return false;
    }

    public boolean isOpaqueCube(BlockState blockState) {
        return false;
    }

    public Item getItemDropped(BlockState state, Random rand, int fortune) {
        return Items.AIR;
    }
}
