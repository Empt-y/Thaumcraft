package thaumcraft.common.lib.research;
import java.util.Iterator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.research.IScanThing;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ScanningManager;


public class ScanGeneric implements IScanThing
{
    @Override
    public boolean checkThing(Player player, Object obj) {
        if (obj == null) {
            return false;
        }
        AspectList al = null;
        if (obj instanceof Entity && !(obj instanceof ItemEntity)) {
            al = AspectHelper.getEntityAspects((Entity)obj);
        }
        else {
            ItemStack is = ScanningManager.getItemFromParms(player, obj);
            if (is != null && !is.isEmpty()) {
                al = AspectHelper.getObjectAspects(is);
            }
        }
        return al != null && al.size() > 0;
    }
    
    @Override
    public void onSuccess(Player player, Object obj) {
        if (obj == null) {
            return;
        }
        AspectList al = null;
        if (obj instanceof Entity && !(obj instanceof ItemEntity)) {
            al = AspectHelper.getEntityAspects((Entity)obj);
        }
        else {
            ItemStack is = ScanningManager.getItemFromParms(player, obj);
            if (is != null && !is.isEmpty()) {
                al = AspectHelper.getObjectAspects(is);
            }
        }
        if (al != null) {
            for (ResearchCategory category : ResearchCategories.researchCategories.values()) {
                ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, category, category.applyFormula(al));
            }
        }
    }
    
    @Override
    public String getResearchKey(Player player, Object obj) {
        if (obj instanceof Entity e && !(obj instanceof ItemEntity)) {
            net.minecraft.resources.Identifier key = net.minecraft.world.entity.EntityType.getKey(e.getType());
            return "!" + (key != null ? key.toString() : "unknown");
        }
        ItemStack is = ScanningManager.getItemFromParms(player, obj);
        if (is != null && !is.isEmpty()) {
            String s2 = "!" + net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(is.getItem());
            if (!is.isDamageableItem()) {
                s2 += is.getDamageValue();
            }
            return s2;
        }
        return null;
    }
}
