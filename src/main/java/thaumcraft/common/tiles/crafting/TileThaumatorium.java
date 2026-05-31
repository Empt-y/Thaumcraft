package thaumcraft.common.tiles.crafting;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import thaumcraft.common.container.ContainerThaumatorium;
import thaumcraft.common.container.TCMenuTypes;


public class TileThaumatorium extends TileThaumcraftInventory implements IAspectContainer, IEssentiaTransport, MenuProvider{
    public AspectList essentia;
    public ArrayList<Integer> recipeHash;
    public ArrayList<AspectList> recipeEssentia;
    public ArrayList<String> recipePlayer;
    public int currentCraft;
    public int maxRecipes;
    public Aspect currentSuction;
    int venting;
    int counter;
    boolean heated;
    CrucibleRecipe currentRecipe;
    public AbstractContainerMenu eventHandler;
    public ArrayList<CrucibleRecipe> recipes;

    public TileThaumatorium(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state, 1);
        essentia = new AspectList();
        recipeHash = new ArrayList<Integer>();
        recipeEssentia = new ArrayList<AspectList>();
        recipePlayer = new ArrayList<String>();
        currentCraft = -1;
        maxRecipes = 1;
        currentSuction = null;
        venting = 0;
        counter = 0;
        heated = false;
        currentRecipe = null;
        recipes = new ArrayList<CrucibleRecipe>();
    }

    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos().getX() - 0.1, getBlockPos().getY() - 0.1, getBlockPos().getZ() - 0.1,
            getBlockPos().getX() + 1.1, getBlockPos().getY() + 2.1, getBlockPos().getZ() + 1.1);
    }

    @Override
    public void readSyncNBT(CompoundTag nbt) {
        essentia.loadAdditional(nbt);
        maxRecipes = nbt.getByteOr("maxrec", (byte)0);
        recipeEssentia = new ArrayList<AspectList>();
        recipeHash = new ArrayList<Integer>();
        recipePlayer = new ArrayList<String>();
        int[] hashes = nbt.getIntArray("recipes").orElse(new int[0]);
        for (int hash : hashes) {
            CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(hash);
            if (recipe != null) {
                recipeEssentia.add(recipe.getAspects().copy());
                recipePlayer.add("");
                recipeHash.add(hash);
            }
        }
    }

    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbt) {
        nbt.putByte("maxrec", (byte) maxRecipes);
        essentia.saveAdditional(nbt);
        int[] hashes = new int[recipeHash.size()];
        int a = 0;
        for (Integer i : recipeHash) { hashes[a++] = i; }
        nbt.putIntArray("recipes", hashes);
        return nbt;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        /* super.loadAdditional removed - CompoundTag not compatible with ValueInput */
        // recipePlayer is loaded via readSyncNBT
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        /* super.saveAdditional removed - CompoundTag not compatible with ValueOutput */
    }

    boolean checkHeat() {
        if (level == null) return false;
        net.minecraft.world.level.block.Block bi = getLevel().getBlockState(worldPosition.below(2)).getBlock();
        return bi == net.minecraft.world.level.block.Blocks.LAVA
            || bi == net.minecraft.world.level.block.Blocks.MAGMA_BLOCK;
    }

    @Override
    public void update() {
        if (level == null) return;
        if (!getLevel().isClientSide()) {
            if (counter == 0 || counter % 40 == 0) {
                heated = checkHeat();
                getUpgrades();
            }
            ++counter;
            if (heated && !gettingPower() && counter % 5 == 0 && recipeHash != null && recipeHash.size() > 0) {
                if (getItem(0).isEmpty()) {
                    currentSuction = null;
                    return;
                }
                if (currentCraft < 0 || currentCraft >= recipeHash.size() || currentRecipe == null
                    || !currentRecipe.catalystMatches(getItem(0))) {
                    for (int a = 0; a < recipeHash.size(); ++a) {
                        CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(recipeHash.get(a));
                        if (recipe != null && recipe.catalystMatches(getItem(0))) {
                            currentCraft = a;
                            currentRecipe = recipe;
                            break;
                        }
                    }
                }
                if (currentCraft < 0 || currentCraft >= recipeHash.size()) return;
                boolean done = true;
                currentSuction = null;
                for (Aspect aspect : recipeEssentia.get(currentCraft).getAspectsSortedByName()) {
                    if (essentia.getAmount(aspect) < recipeEssentia.get(currentCraft).getAmount(aspect)) {
                        currentSuction = aspect;
                        done = false;
                        break;
                    }
                }
                if (done) {
                    completeRecipe();
                } else if (currentSuction != null) {
                    fill();
                }
            }
        }
    }

    private void completeRecipe() {
        if (currentRecipe != null && currentCraft < recipeHash.size()
            && currentRecipe.matches(essentia, getItem(0))
            && !removeItem(0, 1).isEmpty()) {
            essentia = new AspectList();
            ItemStack dropped = getCurrentOutputRecipe();
            Direction facing = BlockStateUtils.getFacing(getLevel().getBlockState(worldPosition));
            InventoryUtils.ejectStackAt(level, worldPosition, facing, dropped);
            currentCraft = -1;
            syncTile(false);
            setChanged();
        }
    }

    void fill() {
        Direction facing = BlockStateUtils.getFacing(getLevel().getBlockState(worldPosition));
        for (int y = 0; y <= 1; ++y) {
            for (Direction dir : Direction.values()) {
                if (dir != facing && dir != Direction.DOWN) {
                    if (y != 0 || dir != Direction.UP) {
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
    }

    public ItemStack getCurrentOutputRecipe() {
        if (currentCraft >= 0 && recipeHash != null && !recipeHash.isEmpty()) {
            CrucibleRecipe recipe = ThaumcraftApi.getCrucibleRecipeFromHash(recipeHash.get(currentCraft));
            if (recipe != null) return recipe.getResultItem().copy();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public int addToContainer(Aspect tt, int am) {
        if (currentRecipe == null) return am;
        int ce = currentRecipe.getAspects().getAmount(tt) - essentia.getAmount(tt);
        if (ce <= 0) return am;
        int add = Math.min(ce, am);
        essentia.add(tt, add);
        syncTile(false);
        setChanged();
        return am - add;
    }

    @Override
    public boolean takeFromContainer(Aspect tt, int am) {
        if (essentia.getAmount(tt) >= am) {
            essentia.remove(tt, am);
            syncTile(false);
            setChanged();
            return true;
        }
        return false;
    }

    @Override public boolean takeFromContainer(AspectList ot) { return false; }
    @Override public boolean doesContainerContain(AspectList ot) { return false; }
    @Override public boolean doesContainerContainAmount(Aspect tt, int am) { return essentia.getAmount(tt) >= am; }
    @Override public int containerContains(Aspect tt) { return essentia.getAmount(tt); }
    @Override public boolean doesContainerAccept(Aspect tag) { return true; }

    @Override
    public boolean isConnectable(Direction face) {
        return level == null || face != BlockStateUtils.getFacing(getLevel().getBlockState(worldPosition));
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return level == null || face != BlockStateUtils.getFacing(getLevel().getBlockState(worldPosition));
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
    @Override public AspectList getAspects() { return essentia; }
    @Override public void setAspects(AspectList aspects) { essentia = aspects; }

    public void setChanged() {
        setChanged();
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return new int[] { 0 };
    }

    public boolean gettingPower() {
        if (level == null) return false;
        return getLevel().hasNeighborSignal(worldPosition) || getLevel().hasNeighborSignal(worldPosition.below()) || getLevel().hasNeighborSignal(worldPosition.above());
    }

    public void getUpgrades() {
        if (level == null) return;
        // upgrade detection stubbed — withProperty API removed
        int mr = 1;
        if (mr != maxRecipes) {
            maxRecipes = mr;
            while (recipeHash.size() > maxRecipes) {
                recipeHash.remove(recipeHash.size() - 1);
            }
            syncTile(false);
            setChanged();
        }
    }

    public void updateRecipes(Player player) {
        recipes.clear();
        ArrayList<CrucibleRecipe> recipesTemp = new ArrayList<CrucibleRecipe>();
        if (!getItem(0).isEmpty() && recipeHash != null) {
            for (Object r : ThaumcraftApi.getCraftingRecipes().values()) {
                if (r instanceof CrucibleRecipe creps) {
                    if (ThaumcraftCapabilities.knowsResearchStrict(player, creps.getResearch()) && creps.catalystMatches(getItem(0))) {
                        recipesTemp.add(creps);
                    } else if (recipeHash != null && !recipeHash.isEmpty()) {
                        for (Integer hash : recipeHash) {
                            if (creps.hash == hash) {
                                recipesTemp.add(creps);
                                break;
                            }
                        }
                    }
                }
            }
        }
        recipes = recipesTemp.stream().sorted(new RecipeOutputComparator()).collect(Collectors.toCollection(ArrayList::new));
    }

    public ArrayList<Integer> generateRecipeHashlist() {
        ArrayList<Integer> hashList = new ArrayList<Integer>();
        outer: for (int hash : recipeHash) {
            for (CrucibleRecipe cr : recipes) {
                if (cr.hash == hash) continue outer;
            }
            hashList.add(hash);
        }
        for (CrucibleRecipe cr2 : recipes) {
            hashList.add(cr2.hash);
        }
        return hashList;
    }

    private class RecipeOutputComparator implements Comparator<CrucibleRecipe>
    {
        @Override
        public int compare(CrucibleRecipe a, CrucibleRecipe b) {
            if (a.equals(b)) return 0;
            return a.getResultItem().getDisplayName().getString()
                .compareTo(b.getResultItem().getDisplayName().getString());
        }
    }

    @Override
    public net.minecraft.network.chat.Component getDisplayName() {
        return net.minecraft.network.chat.Component.translatable("gui.thaumatorium");
    }

    @Override
    public net.minecraft.world.inventory.AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inv, net.minecraft.world.entity.player.Player player) {
        return new thaumcraft.common.container.ContainerThaumatorium(thaumcraft.common.container.TCMenuTypes.THAUMATORIUM.get(), id, inv, this);
    }
}
