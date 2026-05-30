package thaumcraft.common.lib.network.fx;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.common.lib.events.EssentiaHandler;
import io.netty.buffer.ByteBuf;


public class PacketFXEssentiaSource implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketFXEssentiaSource> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "fx_essentia_source"));

    public static final StreamCodec<FriendlyByteBuf, PacketFXEssentiaSource> STREAM_CODEC =
        StreamCodec.of(PacketFXEssentiaSource::encode, PacketFXEssentiaSource::decode);

    private final int x;
    private final int y;
    private final int z;
    private final byte dx;
    private final byte dy;
    private final byte dz;
    private final int color;
    private final int ext;

    public PacketFXEssentiaSource(int x, int y, int z, byte dx, byte dy, byte dz, int color, int ext) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.color = color;
        this.ext = ext;
    }

    public PacketFXEssentiaSource(BlockPos p1, byte dx, byte dy, byte dz, int color, int e) {
        this(p1.getX(), p1.getY(), p1.getZ(), dx, dy, dz, color, e);
    }

    private static void encode(FriendlyByteBuf buf, PacketFXEssentiaSource pkt) {
        buf.writeInt(pkt.x);
        buf.writeInt(pkt.y);
        buf.writeInt(pkt.z);
        buf.writeInt(pkt.color);
        buf.writeByte(pkt.dx);
        buf.writeByte(pkt.dy);
        buf.writeByte(pkt.dz);
        buf.writeShort(pkt.ext);
    }

    private static PacketFXEssentiaSource decode(FriendlyByteBuf buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        int color = buf.readInt();
        byte dx = buf.readByte();
        byte dy = buf.readByte();
        byte dz = buf.readByte();
        int ext = buf.readShort();
        return new PacketFXEssentiaSource(x, y, z, dx, dy, dz, color, ext);
    }

    public static void handle(PacketFXEssentiaSource msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            int tx = msg.x - msg.dx;
            int ty = msg.y - msg.dy;
            int tz = msg.z - msg.dz;
            String key = msg.x + ":" + msg.y + ":" + msg.z + ":" + tx + ":" + ty + ":" + tz + ":" + msg.color;
            if (EssentiaHandler.sourceFX.containsKey(key)) {
                EssentiaHandler.EssentiaSourceFX sf = EssentiaHandler.sourceFX.get(key);
                EssentiaHandler.sourceFX.remove(key);
                EssentiaHandler.sourceFX.put(key, sf);
            }
            else {
                EssentiaHandler.sourceFX.put(key, new EssentiaHandler.EssentiaSourceFX(new BlockPos(msg.getX(), msg.y, msg.z), new BlockPos(tx, ty, tz), msg.color, msg.ext));
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
