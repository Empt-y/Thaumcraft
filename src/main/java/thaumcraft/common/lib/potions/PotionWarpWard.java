package thaumcraft.common.lib.potions;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class PotionWarpWard extends MobEffect {

    public static MobEffect instance;

    public PotionWarpWard(int color) {
        super(MobEffectCategory.NEUTRAL, color);
    }

    @Override
    public boolean isBeneficial() {
        return true;
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    static {
        PotionWarpWard.instance = null;
    }
}
