package thaumcraft.common.entities.projectile;

import java.awt.Color;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.events.ServerEvents;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;


public class EntityFocusProjectile extends ThrowableProjectile
{
    public static net.minecraft.world.entity.EntityType<EntityFocusProjectile> TYPE;
    FocusPackage focusPackage;
    private static EntityDataAccessor<Integer> SPECIAL;
    private static EntityDataAccessor<Integer> OWNER;
    boolean noTouchy;
    private Entity target;
    boolean firstParticle;
    public float lastRenderTick;
    FocusEffect[] effects;
    
    public EntityFocusProjectile(net.minecraft.world.entity.EntityType<? extends EntityFocusProjectile> type, Level par1World) {
        super(type, par1World);
        noTouchy = false;
        firstParticle = false;
        lastRenderTick = 0.0f;
        effects = null;
        // FIXME: setSize removed; dimensions set in EntityType builder
    }
    
    public EntityFocusProjectile(FocusPackage pack, float speed, Trajectory trajectory, int special) {
        super(thaumcraft.api.entities.EntitiesTC.FOCUS_PROJECTILE.get(), pack.world);
        noTouchy = false;
        firstParticle = false;
        lastRenderTick = 0.0f;
        effects = null;
        focusPackage = pack;
        setPos(trajectory.source.x + trajectory.direction.x * pack.getCaster().getBbWidth() * 2.1, trajectory.source.y + trajectory.direction.y * pack.getCaster().getBbWidth() * 2.1, trajectory.source.z + trajectory.direction.z * pack.getCaster().getBbWidth() * 2.1);
        shoot(trajectory.direction.x, trajectory.direction.y, trajectory.direction.z, speed, 0.0f);
        // FIXME: setSize removed; dimensions set in EntityType builder
        setSpecial(special);
    }
    
