package thaumcraft.api.potions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.api.entities.ITaintedMob;


public class PotionFluxTaint extends MobEffect
{
    public static MobEffect instance = null;

    public PotionFluxTaint()
    {
        super(MobEffectCategory.HARMFUL, 0x228B22);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity target, int strength) {
        if (target instanceof ITaintedMob) {
            target.heal(1);
        } else {
            if (!target.isInvertedHealAndHarm() && !(target instanceof Player)) {
                target.hurtServer(level, DamageSourceThaumcraft.taint(level), 1);
            } else if (!target.isInvertedHealAndHarm() && (target.getMaxHealth() > 1 || (target instanceof Player))) {
                target.hurtServer(level, DamageSourceThaumcraft.taint(level), 1);
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        int k = 40 >> amplifier;
        return k > 0 ? duration % k == 0 : true;
    }
}
