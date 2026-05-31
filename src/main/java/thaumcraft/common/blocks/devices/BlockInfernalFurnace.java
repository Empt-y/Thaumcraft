package thaumcraft.common.blocks.devices;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.devices.TileInfernalFurnace;


public class BlockInfernalFurnace extends BlockTCDevice implements IBlockFacingHorizontal
{
    public static boolean ignore;

    public BlockInfernalFurnace() {
        super(null /*  null   Material removed    */, TileInfernalFurnace.class, "infernal_furnace");
        setSoundType(SoundType.STONE);
        setLightLevel(0.9f);
        registerDefaultState(defaultBlockState().setValue(IBlockFacingHorizontal.FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(IBlockFacingHorizontal.FACING);
    }

    @Override
    public boolean rotateBlock(Level world, BlockPos pos, Direction axis) {
        return false;
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return new AABB(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public BlockState getStateForPlacement(Level worldIn, BlockPos pos, Direction facing, float hitX, float hitY, float hitZ, int meta, LivingEntity placer) {
        return defaultBlockState().setValue(IBlockFacingHorizontal.FACING, placer.getDirection().getOpposite());
    }

    public static void destroyFurnace(Level worldIn, BlockPos pos, BlockState state, BlockPos startpos) {
        if (BlockInfernalFurnace.ignore || worldIn.isClientSide()) {
            return;
        }
        BlockInfernalFurnace.ignore = true;
        for (int a = -1; a <= 1; ++a) {
            for (int b = -1; b <= 1; ++b) {
                for (int c = -1; c <= 1; ++c) {
                    BlockPos target = pos.offset(a, b, c);
                    if (!target.equals(startpos)) {
                        BlockState bs = worldIn.getBlockState(target);
                        if (bs.getBlock() == BlocksTC.placeholderNetherbrick) {
                            worldIn.setBlock(target, Blocks.NETHER_BRICKS.defaultBlockState(), 3);
                        }
                        if (bs.getBlock() == BlocksTC.placeholderObsidian) {
                            worldIn.setBlock(target, Blocks.OBSIDIAN.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
        BlockPos front = pos.relative(BlockStateUtils.getFacing(state).getOpposite());
        if (worldIn.getBlockState(front).isAir()) {
            worldIn.setBlock(front, Blocks.IRON_BARS.defaultBlockState(), 3);
        }
        worldIn.setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
        BlockInfernalFurnace.ignore = false;
    }

    @Override
    public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        if (worldIn instanceof Level level) {
            destroyFurnace(level, pos, state, pos);
        }
        super.destroy(worldIn, pos, state);
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity, InsideBlockEffectApplier applier, boolean fromMovement) {
        Vec3 dm = entity.getDeltaMovement();
        double dx = dm.x, dy = dm.y, dz = dm.z;
        if (entity.getX() < pos.getX() + 0.3) dx += 1e-4;
        if (entity.getX() > pos.getX() + 0.7) dx -= 1e-4;
        if (entity.getZ() < pos.getZ() + 0.3) dz += 1e-4;
        if (entity.getZ() > pos.getZ() + 0.7) dz -= 1e-4;
        entity.setDeltaMovement(dx, dy, dz);
        if (!world.isClientSide() && entity.tickCount % 10 == 0) {
            if (entity instanceof ItemEntity ie) {
                entity.setDeltaMovement(dx, 0.025, dz);
                if (entity.onGround()) {
                    TileInfernalFurnace taf = (TileInfernalFurnace)world.getBlockEntity(pos);
                    if (taf != null) ie.setItem(taf.addItemsToInventory(ie.getItem()));
                }
            } else if (entity instanceof LivingEntity && !entity.fireImmune()) {
                entity.hurt(world.damageSources().lava(), 3.0f);
                entity.igniteForSeconds(10);
            }
        }
    }

    static {
        BlockInfernalFurnace.ignore = false;
    }
}
