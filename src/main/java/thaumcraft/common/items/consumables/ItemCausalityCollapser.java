package thaumcraft.common.items.consumables;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import thaumcraft.common.entities.projectile.EntityCausalityCollapser;
import thaumcraft.common.items.ItemTCBase;


public class ItemCausalityCollapser extends ItemTCBase
{
    public ItemCausalityCollapser() {
        super("causality_collapser");
        /* setMaxDamage removed - use Item.Properties */;
    }
    
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (!player.getAbilities().instabuild) {
            player.getItemInHand(hand).shrink(1);
        }
        player.playSound(SoundEvents.EGG_THROW, 0.3f, 0.4f / (ItemCausalityCollapser.getRandom().nextFloat() * 0.4f + 0.8f));
        if (!world.isClientSide()) {
            EntityCausalityCollapser proj = new EntityCausalityCollapser(world, player);
            proj.shoot(player, player.getXRot(), player.getYRot(), -5.0f, 0.8f, 2.0f);
            world.addFreshEntity(proj);
        }
        return InteractionResult.SUCCESS;
    }
}
