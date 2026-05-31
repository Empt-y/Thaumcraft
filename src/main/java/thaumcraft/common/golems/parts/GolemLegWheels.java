package thaumcraft.common.golems.parts;
import net.minecraft.world.level.block.RenderShape;
import java.util.HashMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.parts.GolemLeg;


public class GolemLegWheels implements GolemLeg.ILegFunction
{
    public static HashMap<Integer, Float> ani;
    
    @Override
    public void onUpdateTick(IGolemAPI golem) {
        if (golem.getGolemWorld().isClientSide()) {
            double dist = Math.sqrt(golem.getGolemEntity().distanceToSqr(golem.getGolemEntity().xOld, golem.getGolemEntity().yOld, golem.getGolemEntity().zOld));
            float lastRot = 0.0f;
            if (GolemLegWheels.ani.containsKey(golem.getGolemEntity().getId())) {
                lastRot = GolemLegWheels.ani.get(golem.getGolemEntity().getId());
            }
            double d0 = golem.getGolemEntity().getX() - golem.getGolemEntity().xOld;
            double d2 = golem.getGolemEntity().getY() - golem.getGolemEntity().yOld;
            double d3 = golem.getGolemEntity().getZ() - golem.getGolemEntity().zOld;
            float dirTravel = (float)(Math.atan2(d3, d0) * 180.0 / 3.141592653589793) - 90.0f;
            double dir = 360.0f - (golem.getGolemEntity().getYRot() - dirTravel);
            lastRot += (float)(dist / 1.571 * dir);
            if (lastRot > 360.0f) {
                lastRot -= 360.0f;
            }
            GolemLegWheels.ani.put(golem.getGolemEntity().getId(), lastRot);
            if (golem.getGolemEntity().onGround() && !golem.getGolemEntity().isInWater() && dist > 0.25) {
                int i = Mth.floor(golem.getGolemEntity().getX());
                int j = Mth.floor(golem.getGolemEntity().getY() - 0.20000000298023224);
                int k = Mth.floor(golem.getGolemEntity().getZ());
                BlockPos blockpos = new BlockPos(i, j, k);
                BlockState iblockstate = golem.getGolemEntity().level().getBlockState(blockpos);
                Block block = iblockstate.getBlock();
                if (iblockstate.getRenderShape() != net.minecraft.world.level.block.RenderShape.INVISIBLE) {
                    // spawnParticle removed
                }
            }
        }
    }
    
    static {
        GolemLegWheels.ani = new HashMap<Integer, Float>();
    }
}
