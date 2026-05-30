package thaumcraft.common.golems.ai;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;
import net.minecraft.world.entity.ai.navigation.PathNavigation;


public class AIFollowOwner extends Goal
{
    private EntityOwnedConstruct thePet;
    private LivingEntity theOwner;
    Level theWorld;
    private double followSpeed;
    private PathNavigation petPathfinder;
    private int timeToRecalcPath;
    float maxDist;
    float minDist;
    private float oldWaterCost;
    
    public AIFollowOwner(EntityOwnedConstruct p_i1625_1_, double p_i1625_2_, float p_i1625_4_, float p_i1625_5_) {
        thePet = p_i1625_1_;
        theWorld = p_i1625_1_.level();
        followSpeed = p_i1625_2_;
        petPathfinder = p_i1625_1_.getNavigation();
        minDist = p_i1625_4_;
        maxDist = p_i1625_5_;
        setFlags(java.util.EnumSet.of(net.minecraft.world.entity.ai.goal.Goal.Flag.MOVE, net.minecraft.world.entity.ai.goal.Goal.Flag.LOOK));
        if (!(p_i1625_1_.getNavigation() instanceof GroundPathNavigation) && !(p_i1625_1_.getNavigation() instanceof PathNavigateGolemAir)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }
    
    public boolean canUse() {
        LivingEntity entitylivingbase = thePet.getOwnerEntity();
        if (entitylivingbase == null) {
            return false;
        }
        if (thePet.distanceToSqr(entitylivingbase.getX() + 0.5, entitylivingbase.getY() + 0.5, entitylivingbase.getZ() + 0.5) < minDist * minDist) {
            return false;
        }
        theOwner = entitylivingbase;
        return true;
    }
    
    public boolean canContinueToUse() {
        return !petPathfinder.isDone() && thePet.distanceToSqr(theOwner.getX() + 0.5, theOwner.getY() + 0.5, theOwner.getZ() + 0.5) > maxDist * maxDist;
    }
    
    public void start() {
        timeToRecalcPath = 0;
        oldWaterCost = 0 /* getPathPriority removed */;
        /* setPathPriority removed */;
    }
    
    public void stop() {
        theOwner = null;
        petPathfinder.stop();
        /* setPathPriority removed */;
    }
    
    private boolean func_181065_a(BlockPos p_181065_1_) {
        BlockState iblockstate = theWorld.getBlockState(p_181065_1_);
        Block block = iblockstate.getBlock();
        return block == Blocks.AIR || !null.isCollisionShapeFullBlock(iblockstate, null);
    }
    
    @Override
    public void tick() {
        thePet.getLookControl().setLookAt(theOwner, 10.0f, (float)40);
        int timeToRecalcPath = this.timeToRecalcPath - 1;
        this.timeToRecalcPath = timeToRecalcPath;
        if (timeToRecalcPath <= 0) {
            this.timeToRecalcPath = 10;
            if (!petPathfinder.moveTo(theOwner, followSpeed) && !thePet.isLeashed() && thePet.distanceToSqr(theOwner.getX() + 0.5, theOwner.getY() + 0.5, theOwner.getZ() + 0.5) >= 144.0) {
                int i = Mth.floor(theOwner.getX()) - 2;
                int j = Mth.floor(theOwner.getZ()) - 2;
                int k = Mth.floor(theOwner.getBoundingBox().minY);
                for (int l = 0; l <= 4; ++l) {
                    for (int i2 = 0; i2 <= 4; ++i2) {
                        if ((l < 1 || i2 < 1 || l > 3 || i2 > 3) && theWorld.getBlockState(new BlockPos(i + l, k - 1, j + i2)).isCollisionShapeFullBlock(null, null) && func_181065_a(new BlockPos(i + l, k, j + i2)) && func_181065_a(new BlockPos(i + l, k + 1, j + i2))) {
                            thePet.setPos(i + l + 0.5f, k, j + i2 + 0.5f);
                            petPathfinder.stop();
                            return;
                        }
                    }
                }
            }
        }
    }
}
