package thaumcraft.common.tiles.essentia;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.common.lib.SoundsTC;


public class TileTubeValve extends TileTube
{
    public boolean allowFlow;
    boolean wasPoweredLastTick;
    public float rotation;
    
    public TileTubeValve(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        allowFlow = true;
        wasPoweredLastTick = false;
        rotation = 0.0f;
    }
    
    @Override
    public void update() {
        if (!getLevel().isClientSide() && count % 5 == 0) {
            boolean gettingPower = gettingPower();
            if (wasPoweredLastTick && !gettingPower && !allowFlow) {
                allowFlow = true;
                getLevel().playSound(null, getBlockPos(), SoundsTC.squeek, SoundSource.BLOCKS, 0.7f, 0.9f + this.level.getRandom().nextFloat() * 0.2f);
                syncTile(true);
                setChanged();
            }
            if (!wasPoweredLastTick && gettingPower && allowFlow) {
                allowFlow = false;
                getLevel().playSound(null, getBlockPos(), SoundsTC.squeek, SoundSource.BLOCKS, 0.7f, 0.9f + this.level.getRandom().nextFloat() * 0.2f);
                syncTile(true);
                setChanged();
            }
            wasPoweredLastTick = gettingPower;
        }
        if (getLevel().isClientSide()) {
            if (!allowFlow && rotation < 360.0f) {
                rotation += 20.0f;
            }
            else if (allowFlow && rotation > 0.0f) {
                rotation -= 20.0f;
            }
        }
        super.update();
    }
    
    @Override
    public boolean onCasterRightClick(Level world, ItemStack wandstack, Player player, BlockPos bp, Direction side, InteractionHand hand) {
        HitResult hit = RayTracer.retraceBlock(world, player, getBlockPos());
        if (hit == null) {
            return false;
        }
        if (hit.getType() != net.minecraft.world.phys.HitResult.Type.MISS && true) {
            player.level().playSound(null, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), SoundsTC.tool, SoundSource.BLOCKS, 0.5f, 0.9f + player.level().getRandom().nextFloat() * 0.2f);
            player.swing(hand);
            setChanged();
            syncTile(true);
            openSides[0] = !openSides[0];
            Direction dir = Direction.values()[0];
            BlockEntity tile = world.getBlockEntity(getBlockPos().relative(dir));
            if (tile != null && tile instanceof TileTube) {
                ((TileTube)tile).openSides[dir.getOpposite().ordinal()] = openSides[0];
                syncTile(true);
                tile.setChanged();
            }
            return true;
        }
        if (0 == 6) {
            player.level().playSound(null, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), SoundsTC.tool, SoundSource.BLOCKS, 0.5f, 0.9f + player.level().getRandom().nextFloat() * 0.2f);
            player.swing(hand);
            int a = facing.ordinal();
            setChanged();
            while (++a < 20) {
                if (!canConnectSide(Direction.values()[a % 6])) {
                    a %= 6;
                    facing = Direction.values()[a];
                    syncTile(true);
                    setChanged();
                    break;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public void readSyncNBT(CompoundTag nbttagcompound) {
        super.readSyncNBT(nbttagcompound);
        allowFlow = nbttagcompound.getBooleanOr("flow");
        wasPoweredLastTick = nbttagcompound.getBooleanOr("hadpower", false);
    }
    
    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbttagcompound) {
        nbttagcompound = super.writeSyncNBT(nbttagcompound);
        nbttagcompound.putBoolean("flow", allowFlow);
        nbttagcompound.putBoolean("hadpower", wasPoweredLastTick);
        return nbttagcompound;
    }
    
    @Override
    public boolean isConnectable(Direction face) {
        return face != facing && super.isConnectable(face);
    }
    
    @Override
    public void setSuction(Aspect aspect, int amount) {
        if (allowFlow) {
            super.setSuction(aspect, amount);
        }
    }
    
    @Override
    public boolean gettingPower() {
        return getLevel().hasNeighborSignal(getBlockPos()) > 0;
    }
}
