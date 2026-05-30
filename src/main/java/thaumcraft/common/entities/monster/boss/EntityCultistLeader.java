package thaumcraft.common.entities.monster.boss;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.EquipmentSlot;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.ai.combat.AICultistHurtByTarget;
import thaumcraft.common.entities.ai.combat.AILongRangeAttack;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.entities.monster.cult.EntityCultistCleric;
import thaumcraft.common.entities.monster.cult.EntityCultistKnight;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;


public class EntityCultistLeader extends EntityThaumcraftBoss implements RangedAttackMob
{
    private static final EntityDataAccessor<Byte> NAME =
        SynchedEntityData.defineId(EntityCultistLeader.class, EntityDataSerializers.BYTE);
    String[] titles;

    @SuppressWarnings("unchecked")
    public EntityCultistLeader(Level level) {
        super(null, level);
        // Entity requires EntityType; use factory method
    }

    public EntityCultistLeader(net.minecraft.world.entity.EntityType<? extends EntityCultistLeader> type, Level world) {
        super(type, world);
        titles = new String[] { "Alberic", "Anselm", "Bastian", "Beturian", "Chabier", "Chorache", "Chuse", "Dodorol", "Ebardo", "Ferrando", "Fertus", "Guillen", "Larpe", "Obano", "Zelipe" };
        xpReward = 40;
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.monster.Monster.createMonsterAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.32)
            .add(Attributes.MAX_HEALTH, 150.0)
            .add(Attributes.ATTACK_DAMAGE, 5.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(2, new AILongRangeAttack(this, 16.0, 1.0, 30, 40, 24.0f));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.1, false));
        goalSelector.addGoal(6, new MoveTowardsRestrictionGoal(this, 0.8));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new AICultistHurtByTarget(this, true));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
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

    protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
        setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemsTC.crimsonPraetorHelm));
        setItemSlot(EquipmentSlot.CHEST, new ItemStack(ItemsTC.crimsonPraetorChest));
        setItemSlot(EquipmentSlot.LEGS, new ItemStack(ItemsTC.crimsonPraetorLegs));
        setItemSlot(EquipmentSlot.FEET, new ItemStack(ItemsTC.crimsonBoots));
        if (level().getDifficulty() == Difficulty.EASY) {
            setItemInHand(getUsedItemHand(), new ItemStack(ItemsTC.voidSword));
        } else {
            setItemInHand(getUsedItemHand(), new ItemStack(ItemsTC.crimsonBlade));
        }
    }

    @Override
    public boolean canAttack(LivingEntity entity) {
        Class<?> clazz = entity.getClass();
        return clazz != EntityCultistCleric.class && clazz != EntityCultistLeader.class
            && clazz != EntityCultistKnight.class && super.canAttack(entity);
    }

    // isAlliedTo(Entity) is final in Entity - ally logic handled by canAttack() override

    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor levelAccessor, DifficultyInstance diff, net.minecraft.world.entity.EntitySpawnReason reason, SpawnGroupData data) {
        setEquipmentBasedOnDifficulty(diff);
        setTitle(random.nextInt(titles.length));
        return super.finalizeSpawn(levelAccessor, diff, reason, data);
    }

    @Override
    protected void customServerAiStep(net.minecraft.server.level.ServerLevel serverLevel) {
        super.customServerAiStep(serverLevel);
        List<Entity> list = EntityUtils.getEntitiesInRange(serverLevel, getX(), getY(), getZ(), this, EntityCultist.class, 8.0);
        for (Entity e : list) {
            if (e instanceof EntityCultist cultist && !cultist.hasEffect(MobEffects.REGENERATION)) {
                cultist.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60, 1));
            }
        }
    }

    @Override
    public void performRangedAttack(LivingEntity target, float f) {
        // TODO: EntityGolemOrb spawn
    }

    public void spawnExplosionParticle() {
        if (level().isClientSide()) {
            for (int i = 0; i < 20; ++i) {
                double d0 = random.nextGaussian() * 0.05;
                double d2 = random.nextGaussian() * 0.05;
                double d3 = random.nextGaussian() * 0.05;
                FXDispatcher.INSTANCE.cultistSpawn(getX() + random.nextFloat() * getBbWidth() * 2.0f - getBbWidth() + d0 * 2.0, getY() + random.nextFloat() * getBbHeight() + d2 * 2.0, getZ() + random.nextFloat() * getBbWidth() * 2.0f - getBbWidth() + d3 * 2.0, d0, d2, d3);
            }
        } else {
            level().broadcastEntityEvent(this, (byte) 20);
        }
    }

    public void setSwingingArms(boolean swingingArms) {}
}
