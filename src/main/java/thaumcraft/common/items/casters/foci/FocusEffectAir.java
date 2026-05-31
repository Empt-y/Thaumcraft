package thaumcraft.common.items.casters.foci;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundSource;
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
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;


public class FocusEffectAir extends FocusEffect
{
    @Override
    public String getResearch() {
        return "FOCUSELEMENTAL";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.AIR";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.AIR;
    }
    
    @Override
    public int getComplexity() {
        return getSettingValue("power") * 2;
    }
    
    @Override
    public float getDamageForDisplay(float finalPower) {
        return (1 + getSettingValue("power")) * finalPower;
    }
    
    @Override
    public boolean execute(HitResult target, Trajectory trajectory, float finalPower, int num) {
        if (getPackage().world instanceof net.minecraft.server.level.ServerLevel sl) {
            PacketHandler.sendToAllAround(new PacketFXFocusPartImpact(target.getLocation().x, target.getLocation().y, target.getLocation().z, new String[]{getKey()}), sl, target.getLocation().x, target.getLocation().y, target.getLocation().z, 64.0);
        }
        getPackage().world.playSound(null, target.getLocation().x, target.getLocation().y, target.getLocation().z, SoundEvents.ENDER_DRAGON_FLAP, SoundSource.PLAYERS, 0.5f, 0.66f);
        if (target.getType() == HitResult.Type.ENTITY && ((net.minecraft.world.phys.EntityHitResult)target).getEntity() != null) {
            float damage = getDamageForDisplay(finalPower);
            ((net.minecraft.world.phys.EntityHitResult)target).getEntity().hurt(getPackage().world.damageSources().thrown((((net.minecraft.world.phys.EntityHitResult)target).getEntity() != null) ? ((net.minecraft.world.phys.EntityHitResult)target).getEntity() : getPackage().getCaster(), getPackage().getCaster()), damage);
            if (((net.minecraft.world.phys.EntityHitResult)target).getEntity() instanceof LivingEntity) {
                if (trajectory != null) {
                    ((LivingEntity)((net.minecraft.world.phys.EntityHitResult)target).getEntity()).knockback(damage * 0.25f, -trajectory.direction.x, -trajectory.direction.z);
                }
                else {
                    ((LivingEntity)((net.minecraft.world.phys.EntityHitResult)target).getEntity()).knockback(damage * 0.25f, -Mth.sin(((net.minecraft.world.phys.EntityHitResult)target).getEntity().getYRot() * 0.017453292f), Mth.cos(((net.minecraft.world.phys.EntityHitResult)target).getEntity().getYRot() * 0.017453292f));
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] { new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5)) };
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderParticleFX(Level world, double x, double y, double z, double vx, double vy, double vz) {
        FXDispatcher.GenPart pp = new FXDispatcher.GenPart();
        pp.grav = -0.1f;
        pp.age = 20 + net.minecraft.util.RandomSource.create().nextInt(10);
        pp.alpha = new float[] { 0.5f, 0.0f };
        pp.grid = 32;
        pp.partStart = 337;
        pp.partInc = 1;
        pp.partNum = 5;
        pp.slowDown = 0.75;
        pp.rot = (float)net.minecraft.util.RandomSource.create().nextGaussian() / 2.0f;
        float s = (float)(2.0 + net.minecraft.util.RandomSource.create().nextGaussian() * 0.5);
        pp.scale = new float[] { s, s * 2.0f };
        FXDispatcher.INSTANCE.drawGenericParticles(x, y, z, vx, vy, vz, pp);
    }
    
    @Override
    public void onCast(Entity caster) {
        caster.level().playSound(null, caster.blockPosition().above(), SoundsTC.wind, SoundSource.PLAYERS, 0.125f, 2.0f);
    }
}
