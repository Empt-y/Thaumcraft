package thaumcraft.common.items.armor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
// import net.minecraft.world.item.Item /* ArmorItem removed */; // removed
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;
import thaumcraft.common.lib.events.PlayerEvents;


public class ItemBootsTraveller extends net.minecraft.world.item.Item implements IThaumcraftItems, IRechargable
{
    public ItemBootsTraveller() {
        super(new Item.Properties());
        /* setMaxDamage removed - use Item.Properties */;
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
        return "thaumcraft:textures/entity/0 /* armor field removed *//bootstraveler.png";
    }
    
    public boolean isValidRepairItem(ItemStack repairItem) {
        return ItemStack.isSameItem(repairItem, new ItemStack(Items.LEATHER)) || false;
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.RARE;
    }
    
    public void onArmorTick(Level world, Player player, ItemStack itemStack) {
        boolean hasCharge = RechargeHelper.getCharge(itemStack) > 0;
        if (!world.isClientSide() && player.tickCount % 20 == 0) {
            int e = 0;
            if (!itemStack.isEmpty()) {
                e = itemStack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getIntOr("energy", 0);
            }
            if (e > 0) {
                --e;
            }
            else if (e <= 0 && RechargeHelper.consumeCharge(itemStack, player, 1)) {
                e = 60;
            }
            final int eFinal = e;
            net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, itemStack, t -> t.putInt("energy", eFinal));
        }
        if (hasCharge && !player.getAbilities().flying) {
            if (player.onGround()) {
                float bonus = 0.05f;
                if (player.isInWater()) {
                    bonus /= 4.0f;
                }
                player.moveRelative(1.0f, new net.minecraft.world.phys.Vec3(0.0f, 0.0f, bonus));
            } else {
                if (player.isInWater()) {
                    player.moveRelative(1.0f, new net.minecraft.world.phys.Vec3(0.0f, 0.0f, 0.025f));
                }
            }
        }
    }
    
    public int getMaxCharge(ItemStack stack, LivingEntity player) {
        return 240;
    }
    
    public EnumChargeDisplay showInHud(ItemStack stack, LivingEntity player) {
        return EnumChargeDisplay.PERIODIC;
    }
}
