package thaumcraft.common.blocks.basic;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.internal.WeightedRandomLoot;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.config.ModConfig;


public class BlockStonePorous extends BlockTC
{
    static Random r;
    static ArrayList<WeightedRandomLoot> pdrops;

    public BlockStonePorous() {
        super(null /*  null   Material removed    */, "stone_porous");
        setHardness(1.0f);
        setResistance(5.0f);
        setSoundType(SoundType.STONE);
    }

    public List<ItemStack> getDrops(BlockGetter world, BlockPos pos, BlockState state, int fortune) {
        List<ItemStack> ret = new ArrayList<ItemStack>();
        int rr = BlockStonePorous.r.nextInt(15) + fortune;
        if (rr > 13) {
            if (BlockStonePorous.pdrops == null || BlockStonePorous.pdrops.size() <= 0) {
                createDrops();
            }
            ItemStack s = pickRandom(BlockStonePorous.r, BlockStonePorous.pdrops).item.copy();
            ret.add(s);
        } else {
            ret.add(new ItemStack(Blocks.GRAVEL));
        }
        return ret;
    }

    private static WeightedRandomLoot pickRandom(Random rand, List<WeightedRandomLoot> pool) {
        int total = 0;
        for (WeightedRandomLoot w : pool) total += w.itemWeight;
        int roll = rand.nextInt(total);
        for (WeightedRandomLoot w : pool) {
            roll -= w.itemWeight;
            if (roll < 0) return w;
        }
        return pool.get(pool.size() - 1);
    }

    private void createDrops() {
        BlockStonePorous.pdrops = new ArrayList<WeightedRandomLoot>();
        for (Aspect aspect : Aspect.getCompoundAspects()) {
            ItemStack is = new ItemStack(ItemsTC.crystalEssence);
            ((ItemGenericEssentiaContainer)ItemsTC.crystalEssence).setAspects(is, new AspectList().add(aspect, (aspect == Aspect.FLUX) ? 100 : (aspect.isPrimal() ? 20 : 1)));
            BlockStonePorous.pdrops.add(new WeightedRandomLoot(is, 1));
        }
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.amber), 20));
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.clusters, 1), 20));
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.clusters, 1), 10));
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.clusters, 1), 10));
        if (ModConfig.foundCopperIngot) {
            BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.clusters, 1), 10));
        }
        if (ModConfig.foundTinIngot) {
            BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.clusters, 1), 10));
        }
        if (ModConfig.foundSilverIngot) {
            BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.clusters, 1), 8));
        }
        if (ModConfig.foundLeadIngot) {
            BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(ItemsTC.clusters, 1), 10));
        }
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(Items.DIAMOND), 2));
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(Items.EMERALD), 4));
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(Items.REDSTONE), 8));
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(Items.PRISMARINE_CRYSTALS), 3));
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(Items.PRISMARINE_SHARD), 3));
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(Items.CLAY_BALL), 30));
        BlockStonePorous.pdrops.add(new WeightedRandomLoot(new ItemStack(Items.QUARTZ), 15));
    }

    static {
        BlockStonePorous.r = new Random(System.currentTimeMillis());
        BlockStonePorous.pdrops = null;
    }
}
