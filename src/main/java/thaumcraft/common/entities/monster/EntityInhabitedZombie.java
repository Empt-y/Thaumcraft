package thaumcraft.common.entities.monster;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.lib.SoundsTC;
import net.minecraft.world.entity.monster.zombie.Zombie;


public class EntityInhabitedZombie extends Zombie implements IEldritchMob
{
    public EntityInhabitedZombie(net.minecraft.world.entity.EntityType<? extends EntityInhabitedZombie> type, Level world) {
        super(type, world);
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.monster.Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 30.0)
            .add(Attributes.ATTACK_DAMAGE, 5.0);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, EntityCultist.class, true));
    }

    @Override
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor levelAccessor, DifficultyInstance diff, net.minecraft.world.entity.EntitySpawnReason reason, SpawnGroupData data) {
        float d = (level().getDifficulty() == Difficulty.HARD) ? 0.9f : 0.6f;
        setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemsTC.crimsonPlateHelm));
        if (net.minecraft.util.RandomSource.create().nextFloat() <= d) setItemSlot(EquipmentSlot.CHEST, new ItemStack(ItemsTC.crimsonPlateChest));
        if (net.minecraft.util.RandomSource.create().nextFloat() <= d) setItemSlot(EquipmentSlot.LEGS, new ItemStack(ItemsTC.crimsonPlateLegs));
        return super.finalizeSpawn(levelAccessor, diff, reason, data);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundsTC.crabtalk;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource ds) {
        return SoundEvents.GENERIC_HURT;
    }
}
