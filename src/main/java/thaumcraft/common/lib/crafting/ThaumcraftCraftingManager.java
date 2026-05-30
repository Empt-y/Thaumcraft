package thaumcraft.common.lib.crafting;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.Rarity;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
// import net.minecraft.world.item.Item /* ArmorItem removed */; // removed
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.HoeItem; // OK
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.ItemStack;
// import net.minecraft.world.item.Item /* SwordItem removed */; // removed
// import net.minecraft.world.item.Item /* DiggerItem removed */;; // broken import
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
// FML ObfuscationReflectionHelper removed
// import net.minecraftforge.fml.relauncher.Object /* ReflectionHelper removed */; // removed
// minecraftforge.registries import removed
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.IThaumcraftRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.internal.CommonInternals;


public class ThaumcraftCraftingManager
{
    static int ASPECTCAP = 500;
    
    public static CrucibleRecipe findMatchingCrucibleRecipe(Player player, AspectList aspects, ItemStack lastDrop) {
        int highest = 0;
        CrucibleRecipe out = null;
        for (Object re : ThaumcraftApi.getCraftingRecipes().values()) {
            if (re != null && re instanceof CrucibleRecipe) {
                CrucibleRecipe recipe = (CrucibleRecipe)re;
                ItemStack temp = lastDrop.copy();
                temp.setCount(1);
                if (player == null || !ThaumcraftCapabilities.knowsResearchStrict(player, recipe.getResearch()) || !recipe.matches(aspects, temp)) {
                    continue;
                }
                int result = recipe.getAspects().visSize();
                if (result <= highest) {
                    continue;
                }
                highest = result;
                out = recipe;
            }
        }
        return out;
    }
    
    public static IArcaneRecipe findMatchingArcaneRecipe(CraftingContainer matrix, Player player) {
        int var2 = 0;
        ItemStack var3 = null;
        ItemStack var4 = null;
        for (int var5 = 0; var5 < 15; ++var5) {
            ItemStack var6 = matrix.getItem(var5);
            if (!var6.isEmpty()) {
                if (var2 == 0) {
                    var3 = var6;
                }
                if (var2 == 1) {
                    var4 = var6;
                }
                ++var2;
            }
        }
        // CraftingManager removed — Arcane recipe lookup not supported here
        return null;
    }
    
    public static ItemStack findMatchingArcaneRecipeResult(CraftingContainer awb, Player player) {
        IArcaneRecipe var13 = findMatchingArcaneRecipe(awb, player);
        return (var13 == null) ? null : var13.getResultItem();
    }
    
    public static AspectList findMatchingArcaneRecipeCrystals(CraftingContainer awb, Player player) {
        IArcaneRecipe var13 = findMatchingArcaneRecipe(awb, player);
        return (var13 == null) ? null : var13.getCrystals();
    }
    
    public static int findMatchingArcaneRecipeVis(CraftingContainer awb, Player player) {
        IArcaneRecipe var13 = findMatchingArcaneRecipe(awb, player);
        return (var13 == null) ? 0 : ((var13.getVis() > 0) ? var13.getVis() : var13.getVis());
    }
    
    public static InfusionRecipe findMatchingInfusionRecipe(ArrayList<ItemStack> items, ItemStack input, Player player) {
        for (Object recipe : ThaumcraftApi.getCraftingRecipes().values()) {
            if (recipe != null && recipe instanceof InfusionRecipe && ((InfusionRecipe)recipe).matches(items, input, player.level(), player)) {
                return (InfusionRecipe)recipe;
            }
        }
        return null;
    }
    
    public static AspectList getObjectTags(ItemStack itemstack) {
        return getObjectTags(itemstack, null);
    }
    
    public static AspectList getObjectTags(ItemStack itemstack, ArrayList<String> history) {
        if (itemstack.isEmpty()) {
            return null;
        }
        int ss = CommonInternals.generateUniqueItemstackId(itemstack);
        AspectList tmp = CommonInternals.objectTags.get(ss);
        if (tmp == null) {
            try {
                ItemStack sc = itemstack.copy();
                sc.setDamageValue(32767);
                ss = CommonInternals.generateUniqueItemstackId(sc);
                tmp = CommonInternals.objectTags.get(ss);
                if (tmp == null) {
                    if (itemstack.getDamageValue() == 32767) {
                        int index = 0;
                        do {
                            sc.setDamageValue(index);
                            ss = CommonInternals.generateUniqueItemstackId(sc);
                            tmp = CommonInternals.objectTags.get(ss);
                        } while (++index < 16 && tmp == null);
                    }
                    if (tmp == null) {
                        sc = itemstack.copy();
                        ss = CommonInternals.generateUniqueItemstackIdStripped(sc);
                        tmp = CommonInternals.objectTags.get(ss);
                        if (tmp == null) {
                            sc = itemstack.copy();
                            sc.setDamageValue(32767);
                            ss = CommonInternals.generateUniqueItemstackIdStripped(sc);
                            tmp = CommonInternals.objectTags.get(ss);
                        }
                    }
                    if (tmp == null) {
                        tmp = generateTags(itemstack, history);
                    }
                }
            }
            catch (Exception ex) {}
        }
        return capAspects(getBonusTags(itemstack, tmp), 500);
    }
    
