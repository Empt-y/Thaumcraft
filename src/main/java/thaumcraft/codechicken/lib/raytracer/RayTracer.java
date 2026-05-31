package thaumcraft.codechicken.lib.raytracer;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraft.util.Mth;
import thaumcraft.codechicken.lib.vec.BlockCoord;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.Vector3;


public class RayTracer
{
    private Vector3 vec;
    private Vector3 vec2;
    private Vector3 s_vec;
    private double s_dist;
    private int s_side;
    private IndexedCuboid6 c_cuboid;
    private static ThreadLocal<RayTracer> t_inst;

    public RayTracer() {
        vec = new Vector3();
        vec2 = new Vector3();
        s_vec = new Vector3();
    }

    public static RayTracer instance() {
        RayTracer inst = RayTracer.t_inst.get();
        if (inst == null) {
            RayTracer.t_inst.set(inst = new RayTracer());
        }
        return inst;
    }

    private void traceSide(int side, Vector3 start, Vector3 end, Cuboid6 cuboid) {
        vec.set(start);
        Vector3 hit = null;
        switch (side) {
            case 0: hit = vec.XZintercept(end, cuboid.min.y); break;
            case 1: hit = vec.XZintercept(end, cuboid.max.y); break;
            case 2: hit = vec.XYintercept(end, cuboid.min.z); break;
            case 3: hit = vec.XYintercept(end, cuboid.max.z); break;
            case 4: hit = vec.YZintercept(end, cuboid.min.x); break;
            case 5: hit = vec.YZintercept(end, cuboid.max.x); break;
        }
        if (hit == null) return;
        switch (side) {
            case 0:
            case 1:
                if (!betweenD(cuboid.min.x, hit.x, cuboid.max.x) || !betweenD(cuboid.min.z, hit.z, cuboid.max.z)) return;
                break;
            case 2:
            case 3:
                if (!betweenD(cuboid.min.x, hit.x, cuboid.max.x) || !betweenD(cuboid.min.y, hit.y, cuboid.max.y)) return;
                break;
            case 4:
            case 5:
                if (!betweenD(cuboid.min.y, hit.y, cuboid.max.y) || !betweenD(cuboid.min.z, hit.z, cuboid.max.z)) return;
                break;
        }
        double dist = vec2.set(hit).subtract(start).magSquared();
        if (dist < s_dist) {
            s_side = side;
            s_dist = dist;
            s_vec.set(vec);
        }
    }

    private static boolean betweenD(double min, double val, double max) {
        return min <= val && val <= max;
    }

    private boolean rayTraceCuboid(Vector3 start, Vector3 end, Cuboid6 cuboid) {
        s_dist = Double.MAX_VALUE;
        s_side = -1;
        for (int i = 0; i < 6; ++i) {
            traceSide(i, start, end, cuboid);
        }
        return s_side >= 0;
    }

    public ExtendedMOP rayTraceCuboid(Vector3 start, Vector3 end, Cuboid6 cuboid, BlockCoord pos, Object data) {
        return rayTraceCuboid(start, end, cuboid) ? new ExtendedMOP(s_vec, s_side, pos, data, s_dist) : null;
    }

    public ExtendedMOP rayTraceCuboid(Vector3 start, Vector3 end, Cuboid6 cuboid, Entity entity, Object data) {
        return rayTraceCuboid(start, end, cuboid) ? new ExtendedMOP(entity, s_vec, data, s_dist) : null;
    }

    public void rayTraceCuboids(Vector3 start, Vector3 end, List<IndexedCuboid6> cuboids, BlockCoord pos, Block block, List<ExtendedMOP> hitList) {
        for (IndexedCuboid6 cuboid : cuboids) {
            ExtendedMOP mop = rayTraceCuboid(start, end, cuboid, pos, cuboid.data);
            if (mop != null) {
                hitList.add(mop);
            }
        }
    }

    public static HitResult retraceBlock(Level world, Player player, BlockPos pos) {
        // collisionRayTrace and rayTraceBlocks removed in modern MC
        return null;
    }

    private static double getBlockReachDistance_server(ServerPlayer player) {
        // player.interactionManager.getBlockReachDistance() removed in modern MC
        return 5.0;
    }

    private static double getBlockReachDistance_client() {
        // playerController.getBlockReachDistance() removed in modern MC
        return 5.0;
    }

    public static HitResult retrace(Player player) {
        return retrace(player, getBlockReachDistance(player));
    }

    public static HitResult retrace(Player player, double reach) {
        // player.level().rayTraceBlocks removed; use player.level().clip() in modern MC
        return null;
    }

    public static Vec3 getCorrectedHeadVec(Player player) {
        Vector3 v = Vector3.fromEntity(player);
        v.y += player.getEyeHeight();
        if (player instanceof ServerPlayer && player.isCrouching()) {
            v.y -= 0.08;
        }
        return v.vec3();
    }

    public static Vec3 getStartVec(Player player) {
        return getCorrectedHeadVec(player);
    }

    public static double getBlockReachDistance(Player player) {
        return player.level().isClientSide() ? getBlockReachDistance_client() : ((player instanceof ServerPlayer) ? getBlockReachDistance_server((ServerPlayer)player) : 5.0);
    }

    public static Vec3 getEndVec(Player player) {
        Vec3 headVec = getCorrectedHeadVec(player);
        Vec3 lookVec = player.getLookAngle();
        double reach = getBlockReachDistance(player);
        return headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
    }

    static {
        RayTracer.t_inst = new ThreadLocal<RayTracer>();
    }
}
