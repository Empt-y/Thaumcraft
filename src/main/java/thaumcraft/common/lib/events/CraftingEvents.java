package thaumcraft.common.lib.events;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.items.consumables.ItemPhial;
import thaumcraft.common.lib.research.ResearchManager;


@net.neoforged.fml.common.EventBusSubscriber(modid = "thaumcraft")
public class CraftingEvents
{
    public int getBurnTime(ItemStack fuel) {
        if (ItemStack.isSameItem(fuel, new ItemStack(ItemsTC.alumentum))) {
            return 4800;
        }
        if (ItemStack.isSameItem(fuel, new ItemStack(BlocksTC.logGreatwood))) {
            return 500;
        }
        if (ItemStack.isSameItem(fuel, new ItemStack(BlocksTC.logSilverwood))) {
            return 400;
        }
        return 0;
    }
    
    @SubscribeEvent
    public static void onCrafting(PlayerEvent.ItemCraftedEvent event) {
        ItemStack result = event.getCrafting();
        int warp = ThaumcraftApi.getWarp(result);
        if (!ModConfig.CONFIG_MISC.wussMode && warp > 0 && !event.getEntity().level().isClientSide()) {
            ThaumcraftApi.internalMethods.addWarpToPlayer(event.getEntity(), warp, IPlayerWarp.EnumWarpType.NORMAL);
        }
        if (result.getItem() == ItemsTC.label && !result.isEmpty()) {
            for (int var2 = 0; var2 < event.getInventory().getContainerSize(); ++var2) {
                ItemStack var3 = event.getInventory().getItem(var2);
                if (!var3.isEmpty() && var3.getItem() instanceof ItemPhial) {
                    var3.grow(1);
                    event.getInventory().setItem(var2, var3);
                }
            }
        }
        if (event.getEntity() != null && !event.getEntity().level().isClientSide()) {
            int stackHash = ResearchManager.createItemStackHash(result.copy());
            if (ResearchManager.craftingReferences.contains(stackHash)) {
                ResearchManager.completeResearch(event.getEntity(), "[#]" + stackHash);
            }
            else {
                // Damage value ignored in 1.21.5 - match by item only
                stackHash = ResearchManager.createItemStackHash(new ItemStack(result.getItem(), result.getCount()));
                if (ResearchManager.craftingReferences.contains(stackHash)) {
                    ResearchManager.completeResearch(event.getEntity(), "[#]" + stackHash);
                }
            }
            // OreDictionary removed - tag-based matching not implemented here
        }
    }
    
    @SubscribeEvent
    public static void onAnvil(AnvilUpdateEvent event) {
        if (event.getLeft().getItem() == ItemsTC.primordialPearl || event.getRight().getItem() == ItemsTC.primordialPearl) {
            event.setCanceled(true);
        }
    }
}
