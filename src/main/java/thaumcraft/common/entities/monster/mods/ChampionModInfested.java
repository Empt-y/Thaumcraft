package thaumcraft.common.entities.monster.mods;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.monster.tainted.EntityTaintCrawler;
import thaumcraft.common.lib.SoundsTC;


public class ChampionModInfested implements IChampionModifierEffect
{
    @Override
    public float performEffect(LivingEntity boss, LivingEntity target, DamageSource source, float amount) {
        if (boss.level().getRandom().nextFloat() < 0.4f && !boss.level().isClientSide()) {
            EntityTaintCrawler spiderling = new EntityTaintCrawler(boss.level());
            spiderling.setPos(boss.getX(), boss.getY() + boss.getBbHeight() / 2.0f, boss.getZ());
            boss.level().addFreshEntity(spiderling);
            boss.playSound(SoundsTC.gore, 0.5f, 1.0f);
        }
        return amount;
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void showFX(LivingEntity boss) {
        if (boss.level().getRandom().nextBoolean()) {
            FXDispatcher.INSTANCE.slimeJumpFX(boss, 0);
        }
    }
    
    @Override
    public void preRender(LivingEntity boss, Object renderLivingBase) {
    }
}
