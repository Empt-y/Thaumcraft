package thaumcraft.common.lib.network.playerdata;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;
import io.netty.buffer.ByteBuf;


public class PacketFocusNameToServer implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketFocusNameToServer> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "focus_name_to_server"));

    public static final StreamCodec<FriendlyByteBuf, PacketFocusNameToServer> STREAM_CODEC =
        StreamCodec.of(PacketFocusNameToServer::encode, PacketFocusNameToServer::decode);

    private final long loc;
    private final String name;

    public PacketFocusNameToServer(long loc, String name) {
        this.loc = loc;
        this.name = name;
    }

    public PacketFocusNameToServer(BlockPos pos, String name) {
        this(pos.asLong(), name);
    }

    private static void encode(FriendlyByteBuf buf, PacketFocusNameToServer pkt) {
        buf.writeLong(pkt.loc);
        buf.writeUtf(pkt.name);
    }

    private static PacketFocusNameToServer decode(FriendlyByteBuf buf) {
        long loc = buf.readLong();
        String name = buf.readUtf();
        return new PacketFocusNameToServer(loc, name);
    }

    public static void handle(PacketFocusNameToServer msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = (ServerPlayer) ctx.player();
            if (player == null) {
                return;
            }
            BlockPos pos = BlockPos.of(msg.loc);
            BlockEntity rt = player.level().getBlockEntity(pos);
            if (rt != null && rt instanceof TileFocalManipulator) {
                ((TileFocalManipulator) rt).focusName = msg.name;
                rt.setChanged();
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
