package thaumcraft.common.items.casters.foci;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;


public class FocusEffectHeal extends FocusEffect
{
    @Override
    public String getResearch() {
        return "FOCUSHEAL";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.HEAL";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.LIFE;
    }
    
    @Override
    public int getComplexity() {
        return getSettingValue("power") * 4;
    }
    
    @Override
    public float getDamageForDisplay(float finalPower) {
        return -getSettingValue("power") * finalPower;
    }
    
    @Override
    public boolean execute(HitResult target, Trajectory trajectory, float finalPower, int num) {
        if (getPackage().world instanceof net.minecraft.server.level.ServerLevel sl) {
            PacketHandler.sendToAllAround(new PacketFXFocusPartImpact(target.getLocation().x, target.getLocation().y, target.getLocation().z, new String[]{getKey()}), sl, target.getLocation().x, target.getLocation().y, target.getLocation().z, 64.0);
        }
        if (target.getType() == HitResult.Type.ENTITY && ((net.minecraft.world.phys.EntityHitResult)target).getEntity() != null && ((net.minecraft.world.phys.EntityHitResult)target).getEntity() instanceof LivingEntity) {
            if (((LivingEntity)((net.minecraft.world.phys.EntityHitResult)target).getEntity()).getType().builtInRegistryHolder().is(net.minecraft.tags.EntityTypeTags.UNDEAD)) {
                ((net.minecraft.world.phys.EntityHitResult)target).getEntity().hurt(getPackage().world.damageSources().indirectMagic(getPackage().getCaster(), getPackage().getCaster()), getSettingValue("power") * finalPower * 1.5f);
            }
            else {
                ((LivingEntity)((net.minecraft.world.phys.EntityHitResult)target).getEntity()).heal(getSettingValue("power") * finalPower);
            }
        }
        return false;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] { new NodeSetting("power", "focus.heal.power", new NodeSetting.NodeSettingIntRange(1, 5)) };
    }
    
    @Override
    public void onCast(Entity caster) {
        caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.CHORUS_FLOWER_GROW, SoundSource.PLAYERS, 2.0f, 2.0f + (float)(caster.level().getRandom().nextGaussian() * 0.10000000149011612));
    }
    
    @Override
    public void renderParticleFX(Level world, double x, double y, double z, double vx, double vy, double vz) {
        FXGeneric fb = new FXGeneric(world, x, y, z, vx + getPackage().world.getRandom().nextGaussian() * 0.01, vy + getPackage().world.getRandom().nextGaussian() * 0.01, vz + getPackage().world.getRandom().nextGaussian() * 0.01);
        fb.setMaxAge((int)(10.0f + 10.0f * getPackage().world.getRandom().nextFloat()));
        fb.setRBGColorF(1.0f, 1.0f, 1.0f);
        fb.setAlphaF(0.0f, 0.7f, 0.7f, 0.0f);
        fb.setGridSize(64);
        fb.setParticles(0, 1, 1);
        fb.setScale(getPackage().world.getRandom().nextFloat() * 2.0f, getPackage().world.getRandom().nextFloat());
        fb.setSlowDown(0.8);
        fb.setGravity((float)(getPackage().world.getRandom().nextGaussian() * 0.10000000149011612));
        fb.setRandomMovementScale(0.0125f, 0.0125f, 0.0125f);
        fb.setRotationSpeed((float)getPackage().world.getRandom().nextGaussian());
        ParticleEngine.addEffectWithDelay(world, fb, getPackage().world.getRandom().nextInt(4));
    }
}
