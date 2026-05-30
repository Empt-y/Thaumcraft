package thaumcraft.common.entities.monster.mods;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;


public class ChampionModDummy implements IChampionModifierEffect
{
    @Override
    public float performEffect(LivingEntity boss, LivingEntity target, DamageSource source, float amount) {
        return amount;
    }
    
    @Override
    public void showFX(LivingEntity boss) {
    }
    
    @Override
    public void preRender(LivingEntity boss, Object renderLivingBase) {
    }
}
