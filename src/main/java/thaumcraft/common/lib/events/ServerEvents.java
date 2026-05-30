package thaumcraft.common.lib.events;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import com.google.common.base.Predicate;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import javax.annotation.Nullable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.entities.EntitySpecialItem;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.golems.tasks.TaskHandler;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockBamf;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.devices.TileArcaneEar;
import thaumcraft.common.world.ThaumcraftWorldGenerator;
import thaumcraft.common.world.aura.AuraHandler;
import thaumcraft.common.world.aura.AuraThread;


@net.neoforged.fml.common.EventBusSubscriber(modid = "thaumcraft")
public class ServerEvents
{
    long lastcheck;
    static HashMap<Integer, Integer> serverTicks;
    public static ConcurrentHashMap<Integer, AuraThread> auraThreads;
    DecimalFormat myFormatter;
    public static HashMap<Integer, LinkedBlockingQueue<BreakData>> breakList;
    public static HashMap<Integer, LinkedBlockingQueue<VirtualSwapper>> swapList;
    public static HashMap<Integer, ArrayList<ChunkPos>> chunksToGenerate;
    public static Predicate<SwapperPredicate> DEFAULT_PREDICATE;
    private static HashMap<Integer, LinkedBlockingQueue<RunnableEntry>> serverRunList;
    private static LinkedBlockingQueue<RunnableEntry> clientRunList;

