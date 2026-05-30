package thaumcraft.common.entities;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.server.level.ServerLevel;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusMediumRoot;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.IFocusElement;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.world.taint.TaintHelper;
import thaumcraft.common.entities.monster.EntityWisp;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeedPrime;
import thaumcraft.common.items.casters.foci.FocusEffectFlux;
import thaumcraft.common.items.casters.foci.FocusMediumCloud;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.RandomItemChooser;
import thaumcraft.common.world.aura.AuraHandler;


public class EntityFluxRift extends Entity
{
    private static final EntityDataAccessor<Integer> SEED;
    private static final EntityDataAccessor<Integer> SIZE;
    private static final EntityDataAccessor<Float> STABILITY;
    private static final EntityDataAccessor<Boolean> COLLAPSE;
    int maxSize;
    int lastSize;
    static ArrayList<RandomItemChooser.Item> events;
    public ArrayList<Vec3> points;
    public ArrayList<Float> pointsWidth;

    public EntityFluxRift(EntityType<?> type, Level par1World) {
        super(type, par1World);
        maxSize = 0;
        lastSize = -1;
        points = new ArrayList<Vec3>();
        pointsWidth = new ArrayList<Float>();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SEED, 0);
        builder.define(SIZE, 5);
        builder.define(STABILITY, 0.0f);
        builder.define(COLLAPSE, false);
    }

    public boolean getCollapse() {
        return entityData.get(COLLAPSE);
    }

    public void setCollapse(boolean b) {
        if (b) maxSize = getRiftSize();
        entityData.set(COLLAPSE, b);
    }

    public float getRiftStability() {
        return entityData.get(STABILITY);
    }

    public void setRiftStability(float s) {
        s = Math.max(-100.0f, Math.min(100.0f, s));
        entityData.set(STABILITY, s);
    }

    public int getRiftSize() {
        return entityData.get(SIZE);
    }

    public void setRiftSize(int s) {
        entityData.set(SIZE, s);
        calcBounds();
    }

    private void calcBounds() {
        calcSteps(points, pointsWidth, new Random(getRiftSeed()));
        lastSize = getRiftSize();
        if (points.isEmpty()) return;
        double x0 = Double.MAX_VALUE, y0 = Double.MAX_VALUE, z0 = Double.MAX_VALUE;
        double x2 = -Double.MAX_VALUE, y2 = -Double.MAX_VALUE, z2 = -Double.MAX_VALUE;
        for (Vec3 v : points) {
            if (v.x < x0) x0 = v.x;
            if (v.x > x2) x2 = v.x;
            if (v.y < y0) y0 = v.y;
            if (v.y > y2) y2 = v.y;
            if (v.z < z0) z0 = v.z;
            if (v.z > z2) z2 = v.z;
        }
        setBoundingBox(new AABB(getX() + x0, getY() + y0, getZ() + z0, getX() + x2, getY() + y2, getZ() + z2));
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        if (entityData != null) calcBounds();
    }

    public int getRiftSeed() {
        return entityData.get(SEED);
    }

    public void setRiftSeed(int s) {
        entityData.set(SEED, s);
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        output.putInt("MaxSize", maxSize);
        output.putInt("RiftSize", getRiftSize());
        output.putInt("RiftSeed", getRiftSeed());
        output.putFloat("Stability", getRiftStability());
        output.putBoolean("collapse", getCollapse());
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        maxSize = input.getIntOr("MaxSize", 0);
        setRiftSize(input.getIntOr("RiftSize", 0));
        setRiftSeed(input.getIntOr("RiftSeed", 0));
        setRiftStability(input.getFloatOr("Stability", 0f));
        setCollapse(input.getBooleanOr("collapse", false));
    }

    @Override
    public void move(MoverType type, Vec3 pos) {
        // rifts don't move
    }

    @Override
    public void tick() {
        super.tick();
        if (lastSize != getRiftSize()) {
            calcBounds();
        }
        if (!level().isClientSide()) {
            if (getRiftSeed() == 0) {
                setRiftSeed(random.nextInt());
            }
            // entity interaction logic stubbed — uses removed APIs
            if (points.size() < 3 && !getCollapse()) {
                setCollapse(true);
            }
            if (getCollapse()) {
                setRiftSize(getRiftSize() - 1);
                if (random.nextBoolean()) {
                    AuraHelper.addVis(level(), blockPosition(), 1.0f);
                } else {
                    AuraHelper.polluteAura(level(), blockPosition(), 1.0f, false);
                }
                if (getRiftSize() <= 1) {
                    completeCollapse();
                    return;
                }
            }
            if (tickCount % 120 == 0) {
                setRiftStability(getRiftStability() - 0.2f);
            }
            if (tickCount % 600 == getId() % 600) {
                float taint = AuraHandler.getFlux(level(), blockPosition());
                double size = Math.sqrt(getRiftSize() * 2);
                if (taint >= size && getRiftSize() < 100 && getStability() != EnumStability.VERY_STABLE) {
                    AuraHandler.drainFlux(level(), blockPosition(), (float)size, false);
                    setRiftSize(getRiftSize() + 1);
                }
                if (getRiftStability() < 0.0f && random.nextInt(1000) < Math.abs(getRiftStability()) + getRiftSize()) {
                    executeRiftEvent();
                }
            }
            if (!isRemoved() && tickCount % 300 == 0) {
                playSound(SoundsTC.evilportal, (float)(0.15 + random.nextGaussian() * 0.066), (float)(0.75 + random.nextGaussian() * 0.1));
            }
        } else {
            if (!points.isEmpty() && points.size() > 2 && !getCollapse() && getRiftStability() < 0.0f && random.nextInt(150) < Math.abs(getRiftStability())) {
                int pi = 1 + random.nextInt(points.size() - 2);
                Vec3 v1 = points.get(pi).add(getX(), getY(), getZ());
                FXDispatcher.INSTANCE.drawCurlyWisp(v1.x, v1.y, v1.z, 0, 0, 0, 0.1f + pointsWidth.get(pi) * 3.0f, 1, 1, 1, 0.25f, null, 1, 0, 0);
            }
            if (!points.isEmpty() && points.size() > 2 && getCollapse()) {
                int pi = 1 + random.nextInt(points.size() - 2);
                Vec3 v1 = points.get(pi).add(getX(), getY(), getZ());
                FXDispatcher.INSTANCE.drawCurlyWisp(v1.x, v1.y, v1.z, 0, 0, 0, 0.1f + pointsWidth.get(pi) * 3.0f, 1, 0.3f + random.nextFloat() * 0.1f, 0.3f + random.nextFloat() * 0.1f, 0.4f, null, 1, 0, 0);
            }
        }
    }

    public static void createRift(Level world, BlockPos pos) {
        // stubbed — uses removed world APIs (getPrecipitationHeight, provider, etc.)
    }

    private void executeRiftEvent() {
        RandomItemChooser ric = new RandomItemChooser();
        FluxEventEntry ei = (FluxEventEntry)ric.chooseOnWeight(EntityFluxRift.events);
        if (ei == null) return;
        if (!ei.nearTaintAllowed && TaintHelper.isNearTaintSeed(level(), blockPosition())) return;
        boolean didit = false;
        switch (ei.event) {
            case 4: {
                setCollapse(true);
                didit = true;
                break;
            }
            // other cases stubbed — use removed entity spawn/focus APIs
        }
        if (didit) {
            setRiftStability(getRiftStability() + ei.cost);
        }
    }

    private void calcSteps(ArrayList<Vec3> pp, ArrayList<Float> ww, Random rr) {
        pp.clear();
        ww.clear();
        // stubbed — Vec3.rotatePitch/rotateYaw removed in modern MC
        // minimal bounding box for a size-5 rift
        int size = Math.max(1, getRiftSize());
        float half = size * 0.1f;
        pp.add(new Vec3(-half, -half, 0));
        pp.add(new Vec3(half, half, 0));
        ww.add(size / 300.0f);
        ww.add(0.0f);
    }

    public void addStability() {
        setRiftStability(getRiftStability() + 0.125f);
    }

    public EnumStability getStability() {
        if (getRiftStability() > 50.0f) return EnumStability.VERY_STABLE;
        if (getRiftStability() >= 0.0f) return EnumStability.STABLE;
        if (getRiftStability() > -25.0f) return EnumStability.UNSTABLE;
        return EnumStability.VERY_UNSTABLE;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isOnFire() { return false; }

    private void completeCollapse() {
        int qq = (int)Math.sqrt(maxSize);
        if (level() instanceof ServerLevel sl) {
            if (random.nextInt(100) < qq) {
                spawnAtLocation(sl, new ItemStack(ItemsTC.primordialPearl, 4 + random.nextInt(4)));
            }
            for (int a = 0; a < qq; ++a) {
                spawnAtLocation(sl, new ItemStack(ItemsTC.voidSeed));
            }
        }
        List<LivingEntity> list = EntityUtils.getEntitiesInRange(level(), getX(), getY(), getZ(), this, LivingEntity.class, 32.0);
        switch (getStability()) {
            case VERY_UNSTABLE:
                for (LivingEntity p : list) {
                    int w = (int)((1.0 - p.distanceToSqr(this.getX() + 0.5, this.getY() + 0.5, this.getZ() + 0.5) / 32.0) * 120.0);
                    if (w > 0 && PotionFluxTaint.instance != null) p.addEffect(new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionFluxTaint.instance), w * 20, 0));
                }
            case UNSTABLE:
                for (LivingEntity p : list) {
                    int w = (int)((1.0 - p.distanceToSqr(this.getX() + 0.5, this.getY() + 0.5, this.getZ() + 0.5) / 32.0) * 300.0);
                    if (w > 0) p.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, w * 20, 0));
                }
            case STABLE:
                for (LivingEntity p : list) {
                    if (p instanceof Player pl) {
                        int w = (int)((1.0 - p.distanceToSqr(this.getX() + 0.5, this.getY() + 0.5, this.getZ() + 0.5) / 32.0) * 25.0);
                        if (w > 0) {
                            ThaumcraftApi.internalMethods.addWarpToPlayer(pl, w, IPlayerWarp.EnumWarpType.NORMAL);
                            ThaumcraftApi.internalMethods.addWarpToPlayer(pl, w, IPlayerWarp.EnumWarpType.TEMPORARY);
                        }
                    }
                }
                break;
        }
        discard();
    }

    static {
        SEED = SynchedEntityData.defineId(EntityFluxRift.class, EntityDataSerializers.INT);
        SIZE = SynchedEntityData.defineId(EntityFluxRift.class, EntityDataSerializers.INT);
        STABILITY = SynchedEntityData.defineId(EntityFluxRift.class, EntityDataSerializers.FLOAT);
        COLLAPSE = SynchedEntityData.defineId(EntityFluxRift.class, EntityDataSerializers.BOOLEAN);
        (EntityFluxRift.events = new ArrayList<RandomItemChooser.Item>()).add(new FluxEventEntry(0, 50, 5, true));
        EntityFluxRift.events.add(new FluxEventEntry(1, 10, 0, false));
        EntityFluxRift.events.add(new FluxEventEntry(2, 20, 10, true));
        EntityFluxRift.events.add(new FluxEventEntry(3, 20, 10, true));
        EntityFluxRift.events.add(new FluxEventEntry(4, 1, 0, true));
    }

    static class FluxEventEntry implements RandomItemChooser.Item
    {
        int weight;
        int event;
        int cost;
        boolean nearTaintAllowed;

        protected FluxEventEntry(int event, int weight, int cost, boolean nearTaintAllowed) {
            this.weight = weight;
            this.event = event;
            this.cost = cost;
            this.nearTaintAllowed = nearTaintAllowed;
        }

        @Override
        public double getWeight() { return weight; }
    }

    public enum EnumStability { VERY_STABLE, STABLE, UNSTABLE, VERY_UNSTABLE }
}
