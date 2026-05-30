package thaumcraft.common.entities.projectile;

import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.common.lib.events.ServerEvents;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;


public class EntityFocusMine extends ThrowableProjectile
{
    FocusPackage focusPackage;
    boolean friendly;
    private static EntityDataAccessor<Boolean> ARMED;
    public int counter;
    FocusEffect[] effects;
    
    public EntityFocusMine(net.minecraft.world.entity.EntityType<? extends EntityFocusMine> type, Level par1World) {
        super(type, par1World);
        friendly = false;
        counter = 40;
        effects = null;
        // FIXME: setSize removed; dimensions set in EntityType builder
    }
    
    public EntityFocusMine(FocusPackage pack, Trajectory trajectory, boolean friendly) {
        super(pack.world, pack.getCaster());
        this.friendly = false;
        counter = 40;
        effects = null;
        focusPackage = pack;
        this.friendly = friendly;
        setPos(trajectory.source.x, trajectory.source.y, trajectory.source.z);
        shoot(trajectory.direction.x, trajectory.direction.y, trajectory.direction.z, 0.0f, 0.0f);
        // FIXME: setSize removed; dimensions set in EntityType builder
    }
    
    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        builder.define(EntityFocusMine.ARMED, false);
    }
    
    public boolean getIsArmed() {
        return (boolean) entityData.get((EntityDataAccessor)EntityFocusMine.ARMED);
    }
    
    public void setIsArmed(boolean par1) {
        entityData.set(EntityFocusMine.ARMED, par1);
    }
    
    protected float getGravityVelocity() {
        return 0.01f;
    }

    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("armed", getIsArmed());
        nbt.put("pack", focusPackage.serialize());
        nbt.putBoolean("friendly", friendly);
    }
    
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        friendly = nbt.getBooleanOr("friendly", false);
        setIsArmed(nbt.getBooleanOr("armed", false));
        if (getIsArmed()) {
            counter = 0;
        }
        try {
            (focusPackage = new FocusPackage()).deserialize(nbt.getCompoundOrEmpty("pack"));
        }
        catch (Exception ex) {}
    }
    
    protected void onImpact(HitResult mop) {
        if (mop != null && getOwner() != null) {
            setIsArmed(true);
        }
    }
    
    public void tick() {
        super.tick();
        if (pushOutOfBlocks(getX(), getY(), getZ())) {
            setDeltaMovement(getDeltaMovement().x * 0.25, getDeltaMovement().y, getDeltaMovement().z);
            setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y * 0.25, getDeltaMovement().z);
            setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z * 0.25);
        }
        if (tickCount > 1200 || (!level().isClientSide() && getOwner() == null)) {
            discard();
        }
        if (isAlive() && getIsArmed()) {
            if (counter > 0) {
                --counter;
            }
            if (counter <= 0 && tickCount % 5 == 0) {
                if (level().isClientSide()) {
                    if (effects == null) {
                        effects = focusPackage.getFocusEffects();
                    }
                    if (effects != null && effects.length > 0) {
                        FocusEffect eff = effects[getRandom().nextInt(effects.length)];
                        eff.renderParticleFX(level(), getX() + getRandom().nextGaussian() * 0.1, getY() + getRandom().nextGaussian() * 0.1, getZ() + getRandom().nextGaussian() * 0.1, getRandom().nextGaussian() * 0.009999999776482582, getRandom().nextGaussian() * 0.009999999776482582, getRandom().nextGaussian() * 0.009999999776482582);
                    }
                }
                else {
                    List<LivingEntity> list2 = EntityUtils.getEntitiesInRange(level(), getX(), getY(), getZ(), this, LivingEntity.class, 1.0);
                    int d = 0;
                    for (LivingEntity e : list2) {
                        if (e.isDeadOrDying()) {
                            continue;
                        }
                        if (friendly) {
                            if (!EntityUtils.isFriendly(focusPackage.getCaster(), e)) {
                                continue;
                            }
                        }
                        else if (EntityUtils.isFriendly(focusPackage.getCaster(), e)) {
                            continue;
                        }
                        Vec3 epv = e.position().add(0.0, e.getBbHeight() / 2.0f, 0.0);
                        ServerEvents.addRunnableServer(level(), new Runnable() {
                            @Override
                            public void run() {
                                HitResult ray = null /* new HitResult removed */;
                                ray.getLocation() = e.position().add(0.0, e.getBbHeight() / 2.0f, 0.0);
                                FocusEngine.runFocusPackage(focusPackage.copy(getOwner()), new Trajectory[] { new Trajectory(position(), epv.subtract(position()).normalize()) }, new HitResult[] { ray });
                            }
                        }, d++);
                    }
                    if (d > 0) {
                        discard();
                    }
                }
            }
        }
    }
    
    static {
        ARMED = SynchedEntityData.defineId(EntityFocusMine.class, EntityDataSerializers.BOOLEAN);
    }
}
