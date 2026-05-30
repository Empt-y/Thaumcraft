package thaumcraft.common.tiles.misc;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
// ITickable removed - use BlockEntityTicker<T>
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.items.casters.foci.FocusEffectRift;


public class TileHole extends TileMemory 
{
    public short countdown;
    public short countdownmax;
    public byte count;
    public Direction direction;
    
    public TileHole(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        countdown = 0;
        countdownmax = 120;
        count = 0;
        direction = null;
    }
    
    
    public void update() {
        if (getLevel().isClientSide()) {
            for (int a = 0; a < 2; ++a) {
                surroundwithsparkles();
            }
        }
        else {
            if (countdown == 0 && count > 1 && direction != null) {
                switch (direction.getAxis()) {
                    case Y: {
                        for (int a = 0; a < 9; ++a) {
                            if (a / 3 != 1 || a % 3 != 1) {
                                FocusEffectRift.createHole(level, getBlockPos().offset(-1 + a / 3, 0, -1 + a % 3), null, (byte)1, countdownmax);
                            }
                        }
                        break;
                    }
                    case Z: {
                        for (int a = 0; a < 9; ++a) {
                            if (a / 3 != 1 || a % 3 != 1) {
                                FocusEffectRift.createHole(level, getBlockPos().offset(-1 + a / 3, -1 + a % 3, 0), null, (byte)1, countdownmax);
                            }
                        }
                        break;
                    }
                    case X: {
                        for (int a = 0; a < 9; ++a) {
                            if (a / 3 != 1 || a % 3 != 1) {
                                FocusEffectRift.createHole(level, getBlockPos().offset(0, -1 + a / 3, -1 + a % 3), null, (byte)1, countdownmax);
                            }
                        }
                        break;
                    }
                }
                if (!FocusEffectRift.createHole(level, getBlockPos().relative(direction.getOpposite()), direction, (byte)(count - 1), countdownmax)) {
                    count = 0;
                }
            }
            ++countdown;
            if (countdown % 20 == 0) {
                setChanged();
            }
            if (countdown >= countdownmax) {
                getLevel().setBlock(getBlockPos(), oldblock, 3);
            }
        }
    }
    
    private void surroundwithsparkles() {
        for (Direction d1 : Direction.values()) {
            BlockState b1 = getLevel().getBlockState(getBlockPos().relative(d1));
            if (b1.getBlock() != BlocksTC.hole && !b1.canOcclude()) {
                for (Direction d2 : Direction.values()) {
                    if (d1.getAxis() != d2.getAxis() && (getLevel().getBlockState(getBlockPos().relative(d2)).canOcclude() || getLevel().getBlockState(getBlockPos().relative(d1).relative(d2)).canOcclude())) {
                        float sx = 0.5f * d1.getStepX();
                        float sy = 0.5f * d1.getStepY();
                        float sz = 0.5f * d1.getStepZ();
                        if (sx == 0.0f) {
                            sx = 0.5f * d2.getStepX();
                        }
                        if (sy == 0.0f) {
                            sy = 0.5f * d2.getStepY();
                        }
                        if (sz == 0.0f) {
                            sz = 0.5f * d2.getStepZ();
                        }
                        if (sx == 0.0f) {
                            sx = net.minecraft.util.RandomSource.create().nextFloat();
                        }
                        else {
                            sx += 0.5f;
                        }
                        if (sy == 0.0f) {
                            sy = net.minecraft.util.RandomSource.create().nextFloat();
                        }
                        else {
                            sy += 0.5f;
                        }
                        if (sz == 0.0f) {
                            sz = net.minecraft.util.RandomSource.create().nextFloat();
                        }
                        else {
                            sz += 0.5f;
                        }
                        FXDispatcher.INSTANCE.sparkle(getBlockPos().getX() + sx, getBlockPos().getY() + sy, getBlockPos().getZ() + sz, 0.25f, 0.25f, 1.0f);
                    }
                }
            }
        }
    }
    
        public void loadAdditional(CompoundTag nbttagcompound) {
        /* super.loadAdditional removed - CompoundTag not compatible with ValueInput */
        countdown = nbttagcompound.getShortOr("countdown", (short)0);
        countdownmax = nbttagcompound.getShortOr("countdownmax", (short)0);
        count = nbttagcompound.getByteOr("count", (byte)0);
        byte db = nbttagcompound.getByteOr("direction", (byte)0);
        direction = ((db >= 0) ? Direction.values()[db] : null);
    }
    
        public CompoundTag saveAdditional(CompoundTag nbttagcompound) {
        /* super.saveAdditional removed - CompoundTag not compatible with ValueOutput */
        nbttagcompound.putShort("countdown", countdown);
        nbttagcompound.putShort("countdownmax", countdownmax);
        nbttagcompound.putByte("count", count);
        nbttagcompound.putByte("direction", (direction == null) ? -1 : ((byte) direction.ordinal()));
        return nbttagcompound;
    }
}
