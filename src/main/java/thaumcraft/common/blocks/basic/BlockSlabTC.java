package thaumcraft.common.blocks.basic;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


public class BlockSlabTC extends SlabBlock
{
    public static EnumProperty<Variant> VARIANT;
    boolean wood;
    Block drop;

    // Stub stubs for legacy chained method calls in ConfigBlocks
    public BlockSlabTC setHardness(float h) { return this; }
    public BlockSlabTC setResistance(float r) { return this; }

    protected BlockSlabTC(String name, Block b, boolean wood) {
        super(BlockBehaviour.Properties.of().sound(wood ? SoundType.WOOD : SoundType.STONE));
        this.wood = wood;
        this.drop = b;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(VARIANT);
    }

    @OnlyIn(Dist.CLIENT)
    public ItemStack getItem(net.minecraft.world.level.Level worldIn, BlockPos pos, BlockState state) {
        return new ItemStack(state.getBlock());
    }

    public BlockState getStateFromMeta(int meta) {
        BlockState iblockstate = defaultBlockState().setValue(VARIANT, Variant.DEFAULT);
        if (!isDouble()) {
            iblockstate = iblockstate.setValue(SlabBlock.TYPE, ((meta & 0x8) == 0x0) ? SlabType.BOTTOM : SlabType.TOP);
        }
        return iblockstate;
    }

    public int getMetaFromState(BlockState state) {
        int i = 0;
        if (!isDouble() && state.getValue(SlabBlock.TYPE) == SlabType.TOP) {
            i |= 0x8;
        }
        return i;
    }

    public int damageDropped(BlockState state) {
        return 0;
    }

    public boolean isDouble() {
        return false;
    }

    public String getUnlocalizedName(int meta) {
        return "";
    }

    public Property<?> getVariantProperty() {
        return BlockSlabTC.VARIANT;
    }

    public Comparable<?> getTypeForItem(ItemStack stack) {
        return Variant.DEFAULT;
    }

    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return wood ? 20 : super.getFlammability(state, world, pos, face);
    }

    public int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return wood ? 5 : super.getFireSpreadSpeed(state, world, pos, face);
    }

    static {
        VARIANT = EnumProperty.create("variant", Variant.class);
    }

    public static class Double extends BlockSlabTC
    {
        public Double(String name, Block b, boolean wood) {
            super(name, b, wood);
        }

        @Override
        public boolean isDouble() {
            return true;
        }
    }

    public static class Half extends BlockSlabTC
    {
        public Half(String name, Block b, boolean wood) {
            super(name, b, wood);
        }

        @Override
        public boolean isDouble() {
            return false;
        }
    }

    public enum Variant implements StringRepresentable
    {
        DEFAULT;

        @Override
        public String getSerializedName() {
            return "default";
        }
    }
}
