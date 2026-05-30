package thaumcraft.common.lib.potions;

import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import thaumcraft.api.potions.PotionVisExhaust;

public class PotionInfectiousVisExhaust extends MobEffect {

    public static MobEffect instance;

    public PotionInfectiousVisExhaust(int color) {
        super(MobEffectCategory.HARMFUL, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class, entity.getBoundingBox().inflate(4.0, 4.0, 4.0));
        for (LivingEntity e : targets) {
            if (!e.hasEffect(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionInfectiousVisExhaust.instance))) {
                if (amplifier > 0) {
                    e.addEffect(new MobEffectInstance(
                            net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionInfectiousVisExhaust.instance),
                            6000, amplifier - 1, false, true));
                } else {
                    e.addEffect(new MobEffectInstance(
                            net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionVisExhaust.instance),
                            6000, 0, false, true));
                }
            }
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 40 == 0;
    }

    static {
        PotionInfectiousVisExhaust.instance = null;
    }
}
