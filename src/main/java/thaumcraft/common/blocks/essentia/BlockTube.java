package thaumcraft.common.blocks.essentia;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.casters.ICaster;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.codechicken.lib.raytracer.ExtendedMOP;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.codechicken.lib.vec.BlockCoord;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.items.tools.ItemResonator;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.essentia.TileTube;
import thaumcraft.common.tiles.essentia.TileTubeBuffer;
import thaumcraft.common.tiles.essentia.TileTubeFilter;
import thaumcraft.common.tiles.essentia.TileTubeValve;


@net.neoforged.fml.common.EventBusSubscriber(modid = "thaumcraft", value = net.neoforged.api.distmarker.Dist.CLIENT)
public class BlockTube extends BlockTCDevice
{
    public static BooleanProperty NORTH;
    public static BooleanProperty EAST;
    public static BooleanProperty SOUTH;
    public static BooleanProperty WEST;
    public static BooleanProperty UP;
    public static BooleanProperty DOWN;
    private RayTracer rayTracer;

    public BlockTube(Class tile, String name) {
        super(null /*  null   Material removed    */, tile, name);
        rayTracer = new RayTracer();
        setHardness(0.5f);
        setResistance(5.0f);
        setSoundType(SoundType.METAL);
        registerDefaultState(defaultBlockState()
            .setValue((Property)BlockTube.NORTH, false)
            .setValue((Property)BlockTube.EAST, false)
            .setValue((Property)BlockTube.SOUTH, false)
            .setValue((Property)BlockTube.WEST, false)
            .setValue((Property)BlockTube.UP, false)
            .setValue((Property)BlockTube.DOWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public int getMetaFromState(BlockState state) {
        return 0;
    }

    public void onBlockPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity tile = worldIn.getBlockEntity(pos);
        if (tile != null && tile instanceof TileTube) {
            ((TileTube)tile).facing = placer.getDirection();
            tile.setChanged();
        }
    }

    private Boolean[] makeConnections(BlockState state, BlockGetter world, BlockPos pos) {
        Boolean[] cons = { false, false, false, false, false, false };
        BlockEntity t = world instanceof net.minecraft.world.level.Level ? ((net.minecraft.world.level.Level)world).getBlockEntity(pos) : null;
        if (t != null && t instanceof IEssentiaTransport) {
            IEssentiaTransport tube = (IEssentiaTransport)t;
            int a = 0;
            for (Direction face : Direction.values()) {
                if (tube.isConnectable(face) && ThaumcraftApiHelper.getConnectableTile(world, pos, face) != null) {
                    cons[a] = true;
                }
                ++a;
            }
        }
        return cons;
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        float minx = 0.3125f;
        float maxx = 0.6875f;
        float miny = 0.3125f;
        float maxy = 0.6875f;
        float minz = 0.3125f;
        float maxz = 0.6875f;
        for (int side = 0; side < 6; ++side) {
            Direction fd = Direction.from3DDataValue(side);
            BlockEntity te = ThaumcraftApiHelper.getConnectableTile(source, pos, fd);
            if (te != null) {
                switch (side) {
                    case 0: { miny = 0.0f; break; }
                    case 1: { maxy = 1.0f; break; }
                    case 2: { minz = 0.0f; break; }
                    case 3: { maxz = 1.0f; break; }
                    case 4: { minx = 0.0f; break; }
                    case 5: { maxx = 1.0f; break; }
                }
            }
        }
        return new AABB(minx, miny, minz, maxx, maxy, maxz);
    }

    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    public int getComparatorInputOverride(BlockState state, Level world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te != null && te instanceof TileTubeBuffer) {
            float n = (float)((TileTubeBuffer)te).aspects.visSize();
            float r = n / 10.0f;
            return Mth.floor(r * 14.0f) + ((((TileTubeBuffer)te).aspects.visSize() > 0) ? 1 : 0);
        }
        return 0;
    }

    @Override
    public void destroy(LevelAccessor worldIn, BlockPos pos, BlockState state) {
        BlockEntity te = worldIn.getBlockEntity(pos);
        if (te instanceof TileTube && ((TileTube)te).getEssentiaAmount(Direction.UP) > 0) {
            if (worldIn instanceof Level level) {
                if (!level.isClientSide()) {
                    AuraHelper.polluteAura(level, pos, (float)((TileTube)te).getEssentiaAmount(Direction.UP), true);
                } else {
                    level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.1f, 1.0f + level.getRandom().nextFloat() * 0.1f);
                    for (int a = 0; a < 5; ++a) {
                        FXDispatcher.INSTANCE.drawVentParticles(pos.getX() + 0.33 + level.getRandom().nextFloat() * 0.33, pos.getY() + 0.33 + level.getRandom().nextFloat() * 0.33, pos.getZ() + 0.33 + level.getRandom().nextFloat() * 0.33, 0.0, 0.0, 0.0, Aspect.FLUX.getColor());
                    }
                }
            }
        }
        super.destroy(worldIn, pos, state);
    }

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (state.getBlock() == BlocksTC.tubeValve) {
            if (player.getItemInHand(hand).getItem() instanceof ICaster || player.getItemInHand(hand).getItem() instanceof ItemResonator) {
                return false;
            }
            BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof TileTubeValve) {
                ((TileTubeValve)te).allowFlow = !((TileTubeValve)te).allowFlow;
                world.sendBlockUpdated(pos, state, state, 3);
                te.setChanged();
                if (!world.isClientSide()) {
                    world.playSound(null, pos, SoundsTC.squeek, SoundSource.BLOCKS, 0.7f, 0.9f + world.getRandom().nextFloat() * 0.2f);
                }
                return true;
            }
        }
        if (state.getBlock() == BlocksTC.tubeFilter) {
            BlockEntity te = world.getBlockEntity(pos);
            if (te != null && te instanceof TileTubeFilter && player.isShiftKeyDown() && ((TileTubeFilter)te).aspectFilter != null) {
                ((TileTubeFilter)te).aspectFilter = null;
                world.sendBlockUpdated(pos, state, state, 3);
                te.setChanged();
                if (world.isClientSide()) {
                    world.playSound(null, pos, SoundsTC.key, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
                return true;
            }
            if (te != null && te instanceof TileTubeFilter && ((TileTubeFilter)te).aspectFilter == null && player.getItemInHand(hand).getItem() instanceof IEssentiaContainerItem) {
                if (((IEssentiaContainerItem)player.getItemInHand(hand).getItem()).getAspects(player.getItemInHand(hand)) != null) {
                    ((TileTubeFilter)te).aspectFilter = ((IEssentiaContainerItem)player.getItemInHand(hand).getItem()).getAspects(player.getItemInHand(hand)).getAspects()[0];
                    world.sendBlockUpdated(pos, state, state, 3);
                    te.setChanged();
                    if (world.isClientSide()) {
                        world.playSound(null, pos, SoundsTC.key, SoundSource.BLOCKS, 1.0f, 1.0f);
                    }
                }
                return true;
            }
        }
        return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
    }

    // onBlockHighlight: removed, use RenderHighlightEvent in client event handler

    static {
        NORTH = BooleanProperty.create("north");
        EAST = BooleanProperty.create("east");
        SOUTH = BooleanProperty.create("south");
        WEST = BooleanProperty.create("west");
        UP = BooleanProperty.create("up");
        DOWN = BooleanProperty.create("down");
    }
}
