package thaumcraft.common.items.armor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
// import net.minecraft.world.item.Item /* ArmorItem removed */; // removed
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemVoidArmor extends net.minecraft.world.item.Item implements IWarpingGear, IThaumcraftItems
{
    public ItemVoidArmor(String name, Object /* nested class removed */ enumarmormaterial, int j, EquipmentSlot k) {
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
    
    public Object /* ModelResourceLocation removed */ getCustomModelResourceLocation(String variant) {
        return null /* removed */;
    }
    
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (stack.getItem() == ItemsTC.voidHelm || stack.getItem() == ItemsTC.voidChest || stack.getItem() == ItemsTC.voidBoots) {
            return "thaumcraft:textures/entity/0 /* armor field removed *//void_1.png";
        }
        if (stack.getItem() == ItemsTC.voidLegs) {
            return "thaumcraft:textures/entity/0 /* armor field removed *//void_2.png";
        }
        return "thaumcraft:textures/entity/0 /* armor field removed *//void_1.png";
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.UNCOMMON;
    }
    
    public boolean isValidRepairItem(ItemStack repairItem) {
        return ItemStack.isSameItem(repairItem, new ItemStack(ItemsTC.ingots, 1)) || false;
    }
    
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(stack, world, entity, p_77663_4_, p_77663_5_);
        if (!world.isClientSide() && (stack.getDamageValue() > 0) && entity.tickCount % 20 == 0 && entity instanceof LivingEntity) {
            if (stack.isDamageableItem()) stack.setDamageValue(Math.max(0, stack.getDamageValue() - 1));
        }
    }
    
    public void onArmorTick(Level world, Player player, ItemStack armor) {
        super.onArmorTick(world, player, armor);
        if (!world.isClientSide() && armor.getDamageValue() > 0 && player.tickCount % 20 == 0) {
            if (armor.isDamageableItem()) armor.setDamageValue(Math.max(0, armor.getDamageValue() - 1));
        }
    }
    
    public int getWarp(ItemStack itemstack, Player player) {
        return 1;
    }
}
