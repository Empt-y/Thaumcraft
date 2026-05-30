package thaumcraft.common.lib.crafting;
// baubles import removed
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.player.Player;
// import net.minecraft.world.item.Item /* ArmorItem removed */; // removed
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.events.PlayerEvents;


public class InfusionRunicAugmentRecipe extends InfusionRecipe
{
    public InfusionRunicAugmentRecipe() {
        super("RUNICSHIELDING", null, 0, null, Ingredient.of(net.minecraft.world.item.Items.AIR), "gemAmber", ItemsTC.salisMundus);
    }
    
    public InfusionRunicAugmentRecipe(ItemStack in) {
        super("RUNICSHIELDING", null, 0, null, in, new ItemStack(ItemsTC.salisMundus), "gemAmber");
        int fc = PlayerEvents.getRunicCharge(in);
        if (fc > 0) {
            components.clear();
            ArrayList<ItemStack> com = new ArrayList<ItemStack>();
            components.add(Ingredient.of(ItemsTC.salisMundus));
            components.add(ThaumcraftApiHelper.getIngredient("gemAmber"));
            int c = 0;
            while (c < fc) {
                ++c;
                components.add(ThaumcraftApiHelper.getIngredient("gemAmber"));
            }
        }
    }
    
    @Override
    public boolean matches(List<ItemStack> input, ItemStack central, Level world, Player player) {
        if (!ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(research)) return false;
        if (getRecipeInput() != null && getRecipeInput().items().findAny().isPresent() && !getRecipeInput().test(central)) return false;
        List<ItemStack> comps = new java.util.ArrayList<>(input);
        for (Ingredient comp : getComponents(central)) {
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
        int base = PlayerEvents.getRunicCharge(input) + 1;
        { net.minecraft.nbt.CompoundTag _t = out.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag(); _t.putByte("TC.RUNIC", (byte)base); out.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(_t)); }
        return out;
    }
    
    @Override
    public AspectList getAspects(Player player, ItemStack input, List<ItemStack> comps) {
        AspectList out = new AspectList();
        int vis = 20 + (int)(20.0 * Math.pow(2.0, PlayerEvents.getRunicCharge(input)));
        if (vis > 0) {
            out.add(Aspect.PROTECT, vis);
            out.add(Aspect.CRYSTAL, vis / 2);
            out.add(Aspect.ENERGY, vis / 2);
        }
        return out;
    }
    
    @Override
    public int getInstability(Player player, ItemStack input, List<ItemStack> comps) {
        int i = 5 + PlayerEvents.getRunicCharge(input) / 2;
        return i;
    }
    
    public NonNullList<Ingredient> getComponents(ItemStack input) {
        NonNullList<Ingredient> com = NonNullList.create();
        com.add(Ingredient.of(ItemsTC.salisMundus));
        com.add(ThaumcraftApiHelper.getIngredient("gemAmber"));
        int fc = PlayerEvents.getRunicCharge(input);
        if (fc > 0) {
            for (int c = 0; c < fc; ++c) {
                com.add(ThaumcraftApiHelper.getIngredient("gemAmber"));
            }
        }
        return com;
    }
}
