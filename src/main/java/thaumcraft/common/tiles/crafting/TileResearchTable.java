package thaumcraft.common.tiles.crafting;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.items.IScribeTools;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftManager;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;


public class TileResearchTable extends TileThaumcraftInventory
{
    public ResearchTableData data;
    
    public TileResearchTable(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(2);
        data = null;
        syncedSlots = new int[] { 0, 1 };
    }
    
    @Override
    public void readSyncNBT(CompoundTag nbttagcompound) {
        super.readSyncNBT(nbttagcompound);
        if (nbttagcompound.contains("note")) {
            (data = new ResearchTableData(this)).deserialize(nbttagcompound.getCompoundOrEmpty("note"));
        }
        else {
            data = null;
        }
    }
    
    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbttagcompound) {
        if (data != null) {
            nbttagcompound.put("note", data.serialize());
        }
        else {
            nbttagcompound.remove("note");
        }
        return super.writeSyncNBT(nbttagcompound);
    }
    
    public void startNewTheory(Player player, Set<String> mutators) {
        (data = new ResearchTableData(player, this)).initialize(player, mutators);
        syncTile(false);
        setChanged();
    }
    
    public void finishTheory(Player player) {
        Comparator<Map.Entry<String, Integer>> valueComparator = (e1, e2) -> e2.getValue().compareTo(e1.getValue());
        Map<String, Integer> sortedMap = data.categoryTotals.entrySet().stream().sorted(valueComparator).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        int i = 0;
        for (String cat : sortedMap.keySet()) {
            int tot = Math.round(sortedMap.get(cat) / 100.0f * IPlayerKnowledge.EnumKnowledgeType.THEORY.getProgression());
            if (i > data.penaltyStart) {
                tot = (int)Math.max(1.0, tot * 0.666666667);
            }
            ResearchCategory rc = ResearchCategories.getResearchCategory(cat);
            ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, rc, tot);
            ++i;
        }
        data = null;
    }
    
    public Set<String> checkSurroundingAids() {
        HashMap<String, ITheorycraftAid> mutators = new HashMap<String, ITheorycraftAid>();
        for (int y = -1; y <= 1; ++y) {
            for (int x = -4; x <= 4; ++x) {
                for (int z = -4; z <= 4; ++z) {
                    for (String muk : TheorycraftManager.aids.keySet()) {
                        ITheorycraftAid mu = TheorycraftManager.aids.get(muk);
                        BlockState state = getLevel().getBlockState(getBlockPos().offset(x, y, z));
                        if (mu.getAidObject() instanceof Block) {
                            if (state.getBlock() != mu.getAidObject()) {
                                continue;
                            }
                            mutators.put(muk, mu);
                        }
                        else {
                            if (!(mu.getAidObject() instanceof ItemStack aidStack)) {
                                continue;
                            }
                            ItemStack is = new ItemStack(state.getBlock().asItem());
                            if (is.isEmpty() || !is.is(aidStack.getItem())) {
                                continue;
                            }
                            mutators.put(muk, mu);
                        }
                    }
                }
            }
        }
        List<Entity> l = EntityUtils.getEntitiesInRange(level, getBlockPos(), null, Entity.class, 5.0);
        if (l != null && !l.isEmpty()) {
            for (Entity e : l) {
                for (String muk : TheorycraftManager.aids.keySet()) {
                    ITheorycraftAid mu = TheorycraftManager.aids.get(muk);
                    if (mu.getAidObject() instanceof Class && e.getClass().isAssignableFrom((Class<?>)mu.getAidObject())) {
                        mutators.put(muk, mu);
                    }
                }
            }
        }
        return mutators.keySet();
    }
    
    public boolean consumeInkFromTable() {
        if (getStackInSlot(0).getItem() instanceof IScribeTools && getStackInSlot(0).getDamageValue() < getStackInSlot(0).getMaxDamage()) {
            getStackInSlot(0).setDamageValue(getStackInSlot(0).getDamageValue() + 1);
            syncTile(false);
            setChanged();
            return true;
        }
        return false;
    }
    
    public boolean consumepaperFromTable() {
        if (getStackInSlot(1).getItem() == Items.PAPER && getStackInSlot(1).getCount() > 0) {
            decrStackSize(1, 1);
            syncTile(false);
            setChanged();
            return true;
        }
        return false;
    }
    
    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        switch (i) {
            case 0: {
                if (itemstack.getItem() instanceof IScribeTools) {
                    return true;
                }
                break;
            }
            case 1: {
                if (itemstack.getItem() == Items.PAPER && itemstack.getDamageValue() == 0) {
                    return true;
                }
                break;
            }
        }
        return false;
    }
    
    @Override
    public void handleUpdateTag(net.minecraft.nbt.CompoundTag tag, net.minecraft.core.HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
        if (level != null && getLevel().isClientSide()) {
            syncTile(false);
        }
    }

    @Override
    public boolean triggerEvent(int i, int j) {
        if (i == 1) {
            if (level != null && getLevel().isClientSide()) {
                getLevel().playSound(null, getBlockPos(), SoundsTC.learn, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            return true;
        }
        return super.triggerEvent(i, j);
    }
}
