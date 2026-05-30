package thaumcraft.common.lib.network.fx;

import java.awt.Color;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.client.fx.FXDispatcher;
import io.netty.buffer.ByteBuf;


public class PacketFXZap implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketFXZap> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "fx_zap"));

    public static final StreamCodec<FriendlyByteBuf, PacketFXZap> STREAM_CODEC =
        StreamCodec.of(PacketFXZap::encode, PacketFXZap::decode);

    private final Vec3 source;
    private final Vec3 target;
    private final int color;
    private final float width;

    public PacketFXZap(Vec3 source, Vec3 target, int color, float width) {
        this.source = source;
        this.target = target;
        this.color = color;
        this.width = width;
    }

    private static void encode(FriendlyByteBuf buf, PacketFXZap pkt) {
        buf.writeDouble(pkt.source.x);
        buf.writeDouble(pkt.source.y);
        buf.writeDouble(pkt.source.z);
        buf.writeDouble(pkt.target.x);
        buf.writeDouble(pkt.target.y);
        buf.writeDouble(pkt.target.z);
        buf.writeInt(pkt.color);
        buf.writeFloat(pkt.width);
    }

    private static PacketFXZap decode(FriendlyByteBuf buf) {
        Vec3 source = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        Vec3 target = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        int color = buf.readInt();
        float width = buf.readFloat();
        return new PacketFXZap(source, target, color, width);
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(PacketFXZap msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Color c = new Color(msg.color);
            FXDispatcher.INSTANCE.arcBolt(msg.source.x, msg.source.y, msg.source.z, msg.target.x, msg.target.y, msg.target.z, c.getRed() / 255.0f, c.getGreen() / 255.0f, c.getBlue() / 255.0f, msg.width);
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
