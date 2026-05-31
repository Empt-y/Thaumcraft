package thaumcraft.common.entities.monster.mods;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;


public interface IChampionModifierEffect
{
    float performEffect(LivingEntity p0, LivingEntity p1, DamageSource p2, float p3);
    
    void showFX(LivingEntity p0);
    
    void preRender(LivingEntity p0, Object p1);
}
