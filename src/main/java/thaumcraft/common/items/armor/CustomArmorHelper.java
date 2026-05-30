package thaumcraft.common.items.armor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemUseAnimation;
// import net.minecraft.world.item.Item /* ArmorItem removed */; // removed
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.HumanoidArm;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class CustomArmorHelper
{
    protected static HumanoidModel getCustomArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel model, HumanoidModel model1, HumanoidModel model2) {
        if (model == null) {
            EquipmentSlot type = EquipmentSlot.CHEST /* armorType removed */;
            if (type == EquipmentSlot.CHEST || type == EquipmentSlot.FEET) {
                model = model1;
            }
            else {
                model = model2;
            }
        }
        if (model != null) {
            // In 1.21.5, HumanoidModel no longer has crouching/riding/young/rightArmPose/leftArmPose
            // fields — those are part of the render state and applied automatically.
            // We only set part visibility here.
            model.head.visible = (armorSlot == EquipmentSlot.HEAD);
            model.hat.visible = (armorSlot == EquipmentSlot.HEAD);
            model.body.visible = (armorSlot == EquipmentSlot.CHEST || armorSlot == EquipmentSlot.LEGS);
            model.rightArm.visible = (armorSlot == EquipmentSlot.CHEST);
            model.leftArm.visible = (armorSlot == EquipmentSlot.CHEST);
            model.rightLeg.visible = (armorSlot == EquipmentSlot.LEGS);
            model.leftLeg.visible = (armorSlot == EquipmentSlot.LEGS);
        }
        return model;
    }
}
