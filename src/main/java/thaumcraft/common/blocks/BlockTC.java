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
    private String tcRegistryName;

    // Legacy shim - Material was removed in MC 1.20
    public BlockTC(Object material, String name) {
        super(BlockBehaviour.Properties.of());
        this.tcRegistryName = name;
    }
    // Legacy shim with SoundType
    public BlockTC(Object material, String name, SoundType st) {
        super(BlockBehaviour.Properties.of().sound(st));
        this.tcRegistryName = name;
    }

    public String getTCRegistryName() { return tcRegistryName; }

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
