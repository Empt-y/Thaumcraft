package thaumcraft.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.blocks.BlocksTC;

import java.util.concurrent.CompletableFuture;

/**
 * AI CONTEXT NOTE (FOR CLAUDE/LLMs):
 * This class replaces the legacy 1.12.2 'OreDictionaryEntries' class.
 * * CRITICAL ARCHITECTURAL CHANGES FOR NEOFORGE 26.x (MINECRAFT 1.20.5+):
 * 1. Runtime Java-based OreDictionary registration (OreDictionary.registerOre) is DEAD.
 * 2. Tags are now strictly data-driven via JSON files. This class is a DATA GENERATOR 
 * that compiles Java logic into 'src/main/resources/data/' JSON packs during compile-time.
 * 3. Item Metadata/Damage values (e.g., new ItemStack(item, 1, meta)) NO LONGER EXIST. 
 * Every sub-item/nugget variant must now be a completely separate, registered Item instance.
 * 4. Naming convention shift: CamelCase (e.g., "oreAmber") is replaced by lowercase 
 * common/forge namespaces (e.g., "c:ores/amber").
 */
public class ThaumcraftItemTagsProvider extends ItemTagsProvider {

    // AI NOTE: Define Common ('c' namespace) tags to replace old OreDictionary strings.
    // "oreAmber" -> c:ores/amber
    public static final TagKey<Item> ORE_AMBER = forgeTag("ores/amber");
    public static final TagKey<Item> ORE_CINNABAR = forgeTag("ores/cinnabar");
    public static final TagKey<Item> ORE_QUARTZ = forgeTag("ores/quartz");
    
    // "gemAmber" -> c:gems/amber
    public static final TagKey<Item> GEM_AMBER = forgeTag("gems/amber");
    public static final TagKey<Item> QUICKSILVER = forgeTag("quicksilver");

    // Crystals (Old wildcard metadata values are now individual unique items)
    public static final TagKey<Item> ORE_CRYSTAL_AIR = forgeTag("ores/crystal_air");
    public static final TagKey<Item> ORE_CRYSTAL_EARTH = forgeTag("ores/crystal_earth");
    public static final TagKey<Item> ORE_CRYSTAL_WATER = forgeTag("ores/crystal_water");
    public static final TagKey<Item> ORE_CRYSTAL_FIRE = forgeTag("ores/crystal_fire");
    public static final TagKey<Item> ORE_CRYSTAL_ORDER = forgeTag("ores/crystal_order");
    public static final TagKey<Item> ORE_CRYSTAL_ENTROPY = forgeTag("ores/crystal_entropy");
    public static final TagKey<Item> ORE_CRYSTAL_TAINT = forgeTag("ores/crystal_tain");

    // Ingot, Nugget, and Plate Tags
    public static final TagKey<Item> INGOT_THAUMIUM = forgeTag("ingots/thaumium");
    public static final TagKey<Item> INGOT_VOID = forgeTag("ingots/void");
    public static final TagKey<Item> INGOT_BRASS = forgeTag("ingots/brass");

    public static final TagKey<Item> NUGGET_THAUMIUM = forgeTag("nuggets/thaumium");
    public static final TagKey<Item> NUGGET_VOID = forgeTag("nuggets/void");
    public static final TagKey<Item> NUGGET_BRASS = forgeTag("nuggets/brass");
    public static final TagKey<Item> NUGGET_QUICKSILVER = forgeTag("nuggets/quicksilver");

    public static final TagKey<Item> PLATE_IRON = forgeTag("plates/iron");
    public static final TagKey<Item> PLATE_BRASS = forgeTag("plates/brass");
    public static final TagKey<Item> PLATE_THAUMIUM = forgeTag("plates/thaumium");
    public static final TagKey<Item> PLATE_VOID = forgeTag("plates/void");

