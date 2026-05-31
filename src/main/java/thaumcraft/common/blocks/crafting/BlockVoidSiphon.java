package thaumcraft.common.blocks.crafting;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;


@net.neoforged.fml.common.EventBusSubscriber(modid = "thaumcraft", value = net.neoforged.api.distmarker.Dist.CLIENT)
public class BlockVoidSiphon extends BlockTCDevice implements IBlockEnabled
{
    protected static AABB AABB_MAIN;
    protected static AABB AABB_BASE;
    protected static AABB AABB_TOP;
    protected static AABB AABB_ORB;

    public BlockVoidSiphon() {
        super(null /*  null   Material removed    */, TileVoidSiphon.class, "void_siphon");
        setSoundType(SoundType.METAL);
    }

    public int damageDropped(BlockState state) {
        return 0;
    }

    public boolean isOpaqueCube(BlockState state) {
        return false;
    }

    public boolean isFullCube(BlockState state) {
        return false;
    }

    public boolean isSideSolid(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        return false;
    }

    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        return BlockVoidSiphon.AABB_MAIN;
    }

    public boolean onBlockActivated(Level world, BlockPos pos, BlockState state, Player player, InteractionHand hand, Direction side, float hitX, float hitY, float hitZ) {
        if (world.isClientSide()) {
            return true;
        }
        // TODO: open GUI id=22
        return true;
    }

    static {
        AABB_MAIN = new AABB(0.1875, 0.0, 0.1875, 0.8125, 1.0, 0.8125);
        AABB_BASE = new AABB(0.1875, 0.0, 0.1875, 0.8125, 0.125, 0.8125);
        AABB_TOP = new AABB(0.25, 0.125, 0.25, 0.75, 0.6875, 0.75);
        AABB_ORB = new AABB(0.3125, 0.75, 0.3125, 0.625, 1.0, 0.625);
    }

    @Override
    protected net.minecraft.world.InteractionResult useWithoutItem(net.minecraft.world.level.block.state.BlockState state, net.minecraft.world.level.Level world, net.minecraft.core.BlockPos pos, net.minecraft.world.entity.player.Player player, net.minecraft.world.phys.BlockHitResult hit) {
        if (!world.isClientSide() && !player.isShiftKeyDown()) {
            net.minecraft.world.level.block.entity.BlockEntity te = world.getBlockEntity(pos);
            if (te instanceof net.minecraft.world.MenuProvider) {
                ((net.minecraft.server.level.ServerPlayer) player).openMenu((net.minecraft.world.MenuProvider) te, buf -> buf.writeBlockPos(pos));
            }
        }
        return net.minecraft.world.InteractionResult.SUCCESS;
    }
}
