package thaumcraft.common.golems.seals;
import com.mojang.authlib.GameProfile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
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
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.Utils;


public class SealLumber implements ISeal, ISealGui, ISealConfigArea
{
    int delay;
    HashMap<Integer, Long> cache;
    Identifier icon;
    
    public SealLumber() {
        delay = new Random(System.nanoTime()).nextInt(33);
        cache = new HashMap<Integer, Long>();
        icon = Identifier.fromNamespaceAndPath("thaumcraft", "items/seals/seal_lumber");
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:lumber";
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
        ++delay;
        BlockPos p = GolemHelper.getPosInArea(seal, delay);
        if (!cache.containsValue(p.asLong()) && Utils.isWoodLog(world, p)) {
            Task task = new Task(seal.getSealPos(), p);
            task.setPriority(seal.getPriority());
            TaskHandler.addTask((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), task);
            cache.put(task.getId(), p.asLong());
        }
    }
    
    @Override
    public boolean onTaskCompletion(Level world, IGolemAPI golem, Task task) {
        if (cache.containsKey(task.getId()) && Utils.isWoodLog(world, task.getPos())) {
            FakePlayer fp = FakePlayerFactory.get((ServerLevel)world, new GameProfile(null, "FakeThaumcraftGolem"));
            fp.setPos(golem.getGolemEntity().getX(), golem.getGolemEntity().getY(), golem.getGolemEntity().getZ());
            BlockState bs = world.getBlockState(task.getPos());
            golem.swing();
            if (BlockUtils.breakFurthestBlock(world, task.getPos(), bs, fp)) {
                task.setLifespan((short)Math.max(task.getLifespan(), 10L));
                golem.addRankXp(1);
                return false;
            }
            cache.remove(task.getId());
        }
        task.setSuspended(true);
        return true;
    }
    
    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        if (cache.containsKey(task.getId()) && Utils.isWoodLog(golem.getGolemWorld(), task.getPos())) {
            return true;
        }
        task.setSuspended(true);
        return false;
    }
    
    @Override
    public void onTaskSuspension(Level world, Task task) {
        cache.remove(task.getId());
    }
    
    @Override
    public void readCustomNBT(CompoundTag nbt) {
    }
    
    @Override
    public void writeCustomNBT(CompoundTag nbt) {
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
    
    @Override
    public int[] getGuiCategories() {
        return new int[] { 2, 0, 4 };
    }
    
    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[] { EnumGolemTrait.BREAKER, EnumGolemTrait.SMART };
    }
    
    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return null;
    }
    
    @Override
    public void onTaskStarted(Level world, IGolemAPI golem, Task task) {
    }
}
