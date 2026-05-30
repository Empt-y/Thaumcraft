package thaumcraft.common.lib.network.misc;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.client.lib.events.HudHandler;
import thaumcraft.common.world.aura.AuraChunk;
import io.netty.buffer.ByteBuf;


public class PacketAuraToClient implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketAuraToClient> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "aura_to_client"));

    public static final StreamCodec<FriendlyByteBuf, PacketAuraToClient> STREAM_CODEC =
        StreamCodec.of(PacketAuraToClient::encode, PacketAuraToClient::decode);

    private final short base;
    private final float vis;
    private final float flux;

    public PacketAuraToClient(short base, float vis, float flux) {
        this.base = base;
        this.vis = vis;
        this.flux = flux;
    }

    public PacketAuraToClient(AuraChunk ac) {
        this(ac.getBase(), ac.getVis(), ac.getFlux());
    }

    private static void encode(FriendlyByteBuf buf, PacketAuraToClient pkt) {
        buf.writeShort(pkt.base);
        buf.writeFloat(pkt.vis);
        buf.writeFloat(pkt.flux);
    }

    private static PacketAuraToClient decode(FriendlyByteBuf buf) {
        short base = buf.readShort();
        float vis = buf.readFloat();
        float flux = buf.readFloat();
        return new PacketAuraToClient(base, vis, flux);
    }

    public static void handle(PacketAuraToClient msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            HudHandler.currentAura = new AuraChunk(null, msg.base, msg.vis, msg.flux);
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
