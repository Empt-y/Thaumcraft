package thaumcraft.common.items.armor;
import net.minecraft.world.entity.EquipmentSlot;
import java.util.List;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
// import net.minecraft.world.item.Item /* ArmorItem removed */; // removed
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
// removed: import net.minecraftforge.common.Object /* ISpecialArmor removed */;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.renderers.models.gear.ModelFortressArmor;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;
import net.minecraft.world.item.TooltipFlag;


public class ItemFortressArmor extends net.minecraft.world.item.Item implements IGoggles, IRevealer, IThaumcraftItems
{
    HumanoidModel model1;
    HumanoidModel model2;
    HumanoidModel model;
    
    public ItemFortressArmor(String name, Object /* nested class removed */ material, int renderIndex, EquipmentSlot armorType) {
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
    
    @OnlyIn(Dist.CLIENT)
    public HumanoidModel getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel _default) {
        if (model1 == null) {
            model1 = new ModelFortressArmor(1.0f);
        }
        if (model2 == null) {
            model2 = new ModelFortressArmor(0.5f);
        }
        return model = CustomArmorHelper.getCustomArmorModel(entityLiving, itemStack, armorSlot, model, model1, model2);
    }
    
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "thaumcraft:textures/entity/0 /* armor field removed *//fortress_armor.png";
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.RARE;
    }
    
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level worldIn, List<String> tooltip, TooltipFlag flagIn) {
        if (!stack.isEmpty() && stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("goggles")) {
            tooltip.add(ChatFormatting.DARK_PURPLE + I18n.get("item.goggles.name"));
        }
        if (!stack.isEmpty() && stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("mask")) {
            tooltip.add(ChatFormatting.GOLD + I18n.get("item.fortress_helm.mask." + stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getIntOr("mask", 0)));
        }
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
    
    public boolean isValidRepairItem(ItemStack repairItem) {
        return ItemStack.isSameItem(repairItem, new ItemStack(ItemsTC.ingots, 1)) || false;
    }
    
    public Object /* return type removed */ getProperties(LivingEntity player, ItemStack armor, DamageSource source, double damage, int slot) {
        int priority = 0;
        double ratio = 0 /* damageReduceAmount removed */ / 25.0;
        if (source.isMagicDamage()) {
            priority = 1;
            ratio = 0 /* damageReduceAmount removed */ / 35.0;
        }
        else if (source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE) || source.is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION)) {
            priority = 1;
            ratio = 0 /* damageReduceAmount removed */ / 20.0;
        }
        else if (source.isUnblockable()) {
            priority = 0;
            ratio = 0.0;
        }
        Object /* removed */ ap = null /* ctor removed */;
        if (player instanceof Player) {
            int q = 0;
            for (int a = 1; a < 4; ++a) {
                ItemStack piece = ((Player)player).getInventory().armor.get(a);
                if (piece != null && !piece.isEmpty() && piece.getItem() instanceof ItemFortressArmor) {
                    if (!piece.isEmpty() && piece.contains("mask")) {
                        Object /* removed */ armorProperties = ap;
                        ++armorProperties.Armor;
                    }
                    if (++q <= 1) {
                        Object /* removed */ armorProperties2 = ap;
                        ++armorProperties2.Armor;
                        Object /* removed */ armorProperties3 = ap;
                        ++armorProperties3.Toughness;
                    }
                }
            }
        }
        return ap;
    }
    
    public int getArmorDisplay(Player player, ItemStack armor, int slot) {
        int q = 0;
        int ar = 0;
        for (int a = 1; a < 4; ++a) {
            ItemStack piece = player.getInventory().getItem(36 + a);
            if (piece != null && !piece.isEmpty() && piece.getItem() instanceof ItemFortressArmor) {
                if (!piece.isEmpty() && piece.contains("mask")) {
                    ++ar;
                }
                if (++q <= 1) {
                    ++ar;
                }
            }
        }
        return ar;
    }
    
    public void damageArmor(LivingEntity entity, ItemStack stack, DamageSource source, int damage, int slot) {
        if (source != DamageSource.FALL) {
            stack.hurtAndBreak(damage, entity, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
        }
    }
    
    public boolean showNodes(ItemStack itemstack, LivingEntity player) {
        return !itemstack.isEmpty() && itemstack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("goggles");
    }
    
    public boolean showIngamePopups(ItemStack itemstack, LivingEntity player) {
        return !itemstack.isEmpty() && itemstack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("goggles");
    }
}