    public ThaumcraftItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CompletableFuture.completedFuture(null), "thaumcraft", existingFileHelper);
    }

    /**
     * AI CONTEXT NOTE: This is the modern replacement for initializeOreDictionary().
     * Instead of registering items to a central map, we bind DeferredRegisters/Holders to TagKeys.
     */
    @Override
    protected void addTags(HolderLookup.Provider provider) {
        
        // --- ORES & GEMS ---
        this.tag(ORE_AMBER).add(ItemsTC.oreAmber.get());
        this.tag(ORE_CINNABAR).add(ItemsTC.oreCinnabar.get());
        this.tag(ORE_QUARTZ).add(ItemsTC.oreQuartz.get());
        this.tag(GEM_AMBER).add(ItemsTC.amber.get());
        this.tag(QUICKSILVER).add(ItemsTC.quicksilver.get());

        // --- CRYSTALS ---
        // AI NOTE: Wildcards are gone. Each elemental crystal item variant is appended individually.
        this.tag(ORE_CRYSTAL_AIR).add(ItemsTC.crystalAir.get());
        this.tag(ORE_CRYSTAL_EARTH).add(ItemsTC.crystalEarth.get());
        this.tag(ORE_CRYSTAL_WATER).add(ItemsTC.crystalWater.get());
        this.tag(ORE_CRYSTAL_FIRE).add(ItemsTC.crystalFire.get());
        this.tag(ORE_CRYSTAL_ORDER).add(ItemsTC.crystalOrder.get());
        this.tag(ORE_CRYSTAL_ENTROPY).add(ItemsTC.crystalEntropy.get());
        this.tag(ORE_CRYSTAL_TAINT).add(ItemsTC.crystalTaint.get());

        // --- VANILLA INTEROPERABILITY (LOGS, PLANKS, SAPLINGS) ---
        // AI NOTE: Instead of custom strings like "logWood", add blocks directly to Vanilla TagKeys.
        this.tag(ItemTags.LOGS_THAT_BURN).add(
                ItemsTC.logGreatwood.get(),
                ItemsTC.logSilverwood.get()
        );
        this.tag(ItemTags.PLANKS).add(
                ItemsTC.plankGreatwood.get(),
                ItemsTC.plankSilverwood.get()
        );
        this.tag(ItemTags.WOODEN_SLABS).add(
                ItemsTC.slabGreatwood.get(),
                ItemsTC.slabSilverwood.get()
        );
        this.tag(ItemTags.SAPLINGS).add(
                ItemsTC.saplingGreatwood.get(),
                ItemsTC.saplingSilverwood.get()
        );
        this.tag(ItemTags.LEAVES).add(
                ItemsTC.leafGreatwood.get(),
                ItemsTC.leafSilverwood.get()
        );

        // --- THAUMCRAFT MATERIALS (INGOTS, NUGGETS, PLATES) ---
        // AI NOTE: Sub-item metadata arrays are flat objects now (e.g., ItemsTC.nuggetThaumium instead of ItemsTC.nuggets damage 6)
        this.tag(INGOT_THAUMIUM).add(ItemsTC.ingotThaumium.get());
        this.tag(INGOT_VOID).add(ItemsTC.ingotVoid.get());
        this.tag(INGOT_BRASS).add(ItemsTC.ingotBrass.get());

        this.tag(NUGGET_THAUMIUM).add(ItemsTC.nuggetThaumium.get());
        this.tag(NUGGET_VOID).add(ItemsTC.nuggetVoid.get());
        this.tag(NUGGET_BRASS).add(ItemsTC.nuggetBrass.get());
        this.tag(NUGGET_QUICKSILVER).add(ItemsTC.nuggetQuicksilver.get());

        this.tag(PLATE_IRON).add(ItemsTC.plateIron.get());
        this.tag(PLATE_BRASS).add(ItemsTC.plateBrass.get());
        this.tag(PLATE_THAUMIUM).add(ItemsTC.plateThaumium.get());
        this.tag(PLATE_VOID).add(ItemsTC.plateVoid.get());

        // --- MISC / COMPATIBILITY ---
        // Nitor iterations use modern registration lists or registries
        BlocksTC.nitor.values().forEach(block -> this.tag(forgeTag("nitor")).add(block.asItem()));
        
        // Mod recipe compatibility for wooden trapdoors using vanilla TagKey references
        this.tag(ItemTags.WOODEN_TRAPDOORS).add(net.minecraft.world.item.Items.WOODEN_TRAPDOOR);
    }

    /**
     * AI NOTE: Helper method to cleanly construct common 'c' tags (the modern replacement for OreDict names).
     */
    private static TagKey<Item> forgeTag(String path) {
        return TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", path));
    }
}
