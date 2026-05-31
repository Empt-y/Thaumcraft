package thaumcraft.common.lib.network.fx;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.IFocusElement;
import io.netty.buffer.ByteBuf;


public class PacketFXFocusPartImpact implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketFXFocusPartImpact> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "fx_focus_part_impact"));

    public static final StreamCodec<FriendlyByteBuf, PacketFXFocusPartImpact> STREAM_CODEC =
        StreamCodec.of(PacketFXFocusPartImpact::encode, PacketFXFocusPartImpact::decode);

    final double x;
    final double y;
    final double z;
    final String parts;

    public PacketFXFocusPartImpact(double x, double y, double z, String parts) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.parts = parts;
    }

    public PacketFXFocusPartImpact(double x, double y, double z, String[] parts) {
        this.x = x;
        this.y = y;
        this.z = z;
        StringBuilder sb = new StringBuilder();
        for (int a = 0; a < parts.length; ++a) {
            if (a > 0) sb.append("%");
            sb.append(parts[a]);
        }
        this.parts = sb.toString();
    }

    private static void encode(FriendlyByteBuf buf, PacketFXFocusPartImpact pkt) {
        buf.writeFloat((float) pkt.x);
        buf.writeFloat((float) pkt.y);
        buf.writeFloat((float) pkt.z);
        buf.writeUtf(pkt.parts);
    }

    private static PacketFXFocusPartImpact decode(FriendlyByteBuf buf) {
        double x = buf.readFloat();
        double y = buf.readFloat();
        double z = buf.readFloat();
        String parts = buf.readUtf();
        return new PacketFXFocusPartImpact(x, y, z, parts);
    }

    public static void handle(PacketFXFocusPartImpact msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            processMessage(msg);
        });
    }

    private static void processMessage(PacketFXFocusPartImpact message) {
        String[] partKeys = message.parts.split("%");
        int amt = Math.max(1, 15 / partKeys.length);
        Random r = new Random(Minecraft.getInstance().level.getRandom().nextLong());
        for (String k : partKeys) {
            IFocusElement part = FocusEngine.getElement(k);
            if (part != null && part instanceof FocusEffect) {
                for (int a = 0; a < amt; ++a) {
                    ((FocusEffect) part).renderParticleFX(Minecraft.getInstance().level, message.x, message.y, message.z, r.nextGaussian() * 0.15, r.nextGaussian() * 0.15, r.nextGaussian() * 0.15);
                }
            }
        }
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