    private static AspectList capAspects(AspectList sourcetags, int amount) {
        if (sourcetags == null) {
            return sourcetags;
        }
        AspectList out = new AspectList();
        for (Aspect aspect : sourcetags.getAspects()) {
            if (aspect != null) {
                out.merge(aspect, Math.min(amount, sourcetags.getAmount(aspect)));
            }
        }
        return out;
    }
    
    private static AspectList getBonusTags(ItemStack itemstack, AspectList sourcetags) {
        AspectList tmp = new AspectList();
        if (itemstack.isEmpty()) {
            return tmp;
        }
        Item item = itemstack.getItem();
        if (item != null && item instanceof IEssentiaContainerItem && !((IEssentiaContainerItem)item).ignoreContainedAspects()) {
            if (sourcetags != null) {
                sourcetags.aspects.clear();
            }
            tmp = ((IEssentiaContainerItem)item).getAspects(itemstack);
            if (tmp != null && tmp.size() > 0) {
                for (Aspect tag : tmp.copy().getAspects()) {
                    if (tmp.getAmount(tag) <= 0) {
                        tmp.remove(tag);
                    }
                }
            }
        }
        if (tmp == null) {
            tmp = new AspectList();
        }
        if (sourcetags != null) {
            for (Aspect tag : sourcetags.getAspects()) {
                if (tag != null) {
                    tmp.add(tag, sourcetags.getAmount(tag));
                }
            }
        }
        if (item != null && tmp != null) {
            // Note: ArmorItem and SwordItem were removed in MC 1.21.5; use component checks
            if (itemstack.has(net.minecraft.core.component.DataComponents.ATTRIBUTE_MODIFIERS)) {
                tmp.merge(Aspect.PROTECT, 2);
            }
            if (item instanceof net.minecraft.world.item.BowItem) {
                tmp.merge(Aspect.AVERSION, 10).merge(Aspect.FLIGHT, 5);
            }
            else if (item instanceof ShearsItem || item instanceof HoeItem) {
                tmp.merge(Aspect.TOOL, 16);
            }
            // OreDictionary removed — dye tag checks not supported in modern port
            net.minecraft.world.item.enchantment.ItemEnchantments enchantments = itemstack.getEnchantments();
            if (!enchantments.keySet().isEmpty()) {
                int var5 = 0;
                for (it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment>> enchEntry : enchantments.entrySet()) {
                    net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment> e = enchEntry.getKey();
                    int lvl = enchEntry.getIntValue() * 3;
                    if (e.is(Enchantments.AQUA_AFFINITY))             tmp.merge(Aspect.WATER, lvl);
                    else if (e.is(Enchantments.BANE_OF_ARTHROPODS))   tmp.merge(Aspect.BEAST, lvl / 2).merge(Aspect.AVERSION, lvl / 2);
                    else if (e.is(Enchantments.BLAST_PROTECTION))     tmp.merge(Aspect.PROTECT, lvl / 2).merge(Aspect.ENTROPY, lvl / 2);
                    else if (e.is(Enchantments.EFFICIENCY))           tmp.merge(Aspect.TOOL, lvl);
                    else if (e.is(Enchantments.FEATHER_FALLING))      tmp.merge(Aspect.FLIGHT, lvl);
                    else if (e.is(Enchantments.FIRE_ASPECT))          tmp.merge(Aspect.FIRE, lvl / 2).merge(Aspect.AVERSION, lvl / 2);
                    else if (e.is(Enchantments.FIRE_PROTECTION))      tmp.merge(Aspect.PROTECT, lvl / 2).merge(Aspect.FIRE, lvl / 2);
                    else if (e.is(Enchantments.FLAME))                tmp.merge(Aspect.FIRE, lvl);
                    else if (e.is(Enchantments.FORTUNE))              tmp.merge(Aspect.DESIRE, lvl);
                    else if (e.is(Enchantments.INFINITY))             tmp.merge(Aspect.CRAFT, lvl);
                    else if (e.is(Enchantments.KNOCKBACK))            tmp.merge(Aspect.AIR, lvl);
                    else if (e.is(Enchantments.LOOTING))              tmp.merge(Aspect.DESIRE, lvl);
                    else if (e.is(Enchantments.POWER))                tmp.merge(Aspect.AVERSION, lvl);
                    else if (e.is(Enchantments.PROJECTILE_PROTECTION)) tmp.merge(Aspect.PROTECT, lvl);
                    else if (e.is(Enchantments.PROTECTION))           tmp.merge(Aspect.PROTECT, lvl);
                    else if (e.is(Enchantments.PUNCH))                tmp.merge(Aspect.AIR, lvl);
                    else if (e.is(Enchantments.RESPIRATION))          tmp.merge(Aspect.AIR, lvl);
                    else if (e.is(Enchantments.SHARPNESS))            tmp.merge(Aspect.AVERSION, lvl);
                    else if (e.is(Enchantments.SILK_TOUCH))           tmp.merge(Aspect.EXCHANGE, lvl);
                    else if (e.is(Enchantments.THORNS))               tmp.merge(Aspect.AVERSION, lvl);
                    else if (e.is(Enchantments.SMITE))                tmp.merge(Aspect.UNDEAD, lvl / 2).merge(Aspect.AVERSION, lvl / 2);
                    else if (e.is(Enchantments.UNBREAKING))           tmp.merge(Aspect.EARTH, lvl);
                    else if (e.is(Enchantments.DEPTH_STRIDER))        tmp.merge(Aspect.WATER, lvl);
                    else if (e.is(Enchantments.LUCK_OF_THE_SEA))      tmp.merge(Aspect.DESIRE, lvl);
                    else if (e.is(Enchantments.LURE))                 tmp.merge(Aspect.BEAST, lvl);
                    else if (e.is(Enchantments.FROST_WALKER))         tmp.merge(Aspect.COLD, lvl);
                    else if (e.is(Enchantments.MENDING))              tmp.merge(Aspect.CRAFT, lvl);
                    int weight = e.value().definition().weight();
                    if (weight <= 1) var5 += 6;
                    else if (weight <= 2) var5 += 4;
                    else if (weight <= 5) var5 += 2;
                    var5 += lvl;
                }
                if (var5 > 0) {
                    tmp.merge(Aspect.MAGIC, var5);
                }
            }
        }
        return AspectHelper.cullTags(tmp);
    }
    
