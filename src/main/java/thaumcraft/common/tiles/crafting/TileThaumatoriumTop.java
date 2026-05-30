package thaumcraft.common.tiles.crafting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
// ITickable removed - use BlockEntityTicker<T>
import net.minecraft.network.chat.Component;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileThaumatoriumTop extends TileThaumcraft implements IAspectContainer, IEssentiaTransport, WorldlyContainer
{
    public TileThaumatorium thaumatorium;

    @Override
    public int getContainerSize() { return thaumatorium != null ? thaumatorium.getContainerSize() : 0; }
    @Override
    public net.minecraft.world.item.ItemStack removeItem(int slot, int count) { return thaumatorium != null ? thaumatorium.removeItem(slot, count) : net.minecraft.world.item.ItemStack.EMPTY; }
    @Override
    public net.minecraft.world.item.ItemStack removeItemNoUpdate(int slot) { return thaumatorium != null ? thaumatorium.removeItemNoUpdate(slot) : net.minecraft.world.item.ItemStack.EMPTY; }
    @Override
    public void setItem(int slot, net.minecraft.world.item.ItemStack stack) { if (thaumatorium != null) thaumatorium.setItem(slot, stack); }
    
    public TileThaumatoriumTop(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        thaumatorium = null;
    }
    
    public void update() {
        if (thaumatorium == null) {
            BlockEntity tile = getLevel().getBlockEntity(getBlockPos().below());
            if (tile != null && tile instanceof TileThaumatorium) {
                thaumatorium = (TileThaumatorium)tile;
            }
        }
    }
    
    @Override
    public int addToContainer(Aspect tt, int am) {
        if (thaumatorium == null) {
            return am;
        }
        return thaumatorium.addToContainer(tt, am);
    }
    
    @Override
    public boolean takeFromContainer(Aspect tt, int am) {
        return thaumatorium != null && thaumatorium.takeFromContainer(tt, am);
    }
    
    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContain(AspectList ot) {
        return false;
    }
    
    @Override
    public boolean doesContainerContainAmount(Aspect tt, int am) {
        return thaumatorium != null && thaumatorium.doesContainerContainAmount(tt, am);
    }
    
    @Override
    public int containerContains(Aspect tt) {
        if (thaumatorium == null) {
            return 0;
        }
        return thaumatorium.containerContains(tt);
    }
    
    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }
    
    @Override
    public boolean isConnectable(Direction face) {
        return thaumatorium != null && thaumatorium.isConnectable(face);
    }
    
    @Override
    public boolean canInputFrom(Direction face) {
        return thaumatorium != null && thaumatorium.canInputFrom(face);
    }
    
    @Override
    public boolean canOutputTo(Direction face) {
        return false;
    }
    
    @Override
    public void setSuction(Aspect aspect, int amount) {
        if (thaumatorium == null) {
            return;
        }
        thaumatorium.setSuction(aspect, amount);
    }
    
    @Override
    public Aspect getSuctionType(Direction loc) {
        if (thaumatorium == null) {
            return null;
        }
        return thaumatorium.getSuctionType(loc);
    }
    
    @Override
    public int getSuctionAmount(Direction loc) {
        if (thaumatorium == null) {
            return 0;
        }
        return thaumatorium.getSuctionAmount(loc);
    }
    
    @Override
    public Aspect getEssentiaType(Direction loc) {
        return null;
    }
    
    @Override
    public int getEssentiaAmount(Direction loc) {
        return 0;
    }
    
    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction face) {
        if (thaumatorium == null) {
            return 0;
        }
        return thaumatorium.takeEssentia(aspect, amount, face);
    }
    
    @Override
    public int addEssentia(Aspect aspect, int amount, Direction face) {
        if (thaumatorium == null) {
            return 0;
        }
        return thaumatorium.addEssentia(aspect, amount, face);
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    @Override
    public AspectList getAspects() {
        if (thaumatorium == null) {
            return null;
        }
        return thaumatorium.essentia;
    }
    
    @Override
    public void setAspects(AspectList aspects) {
        if (thaumatorium == null) {
            return;
        }
        thaumatorium.setAspects(aspects);
    }
    
    public int getSizeInventory() {
        return 1;
    }
    
    public ItemStack getStackInSlot(int par1) {
        if (thaumatorium == null) {
            return ItemStack.EMPTY;
        }
        return thaumatorium.getItem(par1);
    }
    
    public ItemStack decrStackSize(int par1, int par2) {
        if (thaumatorium == null) {
            return ItemStack.EMPTY;
        }
        return thaumatorium.decrStackSize(par1, par2);
    }
    
    public ItemStack removeStackFromSlot(int par1) {
        if (thaumatorium == null) {
            return ItemStack.EMPTY;
        }
        return thaumatorium.removeStackFromSlot(par1);
    }
    
    public void setInventorySlotContents(int par1, ItemStack stack2) {
        if (thaumatorium == null) {
            return;
        }
        thaumatorium.setItem(par1, stack2);
    }
    
    public int getInventoryStackLimit() {
        return 64;
    }
    
    public boolean isUsableByPlayer(Player par1Player) {
        return getLevel().getBlockEntity(getBlockPos()) == this && par1Player.distanceToSqr(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5) <= 64.0;
    }
    
    public boolean isItemValidForSlot(int par1, ItemStack stack2) {
        return true;
    }
    
    public int[] getSlotsForFace(Direction side) {
        return new int[] { 0 };
    }
    
    public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
        return true;
    }
    
    public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
        return true;
    }
    
    public void openInventory(Player player) {
    }
    
    public void closeInventory(Player player) {
    }
    
    public int getField(int id) {
        return 0;
    }
    
    public void setField(int id, int value) {
    }
    
    public int getFieldCount() {
        return 0;
    }
    
    @Override
    public void clearContent() {
        thaumatorium.clearContent();
    }

    @Override
    public boolean stillValid(net.minecraft.world.entity.player.Player player) {
        return true;
    }
    
    public String getName() {
        return null;
    }
    
    public boolean hasCustomName() {
        return false;
    }
    
    public Component getDisplayName() {
        return null;
    }
    
    public boolean isEmpty() {
        return thaumatorium.isEmpty();
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, net.minecraft.world.item.ItemStack itemStack, @javax.annotation.Nullable net.minecraft.core.Direction direction) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, net.minecraft.world.item.ItemStack itemStack, net.minecraft.core.Direction direction) {
        return false;
    }
}
