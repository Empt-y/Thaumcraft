package thaumcraft.common.entities.monster.mods;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.client.fx.FXDispatcher;


public class ChampionModWarded implements IChampionModifierEffect
{
    @Override
    public float performEffect(LivingEntity mob, LivingEntity target, DamageSource source, float amount) {
        if (mob.invulnerableTime <= 0 && mob.tickCount % 25 == 0) {
            int bh = (int)mob.getAttribute(Attributes.MAX_HEALTH).getBaseValue() / 2;
            if (mob.getAbsorptionAmount() < bh) {
                mob.setAbsorptionAmount(mob.getAbsorptionAmount() + 1.0f);
            }
        }
        return amount;
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void showFX(LivingEntity boss) {
        if (boss.level().getRandom().nextBoolean()) {
            return;
        }
        float w = boss.level().getRandom().nextFloat() * boss.getBbWidth();
        float d = boss.level().getRandom().nextFloat() * boss.getBbWidth();
        float h = boss.level().getRandom().nextFloat() * boss.getBbHeight();
        FXDispatcher.INSTANCE.drawGenericParticles(boss.getBoundingBox().minX + w, boss.getBoundingBox().minY + h, boss.getBoundingBox().minZ + d, 0.0, 0.0, 0.0, 0.5f + boss.level().getRandom().nextFloat() * 0.1f, 0.5f + boss.level().getRandom().nextFloat() * 0.1f, 0.5f + boss.level().getRandom().nextFloat() * 0.1f, 0.6f, true, 69, 4, 1, 4 + boss.level().getRandom().nextInt(4), 0, 0.8f + boss.level().getRandom().nextFloat() * 0.3f, 0.0f, 0);
    }
    
    @Override
    public void preRender(LivingEntity boss, Object renderLivingBase) {
    }
}
