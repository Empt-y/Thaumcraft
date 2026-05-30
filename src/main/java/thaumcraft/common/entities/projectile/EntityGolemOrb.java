package thaumcraft.common.entities.projectile;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.SoundsTC;
import io.netty.buffer.ByteBuf;


public class EntityGolemOrb extends ThrowableProjectile
{
    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        }

    int targetID;
    LivingEntity target;
    public boolean red;
    
    public EntityGolemOrb(net.minecraft.world.entity.EntityType<? extends EntityGolemOrb> type, Level par1World) {
        super(type, par1World);
        targetID = 0;
        red = false;
    }

    public static EntityGolemOrb create(net.minecraft.world.entity.EntityType<? extends EntityGolemOrb> type, Level par1World, LivingEntity par2Mob, LivingEntity t, boolean r) {
        EntityGolemOrb e = new EntityGolemOrb(type, par1World);
        e.setOwner(par2Mob);
        e.setPos(par2Mob.getX(), par2Mob.getEyeY() - 0.1, par2Mob.getZ());
        e.target = t;
        e.red = r;
        return e;
    }

    /** @deprecated use {@link #create} */
    public EntityGolemOrb(Level par1World, LivingEntity par2Mob, LivingEntity t, boolean r) {
        super(null, par1World);
        this.setOwner(par2Mob);
        this.setPos(par2Mob.getX(), par2Mob.getEyeY() - 0.1, par2Mob.getZ());
        targetID = 0;
        red = false;
        target = t;
        red = r;
    }
    
    protected float getGravityVelocity() {
        return 0.0f;
    }
    
    protected void onImpact(HitResult mop) {
        if (!level().isClientSide() && getOwner() != null && mop.getType() == HitResult.Type.ENTITY) {
            Entity ownerEnt = getOwner();
            float dmg = (ownerEnt instanceof net.minecraft.world.entity.LivingEntity le) ? (float)(le.getAttributeValue(Attributes.ATTACK_DAMAGE) * (red ? 1.0f : 0.6f)) : (red ? 1.0f : 0.6f);
            ((net.minecraft.world.phys.EntityHitResult)mop).getEntity().hurt(level().damageSources().indirectMagic(this, ownerEnt), dmg);
        }
        playSound(SoundsTC.shock, 1.0f, 1.0f + (getRandom().nextFloat() - getRandom().nextFloat()) * 0.2f);
        if (level().isClientSide()) {
            FXDispatcher.INSTANCE.burst(getX(), getY(), getZ(), 1.0f);
        }
        discard();
    }
    
    public void tick() {
        super.tick();
        if (tickCount > (red ? 240 : 160)) {
            discard();
        }
        if (target != null) {
            double d = distanceToSqr(target);
            double dx = target.getX() - getX();
            double dy = target.getBoundingBox().minY + target.getBbHeight() * 0.6 - getY();
            double dz = target.getZ() - getZ();
            double d2 = 0.2;
            dx /= d;
            dy /= d;
            dz /= d;
            setDeltaMovement(getDeltaMovement().x + dx * d2, getDeltaMovement().y, getDeltaMovement().z);
            setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y + dy * d2, getDeltaMovement().z);
            setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z + dz * d2);
            setDeltaMovement(Mth.clamp((float) getDeltaMovement().x, -0.25f, 0.25f), getDeltaMovement().y, getDeltaMovement().z);
            setDeltaMovement(getDeltaMovement().x, Mth.clamp((float) getDeltaMovement().y, -0.25f, 0.25f), getDeltaMovement().z);
            setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, Mth.clamp((float) getDeltaMovement().z, -0.25f, 0.25f));
        }
    }
    
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (isInvulnerableToBase(source)) {
            return false;
        }
        if (source.getEntity() != null) {
            Vec3 vec3 = source.getEntity().getLookAngle();
            if (vec3 != null) {
                setDeltaMovement(vec3.x, getDeltaMovement().y, getDeltaMovement().z);
                setDeltaMovement(getDeltaMovement().x, vec3.y, getDeltaMovement().z);
                setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, vec3.z);
                setDeltaMovement(getDeltaMovement().x * 0.9, getDeltaMovement().y, getDeltaMovement().z);
                setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y * 0.9, getDeltaMovement().z);
                setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z * 0.9);
                playSound(SoundsTC.zap, 1.0f, 1.0f + (getRandom().nextFloat() - getRandom().nextFloat()) * 0.2f);
            }
            return true;
        }
        return false;
    }
}
