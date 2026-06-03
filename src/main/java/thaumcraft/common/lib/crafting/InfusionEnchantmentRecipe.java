package thaumcraft.common.lib.crafting;
import net.minecraft.world.entity.EquipmentSlot;
// baubles import removed
import com.google.common.collect.Multimap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
// import net.minecraft.world.item.Item /* ArmorItem removed */; // removed
import net.minecraft.world.item.ItemStack;
// import net.minecraft.world.item.Item /* DiggerItem removed */;; // broken import
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.IRechargable;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;


public class InfusionEnchantmentRecipe extends InfusionRecipe
{
    EnumInfusionEnchantment enchantment;
    
    public InfusionEnchantmentRecipe(EnumInfusionEnchantment ench, AspectList as, Object... components) {
        super(ench.research, null, 4, as, Ingredient.of(net.minecraft.world.item.Items.AIR), components);
        enchantment = ench;
    }
    
    public InfusionEnchantmentRecipe(InfusionEnchantmentRecipe recipe, ItemStack in) {
        super(recipe.enchantment.research, null, recipe.instability, recipe.aspects, in, recipe.components.toArray());
        enchantment = recipe.enchantment;
    }
    
    @Override
    public boolean matches(List<ItemStack> input, ItemStack central, Level world, Player player) {
        if (central == null || central.isEmpty() || !ThaumcraftCapabilities.knowsResearch(player, research)) {
            return false;
        }
        if (EnumInfusionEnchantment.getInfusionEnchantmentLevel(central, enchantment) >= enchantment.maxLevel) {
            return false;
        }
        if (!enchantment.toolClasses.contains("all")) {
            boolean cool = false;
            // Modern MC: use item tags for tool class checks instead of Forge's old toolClasses system
            if (central.has(net.minecraft.core.component.DataComponents.WEAPON) && enchantment.toolClasses.contains("weapon")) {
                cool = true;
            }
            if (!cool && !central.isEmpty()) {
                // Map item tags to legacy tool classes
                if (enchantment.toolClasses.contains("pickaxe") && central.is(net.minecraft.tags.ItemTags.PICKAXES)) cool = true;
                else if (enchantment.toolClasses.contains("axe") && central.is(net.minecraft.tags.ItemTags.AXES)) cool = true;
                else if (enchantment.toolClasses.contains("shovel") && central.is(net.minecraft.tags.ItemTags.SHOVELS)) cool = true;
                else if (enchantment.toolClasses.contains("hoe") && central.is(net.minecraft.tags.ItemTags.HOES)) cool = true;
                else if (enchantment.toolClasses.contains("sword") && central.is(net.minecraft.tags.ItemTags.SWORDS)) cool = true;
                else if (enchantment.toolClasses.contains("tool") && (
                    central.is(net.minecraft.tags.ItemTags.PICKAXES) || central.is(net.minecraft.tags.ItemTags.AXES) ||
                    central.is(net.minecraft.tags.ItemTags.SHOVELS) || central.is(net.minecraft.tags.ItemTags.HOES))) cool = true;
            }
            if (!cool && central.has(net.minecraft.core.component.DataComponents.EQUIPPABLE)) {
                net.minecraft.world.item.equipment.Equippable eq = central.get(net.minecraft.core.component.DataComponents.EQUIPPABLE);
                net.minecraft.world.entity.EquipmentSlot slot = (eq != null) ? eq.slot() : EquipmentSlot.CHEST;
                String at = "none";
                switch (slot) {
                    case HEAD: {
                        at = "helm";
                        break;
                    }
                    case CHEST: {
                        at = "chest";
                        break;
                    }
                    case LEGS: {
                        at = "legs";
                        break;
                    }
                    case FEET: {
                        at = "boots";
                        break;
                    }
                }
                if (enchantment.toolClasses.contains("armor") || enchantment.toolClasses.contains(at)) {
                    cool = true;
                }
            }
            // baubles removed - skip bauble check
            if (!cool && enchantment.toolClasses.contains("bauble")) {
                cool = true;
            }
            if (!cool && central.getItem() instanceof IRechargable && enchantment.toolClasses.contains("chargable")) {
                cool = true;
            }
            if (!cool) {
                return false;
            }
        }
        if (!(getRecipeInput() == null || !getRecipeInput().items().findAny().isPresent() || getRecipeInput().test(central))) return false;
        if (input == null || getComponents() == null) return true;
        List<ItemStack> comps = new java.util.ArrayList<>(input);
        for (net.minecraft.world.item.crafting.Ingredient comp : getComponents()) {
            boolean found = false;
            for (java.util.Iterator<ItemStack> it = comps.iterator(); it.hasNext(); ) {
                if (comp.test(it.next())) { it.remove(); found = true; break; }
            }
            if (!found) return false;
        }
        return true;
    }
    
    @Override
    public Object getRecipeOutput(Player player, ItemStack input, List<ItemStack> comps) {
        if (input == null) {
            return null;
        }
        ItemStack out = input.copy();
        int cl = EnumInfusionEnchantment.getInfusionEnchantmentLevel(out, enchantment);
        if (cl >= enchantment.maxLevel) {
            return null;
        }
        List<EnumInfusionEnchantment> el = EnumInfusionEnchantment.getInfusionEnchantments(input);
        Random rand = new Random(System.nanoTime());
        if (net.minecraft.util.RandomSource.create().nextInt(10) < el.size()) {
            int base = 1;
            if (!input.isEmpty()) {
                base += input.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getByteOr("TC.WARP", (byte)0);
            }
            { net.minecraft.nbt.CompoundTag _t = out.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag(); _t.putByte("TC.WARP", (byte)base); out.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(_t)); }
        }
        EnumInfusionEnchantment.addInfusionEnchantment(out, enchantment, cl + 1);
        return out;
    }
    
    @Override
    public AspectList getAspects(Player player, ItemStack input, List<ItemStack> comps) {
        AspectList out = new AspectList();
        if (input == null || input.isEmpty()) {
            return out;
        }
        int cl = EnumInfusionEnchantment.getInfusionEnchantmentLevel(input, enchantment) + 1;
        if (cl > enchantment.maxLevel) {
            return out;
        }
        List<EnumInfusionEnchantment> el = EnumInfusionEnchantment.getInfusionEnchantments(input);
        int otherEnchantments = el.size();
        if (el.contains(enchantment)) {
            --otherEnchantments;
        }
        float modifier = cl + otherEnchantments * 0.33f;
        for (Aspect a : getAspects().getAspects()) {
            out.add(a, (int)(getAspects().getAmount(a) * modifier));
        }
        return out;
    }
}
