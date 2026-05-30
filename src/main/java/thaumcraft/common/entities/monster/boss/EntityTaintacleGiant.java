package thaumcraft.common.entities.monster.boss;
import net.minecraft.server.level.ServerBossEvent;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.network.chat.Component;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.Level;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.monster.tainted.EntityTaintacle;
import thaumcraft.common.lib.utils.EntityUtils;


public class EntityTaintacleGiant extends EntityTaintacle implements ITaintedMob, IEldritchMob
{
    protected ServerBossEvent bossInfo;
    private static final EntityDataAccessor<Integer> AGGRO =
        SynchedEntityData.defineId(EntityTaintacleGiant.class, EntityDataSerializers.INT);

    public EntityTaintacleGiant(net.minecraft.world.entity.EntityType<? extends EntityTaintacleGiant> type, Level world) {
        super(type, world);
        bossInfo = new ServerBossEvent(getUUID(), getDisplayName(), BossEvent.BossBarColor.PURPLE, BossEvent.BossBarOverlay.PROGRESS);
        bossInfo.setDarkenScreen(true);
        xpReward = 20;
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.monster.Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 175.0)
            .add(Attributes.ATTACK_DAMAGE, 9.0);
    }

    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor levelAccessor, DifficultyInstance diff, net.minecraft.world.entity.EntitySpawnReason reason, SpawnGroupData data) {
        EntityUtils.makeChampion(this, true);
        return data;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(AGGRO, 0);
    }

    public int getAnger() {
        return entityData.get(AGGRO);
    }

    public void setAnger(int par1) {
        entityData.set(AGGRO, par1);
    }

    @Override
    public void tick() {
        super.tick();
        if (getAnger() > 0) setAnger(getAnger() - 1);
        if (!level().isClientSide() && tickCount % 30 == 0) heal(1.0f);
    }

    @Override
    protected void customServerAiStep(net.minecraft.server.level.ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);
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

    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel serverLevel, DamageSource source, float damage) {
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
    public boolean requiresCustomPersistence() {
        return false;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    public boolean isNonBoss() {
        return false;
    }
}
