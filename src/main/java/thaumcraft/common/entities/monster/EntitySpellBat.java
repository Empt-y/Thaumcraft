package thaumcraft.common.entities.monster;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusPackage;


public class EntitySpellBat extends Monster
{
    private BlockPos currentFlightTarget;
    public LivingEntity owner;
    FocusPackage focusPackage;
    private UUID ownerUniqueId;
    private static final EntityDataAccessor<Boolean> FRIENDLY =
        SynchedEntityData.defineId(EntitySpellBat.class, EntityDataSerializers.BOOLEAN);
    public int damBonus;
    private int attackTime;
    FocusEffect[] effects;
    public int color;

    public EntitySpellBat(net.minecraft.world.entity.EntityType<? extends EntitySpellBat> type, Level world) {
        super(type, world);
        owner = null;
        damBonus = 0;
        effects = null;
        color = 16777215;
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 5.0)
            .add(Attributes.ATTACK_DAMAGE, 1.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(FRIENDLY, false);
    }

    public boolean getIsFriendly() {
        return entityData.get(FRIENDLY);
    }

    public void setIsFriendly(boolean par1) {
        entityData.set(FRIENDLY, par1);
    }

    public LivingEntity getOwner() {
        if (ownerUniqueId != null) {
            Entity e = level().getPlayerByUUID(ownerUniqueId);
            if (e instanceof LivingEntity le) return le;
        }
        return owner;
    }

    public void setOwner(LivingEntity e) {
        owner = e;
        if (e != null) ownerUniqueId = e.getUUID();
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        setDeltaMovement(getDeltaMovement().multiply(1.0, 0.6, 1.0));
        if (attackTime > 0) --attackTime;

        LivingEntity target = getTarget();
        if (target == null) {
            if (currentFlightTarget == null || random.nextInt(30) == 0 || distanceToSqr(currentFlightTarget.getX() + 0.5, currentFlightTarget.getY(), currentFlightTarget.getZ() + 0.5) < 4.0) {
                currentFlightTarget = new BlockPos((int)getX() + random.nextInt(7) - random.nextInt(7), (int)getY() + random.nextInt(6) - 2, (int)getZ() + random.nextInt(7) - random.nextInt(7));
            }
            double vx = currentFlightTarget.getX() + 0.5 - getX();
            double vy = currentFlightTarget.getY() + 0.1 - getY();
            double vz = currentFlightTarget.getZ() + 0.5 - getZ();
            setDeltaMovement(
                getDeltaMovement().x + (Math.signum(vx) * 0.5 - getDeltaMovement().x) * 0.1,
                getDeltaMovement().y + (Math.signum(vy) * 0.7 - getDeltaMovement().y) * 0.1,
                getDeltaMovement().z + (Math.signum(vz) * 0.5 - getDeltaMovement().z) * 0.1);
        } else {
            double vx = target.getX() - getX();
            double vy = target.getY() + target.getEyeHeight() * 0.66f - getY();
            double vz = target.getZ() - getZ();
            setDeltaMovement(
                getDeltaMovement().x + (Math.signum(vx) * 0.5 - getDeltaMovement().x) * 0.1,
                getDeltaMovement().y + (Math.signum(vy) * 0.7 - getDeltaMovement().y) * 0.1,
                getDeltaMovement().z + (Math.signum(vz) * 0.5 - getDeltaMovement().z) * 0.1);
            if (attackTime <= 0 && distanceTo(target) < 3.0f && getSensing().hasLineOfSight(target) && level() instanceof net.minecraft.server.level.ServerLevel sl) {
                attackTime = 20;
                doHurtTarget(sl, target);
            }
        }
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        setIsFriendly(nbt.getBooleanOr("friendly", false));
        damBonus = nbt.getByteOr("damBonus", (byte)0);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("friendly", getIsFriendly());
        nbt.putByte("damBonus", (byte) damBonus);
    }

    @Override
    public float getSoundVolume() {
        return 0.1f;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.BAT_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.BAT_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BAT_DEATH;
    }
}
