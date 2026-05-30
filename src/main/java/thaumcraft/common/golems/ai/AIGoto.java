package thaumcraft.common.golems.ai;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.golems.tasks.TaskHandler;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;


public abstract class AIGoto extends Goal
{
    protected EntityThaumcraftGolem golem;
    protected int taskCounter;
    protected byte type;
    protected int cooldown;
    protected double minDist;
    private BlockPos prevRamble;
    protected BlockPos targetBlock;
    int pause;
    
    public AIGoto(EntityThaumcraftGolem g, byte type) {
        taskCounter = -1;
        this.type = 0;
        minDist = 4.0;
        pause = 0;
        golem = g;
        this.type = type;
        setFlags(java.util.EnumSet.of(net.minecraft.world.entity.ai.goal.Goal.Flag.MOVE, net.minecraft.world.entity.ai.goal.Goal.Flag.JUMP));
    }
    
    public boolean canUse() {
        if (cooldown > 0) {
            --cooldown;
            return false;
        }
        cooldown = 5;
        if (golem.getTask() != null && !golem.getTask().isSuspended()) {
            return false;
        }
        targetBlock = null;
        boolean start = findDestination();
        if (start && golem.getTask() != null && golem.getTask().getSealPos() != null) {
            ISealEntity se = GolemHelper.getSealEntity((golem.level() instanceof net.minecraft.server.level.ServerLevel ? ((golem.level() instanceof net.minecraft.server.level.ServerLevel) ? ((net.minecraft.server.level.ServerLevel)golem.level()).dimension().identifier().hashCode() : 0) : 0), golem.getTask().getSealPos());
            if (se != null) {
                se.getSeal().onTaskStarted(golem.level(), golem, golem.getTask());
            }
        }
        return start;
    }
    
    public void start() {
        moveTo();
        taskCounter = 0;
    }
    
    protected abstract void moveTo();
    
    public boolean canContinueToUse() {
        return taskCounter >= 0 && taskCounter <= 1000 && golem.getTask() != null && !golem.getTask().isSuspended() && isValidDestination(golem.level(), golem.getTask().getPos());
    }
    
    @Override
    public void tick() {
        if (golem.getTask() == null) {
            return;
        }
        if (pause-- <= 0) {
            BlockPos _tpos = (targetBlock == null) ? golem.getTask().getPos() : targetBlock;
            double dist = (golem.getTask().getType() == 0) ? golem.distanceToSqr(_tpos.getX() + 0.5, _tpos.getY() + 0.5, _tpos.getZ() + 0.5) : golem.distanceToSqr(golem.getTask().getEntity());
            if (dist > minDist) {
                golem.getTask().setCompletion(false);
                ++taskCounter;
                if (taskCounter % 5 == 0) {
                    if (prevRamble != null && prevRamble.equals(golem.blockPosition())) {
                        Vec3 vec3 = null; /* DefaultRandomPos call removed */
                        if (vec3 != null) {
                            golem.getNavigation().moveTo(vec3.x + 0.5, vec3.y + 0.5, vec3.z + 0.5, golem.getGolemMoveSpeed());
                        }
                    }
                    else {
                        moveTo();
                    }
                    prevRamble = golem.blockPosition();
                }
            }
            else {
                TaskHandler.completeTask(golem.getTask(), golem);
                if (golem.getTask() != null && golem.getTask().isCompleted()) {
                    if (taskCounter >= 0) {
                        taskCounter = 0;
                    }
                    pause = 0;
                }
                else {
                    pause = 10;
                    ++taskCounter;
                }
                --taskCounter;
            }
        }
    }
    
    public void stop() {
        if (golem.getTask() != null) {
            if (!golem.getTask().isCompleted() && golem.getTask().isReserved() && ModConfig.CONFIG_GRAPHICS.showGolemEmotes) {
                golem.level().broadcastEntityEvent(golem, (byte)6);
            }
            if (golem.getTask().isCompleted() && !golem.getTask().isSuspended()) {
                golem.getTask().setSuspended(true);
            }
            golem.getTask().setReserved(false);
        }
    }
    
    protected abstract boolean findDestination();
    
    protected boolean isValidDestination(Level world, BlockPos pos) {
        return true;
    }
    
    protected boolean areGolemTagsValidForTask(Task ticket) {
        ISealEntity se = SealHandler.getSealEntity((golem.level() instanceof net.minecraft.server.level.ServerLevel ? ((golem.level() instanceof net.minecraft.server.level.ServerLevel) ? ((net.minecraft.server.level.ServerLevel)golem.level()).dimension().identifier().hashCode() : 0) : 0), ticket.getSealPos());
        if (se == null || se.getSeal() == null) {
            return true;
        }
        if (se.isLocked() && !golem.getOwnerId().equals(se.getOwner())) {
            return false;
        }
        if (se.getSeal().getRequiredTags() != null && !golem.getProperties().getTraits().containsAll(Arrays.asList(se.getSeal().getRequiredTags()))) {
            return false;
        }
        if (se.getSeal().getForbiddenTags() != null) {
            for (EnumGolemTrait tag : se.getSeal().getForbiddenTags()) {
                if (golem.getProperties().getTraits().contains(tag)) {
                    return false;
                }
            }
        }
        return true;
    }
}
