package thaumcraft.common.lib.crafting;

import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import thaumcraft.common.items.armor.ItemVoidRobeArmor;

public class RecipesVoidRobeArmorDyes implements CraftingRecipe {

    public static final MapCodec<RecipesVoidRobeArmorDyes> MAP_CODEC = MapCodec.unit(new RecipesVoidRobeArmorDyes());
    public static final StreamCodec<RegistryFriendlyByteBuf, RecipesVoidRobeArmorDyes> STREAM_CODEC =
            StreamCodec.unit(new RecipesVoidRobeArmorDyes());
    public static final RecipeSerializer<RecipesVoidRobeArmorDyes> SERIALIZER =
            new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public boolean matches(CraftingInput inv, Level level) {
        ItemStack armor = ItemStack.EMPTY;
        ArrayList<ItemStack> dyes = new ArrayList<>();
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof ItemVoidRobeArmor) {
                if (!armor.isEmpty()) return false;
                armor = stack;
            } else if (stack.getItem() instanceof DyeItem) {
                dyes.add(stack);
            } else {
                return false;
            }
        }
        return !armor.isEmpty() && !dyes.isEmpty();
    }

    @Override
    public ItemStack assemble(CraftingInput inv) {
        ItemStack armor = ItemStack.EMPTY;
        ItemVoidRobeArmor robeItem = null;
        int[] rgb = new int[3];
        int count = 0, maxBrightness = 0;
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof ItemVoidRobeArmor vra) {
                if (!armor.isEmpty()) return ItemStack.EMPTY;
                robeItem = vra;
                armor = stack.copy();
                armor.setCount(1);
                if (vra.hasColor(stack)) {
                    int ex = vra.getColor(stack);
                    float r = ((ex >> 16) & 0xFF) / 255f, g = ((ex >> 8) & 0xFF) / 255f, b = (ex & 0xFF) / 255f;
                    maxBrightness += (int)(Math.max(r, Math.max(g, b)) * 255f);
                    rgb[0] += (int)(r * 255f); rgb[1] += (int)(g * 255f); rgb[2] += (int)(b * 255f); count++;
                }
            } else if (stack.getItem() instanceof DyeItem dyeItem) {
                net.minecraft.world.item.DyeColor dyeColor = stack.get(net.minecraft.core.component.DataComponents.DYE);
                if (dyeColor == null) continue;
                int drgb = dyeColor.getFireworkColor();
                int r = (drgb >> 16) & 0xFF, g = (drgb >> 8) & 0xFF, b = drgb & 0xFF;
                maxBrightness += Math.max(r, Math.max(g, b));
                rgb[0] += r; rgb[1] += g; rgb[2] += b; count++;
            }
        }
        if (robeItem == null || count == 0) return ItemStack.EMPTY;
        int r = rgb[0]/count, g = rgb[1]/count, b = rgb[2]/count;
        float brightness = maxBrightness / (float)count, max = Math.max(r, Math.max(g, b));
        r = (int)(r * brightness / max); g = (int)(g * brightness / max); b = (int)(b * brightness / max);
        robeItem.setColor(armor, (r << 16) | (g << 8) | b);
        return armor;
    }

    @Override public PlacementInfo placementInfo() { return PlacementInfo.NOT_PLACEABLE; }
    @Override public CraftingBookCategory category() { return CraftingBookCategory.EQUIPMENT; }
    @Override public RecipeBookCategory recipeBookCategory() { return RecipeBookCategories.CRAFTING_EQUIPMENT; }
    @Override public boolean showNotification() { return false; }
    @Override public String group() { return ""; }
    @Override public RecipeSerializer<RecipesVoidRobeArmorDyes> getSerializer() { return SERIALIZER; }
}
