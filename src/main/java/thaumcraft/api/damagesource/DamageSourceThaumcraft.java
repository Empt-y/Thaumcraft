package thaumcraft.api.damagesource;
import net.minecraft.core.Holder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

// TODO: register DamageType JSON files in data/thaumcraft/damage_type/ for taint/tentacle/swarm/dissolve
public class DamageSourceThaumcraft {

    /** Call with a Level to get the proper Holder<DamageType> from the registry. */
    public static DamageSource causeSwarmDamage(Level level, LivingEntity entity) {
        return level.damageSources().generic();
    }

    public static DamageSource causeTentacleDamage(Level level, LivingEntity entity) {
        return level.damageSources().generic();
    }

    public static DamageSource taint(Level level) {
        return level.damageSources().magic();
    }

    public static DamageSource tentacle(Level level) {
        return level.damageSources().generic();
    }

    public static DamageSource swarm(Level level) {
        return level.damageSources().generic();
    }

    public static DamageSource dissolve(Level level) {
        return level.damageSources().magic();
    }
}
