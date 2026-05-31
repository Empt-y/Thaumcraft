package thaumcraft.common.entities.monster.boss;
import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.BossEvent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.level.Level;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.entities.monster.cult.EntityCultistCleric;
import thaumcraft.common.entities.monster.cult.EntityCultistKnight;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;


public class EntityCultistPortalGreater extends Monster
{
    protected ServerBossEvent bossInfo;
    int stage;
    int stagecounter;
    public int pulse;

    public EntityCultistPortalGreater(net.minecraft.world.entity.EntityType<? extends EntityCultistPortalGreater> type, Level world) {
        super(type, world);
        bossInfo = new ServerBossEvent(getUUID(), getDisplayName(), BossEvent.BossBarColor.RED, BossEvent.BossBarOverlay.NOTCHED_6);
        bossInfo.setDarkenScreen(true);
        stage = 0;
        stagecounter = 200;
        pulse = 0;
        xpReward = 30;
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("stage", stage);
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        stage = nbt.getIntOr("stage", 0);
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 500.0)
            .add(Attributes.ATTACK_DAMAGE, 0.0)
            .add(Attributes.KNOCKBACK_RESISTANCE, 1.0);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public void move(MoverType mt, net.minecraft.world.phys.Vec3 vec) {}

    @Override
    public boolean shouldRenderAtSqrDistance(double dist) {
        return dist < 4096.0;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide()) {
            if (stagecounter > 0) {
                --stagecounter;
                if (stagecounter == 160 && stage == 0) {
                    level().broadcastEntityEvent(this, (byte) 16);
                    // TODO: banner placement and network packets
                }
                if (stagecounter > 20 && stagecounter < 150 && stage == 0 && stagecounter % 13 == 0) {
                    int a = (int) getX() + getRandom().nextInt(5) - getRandom().nextInt(5);
                    int b = (int) getZ() + getRandom().nextInt(5) - getRandom().nextInt(5);
                    BlockPos bp2 = new BlockPos(a, (int) getY(), b);
                    if (a != (int) getX() && b != (int) getZ() && level().isEmptyBlock(bp2)) {
                        level().broadcastEntityEvent(this, (byte) 16);
                        float rr = level().getRandom().nextFloat();
                        int md = (rr < 0.05f) ? 2 : ((rr < 0.2f) ? 1 : 0);
                        Block bb = BlocksTC.lootCrateCommon;
                        switch (md) {
                            case 1: bb = BlocksTC.lootCrateUncommon; break;
                            case 2: bb = BlocksTC.lootCrateRare; break;
                        }
                        level().setBlock(bp2, bb.defaultBlockState(), 3);
                    }
                }
            } else if (level().getNearestPlayer(getX(), getY(), getZ(), 48.0, false) != null) {
                level().broadcastEntityEvent(this, (byte) 16);
                switch (stage) {
                    case 0: case 1: case 2: case 3: case 4:
                        stagecounter = 15 + getRandom().nextInt(Math.max(1, 10 - stage)) - stage;
                        spawnMinions();
                        break;
                    case 12:
                        stagecounter = 50 + getTiming() * 2 + getRandom().nextInt(50);
                        spawnBoss();
                        break;
                    default:
                        int t = getTiming();
                        stagecounter = t + getRandom().nextInt(5 + t / 3);
                        spawnMinions();
                        break;
                }
                ++stage;
            } else {
                stagecounter = 30 + getRandom().nextInt(30);
            }
            if (stage < 12) {
                heal(1.0f);
            }
        }
        if (pulse > 0) {
            --pulse;
        }
    }

    int getTiming() {
        List<EntityCultist> l = EntityUtils.getEntitiesInRange(level(), getX(), getY(), getZ(), this, EntityCultist.class, 32.0);
        return l.size() * 20;
    }

    void spawnMinions() {
        EntityCultist cultist;
        if (getRandom().nextFloat() > 0.33f) {
            cultist = new EntityCultistKnight(level());
        } else {
            cultist = new EntityCultistCleric(level());
        }
        cultist.setPos(getX() + getRandom().nextFloat() - getRandom().nextFloat(), getY() + 0.25, getZ() + getRandom().nextFloat() - getRandom().nextFloat());
        cultist.setHomeTo(blockPosition(), 32);
        level().addFreshEntity(cultist);
        cultist.spawnExplosionParticle();
        cultist.playSound(SoundsTC.wandfail, 1.0f, 1.0f);
        if (stage > 12 && level() instanceof net.minecraft.server.level.ServerLevel sl) {
            hurtServer(sl, sl.damageSources().fellOutOfWorld(), (float)(5 + getRandom().nextInt(5)));
        }
    }

    void spawnBoss() {
        EntityCultistLeader cultist = new EntityCultistLeader(level());
        cultist.setPos(getX() + getRandom().nextFloat() - getRandom().nextFloat(), getY() + 0.25, getZ() + getRandom().nextFloat() - getRandom().nextFloat());
        cultist.setHomeTo(blockPosition(), 32);
        level().addFreshEntity(cultist);
        cultist.spawnExplosionParticle();
        cultist.playSound(SoundsTC.wandfail, 1.0f, 1.0f);
    }

    @Override
    public void playerTouch(Player p) {
        if (distanceToSqr(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5) < 3.0 && level() instanceof net.minecraft.server.level.ServerLevel sl) {
            p.hurtServer(sl, sl.damageSources().indirectMagic(this, this), 8.0f);
            playSound(SoundsTC.zap, 1.0f, (getRandom().nextFloat() - getRandom().nextFloat()) * 0.1f + 1.0f);
        }
    }

    @Override
    public float getSoundVolume() {
        return 0.75f;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 540;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundsTC.monolith;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource ds) {
        return SoundsTC.zap;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsTC.shock;
    }

    @Override
    public boolean addEffect(MobEffectInstance effect, Entity source) {
        return false;
    }

    @Override
    public void die(DamageSource source) {
        if (!level().isClientSide() && level() instanceof net.minecraft.server.level.ServerLevel sl) {
            sl.explode(this, getX(), getY(), getZ(), 2.0f, Level.ExplosionInteraction.NONE);
        }
        super.die(source);
    }

    @Override
    public void startSeenByPlayer(ServerPlayer player) {
        super.startSeenByPlayer(player);
        bossInfo.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        bossInfo.removePlayer(player);
    }

    @Override
    public void handleEntityEvent(byte msg) {
        if (msg == 16) {
            pulse = 10;
        } else {
            super.handleEntityEvent(msg);
        }
    }

    @Override
    protected void customServerAiStep(net.minecraft.server.level.ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);
        bossInfo.setProgress(getHealth() / getMaxHealth());
    }
}
