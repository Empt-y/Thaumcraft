package thaumcraft.common.items.casters.foci;
import java.util.Iterator;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockBamf;


public class FocusEffectCurse extends FocusEffect
{
    @Override
    public String getResearch() {
        return "FOCUSCURSE";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.CURSE";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.DEATH;
    }
    
    @Override
    public int getComplexity() {
        return getSettingValue("duration") + getSettingValue("power") * 3;
    }
    
    @Override
    public float getDamageForDisplay(float finalPower) {
        return (1.0f + getSettingValue("power")) * finalPower;
    }
    
    @Override
    public boolean execute(HitResult target, Trajectory trajectory, float finalPower, int num) {
        /* sendToAllAround stub */
        if (target.getType() == HitResult.Type.ENTITY && ((net.minecraft.world.phys.EntityHitResult)target).getEntity() != null) {
            float damage = getDamageForDisplay(finalPower);
            int duration = 20 * getSettingValue("duration");
            int eff = (int)(getSettingValue("power") * finalPower / 2.0f);
            if (eff < 0) {
                eff = 0;
            }
            ((net.minecraft.world.phys.EntityHitResult)target).getEntity().hurt(world.damageSources().indirectMagic((((net.minecraft.world.phys.EntityHitResult)target).getEntity() != null) ? ((net.minecraft.world.phys.EntityHitResult)target).getEntity() : getPackage().getCaster(), getPackage().getCaster()), damage);
            if (((net.minecraft.world.phys.EntityHitResult)target).getEntity() instanceof LivingEntity) {
                ((LivingEntity)((net.minecraft.world.phys.EntityHitResult)target).getEntity()).addEffect(new MobEffectInstance(MobEffects.POISON, duration, Math.round((float)eff)));
                float c = 0.85f;
                if (getPackage().world.getRandom().nextFloat() < c) {
                    ((LivingEntity)((net.minecraft.world.phys.EntityHitResult)target).getEntity()).addEffect(new MobEffectInstance(MobEffects.SLOWNESS, duration, Math.round((float)eff)));
                    c -= 0.15f;
                }
                if (getPackage().world.getRandom().nextFloat() < c) {
                    ((LivingEntity)((net.minecraft.world.phys.EntityHitResult)target).getEntity()).addEffect(new MobEffectInstance(MobEffects.WEAKNESS, duration, Math.round((float)eff)));
                    c -= 0.15f;
                }
                if (getPackage().world.getRandom().nextFloat() < c) {
                    ((LivingEntity)((net.minecraft.world.phys.EntityHitResult)target).getEntity()).addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, duration * 2, Math.round((float)eff)));
                    c -= 0.15f;
                }
                if (getPackage().world.getRandom().nextFloat() < c) {
                    ((LivingEntity)((net.minecraft.world.phys.EntityHitResult)target).getEntity()).addEffect(new MobEffectInstance(MobEffects.HUNGER, duration * 3, Math.round((float)eff)));
                    c -= 0.15f;
                }
                if (getPackage().world.getRandom().nextFloat() < c) {
                    ((LivingEntity)((net.minecraft.world.phys.EntityHitResult)target).getEntity()).addEffect(new MobEffectInstance(MobEffects.UNLUCK, duration * 3, Math.round((float)eff)));
                }
            }
        }
        else if (target.getType() == HitResult.Type.BLOCK) {
            float f = (float)Math.min(8.0, 1.5 * getSettingValue("power") * finalPower);
            for (BlockPos.MutableBlockPos blockpos$mutableblockpos1 : BlockPos.betweenClosed(((net.minecraft.world.phys.BlockHitResult)target).getBlockPos().offset((int)(-f), (int)(-f), (int)(-f)), ((net.minecraft.world.phys.BlockHitResult)target).getBlockPos().offset((int)f, (int)f, (int)f))) {
                if (blockpos$mutableblockpos1.distanceTo(new net.minecraft.world.phys.Vec3(target.getLocation().x, target.getLocation().y, target.getLocation().z)) <= f * f && getPackage().world.isEmptyBlock(blockpos$mutableblockpos1.above())) {
                    getPackage().world.setBlockAndUpdate(blockpos$mutableblockpos1.above(), BlocksTC.effectSap.defaultBlockState());
                }
            }
        }
        return false;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] { new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5)), new NodeSetting("duration", "focus.common.duration", new NodeSetting.NodeSettingIntRange(1, 10)) };
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderParticleFX(Level world, double x, double y, double z, double vx, double vy, double vz) {
        FXGeneric fb = new FXGeneric(world, getX(), getY(), getZ(), vx, vy, vz);
        fb.setMaxAge(8);
        fb.setRBGColorF(0.41f + getPackage().world.getRandom().nextFloat() * 0.2f, 0.0f, 0.019f + getPackage().world.getRandom().nextFloat() * 0.2f);
        fb.setAlphaF(0.0f, getPackage().world.getRandom().nextFloat(), getPackage().world.getRandom().nextFloat(), getPackage().world.getRandom().nextFloat(), 0.0f);
        fb.setGridSize(16);
        fb.setParticles(72 + getPackage().world.getRandom().nextInt(4), 1, 1);
        fb.setScale(2.0f + getPackage().world.getRandom().nextFloat() * 4.0f);
        fb.setLoop(false);
        fb.setSlowDown(0.9);
        fb.setGravity(0.0f);
        fb.setRotationSpeed(getPackage().world.getRandom().nextFloat(), 0.0f);
        ParticleEngine.addEffectWithDelay(world, fb, getPackage().world.getRandom().nextInt(4));
    }
    
    @Override
    public void onCast(Entity caster) {
        caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.ELDER_GUARDIAN_CURSE, SoundSource.PLAYERS, 0.15f, 1.0f + caster.level().getRandom().nextFloat() / 2.0f);
    }
}