    public ServerEvents() {
        lastcheck = 0L;
        myFormatter = new DecimalFormat("#######.##");
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void clientWorldTick(ClientTickEvent.Post event) {
        if (!ServerEvents.clientRunList.isEmpty()) {
            LinkedBlockingQueue<RunnableEntry> temp = new LinkedBlockingQueue<RunnableEntry>();
            while (!ServerEvents.clientRunList.isEmpty()) {
                RunnableEntry current = ServerEvents.clientRunList.poll();
                if (current != null) {
                    if (current.delay > 0) {
                        RunnableEntry runnableEntry = current;
                        --runnableEntry.delay;
                        temp.offer(current);
                    }
                    else {
                        try {
                            current.runnable.run();
                        }
                        catch (Exception ex) {}
                    }
                }
            }
            while (!temp.isEmpty()) {
                ServerEvents.clientRunList.offer(temp.poll());
            }
        }
    }

    /** Pre-tick: start aura threads for newly loaded dimensions. */
    @SubscribeEvent
    public static void worldTickPre(LevelTickEvent.Pre event) {
        int dim = (event.getLevel() instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)event.getLevel()).dimension().identifier().hashCode() : 0);
        if (!ServerEvents.auraThreads.containsKey(dim) && AuraHandler.getAuraWorld(dim) != null) {
            AuraThread at = new AuraThread(dim);
            Thread thread = new Thread(at);
            thread.start();
            ServerEvents.auraThreads.put(dim, at);
        }
    }

    /** Post-tick: run queued runnables, swap/break lists, aura dirty chunk marking. */
    @SubscribeEvent
    public static void worldTick(LevelTickEvent.Post event) {
        int dim = (event.getLevel() instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)event.getLevel()).dimension().identifier().hashCode() : 0);
        if (!ServerEvents.serverTicks.containsKey(dim)) {
            ServerEvents.serverTicks.put(dim, 0);
        }
        LinkedBlockingQueue<RunnableEntry> rlist = ServerEvents.serverRunList.get(dim);
        if (rlist == null) {
            ServerEvents.serverRunList.put(dim, rlist = new LinkedBlockingQueue<RunnableEntry>());
        }
        else if (!rlist.isEmpty()) {
            LinkedBlockingQueue<RunnableEntry> temp = new LinkedBlockingQueue<RunnableEntry>();
            while (!rlist.isEmpty()) {
                RunnableEntry current = rlist.poll();
                if (current != null) {
                    if (current.delay > 0) {
                        RunnableEntry runnableEntry = current;
                        --runnableEntry.delay;
                        temp.offer(current);
                    }
                    else {
                        try {
                            current.runnable.run();
                        }
                        catch (Exception ex) {}
                    }
                }
            }
            while (!temp.isEmpty()) {
                rlist.offer(temp.poll());
            }
        }
        int ticks = ServerEvents.serverTicks.get(dim);
        tickChunkRegeneration(event);
        tickBlockSwap(event.getLevel());
        tickBlockBreak(event.getLevel());
        ArrayList<Integer[]> nbe = TileArcaneEar.noteBlockEvents.get(dim);
        if (nbe != null) {
            nbe.clear();
        }
        if (ticks % 20 == 0) {
            CopyOnWriteArrayList<ChunkPos> dc = AuraHandler.dirtyChunks.get(dim);
            if (dc != null && dc.size() > 0) {
                // Mark the chunk unsaved so data is written on next save cycle
                for (ChunkPos pos : dc) {
                    if (event.getLevel() instanceof net.minecraft.server.level.ServerLevel sl) {
                        net.minecraft.world.level.chunk.LevelChunk chunk = sl.getChunk(pos.x(), pos.z());
                        chunk.markUnsaved();
                    }
                }
                dc.clear();
            }
            if (AuraHandler.riftTrigger.containsKey(dim)) {
                if (!ModConfig.CONFIG_MISC.wussMode) {
                    EntityFluxRift.createRift(event.getLevel(), AuraHandler.riftTrigger.get(dim));
                }
                AuraHandler.riftTrigger.remove(dim);
            }
            TaskHandler.clearSuspendedOrExpiredTasks(event.getLevel());
        }
        SealHandler.tickSealEntities(event.getLevel());
        ServerEvents.serverTicks.put(dim, ticks + 1);
    }

    public static void tickChunkRegeneration(LevelTickEvent.Post event) {
        int dim = (event.getLevel() instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)event.getLevel()).dimension().identifier().hashCode() : 0);
        int count = 0;
        ArrayList<ChunkPos> chunks = ServerEvents.chunksToGenerate.get(dim);
        if (chunks != null && chunks.size() > 0) {
            for (int a = 0; a < 10; ++a) {
                chunks = ServerEvents.chunksToGenerate.get(dim);
                if (chunks == null || chunks.size() <= 0) {
                    break;
                }
                ++count;
                ChunkPos loc = chunks.get(0);
                long worldSeed = (event.getLevel() instanceof net.minecraft.server.level.ServerLevel sl ? sl.getSeed() : 0L);
                Random fmlRandom = new Random(worldSeed);
                long xSeed = fmlRandom.nextLong() >> 3;
                long zSeed = fmlRandom.nextLong() >> 3;
                fmlRandom.setSeed(xSeed * loc.x() + zSeed * loc.z() ^ worldSeed);
                ThaumcraftWorldGenerator.INSTANCE.worldGeneration(fmlRandom, loc.x(), loc.z(), event.getLevel(), false);
                chunks.remove(0);
                ServerEvents.chunksToGenerate.put(dim, chunks);
            }
        }
        if (count > 0) {
            thaumcraft.Thaumcraft.log.info("[Thaumcraft] Regenerated " + count + " chunks. " + Math.max(0, chunks.size()) + " chunks left");
        }
    }

    private static void tickBlockSwap(Level world) {
        int dim = (world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0);
        LinkedBlockingQueue<VirtualSwapper> queue = ServerEvents.swapList.get(dim);
        LinkedBlockingQueue<VirtualSwapper> queue2 = new LinkedBlockingQueue<VirtualSwapper>();
        if (queue != null) {
            while (!queue.isEmpty()) {
                VirtualSwapper vs = queue.poll();
                if (vs != null) {
                    BlockState bs = world.getBlockState(vs.pos);
                    boolean allow = bs.getDestroySpeed(null, net.minecraft.core.BlockPos.ZERO) >= 0.0f;
                    // Material was removed; only BlockState source type is supported now
                    if (vs.source != null && vs.source instanceof BlockState && vs.source != bs) {
                        allow = false;
                    }
                    if (vs.visCost > 0.0f && AuraHelper.getVis(world, vs.pos) < vs.visCost) {
                        allow = false;
                    }
                    // Check target match: compare by item type; skip broken event cancellation check
                    boolean targetMatch = vs.target != null && !vs.target.isEmpty()
                            && ItemStack.isSameItem(vs.target, new ItemStack(bs.getBlock()));
                    if (!world.mayInteract(vs.player, vs.pos) || !allow || targetMatch
                            || !vs.allowSwap.apply(new SwapperPredicate(world, vs.player, vs.pos))) {
                        continue;
                    }
                    int slot = -1;
                    if (!vs.consumeTarget || vs.target == null || vs.target.isEmpty()) {
                        slot = 1;
                    }
                    else {
                        slot = InventoryUtils.getPlayerSlotFor(vs.player, vs.target);
                    }
                    if (vs.player.getAbilities().instabuild) {
                        slot = 1;
                    }
                    boolean matches = false;
                    if (vs.source instanceof BlockState) {
                        matches = (bs == vs.source);
                    }
                    if ((vs.source != null && !matches) || slot < 0) {
                        continue;
                    }
                    if (!vs.player.getAbilities().instabuild) {
                        if (vs.consumeTarget) {
                            vs.player.getInventory().removeItem(slot, 1);
                        }
                        if (vs.pickup) {
                            List<ItemStack> ret = new ArrayList<ItemStack>();
                            if (vs.silk) {
                                ItemStack itemstack = BlockUtils.getSilkTouchDrop(bs);
                                if (itemstack != null && !itemstack.isEmpty()) {
                                    ret.add(itemstack);
                                }
                            }
                            else if (world instanceof net.minecraft.server.level.ServerLevel sl) {
                                ret = Block.getDrops(bs, sl, vs.pos, world.getBlockEntity(vs.pos), vs.player, ItemStack.EMPTY);
                            }
                            if (ret.size() > 0) {
                                for (ItemStack is : ret) {
                                    if (!vs.player.getInventory().add(is)) {
                                        world.addFreshEntity(new ItemEntity(world, vs.pos.getX() + 0.5, vs.pos.getY() + 0.5, vs.pos.getZ() + 0.5, is));
                                    }
                                }
                            }
                        }
                        if (vs.visCost > 0.0f) {
                            ThaumcraftApi.internalMethods.drainVis(world, vs.pos, vs.visCost, false);
                        }
                    }
                    if (vs.target == null || vs.target.isEmpty()) {
                        world.removeBlock(vs.pos, false);
                    }
                    else {
                        Block tb = Block.byItem(vs.target.getItem());
                        if (tb != null && tb != Blocks.AIR) {
                            world.setBlock(vs.pos, net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(), 3);
                        }
                        else {
                            world.removeBlock(vs.pos, false);
                            EntitySpecialItem entityItem = new EntitySpecialItem(world, vs.pos.getX() + 0.5, vs.pos.getY() + 0.1, vs.pos.getZ() + 0.5, vs.target.copy());
                            entityItem.setDeltaMovement(entityItem.getDeltaMovement().x, 0.0, entityItem.getDeltaMovement().z);
                            entityItem.setDeltaMovement(0.0, entityItem.getDeltaMovement().y, entityItem.getDeltaMovement().z);
                            entityItem.setDeltaMovement(entityItem.getDeltaMovement().x, entityItem.getDeltaMovement().y, 0.0);
                            world.addFreshEntity(entityItem);
                        }
                    }
                    if (vs.fx) {
                        /* sendToAllAround stub */
                    }
                    if (vs.lifespan <= 0) {
                        continue;
                    }
                    for (int xx = -1; xx <= 1; ++xx) {
                        for (int yy = -1; yy <= 1; ++yy) {
                            for (int zz = -1; zz <= 1; ++zz) {
                                matches = false;
                                if (vs.source instanceof BlockState) {
                                    matches = (world.getBlockState(vs.pos.offset(xx, yy, zz)) == vs.source);
                                }
                                if ((xx != 0 || yy != 0 || zz != 0) && matches && BlockUtils.isBlockExposed(world, vs.pos.offset(xx, yy, zz))) {
                                    queue2.offer(new VirtualSwapper(vs.pos.offset(xx, yy, zz), vs.source, vs.target, vs.consumeTarget, vs.lifespan - 1, vs.player, vs.fx, vs.fancy, vs.color, vs.pickup, vs.silk, vs.fortune, vs.allowSwap, vs.visCost));
                                }
                            }
                        }
                    }
                }
            }
            ServerEvents.swapList.put(dim, queue2);
        }
    }

    private static void tickBlockBreak(Level world) {
        int dim = (world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0);
        LinkedBlockingQueue<BreakData> queue = ServerEvents.breakList.get(dim);
        LinkedBlockingQueue<BreakData> queue2 = new LinkedBlockingQueue<BreakData>();
        if (queue != null) {
            while (!queue.isEmpty()) {
                BreakData vs = queue.poll();
                if (vs != null) {
                    BlockState bs = world.getBlockState(vs.pos);
                    if (bs == vs.source) {
                        if (vs.visCost > 0.0f && AuraHelper.getVis(world, vs.pos) < vs.visCost) {
                            continue;
                        }
                        if (!world.mayInteract(vs.player, vs.pos) || bs.getDestroySpeed(null, net.minecraft.core.BlockPos.ZERO) < 0.0f) {
                            continue;
                        }
                        if (vs.fx) {
                            world.destroyBlockProgress(vs.pos.hashCode(), vs.pos, (int)((1.0f - vs.durabilityCurrent / vs.durabilityMax) * 10.0f));
                        }
                        BreakData breakData = vs;
                        breakData.durabilityCurrent -= vs.strength;
                        if (vs.durabilityCurrent <= 0.0f) {
                            BlockUtils.harvestBlock(world, vs.player, vs.pos, true, vs.silk, vs.fortune, false);
                            if (vs.fx) {
                                world.destroyBlockProgress(vs.pos.hashCode(), vs.pos, -1);
                            }
                            if (vs.visCost <= 0.0f) {
                                continue;
                            }
                            ThaumcraftApi.internalMethods.drainVis(world, vs.pos, vs.visCost, false);
                        }
                        else {
                            queue2.offer(new BreakData(vs.strength, vs.durabilityCurrent, vs.durabilityMax, vs.pos, vs.source, vs.player, vs.fx, vs.silk, vs.fortune, vs.visCost));
                        }
                    }
                    else {
                        if (!vs.fx) {
                            continue;
                        }
                        world.destroyBlockProgress(vs.pos.hashCode(), vs.pos, -1);
                    }
                }
            }
            ServerEvents.breakList.put(dim, queue2);
        }
    }

    public static void addSwapper(Level world, BlockPos pos, Object source, ItemStack target, boolean consumeTarget, int life, Player player, boolean fx, boolean fancy, int color, boolean pickup, boolean silk, int fortune, Predicate<SwapperPredicate> allowSwap, float visCost) {
        int dim = (world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0);
        LinkedBlockingQueue<VirtualSwapper> queue = ServerEvents.swapList.get(dim);
        if (queue == null) {
            ServerEvents.swapList.put(dim, new LinkedBlockingQueue<VirtualSwapper>());
            queue = ServerEvents.swapList.get(dim);
        }
        queue.offer(new VirtualSwapper(pos, source, target, consumeTarget, life, player, fx, fancy, color, pickup, silk, fortune, allowSwap, visCost));
        ServerEvents.swapList.put(dim, queue);
    }

    public static void addBreaker(Level world, BlockPos pos, BlockState source, Player player, boolean fx, boolean silk, int fortune, float str, float durabilityCurrent, float durabilityMax, int delay, float vis, Runnable run) {
        int dim = (world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0);
        if (delay > 0) {
            addRunnableServer(world, new Runnable() {
                @Override
                public void run() {
                    ServerEvents.addBreaker(world, pos, source, player, fx, silk, fortune, str, durabilityCurrent, durabilityMax, 0, vis, run);
                }
            }, delay);
        }
        else {
            LinkedBlockingQueue<BreakData> queue = ServerEvents.breakList.get(dim);
            if (queue == null) {
                ServerEvents.breakList.put(dim, new LinkedBlockingQueue<BreakData>());
                queue = ServerEvents.breakList.get(dim);
            }
            queue.offer(new BreakData(str, durabilityCurrent, durabilityMax, pos, source, player, fx, silk, fortune, vis));
            ServerEvents.breakList.put(dim, queue);
            if (run != null) {
                run.run();
            }
        }
    }

    public static void addRunnableServer(Level world, Runnable runnable, int delay) {
        if (world.isClientSide()) {
            return;
        }
        LinkedBlockingQueue<RunnableEntry> rlist = ServerEvents.serverRunList.get((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0));
        if (rlist == null) {
            ServerEvents.serverRunList.put((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0), rlist = new LinkedBlockingQueue<RunnableEntry>());
        }
        rlist.add(new RunnableEntry(runnable, delay));
    }

    public static void addRunnableClient(Level world, Runnable runnable, int delay) {
        if (!world.isClientSide()) {
            return;
        }
        ServerEvents.clientRunList.add(new RunnableEntry(runnable, delay));
    }

    static {
        ServerEvents.serverTicks = new HashMap<Integer, Integer>();
        ServerEvents.auraThreads = new ConcurrentHashMap<Integer, AuraThread>();
        ServerEvents.breakList = new HashMap<Integer, LinkedBlockingQueue<BreakData>>();
        ServerEvents.swapList = new HashMap<Integer, LinkedBlockingQueue<VirtualSwapper>>();
        ServerEvents.chunksToGenerate = new HashMap<Integer, ArrayList<ChunkPos>>();
        DEFAULT_PREDICATE = new Predicate<SwapperPredicate>() {
            public boolean apply(@Nullable SwapperPredicate pred) {
                return true;
            }
        };
        ServerEvents.serverRunList = new HashMap<Integer, LinkedBlockingQueue<RunnableEntry>>();
        ServerEvents.clientRunList = new LinkedBlockingQueue<RunnableEntry>();
    }

    public static class BreakData
    {
        float strength;
        float durabilityCurrent;
        float durabilityMax;
        BlockState source;
        BlockPos pos;
        Player player;
        boolean fx;
        boolean silk;
        int fortune;
        float visCost;

        public BreakData(float strength, float durabilityCurrent, float durabilityMax, BlockPos pos, BlockState source, Player player, boolean fx, boolean silk, int fortune, float vis) {
            this.strength = 0.0f;
            this.durabilityCurrent = 1.0f;
            this.durabilityMax = 1.0f;
            this.player = null;
            this.strength = strength;
            this.source = source;
            this.pos = pos;
            this.player = player;
            this.fx = fx;
            this.silk = silk;
            this.fortune = fortune;
            this.durabilityCurrent = durabilityCurrent;
            this.durabilityMax = durabilityMax;
            visCost = vis;
        }
    }

    public static class SwapperPredicate
    {
        public Level world;
        public Player player;
        public BlockPos pos;

        public SwapperPredicate(Level world, Player player, BlockPos pos) {
            this.world = world;
            this.player = player;
            this.pos = pos;
        }
    }

    public static class VirtualSwapper
    {
        int color;
        boolean fancy;
        Predicate<SwapperPredicate> allowSwap;
        int lifespan;
        BlockPos pos;
        Object source;
        ItemStack target;
        Player player;
        boolean fx;
        boolean silk;
        boolean pickup;
        boolean consumeTarget;
        int fortune;
        float visCost;

        VirtualSwapper(BlockPos pos, Object source, ItemStack t, boolean consumeTarget, int life, Player p, boolean fx, boolean fancy, int color, boolean pickup, boolean silk, int fortune, Predicate<SwapperPredicate> allowSwap, float cost) {
            lifespan = 0;
            player = null;
            this.pos = pos;
            this.source = source;
            target = t;
            lifespan = life;
            player = p;
            this.consumeTarget = consumeTarget;
            this.fx = fx;
            this.fancy = fancy;
            this.allowSwap = allowSwap;
            this.silk = silk;
            this.fortune = fortune;
            this.pickup = pickup;
            this.color = color;
            visCost = cost;
        }
    }

    public static class RunnableEntry
    {
        Runnable runnable;
        int delay;

        public RunnableEntry(Runnable runnable, int delay) {
            this.runnable = runnable;
            this.delay = delay;
        }
    }
}
