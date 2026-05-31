package thaumcraft.common.tiles.essentia;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.container.ContainerSmelter;
import thaumcraft.common.container.TCMenuTypes;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.TileThaumcraftInventory;
import thaumcraft.common.tiles.devices.TileBellows;


public class TileSmelter extends TileThaumcraftInventory implements MenuProvider
{
    private static final int[] slots_bottom = new int[] { 1 };
    private static final int[] slots_top = new int[0];
    private static final int[] slots_sides = new int[] { 0 };
    public AspectList aspects;
    public int vis;
    private int maxVis;
    public int smeltTime;
    boolean speedBoost;
    public int furnaceBurnTime;
    public int currentItemBurnTime;
    public int furnaceCookTime;
    int count;
    int bellows;

    public TileSmelter(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state, 2);
        aspects = new AspectList();
        maxVis = 256;
        smeltTime = 100;
        speedBoost = false;
        count = 0;
        bellows = -1;
    }

    @Override
    public void readSyncNBT(CompoundTag nbt) {
        furnaceBurnTime = nbt.getShortOr("BurnTime", (short)0);
    }

    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbt) {
        nbt.putShort("BurnTime", (short) furnaceBurnTime);
        return nbt;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        /* super.loadAdditional removed - CompoundTag not compatible with ValueInput */
        speedBoost = input.getBooleanOr("speedBoost", false);
        furnaceCookTime = input.getShortOr("CookTime", (short)0);
        aspects.loadAdditional(input.read("aspects", CompoundTag.CODEC).orElseGet(CompoundTag::new));
        vis = aspects.visSize();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        /* super.saveAdditional removed - CompoundTag not compatible with ValueOutput */
        output.putBoolean("speedBoost", speedBoost);
        output.putShort("CookTime", (short) furnaceCookTime);
        CompoundTag aspectsTag = new CompoundTag();
        aspects.saveAdditional(aspectsTag);
        output.store("aspects", CompoundTag.CODEC, aspectsTag);
    }

    @Override
    public void update() {
        super.update();
        if (level == null) return;
        boolean flag = furnaceBurnTime > 0;
        boolean flag2 = false;
        ++count;
        if (furnaceBurnTime > 0) {
            --furnaceBurnTime;
        }
        if (!level.isClientSide()) {
            if (bellows < 0) {
                checkNeighbours();
            }
            // smelt logic stubbed — getSpeed() and many old API references removed
            if (flag != furnaceBurnTime > 0) {
                flag2 = true;
            }
        }
        if (flag2) {
            setChanged();
        }
    }

    private boolean canSmelt() {
        if (getItem(0).isEmpty()) return false;
        AspectList al = null /* CraftingManager removed */;
        if (al == null || al.size() == 0) return false;
        int vs = al.visSize();
        if (vs > maxVis - vis) return false;
        smeltTime = (int)(vs * 2 * (1.0f - 0.125f * bellows));
        return true;
    }

    public void checkNeighbours() {
        if (level == null) return;
        Direction[] faces = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
        bellows = TileBellows.getBellows(level, worldPosition, faces);
    }

    public void smeltItem() {
        if (canSmelt()) {
            AspectList al = null /* CraftingManager removed */;
            for (Aspect a : al.getAspects()) {
                aspects.add(a, al.getAmount(a));
            }
            vis = aspects.visSize();
            getItem(0).shrink(1);
            if (getItem(0).getCount() <= 0) {
                setItem(0, ItemStack.EMPTY);
            }
        }
    }

    public boolean isItemFuel(ItemStack stack) {
        if (level != null) return level.fuelValues().isFuel(stack);
        return false;
    }

    @Override
    public boolean isItemValidForSlot(int par1, ItemStack stack2) {
        if (par1 == 0) {
            AspectList al = null /* CraftingManager removed */;
            return al != null && al.size() > 0;
        }
        return par1 == 1 && isItemFuel(stack2);
    }

    @Override
    public int[] getSlotsForFace(Direction par1) {
        return (par1 == Direction.DOWN) ? slots_bottom : ((par1 == Direction.UP) ? slots_top : slots_sides);
    }

    @Override
    public boolean canPlaceItemThroughFace(int par1, ItemStack stack2, Direction par3) {
        return par3 != Direction.UP && isItemValidForSlot(par1, stack2);
    }

    @Override
    public boolean canTakeItemThroughFace(int par1, ItemStack stack2, Direction par3) {
        return par3 != Direction.UP || par1 != 1 || stack2.getItem() == Items.BUCKET;
    }

    public boolean takeFromContainer(Aspect tag, int amount) {
        if (aspects != null && aspects.getAmount(tag) >= amount) {
            aspects.remove(tag, amount);
            vis = aspects.visSize();
            setChanged();
            return true;
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public int getCookProgressScaled(int par1) {
        if (smeltTime <= 0) smeltTime = 1;
        return furnaceCookTime * par1 / smeltTime;
    }

    @OnlyIn(Dist.CLIENT)
    public int getVisScaled(int par1) {
        return vis * par1 / maxVis;
    }

    @OnlyIn(Dist.CLIENT)
    public int getBurnTimeRemainingScaled(int par1) {
        if (currentItemBurnTime == 0) currentItemBurnTime = 200;
        return furnaceBurnTime * par1 / currentItemBurnTime;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.smelter");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ContainerSmelter(TCMenuTypes.SMELTER.get(), id, inv, this);
    }
}
