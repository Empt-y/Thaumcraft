package thaumcraft.common.lib.network.fx;

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


public class PacketFXFocusEffect implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketFXFocusEffect> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "fx_focus_effect"));

    public static final StreamCodec<FriendlyByteBuf, PacketFXFocusEffect> STREAM_CODEC =
        StreamCodec.of(PacketFXFocusEffect::encode, PacketFXFocusEffect::decode);

    final float x;
    final float y;
    final float z;
    final float mx;
    final float my;
    final float mz;
    final String parts;

    public PacketFXFocusEffect(float x, float y, float z, float mx, float my, float mz, String parts) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.mx = mx;
        this.my = my;
        this.mz = mz;
        this.parts = parts;
    }

    public PacketFXFocusEffect(float x, float y, float z, float mx, float my, float mz, String[] parts) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.mx = mx;
        this.my = my;
        this.mz = mz;
        StringBuilder sb = new StringBuilder();
        for (int a = 0; a < parts.length; ++a) {
            if (a > 0) sb.append("%");
            sb.append(parts[a]);
        }
        this.parts = sb.toString();
    }

    private static void encode(FriendlyByteBuf buf, PacketFXFocusEffect pkt) {
        buf.writeFloat(pkt.x);
        buf.writeFloat(pkt.y);
        buf.writeFloat(pkt.z);
        buf.writeFloat(pkt.mx);
        buf.writeFloat(pkt.my);
        buf.writeFloat(pkt.mz);
        buf.writeUtf(pkt.parts);
    }

    private static PacketFXFocusEffect decode(FriendlyByteBuf buf) {
        float x = buf.readFloat();
        float y = buf.readFloat();
        float z = buf.readFloat();
        float mx = buf.readFloat();
        float my = buf.readFloat();
        float mz = buf.readFloat();
        String parts = buf.readUtf();
        return new PacketFXFocusEffect(x, y, z, mx, my, mz, parts);
    }

    public static void handle(PacketFXFocusEffect msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            processMessage(msg);
        });
    }

    private static void processMessage(PacketFXFocusEffect message) {
        String[] partKeys = message.parts.split("%");
        int amt = Math.max(1, 10 / partKeys.length);
        for (String k : partKeys) {
            IFocusElement part = FocusEngine.getElement(k);
            if (part != null && part instanceof FocusEffect) {
                for (int a = 0; a < amt; ++a) {
                    ((FocusEffect) part).renderParticleFX(Minecraft.getInstance().level, message.x, message.y, message.z, message.mx + Minecraft.getInstance().level.getRandom().nextGaussian() / 20.0, message.my + Minecraft.getInstance().level.getRandom().nextGaussian() / 20.0, message.mz + Minecraft.getInstance().level.getRandom().nextGaussian() / 20.0);
                }
            }
        }
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
