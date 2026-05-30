package thaumcraft.common.entities.monster;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;


public class EntityGiantBrainyZombie extends EntityBrainyZombie
{
    private static final EntityDataAccessor<Float> ANGER =
        SynchedEntityData.defineId(EntityGiantBrainyZombie.class, EntityDataSerializers.FLOAT);

    public EntityGiantBrainyZombie(net.minecraft.world.entity.EntityType<? extends EntityGiantBrainyZombie> type, Level world) {
        super(type, world);
        xpReward = 15;
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.monster.Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 60.0)
            .add(Attributes.ATTACK_DAMAGE, 7.0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        goalSelector.addGoal(2, new LeapAtTargetGoal(this, 0.4f));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(ANGER, 0.0f);
    }

    public float getAnger() {
        return entityData.get(ANGER);
    }

    public void setAnger(float par1) {
        entityData.set(ANGER, par1);
    }

    @Override
    public void tick() {
        super.tick();
        if (getAnger() > 1.0f) {
            setAnger(getAnger() - 0.002f);
            getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(7.0f + (getAnger() - 1.0f) * 5.0f);
        }
    }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel sl, DamageSource source, float damage) {
        setAnger(Math.min(2.0f, getAnger() + 0.1f));
        return super.hurtServer(sl, source, damage);
    }
}
