package thaumcraft.common.entities.monster;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import thaumcraft.api.items.ItemsTC;
import net.minecraft.world.entity.monster.zombie.Zombie;


public class EntityBrainyZombie extends Zombie
{
    public EntityBrainyZombie(net.minecraft.world.entity.EntityType<? extends EntityBrainyZombie> type, Level world) {
        super(type, world);
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.monster.Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 25.0)
            .add(Attributes.ATTACK_DAMAGE, 5.0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }
}
