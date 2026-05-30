package thaumcraft.common.items.casters.foci;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
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
import thaumcraft.common.lib.events.ServerEvents;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;


public class FocusEffectEarth extends FocusEffect
{
    @Override
    public String getResearch() {
        return "FOCUSELEMENTAL";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.EARTH";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.EARTH;
    }
    
    @Override
    public int getComplexity() {
        return getSettingValue("power") * 3;
    }
    
    @Override
    public float getDamageForDisplay(float finalPower) {
        return 2 * getSettingValue("power") * finalPower;
    }
    
    @Override
    public boolean execute(HitResult target, Trajectory trajectory, float finalPower, int num) {
        /* sendToAllAround stub */
        if (target.getType() == HitResult.Type.ENTITY && ((net.minecraft.world.phys.EntityHitResult)target).getEntity() != null) {
            float damage = getDamageForDisplay(finalPower);
            ((net.minecraft.world.phys.EntityHitResult)target).getEntity().hurt(level().damageSources().thrown((((net.minecraft.world.phys.EntityHitResult)target).getEntity() != null) ? ((net.minecraft.world.phys.EntityHitResult)target).getEntity() : getPackage().getCaster(), getPackage().getCaster()), damage);
            return true;
        }
        if (target.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((net.minecraft.world.phys.BlockHitResult)target).getBlockPos();
            if (getPackage().getCaster() instanceof Player && getPackage().world.getBlockState(pos).getDestroySpeed(getPackage().world, pos) <= getDamageForDisplay(finalPower) / 25.0f) {
                ServerEvents.addBreaker(getPackage().world, pos, getPackage().world.getBlockState(pos), (Player) getPackage().getCaster(), false, false, 0, 1.0f, 0.0f, 1.0f, num, 0.1f, null);
            }
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
        pp.grav = 0.4f;
        pp.layer = 1;
        pp.age = 20 + getPackage().world.getRandom().nextInt(10);
        pp.alpha = new float[] { 1.0f, 0.0f };
        pp.partStart = 75 + getPackage().world.getRandom().nextInt(4);
        pp.partInc = 1;
        pp.partNum = 1;
        pp.slowDown = 0.9;
        pp.rot = (float)getPackage().world.getRandom().nextGaussian();
        float s = (float)(1.0 + getPackage().world.getRandom().nextGaussian() * 0.20000000298023224);
        pp.scale = new float[] { s, s / 2.0f };
        FXDispatcher.INSTANCE.drawGenericParticles(getX(), getY(), getZ(), getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z, pp);
    }
    
    @Override
    public void onCast(Entity caster) {
        caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.ENDERDRAGON_FIREBALL_EPLD, SoundSource.PLAYERS, 0.25f, 1.0f + (float)(caster.level().getRandom().nextGaussian() * 0.05000000074505806));
    }
}
