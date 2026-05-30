package thaumcraft.common.items.casters.foci;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.tiles.misc.TileHole;


public class FocusEffectRift extends FocusEffect
{
    @Override
    public String getResearch() {
        return "FOCUSRIFT";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.RIFT";
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.ELDRITCH;
    }
    
    @Override
    public int getComplexity() {
        return 3 + getSettingValue("duration") / 2 + getSettingValue("depth") / 4;
    }
    
    @Override
    public boolean execute(HitResult target, Trajectory trajectory, float finalPower, int num) {
        if (target.getType() != HitResult.Type.BLOCK) {
            return false;
        }
        if ((getPackage().world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)getPackage().world).dimension().identifier().hashCode() : 0) == ModConfig.CONFIG_WORLD.dimensionOuterId) {
            getPackage().world.playSound(null, ((net.minecraft.world.phys.BlockHitResult)target).getBlockPos().getX() + 0.5, ((net.minecraft.world.phys.BlockHitResult)target).getBlockPos().getY() + 0.5, ((net.minecraft.world.phys.BlockHitResult)target).getBlockPos().getZ() + 0.5, SoundsTC.wandfail, SoundSource.PLAYERS, 1.0f, 1.0f);
            return false;
        }
        float maxdis = getSettingValue("depth") * finalPower;
        int dur = 20 * getSettingValue("duration");
        int distance = 0;
        BlockPos pos = new BlockPos(((net.minecraft.world.phys.BlockHitResult)target).getBlockPos());
        for (distance = 0; distance < maxdis; ++distance) {
            BlockState bi = getPackage().world.getBlockState(pos);
            if (BlockUtils.isPortableHoleBlackListed(bi) || bi.getBlock() == Blocks.BEDROCK || bi.getBlock() == BlocksTC.hole || bi.isAir()) {
                break;
            }
            if (bi.getDestroySpeed(null, net.minecraft.core.BlockPos.ZERO) == -1.0f) {
                break;
            }
            pos = pos.relative(((net.minecraft.world.phys.BlockHitResult)target).getDirection().getOpposite());
        }
        createHole(getPackage().world, ((net.minecraft.world.phys.BlockHitResult)target).getBlockPos(), ((net.minecraft.world.phys.BlockHitResult)target).getDirection(), (byte)Math.round((float)(distance + 1)), dur);
        return true;
    }
    
    public static boolean createHole(Level world, BlockPos pos, Direction side, byte count, int max) {
        BlockState bs = world.getBlockState(pos);
        if (!world.isClientSide() && world.getBlockEntity(pos) == null && !BlockUtils.isPortableHoleBlackListed(bs) && bs.getBlock() != Blocks.BEDROCK && bs.getBlock() != BlocksTC.hole && (bs.isAir() || !bs.isSolid()) && bs.getDestroySpeed(null, net.minecraft.core.BlockPos.ZERO) != -1.0f) {
            if (world.setBlockAndUpdate(pos, BlocksTC.hole.defaultBlockState())) {
                TileHole ts = (TileHole)world.getBlockEntity(pos);
                ts.oldblock = bs;
                ts.countdownmax = (short)max;
                ts.count = count;
                ts.direction = side;
                ts.setChanged();
            }
            return true;
        }
        return false;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        int[] depth = { 8, 16, 24, 32 };
        String[] depthDesc = { "8", "16", "24", "32" };
        return new NodeSetting[] { new NodeSetting("depth", "focus.rift.depth", new NodeSetting.NodeSettingIntList(depth, depthDesc)), new NodeSetting("duration", "focus.common.duration", new NodeSetting.NodeSettingIntRange(2, 10)) };
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void renderParticleFX(Level world, double x, double y, double z, double vx, double vy, double vz) {
        FXGeneric fb = new FXGeneric(world, x, y, z, vx, vy, vz);
        fb.setMaxAge(16 + net.minecraft.util.RandomSource.create().nextInt(16));
        fb.setParticles(384 + net.minecraft.util.RandomSource.create().nextInt(16), 1, 1);
        fb.setSlowDown(0.75);
        fb.setAlphaF(1.0f, 0.0f);
        fb.setScale((float)(0.699999988079071 + net.minecraft.util.RandomSource.create().nextGaussian() * 0.30000001192092896));
        fb.setRBGColorF(0.25f, 0.25f, 1.0f);
        fb.setRandomMovementScale(0.01f, 0.01f, 0.01f);
        ParticleEngine.addEffectWithDelay(world, fb, 0);
    }
    
    @Override
    public void onCast(Entity caster) {
        caster.level().playSound(null, caster.blockPosition().above(), SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 0.2f, 0.7f);
    }
}
