package thaumcraft.common.lib.network.playerdata;

import java.util.HashMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.tiles.crafting.FocusElementNode;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;
import io.netty.buffer.ByteBuf;


public class PacketFocusNodesToServer implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketFocusNodesToServer> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "focus_nodes_to_server"));

    public static final StreamCodec<FriendlyByteBuf, PacketFocusNodesToServer> STREAM_CODEC =
        StreamCodec.of(PacketFocusNodesToServer::encode, PacketFocusNodesToServer::decode);

    private final long loc;
    private final HashMap<Integer, FocusElementNode> data;
    private final String name;

    public PacketFocusNodesToServer(long loc, HashMap<Integer, FocusElementNode> data, String name) {
        this.loc = loc;
        this.data = data;
        this.name = name;
    }

    public PacketFocusNodesToServer(BlockPos pos, HashMap<Integer, FocusElementNode> data, String name) {
        this(pos.asLong(), data, name);
    }

    private static void encode(FriendlyByteBuf buf, PacketFocusNodesToServer pkt) {
        buf.writeLong(pkt.loc);
        buf.writeByte(pkt.data.size());
        for (FocusElementNode node : pkt.data.values()) {
            Utils.writeCompoundTagToBuffer(buf, node.serialize());
        }
        buf.writeUtf(pkt.name);
    }

    private static PacketFocusNodesToServer decode(FriendlyByteBuf buf) {
        long loc = buf.readLong();
        HashMap<Integer, FocusElementNode> data = new HashMap<>();
        int m = buf.readByte();
        for (int a = 0; a < m; ++a) {
            FocusElementNode node = new FocusElementNode();
            node.deserialize(Utils.readCompoundTagFromBuffer(buf));
            data.put(node.id, node);
        }
        String name = buf.readUtf();
        return new PacketFocusNodesToServer(loc, data, name);
    }

    public static void handle(PacketFocusNodesToServer msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();
            if (player == null) {
                return;
            }
            BlockPos pos = BlockPos.of(msg.loc);
            BlockEntity rt = player.level().getBlockEntity(pos);
            if (rt != null && rt instanceof TileFocalManipulator) {
                ((TileFocalManipulator) rt).data.clear();
                ((TileFocalManipulator) rt).data = msg.data;
                ((TileFocalManipulator) rt).focusName = msg.name;
                rt.setChanged();
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
