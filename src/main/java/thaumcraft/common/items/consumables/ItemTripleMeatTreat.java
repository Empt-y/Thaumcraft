package thaumcraft.common.items.consumables;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemTripleMeatTreat extends Item /* ItemFood removed */ implements IThaumcraftItems
{
    public ItemTripleMeatTreat() {
        super(new net.minecraft.world.item.Item.Properties().food(
            new net.minecraft.world.food.FoodProperties.Builder()
                .nutrition(8)
                .saturationModifier(0.8f)
                .alwaysEdible()
                .build()));
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
    }

    @Override
    public net.minecraft.world.item.ItemStack finishUsingItem(net.minecraft.world.item.ItemStack stack, net.minecraft.world.level.Level level, net.minecraft.world.entity.LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);
        if (!level.isClientSide() && entity instanceof net.minecraft.world.entity.player.Player && entity.getRandom().nextFloat() < 0.66f) {
            entity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 100, 0));
        }
        return result;
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
}
