package thaumcraft.common.blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.NonNullList;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public class BlockTC extends Block
{
    protected static final AABB FULL_BLOCK_AABB = new AABB(0, 0, 0, 1, 1, 1);

    // Modern constructor
    public BlockTC(BlockBehaviour.Properties props) {
        super(props);
    }
    protected String tcRegistryName;

    /** Set before constructing a block in registerBlock() so subclasses that call
     *  BlockBehaviour.Properties.of() directly can pick up the correct registry ID. */
    public static final ThreadLocal<String> PENDING_NAME = ThreadLocal.withInitial(() -> null);

    protected static BlockBehaviour.Properties propsWithId(String name) {
        return BlockBehaviour.Properties.of()
            .sound(SoundType.STONE)
            .setId(net.minecraft.resources.ResourceKey.create(
                net.minecraft.core.registries.Registries.BLOCK,
                net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", name)));
    }

    /** Call from subclass constructors that build their own Properties instead of using the
     *  legacy (material, name) shim.  Falls back to PENDING_NAME if name is not known locally. */
    public static BlockBehaviour.Properties autoProps(BlockBehaviour.Properties base) {
        String name = PENDING_NAME.get();
        if (name != null) {
            return base.setId(net.minecraft.resources.ResourceKey.create(
                net.minecraft.core.registries.Registries.BLOCK,
                net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", name)));
        }
        return base;
    }

    // Legacy shim - Material was removed in MC 1.20
    public BlockTC(Object material, String name) {
        super(propsWithId(name));
        this.tcRegistryName = name;
    }
    // Legacy shim with SoundType
    public BlockTC(Object material, String name, SoundType st) {
        super(propsWithId(name).sound(st));
        this.tcRegistryName = name;
    }

    public String getTCRegistryName() { return tcRegistryName; }
    public void setTCRegistryName(String name) { this.tcRegistryName = name; }

    /** Auto-registers IBlockFacing*/
    @Override
    protected void createBlockStateDefinition(net.minecraft.world.level.block.state.StateDefinition.Builder<Block, net.minecraft.world.level.block.state.BlockState> builder) {
        if (this instanceof IBlockFacingHorizontal) builder.add(IBlockFacingHorizontal.FACING);
        else if (this instanceof IBlockFacing)      builder.add(IBlockFacing.FACING);
        if (this instanceof IBlockEnabled)          builder.add(IBlockEnabled.ENABLED);
    }

    // Old API stubs - these were removed in MC 1.14+
    public BlockTC setUnlocalizedName(String name) { return this; }
    public BlockTC setRegistryName(String name) { this.tcRegistryName = name; return this; }
    public BlockTC setRegistryName(String mod, String name) { this.tcRegistryName = name; return this; }
    public BlockTC setCreativeTab(Object tab) { return this; }
    public BlockTC setResistance(float r) { return this; }
    public BlockTC setHardness(float h) { return this; }
    public BlockTC setHarvestLevel(String tool, int level) { return this; }
    public BlockTC setSoundType(SoundType s) { return this; }
    public BlockTC setLightLevel(Object l) { return this; }
    public BlockTC setTickRandomly(boolean b) { return this; }
    public BlockTC setLightOpacity(int i) { return this; }
    public BlockTC setBlockUnbreakable() { return this; }
    protected boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) { return false; }
    protected void breakBlock(Level worldIn, BlockPos pos, BlockState state) {}

    @OnlyIn(Dist.CLIENT)
    public void getSubBlocks(CreativeModeTab tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this, 1));
    }
}
