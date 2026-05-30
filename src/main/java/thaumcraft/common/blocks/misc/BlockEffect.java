package thaumcraft.common.blocks.misc;
import net.minecraft.world.level.block.RenderShape;
import java.util.Random;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.events.ServerEvents;


public class BlockEffect extends BlockTC
{
    public BlockEffect(String name) {
        super(null /*  false   Material check removed    */, name);
        setTickRandomly(true);
        setResistance(999.0f);
        setLightLevel(0.5f);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        if (state.getBlock() != this) {
            return super.getLightEmission(state, world, pos);
        }
        if (state.getBlock() == BlocksTC.effectGlimmer) {
            return 15;
        }
        return super.getLightEmission(state, world, pos);
    }

    public Object /* BlockFaceShape removed */ getBlockFaceShape(BlockGetter worldIn, BlockState state, BlockPos pos, Direction face) {
        return null;
    }

    public void onEntityCollidedWithBlock(Level world, BlockPos pos, BlockState state, Entity entity) {
        if (state.getBlock() == BlocksTC.effectShock) {
            if (entity instanceof LivingEntity) {
                ServerEvents.addRunnableServer(world, new Runnable() {
                    @Override
                    public void run() {
                        ((LivingEntity)entity).hurt(world.damageSources().magic(), 1.0f);
                        MobEffectInstance pe = new MobEffectInstance(MobEffects.SLOWNESS, 20, 0, true, true);
                        ((LivingEntity)entity).addEffect(pe);
                    }
                }, 0);
            }
            if (!world.isClientSide() && world.getRandom().nextInt(100) == 0) {
                world.removeBlock(pos, false);
            }
        }
        else if (state.getBlock() == BlocksTC.effectSap && !(entity instanceof IEldritchMob) && entity instanceof LivingEntity && !((LivingEntity)entity).hasEffect(MobEffects.WITHER)) {
            ServerEvents.addRunnableServer(world, new Runnable() {
                @Override
                public void run() {
                    MobEffectInstance pe0 = new MobEffectInstance(MobEffects.WITHER, 40, 0, true, true);
                    ((LivingEntity)entity).addEffect(pe0);
                    MobEffectInstance pe2 = new MobEffectInstance(MobEffects.SLOWNESS, 40, 1, true, true);
                    ((LivingEntity)entity).addEffect(pe2);
                    MobEffectInstance pe3 = new MobEffectInstance(MobEffects.HUNGER, 40, 1, true, true);
                    ((LivingEntity)entity).addEffect(pe3);
                }
            }, 0);
        }
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel worldIn, BlockPos pos, RandomSource rand) {
        super.randomTick(state, worldIn, pos, rand);
        if (state.getBlock() != BlocksTC.effectGlimmer) {
            worldIn.removeBlock(pos, false);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void randomDisplayTick(BlockState state, Level w, BlockPos pos, Random r) {
        if (state.getBlock() != BlocksTC.effectGlimmer) {
            float h = r.nextFloat() * 0.33f;
            if (state.getBlock() == BlocksTC.effectShock) {
                FXDispatcher.INSTANCE.spark(pos.getX() + w.getRandom().nextFloat(), pos.getY() + 0.1515f + h / 2.0f, pos.getZ() + w.getRandom().nextFloat(), 3.0f + h * 6.0f, 0.65f + w.getRandom().nextFloat() * 0.1f, 1.0f, 1.0f, 0.8f);
            }
            else {
                FXDispatcher.INSTANCE.spark(pos.getX() + w.getRandom().nextFloat(), pos.getY() + 0.1515f + h / 2.0f, pos.getZ() + w.getRandom().nextFloat(), 3.0f + h * 6.0f, 0.3f - w.getRandom().nextFloat() * 0.1f, 0.0f, 0.5f + w.getRandom().nextFloat() * 0.2f, 1.0f);
            }
            if (r.nextInt(50) == 0) {
                w.playSound(null, pos, SoundsTC.jacobs, SoundSource.AMBIENT, 0.25f, 1.0f + (r.nextFloat() - r.nextFloat()) * 0.2f);
            }
        }
    }

    public boolean isAir(BlockState state) {
        return true;
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return new AABB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }
}
