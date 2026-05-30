package thaumcraft.api.research;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.ThaumcraftApi.EntityTagsNBT;


public class ScanEntity implements IScanThing {
	
	String research;	
	Class entityClass;
	EntityTagsNBT[] NBTData;
	
	/**
	 * false if the specific entity class should be used, or true if anything the inherits from that class is also allowed. 
	 */
	boolean inheritedClasses=false;

	public ScanEntity(String research, Class entityClass, boolean inheritedClasses) {
		this.research = research;
		this.entityClass = entityClass;
		this.inheritedClasses = inheritedClasses;
	}
	
	public ScanEntity(String research, Class entityClass, boolean inheritedClasses, EntityTagsNBT... nbt) {
		this.research = research;
		this.entityClass = entityClass;
		this.inheritedClasses = inheritedClasses;
		NBTData = nbt;
	}

	@Override
	public boolean checkThing(Player player, Object obj) {		
		if (obj!=null && ((!inheritedClasses && entityClass==obj.getClass()) || 
				(inheritedClasses && entityClass.isInstance(obj)))) {			
			// NBT entity scanning not supported in MC 26 (Entity.save uses ValueOutput, not CompoundTag)			
			return true;
		}
		return false;
	}

	@Override
	public String getResearchKey(Player player, Object object) {
		return research;
	}
	
}
