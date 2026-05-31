package thaumcraft.common.entities.monster.mods;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.fx.FXDispatcher;


public class ChampionModGrim implements IChampionModifierEffect
{
    @Override
    public float performEffect(LivingEntity boss, LivingEntity target, DamageSource source, float amount) {
        if (boss.level().getRandom().nextFloat() < 0.4f) {
            target.addEffect(new MobEffectInstance(MobEffects.WITHER, 200));
        }
        return amount;
    }
    
    @Override
    public void showFX(LivingEntity boss) {
        if (boss.level().getRandom().nextBoolean()) {
            return;
        }
        float w = boss.level().getRandom().nextFloat() * boss.getBbWidth();
        float d = boss.level().getRandom().nextFloat() * boss.getBbWidth();
        float h = boss.level().getRandom().nextFloat() * boss.getBbHeight();
        FXDispatcher.INSTANCE.drawGenericParticles(boss.getBoundingBox().minX + w, boss.getBoundingBox().minY + h, boss.getBoundingBox().minZ + d, 0.0, -0.02, 0.0, boss.level().getRandom().nextFloat() * 0.2f, boss.level().getRandom().nextFloat() * 0.2f, boss.level().getRandom().nextFloat() * 0.2f, 0.8f, false, 640, 10, 1, 8 + boss.level().getRandom().nextInt(4), 0, 0.6f + boss.level().getRandom().nextFloat() * 0.4f, 0.0f, 0);
    }
    
    @Override
    public void preRender(LivingEntity boss, Object renderLivingBase) {
        GL11.glColor4f(0.6f, 0.6f, 0.6f, 1.0f);
    }
}
