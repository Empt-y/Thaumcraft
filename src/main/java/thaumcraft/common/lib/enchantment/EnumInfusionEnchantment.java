package thaumcraft.common.lib.enchantment;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;


public enum EnumInfusionEnchantment
{
    COLLECTOR(ImmutableSet.of("axe", "pickaxe", "shovel", "weapon"), 1, "INFUSIONRegistries.ENCHANTMENT"),
    DESTRUCTIVE(ImmutableSet.of("axe", "pickaxe", "shovel"), 1, "INFUSIONRegistries.ENCHANTMENT"),
    BURROWING(ImmutableSet.of("axe", "pickaxe"), 1, "INFUSIONRegistries.ENCHANTMENT"),
    SOUNDING(ImmutableSet.of("pickaxe"), 4, "INFUSIONRegistries.ENCHANTMENT"),
    REFINING(ImmutableSet.of("pickaxe"), 4, "INFUSIONRegistries.ENCHANTMENT"),
    ARCING(ImmutableSet.of("weapon"), 4, "INFUSIONRegistries.ENCHANTMENT"),
    ESSENCE(ImmutableSet.of("weapon"), 5, "INFUSIONRegistries.ENCHANTMENT"),
    VISBATTERY(ImmutableSet.of("chargable"), 3, "?"),
    VISCHARGE(ImmutableSet.of("chargable"), 1, "?"),
    SWIFT(ImmutableSet.of("boots"), 4, "IEARMOR"),
    AGILE(ImmutableSet.of("legs"), 1, "IEARMOR"),
    INFESTED(ImmutableSet.of("chest"), 1, "IETAINT"),
    LAMPLIGHT(ImmutableSet.of("axe", "pickaxe", "shovel"), 1, "INFUSIONRegistries.ENCHANTMENT");
    
    public Set<String> toolClasses;
    public int maxLevel;
    public String research;
    
    private EnumInfusionEnchantment(Set<String> toolClasses, int ml, String research) {
        this.toolClasses = toolClasses;
        maxLevel = ml;
        this.research = research;
    }
    
    public static net.minecraft.nbt.ListTag getInfusionEnchantmentTagList(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        net.minecraft.world.item.component.CustomData data = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        if (data == null || data.isEmpty()) return null;
        net.minecraft.nbt.CompoundTag tag = data.copyTag();
        return tag.contains("infench") ? tag.getListOrEmpty("infench") : null;
    }
    
    public static List<EnumInfusionEnchantment> getInfusionEnchantments(ItemStack stack) {
        ListTag nbttaglist = getInfusionEnchantmentTagList(stack);
        List<EnumInfusionEnchantment> list = new ArrayList<EnumInfusionEnchantment>();
        if (nbttaglist != null) {
            for (int j = 0; j < nbttaglist.size(); ++j) {
                int k = nbttaglist.getCompoundOrEmpty(j).getShortOr("id", (short)0);
                int l = nbttaglist.getCompoundOrEmpty(j).getShortOr("lvl", (short)0);
                if (k >= 0 && k < values().length) {
                    list.add(values()[k]);
                }
            }
        }
        return list;
    }
    
    public static int getInfusionEnchantmentLevel(ItemStack stack, EnumInfusionEnchantment enchantment) {
        ListTag nbttaglist = getInfusionEnchantmentTagList(stack);
        List<EnumInfusionEnchantment> list = new ArrayList<EnumInfusionEnchantment>();
        if (nbttaglist != null) {
            for (int j = 0; j < nbttaglist.size(); ++j) {
                int k = nbttaglist.getCompoundOrEmpty(j).getShortOr("id", (short)0);
                int l = nbttaglist.getCompoundOrEmpty(j).getShortOr("lvl", (short)0);
                if (k >= 0 && k < values().length && values()[k] == enchantment) {
                    return l;
                }
            }
        }
        return 0;
    }
    
    public static void addInfusionEnchantment(ItemStack stack, EnumInfusionEnchantment ie, int level) {
        if (stack == null || stack.isEmpty() || level > ie.maxLevel) {
            return;
        }
        ListTag nbttaglist = getInfusionEnchantmentTagList(stack);
        if (nbttaglist != null) {
            int j = 0;
            while (j < nbttaglist.size()) {
                int k = nbttaglist.getCompoundOrEmpty(j).getShortOr("id", (short)0);
                int l = nbttaglist.getCompoundOrEmpty(j).getShortOr("lvl", (short)0);
                if (k == ie.ordinal()) {
                    if (level <= l) {
                        return;
                    }
                    nbttaglist.getCompoundOrEmpty(j).putShort("lvl", (short)level);
                    final ListTag nbtFinal1 = nbttaglist;
                    net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, stack, t -> t.put("infench", nbtFinal1));
                    return;
                }
                else {
                    ++j;
                }
            }
        }
        else {
            nbttaglist = new ListTag();
        }
        CompoundTag nbttagcompound = new CompoundTag();
        nbttagcompound.putShort("id", (short)ie.ordinal());
        nbttagcompound.putShort("lvl", (short)level);
        nbttaglist.add(nbttagcompound);
        if (nbttaglist.size() > 0) {
            final ListTag nbtFinal2 = nbttaglist;
            net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, stack, t -> t.put("infench", nbtFinal2));
        }
    }
}
