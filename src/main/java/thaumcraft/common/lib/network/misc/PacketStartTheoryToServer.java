
package thaumcraft.common.lib.network.misc;
import net.minecraft.server.level.ServerLevel;

import java.util.HashSet;
import java.util.Set;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.common.tiles.crafting.TileResearchTable;
import io.netty.buffer.ByteBuf;


public class PacketStartTheoryToServer implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketStartTheoryToServer> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "start_theory_to_server"));

    public static final StreamCodec<FriendlyByteBuf, PacketStartTheoryToServer> STREAM_CODEC =
        StreamCodec.of(PacketStartTheoryToServer::encode, PacketStartTheoryToServer::decode);

    private final long pos;
    private final Set<String> aids;

    public PacketStartTheoryToServer(long pos, Set<String> aids) {
        this.pos = pos;
        this.aids = aids;
    }

    public PacketStartTheoryToServer(BlockPos pos, Set<String> aids) {
        this(pos.asLong(), new HashSet<>(aids));
    }

    private static void encode(FriendlyByteBuf buf, PacketStartTheoryToServer pkt) {
        buf.writeLong(pkt.pos);
        buf.writeByte(pkt.aids.size());
        for (String aid : pkt.aids) {
            buf.writeUtf(aid);
        }
    }

    private static PacketStartTheoryToServer decode(FriendlyByteBuf buf) {
        long pos = buf.readLong();
        Set<String> aids = new HashSet<>();
        int s = buf.readByte();
        for (int a = 0; a < s; ++a) {
            aids.add(buf.readUtf());
        }
        return new PacketStartTheoryToServer(pos, aids);
    }

    public static void handle(PacketStartTheoryToServer msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer serverPlayer = (ServerPlayer) ctx.player();
            var level = (ServerLevel) serverPlayer.level();
            Player player = serverPlayer;
            BlockPos bp = BlockPos.of(msg.pos);
            if (level != null && player != null && bp != null) {
                BlockEntity te = getLevel().getBlockEntity(bp);
                if (te != null && te instanceof TileResearchTable) {
                    ((TileResearchTable) te).startNewTheory(player, msg.aids);
                }
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
