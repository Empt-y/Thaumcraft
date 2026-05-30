
package thaumcraft.common.lib.network.tiles;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.tiles.TileThaumcraft;
import io.netty.buffer.ByteBuf;


public class PacketTileToServer implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketTileToServer> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "tile_to_server"));

    public static final StreamCodec<FriendlyByteBuf, PacketTileToServer> STREAM_CODEC =
        StreamCodec.of(PacketTileToServer::encode, PacketTileToServer::decode);

    private final long pos;
    private final CompoundTag nbt;

    public PacketTileToServer(long pos, CompoundTag nbt) {
        this.pos = pos;
        this.nbt = nbt;
    }

    public PacketTileToServer(BlockPos pos, CompoundTag nbt) {
        this(pos.asLong(), nbt);
    }

    private static void encode(FriendlyByteBuf buf, PacketTileToServer pkt) {
        buf.writeLong(pkt.pos);
        Utils.writeCompoundTagToBuffer(buf, pkt.nbt);
    }

    private static PacketTileToServer decode(FriendlyByteBuf buf) {
        long pos = buf.readLong();
        CompoundTag nbt = Utils.readCompoundTagFromBuffer(buf);
        return new PacketTileToServer(pos, nbt);
    }

    public static void handle(PacketTileToServer msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer serverPlayer = (ServerPlayer) ctx.player();
            var level = (ServerLevel) serverPlayer.level();
            BlockPos bp = BlockPos.of(msg.pos);
            if (level != null && bp != null) {
                BlockEntity te = level.getBlockEntity(bp);
                if (te != null && te instanceof TileThaumcraft) {
                    ((TileThaumcraft) te).messageFromClient((msg.nbt == null) ? new CompoundTag() : msg.nbt, serverPlayer);
                }
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
