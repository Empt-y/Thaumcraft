package thaumcraft.common.entities.monster.cult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.Level;

import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.monster.boss.EntityCultistLeader;
import net.minecraft.world.entity.ai.navigation.PathNavigation;


public class EntityCultist extends Monster
{
    public static Identifier LOOT;
    
    public EntityCultist(net.minecraft.world.entity.EntityType<? extends EntityCultist> type, Level p_i1745_1_) {
        super(type, p_i1745_1_);
        // FIXME: setSize removed; dimensions set in EntityType builder
        xpReward = 10;
        ((GroundPathNavigation) getNavigation()).setCanOpenDoors(true);
        setDropChance(EquipmentSlot.CHEST, 0.05f);
        setDropChance(EquipmentSlot.FEET, 0.05f);
        setDropChance(EquipmentSlot.HEAD, 0.05f);
        setDropChance(EquipmentSlot.LEGS, 0.05f);
    }
    
    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.monster.Monster.createMonsterAttributes()
        .add(Attributes.FOLLOW_RANGE, 32.0) .add(Attributes.MOVEMENT_SPEED, 0.3) .add(Attributes.ATTACK_DAMAGE, 4.0);
    }
    
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }
    
    public boolean canPickUpLoot() {
        return false;
    }
    
    protected boolean checkAmbientSpawningRequirements() {
        return true;
    }
    
    protected Item getDropItem() {
        return net.minecraft.world.item.Items.AIR;
    }
    
    public net.minecraft.resources.ResourceKey<net.minecraft.world.level.storage.loot.LootTable> getDefaultLootTable() {
        return net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("minecraft", "empty"));
    }
    
    protected void setLoot(DifficultyInstance diff) {
    }
    
    protected void setEnchantmentBasedOnDifficulty(DifficultyInstance diff) {
    }
    
    public net.minecraft.world.entity.SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor level, DifficultyInstance diff, net.minecraft.world.entity.EntitySpawnReason reason, SpawnGroupData data) {
        setLoot(diff);
        setEnchantmentBasedOnDifficulty(diff);
        return super.finalizeSpawn(level, diff, reason, data);
    }
    
    public boolean requiresCustomPersistence() {
        return true;
    }
    
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("HomeD")) {
            setHomeTo(new BlockPos(nbt.getIntOr("HomeX", 0), nbt.getIntOr("HomeY", 0), nbt.getIntOr("HomeZ", 0)), nbt.getIntOr("HomeD", 0));
        }
    }
    
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        if (getHomePosition() != null && getHomeRadius() > 0.0f) {
            nbt.putInt("HomeD", (int) getHomeRadius());
            nbt.putInt("HomeX", getHomePosition().getX());
            nbt.putInt("HomeY", getHomePosition().getY());
            nbt.putInt("HomeZ", getHomePosition().getZ());
        }
    }
    
    public boolean isOnSameTeam(Entity el) {
        return el instanceof EntityCultist || el instanceof EntityCultistLeader;
    }
    
    @Override
    public boolean canAttack(net.minecraft.world.entity.LivingEntity entity) {
        Class<?> clazz = entity.getClass();
        return clazz != EntityCultistCleric.class && clazz != EntityCultistLeader.class && clazz != EntityCultistKnight.class && super.canAttack(entity);
    }
    
    public void spawnExplosionParticle() {
        if (level().isClientSide()) {
            for (int i = 0; i < 20; ++i) {
                double d0 = random.nextGaussian() * 0.05;
                double d2 = random.nextGaussian() * 0.05;
                double d3 = random.nextGaussian() * 0.05;
                double d4 = 2.0;
                FXDispatcher.INSTANCE.cultistSpawn(getX() + random.nextFloat() * getBbWidth() * 2.0f - getBbWidth() + d0 * d4, getY() + random.nextFloat() * getBbHeight() + d2 * d4, getZ() + random.nextFloat() * getBbWidth() * 2.0f - getBbWidth() + d3 * d4, d0, d2, d3);
            }
        }
        else {
            level().broadcastEntityEvent(this, (byte)20);
        }
    }
    
    static {
        LOOT = Identifier.fromNamespaceAndPath("thaumcraft", "cultist");
    }
}
