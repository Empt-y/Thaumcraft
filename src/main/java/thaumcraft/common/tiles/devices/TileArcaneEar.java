package thaumcraft.common.tiles.devices;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.WeakHashMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.blocks.devices.BlockArcaneEarToggle;
import thaumcraft.common.lib.utils.BlockStateUtils;


public class TileArcaneEar extends BlockEntity
{
    public byte note;
    public byte tone;
    public int redstoneSignal;
    public static WeakHashMap<Integer, ArrayList<Integer[]>> noteBlockEvents;

    public TileArcaneEar(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        note = 0;
        tone = 0;
        redstoneSignal = 0;
    }

    public CompoundTag saveAdditional(CompoundTag par1CompoundTag) {
        par1CompoundTag.putByte("note", note);
        par1CompoundTag.putByte("tone", tone);
        return par1CompoundTag;
    }

    public void loadAdditional(CompoundTag par1CompoundTag) {
        note = par1CompoundTag.getByteOr("note", (byte)0);
        tone = par1CompoundTag.getByteOr("tone", (byte)0);
        if (note < 0) {
            note = 0;
        }
        if (note > 24) {
            note = 24;
        }
    }

    public void update() {
        if (!this.level.isClientSide()) {
            if (redstoneSignal > 0) {
                --redstoneSignal;
                if (redstoneSignal == 0) {
                    Direction facing = getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING).getOpposite();
                    BlockEntity tileentity = this.level.getBlockEntity(this.worldPosition);
                    this.level.setBlock(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue((Property)IBlockEnabled.ENABLED, (Comparable)false), 3);
                    if (tileentity != null) {
                        this.level.setBlockEntity(tileentity);
                    }
                    this.level.updateNeighborsAt(this.worldPosition, getBlockState().getBlock());
                    this.level.updateNeighborsAt(this.worldPosition.relative(facing), getBlockState().getBlock());
                    BlockState state = this.level.getBlockState(this.worldPosition);
                    this.level.markAndNotifyBlock(this.worldPosition, this.level.getChunkAt(this.worldPosition), state, state, 3, 512);
                }
            }
            ArrayList<Integer[]> nbe = TileArcaneEar.noteBlockEvents.get((this.level instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)this.level).dimension().identifier().hashCode() : 0));
            if (nbe != null) {
                for (Integer[] dat : nbe) {
                    if (dat[3] == tone && dat[4] == note && worldPosition.distSqr(new net.minecraft.core.BlockPos(dat[0], dat[1], dat[2])) <= 4096.0) {
                        Direction facing2 = getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING).getOpposite();
                        triggerNote(this.level, this.worldPosition, true);
                        BlockEntity tileentity2 = this.level.getBlockEntity(this.worldPosition);
                        BlockState state2 = this.level.getBlockState(this.worldPosition);
                        if (getBlockState().getBlock() instanceof BlockArcaneEarToggle) {
                            this.level.setBlock(this.worldPosition, state2.setValue((Property)IBlockEnabled.ENABLED, (Comparable)!BlockStateUtils.isEnabled(state2)), 3);
                        }
                        else {
                            redstoneSignal = 10;
                            this.level.setBlock(this.worldPosition, state2.setValue((Property)IBlockEnabled.ENABLED, (Comparable)true), 3);
                        }
                        if (tileentity2 != null) {
                            this.level.setBlockEntity(tileentity2);
                        }
                        this.level.updateNeighborsAt(this.worldPosition, getBlockState().getBlock());
                        this.level.updateNeighborsAt(this.worldPosition.relative(facing2), getBlockState().getBlock());
                        BlockState state3 = this.level.getBlockState(this.worldPosition);
                        this.level.markAndNotifyBlock(this.worldPosition, this.level.getChunkAt(this.worldPosition), state3, state3, 3, 512);
                        break;
                    }
                }
            }
        }
    }

    public void updateTone() {
        try {
            Direction facing = getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING).getOpposite();
            BlockState iblockstate = this.level.getBlockState(this.worldPosition.relative(facing));
            tone = 0;
            Block block = iblockstate.getBlock();
            if (block == Blocks.CLAY) {
                tone = 5;
            }
            if (block == Blocks.GOLD_BLOCK) {
                tone = 6;
            }
            if (block == Blocks.WHITE_WOOL || block == Blocks.ORANGE_WOOL || block == Blocks.MAGENTA_WOOL) {
                tone = 7;
            }
            if (block == Blocks.PACKED_ICE) {
                tone = 8;
            }
            if (block == Blocks.BONE_BLOCK) {
                tone = 9;
            }
            setChanged();
        }
        catch (Exception ex) {}
    }

    public void changePitch() {
        note = (byte)((note + 1) % 25);
        setChanged();
    }

    public void triggerNote(Level world, BlockPos pos, boolean sound) {
        byte i = -1;
        if (sound) {
            Direction facing = getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING).getOpposite();
            BlockState iblockstate = world.getBlockState(pos.relative(facing));
            i = 0;
            Block block = iblockstate.getBlock();
            if (block == Blocks.CLAY) {
                i = 5;
            }
            if (block == Blocks.GOLD_BLOCK) {
                i = 6;
            }
            if (block == Blocks.WHITE_WOOL || block == Blocks.ORANGE_WOOL || block == Blocks.MAGENTA_WOOL) {
                i = 7;
            }
            if (block == Blocks.PACKED_ICE) {
                i = 8;
            }
            if (block == Blocks.BONE_BLOCK) {
                i = 9;
            }
        }
        this.level.blockEvent(pos, getBlockState().getBlock(), i, note);
    }

    static {
        TileArcaneEar.noteBlockEvents = new WeakHashMap<Integer, ArrayList<Integer[]>>();
    }
}
