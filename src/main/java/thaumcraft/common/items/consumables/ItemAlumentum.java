package thaumcraft.common.items.consumables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import thaumcraft.common.entities.projectile.EntityAlumentum;
import thaumcraft.common.items.ItemTCBase;


public class ItemAlumentum extends ItemTCBase
{
    public ItemAlumentum() {
        super("alumentum");
        /* setMaxDamage removed - use Item.Properties */;
    }
    
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (!player.getAbilities().instabuild) {
            player.getItemInHand(hand).shrink(1);
        }
        player.playSound(SoundEvents.EGG_THROW, 0.3f, 0.4f / (net.minecraft.util.RandomSource.create().nextFloat() * 0.4f + 0.8f));
        if (!world.isClientSide()) {
            EntityAlumentum alumentum = new EntityAlumentum(world, player);
            alumentum.shoot(player, player.getXRot(), player.getYRot(), -5.0f, 0.4f, 2.0f);
            world.addFreshEntity(alumentum);
        }
        return InteractionResult.SUCCESS;
    }
}
