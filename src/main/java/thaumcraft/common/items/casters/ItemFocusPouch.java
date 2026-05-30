package thaumcraft.common.items.casters;
// baubles import removed
// baubles import removed
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import thaumcraft.Thaumcraft;
import thaumcraft.common.items.ItemTCBase;
import net.minecraft.world.InteractionResult;


public class ItemFocusPouch extends ItemTCBase 
{
    public ItemFocusPouch() {
        super("focus_pouch");
        /* setMaxDamage removed - use Item.Properties */;
    }
    
    public boolean getShareTag() {
        return true;
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.UNCOMMON;
    }
    
    public boolean hasEffect(ItemStack stack1) {
        return false;
    }
    
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand hand) {
        if (!worldIn.isClientSide()) {
            /* TODO: port to NetworkHooks.openScreen */ 
        }
        return super.use(worldIn, playerIn, hand);
    }
    
    public NonNullList<ItemStack> getInventory(ItemStack item) {
        NonNullList<ItemStack> stackList = NonNullList.withSize(18, ItemStack.EMPTY);
        if (!item.isEmpty()) {
            net.minecraft.world.ContainerHelper.loadAllItems(item.get(), stackList);
        }
        return stackList;
    }
    
    public void setInventory(ItemStack item, NonNullList<ItemStack> stackList) {
        if (item.get() == null) {
            item.put(new CompoundTag());
        }
        net.minecraft.world.ContainerHelper.saveAllItems(item.get(), stackList);
    }
    
    public Object /* BaubleType removed */ getBaubleType(ItemStack itemstack) {
        return null /* nested removed */;
    }
    
    public void onWornTick(ItemStack itemstack, LivingEntity player) {
    }
    
    public void onEquipped(ItemStack itemstack, LivingEntity player) {
    }
    
    public void onUnequipped(ItemStack itemstack, LivingEntity player) {
    }
    
    public boolean canEquip(ItemStack itemstack, LivingEntity player) {
        return true;
    }
    
    public boolean canUnequip(ItemStack itemstack, LivingEntity player) {
        return true;
    }
}
