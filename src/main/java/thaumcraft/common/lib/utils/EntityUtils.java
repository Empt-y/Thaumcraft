package thaumcraft.common.lib.utils;
import net.minecraft.world.Container;
// baubles import removed
// baubles import removed
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
// FML FMLCommonHandler removed
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.common.entities.EntitySpecialItem;
import thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss;
import thaumcraft.common.entities.monster.mods.ChampionModifier;


public class EntityUtils
{
    public static AttributeModifier CHAMPION_HEALTH;
    public static AttributeModifier CHAMPION_DAMAGE;
    public static AttributeModifier BOLDBUFF;
    public static AttributeModifier MIGHTYBUFF;
    public static AttributeModifier[] HPBUFF;
    public static AttributeModifier[] DMGBUFF;

    public static boolean isFriendly(Entity source, Entity target) {
        if (source == null || target == null) {
            return false;
        }
        if (source.getId() == target.getId()) {
            return true;
        }
        // isRidingOrBeingRiddenBy removed; check via hasPassenger and isPassenger
        if (source.hasPassenger(target) || target.hasPassenger(source) || source.isPassenger() && source.getVehicle() == target || target.isPassenger() && target.getVehicle() == source) {
            return true;
        }
        // isOnSameTeam → isAlliedTo
        if (source.isAlliedTo(target)) {
            return true;
        }
        // IEntityOwnable removed — skip owner check
        try {
            if (!target.level().isClientSide() && target instanceof Player) {
                return true;
            }
        }
        catch (Exception ex) {}
        return false;
    }

    public static Vec3 posToHand(Entity e, InteractionHand hand) {
        double px = e.getX();
        double py = e.getBoundingBox().minY + e.getBbHeight() / 2.0f + 0.25;
        double pz = e.getZ();
        float m = (hand == InteractionHand.MAIN_HAND) ? 0.0f : 180.0f;
        px += -Mth.cos((e.getYRot() + m) / 180.0f * 3.141593f) * 0.3f;
        pz += -Mth.sin((e.getYRot() + m) / 180.0f * 3.141593f) * 0.3f;
        // getLook(partialTick) → getLookAngle()
        Vec3 vec3d = e.getLookAngle();
        px += vec3d.x * 0.3;
        py += vec3d.y * 0.3;
        pz += vec3d.z * 0.3;
        return new Vec3(px, py, pz);
    }

    public static boolean hasGoggles(Entity e) {
        if (!(e instanceof Player)) {
            return false;
        }
        Player viewer = (Player)e;
        if (viewer.getMainHandItem().getItem() instanceof IGoggles && showPopups(viewer.getMainHandItem(), viewer)) {
            return true;
        }
        for (int a = 0; a < 4; ++a) {
            if (viewer.getInventory().getItem(36 + a).getItem() instanceof IGoggles && showPopups(viewer.getInventory().getItem(36 + a), viewer)) {
                return true;
            }
        }
        // Baubles removed — skip baubles slot check
        return false;
    }

    private static boolean showPopups(ItemStack stack, Player player) {
        return ((IGoggles)stack.getItem()).showIngamePopups(stack, player);
    }

    public static boolean hasRevealer(Entity e) {
        if (!(e instanceof Player)) {
            return false;
        }
        Player viewer = (Player)e;
        if (viewer.getMainHandItem().getItem() instanceof IRevealer && reveals(viewer.getMainHandItem(), viewer)) {
            return true;
        }
        if (viewer.getOffhandItem().getItem() instanceof IRevealer && reveals(viewer.getOffhandItem(), viewer)) {
            return true;
        }
        for (int a = 0; a < 4; ++a) {
            if (viewer.getInventory().getItem(36 + a).getItem() instanceof IRevealer && reveals(viewer.getInventory().getItem(36 + a), viewer)) {
                return true;
            }
        }
        // Baubles removed — skip baubles slot check
        return false;
    }

    private static boolean reveals(ItemStack stack, Player player) {
        return ((IRevealer)stack.getItem()).showNodes(stack, player);
    }

    public static Entity getPointedEntity(Level world, Entity entity, double minrange, double range, float padding, boolean nonCollide) {
        return getPointedEntity(world, new net.minecraft.world.phys.BlockHitResult(entity.getEyePosition(), net.minecraft.core.Direction.UP, entity.blockPosition(), false), entity.getLookAngle(), minrange, range, padding, nonCollide);
    }

    public static Entity getPointedEntity(Level world, Entity entity, Vec3 lookVec, double minrange, double range, float padding) {
        return getPointedEntity(world, new net.minecraft.world.phys.BlockHitResult(entity.getEyePosition(), net.minecraft.core.Direction.UP, entity.blockPosition(), false), lookVec, minrange, range, padding, false);
    }

