package thaumcraft.common.entities.projectile;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;


public class EntityEldritchOrb extends ThrowableProjectile
{
    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        }

    public EntityEldritchOrb(net.minecraft.world.entity.EntityType<? extends EntityEldritchOrb> type, Level par1World) {
        super(type, par1World);
    }
    
    public EntityEldritchOrb(Level par1World, LivingEntity p) {
        super(null, par1World);
        setOwner(p);
        setPos(p.getX(), p.getEyeY() - 0.1, p.getZ());
        shootFromRotation(p, p.getXRot(), p.getYRot(), -5.0f, 0.75f, 0.0f);
    }
    
    protected float getGravityVelocity() {
        return 0.0f;
    }
    
    public void tick() {
        super.tick();
        if (tickCount > 100) {
            discard();
        }
    }
    
    protected void onImpact(HitResult mop) {
        if (!level().isClientSide() && getOwner() != null) {
            Entity ownerEnt = getOwner();
            float dmg = (ownerEnt instanceof LivingEntity le) ? (float)(le.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.666) : 0.666f;
            List<Entity> list = level().getEntities(this, getBoundingBox().inflate(2.0, 2.0, 2.0), e -> e != ownerEnt);
            for (int i = 0; i < list.size(); ++i) {
                Entity entity1 = list.get(i);
                if (entity1 != null && entity1 instanceof LivingEntity && !((LivingEntity)entity1).getType().builtInRegistryHolder().is(net.minecraft.tags.EntityTypeTags.UNDEAD)) {
                    entity1.hurt(level().damageSources().indirectMagic(this, ownerEnt), dmg);
                    try {
                        ((LivingEntity)entity1).addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 160, 0));
                    }
                    catch (Exception ex) {}
                }
            }
            playSound(SoundEvents.LAVA_EXTINGUISH, 0.5f, 2.6f + (getRandom().nextFloat() - getRandom().nextFloat()) * 0.8f);
            discard();
        }
    }
}
