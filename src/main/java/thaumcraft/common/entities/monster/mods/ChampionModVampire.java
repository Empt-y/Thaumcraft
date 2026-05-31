package thaumcraft.common.entities.monster.mods;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import org.lwjgl.opengl.GL11;
import thaumcraft.client.fx.FXDispatcher;


public class ChampionModVampire implements IChampionModifierEffect
{
    @Override
    public float performEffect(LivingEntity boss, LivingEntity target, DamageSource source, float amount) {
        boss.heal(Math.max(2.0f, amount / 2.0f));
        return amount;
    }
    
    @Override
    public void showFX(LivingEntity boss) {
        if (boss.level().getRandom().nextFloat() > 0.2f) {
            return;
        }
        float w = boss.level().getRandom().nextFloat() * boss.getBbWidth();
        float d = boss.level().getRandom().nextFloat() * boss.getBbWidth();
        float h = boss.level().getRandom().nextFloat() * boss.getBbHeight();
        FXDispatcher.INSTANCE.drawGenericParticles(boss.getBoundingBox().minX + w, boss.getBoundingBox().minY + h, boss.getBoundingBox().minZ + d, 0.0, 0.0, 0.0, 0.9f + boss.level().getRandom().nextFloat() * 0.1f, 0.0f, 0.0f, 0.9f, false, 579, 4, 1, 4 + boss.level().getRandom().nextInt(4), 0, 0.5f + boss.level().getRandom().nextFloat() * 0.2f, 0.0f, 0);
    }
    
    @Override
    public void preRender(LivingEntity boss, Object renderLivingBase) {
        GL11.glColor4f(1.0f, 0.7f, 0.7f, 1.0f);
    }
}
