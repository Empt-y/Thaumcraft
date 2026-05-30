package thaumcraft.common.entities.projectile;

import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.codechicken.lib.vec.Quat;
import thaumcraft.common.lib.SoundsTC;
import io.netty.buffer.ByteBuf;


public class EntityRiftBlast extends ThrowableProjectile
{
    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        }

    int targetID;
    LivingEntity target;
    public boolean red;
    public double[][] points;
    public float[][] colours;
    public double[] radii;
    int growing;
    ArrayList<Quat> vecs;
    
    public EntityRiftBlast(net.minecraft.world.entity.EntityType<? extends EntityRiftBlast> type, Level par1World) {
        super(type, par1World);
        targetID = 0;
        red = false;
        growing = -1;
        vecs = new ArrayList<Quat>();
    }
    
    public EntityRiftBlast(Level par1World, LivingEntity par2Mob, LivingEntity t, boolean r) {
        super(null, par1World);
        setOwner(par2Mob);
        setPos(par2Mob.getX(), par2Mob.getEyeY() - 0.1, par2Mob.getZ());
        targetID = 0;
        red = false;
        growing = -1;
        vecs = new ArrayList<Quat>();
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
            FXDispatcher.INSTANCE.burst(getX(), getY(), getZ(), 6.0f);
        }
        discard();
    }
    
    public void tick() {
        super.tick();
        if (tickCount > (red ? 240 : 160)) {
            discard();
        }
        if (target != null) {
            if (target == null || target.isDeadOrDying()) {
                discard();
            }
            double d = distanceToSqr(target);
            double dx = target.getX() - getX();
            double dy = target.getBoundingBox().minY + target.getBbHeight() * 0.6 - getY();
            double dz = target.getZ() - getZ();
            double d2 = 1.0;
            dx /= d;
            dy /= d;
            dz /= d;
            setDeltaMovement(getDeltaMovement().x + dx * d2, getDeltaMovement().y, getDeltaMovement().z);
            setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y + dy * d2, getDeltaMovement().z);
            setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z + dz * d2);
            setDeltaMovement(Mth.clamp((float) getDeltaMovement().x, -0.33f, 0.33f), getDeltaMovement().y, getDeltaMovement().z);
            setDeltaMovement(getDeltaMovement().x, Mth.clamp((float) getDeltaMovement().y, -0.33f, 0.33f), getDeltaMovement().z);
            setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, Mth.clamp((float) getDeltaMovement().z, -0.33f, 0.33f));
            if (level().isClientSide()) {
                Quat q = new Quat(0.1, getX() + getRandom().nextGaussian() * 0.05, getY() + getRandom().nextGaussian() * 0.05, getZ() + getRandom().nextGaussian() * 0.05);
                vecs.add(q);
                FXDispatcher.INSTANCE.drawCurlyWisp(q.x, q.y, q.z, 0.0, 0.0, 0.0, 0.3f + getRandom().nextFloat() * 0.2f, getRandom().nextFloat(), getRandom().nextFloat() * 0.2f, getRandom().nextFloat() * 0.2f, 0.5f, null, 1, getRandom().nextInt(2), 0);
                if (vecs.size() > 9) {
                    vecs.remove(0);
                }
                points = new double[vecs.size()][3];
                colours = new float[vecs.size()][4];
                radii = new double[vecs.size()];
                int c = 0;
                if (vecs.size() > 1) {
                    float vv = (float)(3.141592653589793 / (float)(vecs.size() - 1));
                    for (Quat v : vecs) {
                        float variance = 1.0f + Mth.sin((c + tickCount) / 3.0f) * 0.2f;
                        float xx = Mth.sin((c + tickCount) / 6.0f) * 0.01f;
                        float yy = Mth.sin((c + tickCount) / 7.0f) * 0.01f;
                        float zz = Mth.sin((c + tickCount) / 8.0f) * 0.01f;
                        points[c][0] = v.x + xx;
                        points[c][1] = v.y + yy;
                        points[c][2] = v.z + zz;
                        radii[c] = v.s * variance;
                        double[] radii = this.radii;
                        int n = c;
                        radii[n] *= Mth.sin(c * vv);
                        colours[c][0] = 1.0f;
                        colours[c][1] = 0.0f;
                        colours[c][2] = 0.0f;
                        colours[c][3] = 1.0f;
                        ++c;
                    }
                }
            }
        }
    }
    
    public boolean attackEntityFrom(DamageSource source, float damage) {
        if (isInvulnerableTo(source)) {
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
