package thaumcraft.common.items.armor;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;
import net.minecraft.network.chat.Component;


public class ItemFortressArmor extends net.minecraft.world.item.Item implements IGoggles, IRevealer, IThaumcraftItems
{
    public ItemFortressArmor(String name, Object material, int renderIndex, EquipmentSlot armorType) {
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

    public Object getCustomModelResourceLocation(String variant) {
        return null;
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return "thaumcraft:textures/entity/armor/fortress_armor.png";
    }

    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.RARE;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        if (!customData.isEmpty()) {
            if (customData.copyTag().contains("goggles")) {
                tooltip.accept(Component.literal("" + ChatFormatting.DARK_PURPLE + I18n.get("item.goggles.name")));
            }
            if (customData.copyTag().contains("mask")) {
                tooltip.accept(Component.literal("" + ChatFormatting.GOLD + I18n.get("item.fortress_helm.mask." + customData.copyTag().getIntOr("mask", 0))));
            }
        }
        super.appendHoverText(stack, context, tooltipDisplay, tooltip, flagIn);
    }

    public boolean isValidRepairItem(ItemStack repairItem) {
        return ItemStack.isSameItem(repairItem, new ItemStack(ItemsTC.ingots, 1));
    }

    public boolean showNodes(ItemStack itemstack, LivingEntity player) {
        return !itemstack.isEmpty() && itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().contains("goggles");
    }

    public boolean showIngamePopups(ItemStack itemstack, LivingEntity player) {
        return !itemstack.isEmpty() && itemstack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().contains("goggles");
    }
}
