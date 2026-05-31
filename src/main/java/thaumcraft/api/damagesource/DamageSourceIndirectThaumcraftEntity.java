package thaumcraft.api.damagesource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;

public class DamageSourceIndirectThaumcraftEntity {

    public static DamageSource create(Level level, Entity direct, Entity indirect) {
        return level.damageSources().indirectMagic(direct, indirect);
    }
}
