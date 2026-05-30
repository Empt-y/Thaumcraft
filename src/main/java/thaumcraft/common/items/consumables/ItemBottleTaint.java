package thaumcraft.common.items.consumables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.projectile.EntityBottleTaint;
import thaumcraft.common.items.ItemTCBase;


public class ItemBottleTaint extends ItemTCBase
{
    public ItemBottleTaint() {
        super("bottle_taint");
        // maxStackSize removed - set in Item.Properties
        /* setMaxDamage removed - use Item.Properties */;
    }
    
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (!player.getAbilities().instabuild) {
            player.getItemInHand(hand).shrink(1);
        }
        player.playSound(SoundEvents.EGG_THROW, 0.5f, 0.4f / (ItemBottleTaint.getRandom().nextFloat() * 0.4f + 0.8f));
        if (!world.isClientSide()) {
            EntityBottleTaint entityBottle = new EntityBottleTaint(world, player);
            entityBottle.shoot(player, player.getXRot(), player.getYRot(), -5.0f, 0.66f, 1.0f);
            world.addFreshEntity(entityBottle);
        }
        return InteractionResult.SUCCESS;
    }
}
