package thaumcraft.common.entities.monster.mods;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.client.fx.FXDispatcher;


public class ChampionModSpined implements IChampionModifierEffect
{
    @Override
    public float performEffect(LivingEntity boss, LivingEntity target, DamageSource source, float amount) {
        if (target == null) {
            return amount;
        }
        if (boss.level() instanceof net.minecraft.server.level.ServerLevel sl) {
            target.hurtServer(sl, sl.damageSources().thorns(boss), (float)(1 + boss.level().getRandom().nextInt(3)));
        }
        target.playSound(SoundEvents.THORNS_HIT, 0.5f, 1.0f);
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
        int p = 704 + boss.level().getRandom().nextInt(4) * 3;
        FXDispatcher.INSTANCE.drawGenericParticles(boss.getBoundingBox().minX + w, boss.getBoundingBox().minY + h, boss.getBoundingBox().minZ + d, 0.0, 0.0, 0.0, 0.5f + boss.level().getRandom().nextFloat() * 0.2f, 0.1f + boss.level().getRandom().nextFloat() * 0.2f, 0.1f + boss.level().getRandom().nextFloat() * 0.2f, 0.7f, false, p, 3, 1, 3, 0, 1.2f + boss.level().getRandom().nextFloat() * 0.3f, 0.0f, 0);
    }
    
    @Override
    public void preRender(LivingEntity boss, Object renderLivingBase) {
    }
}
