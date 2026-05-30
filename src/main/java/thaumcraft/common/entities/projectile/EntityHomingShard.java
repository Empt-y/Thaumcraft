package thaumcraft.common.entities.projectile;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;
import io.netty.buffer.ByteBuf;


public class EntityHomingShard extends ThrowableProjectile
{
    Class tclass;
    boolean persistant;
    int targetID;
    LivingEntity target;
    private static EntityDataAccessor<Byte> STRENGTH;
    public ArrayList<UtilsFX.Vector> vl;
    
    public EntityHomingShard(net.minecraft.world.entity.EntityType<? extends EntityHomingShard> type, Level par1World) {
        super(type, par1World);
        tclass = null;
        persistant = false;
        targetID = 0;
        vl = new ArrayList<UtilsFX.Vector>();
    }
    
    public EntityHomingShard(Level par1World, LivingEntity p, LivingEntity t, int strength, boolean b) {
        super(null, par1World);
        setOwner(p);
        setPos(p.getX(), p.getEyeY() - 0.1, p.getZ());
        tclass = null;
        persistant = false;
        targetID = 0;
        vl = new ArrayList<UtilsFX.Vector>();
        target = t;
        tclass = t.getClass();
        persistant = b;
        setStrength(strength);
        Vec3 v = p.getLookAngle();
        moveTo(p.getX() + v.x / 2.0, p.getY() + p.getEyeHeight() + v.y / 2.0, p.getZ() + v.z / 2.0, p.getYRot(), p.getXRot());
        float f = 0.5f;
        float ry = p.getYRot() + (random.nextFloat() - random.nextFloat()) * 60.0f;
        float rp = p.getXRot() + (random.nextFloat() - random.nextFloat()) * 60.0f;
        setDeltaMovement(-Mth.sin(ry / 180.0f * 3.1415927f) * Mth.cos(rp / 180.0f * 3.1415927f) * f, getDeltaMovement().y, getDeltaMovement().z);
        setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, Mth.cos(ry / 180.0f * 3.1415927f) * Mth.cos(rp / 180.0f * 3.1415927f) * f);
        setDeltaMovement(getDeltaMovement().x, -Mth.sin(rp / 180.0f * 3.1415927f) * f, getDeltaMovement().z);
    }
    
    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        builder.define((EntityDataAccessor)EntityHomingShard.STRENGTH, 0);
    }
    
    public void setStrength(int str) {
        entityData.set(EntityHomingShard.STRENGTH, (byte)str);
    }
    
    public int getStrength() {
        return (byte) entityData.get((EntityDataAccessor)EntityHomingShard.STRENGTH);
    }
    
    protected float getGravityVelocity() {
        return 0.0f;
    }
    
    protected void onImpact(HitResult mop) {
        if (!level().isClientSide() && mop.getType() == HitResult.Type.ENTITY && (getOwner() == null || (getOwner() != null && ((net.minecraft.world.phys.EntityHitResult)mop).getEntity() != getOwner()))) {
            ((net.minecraft.world.phys.EntityHitResult)mop).getEntity().hurt(level().damageSources().indirectMagic(this, getOwner()), 1.0f + getStrength() * 0.5f);
            playSound(SoundsTC.zap, 1.0f, 1.0f + (random.nextFloat() - random.nextFloat()) * 0.2f);
            level().broadcastEntityEvent(this, (byte)16);
            discard();
        }
        if (mop.getType() == HitResult.Type.BLOCK) {
            if (((net.minecraft.world.phys.BlockHitResult)mop).getDirection().getStepZ() != 0) {
                setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z * -0.8);
            }
            if (((net.minecraft.world.phys.BlockHitResult)mop).getDirection().getStepX() != 0) {
                setDeltaMovement(getDeltaMovement().x * -0.8, getDeltaMovement().y, getDeltaMovement().z);
            }
            if (((net.minecraft.world.phys.BlockHitResult)mop).getDirection().getStepY() != 0) {
                setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y * -0.8, getDeltaMovement().z);
            }
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte par1) {
        if (par1 == 16) {
            FXDispatcher.INSTANCE.burst(getX(), getY(), getZ(), 0.3f);
        }
        else {
            super.handleStatusUpdate(par1);
        }
    }
    
    public void tick() {
        vl.add(0, new UtilsFX.Vector((float) lastTickPosX, (float) lastTickPosY, (float) lastTickPosZ));
        if (vl.size() > 6) {
            vl.remove(vl.size() - 1);
        }
        super.tick();
        if (!level().isClientSide()) {
            if (persistant && (target == null || target.isDeadOrDying() || target.distanceToSqr(this.getX() + 0.5, this.getY() + 0.5, this.getZ() + 0.5) > 1250.0)) {
                List<Entity> es = EntityUtils.getEntitiesInRange(world, getX(), getY(), getZ(), this, (Class<? extends Entity>) tclass, 16.0);
                for (Entity e : es) {
                    if (e instanceof LivingEntity && !e.isDeadOrDying() && (getOwner() == null || e.getId() != getOwner().getId())) {
                        target = (LivingEntity)e;
                        break;
                    }
                }
            }
            if (target == null || target.isDeadOrDying()) {
                level().broadcastEntityEvent(this, (byte)16);
                discard();
            }
        }
        if (tickCount > 300) {
            level().broadcastEntityEvent(this, (byte)16);
            discard();
        }
        if (tickCount % 20 == 0 && target != null && !target.isDeadOrDying()) {
            double d = distanceTo(target);
            double dx = target.getX() - getX();
            double dy = target.getBoundingBox().minY + target.getBbHeight() * 0.6 - getY();
            double dz = target.getZ() - getZ();
            dx /= d;
            dy /= d;
            dz /= d;
            setDeltaMovement(dx, getDeltaMovement().y, getDeltaMovement().z);
            setDeltaMovement(getDeltaMovement().x, dy, getDeltaMovement().z);
            setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, dz);
        }
        setDeltaMovement(getDeltaMovement().x * 0.85, getDeltaMovement().y, getDeltaMovement().z);
        setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y * 0.85, getDeltaMovement().z);
        setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z * 0.85);
    }
    
    static {
        STRENGTH = SynchedEntityData.defineId(EntityHomingShard.class, EntityDataSerializers.BYTE);
    }
}
