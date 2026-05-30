package thaumcraft.common.tiles.devices;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.config.ConfigAspects;
import thaumcraft.common.container.slot.SlotPotion;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;


public class TilePotionSprayer extends TileThaumcraftInventory implements IAspectContainer, IEssentiaTransport
{
    public AspectList recipe;
    public AspectList recipeProgress;
    public int charges;
    public int color;
    int counter;
    boolean activated;
    int venting;
    Aspect currentSuction;

    public TilePotionSprayer(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(1);
        recipe = new AspectList();
        recipeProgress = new AspectList();
        charges = 0;
        color = 0;
        counter = 0;
        activated = false;
        venting = 0;
        currentSuction = null;
    }

    @Override
    public void update() {
        super.update();
        ++counter;
        if (level == null) return;
        if (!getLevel().isClientSide()) {
            if (counter % 5 == 0) {
                currentSuction = null;
                if (getItem(0).isEmpty() || charges >= 8) return;
                boolean done = true;
                for (Aspect aspect : recipe.getAspectsSortedByName()) {
                    if (recipeProgress.getAmount(aspect) < recipe.getAmount(aspect)) {
                        currentSuction = aspect;
                        done = false;
                        break;
                    }
                }
                if (done) {
                    recipeProgress = new AspectList();
                    ++charges;
                    syncTile(false);
                    setChanged();
                } else if (currentSuction != null) {
                    fill();
                }
            }
            // potion spray logic stubbed - PotionUtils removed in MC 26
        }
    }

    @Override
    public void readSyncNBT(CompoundTag nbt) {
        (recipe = new AspectList()).loadAdditional(nbt, "recipe");
        (recipeProgress = new AspectList()).loadAdditional(nbt, "progress");
        charges = nbt.getIntOr("charges", 0);
        color = nbt.getIntOr("color", 0);
    }

    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbt) {
        recipe.saveAdditional(nbt, "recipe");
        recipeProgress.saveAdditional(nbt, "progress");
        nbt.putInt("charges", charges);
        nbt.putInt("color", color);
        return nbt;
    }

    @Override
    public boolean isItemValidForSlot(int par1, ItemStack stack) {
        return stack != null && !stack.isEmpty() && SlotPotion.isValidPotion(stack);
    }

    @Override
    public void setItem(int par1, ItemStack stack) {
        super.setItem(par1, stack);
        recalcAspects();
    }

    @Override
    public ItemStack removeItem(int par1, int par2) {
        ItemStack stack = super.removeItem(par1, par2);
        recalcAspects();
        return stack;
    }

    private void recalcAspects() {
        if (level == null || getLevel().isClientSide()) return;
        ItemStack stack = getItem(0);
        color = 3355443;
        if (stack.isEmpty()) {
            recipe = new AspectList();
        } else {
            recipe = ConfigAspects.getPotionAspects(stack);
            color = 3355443; // getPotionColor stubbed
        }
        charges = 0;
        recipe = AspectHelper.cullTags(recipe, 10);
        recipeProgress = new AspectList();
        syncTile(false);
        setChanged();
    }

    void fill() {
        Direction facing = BlockStateUtils.getFacing(getLevel().getBlockState(worldPosition));
        for (int y = 0; y <= 1; ++y) {
            for (Direction dir : Direction.values()) {
                if (dir != facing) {
                    BlockEntity te = ThaumcraftApiHelper.getConnectableTile(level, worldPosition.above(y), dir);
                    if (te instanceof IEssentiaTransport ic) {
                        if (ic.getEssentiaAmount(dir.getOpposite()) > 0
                            && ic.getSuctionAmount(dir.getOpposite()) < getSuctionAmount(null)
                            && getSuctionAmount(null) >= ic.getMinimumSuction()) {
                            int ess = ic.takeEssentia(currentSuction, 1, dir.getOpposite());
                            if (ess > 0) {
                                addToContainer(currentSuction, ess);
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public int addToContainer(Aspect tt, int am) {
        int ce = recipe.getAmount(tt) - recipeProgress.getAmount(tt);
        if (ce <= 0) return am;
        int add = Math.min(ce, am);
        recipeProgress.add(tt, add);
        syncTile(false);
        setChanged();
        return am - add;
    }

    @Override public boolean takeFromContainer(Aspect tt, int am) { return false; }
    @Override public boolean takeFromContainer(AspectList ot) { return false; }
    @Override public boolean doesContainerContain(AspectList ot) { return false; }
    @Override public boolean doesContainerContainAmount(Aspect tt, int am) { return recipeProgress.getAmount(tt) >= am; }
    @Override public int containerContains(Aspect tt) { return recipeProgress.getAmount(tt); }
    @Override public boolean doesContainerAccept(Aspect tag) { return true; }

    @Override
    public boolean isConnectable(Direction face) {
        return face != BlockStateUtils.getFacing(level != null ? getLevel().getBlockState(worldPosition) : null);
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return face != BlockStateUtils.getFacing(level != null ? getLevel().getBlockState(worldPosition) : null);
    }

    @Override public boolean canOutputTo(Direction face) { return false; }
    @Override public void setSuction(Aspect aspect, int amount) { currentSuction = aspect; }
    @Override public Aspect getSuctionType(Direction loc) { return currentSuction; }
    @Override public int getSuctionAmount(Direction loc) { return (currentSuction != null) ? 128 : 0; }
    @Override public Aspect getEssentiaType(Direction loc) { return null; }
    @Override public int getEssentiaAmount(Direction loc) { return 0; }
    @Override public int takeEssentia(Aspect aspect, int amount, Direction face) { return (canOutputTo(face) && takeFromContainer(aspect, amount)) ? amount : 0; }
    @Override public int addEssentia(Aspect aspect, int amount, Direction face) { return canInputFrom(face) ? (amount - addToContainer(aspect, amount)) : 0; }
    @Override public int getMinimumSuction() { return 0; }
    @Override public AspectList getAspects() { return recipeProgress; }
    @Override public void setAspects(AspectList aspects) { recipeProgress = aspects; }
}
