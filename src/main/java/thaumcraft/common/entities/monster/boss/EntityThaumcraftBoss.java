package thaumcraft.common.entities.monster.boss;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.BossEvent;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.Level;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.lib.utils.EntityUtils;


public class EntityThaumcraftBoss extends Monster
{
    protected ServerBossEvent bossInfo;
    private static final EntityDataAccessor<Integer> AGGRO =
        SynchedEntityData.defineId(EntityThaumcraftBoss.class, EntityDataSerializers.INT);
    HashMap<Integer, Integer> aggro;
    int spawnTimer;

    public EntityThaumcraftBoss(net.minecraft.world.entity.EntityType<? extends EntityThaumcraftBoss> type, Level world) {
        super(type, world);
        bossInfo = new ServerBossEvent(getUUID(), getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
        bossInfo.setDarkenScreen(true);
        aggro = new HashMap<>();
        spawnTimer = 0;
        xpReward = 50;
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.getIntOr("HomeD", 0) != Integer.MIN_VALUE) {
            setHomeTo(new BlockPos(nbt.getIntOr("HomeX", 0), nbt.getIntOr("HomeY", 0), nbt.getIntOr("HomeZ", 0)), nbt.getIntOr("HomeD", 0));
        }
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        if (getHomePosition() != null && getHomeRadius() > 0) {
            nbt.putInt("HomeD", getHomeRadius());
            nbt.putInt("HomeX", getHomePosition().getX());
            nbt.putInt("HomeY", getHomePosition().getY());
            nbt.putInt("HomeZ", getHomePosition().getZ());
        }
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
            .add(Attributes.KNOCKBACK_RESISTANCE, 0.95)
            .add(Attributes.FOLLOW_RANGE, 40.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(AGGRO, 0);
    }

    @Override
    protected void customServerAiStep(net.minecraft.server.level.ServerLevel serverLevel) {
        if (getSpawnTimer() == 0) {
            super.customServerAiStep(serverLevel);
        }
        if (getTarget() != null && getTarget().isDeadOrDying()) {
            setTarget(null);
        }
        bossInfo.setProgress(getHealth() / getMaxHealth());
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

    public boolean isNonBoss() {
        return false;
    }

    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor levelAccessor, DifficultyInstance diff, net.minecraft.world.entity.EntitySpawnReason reason, SpawnGroupData data) {
        setHomeTo(blockPosition(), 24);
        generateName();
        bossInfo.setName(getDisplayName());
        return data;
    }

    public int getAnger() {
        return entityData.get(AGGRO);
    }

    public void setAnger(int par1) {
        entityData.set(AGGRO, par1);
    }

    public int getSpawnTimer() {
        return spawnTimer;
    }

    @Override
    public void tick() {
        super.tick();
        if (getSpawnTimer() > 0) {
            --spawnTimer;
        }
        if (getAnger() > 0) {
            setAnger(getAnger() - 1);
        }
        if (!level().isClientSide()) {
            if (tickCount % 30 == 0) {
                heal(1.0f);
            }
            if (getTarget() != null && tickCount % 20 == 0) {
                ArrayList<Integer> dl = new ArrayList<>();
                int players = 0;
                int hei = getTarget().getId();
                int ld;
                int ad = ld = (aggro.containsKey(hei) ? aggro.get(hei) : 0);
                Entity newTarget = null;
                for (Integer ei : aggro.keySet()) {
                    int ca = aggro.get(ei);
                    if (ca > ad + 25 && ca > ad * 1.1 && ca > ld) {
                        newTarget = level().getEntity(hei);
                        if (newTarget == null || newTarget.isRemoved() || distanceToSqr(newTarget.getX() + 0.5, newTarget.getY() + 0.5, newTarget.getZ() + 0.5) > 16384.0) {
                            dl.add(ei);
                        } else {
                            hei = ei;
                            ld = ei;
                            if (newTarget instanceof Player) {
                                ++players;
                            }
                        }
                    }
                }
                for (Integer ei : dl) {
                    aggro.remove(ei);
                }
                if (newTarget != null && hei != getTarget().getId()) {
                    setTarget((LivingEntity) newTarget);
                }
                // TODO: attribute scaling by player count (EntityUtils.HPBUFF/DMGBUFF)
            }
        }
    }

    @Override
    public boolean isInvulnerableTo(net.minecraft.server.level.ServerLevel serverLevel, DamageSource source) {
        return super.isInvulnerableTo(serverLevel, source) || getSpawnTimer() > 0;
    }

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel serverLevel, DamageSource source, float damage) {
        if (getSpawnTimer() > 0) return false;
        if (source.getEntity() instanceof LivingEntity attacker) {
            int targetId = attacker.getId();
            int ad = (int) damage + aggro.getOrDefault(targetId, 0);
            aggro.put(targetId, ad);
        }
        if (damage > 35.0f) {
            if (getAnger() == 0) {
                addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, (int)(damage / 15.0f)));
                addEffect(new MobEffectInstance(MobEffects.STRENGTH, 200, (int)(damage / 10.0f)));
                addEffect(new MobEffectInstance(MobEffects.HASTE, 200, (int)(damage / 40.0f)));
                setAnger(200);
                if (source.getEntity() instanceof Player p) {
                    p.sendOverlayMessage(Component.translatable(getName() + " " + I18n.get("tc.boss.enrage")));
                }
            }
            damage = 35.0f;
        }
        return super.hurtServer(serverLevel, source, damage);
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    public boolean requiresCustomPersistence() {
        return false;
    }

    @Override
    public boolean isAlliedTo(net.minecraft.world.scores.Team team) {
        return super.isAlliedTo(team);
    }

    public void generateName() {}
}
