package thaumcraft.api.potions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;


public class PotionVisExhaust extends MobEffect
{
    public static MobEffect instance = null;

    public PotionVisExhaust()
    {
        super(MobEffectCategory.HARMFUL, 0x6A0DAD);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity target, int amplifier) {
        return true;
    }
}
