package thaumcraft.common.items.casters.foci;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundSource;
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


public class FocusEffectFlux extends FocusEffect
{
    @Override
    public String getResearch() {
        return "FOCUSFLUX";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.FLUX";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.FLUX;
    }
    
    @Override
    public int getComplexity() {
        return getSettingValue("power") * 3;
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
            ((net.minecraft.world.phys.EntityHitResult)target).getEntity().hurt(getPackage().world.damageSources().indirectMagic((((net.minecraft.world.phys.EntityHitResult)target).getEntity() != null) ? ((net.minecraft.world.phys.EntityHitResult)target).getEntity() : getPackage().getCaster(), getPackage().getCaster()), damage);
        }
        return false;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] { new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5)) };
    }
    
    @Override
    public void onCast(Entity caster) {
        caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.CHORUS_FLOWER_GROW, SoundSource.PLAYERS, 2.0f, 2.0f + (float)(caster.level().getRandom().nextGaussian() * 0.10000000149011612));
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderParticleFX(Level world, double x, double y, double z, double vx, double vy, double vz) {
        FXGeneric fb = new FXGeneric(world, x, y, z, vx + getPackage().world.getRandom().nextGaussian() * 0.01, vy + getPackage().world.getRandom().nextGaussian() * 0.01, vz + getPackage().world.getRandom().nextGaussian() * 0.01);
        fb.setMaxAge((int)(15.0f + 10.0f * getPackage().world.getRandom().nextFloat()));
        fb.setRBGColorF(0.25f + getPackage().world.getRandom().nextFloat() * 0.25f, 0.0f, 0.25f + getPackage().world.getRandom().nextFloat() * 0.25f);
        fb.setAlphaF(0.0f, 1.0f, 1.0f, 0.0f);
        fb.setGridSize(64);
        fb.setParticles(128, 14, 1);
        fb.setScale(2.0f + getPackage().world.getRandom().nextFloat(), 0.25f + getPackage().world.getRandom().nextFloat() * 0.25f);
        fb.setLoop(true);
        fb.setSlowDown(0.9);
        fb.setGravity((float)(getPackage().world.getRandom().nextGaussian() * 0.10000000149011612));
        fb.setRandomMovementScale(0.0125f, 0.0125f, 0.0125f);
        fb.setRotationSpeed((float)getPackage().world.getRandom().nextGaussian());
        ParticleEngine.addEffectWithDelay(world, fb, getPackage().world.getRandom().nextInt(4));
    }
}
