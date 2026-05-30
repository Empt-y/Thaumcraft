package thaumcraft.common.items.armor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
// import net.minecraft.world.item.Item /* ArmorItem removed */; // removed
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemCultistBoots extends net.minecraft.world.item.Item implements IWarpingGear, IVisDiscountGear, IThaumcraftItems
{
    public ItemCultistBoots() {
        super(new net.minecraft.world.item.Item.Properties());
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
    
    @OnlyIn(Dist.CLIENT)
    public Object /* ItemMeshDefinition removed */ getCustomMesh() {
        return null;
    }
    
    @OnlyIn(Dist.CLIENT)
    public Object /* ModelResourceLocation removed */ getCustomModelResourceLocation(String variant) {
        return null /* removed */;
    }
    
    @OnlyIn(Dist.CLIENT)
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "thaumcraft:textures/entity/0 /* armor field removed *//cultistboots.png";
    }
    
    public boolean isValidRepairItem(ItemStack repairItem) {
        return ItemStack.isSameItem(repairItem, new ItemStack(Items.IRON_INGOT)) || false;
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.UNCOMMON;
    }
    
    public int getWarp(ItemStack itemstack, Player player) {
        return 1;
    }
    
    public int getVisDiscount(ItemStack stack, Player player) {
        return 1;
    }
}
