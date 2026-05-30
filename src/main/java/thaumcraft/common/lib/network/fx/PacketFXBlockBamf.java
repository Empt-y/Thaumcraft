package thaumcraft.common.lib.network.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;


public class PacketFXBlockBamf implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketFXBlockBamf> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "fx_block_bamf"));

    public static final StreamCodec<FriendlyByteBuf, PacketFXBlockBamf> STREAM_CODEC =
        StreamCodec.of(PacketFXBlockBamf::encode, PacketFXBlockBamf::decode);

    private final double x;
    private final double y;
    private final double z;
    private final int color;
    private final byte flags;
    private final byte face;

    public PacketFXBlockBamf(double x, double y, double z, int color, byte flags, byte face) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        this.flags = flags;
        this.face = face;
    }

    public PacketFXBlockBamf(double x, double y, double z, int color, boolean sound, boolean flair, Direction side) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.color = color;
        int f = 0;
        if (sound) {
            f = Utils.setBit(f, 0);
        }
        if (flair) {
            f = Utils.setBit(f, 1);
        }
        this.face = (side != null) ? (byte) side.ordinal() : -1;
        this.flags = (byte) f;
    }

    public PacketFXBlockBamf(BlockPos pos, int color, boolean sound, boolean flair, Direction side) {
        this(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, color, sound, flair, side);
    }

    private static void encode(FriendlyByteBuf buf, PacketFXBlockBamf pkt) {
        buf.writeDouble(pkt.x);
        buf.writeDouble(pkt.y);
        buf.writeDouble(pkt.z);
        buf.writeInt(pkt.color);
        buf.writeByte(pkt.flags);
        buf.writeByte(pkt.face);
    }

    private static PacketFXBlockBamf decode(FriendlyByteBuf buf) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        int color = buf.readInt();
        byte flags = buf.readByte();
        byte face = buf.readByte();
        return new PacketFXBlockBamf(x, y, z, color, flags, face);
    }

    public static void handle(PacketFXBlockBamf msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            processMessage(msg);
        });
    }

    @OnlyIn(Dist.CLIENT)
    private static void processMessage(PacketFXBlockBamf message) {
        Direction side = null;
        if (message.face >= 0) {
            side = Direction.from3DDataValue(message.face);
        }
        if (message.color != -9999) {
            FXDispatcher.INSTANCE.drawBamf(message.x, message.y, message.z, message.color, Utils.getBit(message.flags, 0), Utils.getBit(message.flags, 1), side);
        }
        else {
            FXDispatcher.INSTANCE.drawBamf(message.x, message.y, message.z, Utils.getBit(message.flags, 0), Utils.getBit(message.flags, 1), side);
        }
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
