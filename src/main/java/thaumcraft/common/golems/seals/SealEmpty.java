package thaumcraft.common.golems.seals;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.utils.InventoryUtils;


public class SealEmpty extends SealFiltered
{
    int delay;
    int filterInc;
    HashMap<Integer, ItemStack> cache;
    Identifier icon;
    protected ISealConfigToggles.SealToggle[] props;
    
    public SealEmpty() {
        delay = new Random(System.nanoTime()).nextInt(30);
        filterInc = 0;
        cache = new HashMap<Integer, ItemStack>();
        icon = Identifier.fromNamespaceAndPath("thaumcraft", "items/seals/seal_empty");
        props = new ISealConfigToggles.SealToggle[] { new ISealConfigToggles.SealToggle(true, "pmeta", "golem.prop.meta"), new ISealConfigToggles.SealToggle(true, "pnbt", "golem.prop.nbt"), new ISealConfigToggles.SealToggle(false, "pore", "golem.prop.ore"), new ISealConfigToggles.SealToggle(false, "pmod", "golem.prop.mod"), new ISealConfigToggles.SealToggle(false, "pcycle", "golem.prop.cycle"), new ISealConfigToggles.SealToggle(false, "pleave", "golem.prop.leave") };
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:empty";
    }
    
    @Override
    public void tickSeal(Level world, ISealEntity seal) {
        if (delay % 100 == 0) {
            Iterator<Integer> it = cache.keySet().iterator();
            while (it.hasNext()) {
                Task t = TaskHandler.getTask((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), it.next());
                if (t == null) {
                    it.remove();
                }
            }
        }
        if (delay++ % 20 != 0) {
            return;
        }
        ItemStack stack = InventoryUtils.findFirstMatchFromFilter(getInv(filterInc), isBlacklist(), ThaumcraftInvHelper.getItemHandlerAt(world, seal.getSealPos().pos, seal.getSealPos().face), seal.getSealPos().face, new ThaumcraftInvHelper.InvFilter(!props[0].value, !props[1].value, props[2].value, props[3].value), props[5].value);
        if (stack != null && !stack.isEmpty()) {
            Task task = new Task(seal.getSealPos(), seal.getSealPos().pos);
            task.setPriority(seal.getPriority());
            task.setLifespan((short)5);
            TaskHandler.addTask((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), task);
            cache.put(task.getId(), stack);
        }
    }
    
    @Override
    public boolean onTaskCompletion(Level world, IGolemAPI golem, Task task) {
        ItemStack stack = cache.get(task.getId());
        int sa = ThaumcraftInvHelper.countTotalItemsIn(world, task.getSealPos().pos, task.getSealPos().face, stack, new ThaumcraftInvHelper.InvFilter(!props[0].value, !props[1].value, props[2].value, props[3].value));
        if (stack != null && !stack.isEmpty() && props[5].value && sa <= stack.getCount()) {
            stack = stack.copy();
            stack.setCount(sa - 1);
        }
        if (stack != null && !stack.isEmpty()) {
            int limit = golem.canCarryAmount(stack);
            if (limit > 0) {
                ItemStack s = golem.holdItem(InventoryUtils.removeStackFrom(world, task.getSealPos().pos, task.getSealPos().face, InventoryUtils.copyLimitedStack(stack, limit), new ThaumcraftInvHelper.InvFilter(!props[0].value, !props[1].value, props[2].value, props[3].value), false));
                if (!s.isEmpty()) {
                    InventoryUtils.ejectStackAt(world, task.getSealPos().pos.relative(task.getSealPos().face), task.getSealPos().face.getOpposite(), s);
                }
                ((Entity)golem).playSound(SoundEvents.ITEM_PICKUP, 0.125f, ((net.minecraft.util.RandomSource.create().nextFloat() - net.minecraft.util.RandomSource.create().nextFloat()) * 0.7f + 1.0f) * 2.0f);
                golem.swing();
            }
        }
        cache.remove(task.getId());
        ++filterInc;
        task.setSuspended(true);
        return true;
    }
    
    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        ItemStack stack = cache.get(task.getId());
        return stack != null && !stack.isEmpty() && golem.canCarry(stack, true);
    }
    
    @Override
    public boolean canPlaceAt(Level world, BlockPos pos, Direction side) {
        IItemHandler inv = ThaumcraftInvHelper.getItemHandlerAt(world, pos, side);
        return inv != null;
    }
    
    public NonNullList<ItemStack> getInv(int c) {
        return super.getInv();
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
    public void onTaskStarted(Level world, IGolemAPI golem, Task task) {
    }
    
    @Override
    public void onTaskSuspension(Level world, Task task) {
        cache.remove(task.getId());
    }
    
    @Override
    public void onRemoval(Level world, BlockPos pos, Direction side) {
    }
}
