package thaumcraft.common.tiles.essentia;
import net.minecraft.nbt.CompoundTag;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;


public class TileTubeFilter extends TileTube implements IAspectContainer
{
    public Aspect aspectFilter;
    
    public TileTubeFilter(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        aspectFilter = null;
    }
    
    @Override
    public void readSyncNBT(CompoundTag nbttagcompound) {
        super.readSyncNBT(nbttagcompound);
        aspectFilter = Aspect.getAspect(nbttagcompound.getStringOr("AspectFilter", ""));
    }
    
    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbttagcompound) {
        nbttagcompound = super.writeSyncNBT(nbttagcompound);
        if (aspectFilter != null) {
            nbttagcompound.putString("AspectFilter", aspectFilter.get());
        }
        return nbttagcompound;
    }
    
    @Override
    void calculateSuction(Aspect filter, boolean restrict, boolean dir) {
        super.calculateSuction(aspectFilter, restrict, dir);
    }
    
    @Override
    public AspectList getAspects() {
        if (aspectFilter != null) {
            return new AspectList().add(aspectFilter, -1);
        }
        return null;
    }
    
    @Override
    public void setAspects(AspectList aspects) {
    }
    
    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return false;
    }
    
    @Override
    public int addToContainer(Aspect tag, int amount) {
        return 0;
    }
    
    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        return false;
    }
    
    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return false;
    }
    
    @Override
    public boolean doesContainerContain(AspectList ot) {
        return false;
    }
    
    @Override
    public int containerContains(Aspect tag) {
        return 0;
    }
}
