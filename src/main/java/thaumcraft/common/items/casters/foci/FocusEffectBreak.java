package thaumcraft.common.items.casters.foci;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
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
import thaumcraft.common.lib.events.ServerEvents;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;


public class FocusEffectBreak extends FocusEffect
{
    @Override
    public String getResearch() {
        return "FOCUSBREAK";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.BREAK";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.ENTROPY;
    }
    
    @Override
    public int getComplexity() {
        return getSettingValue("power") * 3 + getSettingValue("silk") * 4 + ((getSettingValue("fortune") == 0) ? 0 : ((getSettingValue("fortune") + 1) * 3));
    }
    
    @Override
    public boolean execute(HitResult target, Trajectory trajectory, float finalPower, int num) {
        if (target.getType() == HitResult.Type.BLOCK) {
            /* sendToAllAround stub */
            boolean silk = getSettingValue("silk") > 0;
            int fortune = getSettingValue("fortune");
            float strength = getSettingValue("power") * finalPower;
            float dur = getPackage().world.getBlockState(((net.minecraft.world.phys.BlockHitResult)target).getBlockPos()).getDestroySpeed(getPackage().world, ((net.minecraft.world.phys.BlockHitResult)target).getBlockPos()) * 100.0f;
            dur = (float)Math.sqrt(dur);
            if (getPackage().getCaster() instanceof Player) {
                ServerEvents.addBreaker(getPackage().world, ((net.minecraft.world.phys.BlockHitResult)target).getBlockPos(), getPackage().world.getBlockState(((net.minecraft.world.phys.BlockHitResult)target).getBlockPos()), (Player) getPackage().getCaster(), true, silk, fortune, strength, dur, dur, (int)(dur / strength / 3.0f * num), 0.25f + (silk ? 0.25f : 0.0f) + fortune * 0.1f, null);
            }
            return true;
        }
        return true;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        int[] silk = { 0, 1 };
        String[] silkDesc = { "focus.common.no", "focus.common.yes" };
        int[] fortune = { 0, 1, 2, 3, 4 };
        String[] fortuneDesc = { "focus.common.no", "I", "II", "III", "IV" };
        return new NodeSetting[] { new NodeSetting("power", "focus.break.power", new NodeSetting.NodeSettingIntRange(1, 5)), new NodeSetting("fortune", "focus.common.fortune", new NodeSetting.NodeSettingIntList(fortune, fortuneDesc)), new NodeSetting("silk", "focus.common.silk", new NodeSetting.NodeSettingIntList(silk, silkDesc)) };
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderParticleFX(Level world, double x, double y, double z, double vx, double vy, double vz) {
        FXGeneric fb = new FXGeneric(world, getX(), getY(), getZ(), vx, vy, vz);
        fb.setMaxAge(6 + getPackage().world.getRandom().nextInt(6));
        int q = getPackage().world.getRandom().nextInt(4);
        fb.setParticles(704 + q * 3, 3, 1);
        fb.setSlowDown(0.8);
        fb.setScale((float)(1.7000000476837158 + getPackage().world.getRandom().nextGaussian() * 0.30000001192092896));
        ParticleEngine.addEffect(world, fb);
    }
    
    @Override
    public void onCast(Entity caster) {
        caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.END_PORTAL_SPAWN, SoundSource.PLAYERS, 0.1f, 2.0f + (float)(caster.level().getRandom().nextGaussian() * 0.05000000074505806));
    }
}
