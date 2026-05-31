package thaumcraft.common.items.consumables;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemZombieBrain extends Item /* ItemFood removed */ implements IThaumcraftItems
{
    public ItemZombieBrain() {
        super(thaumcraft.common.config.TCItemInit.take().food(
            new net.minecraft.world.food.FoodProperties.Builder()
                .nutrition(2)
                .saturationModifier(0.1f)
                .build()));
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
    }
    
    public void onFoodEaten(ItemStack stack, Level world, Player player) {
        if (!world.isClientSide()) {
            if (net.minecraft.util.RandomSource.create().nextFloat() < 0.1f) {
                ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1, IPlayerWarp.EnumWarpType.NORMAL);
            }
            else {
                ThaumcraftApi.internalMethods.addWarpToPlayer(player, 1 + net.minecraft.util.RandomSource.create().nextInt(3), IPlayerWarp.EnumWarpType.TEMPORARY);
            }
        }
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
