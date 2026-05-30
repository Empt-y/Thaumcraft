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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.client.renderers.models.gear.ModelLeaderArmor;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemCultistLeaderArmor extends net.minecraft.world.item.Item implements IThaumcraftItems
{
    HumanoidModel model1;
    HumanoidModel model2;
    HumanoidModel model;
    
    public ItemCultistLeaderArmor(String name, int j, EquipmentSlot k) {
        super(new net.minecraft.world.item.Item.Properties());
        model1 = null;
        model2 = null;
        model = null;
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
    
    public Object /* ModelResourceLocation removed */ getCustomModelResourceLocation(String variant) {
        return null /* removed */;
    }
    
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "thaumcraft:textures/entity/0 /* armor field removed *//cultist_leader_armor.png";
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.RARE;
    }
    
    public boolean isValidRepairItem(ItemStack repairItem) {
        return ItemStack.isSameItem(repairItem, new ItemStack(Items.IRON_INGOT)) || false;
    }
    
    @OnlyIn(Dist.CLIENT)
    public HumanoidModel getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel _default) {
        if (model1 == null) {
            model1 = new ModelLeaderArmor(1.0f);
        }
        if (model2 == null) {
            model2 = new ModelLeaderArmor(0.5f);
        }
        return model = CustomArmorHelper.getCustomArmorModel(entityLiving, itemStack, armorSlot, model, model1, model2);
    }
}
