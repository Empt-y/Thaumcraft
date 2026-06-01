package thaumcraft.common.blocks.basic;
import java.util.List;
import java.util.Random;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.tiles.misc.TileBarrierStone;


public class BlockPavingStone extends BlockTC
{
    public BlockPavingStone(String name) {
        super(null /*  null   Material removed    */, name, true);

        setHardness(2.5f);
        setSoundType(SoundType.STONE);
        setTickRandomly(true);
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return new AABB(0.0, 0.0, 0.0, 1.0, 0.9375, 1.0);
    }

    public boolean canHarvestBlock(BlockGetter world, BlockPos pos, Player player) {
        return true;
    }

    public boolean hasBlockEntity(BlockState state) {
        return state.getBlock() == BlocksTC.pavingStoneBarrier;
    }

    public BlockEntity newBlockEntity(net.minecraft.core.BlockPos pos, BlockState state) {
        return (state.getBlock() == BlocksTC.pavingStoneBarrier) ? new TileBarrierStone(null, pos, state) : null;
    }

    public void stepOn(Level worldIn, BlockPos pos, BlockState state, Entity e) {
        if (!worldIn.isClientSide() && state.getBlock() == BlocksTC.pavingStoneTravel && e instanceof LivingEntity) {
            ((LivingEntity)e).addEffect(new MobEffectInstance(MobEffects.SPEED, 40, 1, false, false));
            ((LivingEntity)e).addEffect(new MobEffectInstance(MobEffects.JUMP_BOOST, 40, 0, false, false));
        }
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public void randomDisplayTick(BlockState state, Level world, BlockPos pos, Random random) {
        if (state.getBlock() == BlocksTC.pavingStoneBarrier) {
            if (world.getBestNeighborSignal(pos) > 0) {
                for (int a = 0; a < 4; ++a) {
                    FXDispatcher.INSTANCE.blockRunes(pos.getX(), pos.getY() + 0.7f, pos.getZ(), 0.2f + net.minecraft.util.RandomSource.create().nextFloat() * 0.4f, net.minecraft.util.RandomSource.create().nextFloat() * 0.3f, 0.8f + net.minecraft.util.RandomSource.create().nextFloat() * 0.2f, 20, -0.02f);
                }
            } else {
                BlockState above1 = world.getBlockState(pos.above(1));
                BlockState above2 = world.getBlockState(pos.above(2));
                if ((above1.getBlock() == BlocksTC.barrier && above1.isAir()) ||
                    (above2.getBlock() == BlocksTC.barrier && above2.isAir())) {
                    for (int a = 0; a < 6; ++a) {
                        FXDispatcher.INSTANCE.blockRunes(pos.getX(), pos.getY() + 0.7f, pos.getZ(), 0.9f + net.minecraft.util.RandomSource.create().nextFloat() * 0.1f, net.minecraft.util.RandomSource.create().nextFloat() * 0.3f, net.minecraft.util.RandomSource.create().nextFloat() * 0.3f, 24, -0.02f);
                    }
                } else {
                    List<Entity> list = world.getEntitiesOfClass(Entity.class, new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1).inflate(1.0, 1.0, 1.0));
                    if (!list.isEmpty()) {
                        for (Entity entity : list) {
                            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
                                FXDispatcher.INSTANCE.blockRunes(pos.getX(), pos.getY() + 0.6f + net.minecraft.util.RandomSource.create().nextFloat() * Math.max(0.8f, entity.getEyeHeight()), pos.getZ(), 0.6f + net.minecraft.util.RandomSource.create().nextFloat() * 0.4f, 0.0f, 0.3f + net.minecraft.util.RandomSource.create().nextFloat() * 0.7f, 20, 0.0f);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
