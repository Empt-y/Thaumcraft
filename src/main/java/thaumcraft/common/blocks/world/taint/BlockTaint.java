package thaumcraft.common.blocks.world.taint;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.api.entities.EntitiesTC;
import thaumcraft.common.entities.EntityFallingTaint;
import thaumcraft.common.entities.monster.tainted.EntityTaintSwarm;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.Utils;

public class BlockTaint extends BlockTC implements ITaintBlock
{
    private static final Random r = new Random(System.currentTimeMillis());

    public BlockTaint(String name) {
        super(null /*  null   Material removed    */, name);
        setHardness(10.0f);
        setResistance(100.0f);
        setSoundType(SoundsTC.GORE);
        setTickRandomly(true);
    }

    public BlockTaint(BlockBehaviour.Properties props) {
        super(props);
    }

    @Override
    public MapColor getMapColor(BlockState state, BlockGetter worldIn, BlockPos pos, MapColor defaultColor) {
        return MapColor.COLOR_PURPLE;
    }

    public void die(Level world, BlockPos pos, BlockState state) {
        if (state.getBlock() == BlocksTC.taintRock) {
            world.setBlock(pos, BlocksTC.stonePorous.defaultBlockState(), 3);
        } else if (state.getBlock() == BlocksTC.taintSoil) {
            world.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
        } else if (state.getBlock() == BlocksTC.taintCrust || state.getBlock() == BlocksTC.taintGeyser) {
            world.setBlock(pos, BlocksTC.fluxGoo.defaultBlockState(), 3);
        } else {
            world.removeBlock(pos, false);
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel world, BlockPos pos, RandomSource rand) {
        if (!TaintHelper.isNearTaintSeed(world, pos) && rand.nextInt(10) == 0) {
            die(world, pos, state);
            return;
        }
        if (state.getBlock() == BlocksTC.taintRock) {
            TaintHelper.spreadFibres(world, pos);
        }
        if (state.getBlock() == BlocksTC.taintCrust) {
            if (tryToFall(world, pos, pos)) return;
            if (world.isEmptyBlock(pos.above())) {
                Direction dir = Direction.Plane.HORIZONTAL.getRandomDirection(rand);
                boolean canFall = true;
                for (int a = 1; a < 4; ++a) {
                    BlockPos step = pos.relative(dir);
                    if (!world.isEmptyBlock(step.below(a)) || world.getBlockState(pos.below(a)).getBlock() != this) {
                        canFall = false;
                        break;
                    }
                }
                if (canFall) {
                    tryToFall(world, pos, pos.relative(dir));
                }
            }
        } else if (state.getBlock() == BlocksTC.taintGeyser) {
            if (rand.nextFloat() < 0.2f
                    && world.getPlayers(p -> p.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 32.0 * 32.0).size() > 0
                    && EntityUtils.getEntitiesInRange(world, pos, null, EntityTaintSwarm.class, 32.0).isEmpty()) {
                EntityTaintSwarm swarm = EntitiesTC.TAINT_SWARM.get().create(world, net.minecraft.world.entity.EntitySpawnReason.NATURAL);
                swarm.snapTo(pos.getX() + 0.5, pos.getY() + 1.25, pos.getZ() + 0.5, (float) rand.nextInt(360), 0.0f);
                world.addFreshEntity(swarm);
            } else if (AuraHelper.getFlux(world, pos) < 2.0f) {
                AuraHelper.polluteAura(world, pos, 0.25f, true);
            }
        }
    }

    @Override
    public void stepOn(Level world, BlockPos pos, BlockState state, Entity entity) {
        if (!world.isClientSide() && entity instanceof LivingEntity living
                && !living.is(EntityTypeTags.UNDEAD)
                && world.getRandom().nextInt(250) == 0) {
            Holder<MobEffect> effect = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionFluxTaint.instance);
            living.addEffect(new MobEffectInstance(effect, 200, 0, false, true));
        }
    }

    @Override
    protected boolean triggerEvent(BlockState state, Level world, BlockPos pos, int eventID, int eventParam) {
        if (eventID == 1) {
            if (world.isClientSide()) {
                world.playSound(null, pos, SoundEvents.CHORUS_FLOWER_DEATH, SoundSource.BLOCKS,
                        0.1f, 0.9f + world.getRandom().nextFloat() * 0.2f);
            }
            return true;
        }
        return super.triggerEvent(state, world, pos, eventID, eventParam);
    }

    public static boolean canFallBelow(Level world, BlockPos pos) {
        BlockState bs = world.getBlockState(pos);
        Block block = bs.getBlock();
        for (int xx = -1; xx <= 1; ++xx) {
            for (int zz = -1; zz <= 1; ++zz) {
                for (int yy = -1; yy <= 1; ++yy) {
                    if (Utils.isWoodLog(world, pos.offset(xx, yy, zz))) return false;
                }
            }
        }
        if (bs.isAir()) return true;
        if (block == BlocksTC.fluxGoo
                && bs.getValue(BlockFluxGoo.LEVEL) < 4) return true;
        return block == Blocks.FIRE || block == BlocksTC.taintFibre || bs.canBeReplaced();
    }

    private boolean tryToFall(Level world, BlockPos pos, BlockPos target) {
        if (!BlockTaintFibre.isOnlyAdjacentToTaint(world, pos)) return false;
        if (!canFallBelow(world, target.below()) || target.getY() < 0) return false;
        if (world instanceof ServerLevel sl) {
            EntityFallingTaint falling = new EntityFallingTaint(world,
                    target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5,
                    world.getBlockState(pos), pos);
            sl.addFreshEntity(falling);
            return true;
        }
        return false;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (state.getBlock() == BlocksTC.taintRock) {
            int rr = r.nextInt(15);
            if (rr > 13) {
                List<ItemStack> ret = new ArrayList<>();
                // TODO: add flux crystal drop when item is available
                return ret;
            }
        }
        return super.getDrops(state, params);
    }
}
