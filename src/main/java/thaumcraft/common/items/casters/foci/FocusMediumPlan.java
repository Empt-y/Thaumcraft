package thaumcraft.common.items.casters.foci;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.ICaster;
import thaumcraft.api.casters.NodeSetting;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.api.items.IArchitect;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.lib.utils.BlockUtils;


public class FocusMediumPlan extends FocusMedium implements IArchitect
{
    ArrayList<BlockPos> checked;
    
    public FocusMediumPlan() {
        checked = new ArrayList<BlockPos>();
    }
    
    @Override
    public String getResearch() {
        return "FOCUSPLAN";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.PLAN";
    }
    
    @Override
    public int getComplexity() {
        return 4;
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.CRAFT;
    }
    
    @Override
    public NodeSetting[] createSettings() {
        int[] method = { 0, 1 };
        String[] methodDesc = { "focus.plan.full", "focus.plan.surface" };
        return new NodeSetting[] { new NodeSetting("method", "focus.plan.method", new NodeSetting.NodeSettingIntList(method, methodDesc)) };
    }
    
    @Override
    public HitResult[] supplyTargets() {
        if (getParent() == null || !(getPackage().getCaster() instanceof Player)) {
            return new HitResult[0];
        }
        ArrayList<HitResult> targets = new ArrayList<HitResult>();
        ItemStack casterStack = ItemStack.EMPTY;
        if (getPackage().getCaster().getMainHandItem() != null && getPackage().getCaster().getMainHandItem().getItem() instanceof ICaster) {
            casterStack = getPackage().getCaster().getMainHandItem();
        }
        else if (getPackage().getCaster().getOffhandItem() != null && getPackage().getCaster().getOffhandItem().getItem() instanceof ICaster) {
            casterStack = getPackage().getCaster().getOffhandItem();
        }
        for (Trajectory sT : getParent().supplyTrajectories()) {
            Vec3 end = sT.direction.normalize();
            end = end.scale(16.0);
            end = end.add(sT.source);
            HitResult target = getPackage().world.clip(new net.minecraft.world.level.ClipContext(sT.source, end, net.minecraft.world.level.ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, null));
            if (target != null && target.getType() == HitResult.Type.BLOCK) {
                ArrayList<BlockPos> usl = getArchitectBlocks(casterStack, getPackage().world, ((net.minecraft.world.phys.BlockHitResult)target).getBlockPos(), ((net.minecraft.world.phys.BlockHitResult)target).getDirection(), (Player) getPackage().getCaster());
                ArrayList<BlockPos> sl = usl.stream().sorted(new BlockUtils.BlockPosComparator(((net.minecraft.world.phys.BlockHitResult)target).getBlockPos())).collect(Collectors.toCollection(ArrayList::new));
                for (BlockPos p : sl) {
                    targets.add(new net.minecraft.world.phys.BlockHitResult(new net.minecraft.world.phys.Vec3(p.getX() + 0.5, p.getY() + 0.5, p.getZ() + 0.5), ((net.minecraft.world.phys.BlockHitResult)target).getDirection(), p, false));
                }
            }
        }
        return targets.toArray(new HitResult[0]);
    }
    
    @Override
    public HitResult getArchitectMOP(ItemStack stack, Level world, LivingEntity player) {
        Vec3 start = player.position();
        start = start.add(0.0, player.getEyeHeight(), 0.0);
        Vec3 end = player.getLookAngle();
        end = end.scale(16.0);
        end = end.add(start);
        return world.clip(new net.minecraft.world.level.ClipContext(start, end, net.minecraft.world.level.ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, null));
    }
    
    @Override
    public boolean useBlockHighlight(ItemStack stack) {
        return false;
    }
    
    @Override
    public boolean isExclusive() {
        return true;
    }
    
