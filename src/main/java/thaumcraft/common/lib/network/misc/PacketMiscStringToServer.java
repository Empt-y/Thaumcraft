package thaumcraft.common.lib.network.misc;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.common.container.ContainerLogistics;
import io.netty.buffer.ByteBuf;


public class PacketMiscStringToServer implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketMiscStringToServer> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "misc_string_to_server"));

    public static final StreamCodec<FriendlyByteBuf, PacketMiscStringToServer> STREAM_CODEC =
        StreamCodec.of(PacketMiscStringToServer::encode, PacketMiscStringToServer::decode);

    private final int id;
    private final String text;

    public PacketMiscStringToServer(int id, String text) {
        this.id = id;
        this.text = text;
    }

    private static void encode(FriendlyByteBuf buf, PacketMiscStringToServer pkt) {
        buf.writeInt(pkt.id);
        buf.writeUtf(pkt.text);
    }

    private static PacketMiscStringToServer decode(FriendlyByteBuf buf) {
        int id = buf.readInt();
        String text = buf.readUtf();
        return new PacketMiscStringToServer(id, text);
    }

    public static void handle(PacketMiscStringToServer msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();
            if (msg.id == 0 && player.containerMenu instanceof ContainerLogistics) {
                ContainerLogistics container = (ContainerLogistics) player.containerMenu;
                container.searchText = msg.text;
                container.refreshItemList(true);
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
