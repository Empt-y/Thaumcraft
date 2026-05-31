package thaumcraft.common.blocks.world.taint;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.potions.PotionVisExhaust;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.entities.monster.EntityThaumicSlime;
import thaumcraft.common.lib.SoundsTC;

/**
 * Flux goo — finite taint fluid, ported from 1.12 BlockFluidFinite to a plain Block
 * with custom randomTick spreading. Level 0-7 (like old fluid quanta).
 */
public class BlockFluxGoo extends Block
{
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, 7);

    public BlockFluxGoo() {
        super(defaultProps());
        registerDefaultState(stateDefinition.any().setValue(LEVEL, 7));
    }

    public static BlockBehaviour.Properties defaultProps() {
        return BlockBehaviour.Properties.of()
                .sound(net.minecraft.world.level.block.SoundType.SLIME_BLOCK)
                .strength(100.0f, 100.0f)
                .noCollision()
                .randomTicks()
                .noLootTable();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity,
            InsideBlockEffectApplier applier, boolean isMovementBlocked) {
        int meta = state.getValue(LEVEL);
        double quanta = meta / 8.0;

        entity.setDeltaMovement(
                entity.getDeltaMovement().multiply(1.0 - quanta, 1.0, 1.0 - quanta));

        if (!world.isClientSide()) {
            if (entity instanceof EntityThaumicSlime slime) {
                if (slime.getSize() < meta && world.getRandom().nextBoolean()) {
                    slime.setSize(slime.getSize() + 1, true);
                    if (meta > 1) {
                        world.setBlock(pos, state.setValue(LEVEL, meta - 1), 2);
                    } else {
                        world.removeBlock(pos, false);
                    }
                }
            } else if (entity instanceof LivingEntity living) {
                Holder<MobEffect> effect = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionVisExhaust.instance);
                living.addEffect(new MobEffectInstance(effect, 600, meta / 3, true, true));
            }
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
        int meta = state.getValue(LEVEL);

        if (meta >= 2 && meta < 6 && world.isEmptyBlock(pos.above()) && rand.nextInt(50) == 0) {
            spawnSlime(world, pos, 1);
            world.removeBlock(pos, false);
            return;
        }
        if (meta >= 6 && world.isEmptyBlock(pos.above()) && rand.nextInt(50) == 0) {
            spawnSlime(world, pos, 2);
            world.removeBlock(pos, false);
            return;
        }

        if (rand.nextInt(4) == 0) {
            if (meta == 0) {
                if (rand.nextBoolean()) {
                    AuraHelper.polluteAura(world, pos, 1.0f, true);
                    world.removeBlock(pos, false);
                } else {
                    world.setBlock(pos, BlocksTC.taintFibre.defaultBlockState(), 2);
                }
            } else {
                world.setBlock(pos, state.setValue(LEVEL, meta - 1), 2);
                AuraHelper.polluteAura(world, pos, 1.0f, true);
            }
        }
    }

    private void spawnSlime(ServerLevel world, BlockPos pos, int size) {
        EntityThaumicSlime slime = EntityThaumicSlime.TYPE != null ? new EntityThaumicSlime(EntityThaumicSlime.TYPE, world) : null;
        slime.snapTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0f, 0.0f);
        // slime.setSize removed - dimensions in EntityType
        world.addFreshEntity(slime);
        slime.playSound(SoundsTC.gore, 1.0f, 1.0f);
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource rand) {
        int meta = state.getValue(LEVEL);
        if (rand.nextInt(44) <= meta) {
            FXGeneric fb = new FXGeneric(world,
                    pos.getX() + rand.nextFloat(),
                    pos.getY() + 0.125f * meta,
                    pos.getZ() + rand.nextFloat(), 0.0, 0.0, 0.0);
            fb.setMaxAge(2 + rand.nextInt(3));
            fb.setScale(rand.nextFloat() * 0.3f + 0.2f);
            fb.setRBGColorF(1.0f, 0.0f, 0.5f);
            fb.setRandomMovementScale(0.001f, 0.001f, 0.001f);
            fb.setGravity(-0.01f);
            fb.setAlphaF(0.25f);
            fb.setParticle(64);
            fb.setFinalFrames(65, 66);
            ParticleEngine.addEffect(world, fb);
        }
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face) {
        return 0;
    }

    @Override
    public net.minecraft.world.level.block.SoundType getSoundType(
            net.minecraft.world.level.block.state.BlockState state,
            net.minecraft.world.level.LevelReader world, net.minecraft.core.BlockPos pos,
            @javax.annotation.Nullable net.minecraft.world.entity.Entity entity) {
        net.minecraft.world.level.block.SoundType t = super.getSoundType(state, world, pos, entity);
        return t != null ? t : net.minecraft.world.level.block.SoundType.STONE;
    }

}
