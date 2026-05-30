package thaumcraft.common.items.casters.foci;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.sounds.SoundEvents;
// DamageSource removed
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
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXFocusPartImpact;


public class FocusEffectFire extends FocusEffect
{
    @Override
    public String getResearch() {
        return "BASEAUROMANCY";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.FIRE";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.FIRE;
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
        if (target.getType() != HitResult.Type.ENTITY || ((net.minecraft.world.phys.EntityHitResult)target).getEntity() == null) {
            if (target.getType() == HitResult.Type.BLOCK && getSettingValue("duration") > 0) {
                BlockPos pos = ((net.minecraft.world.phys.BlockHitResult)target).getBlockPos();
                pos = pos.relative(((net.minecraft.world.phys.BlockHitResult)target).getDirection());
                if (getPackage().world.isEmptyBlock(pos) && getPackage().world.getRandom().nextFloat() < finalPower) {
                    getPackage().world.playSound(null, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0f, getPackage().world.getRandom().nextFloat() * 0.4f + 0.8f);
                    getPackage().world.setBlock(pos, Blocks.FIRE.defaultBlockState(), 11);
                    return true;
                }
            }
            return false;
        }
        if (((net.minecraft.world.phys.EntityHitResult)target).getEntity().isImmuneToFire()) {
            return false;
        }
        float fire = (float)(1 + getSettingValue("duration") * getSettingValue("duration"));
        float damage = getDamageForDisplay(finalPower);
        fire *= finalPower;
        ((net.minecraft.world.phys.EntityHitResult)target).getEntity().hurt(new DamageSource("fireball", (((net.minecraft.world.phys.EntityHitResult)target).getEntity() != null) ? ((net.minecraft.world.phys.EntityHitResult)target).getEntity() : getPackage().getCaster(), getPackage().getCaster()).setFireDamage(), damage);
        if (fire > 0.0f) {
            ((net.minecraft.world.phys.EntityHitResult)target).getEntity().setFire(Math.round(fire));
        }
        return true;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        return new NodeSetting[] { new NodeSetting("power", "focus.common.power", new NodeSetting.NodeSettingIntRange(1, 5)), new NodeSetting("duration", "focus.fire.burn", new NodeSetting.NodeSettingIntRange(0, 5)) };
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderParticleFX(Level world, double x, double y, double z, double vx, double vy, double vz) {
        FXDispatcher.GenPart pp = new FXDispatcher.GenPart();
        pp.grav = -0.2f;
        pp.age = 10;
        pp.alpha = new float[] { 0.7f };
        pp.partStart = 640;
        pp.partInc = 1;
        pp.partNum = 10;
        pp.slowDown = 0.75;
        pp.scale = new float[] { (float)(1.5 + getPackage().world.getRandom().nextGaussian() * 0.20000000298023224) };
        FXDispatcher.INSTANCE.drawGenericParticles(x, y, z, vx, vy, vz, pp);
    }
    
    @Override
    public void onCast(Entity caster) {
        caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.ITEM_FIRECHARGE_USE, SoundSource.PLAYERS, 1.0f, 1.0f + (float)(caster.level().getRandom().nextGaussian() * 0.05000000074505806));
    }
}
