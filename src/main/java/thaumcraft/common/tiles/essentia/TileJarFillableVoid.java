package thaumcraft.common.tiles.essentia;
import net.minecraft.core.Direction;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aura.AuraHelper;


public class TileJarFillableVoid extends TileJarFillable
{
    int count;
    
    public TileJarFillableVoid(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        count = 0;
    }
    
    @Override
    public int addToContainer(Aspect tt, int am) {
        boolean up = amount < 250;
        if (am == 0) {
            return am;
        }
        if (tt == aspect || amount == 0) {
            aspect = tt;
            amount += am;
            am = 0;
            if (amount > 250) {
                if (net.minecraft.util.RandomSource.create().nextInt(250) == 0) {
                    AuraHelper.polluteAura(getLevel(), getBlockPos(), 1.0f, true);
                }
                amount = 250;
            }
        }
        if (up) {
            syncTile(false);
            setChanged();
        }
        return am;
    }
    
    @Override
    public int getMinimumSuction() {
        return (aspectFilter != null) ? 48 : 32;
    }
    
    @Override
    public int getSuctionAmount(Direction loc) {
        if (aspectFilter != null && amount < 250) {
            return 48;
        }
        return 32;
    }
    
    @Override
    public void update() {
        if (!getLevel().isClientSide() && ++count % 5 == 0) {
            fillJar();
        }
    }
}