    public static Entity getPointedEntity(Level world, HitResult ray, Vec3 lookVec, double minrange, double range, float padding) {
        return getPointedEntity(world, ray, lookVec, minrange, range, padding, false);
    }

    public static Entity getPointedEntity(Level world, HitResult ray, Vec3 lookVec, double minrange, double range, float padding, boolean nonCollide) {
        Entity pointedEntity = null;
        double d = range;
        Vec3 entityVec = new Vec3(ray.getLocation().x, ray.getLocation().y, ray.getLocation().z);
        Vec3 vec3d2 = entityVec.add(lookVec.x * d, lookVec.y * d, lookVec.z * d);
        float f1 = padding;
        Entity hitEntity = (ray instanceof net.minecraft.world.phys.EntityHitResult ehr) ? ehr.getEntity() : null;
        AABB bb = (hitEntity != null) ? hitEntity.getBoundingBox() : new AABB(ray.getLocation().x, ray.getLocation().y, ray.getLocation().z, ray.getLocation().x, ray.getLocation().y, ray.getLocation().z).inflate(0.5);
        // getEntitiesOfClass(Class, Entity, AABB) removed; use getEntities(Entity, AABB, Predicate)
        List<Entity> list = world.getEntities(hitEntity, bb.expandTowards(lookVec.x * d, lookVec.y * d, lookVec.z * d).inflate(f1, f1, f1));
        double d2 = 0.0;
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (ray.getLocation().distanceTo(entity.position()) >= minrange) {
                if (entity.isPickable() || nonCollide) {
                    // rayTraceBlocks removed; use clip with ClipContext — MISS means no blocks in the way
                    if (world.clip(new net.minecraft.world.level.ClipContext(ray.getLocation(), new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ()), net.minecraft.world.level.ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, entity)).getType() == HitResult.Type.MISS) {
                        // getCollisionBorderSize() removed; use getPickRadius()
                        float f2 = Math.max(0.8f, entity.getPickRadius());
                        AABB axisalignedbb = entity.getBoundingBox().inflate(f2, f2, f2);
                        // calculateIntercept removed; use AABB.clip
                        java.util.Optional<Vec3> intercept = axisalignedbb.clip(entityVec, vec3d2);
                        if (axisalignedbb.contains(entityVec)) {
                            if (0.0 < d2 || d2 == 0.0) {
                                pointedEntity = entity;
                                d2 = 0.0;
                            }
                        }
                        else if (intercept.isPresent()) {
                            double d3 = entityVec.distanceTo(intercept.get());
                            if (d3 < d2 || d2 == 0.0) {
                                pointedEntity = entity;
                                d2 = d3;
                            }
                        }
                    }
                }
            }
        }
        return pointedEntity;
    }

    public static HitResult getPointedEntityRay(Level world, Entity ignoreEntity, Vec3 startVec, Vec3 lookVec, double minrange, double range, float padding, boolean nonCollide) {
        HitResult pointedEntityRay = null;
        double d = range;
        Vec3 vec3d2 = startVec.add(lookVec.x * d, lookVec.y * d, lookVec.z * d);
        float f1 = padding;
        AABB bb = (ignoreEntity != null) ? ignoreEntity.getBoundingBox() : new AABB(startVec.x, startVec.y, startVec.z, startVec.x, startVec.y, startVec.z).inflate(0.5);
        List<Entity> list = world.getEntities(ignoreEntity, bb.expandTowards(lookVec.x * d, lookVec.y * d, lookVec.z * d).inflate(f1, f1, f1));
        double d2 = 0.0;
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (startVec.distanceTo(entity.position()) >= minrange) {
                if (entity.isPickable() || nonCollide) {
                    if (world.clip(new net.minecraft.world.level.ClipContext(startVec, new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ()), net.minecraft.world.level.ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, entity)).getType() == HitResult.Type.MISS) {
                        float f2 = Math.max(0.8f, entity.getPickRadius());
                        AABB axisalignedbb = entity.getBoundingBox().inflate(f2, f2, f2);
                        java.util.Optional<Vec3> intercept = axisalignedbb.clip(startVec, vec3d2);
                        if (axisalignedbb.contains(startVec)) {
                            if (0.0 < d2 || d2 == 0.0) {
                                pointedEntityRay = null; /* TODO: EntityHitResult */
                                d2 = 0.0;
                            }
                        }
                        else if (intercept.isPresent()) {
                            double d3 = startVec.distanceTo(intercept.get());
                            if (d3 < d2 || d2 == 0.0) {
                                pointedEntityRay = null; /* TODO: EntityHitResult */
                                d2 = d3;
                            }
                        }
                    }
                }
            }
        }
        return pointedEntityRay;
    }

    public static Entity getPointedEntity(Level world, LivingEntity player, double range, Class<?> clazz) {
        Entity pointedEntity = null;
        double d = range;
        Vec3 vec3d = new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
        Vec3 vec3d2 = player.getLookAngle();
        Vec3 vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        float f1 = 1.1f;
        List<Entity> list = world.getEntities(player, player.getBoundingBox().expandTowards(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d).inflate(f1, f1, f1));
        double d2 = 0.0;
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (entity.isPickable() && world.clip(new net.minecraft.world.level.ClipContext(new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ()), new Vec3(entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ()), net.minecraft.world.level.ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, entity)).getType() == HitResult.Type.MISS) {
                if (!clazz.isInstance(entity)) {
                    float f2 = Math.max(0.8f, entity.getPickRadius());
                    AABB axisalignedbb = entity.getBoundingBox().inflate(f2, f2, f2);
                    java.util.Optional<Vec3> intercept = axisalignedbb.clip(vec3d, vec3d3);
                    if (axisalignedbb.contains(vec3d)) {
                        if (0.0 < d2 || d2 == 0.0) {
                            pointedEntity = entity;
                            d2 = 0.0;
                        }
                    }
                    else if (intercept.isPresent()) {
                        double d3 = vec3d.distanceTo(intercept.get());
                        if (d3 < d2 || d2 == 0.0) {
                            pointedEntity = entity;
                            d2 = d3;
                        }
                    }
                }
            }
        }
        return pointedEntity;
    }

    public static boolean canEntityBeSeen(Entity entity, net.minecraft.world.level.block.entity.BlockEntity te) {
        return true; // rayTraceBlocks removed; use level ClipContext for proper raytrace
    }

    public static boolean canEntityBeSeen(Entity lookingEntity, double x, double y, double z) {
        return true; // rayTraceBlocks removed
    }

    public static boolean canEntityBeSeen(Entity lookingEntity, Entity targetEntity) {
        return true; // rayTraceBlocks removed
    }

    public static void resetFloatCounter(net.minecraft.server.level.ServerPlayer player) {
        // floatingTickCount → aboveGroundTickCount (private); use public resetFlyingTicks()
        player.connection.resetFlyingTicks();
    }

    public static <T extends Entity> List<T> getEntitiesInRange(Level world, BlockPos pos, Entity entity, Class<? extends T> classEntity, double range) {
        return getEntitiesInRange(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, entity, classEntity, range);
    }

    public static <T extends Entity> List<T> getEntitiesInRange(Level world, double x, double y, double z, Entity entity, Class<? extends T> classEntity, double range) {
        ArrayList<T> out = new ArrayList<T>();
        List list = world.getEntitiesOfClass(classEntity, new AABB(x, y, z, x, y, z).inflate(range, range, range));
        if (list.size() > 0) {
            for (Object e : list) {
                Entity ent = (Entity)e;
                if (entity != null && entity.getId() == ent.getId()) {
                    continue;
                }
                out.add((T)ent);
            }
        }
        return out;
    }

    public static <T extends Entity> List<T> getEntitiesInRangeSorted(Level world, Entity entity, Class<? extends T> classEntity, double range) {
        List<T> list = getEntitiesInRange(world, entity.getX(), entity.getY(), entity.getZ(), entity, classEntity, range);
        List<T> sl = list.stream().sorted(new EntityDistComparator(entity)).collect(Collectors.toList());
        return sl;
    }

    public static boolean isVisibleTo(float fov, Entity ent, Entity ent2, float range) {
        double[] x = { ent2.getX(), ent2.getBoundingBox().minY + ent2.getBbHeight() / 2.0f, ent2.getZ() };
        double[] t = { ent.getX(), ent.getBoundingBox().minY + ent.getEyeHeight(), ent.getZ() };
        Vec3 q = ent.getLookAngle();
        q = new Vec3(q.x * range, q.y * range, q.z * range);
        Vec3 l = q.add(ent.getX(), ent.getBoundingBox().minY + ent.getEyeHeight(), ent.getZ());
        double[] b = { l.x, l.y, l.z };
        return Utils.isLyingInCone(x, t, b, fov);
    }

    public static boolean isVisibleTo(float fov, Entity ent, double xx, double yy, double zz, float range) {
        double[] x = { xx, yy, zz };
        double[] t = { ent.getX(), ent.getBoundingBox().minY + ent.getEyeHeight(), ent.getZ() };
        Vec3 q = ent.getLookAngle();
        q = new Vec3(q.x * range, q.y * range, q.z * range);
        Vec3 l = q.add(ent.getX(), ent.getBoundingBox().minY + ent.getEyeHeight(), ent.getZ());
        double[] b = { l.x, l.y, l.z };
        return Utils.isLyingInCone(x, t, b, fov);
    }

    public static ItemEntity entityDropSpecialItem(Entity entity, ItemStack stack, float dropheight) {
        if (stack.getCount() != 0 && !stack.isEmpty()) {
            EntitySpecialItem entityitem = new EntitySpecialItem(entity.level(), entity.getX(), entity.getY() + dropheight, entity.getZ(), stack);
            // setDefaultPickupDelay() renamed to setDefaultPickUpDelay() in 1.21.5
            entityitem.setDefaultPickUpDelay();
            entityitem.setDeltaMovement(entityitem.getDeltaMovement().x, 0.10000000149011612, entityitem.getDeltaMovement().z);
            entityitem.setDeltaMovement(0.0, entityitem.getDeltaMovement().y, entityitem.getDeltaMovement().z);
            entityitem.setDeltaMovement(entityitem.getDeltaMovement().x, entityitem.getDeltaMovement().y, 0.0);
            // captureDrops is now a method returning Collection, not a public boolean field
            if (entity.captureDrops() != null) {
                entity.captureDrops().add(entityitem);
            }
            else {
                entity.level().addFreshEntity(entityitem);
            }
            return entityitem;
        }
        return null;
    }

    public static void makeChampion(Monster entity, boolean persist) {
        try {
            if (entity.getAttribute(net.minecraft.core.Holder.direct(ThaumcraftApiHelper.CHAMPION_MOD)).getValue() > -2.0) {
                return;
            }
        }
        catch (Exception e) {
            return;
        }
        int type = entity.level().getRandom().nextInt(ChampionModifier.mods.length);
        // EntityCreeper → Creeper
        if (entity instanceof Creeper) {
            type = 0;
        }
        AttributeInstance modai = entity.getAttribute(net.minecraft.core.Holder.direct(ThaumcraftApiHelper.CHAMPION_MOD));
        modai.removeModifier(ChampionModifier.mods[type].attributeMod);
        modai.addPermanentModifier(ChampionModifier.mods[type].attributeMod);
        if (!(entity instanceof EntityThaumcraftBoss)) {
            AttributeInstance iattributeinstance = entity.getAttribute(Attributes.MAX_HEALTH);
            iattributeinstance.removeModifier(EntityUtils.CHAMPION_HEALTH);
            iattributeinstance.addPermanentModifier(EntityUtils.CHAMPION_HEALTH);
            AttributeInstance iattributeinstance2 = entity.getAttribute(Attributes.ATTACK_DAMAGE);
            iattributeinstance2.removeModifier(EntityUtils.CHAMPION_DAMAGE);
            iattributeinstance2.addPermanentModifier(EntityUtils.CHAMPION_DAMAGE);
            entity.heal(25.0f);
            // setCustomNameTag → setCustomName(Component)
            entity.setCustomName(net.minecraft.network.chat.Component.literal(ChampionModifier.mods[type].getModNameLocalized() + " " + entity.getName().getString()));
        }
        else {
            ((EntityThaumcraftBoss)entity).generateName();
        }
        if (persist) {
            entity.setPersistenceRequired();
        }
        switch (type) {
            case 0: {
                AttributeInstance sai = entity.getAttribute(Attributes.MOVEMENT_SPEED);
                sai.removeModifier(EntityUtils.BOLDBUFF);
                sai.addPermanentModifier(EntityUtils.BOLDBUFF);
                break;
            }
            case 3: {
                AttributeInstance mai = entity.getAttribute(Attributes.ATTACK_DAMAGE);
                mai.removeModifier(EntityUtils.MIGHTYBUFF);
                mai.addPermanentModifier(EntityUtils.MIGHTYBUFF);
                break;
            }
            case 5: {
                int bh = (int)entity.getAttribute(Attributes.MAX_HEALTH).getBaseValue() / 2;
                entity.setAbsorptionAmount(entity.getAbsorptionAmount() + bh);
                break;
            }
        }
    }

    public static void makeTainted(LivingEntity target) {
        try {
            if (target.getAttribute(net.minecraft.core.Holder.direct(ThaumcraftApiHelper.CHAMPION_MOD)) != null && target.getAttribute(net.minecraft.core.Holder.direct(ThaumcraftApiHelper.CHAMPION_MOD)).getValue() > -1.0) {
                return;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return;
        }
        int type = 13;
        AttributeInstance modai = target.getAttribute(net.minecraft.core.Holder.direct(ThaumcraftApiHelper.CHAMPION_MOD));
        if (modai == null) {
            return;
        }
        if (target.getAttribute(net.minecraft.core.Holder.direct(ThaumcraftApiHelper.CHAMPION_MOD)).getValue() == -1.0) {
            modai.addPermanentModifier(ChampionModifier.ATTRIBUTE_MINUS_ONE);
        }
        modai.removeModifier(ChampionModifier.mods[type].attributeMod);
        modai.addPermanentModifier(ChampionModifier.mods[type].attributeMod);
        if (!(target instanceof EntityThaumcraftBoss)) {
            AttributeInstance iattributeinstance = target.getAttribute(Attributes.MAX_HEALTH);
            iattributeinstance.removeModifier(EntityUtils.HPBUFF[5]);
            iattributeinstance.addPermanentModifier(EntityUtils.HPBUFF[5]);
            AttributeInstance iattributeinstance2 = target.getAttribute(Attributes.ATTACK_DAMAGE);
            if (iattributeinstance2 == null) {
                // registerAttribute removed in 1.16+ — attributes are registered via AttributeSupplier.
                // If the entity doesn't have ATTACK_DAMAGE, we can't set it here; skip gracefully.
                target.getAttribute(Attributes.ATTACK_DAMAGE); // no-op if null
            }
            else {
                iattributeinstance2.setBaseValue(Math.max(2.0f, (target.getBbHeight() + target.getBbWidth()) * 2.0f));
                iattributeinstance2.removeModifier(EntityUtils.DMGBUFF[0]);
                iattributeinstance2.addPermanentModifier(EntityUtils.DMGBUFF[0]);
            }
            target.heal(25.0f);
        }
        else {
            ((EntityThaumcraftBoss)target).generateName();
        }
    }

    static {
        CHAMPION_HEALTH = new net.minecraft.world.entity.ai.attributes.AttributeModifier(net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", "champion_health_buff"), 100.0, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE);
        CHAMPION_DAMAGE = new net.minecraft.world.entity.ai.attributes.AttributeModifier(net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", "champion_damage_buff"), 2.0, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        BOLDBUFF = new net.minecraft.world.entity.ai.attributes.AttributeModifier(net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", "bold_speed_boost"), 0.3, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        MIGHTYBUFF = new net.minecraft.world.entity.ai.attributes.AttributeModifier(net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", "mighty_damage_boost"), 2.0, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);
        HPBUFF = new AttributeModifier[] { new net.minecraft.world.entity.ai.attributes.AttributeModifier(net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", "health_buff_1"), 50.0, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE), new net.minecraft.world.entity.ai.attributes.AttributeModifier(net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", "health_buff_2"), 50.0, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE), new net.minecraft.world.entity.ai.attributes.AttributeModifier(net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", "health_buff_3"), 50.0, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE), new net.minecraft.world.entity.ai.attributes.AttributeModifier(net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", "health_buff_4"), 50.0, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE), new net.minecraft.world.entity.ai.attributes.AttributeModifier(net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", "health_buff_5"), 50.0, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE), new net.minecraft.world.entity.ai.attributes.AttributeModifier(net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", "health_buff_6"), 25.0, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE) };
        DMGBUFF = new AttributeModifier[] { new net.minecraft.world.entity.ai.attributes.AttributeModifier(net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", "damage_buff_1"), 0.5, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE), new net.minecraft.world.entity.ai.attributes.AttributeModifier(net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", "damage_buff_2"), 0.5, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE), new net.minecraft.world.entity.ai.attributes.AttributeModifier(net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", "damage_buff_3"), 0.5, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE), new net.minecraft.world.entity.ai.attributes.AttributeModifier(net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", "damage_buff_4"), 0.5, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE), new net.minecraft.world.entity.ai.attributes.AttributeModifier(net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", "damage_buff_5"), 0.5, net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE) };
    }

    public static class EntityDistComparator implements Comparator<Entity>
    {
        private Entity source;

        public EntityDistComparator(Entity source) {
            this.source = source;
        }

        @Override
        public int compare(Entity a, Entity b) {
            if (a.equals(b)) {
                return 0;
            }
            double da = source.position().distanceToSqr(a.position());
            double db = source.position().distanceToSqr(b.position());
            return (da < db) ? -1 : ((da > db) ? 1 : 0);
        }
    }
}
