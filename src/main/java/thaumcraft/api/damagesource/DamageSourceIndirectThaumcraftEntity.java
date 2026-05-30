package thaumcraft.api.damagesource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;

// TODO: register DamageType JSON files in data/thaumcraft/damage_type/
public class DamageSourceIndirectThaumcraftEntity {

    public static DamageSource create(Level level, Entity direct, Entity indirect) {
        return level.damageSources().generic();
    }
}
