package thaumcraft.common.items.casters.foci;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.IFocusBlockPicker;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.items.casters.ItemCaster;
import thaumcraft.common.lib.events.ServerEvents;


public class FocusEffectExchange extends FocusEffect implements IFocusBlockPicker
{
    @Override
    public String getResearch() {
        return "FOCUSEXCHANGE";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.EXCHANGE";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.EXCHANGE;
    }
    
    @Override
    public int getComplexity() {
        return (5 + getSettingValue("silk") * 4 + getSettingValue("fortune") == 0) ? 0 : ((getSettingValue("fortune") + 1) * 3);
    }
    
    @Override
    public boolean execute(HitResult target, Trajectory trajectory, float finalPower, int num) {
        if (target.getType() != HitResult.Type.BLOCK) {
            return false;
        }
        ItemStack casterStack = ItemStack.EMPTY;
        if (getPackage().getCaster().getMainHandItem() != null && getPackage().getCaster().getMainHandItem().getItem() instanceof ItemCaster) {
            casterStack = getPackage().getCaster().getMainHandItem();
        }
        else if (getPackage().getCaster().getOffhandItem() != null && getPackage().getCaster().getOffhandItem().getItem() instanceof ItemCaster) {
            casterStack = getPackage().getCaster().getOffhandItem();
        }
        if (casterStack.isEmpty()) {
            return false;
        }
        boolean silk = getSettingValue("silk") > 0;
        int fortune = getSettingValue("fortune");
        if (getPackage().getCaster() instanceof Player && ((ItemCaster)casterStack.getItem()).getPickedBlock(casterStack) != null && !((ItemCaster)casterStack.getItem()).getPickedBlock(casterStack).isEmpty()) {
            ServerEvents.addSwapper(getPackage().world, ((net.minecraft.world.phys.BlockHitResult)target).getBlockPos(), getPackage().world.getBlockState(((net.minecraft.world.phys.BlockHitResult)target).getBlockPos()), ((ItemCaster)casterStack.getItem()).getPickedBlock(casterStack), true, 0, (Player) getPackage().getCaster(), true, false, 8038177, true, silk, fortune, ServerEvents.DEFAULT_PREDICATE, 0.25f + (silk ? 0.25f : 0.0f) + fortune * 0.1f);
        }
        return true;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        int[] silk = { 0, 1 };
        String[] silkDesc = { "focus.common.no", "focus.common.yes" };
        int[] fortune = { 0, 1, 2, 3, 4 };
        String[] fortuneDesc = { "focus.common.no", "I", "II", "III", "IV" };
        return new NodeSetting[] { new NodeSetting("fortune", "focus.common.fortune", new NodeSetting.NodeSettingIntList(fortune, fortuneDesc)), new NodeSetting("silk", "focus.common.silk", new NodeSetting.NodeSettingIntList(silk, silkDesc)) };
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderParticleFX(Level world, double x, double y, double z, double vx, double vy, double vz) {
        FXGeneric fb = new FXGeneric(world, x, y, z, vx + getPackage().world.getRandom().nextGaussian() * 0.01, vy + getPackage().world.getRandom().nextGaussian() * 0.01, vz + getPackage().world.getRandom().nextGaussian() * 0.01);
        fb.setMaxAge(9);
        fb.setRBGColorF(0.25f + getPackage().world.getRandom().nextFloat() * 0.25f, 0.25f + getPackage().world.getRandom().nextFloat() * 0.25f, 0.25f + getPackage().world.getRandom().nextFloat() * 0.25f);
        fb.setAlphaF(0.0f, 0.6f, 0.6f, 0.0f);
        fb.setGridSize(64);
        fb.setParticles(448, 9, 1);
        fb.setScale(0.5f, 0.25f);
        fb.setGravity((float)(getPackage().world.getRandom().nextGaussian() * 0.009999999776482582));
        fb.setRandomMovementScale(0.0025f, 0.0025f, 0.0025f);
        ParticleEngine.addEffect(world, fb);
    }
    
    @Override
    public void onCast(Entity caster) {
        caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.2f, 2.0f + (float)(caster.level().getRandom().nextGaussian() * 0.05000000074505806));
    }
}
