package thaumcraft.common.lib.potions;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import thaumcraft.api.blocks.BlocksTC;

public class PotionThaumarhia extends MobEffect {

    public static MobEffect instance;

    public PotionThaumarhia(int color) {
        super(MobEffectCategory.HARMFUL, color);
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity entity, int amplifier) {
        BlockPos pos = entity.blockPosition();
        if (entity.getRandom().nextInt(15) == 0 && level.isEmptyBlock(pos)) {
            level.setBlockAndUpdate(pos, BlocksTC.fluxGoo.defaultBlockState());
        }
        return true;
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    static {
        PotionThaumarhia.instance = null;
    }
}
