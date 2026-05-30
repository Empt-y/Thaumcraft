package thaumcraft.common.items.casters.foci;
import java.util.ArrayList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusMedium;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.Trajectory;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXFocusEffect;
import thaumcraft.common.lib.utils.EntityUtils;


public class FocusMediumTouch extends FocusMedium
{
    @Override
    public String getResearch() {
        return "BASEAUROMANCY";
    }
    
    @Override
    public String getKey() {
        return "thaumcraft.TOUCH";
    }
    
    @Override
    public int getComplexity() {
        return 2;
    }
    
    @Override
    public EnumSupplyType[] willSupply() {
        return new EnumSupplyType[] { EnumSupplyType.TRAJECTORY, EnumSupplyType.TARGET };
    }
    
    @Override
    public Aspect getAspect() {
        return Aspect.AVERSION;
    }
    
    @Override
    public Trajectory[] supplyTrajectories() {
        if (getParent() == null) {
            return new Trajectory[0];
        }
        ArrayList<Trajectory> trajectories = new ArrayList<Trajectory>();
        double range = (this instanceof FocusMediumBolt) ? 16.0 : RayTracer.getBlockReachDistance((Player) getPackage().getCaster());
        for (Trajectory sT : getParent().supplyTrajectories()) {
            Vec3 end = sT.direction.normalize();
            HitResult ray = EntityUtils.getPointedEntityRay(getPackage().world, getPackage().getCaster(), sT.source, end, 0.25, range, 0.25f, false);
            if (ray == null) {
                end = end.scale(range);
                end = end.add(sT.source);
                ray = getPackage().world.clip(new net.minecraft.world.level.ClipContext(sT.source, end, net.minecraft.world.level.ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, null));
                if (ray != null) {
                    end = ray.getLocation();
                }
            }
            else if (((net.minecraft.world.phys.EntityHitResult)ray).getEntity() != null) {
                end = end.scale(sT.source.distanceTo(((net.minecraft.world.phys.EntityHitResult)ray).getEntity().position()));
                end = end.add(sT.source);
            }
            trajectories.add(new Trajectory(end, sT.direction.normalize()));
        }
        return trajectories.toArray(new Trajectory[0]);
    }
    
    @Override
    public HitResult[] supplyTargets() {
        if (getParent() == null || !(getPackage().getCaster() instanceof Player)) {
            return new HitResult[0];
        }
        ArrayList<HitResult> targets = new ArrayList<HitResult>();
        double range = (this instanceof FocusMediumBolt) ? 16.0 : RayTracer.getBlockReachDistance((Player) getPackage().getCaster());
        for (Trajectory sT : getParent().supplyTrajectories()) {
            Vec3 end = sT.direction.normalize();
            HitResult ray = EntityUtils.getPointedEntityRay(getPackage().world, getPackage().getCaster(), sT.source, end, 0.25, range, 0.25f, false);
            if (ray == null) {
                end = end.scale(range);
                end = end.add(sT.source);
                ray = getPackage().world.clip(new net.minecraft.world.level.ClipContext(sT.source, end, net.minecraft.world.level.ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, null));
            }
            if (ray != null) {
                targets.add(ray);
            }
        }
        return targets.toArray(new HitResult[0]);
    }
    
    @Override
    public boolean execute(Trajectory trajectory) {
        FocusEffect[] fe = getPackage().getFocusEffects();
        if (fe != null && fe.length > 0) {
            String[] effects = new String[fe.length];
            for (int a = 0; a < fe.length; ++a) {
                effects[a] = fe[a].getKey();
            }
            /* sendToAllAround stub */
        }
        return true;
    }
}
