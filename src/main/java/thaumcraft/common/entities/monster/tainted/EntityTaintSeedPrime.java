package thaumcraft.common.entities.monster.tainted;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import thaumcraft.common.config.ConfigItems;


public class EntityTaintSeedPrime extends EntityTaintSeed
{
    public EntityTaintSeedPrime(net.minecraft.world.entity.EntityType<? extends EntityTaintSeedPrime> type, Level world) {
        super(type, world);
        xpReward = 12;
    }

    @Override
    protected int getArea() {
        return 2;
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.monster.Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 150.0)
            .add(Attributes.ATTACK_DAMAGE, 7.0)
            .add(Attributes.MOVEMENT_SPEED, 0.0);
    }
}
