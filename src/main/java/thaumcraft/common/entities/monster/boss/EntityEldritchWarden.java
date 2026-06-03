package thaumcraft.common.entities.monster.boss;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.Level;
import thaumcraft.api.entities.EntitiesTC;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.entities.projectile.EntityEldritchOrb;
import thaumcraft.common.lib.SoundsTC;


public class EntityEldritchWarden extends EntityThaumcraftBoss implements RangedAttackMob, IEldritchMob
{
    protected ServerBossEvent bossInfo2;
    String[] titles;
    private static final EntityDataAccessor<Byte> NAME =
        SynchedEntityData.defineId(EntityEldritchWarden.class, EntityDataSerializers.BYTE);
    boolean fieldFrenzy;
    int fieldFrenzyCounter;
    boolean lastBlast;
    public float armLiftL;
    public float armLiftR;

    public EntityEldritchWarden(net.minecraft.world.entity.EntityType<? extends EntityEldritchWarden> type, Level world) {
        super(type, world);
        bossInfo2 = new ServerBossEvent(java.util.UUID.randomUUID(), net.minecraft.network.chat.Component.literal(""), BossEvent.BossBarColor.BLUE, BossEvent.BossBarOverlay.NOTCHED_10);
        titles = new String[] { "Aphoom-Zhah", "Basatan", "Chaugnar Faugn", "Mnomquah", "Nyogtha", "Oorn", "Shaikorth", "Rhan-Tegoth", "Rhogog", "Shudde M'ell", "Vulthoom", "Yag-Kosha", "Yibb-Tstll", "Zathog", "Zushakon" };
        fieldFrenzy = false;
        fieldFrenzyCounter = 0;
        lastBlast = false;
        armLiftL = 0.0f;
        armLiftR = 0.0f;
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.monster.Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 400.0)
            .add(Attributes.MOVEMENT_SPEED, 0.33)
            .add(Attributes.ATTACK_DAMAGE, 10.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new AILongRangeAttack(this, 3.0, 1.0, 20, 40, 24.0f));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1, false));
        goalSelector.addGoal(5, new MoveTowardsRestrictionGoal(this, 0.8));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, EntityCultist.class, true));
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(NAME, (byte) 0);
    }

    @Override
    public void generateName() {
        setCustomName(net.minecraft.network.chat.Component.literal(getTitle()));
        bossInfo.setName(getDisplayName());
        bossInfo2.setName(getDisplayName());
    }

    private String getTitle() {
        return titles[entityData.get(NAME) % titles.length];
    }

    private void setTitle(int title) {
        entityData.set(NAME, (byte)(title % titles.length));
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putByte("title", entityData.get(NAME));
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        setTitle(nbt.getByteOr("title", (byte)0));
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossInfo2.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossInfo2.removePlayer(player);
    }

    @Override
    protected void customServerAiStep(net.minecraft.server.level.ServerLevel serverLevel) {
        if (fieldFrenzyCounter == 0) {
            super.customServerAiStep(serverLevel);
        }
        int bh = (int)(getAttribute(Attributes.MAX_HEALTH).getBaseValue() * 0.66);
        if (invulnerableTime <= 0 && tickCount % 25 == 0 && getAbsorptionAmount() < bh) {
            setAbsorptionAmount(getAbsorptionAmount() + 1.0f);
        }
        bossInfo2.setProgress(getAbsorptionAmount() / Math.max(1, bh));
    }

    @Override
    public void tick() {
        if (getSpawnTimer() == 150) {
            level().broadcastEntityEvent(this, (byte) 18);
        }
        super.tick();
        if (level().isClientSide()) {
            if (armLiftL > 0.0f) armLiftL -= 0.05f;
            if (armLiftR > 0.0f) armLiftR -= 0.05f;
            float x = (float)(getX() + (getRandom().nextFloat() - getRandom().nextFloat()) * 0.2f);
            float z = (float)(getZ() + (getRandom().nextFloat() - getRandom().nextFloat()) * 0.2f);
            FXDispatcher.INSTANCE.wispFXEG(x, getY() + 0.25 * getBbHeight(), z, this);
        } else {
            if (!fieldFrenzy && thaumcraft.api.blocks.BlocksTC.effectSap != null) {
                for (int l = 0; l < 4; ++l) {
                    int ii = Mth.floor(getX() + (l % 2 * 2 - 1) * 0.25f);
                    int jj = Mth.floor(getY());
                    int kk = Mth.floor(getZ() + (l / 2 % 2 * 2 - 1) * 0.25f);
                    net.minecraft.core.BlockPos bpSap = new net.minecraft.core.BlockPos(ii, jj, kk);
                    if (level().isEmptyBlock(bpSap)) {
                        level().setBlockAndUpdate(bpSap, thaumcraft.api.blocks.BlocksTC.effectSap.defaultBlockState());
                    }
                }
            }
            if (fieldFrenzyCounter > 0) {
                --fieldFrenzyCounter;
                if (fieldFrenzyCounter == 0) fieldFrenzy = false;
            }
        }
    }

    @Override
    public boolean isInvulnerableTo(net.minecraft.server.level.ServerLevel serverLevel, DamageSource ds) {
        return fieldFrenzyCounter > 0 || super.isInvulnerableTo(serverLevel, ds);
    }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel serverLevel, DamageSource source, float damage) {
        if (isInvulnerableTo(serverLevel, source)) return false;
        boolean result = super.hurtServer(serverLevel, source, damage);
        if (result && !fieldFrenzy && getAbsorptionAmount() <= 0.0f) {
            fieldFrenzy = true;
            fieldFrenzyCounter = 150;
        }
        return result;
    }

    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor levelAccessor, DifficultyInstance diff, net.minecraft.world.entity.EntitySpawnReason reason, SpawnGroupData data) {
        spawnTimer = 150;
        setTitle(getRandom().nextInt(titles.length));
        setAbsorptionAmount(getAbsorptionAmount() + (float)(getAttribute(Attributes.MAX_HEALTH).getBaseValue() * 0.66));
        return super.finalizeSpawn(levelAccessor, diff, reason, data);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float f) {
        if (getRandom().nextFloat() > 0.2f) {
            EntityEldritchOrb blast = new EntityEldritchOrb(EntitiesTC.ELDRITCH_ORB.get(), level());
            blast.setOwner(this);
            lastBlast = !lastBlast;
            level().broadcastEntityEvent(this, (byte)(lastBlast ? 16 : 15));
            if (lastBlast) armLiftL = 1.0f; else armLiftR = 1.0f;
            int rr = lastBlast ? 90 : 180;
            double xx = Mth.cos((getYRot() + rr) % 360.0f / 180.0f * Mth.PI) * 0.5f;
            double yy = 0.13;
            double zz = Mth.sin((getYRot() + rr) % 360.0f / 180.0f * Mth.PI) * 0.5f;
            blast.setPos(getX() - xx, getEyeY() - 0.1 - yy, getZ() - zz);
            double d0 = target.getX() + target.getDeltaMovement().x - getX();
            double d2 = target.getY() - getY() - target.getBbHeight() / 2.0f;
            double d3 = target.getZ() + target.getDeltaMovement().z - getZ();
            blast.shoot(d0, d2, d3, 1.0f, 2.0f);
            playSound(SoundsTC.egattack, 2.0f, 1.0f + getRandom().nextFloat() * 0.1f);
            level().addFreshEntity(blast);
        } else if (getSensing().hasLineOfSight(target)) {
            target.push(
                -Mth.sin(getYRot() * Mth.PI / 180.0f) * 1.5f,
                0.1,
                Mth.cos(getYRot() * Mth.PI / 180.0f) * 1.5f);
            try {
                target.addEffect(new MobEffectInstance(MobEffects.WITHER, 400, 0));
                target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 400, 0));
            } catch (Exception ignored) {}
            playSound(SoundsTC.egscreech, 4.0f, 1.0f + getRandom().nextFloat() * 0.1f);
        }
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        return entity.getClass() != EntityEldritchGuardian.class && super.canAttack(entity);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundsTC.egidle;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsTC.egdeath;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 500;
    }

    @Override
    public void handleEntityEvent(byte event) {
        if (event == 15) {
            armLiftL = 0.5f;
        } else if (event == 16) {
            armLiftR = 0.5f;
        } else if (event == 17) {
            armLiftL = 0.9f;
            armLiftR = 0.9f;
        } else if (event == 18) {
            spawnTimer = 150;
        } else {
            super.handleEntityEvent(event);
        }
    }

    public void setSwingingArms(boolean swingingArms) {}
}
