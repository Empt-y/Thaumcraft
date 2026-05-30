package thaumcraft.common.lib.network.fx;

import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.client.fx.FXDispatcher;
import io.netty.buffer.ByteBuf;


public class PacketFXBlockArc implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketFXBlockArc> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "fx_block_arc"));

    public static final StreamCodec<FriendlyByteBuf, PacketFXBlockArc> STREAM_CODEC =
        StreamCodec.of(PacketFXBlockArc::encode, PacketFXBlockArc::decode);

    private final int x;
    private final int y;
    private final int z;
    private final float tx;
    private final float ty;
    private final float tz;
    private final float r;
    private final float g;
    private final float b;

    public PacketFXBlockArc(int x, int y, int z, float tx, float ty, float tz, float r, float g, float b) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.tx = tx;
        this.ty = ty;
        this.tz = tz;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public PacketFXBlockArc(BlockPos pos, Entity source, float r, float g, float b) {
        this(pos.getX(), pos.getY(), pos.getZ(),
             (float) source.getX(),
             (float) (source.getBoundingBox().minY + source.getBbHeight() / 2.0f),
             (float) source.getZ(),
             r, g, b);
    }

    public PacketFXBlockArc(BlockPos pos, BlockPos pos2, float r, float g, float b) {
        this(pos.getX(), pos.getY(), pos.getZ(),
             pos2.getX() + 0.5f,
             pos2.getY() + 0.5f,
             pos2.getZ() + 0.5f,
             r, g, b);
    }

    private static void encode(FriendlyByteBuf buf, PacketFXBlockArc pkt) {
        buf.writeInt(pkt.x);
        buf.writeInt(pkt.y);
        buf.writeInt(pkt.z);
        buf.writeFloat(pkt.tx);
        buf.writeFloat(pkt.ty);
        buf.writeFloat(pkt.tz);
        buf.writeFloat(pkt.r);
        buf.writeFloat(pkt.g);
        buf.writeFloat(pkt.b);
    }

    private static PacketFXBlockArc decode(FriendlyByteBuf buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        float tx = buf.readFloat();
        float ty = buf.readFloat();
        float tz = buf.readFloat();
        float r = buf.readFloat();
        float g = buf.readFloat();
        float b = buf.readFloat();
        return new PacketFXBlockArc(x, y, z, tx, ty, tz, r, g, b);
    }

    public static void handle(PacketFXBlockArc msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            FXDispatcher.INSTANCE.arcLightning(msg.tx, msg.ty, msg.tz, msg.x + 0.5, msg.y + 0.5, msg.z + 0.5, msg.r, msg.g, msg.b, 0.5f);
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
