package thaumcraft.common.lib.network.fx;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.casters.FocusEffect;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.IFocusElement;
import io.netty.buffer.ByteBuf;


public class PacketFXFocusPartImpactBurst implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketFXFocusPartImpactBurst> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "fx_focus_part_impact_burst"));

    public static final StreamCodec<FriendlyByteBuf, PacketFXFocusPartImpactBurst> STREAM_CODEC =
        StreamCodec.of(PacketFXFocusPartImpactBurst::encode, PacketFXFocusPartImpactBurst::decode);

    private final double x;
    private final double y;
    private final double z;
    private final String parts;

    public PacketFXFocusPartImpactBurst(double x, double y, double z, String parts) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.parts = parts;
    }

    public PacketFXFocusPartImpactBurst(double x, double y, double z, String[] parts) {
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

    private static void encode(FriendlyByteBuf buf, PacketFXFocusPartImpactBurst pkt) {
        buf.writeFloat((float) pkt.x);
        buf.writeFloat((float) pkt.y);
        buf.writeFloat((float) pkt.z);
        buf.writeUtf(pkt.parts);
    }

    private static PacketFXFocusPartImpactBurst decode(FriendlyByteBuf buf) {
        double x = buf.readFloat();
        double y = buf.readFloat();
        double z = buf.readFloat();
        String parts = buf.readUtf();
        return new PacketFXFocusPartImpactBurst(x, y, z, parts);
    }

    public static void handle(PacketFXFocusPartImpactBurst msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            processMessage(msg);
        });
    }

    @OnlyIn(Dist.CLIENT)
    private static void processMessage(PacketFXFocusPartImpactBurst message) {
        String[] partKeys = message.parts.split("%");
        int amt = Math.max(1, 20 / partKeys.length);
        Random r = new Random(Minecraft.getInstance().level.getRandom().nextLong());
        for (String k : partKeys) {
            IFocusElement part = FocusEngine.getElement(k);
            if (part != null && part instanceof FocusEffect) {
                for (int a = 0; a < amt; ++a) {
                    ((FocusEffect) part).renderParticleFX(Minecraft.getInstance().level, message.x, message.y, message.z, r.nextGaussian() * 0.4, r.nextGaussian() * 0.4, r.nextGaussian() * 0.4);
                }
            }
        }
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
