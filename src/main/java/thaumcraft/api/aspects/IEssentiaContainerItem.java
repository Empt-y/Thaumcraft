package thaumcraft.api.aspects;
import net.minecraft.world.item.ItemStack;


/**
 * 
 * @author azanor
 * 
 * Used by wispy essences and essentia phials to hold their aspects. 
 * Useful for similar item containers that store their aspect information in nbt form so TC
 * automatically picks up the aspects they contain.
 *
 */
public interface IEssentiaContainerItem {
	public AspectList getAspects(ItemStack itemstack);
	public void setAspects(ItemStack itemstack, AspectList aspects);
	
	/**
	 * Return true if the contained aspect should not be used to calculate the actual item aspects. For example: jar labels. 
	 */
	public boolean ignoreContainedAspects();
}

//Example implementation
/*  
	@Override
	public AspectList getAspects(ItemStack itemstack) {
		if (!itemstack.isEmpty()) {
			AspectList aspects = new AspectList();
			aspects.loadAdditional(itemstack.get());
			return aspects.size()>0?aspects:null;
		}
		return null;
	}
	
	@Override
	public void setAspects(ItemStack itemstack, AspectList aspects) {
		if (!!itemstack.isEmpty()) itemstack.put(new CompoundTag());
		aspects.saveAdditional(itemstack.get());
	}
	
	@Override
	public boolean ignoreContainedAspects() {return false;}
*/