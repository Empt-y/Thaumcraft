package thaumcraft.common.entities.monster.mods;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.client.fx.FXDispatcher;


public class ChampionModBold implements IChampionModifierEffect
{
    @Override
    public float performEffect(LivingEntity boss, LivingEntity target, DamageSource source, float ammount) {
        return 0.0f;
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void showFX(LivingEntity boss) {
        if (boss.level().getRandom().nextBoolean()) {
            return;
        }
        float w = boss.level().getRandom().nextFloat() * boss.getBbWidth();
        float d = boss.level().getRandom().nextFloat() * boss.getBbWidth();
        float h = boss.level().getRandom().nextFloat() * boss.getBbHeight() / 3.0f;
        FXDispatcher.INSTANCE.spark(boss.getBoundingBox().minX + w, boss.getBoundingBox().minY + h, boss.getBoundingBox().minZ + d, 0.2f, 0.3f - boss.level().getRandom().nextFloat() * 0.1f, 0.0f, 0.8f + boss.level().getRandom().nextFloat() * 0.2f, 1.0f);
    }
    
    @Override
    public void preRender(LivingEntity boss, Object renderLivingBase) {
    }
}