    public static void getPotionReagentsRecursive(Object potion, HashSet<ItemStack> hashSet) {
        // TODO: port to modern MC potion brewing API (BrewingRecipeRegistry)
    }
    
    public static AspectList generateTags(ItemStack is) {
        AspectList temp = generateTags(is, new ArrayList<String>());
        return temp;
    }
    
    public static AspectList generateTags(ItemStack is, ArrayList<String> history) {
        if (history == null) {
            history = new ArrayList<String>();
        }
        ItemStack stack = is.copy();
        stack.setCount(1);
        try {
            if (stack.isDamageableItem()) {
                stack.setDamageValue(32767);
            }
        }
        catch (Exception ex) {}
        if (ThaumcraftApi.exists(stack)) {
            return getObjectTags(stack, history);
        }
        String ss = stack.getItem().toString() + ":" + stack.getDamageValue();
        if (history.contains(ss)) {
            return null;
        }
        history.add(ss);
        if (history.size() < 100) {
            if (stack.getDamageValue() == 32767) {
                stack.setDamageValue(0);
            }
            AspectList ret = generateTagsFromRecipes(stack, history);
            ret = capAspects(ret, 500);
            ThaumcraftApi.registerObjectTag(is, ret);
            return ret;
        }
        return null;
    }
    