    @Override
    public boolean showAxis(ItemStack stack, Level world, Player player, Direction side, EnumAxis axis) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        int dim = CasterManager.getAreaDim(stack);
        if (getSettingValue("method") == 0) {
            switch (axis) {
                case Y: {
                    if (dim == 0 || dim == 3) {
                        return true;
                    }
                    break;
                }
                case Z: {
                    if (dim == 0 || dim == 2) {
                        return true;
                    }
                    break;
                }
                case X: {
                    if (dim == 0 || dim == 1) {
                        return true;
                    }
                    break;
                }
            }
        }
        else {
            switch (side.getAxis()) {
                case Y: {
                    if ((axis == EnumAxis.X && (dim == 0 || dim == 1)) || (axis == EnumAxis.Z && (dim == 0 || dim == 2))) {
                        return true;
                    }
                    break;
                }
                case Z: {
                    if ((axis == EnumAxis.Y && (dim == 0 || dim == 1)) || (axis == EnumAxis.X && (dim == 0 || dim == 2))) {
                        return true;
                    }
                    break;
                }
                case X: {
                    if ((axis == EnumAxis.Y && (dim == 0 || dim == 1)) || (axis == EnumAxis.Z && (dim == 0 || dim == 2))) {
                        return true;
                    }
                    break;
                }
            }
        }
        return false;
    }
    
    @Override
    public ArrayList<BlockPos> getArchitectBlocks(ItemStack stack, Level world, BlockPos pos, Direction side, Player player) {
        ArrayList<BlockPos> out = new ArrayList<BlockPos>();
        if (stack == null || stack.isEmpty()) {
            return out;
        }
        if (getSettingValue("method") == 0) {
            checked.clear();
            checkNeighboursFull(world, pos, new BlockPos(pos), side, CasterManager.getAreaX(stack), CasterManager.getAreaY(stack), CasterManager.getAreaZ(stack), out, player);
        }
        else {
            BlockState bi = world.getBlockState(pos);
            checked.clear();
            if (side.getAxis() == Direction.Axis.Z) {
                checkNeighboursSurface(world, pos, bi, new BlockPos(pos), side, CasterManager.getAreaZ(stack), CasterManager.getAreaY(stack), CasterManager.getAreaX(stack), out, player);
            }
            else {
                checkNeighboursSurface(world, pos, bi, new BlockPos(pos), side, CasterManager.getAreaX(stack), CasterManager.getAreaY(stack), CasterManager.getAreaZ(stack), out, player);
            }
        }
        return out;
    }
    
    public void checkNeighboursFull(Level world, BlockPos pos1, BlockPos pos2, Direction side, int sizeX, int sizeY, int sizeZ, ArrayList<BlockPos> list, Player player) {
        if (checked.contains(pos2)) {
            return;
        }
        checked.add(pos2);
        if (!world.isEmptyBlock(pos2)) {
            list.add(pos2);
        }
        int xs = -sizeX + pos1.getX();
        int xe = sizeX + pos1.getX();
        int ys = -sizeY + pos1.getY();
        int ye = sizeY + pos1.getY();
        int zs = -sizeZ + pos1.getZ();
        int ze = sizeZ + pos1.getZ();
        xs -= sizeX * side.getStepX();
        xe -= sizeX * side.getStepX();
        ys -= sizeY * side.getStepY();
        ye -= sizeY * side.getStepY();
        zs -= sizeZ * side.getStepZ();
        ze -= sizeZ * side.getStepZ();
        for (Direction dir : Direction.values()) {
            BlockPos q = pos2.relative(dir);
            if (q.getX() >= xs && q.getX() <= xe && q.getY() >= ys && q.getY() <= ye && q.getZ() >= zs) {
                if (q.getZ() <= ze) {
                    checkNeighboursFull(world, pos1, q, side, sizeX, sizeY, sizeZ, list, player);
                }
            }
        }
    }
    
    public void checkNeighboursSurface(Level world, BlockPos pos1, BlockState bi, BlockPos pos2, Direction side, int sizeX, int sizeY, int sizeZ, ArrayList<BlockPos> list, Player player) {
        if (checked.contains(pos2)) {
            return;
        }
        checked.add(pos2);
        switch (side.getAxis()) {
            case Y: {
                if (Math.abs(pos2.getX() - pos1.getX()) > sizeX) {
                    return;
                }
                if (Math.abs(pos2.getZ() - pos1.getZ()) > sizeZ) {
                    return;
                }
                break;
            }
            case Z: {
                if (Math.abs(pos2.getX() - pos1.getX()) > sizeX) {
                    return;
                }
                if (Math.abs(pos2.getY() - pos1.getY()) > sizeZ) {
                    return;
                }
                break;
            }
            case X: {
                if (Math.abs(pos2.getY() - pos1.getY()) > sizeX) {
                    return;
                }
                if (Math.abs(pos2.getZ() - pos1.getZ()) > sizeZ) {
                    return;
                }
                break;
            }
        }
        if (world.getBlockState(pos2) == bi && BlockUtils.isBlockExposed(world, pos2) && !world.isEmptyBlock(pos2)) {
            list.add(pos2);
            for (Direction dir : Direction.values()) {
                if (dir != side) {
                    if (dir.getOpposite() != side) {
                        checkNeighboursSurface(world, pos1, bi, pos2.relative(dir), side, sizeX, sizeY, sizeZ, list, player);
                    }
                }
            }
        }
    }
}
