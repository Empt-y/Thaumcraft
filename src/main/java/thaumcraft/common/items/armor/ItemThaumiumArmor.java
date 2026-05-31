package thaumcraft.common.items.armor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
// import net.minecraft.world.item.Item /* ArmorItem removed */; // removed
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemThaumiumArmor extends net.minecraft.world.item.Item implements IThaumcraftItems
{
    public ItemThaumiumArmor(String name, Object /* nested class removed */ enumarmormaterial, int j, EquipmentSlot k) {
        super(thaumcraft.common.config.TCItemInit.take());
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
    }
    
    public Item getItem() {
        return this;
    }
    
    public String[] getVariantNames() {
        return new String[] { "normal" };
    }
    
    public int[] getVariantMeta() {
        return new int[] { 0 };
    }
    
    public Object /* ModelResourceLocation removed */ getCustomModelResourceLocation(String variant) {
        return null /* removed */;
    }
    
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (stack.getItem() == ItemsTC.thaumiumHelm || stack.getItem() == ItemsTC.thaumiumChest || stack.getItem() == ItemsTC.thaumiumBoots) {
            return "thaumcraft:textures/entity/0 /* armor field removed *//thaumium_1.png";
        }
        if (stack.getItem() == ItemsTC.thaumiumLegs) {
            return "thaumcraft:textures/entity/0 /* armor field removed *//thaumium_2.png";
        }
        return "thaumcraft:textures/entity/0 /* armor field removed *//thaumium_1.png";
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.UNCOMMON;
    }
    
    public boolean isValidRepairItem(ItemStack repairItem) {
        return ItemStack.isSameItem(repairItem, new ItemStack(ItemsTC.ingots, 1)) || false;
    }
}
