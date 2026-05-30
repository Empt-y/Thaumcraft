package thaumcraft.api.golems;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.api.golems.tasks.Task;



public class GolemHelper {

	/**
	 * Make sure to register your seals during the preInit phase before TC is loaded
	 * @param seal
	 */
	public static void registerSeal(ISeal seal) {
		ThaumcraftApi.internalMethods.registerSeal(seal);
	}

	public static ISeal getSeal(String key) {
		return ThaumcraftApi.internalMethods.getSeal(key);
	}
	
	public static ItemStack getSealStack(String key) {
		return ThaumcraftApi.internalMethods.getSealStack(key);
	}

	public static ISealEntity getSealEntity(int dim, SealPos pos) {
		return ThaumcraftApi.internalMethods.getSealEntity(dim, pos);
	}
	
	public static void addGolemTask(int dim, Task task) {
		ThaumcraftApi.internalMethods.addGolemTask(dim, task);
	}
	
	public static HashMap<Integer,ArrayList<ProvisionRequest>> provisionRequests = new HashMap<>();
	static int LISTLIMIT = 1000;
	
	/**
	 * 
	 * @param world
	 * @param seal
	 * @param stack the stack requested. Can accept wildcard values.
	 */
	public static void requestProvisioning(Level world, ISealEntity seal, ItemStack stack) {
		if (!provisionRequests.containsKey((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0)))
			provisionRequests.put((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), new ArrayList<ProvisionRequest>());
		ArrayList<ProvisionRequest> list = provisionRequests.get((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0));
		ProvisionRequest pr = new ProvisionRequest(seal,stack.copy());
		if (!list.contains(pr)) {
			list.add(pr);
		}
		if (list.size()>LISTLIMIT) list.remove(0);
	}
	
	/**
	 * 
	 * @param world
	 * @param pos
	 * @param side
	 * @param stack the stack requested. Can accept wildcard values.
	 */
	public static void requestProvisioning(Level world, BlockPos pos, Direction side, ItemStack stack) {
		if (!provisionRequests.containsKey((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0)))
			provisionRequests.put((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), new ArrayList<ProvisionRequest>());
		ArrayList<ProvisionRequest> list = provisionRequests.get((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0));
		ProvisionRequest pr = new ProvisionRequest(pos, side, stack.copy());
		if (!list.contains(pr)) {
			list.add(pr);
		}
		if (list.size()>LISTLIMIT) list.remove(0);
	}
	
	/**
	 * 
	 * @param world
	 * @param entity
	 * @param stack the stack requested. Can accept wildcard values.
	 */
	public static void requestProvisioning(Level world, Entity entity, ItemStack stack) {
		if (!provisionRequests.containsKey((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0)))
			provisionRequests.put((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), new ArrayList<ProvisionRequest>());
		ArrayList<ProvisionRequest> list = provisionRequests.get((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0));
		ProvisionRequest pr = new ProvisionRequest(entity, stack.copy());
		if (!list.contains(pr)) {
			list.add(pr);
		}
		if (list.size()>LISTLIMIT) list.remove(0);
	}
	
	/**
	 * 
	 * @param world
	 * @param pos
	 * @param side
	 * @param stack the stack requested. Can accept wildcard values.
	 * @param ui a unique number to make the request slightly more unique in case you want to add multiple similar requests
	 */
	public static void requestProvisioning(Level world, BlockPos pos, Direction side, ItemStack stack, int ui) {
		if (!provisionRequests.containsKey((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0)))
			provisionRequests.put((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), new ArrayList<ProvisionRequest>());
		ArrayList<ProvisionRequest> list = provisionRequests.get((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0));
		ProvisionRequest pr = new ProvisionRequest(pos, side, stack.copy());
		pr.setUI(ui);
		if (!list.contains(pr)) {
			list.add(pr);
		}
		if (list.size()>LISTLIMIT) list.remove(0);
	}
	
	/**
	 * 
	 * @param world
	 * @param entity
	 * @param stack the stack requested. Can accept wildcard values.
	 * @param ui a unique number to make the request slightly more unique in case you want to add multiple similar requests
	 */
	public static void requestProvisioning(Level world, Entity entity, ItemStack stack, int ui) {
		if (!provisionRequests.containsKey((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0)))
			provisionRequests.put((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), new ArrayList<ProvisionRequest>());
		ArrayList<ProvisionRequest> list = provisionRequests.get((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0));
		ProvisionRequest pr = new ProvisionRequest(entity, stack.copy());
		pr.setUI(ui);
		if (!list.contains(pr)) {
			list.add(pr);
		}
		if (list.size()>LISTLIMIT) list.remove(0);
	}
	
	/**
	 * This method is used to get a single blockpos from within a designated seal area.
	 * This method is best used if you want to increment through the blocks in the area. 
	 * @param seal
	 * @param count a value used to derive a specific pos 
	 * @return
	 */
	public static BlockPos getPosInArea(ISealEntity seal, int count) {
		int xx = 1 + (seal.getArea().getX()-1) * (seal.getSealPos().face.getStepX()==0?2:1);
		int yy = 1 + (seal.getArea().getY()-1) * (seal.getSealPos().face.getStepY()==0?2:1);
		int zz = 1 + (seal.getArea().getZ()-1) * (seal.getSealPos().face.getStepZ()==0?2:1);
		
		int qx = seal.getSealPos().face.getStepX()!=0?seal.getSealPos().face.getStepX():1;
		int qy = seal.getSealPos().face.getStepY()!=0?seal.getSealPos().face.getStepY():1;
		int qz = seal.getSealPos().face.getStepZ()!=0?seal.getSealPos().face.getStepZ():1;
		
		int y = qy*((count/zz)/xx)%yy + seal.getSealPos().face.getStepY();		
		int x = qx*(count/zz)%xx + seal.getSealPos().face.getStepX();					
		int z = qz*count%zz + seal.getSealPos().face.getStepZ();		
				
		BlockPos p = seal.getSealPos().pos.offset(
				x - (seal.getSealPos().face.getStepX()==0?xx/2:0),
				y - (seal.getSealPos().face.getStepY()==0?yy/2:0),
				z - (seal.getSealPos().face.getStepZ()==0?zz/2:0));
		
		return p;
	}

	
	/**
	 * Returns the designated seal area as a AABB
	 * @param seal
	 * @return
	 */
	public static AABB getBoundsForArea(ISealEntity seal) {
		return new AABB(
				seal.getSealPos().pos.getX(), seal.getSealPos().pos.getY(), seal.getSealPos().pos.getZ(), 
				seal.getSealPos().pos.getX()+1, seal.getSealPos().pos.getY()+1, seal.getSealPos().pos.getZ()+1)
				.move(
					seal.getSealPos().face.getStepX(),
					seal.getSealPos().face.getStepY(),
					seal.getSealPos().face.getStepZ())
				.expandTowards(
					seal.getSealPos().face.getStepX()!=0?(seal.getArea().getX()-1) * seal.getSealPos().face.getStepX():0,
					seal.getSealPos().face.getStepY()!=0?(seal.getArea().getY()-1) * seal.getSealPos().face.getStepY():0,
					seal.getSealPos().face.getStepZ()!=0?(seal.getArea().getZ()-1) * seal.getSealPos().face.getStepZ():0)
				.inflate(
					seal.getSealPos().face.getStepX()==0?seal.getArea().getX()-1:0,
					seal.getSealPos().face.getStepY()==0?seal.getArea().getY()-1:0,
					seal.getSealPos().face.getStepZ()==0?seal.getArea().getZ()-1:0);
	}
	
	

}
