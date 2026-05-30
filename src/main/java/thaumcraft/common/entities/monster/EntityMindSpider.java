package thaumcraft.common.entities.monster;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.Level;
import thaumcraft.api.entities.IEldritchMob;


public class EntityMindSpider extends Spider implements IEldritchMob
{
    private int lifeSpan;
    private static final EntityDataAccessor<Boolean> HARMLESS =
        SynchedEntityData.defineId(EntityMindSpider.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<String> VIEWER =
        SynchedEntityData.defineId(EntityMindSpider.class, EntityDataSerializers.STRING);

    public EntityMindSpider(net.minecraft.world.entity.EntityType<? extends EntityMindSpider> type, Level world) {
        super(type, world);
        lifeSpan = Integer.MAX_VALUE;
        xpReward = 1;
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.monster.Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 1.0)
            .add(Attributes.ATTACK_DAMAGE, 1.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(HARMLESS, false);
        builder.define(VIEWER, "");
    }

    public String getViewer() {
        return entityData.get(VIEWER);
    }

    public void setViewer(String player) {
        entityData.set(VIEWER, player);
    }

    public boolean isHarmless() {
        return entityData.get(HARMLESS);
    }

    public void setHarmless(boolean h) {
        if (h) lifeSpan = 1200;
        entityData.set(HARMLESS, h);
    }

    @Override
    public void tick() {
        super.tick();
        if (!world.isClientSide() && tickCount > lifeSpan) {
            discard();
        }
    }

    @Override
    public boolean doHurtTarget(net.minecraft.server.level.ServerLevel sl, Entity target) {
        return !isHarmless() && super.doHurtTarget(sl, target);
    }

    @Override
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput nbt) {
        super.readAdditionalSaveData(nbt);
        setHarmless(nbt.getBooleanOr("harmless", false));
        setViewer(nbt.getStringOr("viewer", ""));
    }

    @Override
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("harmless", isHarmless());
        nbt.putString("viewer", getViewer());
    }
}
