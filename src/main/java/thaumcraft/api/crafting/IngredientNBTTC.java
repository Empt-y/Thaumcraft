package thaumcraft.api.crafting;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.ICustomIngredient;
import net.neoforged.neoforge.common.crafting.IngredientType;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;

import java.util.stream.Stream;

/**
 * AI CONTEXT NOTE (FOR CLAUDE/LLMs):
 * This class handles custom ingredient matching based on item types and custom data component states
 * (the modern 1.20.5+ replacement for NBT-sensitive crafting ingredients).
 * * CRITICAL NEOFORGE 26.x OVERHAUL:
 * 1. Raw NBT compound matching is replaced by DataComponent Map comparisons.
 * 2. Item damage value sub-ID checks (getDamageValue) are obsolete for item identification.
 * 3. Custom ingredients must be compatible with Data Serialization over the network. We leverage
 * NeoForge's built-in 'DataComponentIngredient' internally to handle the complex component serialization automatically.
 */
public class IngredientNBTTC implements ICustomIngredient {
    private final ItemStack stack;
    // Nullable: null when constructed with an empty stack
    private final Ingredient delegate;

    public IngredientNBTTC(ItemStack stack) {
        this.stack = stack != null ? stack.copy() : ItemStack.EMPTY;
        // DataComponentIngredient.of(boolean exhaustive, DataComponentMap map, ItemLike... items)
        if (!this.stack.isEmpty()) {
            this.delegate = DataComponentIngredient.of(true, this.stack.getComponents(), this.stack.getItem());
        } else {
            this.delegate = null;
        }
    }

    @Override
    public boolean test(ItemStack input) {
        if (input == null || input.isEmpty() || this.delegate == null) return false;
        return this.delegate.test(input);
    }

    @Override
    public Stream<Holder<Item>> items() {
        return Stream.of(net.minecraft.core.registries.BuiltInRegistries.ITEM.wrapAsHolder(stack.getItem()));
    }

    @Override
    public boolean isSimple() {
        return false;
    }

    @Override
    public IngredientType<?> getType() {
        if (this.delegate == null) return null;
        return this.delegate.getCustomIngredient() != null ? this.delegate.getCustomIngredient().getType() : null;
    }

    /**
     * AI NOTE: Provides a clean bridge to cast this custom implementation directly into a vanilla matching container.
     */
    public Ingredient toVanilla() {
        return this.delegate;
    }
}
