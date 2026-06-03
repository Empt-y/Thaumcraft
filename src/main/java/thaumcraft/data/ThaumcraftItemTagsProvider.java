package thaumcraft.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.blocks.BlocksTC;

import java.util.concurrent.CompletableFuture;

public class ThaumcraftItemTagsProvider extends ItemTagsProvider {

    public static final TagKey<Item> ORE_AMBER        = forgeTag("ores/amber");
    public static final TagKey<Item> ORE_CINNABAR     = forgeTag("ores/cinnabar");
    public static final TagKey<Item> ORE_QUARTZ       = forgeTag("ores/quartz");
    public static final TagKey<Item> GEM_AMBER        = forgeTag("gems/amber");
    public static final TagKey<Item> QUICKSILVER      = forgeTag("quicksilver");
    public static final TagKey<Item> ORE_CRYSTAL_AIR     = forgeTag("ores/crystal_air");
    public static final TagKey<Item> ORE_CRYSTAL_EARTH   = forgeTag("ores/crystal_earth");
    public static final TagKey<Item> ORE_CRYSTAL_WATER   = forgeTag("ores/crystal_water");
    public static final TagKey<Item> ORE_CRYSTAL_FIRE    = forgeTag("ores/crystal_fire");
    public static final TagKey<Item> ORE_CRYSTAL_ORDER   = forgeTag("ores/crystal_order");
    public static final TagKey<Item> ORE_CRYSTAL_ENTROPY = forgeTag("ores/crystal_entropy");
    public static final TagKey<Item> ORE_CRYSTAL_TAINT   = forgeTag("ores/crystal_taint");
    public static final TagKey<Item> INGOT_THAUMIUM  = forgeTag("ingots/thaumium");
    public static final TagKey<Item> INGOT_VOID      = forgeTag("ingots/void");
    public static final TagKey<Item> INGOT_BRASS     = forgeTag("ingots/brass");
    public static final TagKey<Item> NUGGET_THAUMIUM     = forgeTag("nuggets/thaumium");
    public static final TagKey<Item> NUGGET_VOID         = forgeTag("nuggets/void");
    public static final TagKey<Item> NUGGET_BRASS        = forgeTag("nuggets/brass");
    public static final TagKey<Item> NUGGET_QUICKSILVER  = forgeTag("nuggets/quicksilver");
    public static final TagKey<Item> PLATE_IRON      = forgeTag("plates/iron");
    public static final TagKey<Item> PLATE_BRASS     = forgeTag("plates/brass");
    public static final TagKey<Item> PLATE_THAUMIUM  = forgeTag("plates/thaumium");
    public static final TagKey<Item> PLATE_VOID      = forgeTag("plates/void");

    public ThaumcraftItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, "thaumcraft");
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        if (BlocksTC.oreAmber    != null) this.tag(ORE_AMBER).add(BlocksTC.oreAmber.asItem());
        if (BlocksTC.oreCinnabar != null) this.tag(ORE_CINNABAR).add(BlocksTC.oreCinnabar.asItem());
        if (ItemsTC.amber        != null) this.tag(GEM_AMBER).add(ItemsTC.amber);
        if (ItemsTC.quicksilver  != null) this.tag(QUICKSILVER).add(ItemsTC.quicksilver);

        if (BlocksTC.crystalAir     != null) this.tag(ORE_CRYSTAL_AIR).add(BlocksTC.crystalAir.asItem());
        if (BlocksTC.crystalEarth   != null) this.tag(ORE_CRYSTAL_EARTH).add(BlocksTC.crystalEarth.asItem());
        if (BlocksTC.crystalWater   != null) this.tag(ORE_CRYSTAL_WATER).add(BlocksTC.crystalWater.asItem());
        if (BlocksTC.crystalFire    != null) this.tag(ORE_CRYSTAL_FIRE).add(BlocksTC.crystalFire.asItem());
        if (BlocksTC.crystalOrder   != null) this.tag(ORE_CRYSTAL_ORDER).add(BlocksTC.crystalOrder.asItem());
        if (BlocksTC.crystalEntropy != null) this.tag(ORE_CRYSTAL_ENTROPY).add(BlocksTC.crystalEntropy.asItem());
        if (BlocksTC.crystalTaint   != null) this.tag(ORE_CRYSTAL_TAINT).add(BlocksTC.crystalTaint.asItem());

        if (BlocksTC.logGreatwood  != null) this.tag(ItemTags.LOGS_THAT_BURN).add(BlocksTC.logGreatwood.asItem());
        if (BlocksTC.logSilverwood != null) this.tag(ItemTags.LOGS_THAT_BURN).add(BlocksTC.logSilverwood.asItem());

        BlocksTC.nitor.values().forEach(block -> this.tag(forgeTag("nitor")).add(block.asItem()));
    }

    private static TagKey<Item> forgeTag(String path) {
        return TagKey.create(net.minecraft.core.registries.Registries.ITEM, Identifier.fromNamespaceAndPath("c", path));
    }
}
