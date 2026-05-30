
package thaumcraft.common.lib.network.misc;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.tiles.devices.TileArcaneEar;
import io.netty.buffer.ByteBuf;


public class PacketNote implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketNote> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "note"));

    public static final StreamCodec<FriendlyByteBuf, PacketNote> STREAM_CODEC =
        StreamCodec.of(PacketNote::encode, PacketNote::decode);

    private final int x;
    private final int y;
    private final int z;
    private final int dim;
    private final byte note;

    public PacketNote(int x, int y, int z, int dim) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;
        this.note = -1;
    }

    public PacketNote(int x, int y, int z, int dim, byte note) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dim = dim;
        this.note = note;
    }

    private static void encode(FriendlyByteBuf buf, PacketNote pkt) {
        buf.writeInt(pkt.x);
        buf.writeInt(pkt.y);
        buf.writeInt(pkt.z);
        buf.writeInt(pkt.dim);
        buf.writeByte(pkt.note);
    }

    private static PacketNote decode(FriendlyByteBuf buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        int dim = buf.readInt();
        byte note = buf.readByte();
        return new PacketNote(x, y, z, dim, note);
    }

    public static void handle(PacketNote msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            // Determine side: if ctx.player() is a ServerPlayer, we're on the server
            if (ctx.player() instanceof ServerPlayer) {
                // Server side: look up tile, read note, broadcast to nearby players
                if (msg.note == -1) {
                    // TODO: null /* TODO: DimensionManager removed */ doesn't exist in NeoForge 26.1.2
                    // Use server-side dimension lookup via ctx.player().getServer().getLevel(...)
                    // For now, use the player's current level as a best-effort fallback
                    var level = (ServerLevel) ((ServerPlayer) ctx.player()).level();
                    if (level == null) return;
                    BlockEntity tile2 = level.getBlockEntity(new BlockPos(msg.x, msg.y, msg.z));
                    byte note = -1;
                    if (tile2 != null && tile2 instanceof net.minecraft.world.level.block.entity.NoteBlockBlockEntity) {
                        note = (byte)((net.minecraft.world.level.block.entity.NoteBlockBlockEntity)tile2).getNote();
                    }
                    else if (tile2 != null && tile2 instanceof TileArcaneEar) {
                        note = ((TileArcaneEar)tile2).note;
                    }
                    if (note >= 0) {
                        final byte finalNote = note;
                        /* sendToAllAround stub */
                    }
                    if (tile != null && tile instanceof net.minecraft.world.level.block.entity.NoteBlockBlockEntity) {
                        // NoteBlockBlockEntity.setNote is not directly settable via a simple field in NeoForge 26.1.2
                        // TODO: verify correct API for setting note on NoteBlockBlockEntity client-side
                    }
                    else if (tile != null && tile instanceof TileArcaneEar) {
                        ((TileArcaneEar)tile).note = msg.note;
                    }
                }
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
