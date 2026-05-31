package thaumcraft.common.tiles.devices;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.BonemealableBlock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
// ITickable removed - use BlockEntityTicker<T>
import net.minecraft.core.BlockPos;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXBlockMist;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.CropUtils;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileLampGrowth extends TileThaumcraft implements IEssentiaTransport
{
    private boolean reserve;
    public int charges;
    public int maxCharges;
    int lx;
    int ly;
    int lz;
    Block lid;
    Block lmd;
    ArrayList<BlockPos> checklist;
    int drawDelay;

    public TileLampGrowth(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        reserve = false;
        charges = -1;
        maxCharges = 20;
        lx = 0;
        ly = 0;
        lz = 0;
        lid = Blocks.AIR;
        lmd = Blocks.AIR;
        checklist = new ArrayList<BlockPos>();
        drawDelay = 0;
    }

    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (getLevel() != null && getLevel().isClientSide()) {
            getLevel().getLightEngine().checkBlock(getBlockPos());
        }
    }

    public void update() {
        if (!getLevel().isClientSide()) {
            if (charges <= 0) {
                if (reserve) {
                    charges = maxCharges;
                    reserve = false;
                    setChanged();
                    syncTile(true);
                }
                else if (drawEssentia()) {
                    charges = maxCharges;
                    setChanged();
                    syncTile(true);
                }
                if (charges <= 0) {
                    if (BlockStateUtils.isEnabled(getBlockState())) {
                        getLevel().setBlock(getBlockPos(), getLevel().getBlockState(getBlockPos()).setValue((Property)IBlockEnabled.ENABLED, (Comparable)false), 3);
                    }
                }
                else if (!gettingPower() && !BlockStateUtils.isEnabled(getBlockState())) {
                    getLevel().setBlock(getBlockPos(), getLevel().getBlockState(getBlockPos()).setValue((Property)IBlockEnabled.ENABLED, (Comparable)true), 3);
                }
            }
            if (!reserve && drawEssentia()) {
                reserve = true;
            }
            if (charges == 0) {
                charges = -1;
                syncTile(true);
            }
            if (!gettingPower() && charges > 0) {
                updatePlant();
            }
        }
    }

    boolean isPlant(BlockPos bp) {
        BlockState b = getLevel().getBlockState(bp);
        boolean flag = b.getBlock() instanceof BonemealableBlock;
        // Material was removed in 1.20; use tag/block checks instead
        return flag;
    }

    private void updatePlant() {
        BlockState bs = getLevel().getBlockState(new BlockPos(lx, ly, lz));
        if (lid != bs.getBlock() || lmd != bs.getBlock()) {
            Player p = getLevel().getNearestPlayer(lx, ly, lz, 32.0, false);
            if (p != null && getLevel() instanceof net.minecraft.server.level.ServerLevel sl) {
                PacketHandler.sendToAllAround(new PacketFXBlockMist(new BlockPos(lx, ly, lz), 4259648), sl, lx, ly, lz, 32.0);
            }
            lid = bs.getBlock();
            lmd = bs.getBlock();
        }
        int distance = 6;
        if (checklist.size() == 0) {
            for (int a = -distance; a <= distance; ++a) {
                for (int b = -distance; b <= distance; ++b) {
                    checklist.add(getBlockPos().offset(a, distance, b));
                }
            }
            Collections.shuffle(checklist, new Random());
        }
        int x = checklist.get(0).getX();
        int y = checklist.get(0).getY();
        int z = checklist.get(0).getZ();
        checklist.remove(0);
        while (y >= getBlockPos().getY() - distance) {
            BlockPos bp = new BlockPos(x, y, z);
            double dx = getBlockPos().getX() - (x + 0.5);
            double dy = getBlockPos().getY() - (y + 0.5);
            double dz = getBlockPos().getZ() - (z + 0.5);
            double distSq = dx*dx + dy*dy + dz*dz;
            if (!getLevel().isEmptyBlock(bp) && isPlant(bp) && distSq < distance * distance && !CropUtils.isGrownCrop(getLevel(), bp) && CropUtils.doesLampGrow(getLevel(), bp)) {
                --charges;
                lx = x;
                ly = y;
                lz = z;
                BlockState bs2 = getLevel().getBlockState(bp);
                lid = bs2.getBlock();
                lmd = bs2.getBlock();
                getLevel().scheduleTick(bp, lid, 1);
                return;
            }
            --y;
        }
    }

    @Override
    public void readSyncNBT(CompoundTag nbttagcompound) {
        reserve = nbttagcompound.getBooleanOr("reserve", false);
        charges = nbttagcompound.getIntOr("charges", 0);
    }

    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbttagcompound) {
        nbttagcompound.putBoolean("reserve", reserve);
        nbttagcompound.putInt("charges", charges);
        return nbttagcompound;
    }

    boolean drawEssentia() {
        if (++drawDelay % 5 != 0) {
            return false;
        }
        BlockEntity te = ThaumcraftApiHelper.getConnectableTile(getLevel(), getBlockPos(), getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING));
        if (te != null) {
            IEssentiaTransport ic = (IEssentiaTransport)te;
            if (!ic.canOutputTo(getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING).getOpposite())) {
                return false;
            }
            if (ic.getSuctionAmount(getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING).getOpposite()) < getSuctionAmount(getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING)) && ic.takeEssentia(Aspect.PLANT, 1, getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING).getOpposite()) == 1) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isConnectable(Direction face) {
        return face == getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
    }

    @Override
    public boolean canInputFrom(Direction face) {
        return face == getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
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
        return Aspect.PLANT;
    }

    @Override
    public int getSuctionAmount(Direction face) {
        return (face == getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING) && (!reserve || charges <= 0)) ? 128 : 0;
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
    public int takeEssentia(Aspect aspect, int amount, Direction loc) {
        return 0;
    }

    @Override
    public int addEssentia(Aspect aspect, int amount, Direction loc) {
        return 0;
    }
}
