package thaumcraft.common.items.baubles;
// baubles import removed
// baubles import removed
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemTCBase;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import java.util.function.Consumer;
import net.minecraft.world.item.component.TooltipDisplay;


public class ItemVerdantCharm extends ItemTCBase implements IRechargable
{
    public ItemVerdantCharm() {
        super("verdant_charm");
        // maxStackSize removed - set in Item.Properties
        // canRepair field removed
        /* setMaxDamage removed - use Item.Properties */;
        /* addPropertyOverride removed */

    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.RARE;
    }
    
    public Object /* BaubleType removed */ getBaubleType(ItemStack itemstack) {
        return null /* nested removed */;
    }
    
    
    public void getSubItems(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == null /* SEARCH tab removed */) {
            items.add(new ItemStack(this));
            ItemStack vhbl = new ItemStack(this);
            net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, vhbl, t -> t.put("type", net.minecraft.nbt.ByteTag.valueOf((byte)1)));
            items.add(vhbl);
            ItemStack vhbl2 = new ItemStack(this);
            net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, vhbl2, t -> t.put("type", net.minecraft.nbt.ByteTag.valueOf((byte)2)));
            items.add(vhbl2);
        }
    }
    
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flagIn) {
        if (!stack.isEmpty() && stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getByteOr("type", (byte)0) == 1) {
            tooltip.accept(net.minecraft.network.chat.Component.literal("" + ChatFormatting.GOLD + I18n.get("item.verdant_charm.life.text")));
        }
        if (!stack.isEmpty() && stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getByteOr("type", (byte)0) == 2) {
            tooltip.accept(net.minecraft.network.chat.Component.literal("" + ChatFormatting.GOLD + I18n.get("item.verdant_charm.sustain.text")));
        }
    }
    
    public void onWornTick(ItemStack itemstack, LivingEntity player) {
        if (!player.level().isClientSide() && player.tickCount % 20 == 0 && player instanceof Player) {
            if (player.getEffect(MobEffects.WITHER) != null && RechargeHelper.consumeCharge(itemstack, player, 20)) {
                player.removeEffect(MobEffects.WITHER);
                return;
            }
            if (player.getEffect(MobEffects.POISON) != null && RechargeHelper.consumeCharge(itemstack, player, 10)) {
                player.removeEffect(MobEffects.POISON);
                return;
            }
            if (player.getEffect(net.minecraft.core.Holder.direct(PotionFluxTaint.instance)) != null && RechargeHelper.consumeCharge(itemstack, player, 5)) {
                player.removeEffect(net.minecraft.core.Holder.direct(PotionFluxTaint.instance));
                return;
            }
            if (!itemstack.isEmpty() && itemstack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getByteOr("type", (byte)0) == 1 && player.getHealth() < player.getMaxHealth() && RechargeHelper.consumeCharge(itemstack, player, 5)) {
                player.heal(1.0f);
                return;
            }
            if (!itemstack.isEmpty() && itemstack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getByteOr("type", (byte)0) == 2) {
                if (player.getAirSupply() < 100 && RechargeHelper.consumeCharge(itemstack, player, 1)) {
                    player.setAirSupply(300);
                    return;
                }
                if (player instanceof Player && ((Player)player).canEat(false) && RechargeHelper.consumeCharge(itemstack, player, 1)) {
                    ((Player)player).getFoodData().eat(1, 0.3f);
                }
            }
        }
    }
    
    public int getMaxCharge(ItemStack stack, LivingEntity player) {
        return 200;
    }
    
    public EnumChargeDisplay showInHud(ItemStack stack, LivingEntity player) {
        return EnumChargeDisplay.NORMAL;
    }
    
    public boolean willAutoSync(ItemStack itemstack, LivingEntity player) {
        return true;
    }
}
