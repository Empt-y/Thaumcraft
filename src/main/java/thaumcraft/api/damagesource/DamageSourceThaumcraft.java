package thaumcraft.api.damagesource;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class DamageSourceThaumcraft {

    public static final ResourceKey<DamageType> TAINT    = key("taint");
    public static final ResourceKey<DamageType> TENTACLE = key("tentacle");
    public static final ResourceKey<DamageType> SWARM    = key("swarm");
    public static final ResourceKey<DamageType> DISSOLVE = key("dissolve");

    private static ResourceKey<DamageType> key(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath("thaumcraft", name));
    }

    private static DamageSource get(Level level, ResourceKey<DamageType> key) {
        Holder<DamageType> holder = level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key);
        return new DamageSource(holder);
    }

    private static DamageSource getEntity(Level level, ResourceKey<DamageType> key, LivingEntity entity) {
        Holder<DamageType> holder = level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key);
        return new DamageSource(holder, entity);
    }

    public static DamageSource causeSwarmDamage(Level level, LivingEntity entity) {
        return getEntity(level, SWARM, entity);
    }

    public static DamageSource causeTentacleDamage(Level level, LivingEntity entity) {
        return getEntity(level, TENTACLE, entity);
    }

    public static DamageSource taint(Level level) {
        return get(level, TAINT);
    }

    public static DamageSource tentacle(Level level) {
        return get(level, TENTACLE);
    }

    public static DamageSource swarm(Level level) {
        return get(level, SWARM);
    }

    public static DamageSource dissolve(Level level) {
        return get(level, DISSOLVE);
    }
}
