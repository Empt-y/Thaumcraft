package thaumcraft.common.entities.projectile;

import java.util.HashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import io.netty.buffer.ByteBuf;


public class EntityGrapple extends ThrowableProjectile
{
    public InteractionHand hand;
    LivingEntity cthrower;
    boolean p;
    boolean boost;
    int prevDist;
    int count;
    boolean added;
    public float ampl;
    public static HashMap<Integer, Integer> grapples;
    
    public EntityGrapple(net.minecraft.world.entity.EntityType<? extends EntityGrapple> type, Level par1World) {
        super(type, par1World);
        hand = InteractionHand.MAIN_HAND;
        p = false;
        prevDist = 0;
        count = 0;
        added = false;
        ampl = 0.0f;
        // FIXME: setSize removed; dimensions set in EntityType builder
    }
    
    public boolean isInRangeToRenderDist(double distance) {
        return distance < 4096.0;
    }
    
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity, 0.0f);
    }
    
    public EntityGrapple(Level par1World, LivingEntity par2Mob, InteractionHand hand) {
        super(null, par1World);
        setOwner(par2Mob);
        setPos(par2Mob.getX(), par2Mob.getEyeY() - 0.1, par2Mob.getZ());
        this.hand = InteractionHand.MAIN_HAND;
        p = false;
        prevDist = 0;
        count = 0;
        added = false;
        ampl = 0.0f;
        // FIXME: setSize removed; dimensions set in EntityType builder
        this.hand = hand;
    }
    
    public EntityGrapple(Level par1World, double par2, double par4, double par6) {
        super(null, par1World);
        hand = InteractionHand.MAIN_HAND;
        p = false;
        prevDist = 0;
        count = 0;
        added = false;
        ampl = 0.0f;
        // FIXME: setSize removed; dimensions set in EntityType builder
    }
    
    public LivingEntity getOwner() {
        if (cthrower != null) {
            return cthrower;
        }
        net.minecraft.world.entity.Entity owner = super.getOwner();
        return (owner instanceof LivingEntity le) ? le : null;
    }
    
    protected float getGravityVelocity() {
        return getPulling() ? 0.0f : 0.03f;
    }
    
    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
    }
    
    public void setPulling() {
        p = true;
    }
    
    public boolean getPulling() {
        return p;
    }
    
    public void tick() {
        super.tick();
        if (!getPulling() && !isRemoved() && (tickCount > 30 || getOwner() == null)) {
            if (getOwner() != null) {
                EntityGrapple.grapples.remove(getOwner().getId());
            }
            discard();
        }
        if (getOwner() != null) {
            if (!level().isClientSide() && !isRemoved() && !added) {
                if (EntityGrapple.grapples.containsKey(getOwner().getId())) {
                    int ii = EntityGrapple.grapples.get(getOwner().getId());
                    if (ii != getId()) {
                        Entity e = level().getEntity(ii);
                        if (e != null) {
                            e.discard();
                        }
                    }
                }
                EntityGrapple.grapples.put(getOwner().getId(), getId());
                added = true;
            }
            try {
                if (getOwner() != null && EntityGrapple.grapples.containsKey(getOwner().getId()) && EntityGrapple.grapples.get(getOwner().getId()) != getId()) {
                    discard();
                }
            }
            catch (Exception ex) {}
            double dis = getOwner().distanceTo(this);
            if (getOwner() != null && getPulling() && !isRemoved()) {
                if (getOwner().isCrouching()) {
                    EntityGrapple.grapples.remove(getOwner().getId());
                    discard();
                }
                else {
                    if (!level().isClientSide() && getOwner() instanceof net.minecraft.server.level.ServerPlayer) {
                        ((net.minecraft.server.level.ServerPlayer) getOwner()).connection.floatingTickCount = 0;
                    }
                    getOwner().fallDistance = 0.0f;
                    double mx = getX() - getOwner().getX();
                    double my = getY() - getOwner().getY();
                    double mz = getZ() - getOwner().getZ();
                    double dd = dis;
                    if (dis < 8.0) {
                        dd = dis * (8.0 - dis);
                    }
                    dd = Math.max(1.0E-9, dd);
                    mx /= dd * 5.0;
                    my /= dd * 5.0;
                    mz /= dd * 5.0;
                    Vec3 v2 = new Vec3(mx, my, mz);
                    if (v2.length() > 0.25) {
                        v2 = v2.normalize();
                        mx = v2.x / 4.0;
                        my = v2.y / 4.0;
                        mz = v2.z / 4.0;
                    }
                    LivingEntity thrower = getOwner();
                    thrower.setDeltaMovement(thrower.getDeltaMovement().x + mx, thrower.getDeltaMovement().y, thrower.getDeltaMovement().z);
                    LivingEntity thrower2 = getOwner();
                    thrower2.setDeltaMovement(thrower2.getDeltaMovement().x, thrower2.getDeltaMovement().y + my + 0.033, thrower2.getDeltaMovement().z);
                    LivingEntity thrower3 = getOwner();
                    thrower3.setDeltaMovement(thrower3.getDeltaMovement().x, thrower3.getDeltaMovement().y, thrower3.getDeltaMovement().z + mz);
                    if (!boost) {
                        LivingEntity thrower4 = getOwner();
                        thrower4.setDeltaMovement(thrower4.getDeltaMovement().x, thrower4.getDeltaMovement().y + 0.4000000059604645, thrower4.getDeltaMovement().z);
                        boost = true;
                    }
                    int d = (int)(dis / 2.0);
                    if (d == prevDist) {
                        ++count;
                    }
                    else {
                        count = 0;
                    }
                    prevDist = d;
                }
            }
            if (level().isClientSide()) {
                if (!getPulling()) {
                    ampl += 0.02f;
                }
                else {
                    ampl *= 0.66f;
                }
            }
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    public void handleStatusUpdate(byte id) {
        if (id == 6) {
            setPulling();
            setDeltaMovement(0.0, getDeltaMovement().y, getDeltaMovement().z);
            setDeltaMovement(getDeltaMovement().x, 0.0, getDeltaMovement().z);
            setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, 0.0);
        }
    }
    
    protected void onImpact(HitResult mop) {
        if (!level().isClientSide()) {
            setPulling();
            setDeltaMovement(0.0, getDeltaMovement().y, getDeltaMovement().z);
            setDeltaMovement(getDeltaMovement().x, 0.0, getDeltaMovement().z);
            setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, 0.0);
            setPos(mop.getLocation().x, getY(), getZ());
            setPos(getX(), mop.getLocation().y, getZ());
            setPos(getX(), getY(), mop.getLocation().z);
            level().broadcastEntityEvent(this, (byte)6);
        }
    }
    
    static {
        EntityGrapple.grapples = new HashMap<Integer, Integer>();
    }
}
