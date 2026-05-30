package thaumcraft.common.entities.projectile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.events.ServerEvents;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;


public class EntityFocusCloud extends Entity
{
    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float damage) {
        return false; // Entity is abstract, no super to call
    }

    FocusPackage focusPackage;
    private LivingEntity owner;
    private UUID ownerUniqueId;
    private int duration;
    private static EntityDataAccessor<Float> RADIUS;
    static HashMap<Long, Long> cooldownMap;
    FocusEffect[] effects;
    
    public EntityFocusCloud(net.minecraft.world.entity.EntityType<? extends EntityFocusCloud> type, Level par1World) {
        super(type, par1World);
        effects = null;
    }
    
    public EntityFocusCloud(FocusPackage pack, Trajectory trajectory, float rad, int dur) {
        super(pack.world);
        effects = null;
        focusPackage = pack;
        setPos(trajectory.source.x, trajectory.source.y, trajectory.source.z);
        // FIXME: setSize removed; dimensions set in EntityType builder
        setOwner(pack.getCaster());
        setRadius(rad);
        setDuration(dur);
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void setDuration(int durationIn) {
        duration = durationIn;
    }
    
    public void setOwner(@Nullable LivingEntity ownerIn) {
        owner = ownerIn;
        ownerUniqueId = ((ownerIn == null) ? null : ownerIn.getUUID());
    }
    
    @Nullable
    public LivingEntity getOwner() {
        if (owner == null && ownerUniqueId != null && world instanceof ServerLevel) {
            Entity entity = ((ServerLevel) world).getEntity(ownerUniqueId);
            if (entity instanceof LivingEntity) {
                owner = (LivingEntity)entity;
            }
        }
        return owner;
    }
    
    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        builder.define(EntityFocusCloud.RADIUS, 0.5f);
    }
    
    public void setRadius(float radiusIn) {
        double d0 = getX();
        double d2 = getY();
        double d3 = getZ();
        // FIXME: setSize removed; dimensions set in EntityType builder
        setPos(d0, d2, d3);
        if (!level().isClientSide()) {
            entityData.set(EntityFocusCloud.RADIUS, radiusIn);
        }
    }
    
    public float getRadius() {
        return (float) entityData.get((EntityDataAccessor)EntityFocusCloud.RADIUS);
    }

    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
        nbt.putInt("Age", tickCount);
        nbt.putInt("Duration", duration);
        nbt.putFloat("Radius", getRadius());
        if (ownerUniqueId != null) {
            nbt.putIntArray("OwnerUUID", net.minecraft.core.UUIDUtil.uuidToIntArray(ownerUniqueId));
        }
        nbt.put("pack", focusPackage.serialize());
    }
    
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
        tickCount = nbt.getIntOr("Age", 0);
        duration = nbt.getIntOr("Duration", 0);
        setRadius(nbt.getFloatOr("Radius", 0.0f));
        ownerUniqueId = net.minecraft.core.UUIDUtil.uuidFromIntArray(nbt.getIntArray("OwnerUUID").orElse(new int[4]));
        try {
            (focusPackage = new FocusPackage()).deserialize(nbt.getCompoundOrEmpty("pack"));
        }
        catch (Exception ex) {}
    }
    
    public void tick() {
        super.tick();
        float rad = getRadius();
        int dur = getDuration();
        if (!level().isClientSide() && (tickCount > dur * 20 || getOwner() == null)) {
            discard();
        }
        if (isAlive()) {
            if (level().isClientSide()) {
                if (effects == null) {
                    effects = focusPackage.getFocusEffects();
                }
                if (effects != null && effects.length > 0) {
                    for (int a = 0; a < rad; ++a) {
                        FocusEffect eff = effects[random.nextInt(effects.length)];
                        FXDispatcher.INSTANCE.drawFocusCloudParticle(getX() + random.nextGaussian() * rad / 2.0 * 0.85, getY() + random.nextGaussian() * rad / 2.0 * 0.85, getZ() + random.nextGaussian() * rad / 2.0 * 0.85, random.nextGaussian() * 0.01, random.nextGaussian() * 0.01, random.nextGaussian() * 0.01, FocusEngine.getElementColor(eff.getKey()));
                        eff.renderParticleFX(world, getX() + random.nextGaussian() * rad / 2.0, getY() + random.nextGaussian() * rad / 2.0, getZ() + random.nextGaussian() * rad / 2.0, random.nextGaussian() * 0.009999999776482582, random.nextGaussian() * 0.009999999776482582, random.nextGaussian() * 0.009999999776482582);
                    }
                }
            }
            else if (tickCount % 5 == 0) {
                long t = System.currentTimeMillis();
                ArrayList<Trajectory> trajectories = new ArrayList<Trajectory>();
                ArrayList<HitResult> targets = new ArrayList<HitResult>();
                List<Entity> list = EntityUtils.getEntitiesInRange(world, getX(), getY(), getZ(), this, Entity.class, rad);
                for (Entity e : list) {
                    if (e.isDeadOrDying()) {
                        continue;
                    }
                    if (e instanceof EntityFocusCloud) {
                        Vec3 v = e.position().subtract(position());
                        e.move(MoverType.SELF, new net.minecraft.world.phys.Vec3(v.x / 50.0, v.y / 50.0, v.z / 50.0));
                        ((EntityFocusCloud)e).pushOutOfBlocks(getX(), getY(), getZ());
                    }
                    if (!(e instanceof LivingEntity)) {
                        continue;
                    }
                    if (EntityFocusCloud.cooldownMap.containsKey(e.getId()) && EntityFocusCloud.cooldownMap.get(e.getId()) > t) {
                        continue;
                    }
                    EntityFocusCloud.cooldownMap.put((long)e.getId(), t + 2000L);
                    HitResult ray = null /* new HitResult removed */;
                    ray.getLocation() = e.position().add(0.0, e.getBbHeight() / 2.0f, 0.0);
                    Trajectory tra = new Trajectory(position(), position().subtractReverse(ray.getLocation()));
                    targets.add(ray);
                    trajectories.add(tra);
                }
                for (int a2 = 0; a2 < rad; ++a2) {
                    Vec3 dV = new Vec3(random.nextGaussian(), random.nextGaussian(), random.nextGaussian());
                    dV = dV.normalize();
                    HitResult br = level().rayTraceBlocks(position(), position().add(dV.scale(rad)));
                    long bl = 0L;
                    if (br != null) {
                        bl = br.getPos().asLong();
                        if (EntityFocusCloud.cooldownMap.containsKey(bl)) {
                            if (EntityFocusCloud.cooldownMap.get(bl) <= t) {
                                EntityFocusCloud.cooldownMap.remove(bl);
                            }
                            else {
                                br = null;
                            }
                        }
                    }
                    if (br != null) {
                        targets.add(br);
                        Trajectory tra2 = new Trajectory(position(), dV);
                        trajectories.add(tra2);
                        EntityFocusCloud.cooldownMap.put(bl, t + 2000L);
                    }
                }
                if (!targets.isEmpty()) {
                    ServerEvents.addRunnableServer(level(), new Runnable() {
                        @Override
                        public void run() {
                            FocusEngine.runFocusPackage(focusPackage.copy(getOwner()), trajectories.toArray(new Trajectory[0]), targets.toArray(new HitResult[0]));
                        }
                    }, 0);
                }
            }
        }
    }
    
    static {
        RADIUS = SynchedEntityData.defineId(EntityFocusCloud.class, EntityDataSerializers.FLOAT);
        EntityFocusCloud.cooldownMap = new HashMap<Long, Long>();
    }
}
