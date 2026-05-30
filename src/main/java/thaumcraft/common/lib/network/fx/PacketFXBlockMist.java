package thaumcraft.common.lib.network.fx;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.client.fx.FXDispatcher;
import io.netty.buffer.ByteBuf;


public class PacketFXBlockMist implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketFXBlockMist> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "fx_block_mist"));

    public static final StreamCodec<FriendlyByteBuf, PacketFXBlockMist> STREAM_CODEC =
        StreamCodec.of(PacketFXBlockMist::encode, PacketFXBlockMist::decode);

    private final long loc;
    private final int color;

    public PacketFXBlockMist(long loc, int color) {
        this.loc = loc;
        this.color = color;
    }

    public PacketFXBlockMist(BlockPos pos, int color) {
        this(pos.asLong(), color);
    }

    private static void encode(FriendlyByteBuf buf, PacketFXBlockMist pkt) {
        buf.writeLong(pkt.loc);
        buf.writeInt(pkt.color);
    }

    private static PacketFXBlockMist decode(FriendlyByteBuf buf) {
        long loc = buf.readLong();
        int color = buf.readInt();
        return new PacketFXBlockMist(loc, color);
    }

    public static void handle(PacketFXBlockMist msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            FXDispatcher.INSTANCE.drawBlockMistParticles(BlockPos.of(msg.loc), msg.color);
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
