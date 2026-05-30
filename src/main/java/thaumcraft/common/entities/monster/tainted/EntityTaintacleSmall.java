package thaumcraft.common.entities.monster.tainted;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;
import thaumcraft.api.entities.ITaintedMob;


public class EntityTaintacleSmall extends EntityTaintacle implements ITaintedMob
{
    int lifetime;

    @SuppressWarnings("unchecked")
    public EntityTaintacleSmall(Level world) {
        super(null, world);
        // Entity requires EntityType; use factory method
    }

    public EntityTaintacleSmall(net.minecraft.world.entity.EntityType<? extends EntityTaintacleSmall> type, Level world) {
        super(type, world);
        lifetime = 200;
        xpReward = 0;
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.monster.Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 5.0)
            .add(Attributes.ATTACK_DAMAGE, 2.0);
    }

    @Override
    public void tick() {
        super.tick();
        if (lifetime-- <= 0 && level() instanceof net.minecraft.server.level.ServerLevel sl) {
            hurtServer(sl, sl.damageSources().magic(), 10.0f);
        }
    }
}
