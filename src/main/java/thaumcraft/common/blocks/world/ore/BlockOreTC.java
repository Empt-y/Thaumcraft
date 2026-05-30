package thaumcraft.common.blocks.world.ore;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.BlockTC;

public class BlockOreTC extends BlockTC
{
    public BlockOreTC(String name) {
        super(null /*  null   Material removed    */, name);
        setResistance(5.0f);
        setSoundType(SoundType.STONE);
    }

    public Item getItemDropped(BlockState state) {
        return (state.getBlock() == BlocksTC.oreQuartz) ? Items.QUARTZ
             : (state.getBlock() == BlocksTC.oreAmber) ? ItemsTC.amber
             : state.getBlock().asItem();
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = super.getDrops(state, params);
        if (this == BlocksTC.oreAmber && drops != null) {
            RandomSource rand = RandomSource.create();
            for (int a = 0; a < drops.size(); ++a) {
                ItemStack is = drops.get(a);
                if (!is.isEmpty() && is.getItem() == ItemsTC.amber && rand.nextFloat() < 0.066f) {
                    drops.set(a, new ItemStack(ItemsTC.curio, 1));
                }
            }
        }
        return drops;
    }

    @Override
    public int getExpDrop(BlockState state, LevelAccessor level, BlockPos pos,
            @Nullable BlockEntity blockEntity, @Nullable Entity breaker, ItemStack tool) {
        if (getItemDropped(state) != state.getBlock().asItem()
                && (this == BlocksTC.oreAmber || this == BlocksTC.oreQuartz)
                && level instanceof ServerLevel sl) {
            int xp = Mth.randomBetweenInclusive(level.getRandom(), 1, 4);
            return EnchantmentHelper.processBlockExperience(sl, tool, xp);
        }
        return 0;
    }
}
