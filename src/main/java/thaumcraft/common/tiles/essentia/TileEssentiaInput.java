package thaumcraft.common.tiles.essentia;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
// ITickable removed - use BlockEntityTicker<T>
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.lib.events.EssentiaHandler;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileEssentiaInput extends TileThaumcraft implements IEssentiaTransport
{
    int count;
    
    public TileEssentiaInput(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        count = 0;
    }
    
    @Override
    public boolean isConnectable(Direction face) {
        return face == getFacing().getOpposite();
    }
    
    @Override
    public boolean canInputFrom(Direction face) {
        return face == getFacing().getOpposite();
    }
    
    @Override
    public boolean canOutputTo(Direction face) {
        return false;
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
        return 128;
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
            fillJar();
        }
    }
    
    void fillJar() {
        BlockEntity te = ThaumcraftApiHelper.getConnectableTile(world, getBlockPos(), getFacing().getOpposite());
        if (te != null) {
            IEssentiaTransport ic = (IEssentiaTransport)te;
            if (!ic.canOutputTo(getFacing())) {
                return;
            }
            if (ic.getEssentiaAmount(getFacing()) > 0 && ic.getSuctionAmount(getFacing()) < getSuctionAmount(getFacing().getOpposite()) && getSuctionAmount(getFacing().getOpposite()) >= ic.getMinimumSuction()) {
                Aspect ta = ic.getEssentiaType(getFacing());
                if (EssentiaHandler.addEssentia(this, ta, getFacing(), 16, false, 5)) {
                    ic.takeEssentia(ta, 1, getFacing());
                }
            }
        }
    }
}
