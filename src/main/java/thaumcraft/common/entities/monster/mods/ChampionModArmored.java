package thaumcraft.common.entities.monster.mods;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import thaumcraft.client.fx.FXDispatcher;


public class ChampionModArmored implements IChampionModifierEffect
{
    @Override
    public float performEffect(LivingEntity mob, LivingEntity target, DamageSource source, float amount) {
        if (!source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_ARMOR)) {
            float f1 = amount * 19.0f;
            amount = f1 / 25.0f;
        }
        return amount;
    }
    
    @Override
    public void showFX(LivingEntity boss) {
        if (boss.level().getRandom().nextInt(4) != 0) {
            return;
        }
        float w = boss.level().getRandom().nextFloat() * boss.getBbWidth();
        float d = boss.level().getRandom().nextFloat() * boss.getBbWidth();
        float h = boss.level().getRandom().nextFloat() * boss.getBbHeight();
        FXDispatcher.INSTANCE.drawGenericParticles(boss.getBoundingBox().minX + w, boss.getBoundingBox().minY + h, boss.getBoundingBox().minZ + d, 0.0, 0.0, 0.0, 0.9f, 0.9f, 0.9f + boss.level().getRandom().nextFloat() * 0.1f, 0.7f, false, 448, 9, 1, 5 + boss.level().getRandom().nextInt(4), 0, 0.6f + boss.level().getRandom().nextFloat() * 0.2f, 0.0f, 0);
    }
    
    @Override
    public void preRender(LivingEntity boss, Object renderLivingBase) {
    }
}
