package thaumcraft.common.blocks.misc;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.potions.PotionWarpWard;

/**
 * Purifying fluid block. In 1.12.2 this extended BlockFluidClassic; in 1.21.x fluid blocks
 * extend LiquidBlock. Full fluid registration (forge fluid + bucket) is TODO.
 */
public class BlockFluidPure extends Block
{
    public BlockFluidPure() {
        super(BlockBehaviour.Properties.of()
                .sound(net.minecraft.world.level.block.SoundType.STONE)
                .mapColor(MapColor.WATER)
                .noCollision()
                .strength(100.0f)
                .noLootTable());
        // TODO: register as a NeoForge fluid with bucket item
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity,
            InsideBlockEffectApplier applier, boolean isMovementBlocked) {
        entity.setDeltaMovement(entity.getDeltaMovement().scale(0.5));

        if (!world.isClientSide() && entity instanceof Player player) {
            Holder<MobEffect> warpWardHolder = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionWarpWard.instance);
            if (!player.hasEffect(warpWardHolder)) {
                int warp = ThaumcraftCapabilities.getWarp(player).get(IPlayerWarp.EnumWarpType.PERMANENT);
                int div = Math.max(1, warp > 0 ? (int) Math.sqrt(warp) : 1);
                player.addEffect(new MobEffectInstance(warpWardHolder, Math.min(32000, 200000 / div), 0, true, true));
                world.removeBlock(pos, false);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        if (rand.nextInt(10) == 0) {
            FXGeneric fb = new FXGeneric(world, pos.getX() + rand.nextFloat(), pos.getY() + 0.125f * 4,
                    pos.getZ() + rand.nextFloat(), 0.0, 0.0, 0.0);
            fb.setMaxAge(10 + rand.nextInt(10));
            fb.setScale(rand.nextFloat() * 0.3f + 0.3f);
            fb.setRBGColorF(1.0f, 1.0f, 1.0f);
            fb.setRandomMovementScale(0.001f, 0.001f, 0.001f);
            fb.setGravity(-0.01f);
            fb.setAlphaF(0.25f);
            fb.setParticle(64);
            fb.setFinalFrames(65, 66);
            ParticleEngine.addEffect(world, fb);
        }
        if (rand.nextInt(50) == 0) {
            double x = pos.getX() + rand.nextFloat();
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + rand.nextFloat();
            world.playSound(null, x, y, z, SoundEvents.LAVA_POP, SoundSource.BLOCKS,
                    0.1f + rand.nextFloat() * 0.1f, 0.9f + rand.nextFloat() * 0.15f);
        }
    }
}
