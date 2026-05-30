package thaumcraft.common.lib.network.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.client.fx.other.FXSonic;
import io.netty.buffer.ByteBuf;


public class PacketFXSonic implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketFXSonic> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "fx_sonic"));

    public static final StreamCodec<FriendlyByteBuf, PacketFXSonic> STREAM_CODEC =
        StreamCodec.of(PacketFXSonic::encode, PacketFXSonic::decode);

    private final int source;

    public PacketFXSonic(int source) {
        this.source = source;
    }

    private static void encode(FriendlyByteBuf buf, PacketFXSonic pkt) {
        buf.writeInt(pkt.source);
    }

    private static PacketFXSonic decode(FriendlyByteBuf buf) {
        int source = buf.readInt();
        return new PacketFXSonic(source);
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(PacketFXSonic msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var level = Minecraft.getInstance().level;
            Entity p = level().getEntity(msg.source);
            if (p != null) {
                FXSonic fb = new FXSonic(level, p.getX(), p.getY(), p.getZ(), p, 10);
                Minecraft.getInstance().particleEngine.add(fb);
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
