package thaumcraft.common.blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import thaumcraft.Thaumcraft;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.lib.utils.InventoryUtils;


public class BlockTCTile extends BlockTC implements EntityBlock
{
    protected Class<? extends BlockEntity> tileClass;
    protected static boolean keepInventory;
    protected static boolean spillEssentia;

    public BlockTCTile(Object mat, Class<? extends BlockEntity> tc, String name) {
        super(mat, name);
        setHardness(2.0f);
        setResistance(20.0f);
        tileClass = tc;
    }

    public boolean canHarvestBlock(BlockGetter world, BlockPos pos, Player player) {
        return true;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (tileClass == null) return null;
        try {
            return tileClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            Thaumcraft.log.catching(e);
        }
        return null;
    }

    public boolean hasBlockEntity(BlockState state) {
        return true;
    }

    @Override
    public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        if (worldIn instanceof Level level) {
            InventoryUtils.dropItems(level, pos);
            BlockEntity tileentity = level.getBlockEntity(pos);
            if (tileentity instanceof IEssentiaTransport && BlockTCTile.spillEssentia && !level.isClientSide()) {
                int ess = ((IEssentiaTransport)tileentity).getEssentiaAmount(Direction.UP);
                if (ess > 0) {
                    AuraHelper.polluteAura(level, pos, (float)ess, true);
                }
            }
        }
        super.destroy(worldIn, pos, state);
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int id, int param) {
        super.triggerEvent(state, worldIn, pos, id, param);
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        return tileentity != null && tileentity.triggerEvent(id, param);
    }

    static {
        BlockTCTile.keepInventory = false;
        BlockTCTile.spillEssentia = true;
    }
}
