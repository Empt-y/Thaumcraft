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


/**
 * 
 * @author Azanor
 *
 * @see IInternalMethodHandler#addKnowledge
 * @see IInternalMethodHandler#progressResearch
 * @see IInternalMethodHandler#completeResearch
 * @see IInternalMethodHandler#doesPlayerHaveRequisites
 * @see IInternalMethodHandler#addWarpToPlayer
 * @see IInternalMethodHandler#getObjectAspects
 * @see IInternalMethodHandler#generateTags
 * @see IInternalMethodHandler#drainVis
 * @see IInternalMethodHandler#drainFlux
 * @see IInternalMethodHandler#addVis
 * @see IInternalMethodHandler#addFlux
 * @see IInternalMethodHandler#getTotalAura
 * @see IInternalMethodHandler#getVis
 * @see IInternalMethodHandler#getFlux
 * @see IInternalMethodHandler#getAuraBase
 * @see IInternalMethodHandler#shouldPreserveAura
 * @see IInternalMethodHandler#registerSeal
 * @see IInternalMethodHandler#getSeal
 * @see IInternalMethodHandler#getSealEntity
 * @see IInternalMethodHandler#addGolemTask 
 * @see IInternalMethodHandler#getSealStack
 */
public interface IInternalMethodHandler {
	
	/**
	 * Add raw knowledge points (not whole, Object knowledges) to the given player.
	 * This method will trigger appropriate gui notifications, etc.
	 * @param player
	 * @param type
	 * @param category
	 * @param amount
	 * @return if the knowledge was added
	 */
	public boolean addKnowledge(Player player, EnumKnowledgeType type, ResearchCategory category, int amount);
	
	/**
	 * Progresses research with all the proper bells and whistles (popups, sounds, warp, etc)
	 * If the research is linked to a research entry with stages the player's current stage will be increased 
	 * by 1, or set to 1 if the research was not known before.
	 * @param player
	 * @param researchkey
	 * @return if operation succeeded
	 */
	boolean progressResearch(Player player, String researchkey);
	
	/**
	 * Completes research with all the proper bells and whistles (popups, sounds, warp, etc)
	 * This automatically sets all its stages as complete. 
	 * Most of the time you should probably use progressResearch instead.
	 * @param player
	 * @param researchkey
	 * @return if operation succeeded
	 */
	public boolean completeResearch(Player player, String researchkey);
	
	/**
	 * @param player 
	 * @param researchkey the key of the research you want to check
	 * @return does the player have all the required knowledge to complete the passed researchkey
	 */
	boolean doesPlayerHaveRequisites(Player player, String researchkey);
	
	/**
	 * Adds warp with all the proper bells and whistles (text, sounds, etc)
	 * @param player
	 * @param researchkey
	 * @return
	 */
	public void addWarpToPlayer(Player player, int amount, EnumWarpType type);
	
	/**
	 * The total of the players normal + permanent warp. NOT temporary warp.
	 * @param player
	 * @return
	 */
	public int getActualWarp(Player player);

	public AspectList getObjectAspects(ItemStack is);
	public AspectList generateTags(ItemStack is);
	
	public float drainVis(Level world, BlockPos pos, float amount, boolean simulate);
	public float drainFlux(Level world, BlockPos pos, float amount, boolean simulate);	
	public void addVis(Level world, BlockPos pos, float amount);
	public void addFlux(Level world, BlockPos pos, float amount, boolean showEffect);			
	
	/**
	 * returns the aura and flux in a chunk added together
	 * @param world
	 * @param pos
	 * @return
	 */
	public float getTotalAura(Level world, BlockPos pos);
	public float getVis(Level world, BlockPos pos);
	public float getFlux(Level world, BlockPos pos);
	public int getAuraBase(Level world, BlockPos pos);	
	
	public void registerSeal(ISeal seal);
	public ISeal getSeal(String key);
	public ISealEntity getSealEntity(int dim, SealPos pos);
	public void addGolemTask(int dim, Task task);
	public boolean shouldPreserveAura(Level world, Player player, BlockPos pos);
	public ItemStack getSealStack(String key);

	

	

	

	
	
	
}
