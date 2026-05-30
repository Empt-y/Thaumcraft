package thaumcraft.common.items.casters.foci;
import java.util.Iterator;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;


public class FocusEffectFrost extends FocusEffect
{
    @Override
    public String getResearch() {
        return "FOCUSELEMENTAL";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.FROST";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.COLD;
    }
    
    @Override
    public int getComplexity() {
        return getSettingValue("duration") + getSettingValue("power") * 2;
    }
    
    @Override
    public float getDamageForDisplay(float finalPower) {
        return (3 + getSettingValue("power")) * finalPower;
    }
    
    @Override
    public boolean execute(HitResult target, Trajectory trajectory, float finalPower, int num) {
        /* sendToAllAround stub */
        if (target.getType() == HitResult.Type.ENTITY && ((net.minecraft.world.phys.EntityHitResult)target).getEntity() != null) {
            float damage = getDamageForDisplay(finalPower);
            int duration = 20 * getSettingValue("duration");
            int potency = (int)(1.0f + getSettingValue("power") * finalPower / 3.0f);
            ((net.minecraft.world.phys.EntityHitResult)target).getEntity().hurt(getPackage().world.damageSources().thrown((((net.minecraft.world.phys.EntityHitResult)target).getEntity() != null) ? ((net.minecraft.world.phys.EntityHitResult)target).getEntity() : getPackage().getCaster(), getPackage().getCaster()), damage);
            if (((net.minecraft.world.phys.EntityHitResult)target).getEntity() instanceof LivingEntity) {
                ((LivingEntity)((net.minecraft.world.phys.EntityHitResult)target).getEntity()).addEffect(new MobEffectInstance(MobEffects.SLOWNESS, duration, potency));
            }
        }
        else if (target.getType() == HitResult.Type.BLOCK) {
            float f = Math.min(16.0f, 2 * getSettingValue("power") * finalPower);
            int fi = (int) f;
            for (BlockPos blockpos$mutableblockpos1 : BlockPos.betweenClosed(((net.minecraft.world.phys.BlockHitResult)target).getBlockPos().offset(-fi, -fi, -fi), ((net.minecraft.world.phys.BlockHitResult)target).getBlockPos().offset(fi, fi, fi))) {
                net.minecraft.world.phys.Vec3 hitVec = target.getLocation();
                if (blockpos$mutableblockpos1.distToCenterSqr(hitVec.x, hitVec.y, hitVec.z) <= f * f) {
                    BlockState iblockstate1 = getPackage().world.getBlockState(blockpos$mutableblockpos1);
                    if (!iblockstate1.canBeReplaced() /* was: getMaterial() != WATER check */) {
                        continue;
                    }
                    getPackage().world.setBlockAndUpdate(blockpos$mutableblockpos1, Blocks.FROSTED_ICE.defaultBlockState());
                    getPackage().world.scheduleTick(blockpos$mutableblockpos1.toImmutable(), Blocks.FROSTED_ICE, Mth.randomBetweenInclusive(getPackage().world.getRandom(), 60, 120));
                }
            }
        }
        return false;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] { new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5)), new NodeSetting("duration", "focus.common.duration", new NodeSetting.NodeSettingIntRange(2, 10)) };
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderParticleFX(Level world, double x, double y, double z, double vx, double vy, double vz) {
        FXGeneric fb = new FXGeneric(world, x, y, z, vx, vy, vz);
        fb.setMaxAge(40 + getPackage().world.getRandom().nextInt(40));
        fb.setAlphaF(1.0f, 0.0f);
        fb.setParticles(8, 1, 1);
        fb.setGravity(0.033f);
        fb.setSlowDown(0.8);
        fb.setRandomMovementScale(0.0025f, 1.0E-4f, 0.0025f);
        fb.setScale((float)(0.699999988079071 + getPackage().world.getRandom().nextGaussian() * 0.30000001192092896));
        fb.setRotationSpeed(getPackage().world.getRandom().nextFloat() * 3.0f, (float)getPackage().world.getRandom().nextGaussian() / 4.0f);
        ParticleEngine.addEffectWithDelay(world, fb, 0);
    }
    
    @Override
    public void onCast(Entity caster) {
        caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 0.2f, 1.0f + (float)(caster.level().getRandom().nextGaussian() * 0.05000000074505806));
    }
}
