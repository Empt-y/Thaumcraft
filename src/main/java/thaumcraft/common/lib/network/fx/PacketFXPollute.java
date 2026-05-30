package thaumcraft.common.lib.network.fx;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.client.fx.FXDispatcher;
import io.netty.buffer.ByteBuf;


public class PacketFXPollute implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketFXPollute> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "fx_pollute"));

    public static final StreamCodec<FriendlyByteBuf, PacketFXPollute> STREAM_CODEC =
        StreamCodec.of(PacketFXPollute::encode, PacketFXPollute::decode);

    private final int x;
    private final int y;
    private final int z;
    private final byte amount;

    public PacketFXPollute(int x, int y, int z, byte amount) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.amount = amount;
    }

    public PacketFXPollute(BlockPos pos, float amt) {
        this.getX() = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        if (amt < 1.0f && amt > 0.0f) {
            amt = 1.0f;
        }
        this.amount = (byte) amt;
    }

    public PacketFXPollute(BlockPos pos, float amt, boolean vary) {
        this(pos, amt);
    }

    private static void encode(FriendlyByteBuf buf, PacketFXPollute pkt) {
        buf.writeInt(pkt.x);
        buf.writeInt(pkt.y);
        buf.writeInt(pkt.z);
        buf.writeByte(pkt.amount);
    }

    private static PacketFXPollute decode(FriendlyByteBuf buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        byte amount = buf.readByte();
        return new PacketFXPollute(x, y, z, amount);
    }

    public static void handle(PacketFXPollute msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            for (int a = 0; a < Math.min(40, msg.amount); ++a) {
                FXDispatcher.INSTANCE.drawPollutionParticles(new BlockPos(msg.getX(), msg.y, msg.z));
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
