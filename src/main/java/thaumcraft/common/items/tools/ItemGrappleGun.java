package thaumcraft.common.items.tools;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.entities.projectile.EntityGrapple;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;


public class ItemGrappleGun extends ItemTCBase implements IRechargable
{
    public ItemGrappleGun() {
        super("grapple_gun");
        /* addPropertyOverride removed */

    }
    
    @Override
    public int getMaxCharge(ItemStack stack, LivingEntity player) {
        return 100;
    }
    
    @Override
    public EnumChargeDisplay showInHud(ItemStack stack, LivingEntity player) {
        return EnumChargeDisplay.NORMAL;
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.UNCOMMON;
    }
    
    public void onUpdate(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!EntityGrapple.grapples.containsKey(entityIn.getId()) && !stack.isEmpty() && stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getByteOr("loaded", (byte)0) == 1) {
            net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, stack, t -> t.put("loaded", net.minecraft.nbt.ByteTag.valueOf((byte)0)));
        }
    }
    
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        player.playSound(SoundsTC.ice, 3.0f, 0.8f + net.minecraft.util.RandomSource.create().nextFloat() * 0.1f);
        if (!world.isClientSide() && RechargeHelper.getCharge(player.getItemInHand(hand)) > 0) {
            EntityGrapple grapple = new EntityGrapple(world, player, hand);
            grapple.shootFromRotation(player, player.getXRot(), player.getYRot(), -5.0f, 1.5f, 0.0f);
            double px = -Mth.cos((player.getYRot() - 0.5f) / 180.0f * 3.141593f) * 0.2f * ((grapple.hand == InteractionHand.MAIN_HAND) ? 1 : -1);
            double pz = -Mth.sin((player.getYRot() - 0.5f) / 180.0f * 3.141593f) * 0.3f * ((grapple.hand == InteractionHand.MAIN_HAND) ? 1 : -1);
            Vec3 vl = player.getLookAngle();
            grapple.setPos(grapple.getX() + px + vl.x, grapple.getY(), grapple.getZ() + pz + vl.y);
            if (world.addFreshEntity(grapple)) {
                RechargeHelper.consumeCharge(player.getItemInHand(hand), player, 1);
                { ItemStack _s = player.getItemInHand(hand); net.minecraft.nbt.CompoundTag _t = _s.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag(); _t.putByte("loaded", (byte)1); _s.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(_t)); }
            }
        }
        return InteractionResult.SUCCESS;
    }
    
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (!oldStack.isEmpty() && oldStack.getItem() == this && !newStack.isEmpty() && newStack.getItem() == this && !ItemStack.isSameItemSameComponents(oldStack, newStack)) {
            boolean b1 = !!oldStack.isEmpty() || oldStack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getByteOr("loaded", (byte)0) == 0;
            boolean b2 = !!newStack.isEmpty() || newStack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getByteOr("loaded", (byte)0) == 0;
            return b1 != b2;
        }
        return newStack.getItem() != oldStack.getItem();
    }
}
