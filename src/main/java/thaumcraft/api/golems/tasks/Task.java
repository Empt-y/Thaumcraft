package thaumcraft.api.golems.tasks;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.ProvisionRequest;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;



public class Task {

	private UUID golemUUID;
	private int id;	
	private byte type;
	private SealPos sealPos;	
	private BlockPos pos;	
	private Entity entity; 
	private boolean reserved;
	private boolean suspended;
	private boolean completed;
	private int data;
	private ProvisionRequest linkedProvision;
	/**
	 * Lifespan in seconds. Default 300 seconds
	 */
	private short lifespan;
	private byte priority=0;
	
	private Task() {}

	public Task(SealPos sealPos, BlockPos pos) {
		this.sealPos = sealPos;
		this.pos = pos;
		if (sealPos==null) {
			id = (System.currentTimeMillis()+"/BNPOS/"+pos.toString()).hashCode();
		} else
			id = (System.currentTimeMillis()+"/B/"+sealPos.face.toString()+"/"+sealPos.pos.toString()+"/"+pos.toString()).hashCode();
		type = 0;
		lifespan = 300;
	}
	
	public Task(SealPos sealPos, Entity entity) {
		this.sealPos = sealPos;
		this.entity = entity;
		if (sealPos==null) {
			id = (System.currentTimeMillis()+"/ENPOS/"+entity.getId()).hashCode();
		} else
			id = (System.currentTimeMillis()+"/E/"+sealPos.face.toString()+"/"+sealPos.pos.toString()+"/"+entity.getId()).hashCode();
		type = 1;
		lifespan = 300;
	}	

	public byte getPriority() {
		return priority;
	}

	public void setPriority(byte priority) {
		this.priority = priority;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompletion(boolean fulfilled) {
		completed = fulfilled;
		lifespan += 1;
	}

	public UUID getGolemUUID() {
		return golemUUID;
	}

	public void setGolemUUID(UUID golemUUID) {
		this.golemUUID = golemUUID;
	}

	public BlockPos getPos() {
		return type==1?entity.blockPosition():pos;
	}	
	
	public byte getType() {
		return type;
	}

	public Entity getEntity() {
		return entity;
	}

	public int getId() {
		return id;
	}
	
	public boolean isReserved() {
		return reserved;
	}

	public void setReserved(boolean res) {
		reserved = res;
		lifespan += 120;
	}

	public boolean isSuspended() {
		return suspended;
	}

	public void setSuspended(boolean suspended) {
		setLinkedProvision(null);
		this.suspended = suspended;
	}

	public SealPos getSealPos() {
		return sealPos;
	}

	public boolean equals(Object o)
    {
        if (!(o instanceof Task))
        {
            return false;
        }
        else
        {
        	Task t = (Task)o;
            return t.id == id;
        }
    }

	public long getLifespan() {
		return lifespan;
	}
	
	public void setLifespan(short ls) {
		lifespan = ls;
	}

	public boolean canGolemPerformTask(IGolemAPI golem) {
		int dim = (golem.getGolemWorld() instanceof net.minecraft.server.level.ServerLevel sl) ? sl.dimension().identifier().hashCode() : 0;
		ISealEntity se = GolemHelper.getSealEntity(dim, sealPos);
		if (se!=null) {
			if (golem.getGolemColor()>0 && se.getColor()>0 && golem.getGolemColor() != se.getColor()) return false;
			return se.getSeal().canGolemPerformTask(golem,this);
		} else {
			return true;
		}
	}

	public int getData() {
		return data;
	}

	public void setData(int data) {
		this.data = data;
	}

	public ProvisionRequest getLinkedProvision() {
		return linkedProvision;
	}

	public void setLinkedProvision(ProvisionRequest linkedProvision) {
		this.linkedProvision = linkedProvision;
	}

	
	
	
//	public static Task readNBT(CompoundTag nbt)
//  {		
//		Task task = new Task();
//		task.id = nbt.getIntOr("id", 0);
//		task.type = nbt.getByteOr("type", (byte)0);		
//		if (nbt.child("pos").isPresent()) task.pos = BlockPos.of(nbt.getLongOr("pos", 0L));	
//		
//		if (nbt.child("GUUIDMost").isPresent() && nbt.child("GUUIDLeast").isPresent())
//			task.golemUUID = new UUID(nbt.getLongOr("GUUIDMost", 0L), nbt.getLongOr("GUUIDLeast", 0L));
//		
//		if (nbt.child("EUUIDMost").isPresent() && nbt.child("EUUIDLeast").isPresent())
//			task.entityUUID = new UUID(nbt.getLongOr("EUUIDMost", 0L), nbt.getLongOr("EUUIDLeast", 0L));
//		
//		if (task.pos==null && task.entityUUID==null) return null;
//		
//		task.reserved = nbt.getBooleanOr("reserved", false);
//		task.waitOnSuspension = nbt.getBooleanOr("wos", false);
//		task.suspended = false;
//		task.completed = nbt.getBooleanOr("completed", false);
//		task.expireTime = System.currentTimeMillis() + 300000;		
//		if (nbt.child("sealpos").isPresent()) {
//			CompoundTag sealpos = nbt.getCompoundOrEmpty("sealpos");
//			SealPos sp = new SealPos(BlockPos.of(nbt.getLongOr("pos", 0L)), Direction.values()[nbt.getByteOr("face", (byte)0)]);
//			TaskHandler.sealTaskCrossRef.put(task.id, sp);
//		}
//		return task;
//  }
//	
//	public static CompoundTag writeNBT(Task task)
//  {
//		CompoundTag nbt = new CompoundTag();
//		nbt.putInt("id", task.id);
//		nbt.putByte("type", task.type);
//		if (task.pos!=null) nbt.putLong("pos", task.pos.asLong());
//		if (task.entity!=null) {
//			nbt.putLong("EUUIDMost", task.entity.getUUID().getMostSignificantBits());
//          nbt.putLong("EUUIDLeast", task.entity.getUUID().getLeastSignificantBits());
//		}
//		if (task.golemUUID!=null) {
//			nbt.putLong("GUUIDMost", task.golemUUID.getMostSignificantBits());
//          nbt.putLong("GUUIDLeast", task.golemUUID.getLeastSignificantBits());
//		}
//		nbt.putBoolean("reserved", task.reserved);
//		nbt.putBoolean("wos", task.waitOnSuspension);
//		nbt.putBoolean("completed", task.completed);
//		
//		SealPos sp = TaskHandler.sealTaskCrossRef.get(task.getId());
//		if (sp!=null) {
//			CompoundTag sealpos = new CompoundTag();
//			sealpos.putLong("pos", sp.pos.asLong());
//			sealpos.putByte("face", (byte) sp.face.ordinal());
//			nbt.put("sealpos", sealpos);
//		}
//		return nbt;
//  }

}