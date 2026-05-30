package thaumcraft.common.golems.seals;
import java.util.Random;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Tuple;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.utils.InventoryUtils;


public class SealFill extends SealFiltered
{
    int delay;
    int watchedTask;
    protected ISealConfigToggles.SealToggle[] props;
    Identifier icon;
    
    public SealFill() {
        delay = new Random(System.nanoTime()).nextInt(50);
        watchedTask = Integer.MIN_VALUE;
        props = new ISealConfigToggles.SealToggle[] { new ISealConfigToggles.SealToggle(true, "pmeta", "golem.prop.meta"), new ISealConfigToggles.SealToggle(true, "pnbt", "golem.prop.nbt"), new ISealConfigToggles.SealToggle(false, "pore", "golem.prop.ore"), new ISealConfigToggles.SealToggle(false, "pmod", "golem.prop.mod"), new ISealConfigToggles.SealToggle(false, "pexist", "golem.prop.exist") };
        icon = Identifier.fromNamespaceAndPath("thaumcraft", "items/seals/seal_fill");
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:fill";
    }
    
    @Override
    public void tickSeal(Level world, ISealEntity seal) {
        if (delay++ % 20 != 0) {
            return;
        }
        Task oldTask = TaskHandler.getTask((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), watchedTask);
        if (oldTask == null || oldTask.isReserved() || oldTask.isSuspended() || oldTask.isCompleted()) {
            Task task = new Task(seal.getSealPos(), seal.getSealPos().pos);
            task.setPriority(seal.getPriority());
            TaskHandler.addTask((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), task);
            watchedTask = task.getId();
        }
    }
    
    @Override
    public void onTaskStarted(Level world, IGolemAPI golem, Task task) {
        ISealEntity se = SealHandler.getSealEntity((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), task.getSealPos());
        if (se != null && !se.isStoppedByRedstone(world)) {
            Task newTask = new Task(task.getSealPos(), task.getSealPos().pos);
            newTask.setPriority(se.getPriority());
            TaskHandler.addTask((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), newTask);
            watchedTask = newTask.getId();
        }
    }
    
    @Override
    public boolean onTaskCompletion(Level world, IGolemAPI golem, Task task) {
        ThaumcraftInvHelper.InvFilter filter = new ThaumcraftInvHelper.InvFilter(!props[0].value, !props[1].value, props[2].value, props[3].value);
        Tuple<ItemStack, Integer> tuple = InventoryUtils.findFirstMatchFromFilterTuple(getInv(), getSizes(), isBlacklist(), golem.getCarrying(), filter);
        if (tuple.getA() != null && !tuple.getA().isEmpty()) {
            IItemHandler inv = ThaumcraftInvHelper.getItemHandlerAt(world, task.getSealPos().pos, task.getSealPos().face);
            int limit = tuple.getA().getCount();
            if (hasStacksizeLimiters() && tuple.getB() != null && tuple.getB() > 0) {
                int c = (inv == null) ? InventoryUtils.countStackInWorld(golem.getGolemWorld(), task.getSealPos().pos, tuple.getA(), 1.5, filter) : ThaumcraftInvHelper.countTotalItemsIn(inv, tuple.getA(), filter);
                if (c < tuple.getB()) {
                    limit = tuple.getB() - c;
                }
                else {
                    limit = 0;
                }
            }
            if (limit > 0) {
                ItemStack t = tuple.getA().copy();
                t.setCount(limit);
                ItemStack s = golem.dropItem(t);
                if (inv == null) {
                    ItemEntity entityItem;
                    ItemEntity ie = entityItem = new ItemEntity(world, task.getSealPos().pos.getX() + 0.5 + task.getSealPos().face.getStepX(), task.getSealPos().pos.getY() + 0.5 + task.getSealPos().face.getStepY(), task.getSealPos().pos.getZ() + 0.5 + task.getSealPos().face.getStepZ(), s);
                    ie.setDeltaMovement(ie.getDeltaMovement().x / 5.0, ie.getDeltaMovement().y / 2.0, ie.getDeltaMovement().z / 5.0);
                    world.addFreshEntity(ie);
                }
                else {
                    golem.holdItem(ItemHandlerHelper.insertItemStacked(inv, s, false));
                }
                ((Entity)golem).playSound(SoundEvents.ITEM_PICKUP, 0.125f, ((net.minecraft.util.RandomSource.create().nextFloat() - net.minecraft.util.RandomSource.create().nextFloat()) * 0.7f + 1.0f) * 1.0f);
                golem.addRankXp(1);
                golem.swing();
            }
        }
        task.setSuspended(true);
        return true;
    }
    
    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        ThaumcraftInvHelper.InvFilter filter = new ThaumcraftInvHelper.InvFilter(!props[0].value, !props[1].value, props[2].value, props[3].value);
        Tuple<ItemStack, Integer> tuple = InventoryUtils.findFirstMatchFromFilterTuple(getInv(), getSizes(), isBlacklist(), golem.getCarrying(), filter);
        if (tuple.getA() != null && !tuple.getA().isEmpty()) {
            IItemHandler inv = ThaumcraftInvHelper.getItemHandlerAt(golem.getGolemWorld(), task.getSealPos().pos, task.getSealPos().face);
            if (inv != null) {
                if (tuple.getA() != null && !tuple.getA().isEmpty() && props[4].value && ThaumcraftInvHelper.countTotalItemsIn(inv, tuple.getA(), filter) <= 0) {
                    return false;
                }
                if (tuple.getA() != null && !tuple.getA().isEmpty() && ThaumcraftInvHelper.hasRoomForSome(golem.getGolemWorld(), task.getSealPos().pos, task.getSealPos().face, tuple.getA())) {
                    if (!hasStacksizeLimiters() || tuple.getB() == null || tuple.getB() <= 0) {
                        return true;
                    }
                    if (ThaumcraftInvHelper.countTotalItemsIn(inv, tuple.getA(), filter) < tuple.getB()) {
                        return true;
                    }
                }
            }
            else if (tuple.getA() != null && !tuple.getA().isEmpty()) {
                return !hasStacksizeLimiters() || tuple.getB() == null || tuple.getB() <= 0 || InventoryUtils.countStackInWorld(golem.getGolemWorld(), task.getSealPos().pos, tuple.getA(), 1.5, filter) < tuple.getB();
            }
        }
        return false;
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
        return new int[] { 1, 0, 4 };
    }
    
    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return null;
    }
    
    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return new EnumGolemTrait[] { EnumGolemTrait.CLUMSY };
    }
    
    @Override
    public void onTaskSuspension(Level world, Task task) {
    }
    
    @Override
    public void onRemoval(Level world, BlockPos pos, Direction side) {
    }
    
    @Override
    public boolean hasStacksizeLimiters() {
        return !isBlacklist();
    }
}
