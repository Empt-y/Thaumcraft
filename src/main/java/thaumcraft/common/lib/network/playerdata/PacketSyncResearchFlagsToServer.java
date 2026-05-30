package thaumcraft.common.lib.network.playerdata;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;


public class PacketSyncResearchFlagsToServer implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketSyncResearchFlagsToServer> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "sync_research_flags_to_server"));

    public static final StreamCodec<FriendlyByteBuf, PacketSyncResearchFlagsToServer> STREAM_CODEC =
        StreamCodec.of(PacketSyncResearchFlagsToServer::encode, PacketSyncResearchFlagsToServer::decode);

    final String key;
    final byte flags;

    public PacketSyncResearchFlagsToServer(String key, byte flags) {
        this.key = key;
        this.flags = flags;
    }

    public PacketSyncResearchFlagsToServer(Player player, String key) {
        this.key = key;
        this.flags = Utils.pack(
            ThaumcraftCapabilities.getKnowledge(player).hasResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.PAGE),
            ThaumcraftCapabilities.getKnowledge(player).hasResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.POPUP),
            ThaumcraftCapabilities.getKnowledge(player).hasResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.RESEARCH)
        );
    }

    private static void encode(FriendlyByteBuf buf, PacketSyncResearchFlagsToServer pkt) {
        buf.writeUtf(pkt.key);
        buf.writeByte(pkt.flags);
    }

    private static PacketSyncResearchFlagsToServer decode(FriendlyByteBuf buf) {
        String key = buf.readUtf();
        byte flags = buf.readByte();
        return new PacketSyncResearchFlagsToServer(key, flags);
    }

    public static void handle(PacketSyncResearchFlagsToServer msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = (Player) ctx.player();
            if (player != null) {
                boolean[] b = Utils.unpack(msg.flags);
                if (b[0]) {
                    ThaumcraftCapabilities.getKnowledge(player).setResearchFlag(msg.key, IPlayerKnowledge.EnumResearchFlag.PAGE);
                }
                else {
                    ThaumcraftCapabilities.getKnowledge(player).clearResearchFlag(msg.key, IPlayerKnowledge.EnumResearchFlag.PAGE);
                }
                if (b[1]) {
                    ThaumcraftCapabilities.getKnowledge(player).setResearchFlag(msg.key, IPlayerKnowledge.EnumResearchFlag.POPUP);
                }
                else {
                    ThaumcraftCapabilities.getKnowledge(player).clearResearchFlag(msg.key, IPlayerKnowledge.EnumResearchFlag.POPUP);
                }
                if (b[2]) {
                    ThaumcraftCapabilities.getKnowledge(player).setResearchFlag(msg.key, IPlayerKnowledge.EnumResearchFlag.RESEARCH);
                }
                else {
                    ThaumcraftCapabilities.getKnowledge(player).clearResearchFlag(msg.key, IPlayerKnowledge.EnumResearchFlag.RESEARCH);
                }
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
