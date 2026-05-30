package thaumcraft.common.golems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import thaumcraft.Thaumcraft;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.golems.ISealDisplayer;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;


public class ItemGolemBell extends ItemTCBase implements ISealDisplayer
{
    public ItemGolemBell() {
        super("golem_bell");
    }
    
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand hand) {
        playerIn.swing(hand);
        if (!worldIn.isClientSide()) {
            HitResult mop = RayTracer.retrace(playerIn);
            if (mop != null && (mop.getType() == HitResult.Type.BLOCK || mop.getType() == HitResult.Type.ENTITY)) {
                ISealEntity se = getSeal(playerIn);
                if (se != null) {
                    if (playerIn.isCrouching()) {
                        SealHandler.removeSealEntity(playerIn.level(), se.getSealPos(), false);
                        worldIn.playSound(null, se.getSealPos().pos, SoundsTC.zap, SoundSource.BLOCKS, 0.5f, 1.0f);
                    }
                    else {
                        /* TODO: port to NetworkHooks.openScreen */ 
                    }
                }
                return InteractionResult.sidedSuccess(worldIn.isClientSide());
            }
            if (playerIn.isCrouching() && ThaumcraftCapabilities.knowsResearch(playerIn, "GOLEMLOGISTICS")) {
                /* TODO: port to NetworkHooks.openScreen */ 
                return InteractionResult.sidedSuccess(worldIn.isClientSide());
            }
        }
        else {
            playerIn.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 0.6f, 1.0f + worldIn.getRandom().nextFloat() * 0.1f);
        }
        return super.use(worldIn, playerIn, hand);
    }
    
    public InteractionResult onItemUseFirst(Player player, Level world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, InteractionHand hand) {
        player.swing(hand);
        if (!world.isClientSide()) {
            ISealEntity se = SealHandler.getSealEntity((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), new SealPos(pos, side));
            if (se != null) {
                if (player.isCrouching()) {
                    SealHandler.removeSealEntity(world, se.getSealPos(), false);
                    world.playSound(null, pos, SoundsTC.zap, SoundSource.BLOCKS, 0.5f, 1.0f);
                }
                else {
                    /* TODO: port to NetworkHooks.openScreen */ 
                }
                return InteractionResult.SUCCESS;
            }
            if (player.isCrouching() && ThaumcraftCapabilities.knowsResearch(player, "GOLEMLOGISTICS")) {
                /* TODO: port to NetworkHooks.openScreen */ 
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
    
    public static ISealEntity getSeal(Player playerIn) {
        float f = playerIn.getXRot();
        float f2 = playerIn.getYRot();
        double d0 = playerIn.getX();
        double d2 = playerIn.getY() + playerIn.getEyeHeight();
        double d3 = playerIn.getZ();
        Vec3 vec0 = new Vec3(d0, d2, d3);
        float f3 = Mth.cos(-f2 * 0.017453292f - 3.1415927f);
        float f4 = Mth.sin(-f2 * 0.017453292f - 3.1415927f);
        float f5 = -Mth.cos(-f * 0.017453292f);
        float f6 = Mth.sin(-f * 0.017453292f);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d4 = 5.0;
        Vec3 vec2 = vec0.add(f7 * d4, f6 * d4, f8 * d4);
        Vec3 vec3 = new Vec3(f7 * d4, f6 * d4, f8 * d4);
        Vec3 vec4 = vec0.add(vec3.x / 10.0, vec3.y / 10.0, vec3.z / 10.0);
        for (int a = 0; a < vec3.length() * 10.0; ++a) {
            BlockPos pos = new BlockPos(vec4);
            HitResult mop = collisionRayTrace(playerIn.level(), pos, vec0, vec2);
            if (mop != null) {
                ISealEntity se = SealHandler.getSealEntity((playerIn.level() instanceof net.minecraft.server.level.ServerLevel ? ((playerIn.level() instanceof net.minecraft.server.level.ServerLevel) ? ((net.minecraft.server.level.ServerLevel)playerIn.level()).dimension().identifier().hashCode() : 0) : 0), new SealPos(pos, ((net.minecraft.world.phys.BlockHitResult)mop).getDirection()));
                if (se != null) {
                    return se;
                }
            }
            vec4 = vec4.add(vec3.x / 10.0, vec3.y / 10.0, vec3.z / 10.0);
        }
        return null;
    }
    
    private static boolean isVecInsideYZBounds(Vec3 point, BlockPos pos) {
        return point != null && (point.y >= pos.getY() && point.y <= pos.getY() + 1 && point.z >= pos.getZ() && point.z <= pos.getZ() + 1);
    }
    
    private static boolean isVecInsideXZBounds(Vec3 point, BlockPos pos) {
        return point != null && (point.x >= pos.getX() && point.x <= pos.getX() + 1 && point.z >= pos.getZ() && point.z <= pos.getZ() + 1);
    }
    
    private static boolean isVecInsideXYBounds(Vec3 point, BlockPos pos) {
        return point != null && (point.x >= pos.getX() && point.x <= pos.getX() + 1 && point.y >= pos.getY() && point.y <= pos.getY() + 1);
    }
    
    private static HitResult collisionRayTrace(Level worldIn, BlockPos pos, Vec3 start, Vec3 end) {
        Vec3 vec3 = start.getIntermediateWithXValue(end, pos.getX());
        Vec3 vec4 = start.getIntermediateWithXValue(end, pos.getX() + 1);
        Vec3 vec5 = start.getIntermediateWithYValue(end, pos.getY());
        Vec3 vec6 = start.getIntermediateWithYValue(end, pos.getY() + 1);
        Vec3 vec7 = start.getIntermediateWithZValue(end, pos.getZ());
        Vec3 vec8 = start.getIntermediateWithZValue(end, pos.getZ() + 1);
        if (!isVecInsideYZBounds(vec3, pos)) {
            vec3 = null;
        }
        if (!isVecInsideYZBounds(vec4, pos)) {
            vec4 = null;
        }
        if (!isVecInsideXZBounds(vec5, pos)) {
            vec5 = null;
        }
        if (!isVecInsideXZBounds(vec6, pos)) {
            vec6 = null;
        }
        if (!isVecInsideXYBounds(vec7, pos)) {
            vec7 = null;
        }
        if (!isVecInsideXYBounds(vec8, pos)) {
            vec8 = null;
        }
        Vec3 vec9 = null;
        if (vec3 != null && (vec9 == null || start.distanceToSqr(vec3.x + 0.5, vec3.y + 0.5, vec3.z + 0.5) < start.distanceToSqr(vec9.x + 0.5, vec9.y + 0.5, vec9.z + 0.5))) {
            vec9 = vec3;
        }
        if (vec4 != null && (vec9 == null || start.distanceToSqr(vec4.x + 0.5, vec4.y + 0.5, vec4.z + 0.5) < start.distanceToSqr(vec9.x + 0.5, vec9.y + 0.5, vec9.z + 0.5))) {
            vec9 = vec4;
        }
        if (vec5 != null && (vec9 == null || start.distanceToSqr(vec5.x + 0.5, vec5.y + 0.5, vec5.z + 0.5) < start.distanceToSqr(vec9.x + 0.5, vec9.y + 0.5, vec9.z + 0.5))) {
            vec9 = vec5;
        }
        if (vec6 != null && (vec9 == null || start.distanceToSqr(vec6.x + 0.5, vec6.y + 0.5, vec6.z + 0.5) < start.distanceToSqr(vec9.x + 0.5, vec9.y + 0.5, vec9.z + 0.5))) {
            vec9 = vec6;
        }
        if (vec7 != null && (vec9 == null || start.distanceToSqr(vec7.x + 0.5, vec7.y + 0.5, vec7.z + 0.5) < start.distanceToSqr(vec9.x + 0.5, vec9.y + 0.5, vec9.z + 0.5))) {
            vec9 = vec7;
        }
        if (vec8 != null && (vec9 == null || start.distanceToSqr(vec8.x + 0.5, vec8.y + 0.5, vec8.z + 0.5) < start.distanceToSqr(vec9.x + 0.5, vec9.y + 0.5, vec9.z + 0.5))) {
            vec9 = vec8;
        }
        if (vec9 == null) {
            return null;
        }
        Direction enumfacing = null;
        if (vec9 == vec3) {
            enumfacing = Direction.WEST;
        }
        if (vec9 == vec4) {
            enumfacing = Direction.EAST;
        }
        if (vec9 == vec5) {
            enumfacing = Direction.DOWN;
        }
        if (vec9 == vec6) {
            enumfacing = Direction.UP;
        }
        if (vec9 == vec7) {
            enumfacing = Direction.NORTH;
        }
        if (vec9 == vec8) {
            enumfacing = Direction.SOUTH;
        }
        return new net.minecraft.world.phys.BlockHitResult(new net.minecraft.world.phys.Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5), enumfacing, pos, false);
    }
}
