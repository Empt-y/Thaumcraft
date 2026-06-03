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
		return false;
	}

	@Override
	public void addWarpToPlayer(Player player, int amount, EnumWarpType type) {
		
	}

	@Override
	public AspectList getObjectAspects(ItemStack is) {
		return null;
	}

	@Override
	public AspectList generateTags(ItemStack is) {
		return null;
	}

	@Override
	public float drainVis(Level world, BlockPos pos, float amount, boolean simulate) {
		return 0;
	}

	@Override
	public float drainFlux(Level world, BlockPos pos, float amount, boolean simulate) {
		return 0;
	}

	@Override
	public void addVis(Level world, BlockPos pos, float amount) {
		
	}

	@Override
	public void addFlux(Level world, BlockPos pos, float amount, boolean showEffect) {
		
	}

	@Override
	public float getTotalAura(Level world, BlockPos pos) {
		return 0;
	}

	@Override
	public float getVis(Level world, BlockPos pos) {
		return 0;
	}

	@Override
	public float getFlux(Level world, BlockPos pos) {
		return 0;
	}

	@Override
	public int getAuraBase(Level world, BlockPos pos) {
		return 0;
	}

	@Override
	public void registerSeal(ISeal seal) {
		
	}

	@Override
	public ISeal getSeal(String key) {
		return null;
	}

	@Override
	public ISealEntity getSealEntity(int dim, SealPos pos) {
		return null;
	}

	@Override
	public void addGolemTask(int dim, Task task) {
		
	}

	@Override
	public boolean shouldPreserveAura(Level world, Player player,
			BlockPos pos) {
		return false;
	}

	@Override
	public ItemStack getSealStack(String key) {
		return null;
	}

	@Override
	public boolean doesPlayerHaveRequisites(Player player, String researchkey) {
		return false;
	}

	@Override
	public boolean addKnowledge(Player player, EnumKnowledgeType type, ResearchCategory field, int amount) {
		return false;
	}

	@Override
	public boolean progressResearch(Player player, String researchkey) {
		return false;
	}

	@Override
	public int getActualWarp(Player player) {
		return 0;
	}

	

	
}