    protected float getGravityVelocity() {
        return (getSpecial() > 1) ? 0.005f : 0.01f;
    }
    
    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        builder.define(EntityFocusProjectile.SPECIAL, 0);
        builder.define(EntityFocusProjectile.OWNER, 0);
    }
    
    public void setOwner(int s) {
        entityData.set(EntityFocusProjectile.OWNER, s);
    }
    
    public int getOwnerId() {
        return (int) entityData.get((EntityDataAccessor)EntityFocusProjectile.OWNER);
    }
    
    @Override
    public net.minecraft.world.entity.Entity getOwner() {
        if (level().isClientSide()) {
            Entity e = level().getEntity(getOwnerId());
            if (e != null && e instanceof LivingEntity) {
                return (LivingEntity)e;
            }
        }
        return super.getOwner();
    }
    
    public void setSpecial(int s) {
        entityData.set(EntityFocusProjectile.SPECIAL, s);
    }
    
    public int getSpecial() {
        return (int) entityData.get((EntityDataAccessor)EntityFocusProjectile.SPECIAL);
    }

    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.store("pack", net.minecraft.nbt.CompoundTag.CODEC, focusPackage.serialize());
        nbt.putInt("special", getSpecial());
    }
    
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        setSpecial(nbt.getIntOr("special", 0));
        try {
            (focusPackage = new FocusPackage()).deserialize(nbt.read("pack", net.minecraft.nbt.CompoundTag.CODEC).orElse(new net.minecraft.nbt.CompoundTag()));
            if (focusPackage != null) focusPackage.world = level();
        }
        catch (Exception ex) {}
        if (getOwner() != null) {
        }
    }
    
    protected void onImpact(HitResult mop) {
        if (mop != null) {
            if (getSpecial() == 1 && mop.getType() == HitResult.Type.BLOCK) {
                BlockState bs = level().getBlockState(((net.minecraft.world.phys.BlockHitResult)mop).getBlockPos());
                AABB bb = bs.getCollisionShape(level(), ((net.minecraft.world.phys.BlockHitResult)mop).getBlockPos()).isEmpty() ? null : bs.getCollisionShape(level(), ((net.minecraft.world.phys.BlockHitResult)mop).getBlockPos()).bounds();
                if (bb == null) {
                    return;
                }
                setPos(getDeltaMovement().x, getY(), getZ());
                setPos(getX(), getDeltaMovement().y, getZ());
                setPos(getX(), getY(), getDeltaMovement().z);
                if (((net.minecraft.world.phys.BlockHitResult)mop).getDirection().getStepZ() != 0) {
                    setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z * -1.0);
                }
                if (((net.minecraft.world.phys.BlockHitResult)mop).getDirection().getStepX() != 0) {
                    setDeltaMovement(getDeltaMovement().x * -1.0, getDeltaMovement().y, getDeltaMovement().z);
                }
                if (((net.minecraft.world.phys.BlockHitResult)mop).getDirection().getStepY() != 0) {
                    setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y * -0.9, getDeltaMovement().z);
                }
                setDeltaMovement(getDeltaMovement().x * 0.9, getDeltaMovement().y, getDeltaMovement().z);
                setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y * 0.9, getDeltaMovement().z);
                setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z * 0.9);
                float var20 = (float)Math.sqrt(getDeltaMovement().x * getDeltaMovement().x + getDeltaMovement().y * getDeltaMovement().y + getDeltaMovement().z * getDeltaMovement().z);
                setPos(getDeltaMovement().x / var20 * 0.05000000074505806, getY(), getZ());
                setPos(getX(), getDeltaMovement().y / var20 * 0.05000000074505806, getZ());
                setPos(getX(), getY(), getDeltaMovement().z / var20 * 0.05000000074505806);
                if (!level().isClientSide()) {
                    playSound(SoundEvents.LEAD_TIED, 0.25f, 1.0f);
                }
                if (!level().isClientSide() && new Vec3(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z).length() < 0.2) {
                    discard();
                }
            }
            else if (!level().isClientSide()) {
                if (((net.minecraft.world.phys.EntityHitResult)mop).getEntity() != null) {
                    // mop.getLocation() = position(); // EntityHitResult location is immutable
                }
                Vec3 pv = new Vec3(xo, yo, zo);
                Vec3 vf = new Vec3(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z);
                ServerEvents.addRunnableServer(level(), new Runnable() {
                    @Override
                    public void run() {
                        FocusEngine.runFocusPackage(focusPackage, new Trajectory[] { new Trajectory(pv, vf.normalize()) }, new HitResult[] { mop });
                    }
                }, 0);
                discard();
            }
        }
    }
    
    public void tick() {
        super.tick();
        if (tickCount > 1200 || (!level().isClientSide() && getOwner() == null)) {
            discard();
        }
        firstParticle = true;
        if (target == null && tickCount % 5 == 0 && getSpecial() > 1) {
            List<LivingEntity> list = EntityUtils.getEntitiesInRangeSorted(level(), this, LivingEntity.class, 16.0);
            for (LivingEntity pt : list) {
                if (pt.isAlive() && EntityUtils.isVisibleTo(1.75f, this, pt, 16.0f)) {
                    if (level().clip(new net.minecraft.world.level.ClipContext(position(), pt.getEyePosition(), net.minecraft.world.level.ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, this)).getType() != net.minecraft.world.phys.HitResult.Type.MISS) {
                        continue;
                    }
                    boolean f = EntityUtils.isFriendly(getOwner(), pt);
                    if (f && getSpecial() == 3) {
                        target = pt;
                        break;
                    }
                    if (!f && getSpecial() == 2) {
                        target = pt;
                        break;
                    }
                    continue;
                }
            }
        }
        if (target != null) {
            double d = distanceToSqr(target);
            double dx = target.getX() - getX();
            double dy = target.getBoundingBox().minY + target.getBbHeight() * 0.6 - getY();
            double dz = target.getZ() - getZ();
            Vec3 v = new Vec3(dx, dy, dz);
            v = v.normalize();
            Vec3 mv = new Vec3(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z);
            double lv = mv.length();
            mv = mv.normalize().add(v.scale(0.275));
            mv = mv.normalize().scale(lv);
            setDeltaMovement(mv.x, getDeltaMovement().y, getDeltaMovement().z);
            setDeltaMovement(getDeltaMovement().x, mv.y, getDeltaMovement().z);
            setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, mv.z);
            if (tickCount % 5 == 0 && (!target.isAlive() || !EntityUtils.isVisibleTo(1.75f, this, target, 16.0f) || level().clip(new net.minecraft.world.level.ClipContext(position(), target.getEyePosition(), net.minecraft.world.level.ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, this)).getType() != net.minecraft.world.phys.HitResult.Type.MISS)) {
                target = null;
            }
        }
    }
    
    public Vec3 getLookAngle() {
        return new Vec3(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z).normalize();
    }
    
    public void renderParticle(float coeff) {
        lastRenderTick = coeff;
        if (effects == null) {
            effects = focusPackage.getFocusEffects();
        }
        if (effects != null && effects.length > 0) {
            FocusEffect eff = effects[getRandom().nextInt(effects.length)];
            float scale = 1.0f;
            Color c1 = new Color(FocusEngine.getElementColor(eff.getKey()));
            FXDispatcher.INSTANCE.drawFireMote((float)(xo + (getX() - xo) * coeff), (float)(yo + (getY() - yo) * coeff) + getBbHeight() / 2.0f, (float)(zo + (getZ() - zo) * coeff), 0.0125f * (getRandom().nextFloat() - 0.5f) * scale, 0.0125f * (getRandom().nextFloat() - 0.5f) * scale, 0.0125f * (getRandom().nextFloat() - 0.5f) * scale, c1.getRed() / 255.0f, c1.getGreen() / 255.0f, c1.getBlue() / 255.0f, 0.5f, 7.0f * scale);
            if (firstParticle) {
                firstParticle = false;
                eff.renderParticleFX(level(), xo + (getX() - xo) * coeff + getRandom().nextGaussian() * 0.10000000149011612, yo + (getY() - yo) * coeff + getBbHeight() / 2.0f + getRandom().nextGaussian() * 0.10000000149011612, zo + (getZ() - zo) * coeff + getRandom().nextGaussian() * 0.10000000149011612, getRandom().nextGaussian() * 0.009999999776482582, getRandom().nextGaussian() * 0.009999999776482582, getRandom().nextGaussian() * 0.009999999776482582);
            }
        }
    }
    
    static {
        SPECIAL = SynchedEntityData.defineId(EntityFocusProjectile.class, EntityDataSerializers.INT);
        OWNER = SynchedEntityData.defineId(EntityFocusProjectile.class, EntityDataSerializers.INT);
    }
}
