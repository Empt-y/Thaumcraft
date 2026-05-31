package thaumcraft.common.golems.seals;
import com.mojang.authlib.GameProfile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.UUID;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.Connection;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.tasks.Task;
import thaumcraft.common.golems.client.gui.SealBaseContainer;
import thaumcraft.common.golems.client.gui.SealBaseGUI;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.network.FakeNetHandlerPlayServer;
import thaumcraft.common.lib.utils.BlockUtils;


public class SealBreaker extends SealFiltered implements ISealConfigArea, ISealConfigToggles
{
    int delay;
    HashMap<Integer, Long> cache;
    Identifier icon;
    protected SealToggle[] props;
    
    public SealBreaker() {
        delay = new Random(System.nanoTime()).nextInt(42);
        cache = new HashMap<Integer, Long>();
        icon = Identifier.fromNamespaceAndPath("thaumcraft", "items/seals/seal_breaker");
        props = new SealToggle[] { new SealToggle(true, "pmeta", "golem.prop.meta") };
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:breaker";
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
        if (!cache.containsValue(p.asLong()) && isValidBlock(world, p)) {
            Task task = new Task(seal.getSealPos(), p);
            task.setPriority(seal.getPriority());
            task.setData((int)(world.getBlockState(p).getDestroySpeed(world, p) * 10.0f));
            TaskHandler.addTask((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), task);
            cache.put(task.getId(), p.asLong());
        }
    }
    
    private boolean isValidBlock(Level world, BlockPos p) {
        BlockState bs = world.getBlockState(p);
        if (!world.isEmptyBlock(p) && bs.getDestroySpeed(null, net.minecraft.core.BlockPos.ZERO) >= 0.0f) {
            for (ItemStack ts : getInv()) {
                if (ts != null && !ts.isEmpty()) {
                    ItemStack fs = BlockUtils.getSilkTouchDrop(bs);
                    if (fs == null || !fs.isEmpty()) {
                        fs = new ItemStack(bs.getBlock(), 1);
                    }
                    if (!getToggles()[0].value) {
                        fs.setDamageValue(32767);
                    }
                    if (isBlacklist()) {
                        if (false /* OreDictionary.itemMatches removed */) {
                            return false;
                        }
                        continue;
                    }
                    else {
                        if (!false /* OreDictionary.itemMatches removed */) {
                            return false;
                        }
                        continue;
                    }
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean onTaskCompletion(Level world, IGolemAPI golem, Task task) {
        BlockState bs = world.getBlockState(task.getPos());
        if (cache.containsKey(task.getId()) && isValidBlock(world, task.getPos())) {
            FakePlayer fp = FakePlayerFactory.get((ServerLevel)world, new GameProfile(null, "FakeThaumcraftGolem"));
            // FakePlayer handles its own connection in modern NeoForge
            fp.setPos(golem.getGolemEntity().getX(), golem.getGolemEntity().getY(), golem.getGolemEntity().getZ());
            golem.swing();
            boolean silky = getToggles().length > 1 && getToggles()[1].value;
            int bspd = silky ? 7 : 21;
            if (task.getData() > bspd) {
                float bh = bs.getDestroySpeed(null, net.minecraft.core.BlockPos.ZERO) * 10.0f;
                task.setLifespan((short)Math.max(task.getLifespan(), 10L));
                task.setData(task.getData() - bspd);
                int progress = (int)(9.0f * (1.0f - task.getData() / bh));
                world.playSound(null, task.getPos(), bs.getSoundType().getBreakSound(), SoundSource.BLOCKS, (bs.getSoundType().getVolume() + 0.7f) / 8.0f, bs.getSoundType().getPitch() * 0.5f);
                BlockUtils.destroyBlockPartially(world, golem.getGolemEntity().getId(), task.getPos(), progress);
                return false;
            }
            BlockUtils.destroyBlockPartially(world, golem.getGolemEntity().getId(), task.getPos(), 10);
            BlockUtils.harvestBlock(world, fp, task.getPos(), true, silky, 0, true);
            golem.addRankXp(1);
            cache.remove(task.getId());
        }
        task.setSuspended(true);
        return true;
    }
    
    @Override
    public boolean canGolemPerformTask(IGolemAPI golem, Task task) {
        if (cache.containsKey(task.getId()) && isValidBlock(golem.getGolemWorld(), task.getPos())) {
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
        return new int[] { 2, 1, 3, 0, 4 };
    }
    
    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[] { EnumGolemTrait.BREAKER };
    }
    
    @Override
    public EnumGolemTrait[] getForbiddenTags() {
        return null;
    }
    
    @Override
    public void onTaskStarted(Level world, IGolemAPI golem, Task task) {
    }
    
    @Override
    public SealToggle[] getToggles() {
        return props;
    }
    
    @Override
    public void setToggle(int indx, boolean value) {
        props[indx].setValue(value);
    }
}
