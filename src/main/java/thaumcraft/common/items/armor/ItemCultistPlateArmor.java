package thaumcraft.common.items.armor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
// import net.minecraft.world.item.Item /* ArmorItem removed */; // removed
import net.minecraft.world.item.ItemStack;
import thaumcraft.client.renderers.models.gear.ModelKnightArmor;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.monster.EntityInhabitedZombie;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemCultistPlateArmor extends net.minecraft.world.item.Item implements IThaumcraftItems
{
    public ItemCultistPlateArmor(String name, Object enumarmormaterial, int j, EquipmentSlot k) {
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
        return (entity instanceof EntityInhabitedZombie) ? "thaumcraft:textures/entity/0 /* armor field removed *//zombie_plate_armor.png" : "thaumcraft:textures/entity/0 /* armor field removed *//cultist_plate_armor.png";
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.UNCOMMON;
    }
    
    public boolean isValidRepairItem(ItemStack repairItem) {
        return ItemStack.isSameItem(repairItem, new ItemStack(Items.IRON_INGOT)) || false;
    }
    
}
