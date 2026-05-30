package thaumcraft.common.items.armor;
import net.minecraft.world.entity.EquipmentSlot;
// baubles import removed
// baubles import removed
// baubles import removed
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
// import net.minecraft.world.item.Item /* ArmorItem removed */; // removed
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;
import net.minecraft.client.renderer.rendertype.RenderType;


public class ItemGoggles extends net.minecraft.world.item.Item implements IVisDiscountGear, IRevealer, IGoggles, IThaumcraftItems
{
    Identifier tex;
    
    public ItemGoggles() {
        super(new Item.Properties());
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/items/goggles_bauble.png");
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
        return "thaumcraft:textures/entity/0 /* armor field removed *//goggles.png";
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.RARE;
    }
    
    public boolean isValidRepairItem(ItemStack repairItem) {
        return ItemStack.isSameItem(repairItem, new ItemStack(ItemsTC.ingots, 1)) || false;
    }
    
    public int getVisDiscount(ItemStack stack, Player player) {
        return 5;
    }
    
    public boolean showNodes(ItemStack itemstack, LivingEntity player) {
        return true;
    }
    
    public boolean showIngamePopups(ItemStack itemstack, LivingEntity player) {
        return true;
    }
    
    public Object /* BaubleType removed */ getBaubleType(ItemStack arg0) {
        return null /* nested removed */;
    }
    
    @OnlyIn(Dist.CLIENT)
    public void onPlayerBaubleRender(ItemStack stack, Player player, float ticks) {
        if (type == null) {
            boolean helmetEmpty = player.getItemBySlot(EquipmentSlot.HEAD) != null;
            Minecraft.getInstance().renderEngine.bindTexture(tex);
            /* null call removed */;
            /* null call removed */;
            /* null call removed */;
            RenderSystem.rotate(180.0f, 0.0f, 1.0f, 0.0f);
            RenderSystem.translate(-0.5, -0.5, armor ? 0.11999999731779099 : 0.0);
            UtilsFX.renderTextureIn3D(0.0f, 0.0f, 1.0f, 1.0f, 16, 26, 0.1f);
        }
    }
}
