package thaumcraft.common.entities.ai.pech;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.sounds.SoundSource;
import thaumcraft.common.entities.monster.EntityPech;


public class AIPechItemEntityGoto extends Goal
{
    private EntityPech pech;
    private Entity targetEntity;
    float maxTargetDistance;
    private int count;
    private int failedPathFindingPenalty;
    
    public AIPechItemEntityGoto(EntityPech par1PathfinderMob) {
        maxTargetDistance = 16.0f;
        pech = par1PathfinderMob;
        // FIXME: setMutexBits removed - use setFlags(EnumSet.of(Goal.Flag.MOVE))
    }
    
    @Override
        public boolean canUse() {
        int count = this.count - 1;
        this.count = count;
        if (count > 0) {
            return false;
        }
        double range = Double.MAX_VALUE;
        List<Entity> targets = pech.level().getEntities(pech, pech.getBoundingBox().inflate(maxTargetDistance, maxTargetDistance, maxTargetDistance));
        if (targets.size() == 0) {
            return false;
        }
        for (Entity e : targets) {
            if (e instanceof ItemEntity && pech.canPickup(((ItemEntity)e).getItem())) {
                net.minecraft.world.entity.Entity thrower = ((ItemEntity)e).getOwner();
                String username = thrower != null ? thrower.getName().getString() : null;
                if (username != null && username.equals("PechDrop")) {
                    continue;
                }
                double distance = e.distanceToSqr(pech.getX(), pech.getY(), pech.getZ());
                if (distance >= range || distance > maxTargetDistance * maxTargetDistance) {
                    continue;
                }
                range = distance;
                targetEntity = e;
            }
        }
        return targetEntity != null;
    }
    
    @Override
        public boolean canContinueToUse() {
        return targetEntity != null && targetEntity.isAlive() && (!pech.getNavigation().isDone() && targetEntity.distanceToSqr(pech.getX() + 0.5, pech.getY() + 0.5, pech.getZ() + 0.5) < maxTargetDistance * maxTargetDistance);
    }
    
    @Override
        public void stop() {
        targetEntity = null;
    }
    
    @Override
        public void start() {
        pech.getNavigation().moveTo(pech.getNavigation().createPath(targetEntity, 1), pech.getAttribute(Attributes.MOVEMENT_SPEED).getValue() * 1.5);
        count = 0;
    }
    
    @Override
        public void tick() {
        pech.getLookControl().setLookAt(targetEntity, 30.0f, 30.0f);
        if (pech.getSensing().hasLineOfSight(targetEntity) && --count <= 0) {
            count = failedPathFindingPenalty + 4 + pech.getRandom().nextInt(4);
            pech.getNavigation().moveTo(targetEntity, pech.getAttribute(Attributes.MOVEMENT_SPEED).getValue() * 1.5);
            if (pech.getNavigation().getPath() != null) {
                Node finalPathPoint = pech.getNavigation().getPath().getEndNode();
                if (finalPathPoint != null && targetEntity.distanceToSqr(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1.0) {
                    failedPathFindingPenalty = 0;
                }
                else {
                    failedPathFindingPenalty += 10;
                }
            }
            else {
                failedPathFindingPenalty += 10;
            }
        }
        double distance = pech.distanceToSqr(targetEntity.getX(), targetEntity.getBoundingBox().minY, targetEntity.getZ());
        if (distance <= 1.5) {
            count = 0;
            int am = ((ItemEntity) targetEntity).getItem().getCount();
            ItemStack is = pech.pickupItem(((ItemEntity) targetEntity).getItem());
            if (is != null && !is.isEmpty() && is.getCount() > 0) {
                ((ItemEntity) targetEntity).setItem(is);
            }
            else {
                targetEntity.discard();
            }
            if (is == null || is.isEmpty() || is.getCount() != am) {
                targetEntity.level().playSound(null, targetEntity.getX(), targetEntity.getY(), targetEntity.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.NEUTRAL, 0.2f, ((targetEntity.level().getRandom().nextFloat() - targetEntity.level().getRandom().nextFloat()) * 0.7f + 1.0f) * 2.0f);
            }
        }
    }
}
