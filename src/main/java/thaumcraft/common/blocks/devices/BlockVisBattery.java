package thaumcraft.common.blocks.devices;
import net.minecraft.world.level.block.LiquidBlock;
import java.util.Random;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.world.aura.AuraHandler;


public class BlockVisBattery extends BlockTC
{
    public static IntegerProperty CHARGE;

    public BlockVisBattery() {
        super(null, "vis_battery");
        setHardness(0.5f);
        setSoundType(SoundType.STONE);
        setTickRandomly(true);
        registerDefaultState(defaultBlockState().setValue(CHARGE, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHARGE);
    }

    public int damageDropped(BlockState state) {
        return 0;
    }

    public void updateTick(Level worldIn, BlockPos pos, BlockState state, Random rand) {
        if (!worldIn.isClientSide()) {
            int charge = getMetaFromState(state);
            if (worldIn.hasNeighborSignal(pos)) {
                if (charge > 0) {
                    AuraHandler.addVis(worldIn, pos, 1.0f);
                    worldIn.setBlock(pos, state.setValue(CHARGE, charge - 1), 3);
                    worldIn.scheduleTick(pos, state.getBlock(), 5);
                }
            }
            else {
                float aura = AuraHelper.getVis(worldIn, pos);
                int base = AuraHelper.getAuraBase(worldIn, pos);
                if (charge < 10 && aura > base * 0.9 && aura > 1.0f) {
                    AuraHandler.drainVis(worldIn, pos, 1.0f, false);
                    worldIn.setBlock(pos, state.setValue(CHARGE, charge + 1), 3);
                    worldIn.scheduleTick(pos, state.getBlock(), 100 + rand.nextInt(100));
                }
                else if (charge > 0 && aura < base * 0.75) {
                    AuraHandler.addVis(worldIn, pos, 1.0f);
                    worldIn.setBlock(pos, state.setValue(CHARGE, charge - 1), 3);
                    worldIn.scheduleTick(pos, state.getBlock(), 20 + rand.nextInt(20));
                }
            }
        }
    }

    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (worldIn.hasNeighborSignal(pos)) {
            worldIn.scheduleTick(pos, this, 1);
        }
    }

    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    public int getComparatorInputOverride(BlockState state, Level world, BlockPos pos) {
        return getMetaFromState(state);
    }

    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        return state.getValue(CHARGE);
    }

    /* createBlockState() removed */

    public BlockState getStateFromMeta(int meta) {
        return defaultBlockState().setValue(CHARGE, Math.min(meta, 10));
    }

    public int getMetaFromState(BlockState state) {
        return (int)state.getValue(LiquidBlock.LEVEL);
    }

    @OnlyIn(Dist.CLIENT)
    public void getSubBlocks(CreativeModeTab tab, NonNullList<ItemStack> list) {
        list.add(new ItemStack(this));
    }

    static {
        CHARGE = IntegerProperty.create("charge", 0, 10);
    }
}
