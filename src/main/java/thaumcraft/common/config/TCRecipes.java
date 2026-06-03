package thaumcraft.common.config;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thaumcraft.Thaumcraft;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.crafting.ShapelessArcaneRecipe;

public class TCRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, Thaumcraft.MODID);

    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, Thaumcraft.MODID);

    // ── Crucible ─────────────────────────────────────────────────────────────
    public static final DeferredHolder<RecipeType<?>, RecipeType<CrucibleRecipe>> CRUCIBLE_TYPE =
            TYPES.register("crucible", () -> RecipeType.simple(Identifier.fromNamespaceAndPath(Thaumcraft.MODID, "crucible")));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CrucibleRecipe>> CRUCIBLE_SERIALIZER =
            SERIALIZERS.register("crucible", () -> new RecipeSerializer<>(
                    MapCodec.unit(CrucibleRecipe.EMPTY),
                    StreamCodec.unit(CrucibleRecipe.EMPTY)
            ));

    // ── Shaped Arcane Workbench ───────────────────────────────────────────────
    public static final DeferredHolder<RecipeType<?>, RecipeType<ShapedArcaneRecipe>> SHAPED_ARCANE_TYPE =
            TYPES.register("shaped_arcane", () -> RecipeType.simple(Identifier.fromNamespaceAndPath(Thaumcraft.MODID, "shaped_arcane")));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapedArcaneRecipe>> SHAPED_ARCANE_SERIALIZER =
            SERIALIZERS.register("shaped_arcane", () -> new RecipeSerializer<>(
                    MapCodec.unit(ShapedArcaneRecipe.EMPTY),
                    StreamCodec.unit(ShapedArcaneRecipe.EMPTY)
            ));

    // ── Shapeless Arcane Workbench ────────────────────────────────────────────
    public static final DeferredHolder<RecipeType<?>, RecipeType<ShapelessArcaneRecipe>> SHAPELESS_ARCANE_TYPE =
            TYPES.register("shapeless_arcane", () -> RecipeType.simple(Identifier.fromNamespaceAndPath(Thaumcraft.MODID, "shapeless_arcane")));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ShapelessArcaneRecipe>> SHAPELESS_ARCANE_SERIALIZER =
            SERIALIZERS.register("shapeless_arcane", () -> new RecipeSerializer<>(
                    MapCodec.unit(ShapelessArcaneRecipe.EMPTY),
                    StreamCodec.unit(ShapelessArcaneRecipe.EMPTY)
            ));

    // ── Infusion ──────────────────────────────────────────────────────────────
    public static final DeferredHolder<RecipeType<?>, RecipeType<InfusionRecipe>> INFUSION_TYPE =
            TYPES.register("infusion", () -> RecipeType.simple(Identifier.fromNamespaceAndPath(Thaumcraft.MODID, "infusion")));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<InfusionRecipe>> INFUSION_SERIALIZER =
            SERIALIZERS.register("infusion", () -> new RecipeSerializer<>(
                    MapCodec.unit(InfusionRecipe.EMPTY),
                    StreamCodec.unit(InfusionRecipe.EMPTY)
            ));

    public static void register(IEventBus bus) {
        SERIALIZERS.register(bus);
        TYPES.register(bus);
    }
}
