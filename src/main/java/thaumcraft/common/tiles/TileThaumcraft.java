package thaumcraft.common.tiles;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.tiles.PacketTileToClient;
import thaumcraft.common.lib.network.tiles.PacketTileToServer;


public class TileThaumcraft extends BlockEntity
{
    public TileThaumcraft(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    /** Compatibility alias for old TC code that used getPos(). */
    public BlockPos getPos() { return getBlockPos(); }

    public void sendMessageToClient(CompoundTag nbt, @Nullable ServerPlayer player) {
        if (player != null) {
            PacketHandler.sendToPlayer(new PacketTileToClient(getBlockPos(), nbt), player);
        }
        // Broadcast removed — NetworkRegistry.TargetPoint no longer exists
    }

    public void sendMessageToServer(CompoundTag nbt) {
        PacketHandler.sendToServer(new PacketTileToServer(getBlockPos(), nbt));
    }

    public void messageFromServer(CompoundTag nbt) {
    }

    public void messageFromClient(CompoundTag nbt, ServerPlayer player) {
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        /* super.loadAdditional removed - CompoundTag not compatible with ValueInput */
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        /* super.saveAdditional removed - CompoundTag not compatible with ValueOutput */
    }

    public void readSyncNBT(CompoundTag nbt) {
    }

    public CompoundTag writeSyncNBT(CompoundTag nbt) {
        return nbt;
    }

    public void syncTile(boolean rerender) {
        if (level != null) {
            BlockState state = getLevel().getBlockState(worldPosition);
            getLevel().sendBlockUpdated(worldPosition, state, state, 2 + (rerender ? 4 : 0));
        }
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag nbt = new CompoundTag();
        writeSyncNBT(nbt);
        return nbt;
    }

    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        readSyncNBT(tag);
    }

    public boolean shouldRefresh(Level world, BlockPos pos, BlockState oldState, BlockState newState) {
        return oldState.getBlock() != newState.getBlock();
    }

    public Direction getFacing() {
        return Direction.NORTH;
    }

    public boolean gettingPower() {
        return level != null && getLevel().hasNeighborSignal(worldPosition);
    }
}