    private static AspectList generateTagsFromCrucibleRecipes(ItemStack stack, ArrayList<String> history) {
        CrucibleRecipe cr = ThaumcraftApi.getCrucibleRecipe(stack);
        if (cr == null) {
            return null;
        }
        AspectList ot = cr.getAspects().copy();
        int ss = cr.getResultItem().getCount();
        ItemStack[] catItems = cr.getCatalyst().items().map(h -> new ItemStack(h.value())).toArray(ItemStack[]::new);
        if (catItems.length == 0) return null;
        ItemStack cat = catItems[0];
        if (cat == null || cat.isEmpty()) {
            return null;
        }
        AspectList ot2 = getObjectTags(cat, history);
        AspectList out = new AspectList();
        if (ot2 != null && ot2.size() > 0) {
            for (Aspect tt : ot2.getAspects()) {
                out.add(tt, ot2.getAmount(tt));
            }
        }
        for (Aspect tt : ot.getAspects()) {
            int amt = (int)(Math.sqrt(ot.getAmount(tt)) / ss);
            out.add(tt, amt);
        }
        for (Aspect as : out.getAspects()) {
            if (out.getAmount(as) <= 0) {
                out.remove(as);
            }
        }
        return out;
    }
    
    private static AspectList generateTagsFromInfusionRecipes(ItemStack stack, ArrayList<String> history) {
        InfusionRecipe cr = ThaumcraftApi.getInfusionRecipe(stack);
        if (cr != null) {
            AspectList ot = cr.getAspects().copy();
            NonNullList<Ingredient> ingredients = NonNullList.create();
            ItemStack infusionResult = (cr.getRecipeOutput() instanceof ItemStack) ? (ItemStack) cr.getRecipeOutput() : ItemStack.EMPTY;
            if (!infusionResult.isEmpty()) ingredients.add(Ingredient.of(infusionResult.getItem()));
            ingredients.addAll(cr.getComponents());
            AspectList out = new AspectList();
            AspectList ot2 = getAspectsFromIngredients(ingredients, infusionResult, null, history);
            for (Aspect tt : ot2.getAspects()) {
                out.add(tt, ot2.getAmount(tt));
            }
            for (Aspect tt : ot.getAspects()) {
                int amt = (int)(Math.sqrt(ot.getAmount(tt)) / (infusionResult.isEmpty() ? 1 : infusionResult.getCount()));
                out.add(tt, amt);
            }
            for (Aspect as : out.getAspects()) {
                if (out.getAmount(as) <= 0) {
                    out.remove(as);
                }
            }
            return out;
        }
        return null;
    }
    
    private static AspectList generateTagsFromCraftingRecipes(ItemStack stack, ArrayList<String> history) {
        AspectList ret = null;
        int value = Integer.MAX_VALUE;
        // CraftingManager removed in modern MC — crafting recipe lookup not supported
        return ret;
    }
    
    private static AspectList getAspectsFromIngredients(NonNullList<Ingredient> nonNullList, ItemStack recipeOut, Recipe recipe, ArrayList<String> history) {
        AspectList out = new AspectList();
        AspectList mid = new AspectList();
        NonNullList<ItemStack> exlist = NonNullList.create();
        // CraftingContainer and getRemainingItems not available without input context
        int index2 = -1;
        for (Ingredient is2 : nonNullList) {
            ++index2;
            ItemStack[] is2Stacks = is2.items().map(h -> new ItemStack(h.value())).toArray(ItemStack[]::new);
            if (is2Stacks.length <= 0) {
                continue;
            }
            AspectList obj = getObjectTags(is2Stacks[0], history);
            if (obj == null) {
                continue;
            }
            for (Aspect as : obj.getAspects()) {
                if (as != null) {
                    mid.add(as, obj.getAmount(as));
                }
            }
        }
        if (exlist != null) {
            for (ItemStack ri : exlist) {
                if (!ri.isEmpty()) {
                    AspectList obj = getObjectTags(ri, history);
                    for (Aspect as : obj.getAspects()) {
                        mid.reduce(as, obj.getAmount(as));
                    }
                }
            }
        }
        for (Aspect as2 : mid.getAspects()) {
            if (as2 != null) {
                float v = mid.getAmount(as2) * 0.75f / recipeOut.getCount();
                if (v < 1.0f && v > 0.75) {
                    v = 1.0f;
                }
                out.add(as2, (int)v);
            }
        }
        for (Aspect as2 : out.getAspects()) {
            if (out.getAmount(as2) <= 0) {
                out.remove(as2);
            }
        }
        return out;
    }
    
    private static AspectList generateTagsFromRecipes(ItemStack stack, ArrayList<String> history) {
        AspectList ret = null;
        int value = 0;
        ret = generateTagsFromCrucibleRecipes(stack, history);
        if (ret != null) {
            return ret;
        }
        ret = generateTagsFromInfusionRecipes(stack, history);
        if (ret != null) {
            return ret;
        }
        ret = generateTagsFromCraftingRecipes(stack, history);
        return ret;
    }
}
