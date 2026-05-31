package thaumcraft.common.golems.seals;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.golem.AbstractGolem;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
// import net.minecraft.world.entity.animal.Animal; // removed
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.ISealGui;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import thaumcraft.common.golems.client.gui.SealBaseGUI;
import thaumcraft.common.golems.tasks.TaskHandler;


public class SealButcher implements ISeal, ISealGui, ISealConfigArea
{
    int delay;
    boolean wait;
    Identifier icon;
    
    public SealButcher() {
        delay = new Random(System.nanoTime()).nextInt(200);
        wait = false;
        icon = Identifier.fromNamespaceAndPath("thaumcraft", "items/seals/seal_butcher");
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:butcher";
    }
    
    @Override
    public void tickSeal(Level world, ISealEntity seal) {
        if (delay++ % 200 != 0 || wait) {
            return;
        }
        AABB area = GolemHelper.getBoundsForArea(seal);
        List list = world.getEntitiesOfClass(LivingEntity.class, area);
        if (list.size() > 0) {
            for (Object e : list) {
                LivingEntity target = (LivingEntity)e;
                if (isValidTarget(target)) {
                    List<LivingEntity> var55 = world.getEntities(net.minecraft.world.level.entity.EntityTypeTest.forClass(LivingEntity.class), area, e2 -> e2.getClass() == target.getClass());
                    Iterator<LivingEntity> var56;
                    int count;
                    LivingEntity var57;
                    for (var56 = var55.iterator(), count = 0; var56.hasNext() && count < 3; ++count) {
                        var57 = var56.next();
                        if (isValidTarget(var57)) {}
                    }
                    if (count > 2) {
                        Task task = new Task(seal.getSealPos(), target);
                        task.setPriority(seal.getPriority());
                        task.setLifespan((short)10);
                        TaskHandler.addTask((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), task);
                        wait = true;
                        break;
                    }
                    continue;
                }
            }
        }
    }
    
    private boolean isValidTarget(LivingEntity target) {
        return (target instanceof net.minecraft.world.entity.animal.Animal || target instanceof Animal) && !(target instanceof Object /* IMob removed */) && (!(target instanceof TamableAnimal) || ((TamableAnimal)target).getOwner() == null) && !(target instanceof AbstractGolem) && (!(target instanceof net.minecraft.world.entity.animal.Animal) || !target.isBaby());
    }
    
    @Override
    public void onTaskStarted(Level world, IGolemAPI golem, Task task) {
        if (task.getEntity() != null && task.getEntity() instanceof LivingEntity && isValidTarget((LivingEntity)task.getEntity())) {
            ((Mob)golem).setTarget((LivingEntity)task.getEntity());
            golem.addRankXp(1);
        }
        task.setSuspended(true);
        wait = false;
    }
    
    @Override
    public boolean onTaskCompletion(Level world, IGolemAPI golem, Task task) {
        task.setSuspended(true);
        wait = false;
        return true;
    }
    
    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        return true;
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
        return new EnumGolemTrait[] { EnumGolemTrait.FIGHTER, EnumGolemTrait.SMART };
    }
    
    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return null;
    }
    
    @Override
    public void onTaskSuspension(Level world, Task task) {
        wait = false;
    }
    
    @Override
    public void readCustomNBT(CompoundTag nbt) {
    }
    
    @Override
    public void writeCustomNBT(CompoundTag nbt) {
    }
    
    @Override
    public void onRemoval(Level world, BlockPos pos, Direction side) {
        wait = false;
    }
    
    @Override
    public Object returnContainer(Level world, Player player, BlockPos pos, Direction side, ISealEntity seal) {
        return new SealBaseContainer(player.getInventory(), world, seal);
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public Object returnGui(Level world, Player player, BlockPos pos, Direction side, ISealEntity seal) {
        return new thaumcraft.common.golems.client.gui.SealBaseGUI(new thaumcraft.common.golems.client.gui.SealBaseContainer(player.getInventory(), world, seal), player.getInventory(), net.minecraft.network.chat.Component.empty());
    }
}
