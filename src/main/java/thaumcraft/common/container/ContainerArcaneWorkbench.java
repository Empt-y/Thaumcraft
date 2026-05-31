package thaumcraft.common.container;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.blocks.world.ore.ShardType;
import thaumcraft.common.container.slot.SlotCraftingArcaneWorkbench;
import thaumcraft.common.container.slot.SlotCrystal;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;


public class ContainerArcaneWorkbench extends AbstractContainerMenu
{
    private TileArcaneWorkbench tileEntity;
    private Inventory ip;
    public ResultContainer craftResult;
    public static int[] xx;
    public static int[] yy;
    private int lastVis;
    private long lastCheck;

    public ContainerArcaneWorkbench(int id, Inventory inv, net.minecraft.network.RegistryFriendlyByteBuf buf) {
        this(TCMenuTypes.ARCANE_WORKBENCH.get(), id, inv,
            (TileArcaneWorkbench) inv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public ContainerArcaneWorkbench(net.minecraft.world.inventory.MenuType<ContainerArcaneWorkbench> type, int id, Inventory par1InventoryPlayer, TileArcaneWorkbench e) {
        super(type, id);
        craftResult = new ResultContainer();
        lastVis = -1;
        lastCheck = 0L;
        tileEntity = e;
        tileEntity.inventoryCraft.eventHandler = this;
        ip = par1InventoryPlayer;
        e.getAura();
        addSlot(new SlotCraftingArcaneWorkbench(tileEntity, par1InventoryPlayer.player, tileEntity.inventoryCraft, craftResult, 15, 160, 64));
        for (int var6 = 0; var6 < 3; ++var6) {
            for (int var7 = 0; var7 < 3; ++var7) {
                addSlot(new Slot(tileEntity.inventoryCraft, var7 + var6 * 3, 40 + var7 * 24, 40 + var6 * 24));
            }
        }
        for (ShardType st : ShardType.values()) {
            if (st.ordinal() < 6) {
                addSlot(new SlotCrystal(st.getAspect(), tileEntity.inventoryCraft, st.ordinal() + 9, ContainerArcaneWorkbench.xx[st.ordinal()], ContainerArcaneWorkbench.yy[st.ordinal()]));
            }
        }
        for (int var6 = 0; var6 < 3; ++var6) {
            for (int var7 = 0; var7 < 9; ++var7) {
                addSlot(new Slot(par1InventoryPlayer, var7 + var6 * 9 + 9, 16 + var7 * 18, 151 + var6 * 18));
            }
        }
        for (int var6 = 0; var6 < 9; ++var6) {
            addSlot(new Slot(par1InventoryPlayer, var6, 16 + var6 * 18, 209));
        }
        slotsChanged(tileEntity.inventoryCraft);
    }

    @Override
    public void addSlotListener(ContainerListener par1ICrafting) {
        super.addSlotListener(par1ICrafting);
        tileEntity.getAura();
        par1ICrafting.dataChanged(this, 0, tileEntity.auraVisServer);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        long t = System.currentTimeMillis();
        if (t > lastCheck) {
            lastCheck = t + 500L;
            tileEntity.getAura();
        }
        if (lastVis != tileEntity.auraVisServer) {
            setData(0, tileEntity.auraVisServer);
        }
        lastVis = tileEntity.auraVisServer;
    }

    @Override
    public void setData(int par1, int par2) {
        if (par1 == 0) {
            tileEntity.auraVisClient = par2;
        }
    }

    @Override
    public void slotsChanged(Container par1IInventory) {
        IArcaneRecipe recipe = null /* CraftingManager removed */;
        boolean hasVis = true;
        boolean hasCrystals = true;
        if (recipe != null) {
            int vis = recipe.getVis();
            vis *= (int)(1.0f - CasterManager.getTotalVisDiscount(ip.player));
            AspectList crystals = recipe.getCrystals();
            tileEntity.getAura();
            Level level = tileEntity.getLevel();
            hasVis = (level != null && level.isClientSide() ? (tileEntity.auraVisClient >= vis) : (tileEntity.auraVisServer >= vis));
            if (crystals != null && crystals.size() > 0) {
                for (Aspect aspect : crystals.getAspects()) {
                    if (ThaumcraftInvHelper.countTotalItemsIn(ThaumcraftInvHelper.wrapInventory(tileEntity.inventoryCraft, net.minecraft.core.Direction.UP), ThaumcraftApiHelper.makeCrystal(aspect, crystals.getAmount(aspect)), ThaumcraftInvHelper.InvFilter.STRICT) < crystals.getAmount(aspect)) {
                        hasCrystals = false;
                        break;
                    }
                }
            }
        }
        if (hasVis && hasCrystals) {
            Level level = tileEntity.getLevel();
            if (level != null) {
                slotChangedCraftingGrid(level, ip.player, tileEntity.inventoryCraft, craftResult);
            }
        }
        super.broadcastChanges();
    }

    protected void slotChangedCraftingGrid(Level world, Player player, InventoryArcaneWorkbench craftMat, ResultContainer craftRes) {
        if (!world.isClientSide() && player instanceof ServerPlayer entityplayermp) {
            ItemStack itemstack = ItemStack.EMPTY;
            IArcaneRecipe arecipe = null /* CraftingManager removed */;
            if (arecipe != null && ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(arecipe.getResearch())) {
                itemstack = arecipe.getResultItem();
            }
            craftRes.setItem(0, itemstack);
        }
    }

    @Override
    public void removed(Player par1Player) {
        super.removed(par1Player);
        Level level = tileEntity.getLevel();
        if (level != null && !level.isClientSide()) {
            tileEntity.inventoryCraft.eventHandler = new ContainerDummy();
        }
    }

    @Override
    public boolean stillValid(Player par1Player) {
        Level level = tileEntity.getLevel();
        if (level == null) return false;
        return level.getBlockEntity(tileEntity.getBlockPos()) == tileEntity &&
               par1Player.distanceToSqr(tileEntity.getBlockPos().getCenter()) <= 64.0;
    }

    @Override
    public ItemStack quickMoveStack(Player par1Player, int par1) {
        ItemStack var2 = ItemStack.EMPTY;
        Slot var3 = slots.get(par1);
        if (var3 != null && var3.hasItem()) {
            ItemStack var4 = var3.getItem();
            var2 = var4.copy();
            if (par1 == 0) {
                if (!moveItemStackTo(var4, 16, 52, true)) {
                    return ItemStack.EMPTY;
                }
                var3.onQuickCraft(var4, var2);
            } else if (par1 >= 16 && par1 < 52) {
                for (ShardType st : ShardType.values()) {
                    if (st.ordinal() < 6) {
                        if (SlotCrystal.isValidCrystal(var4, st.getAspect())) {
                            if (!moveItemStackTo(var4, 10 + st.ordinal(), 11 + st.ordinal(), false)) {
                                return ItemStack.EMPTY;
                            }
                            if (var4.getCount() == 0) break;
                        }
                    }
                }
                if (var4.getCount() != 0) {
                    if (par1 < 43) {
                        if (!moveItemStackTo(var4, 43, 52, false)) return ItemStack.EMPTY;
                    } else if (!moveItemStackTo(var4, 16, 43, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!moveItemStackTo(var4, 16, 52, false)) {
                return ItemStack.EMPTY;
            }
            if (var4.getCount() == 0) {
                var3.set(ItemStack.EMPTY);
            } else {
                var3.setChanged();
            }
            if (var4.getCount() == var2.getCount()) {
                return ItemStack.EMPTY;
            }
            var3.onTake(ip.player, var4);
        }
        return var2;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, Slot slot) {
        return slot.container != craftResult && super.canTakeItemForPickAll(stack, slot);
    }

    static {
        ContainerArcaneWorkbench.xx = new int[] { 64, 17, 112, 17, 112, 64 };
        ContainerArcaneWorkbench.yy = new int[] { 13, 35, 35, 93, 93, 115 };
    }

    public TileArcaneWorkbench getTile() { return tileEntity; }
}
