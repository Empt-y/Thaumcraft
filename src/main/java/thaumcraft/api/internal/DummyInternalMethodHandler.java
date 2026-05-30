package thaumcraft.api.internal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge.EnumKnowledgeType;
import thaumcraft.api.capabilities.IPlayerWarp.EnumWarpType;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.api.research.ResearchCategory;


public class DummyInternalMethodHandler implements IInternalMethodHandler {
	
	@Override
	public boolean completeResearch(Player player, String researchkey) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addWarpToPlayer(Player player, int amount, EnumWarpType type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AspectList getObjectAspects(ItemStack is) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AspectList generateTags(ItemStack is) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float drainVis(Level world, BlockPos pos, float amount, boolean simulate) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float drainFlux(Level world, BlockPos pos, float amount, boolean simulate) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addVis(Level world, BlockPos pos, float amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addFlux(Level world, BlockPos pos, float amount, boolean showEffect) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float getTotalAura(Level world, BlockPos pos) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getVis(Level world, BlockPos pos) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getFlux(Level world, BlockPos pos) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getAuraBase(Level world, BlockPos pos) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void registerSeal(ISeal seal) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ISeal getSeal(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISealEntity getSealEntity(int dim, SealPos pos) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addGolemTask(int dim, Task task) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean shouldPreserveAura(Level world, Player player,
			BlockPos pos) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ItemStack getSealStack(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean doesPlayerHaveRequisites(Player player, String researchkey) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addKnowledge(Player player, EnumKnowledgeType type, ResearchCategory field, int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean progressResearch(Player player, String researchkey) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getActualWarp(Player player) {
		// TODO Auto-generated method stub
		return 0;
	}

	

	
}
