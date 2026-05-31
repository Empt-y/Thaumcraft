package thaumcraft.common.tiles.crafting;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.casters.FocusModSplit;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.items.casters.ItemFocus;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;
import thaumcraft.common.world.aura.AuraHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import thaumcraft.common.container.ContainerFocalManipulator;
import thaumcraft.common.container.TCMenuTypes;


public class TileFocalManipulator extends TileThaumcraftInventory implements MenuProvider{
    public float vis;
    public HashMap<Integer, FocusElementNode> data;
    public String focusName;
    int ticks;
    public boolean doGather;
    public float visCost;
    public int xpCost;
    private AspectList crystals;
    public AspectList crystalsSync;
    public boolean doGuiReset;

    public TileFocalManipulator(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state, 1);
        vis = 0.0f;
        data = new HashMap<Integer, FocusElementNode>();
        focusName = "";
        ticks = 0;
        visCost = 0.0f;
        xpCost = 0;
        crystals = new AspectList();
        crystalsSync = new AspectList();
        doGuiReset = false;
        syncedSlots = new int[] { 0 };
    }

    @Override
    public void readSyncNBT(CompoundTag nbt) {
        super.readSyncNBT(nbt);
        vis = nbt.getFloatOr("vis", 0.0f);
        focusName = nbt.getStringOr("focusName", "");
        (crystalsSync = new AspectList()).loadAdditional(nbt, "crystals");
        ListTag nodelist = nbt.getListOrEmpty("nodes");
        data.clear();
        for (int x = 0; x < nodelist.size(); ++x) {
            CompoundTag nodenbt = nodelist.getCompoundOrEmpty(x);
            FocusElementNode node = new FocusElementNode();
            node.deserialize(nodenbt);
            data.put(node.id, node);
        }
    }

    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbt) {
        super.writeSyncNBT(nbt);
        nbt.putFloat("vis", vis);
        nbt.putString("focusName", focusName);
        crystalsSync.saveAdditional(nbt, "crystals");
        ListTag nodelist = new ListTag();
        for (FocusElementNode node : data.values()) {
            nodelist.add(node.serialize());
        }
        nbt.put("nodes", nodelist);
        return nbt;
    }

    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                        worldPosition.getX() + 1, worldPosition.getY() + 1, worldPosition.getZ() + 1);
    }

    @Override
    public void setItem(int par1, ItemStack stack) {
        ItemStack prev = getItem(par1);
        super.setItem(par1, stack);
        if (stack.isEmpty() || !ItemStack.isSameItemSameComponents(stack, prev)) {
            if (level != null && level.isClientSide()) {
                data.clear();
                doGuiReset = true;
            } else {
                vis = 0.0f;
                crystalsSync = new AspectList();
                setChanged();
                syncSlots(null);
            }
        }
    }

    public float spendAura(float vis) {
        if (level != null && level.getBlockState(worldPosition.above()).getBlock() == BlocksTC.arcaneWorkbenchCharger) {
            float q = vis;
            float z = vis / 9.0f;
        Label_0110:
            for (int xx = -1; xx <= 1; ++xx) {
                for (int zz = -1; zz <= 1; ++zz) {
                    if (z > q) {
                        z = q;
                    }
                    q -= AuraHandler.drainVis(level, worldPosition.offset(xx * 16, 0, zz * 16), z, false);
                    if (q <= 0.0f) {
                        break Label_0110;
                    }
                }
            }
            return vis - q;
        }
        return AuraHandler.drainVis(level, worldPosition, vis, false);
    }

    @Override
    public void update() {
        super.update();
        if (level == null) return;
        boolean complete = false;
        ++ticks;
        if (!level.isClientSide()) {
            if (ticks % 20 == 0) {
                if (vis > 0.0f && (getItem(0).isEmpty() || !(getItem(0).getItem() instanceof ItemFocus))) {
                    complete = true;
                    vis = 0.0f;
                    level.playSound(null, worldPosition, SoundsTC.wandfail, SoundSource.BLOCKS, 0.33f, 1.0f);
                }
                if (!complete && vis > 0.0f) {
                    float amt = spendAura(Math.min(20.0f, vis));
                    if (amt > 0.0f) {
                        level.blockEvent(worldPosition, getBlockState().getBlock(), 5, 1);
                        vis -= amt;
                        syncTile(false);
                        setChanged();
                    }
                    if (vis <= 0.0f && !getItem(0).isEmpty() && getItem(0).getItem() instanceof ItemFocus) {
                        complete = true;
                        endCraft();
                    }
                }
            }
        } else if (vis > 0.0f) {
            FXDispatcher.INSTANCE.drawGenericParticles(
                worldPosition.getX() + 0.5 + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.3f,
                worldPosition.getY() + 1.4 + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.3f,
                worldPosition.getZ() + 0.5 + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.3f,
                0.0, 0.0, 0.0,
                0.5f + level.getRandom().nextFloat() * 0.4f,
                1.0f - level.getRandom().nextFloat() * 0.4f,
                1.0f - level.getRandom().nextFloat() * 0.4f,
                0.8f, false, 448, 9, 1,
                6 + level.getRandom().nextInt(5), 0,
                0.3f + level.getRandom().nextFloat() * 0.3f, 0.0f, 0);
        }
        if (complete) {
            vis = 0.0f;
            syncTile(false);
            setChanged();
        }
    }

    private FocusPackage generateFocus() {
        if (data != null && !data.isEmpty()) {
            FocusPackage core = new FocusPackage();
            int totalComplexity = 0;
            HashMap<String, Integer> compCount = new HashMap<String, Integer>();
            for (FocusElementNode node : data.values()) {
                if (node.node != null) {
                    int a = 0;
                    if (compCount.containsKey(node.node.getKey())) {
                        a = compCount.get(node.node.getKey());
                    }
                    ++a;
                    node.complexityMultiplier = 0.5f * (a + 1);
                    compCount.put(node.node.getKey(), a);
                    totalComplexity += (int)(node.node.getComplexity() * node.complexityMultiplier);
                }
            }
            core.setComplexity(totalComplexity);
            FocusElementNode root = data.get(0);
            traverseChildren(core, root);
            return core;
        }
        return null;
    }

    private void traverseChildren(FocusPackage currentPackage, FocusElementNode currentNode) {
        if (currentPackage == null || currentNode == null) {
            return;
        }
        currentPackage.addNode(currentNode.node);
        if (currentNode.children == null || currentNode.children.length == 0) {
            return;
        }
        if (currentNode.children.length == 1) {
            traverseChildren(currentPackage, data.get(currentNode.children[0]));
        } else {
            FocusModSplit splitNode = (FocusModSplit)currentNode.node;
            splitNode.getSplitPackages().clear();
            for (int c : currentNode.children) {
                FocusPackage splitPackage = new FocusPackage();
                traverseChildren(splitPackage, data.get(c));
                splitNode.getSplitPackages().add(splitPackage);
            }
        }
    }

    public void endCraft() {
        vis = 0.0f;
        if (!getItem(0).isEmpty() && getItem(0).getItem() instanceof ItemFocus) {
            FocusPackage core = generateFocus();
            if (core != null && level != null) {
                level.playSound(null, worldPosition, SoundsTC.wand, SoundSource.BLOCKS, 1.0f, 1.0f);
                ItemStack focus = getItem(0);
                if (!focusName.isEmpty()) {
                    focus.set(net.minecraft.core.component.DataComponents.CUSTOM_NAME,
                              net.minecraft.network.chat.Component.literal(focusName));
                }
                ItemFocus.setPackage(focus, core);
                setItem(0, focus);
                crystalsSync = new AspectList();
                data.clear();
                syncTile(false);
                setChanged();
            }
        }
    }

    public boolean startCraft(int id, Player p) {
        if (data == null || data.isEmpty() || vis > 0.0f || getItem(0).isEmpty() || !(getItem(0).getItem() instanceof ItemFocus)) {
            return false;
        }
        int maxComplexity = ((ItemFocus) getItem(0).getItem()).getMaxComplexity();
        int totalComplexity = 0;
        crystals = new AspectList();
        HashMap<String, Integer> compCount = new HashMap<String, Integer>();
        for (FocusElementNode node : data.values()) {
            if (node.node == null) {
                return false;
            }
            if (!ThaumcraftCapabilities.knowsResearchStrict(p, node.node.getResearch())) {
                return false;
            }
            int a = 0;
            if (compCount.containsKey(node.node.getKey())) {
                a = compCount.get(node.node.getKey());
            }
            ++a;
            node.complexityMultiplier = 0.5f * (a + 1);
            compCount.put(node.node.getKey(), a);
            totalComplexity += (int)(node.node.getComplexity() * node.complexityMultiplier);
            if (node.node.getAspect() == null) {
                continue;
            }
            crystals.add(node.node.getAspect(), 1);
        }
        vis = (float)(totalComplexity * 10 + maxComplexity / 5);
        xpCost = (int)Math.max(1L, Math.round(Math.sqrt(totalComplexity)));
        if (!p.getAbilities().instabuild && p.experienceLevel < xpCost) {
            vis = 0.0f;
            return false;
        }
        if (!p.getAbilities().instabuild) {
            p.giveExperienceLevels(-xpCost);
        }
        if (crystals.getAspects().length > 0) {
            ItemStack[] components = new ItemStack[crystals.getAspects().length];
            int r = 0;
            for (Aspect as : crystals.getAspects()) {
                components[r] = ThaumcraftApiHelper.makeCrystal(as, crystals.getAmount(as));
                ++r;
            }
            if (components.length >= 0) {
                for (int a = 0; a < components.length; ++a) {
                    if (!InventoryUtils.isPlayerCarryingAmount(p, components[a], false)) {
                        vis = 0.0f;
                        return false;
                    }
                }
                for (int a = 0; a < components.length; ++a) {
                    InventoryUtils.consumePlayerItem(p, components[a], true, false);
                }
                crystalsSync = crystals.copy();
            }
            setChanged();
            syncTile(false);
            if (level != null) {
                level.playSound(null, worldPosition, SoundsTC.craftstart, SoundSource.BLOCKS, 1.0f, 1.0f);
            }
            return true;
        }
        vis = 0.0f;
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int par1, ItemStack stack) {
        return stack.getItem() instanceof ItemFocus;
    }

    @Override
    public boolean triggerEvent(int i, int j) {
        if (i == 1) {
            doGuiReset = true;
        }
        if (i == 5) {
            if (level != null && level.isClientSide()) {
                FXDispatcher.INSTANCE.visSparkle(
                    worldPosition.getX() + level.getRandom().nextInt(3) - level.getRandom().nextInt(3),
                    worldPosition.getY() + level.getRandom().nextInt(3),
                    worldPosition.getZ() + level.getRandom().nextInt(3) - level.getRandom().nextInt(3),
                    worldPosition.getX(), worldPosition.getY() + 1, worldPosition.getZ(), j);
            }
            return true;
        }
        return super.triggerEvent(i, j);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.focalmanipulator");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ContainerFocalManipulator(TCMenuTypes.FOCAL_MANIPULATOR.get(), id, inv, this);
    }
}
