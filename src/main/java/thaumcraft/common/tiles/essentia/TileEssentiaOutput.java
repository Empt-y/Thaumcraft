package thaumcraft.common.tiles.essentia;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
// ITickable removed - use BlockEntityTicker<T>
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.lib.events.EssentiaHandler;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileEssentiaOutput extends TileThaumcraft implements IEssentiaTransport
{
    int count;
    
    public TileEssentiaOutput(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        count = 0;
    }
    
    @Override
    public boolean isConnectable(Direction face) {
        return face == getFacing().getOpposite();
    }
    
    @Override
    public boolean canInputFrom(Direction face) {
        return false;
    }
    
    @Override
    public boolean canOutputTo(Direction face) {
        return face == getFacing().getOpposite();
    }
    
    @Override
    public void setSuction(Aspect aspect, int amount) {
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    @Override
    public Aspect getSuctionType(Direction loc) {
        return null;
    }
    
    @Override
    public int getSuctionAmount(Direction loc) {
        return 0;
    }
    
    @Override
    public Aspect getEssentiaType(Direction loc) {
        return null;
    }
    
    @Override
    public int getEssentiaAmount(Direction loc) {
        return 0;
    }
    
    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction face) {
        return 0;
    }
    
    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        return amount;
    }
    
    public void update() {
        if (!getLevel().isClientSide() && ++count % 5 == 0) {
            fillBuffer();
        }
    }
    
    void fillBuffer() {
        BlockEntity te = ThaumcraftApiHelper.getConnectableTile(level, getBlockPos(), getFacing().getOpposite());
        if (te != null) {
            IEssentiaTransport ic = (IEssentiaTransport)te;
            if (!ic.canInputFrom(getFacing())) {
                return;
            }
            if (ic.getSuctionAmount(getFacing()) > 0 && ic.getSuctionType(getFacing()) != null) {
                Aspect ta = ic.getSuctionType(getFacing());
                if (EssentiaHandler.drainEssentiaWithConfirmation(this, ta, getFacing(), 16, false, 5) && ic.addEssentia(ta, 1, getFacing()) > 0) {
                    EssentiaHandler.confirmDrain();
                }
            }
        }
    }
}
