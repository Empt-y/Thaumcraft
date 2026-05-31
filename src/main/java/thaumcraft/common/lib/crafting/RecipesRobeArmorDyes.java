package thaumcraft.common.lib.crafting;

import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import net.minecraft.core.NonNullList;
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
import thaumcraft.common.items.armor.ItemRobeArmor;

public class RecipesRobeArmorDyes implements CraftingRecipe {

    public static final MapCodec<RecipesRobeArmorDyes> MAP_CODEC = MapCodec.unit(new RecipesRobeArmorDyes());
    public static final StreamCodec<RegistryFriendlyByteBuf, RecipesRobeArmorDyes> STREAM_CODEC =
            StreamCodec.unit(new RecipesRobeArmorDyes());
    public static final RecipeSerializer<RecipesRobeArmorDyes> SERIALIZER =
            new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    @Override
    public boolean matches(CraftingInput inv, Level level) {
        ItemStack armor = ItemStack.EMPTY;
        ArrayList<ItemStack> dyes = new ArrayList<>();
        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof ItemRobeArmor) {
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
        ItemRobeArmor robeItem = null;
        int[] rgb = new int[3];
        int count = 0;
        int maxBrightness = 0;

        for (int i = 0; i < inv.size(); ++i) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;
            if (stack.getItem() instanceof ItemRobeArmor ra) {
                if (!armor.isEmpty()) return ItemStack.EMPTY;
                robeItem = ra;
                armor = stack.copy();
                armor.setCount(1);
                if (ra.hasColor(stack)) {
                    int existing = ra.getColor(stack);
                    float r = ((existing >> 16) & 0xFF) / 255.0f;
                    float g = ((existing >> 8) & 0xFF) / 255.0f;
                    float b = (existing & 0xFF) / 255.0f;
                    maxBrightness += (int)(Math.max(r, Math.max(g, b)) * 255.0f);
                    rgb[0] += (int)(r * 255.0f);
                    rgb[1] += (int)(g * 255.0f);
                    rgb[2] += (int)(b * 255.0f);
                    count++;
                }
            } else if (stack.getItem() instanceof DyeItem dyeItem) {
                net.minecraft.world.item.DyeColor dyeColor = stack.get(net.minecraft.core.component.DataComponents.DYE);
                if (dyeColor == null) continue;
                int dyeRgb = dyeColor.getFireworkColor();
                int r = (dyeRgb >> 16) & 0xFF;
                int g = (dyeRgb >> 8) & 0xFF;
                int b = dyeRgb & 0xFF;
                maxBrightness += Math.max(r, Math.max(g, b));
                rgb[0] += r;
                rgb[1] += g;
                rgb[2] += b;
                count++;
            }
        }
        if (robeItem == null || count == 0) return ItemStack.EMPTY;
        int r = rgb[0] / count;
        int g = rgb[1] / count;
        int b = rgb[2] / count;
        float brightness = maxBrightness / (float)count;
        float maxChannel = Math.max(r, Math.max(g, b));
        r = (int)(r * brightness / maxChannel);
        g = (int)(g * brightness / maxChannel);
        b = (int)(b * brightness / maxChannel);
        robeItem.setColor(armor, (r << 16) | (g << 8) | b);
        return armor;
    }

    @Override
    public PlacementInfo placementInfo() {
        return PlacementInfo.NOT_PLACEABLE;
    }



    @Override
    public CraftingBookCategory category() { return CraftingBookCategory.EQUIPMENT; }

    @Override
    public RecipeBookCategory recipeBookCategory() { return RecipeBookCategories.CRAFTING_EQUIPMENT; }

    @Override
    public boolean showNotification() { return false; }

    @Override
    public String group() { return ""; }

    @Override
    public RecipeSerializer<RecipesRobeArmorDyes> getSerializer() { return SERIALIZER; }
}
