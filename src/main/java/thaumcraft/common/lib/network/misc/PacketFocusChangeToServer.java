
package thaumcraft.common.lib.network.misc;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.api.casters.ICaster;
import thaumcraft.common.items.casters.CasterManager;
import io.netty.buffer.ByteBuf;


public class PacketFocusChangeToServer implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketFocusChangeToServer> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "focus_change_to_server"));

    public static final StreamCodec<FriendlyByteBuf, PacketFocusChangeToServer> STREAM_CODEC =
        StreamCodec.of(PacketFocusChangeToServer::encode, PacketFocusChangeToServer::decode);

    private final String focus;

    public PacketFocusChangeToServer(String focus) {
        this.focus = focus;
    }

    private static void encode(FriendlyByteBuf buf, PacketFocusChangeToServer pkt) {
        buf.writeUtf(pkt.focus);
    }

    private static PacketFocusChangeToServer decode(FriendlyByteBuf buf) {
        String focus = buf.readUtf();
        return new PacketFocusChangeToServer(focus);
    }

    public static void handle(PacketFocusChangeToServer msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = (Player) ctx.player();
            var level = (ServerLevel) ((ServerPlayer) ctx.player()).level();
            if (level == null) {
                return;
            }
            if (player != null && player.getMainHandItem().getItem() instanceof ICaster) {
                CasterManager.changeFocus(player.getMainHandItem(), level, player, msg.focus);
            }
            else if (player != null && player.getOffhandItem().getItem() instanceof ICaster) {
                CasterManager.changeFocus(player.getOffhandItem(), level, player, msg.focus);
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
