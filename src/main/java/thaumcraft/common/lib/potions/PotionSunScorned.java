package thaumcraft.common.lib.potions;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class PotionSunScorned extends MobEffect {

    public static MobEffect instance;

    public PotionSunScorned(int color) {
        super(MobEffectCategory.HARMFUL, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        float brightness = entity.getBrightness();
        BlockPos eyePos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());
        if (brightness > 0.5f
                && entity.getRandom().nextFloat() * 30.0f < (brightness - 0.4f) * 2.0f
                && level.canSeeSky(eyePos)) {
            entity.setRemainingFireTicks(4 * 20);
        } else if (brightness < 0.25f && entity.getRandom().nextFloat() > brightness * 2.0f) {
            entity.heal(1.0f);
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 40 == 0;
    }

    static {
        PotionSunScorned.instance = null;
    }
}
