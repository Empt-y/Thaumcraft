package thaumcraft.common.lib.network.playerdata;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import io.netty.buffer.ByteBuf;


public class PacketPlayerFlagToServer implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketPlayerFlagToServer> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "player_flag_to_server"));

    public static final StreamCodec<FriendlyByteBuf, PacketPlayerFlagToServer> STREAM_CODEC =
        StreamCodec.of(PacketPlayerFlagToServer::encode, PacketPlayerFlagToServer::decode);

    final byte flag;

    public PacketPlayerFlagToServer(byte flag) {
        this.flag = flag;
    }

    public PacketPlayerFlagToServer(LivingEntity player, int i) {
        this.flag = (byte) i;
    }

    private static void encode(FriendlyByteBuf buf, PacketPlayerFlagToServer pkt) {
        buf.writeByte(pkt.flag);
    }

    private static PacketPlayerFlagToServer decode(FriendlyByteBuf buf) {
        byte flag = buf.readByte();
        return new PacketPlayerFlagToServer(flag);
    }

    public static void handle(PacketPlayerFlagToServer msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = (Player) ctx.player();
            if (player != null) {
                switch (msg.flag) {
                    case 1: {
                        player.fallDistance = 0.0f;
                        break;
                    }
                }
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
