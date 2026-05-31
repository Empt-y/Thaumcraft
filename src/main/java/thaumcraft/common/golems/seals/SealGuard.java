package thaumcraft.common.golems.seals;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Animal;
// import net.minecraft.world.entity.animal.Animal; // removed
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
// FML FMLCommonHandler removed
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import thaumcraft.common.golems.client.gui.SealBaseGUI;
import thaumcraft.common.golems.tasks.TaskHandler;


public class SealGuard implements ISeal, ISealGui, ISealConfigArea
{
    int delay;
    protected ISealConfigToggles.SealToggle[] props;
    Identifier icon;
    
    public SealGuard() {
        delay = new Random(System.nanoTime()).nextInt(22);
        props = new ISealConfigToggles.SealToggle[] { new ISealConfigToggles.SealToggle(true, "pmob", "golem.prop.mob"), new ISealConfigToggles.SealToggle(false, "panimal", "golem.prop.animal"), new ISealConfigToggles.SealToggle(false, "pplayer", "golem.prop.player") };
        icon = Identifier.fromNamespaceAndPath("thaumcraft", "items/seals/seal_guard");
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:guard";
    }
    
    @Override
    public void tickSeal(Level world, ISealEntity seal) {
        if (delay++ % 20 != 0) {
            return;
        }
        AABB area = GolemHelper.getBoundsForArea(seal);
        List list = world.getEntitiesOfClass(LivingEntity.class, area);
        if (list.size() > 0) {
            for (Object e : list) {
                LivingEntity target = (LivingEntity)e;
                if (isValidTarget(target)) {
                    Task task = new Task(seal.getSealPos(), target);
                    task.setPriority(seal.getPriority());
                    task.setLifespan((short)10);
                    TaskHandler.addTask((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), task);
                }
            }
        }
    }
    
    private boolean isValidTarget(LivingEntity target) {
        boolean valid = false;
        if (props[0].value && (target instanceof Object /* IMob removed */ || target instanceof Monster)) {
            valid = true;
        }
        if (props[1].value && (target instanceof net.minecraft.world.entity.animal.Animal || target instanceof Animal)) {
            valid = true;
        }
        if (props[2].value&& target instanceof Player) {
            valid = true;
        }
        return valid;
    }
    
    @Override
    public void onTaskStarted(Level world, IGolemAPI golem, Task task) {
        if (task.getEntity() != null && task.getEntity() instanceof LivingEntity && isValidTarget((LivingEntity)task.getEntity())) {
            ((Mob)golem).setTarget((LivingEntity)task.getEntity());
            golem.addRankXp(1);
        }
        task.setSuspended(true);
    }
    
    @Override
    public boolean onTaskCompletion(Level world, IGolemAPI golem, Task task) {
        task.setSuspended(true);
        return true;
    }
    
    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        net.minecraft.world.scores.PlayerTeam golemTeam = golem.getGolemEntity().getTeam();
        return golemTeam == null || !golemTeam.equals(task.getEntity() != null ? task.getEntity().getTeam() : null);
    }
    
    @Override
    public boolean canPlaceAt(Level world, BlockPos pos, Direction side) {
        return !world.isEmptyBlock(pos);
    }
    
    @Override
    public Identifier getSealIcon() {
        return icon;
    }
    
    @Override
    public int[] getGuiCategories() {
        return new int[] { 2, 0, 4 };
    }
    
    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[] { EnumGolemTrait.FIGHTER };
    }
    
    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return null;
    }
    
    @Override
    public void onTaskSuspension(Level world, Task task) {
    }
    
    @Override
    public void readCustomNBT(CompoundTag nbt) {
    }
    
    @Override
    public void writeCustomNBT(CompoundTag nbt) {
    }
    
    @Override
    public void onRemoval(Level world, BlockPos pos, Direction side) {
    }
    
    @Override
    public Object returnContainer(Level world, Player player, BlockPos pos, Direction side, ISealEntity seal) {
        return new SealBaseContainer(player.getInventory(), world, seal);
    }
    
    @Override
    public Object returnGui(Level world, Player player, BlockPos pos, Direction side, ISealEntity seal) {
        return new thaumcraft.common.golems.client.gui.SealBaseGUI(new thaumcraft.common.golems.client.gui.SealBaseContainer(player.getInventory(), world, seal), player.getInventory(), net.minecraft.network.chat.Component.empty());
    }
}
