package thaumcraft.common.tiles.crafting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.container.ContainerGolemBuilder;
import thaumcraft.common.golems.GolemProperties;
import thaumcraft.common.golems.ItemGolemPlacer;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;


public class TileGolemBuilder extends TileThaumcraftInventory implements IEssentiaTransport
{
    public long golem;
    public int cost;
    public int maxCost;
    public boolean[] hasStuff;
    boolean bufferedEssentia;
    int ticks;
    public int press;
    IGolemProperties props;
    ItemStack[] components;

    public TileGolemBuilder(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state, 1);
        golem = -1L;
        cost = 0;
        maxCost = 0;
        hasStuff = null;
        bufferedEssentia = false;
        ticks = 0;
        press = 0;
        props = null;
        components = null;
    }

    @Override
    public void messageFromClient(CompoundTag nbt, net.minecraft.server.level.ServerPlayer player) {
        super.messageFromClient(nbt, player);
        if (nbt.getStringOr("check", "") != null || nbt.getIntOr("check", Integer.MIN_VALUE) != Integer.MIN_VALUE) {
            hasStuff = checkCraft(nbt.getLongOr("golem", 0L));
            byte[] ba = new byte[hasStuff.length];
            for (int a = 0; a < ba.length; ++a) {
                ba[a] = (byte)(hasStuff[a] ? 1 : 0);
            }
            CompoundTag nbt2 = new CompoundTag();
            nbt2.putByteArray("stuff", ba);
            sendMessageToClient(nbt2, player);
        } else if (nbt.contains("golem")) {
            startCraft(nbt.getLongOr("golem", 0L), player);
        }
    }

    @Override
    public void messageFromServer(CompoundTag nbt) {
        super.messageFromServer(nbt);
        if (nbt.contains("stuff")) {
            hasStuff = null;
            byte[] ba = nbt.getByteArray("stuff").orElse(new byte[0]);
            if (ba.length > 0) {
                hasStuff = new boolean[ba.length];
                for (int a = 0; a < ba.length; ++a) {
                    hasStuff[a] = (ba[a] == 1);
                }
            }
            ContainerGolemBuilder.redo = true;
        }
    }

    @Override
    public void readSyncNBT(CompoundTag nbttagcompound) {
        super.readSyncNBT(nbttagcompound);
        golem = nbttagcompound.getLongOr("golem", 0L);
        cost = nbttagcompound.getIntOr("cost", 0);
        maxCost = nbttagcompound.getIntOr("mcost", 0);
        if (golem >= 0L) {
            try {
                props = GolemProperties.fromLong(golem);
                components = props.generateComponents();
            } catch (Exception e) {
                props = null;
                components = null;
                cost = 0;
                golem = -1L;
            }
        }
    }

    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbttagcompound) {
        nbttagcompound.putLong("golem", golem);
        nbttagcompound.putInt("cost", cost);
        nbttagcompound.putInt("mcost", maxCost);
        return super.writeSyncNBT(nbttagcompound);
    }

    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.getX() - 1, worldPosition.getY(), worldPosition.getZ() - 1,
                        worldPosition.getX() + 2, worldPosition.getY() + 2, worldPosition.getZ() + 2);
    }

    @Override
    public void update() {
        super.update();
        if (level == null) return;
        boolean complete = false;
        if (!getLevel().isClientSide()) {
            ++ticks;
            if (ticks % 5 == 0 && !complete && cost > 0 && golem >= 0L) {
                if (bufferedEssentia || drawEssentia()) {
                    bufferedEssentia = false;
                    --cost;
                    setChanged();
                }
                if (cost <= 0) {
                    ItemStack placer = new ItemStack(ItemsTC.golemPlacer);
                    net.minecraft.nbt.CompoundTag propsTag = new net.minecraft.nbt.CompoundTag();
                    propsTag.putLong("props", golem);
                    placer.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                               net.minecraft.world.item.component.CustomData.of(propsTag));
                    ItemStack current = getItem(0);
                    if (current.isEmpty() || (current.getCount() < current.getMaxStackSize()
                            && ItemStack.isSameItem(current, placer) && ItemStack.isSameItemSameComponents(current, placer))) {
                        if (current.isEmpty()) {
                            setItem(0, placer.copy());
                        } else {
                            current.grow(1);
                        }
                        complete = true;
                        getLevel().playSound(null, worldPosition, SoundsTC.wand, SoundSource.BLOCKS, 1.0f, 1.0f);
                    }
                }
            }
        } else {
            if (press < 90 && cost > 0 && golem > 0L) {
                press += 6;
                if (press >= 60) {
                    getLevel().playLocalSound(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                        SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.66f, 1.0f + level.getRandom().nextFloat() * 0.1f, false);
                    for (int a = 0; a < 16; ++a) {
                        FXDispatcher.INSTANCE.drawVentParticles(
                            worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5,
                            level.getRandom().nextGaussian() * 0.1, 0.0, level.getRandom().nextGaussian() * 0.1, 11184810);
                    }
                }
            }
            if (press >= 90 && level.getRandom().nextInt(8) == 0) {
                FXDispatcher.INSTANCE.drawVentParticles(
                    worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5,
                    level.getRandom().nextGaussian() * 0.1, 0.0, level.getRandom().nextGaussian() * 0.1, 11184810);
                getLevel().playLocalSound(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                    SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.1f, 1.0f + level.getRandom().nextFloat() * 0.1f, false);
            }
            if (press > 0 && (cost <= 0 || golem == -1L)) {
                if (press >= 90) {
                    for (int a = 0; a < 10; ++a) {
                        FXDispatcher.INSTANCE.drawVentParticles(
                            worldPosition.getX() + 0.5, worldPosition.getY() + 1, worldPosition.getZ() + 0.5,
                            level.getRandom().nextGaussian() * 0.1, 0.0, level.getRandom().nextGaussian() * 0.1, 11184810);
                    }
                }
                press -= 3;
            }
        }
        if (complete) {
            cost = 0;
            golem = -1L;
            syncTile(false);
            setChanged();
        }
    }

    public boolean[] checkCraft(long id) {
        IGolemProperties props = GolemProperties.fromLong(id);
        ItemStack[] cc = props.generateComponents();
        boolean[] ret = new boolean[cc.length];
        int a = 0;
        for (ItemStack stack : props.generateComponents()) {
            ret[a] = InventoryUtils.checkAdjacentChests(level, worldPosition, stack);
            ++a;
        }
        return ret;
    }

    public boolean startCraft(long id, Player p) {
        ItemStack placer = new ItemStack(ItemsTC.golemPlacer);
        ItemStack current = getItem(0);
        if (!current.isEmpty() && (current.getCount() >= current.getMaxStackSize()
                || !ItemStack.isSameItem(current, placer) || !ItemStack.isSameItemSameComponents(current, placer))) {
            cost = 0;
            props = null;
            components = null;
            golem = -1L;
            return false;
        }
        golem = id;
        props = GolemProperties.fromLong(golem);
        components = props.generateComponents();
        if (!InventoryUtils.consumeItemsFromAdjacentInventoryOrPlayer(level, worldPosition, p, true, components)) {
            cost = 0;
            props = null;
            components = null;
            golem = -1L;
            return false;
        }
        cost = props.getTraits().size() * 2;
        for (ItemStack stack : components) {
            cost += stack.getCount();
        }
        InventoryUtils.consumeItemsFromAdjacentInventoryOrPlayer(level, worldPosition, p, false, components);
        maxCost = cost;
        setChanged();
        syncTile(false);
        if (level != null) {
            getLevel().playSound(null, worldPosition, SoundsTC.wand, SoundSource.BLOCKS, 0.25f, 1.0f);
        }
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int par1, ItemStack stack2) {
        return stack2 != null && !stack2.isEmpty() && stack2.getItem() instanceof ItemGolemPlacer;
    }

    boolean drawEssentia() {
        for (Direction face : Direction.values()) {
            BlockEntity te = ThaumcraftApiHelper.getConnectableTile(level, worldPosition, face);
            if (te != null) {
                IEssentiaTransport ic = (IEssentiaTransport)te;
                if (!ic.canOutputTo(face.getOpposite())) {
                    return false;
                }
                if (ic.getSuctionAmount(face.getOpposite()) < getSuctionAmount(face) && ic.takeEssentia(Aspect.MECHANISM, 1, face.getOpposite()) == 1) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isConnectable(Direction face) {
        return face.getStepY() == 0 || face == Direction.DOWN;
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return isConnectable(face);
    }

    @Override
    public boolean canOutputTo(Direction face) {
        return false;
    }

    @Override
    public void setSuction(Aspect aspect, int amount) {
    }

    @Override
    public int getMinimumSuction() {
        return 0;
    }

    @Override
    public Aspect getSuctionType(Direction face) {
        return Aspect.MECHANISM;
    }

    @Override
    public int getSuctionAmount(Direction face) {
        return (cost > 0 && golem >= 0L) ? 128 : 0;
    }

    @Override
    public Aspect getEssentiaType(Direction loc) {
        return null;
    }

    @Override
    public int getEssentiaAmount(Direction loc) {
        return 0;
    }

    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction facing) {
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, Direction facing) {
        if (!bufferedEssentia && cost > 0 && golem >= 0L && aspect == Aspect.MECHANISM) {
            bufferedEssentia = true;
            return 1;
        }
        return 0;
    }

    public boolean canRenderBreaking() {
        return true;
    }
}
