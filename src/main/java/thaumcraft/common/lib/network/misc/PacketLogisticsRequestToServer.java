
package thaumcraft.common.lib.network.misc;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.api.golems.GolemHelper;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;


public class PacketLogisticsRequestToServer implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketLogisticsRequestToServer> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "logistics_request_to_server"));

    public static final StreamCodec<FriendlyByteBuf, PacketLogisticsRequestToServer> STREAM_CODEC =
        StreamCodec.of(PacketLogisticsRequestToServer::encode, PacketLogisticsRequestToServer::decode);

    private final BlockPos pos;
    private final ItemStack stack;
    private final Direction side;
    private final int stacksize;

    public PacketLogisticsRequestToServer(BlockPos pos, Direction side, ItemStack stack, int size) {
        this.pos = pos;
        this.stack = stack;
        this.side = side;
        this.stacksize = size;
    }

    // Internal all-field constructor for decode
    private PacketLogisticsRequestToServer(BlockPos pos, Direction side, ItemStack stack, int stacksize, boolean internal) {
        this.pos = pos;
        this.stack = stack;
        this.side = side;
        this.stacksize = stacksize;
    }

    private static void encode(FriendlyByteBuf buf, PacketLogisticsRequestToServer pkt) {
        if (pkt.pos == null || pkt.side == null) {
            buf.writeBoolean(false);
        }
        else {
            buf.writeBoolean(true);
            buf.writeLong(pkt.pos.asLong());
            buf.writeByte(pkt.side.ordinal());
        }
        Utils.writeItemStackToBuffer(buf, pkt.stack);
        buf.writeInt(pkt.stacksize);
    }

    private static PacketLogisticsRequestToServer decode(FriendlyByteBuf buf) {
        BlockPos pos = null;
        Direction side = null;
        if (buf.readBoolean()) {
            pos = BlockPos.of(buf.readLong());
            side = Direction.values()[buf.readByte()];
        }
        ItemStack stack = Utils.readItemStackFromBuffer(buf);
        int stacksize = buf.readInt();
        return new PacketLogisticsRequestToServer(pos, side, stack, stacksize, true);
    }

    public static void handle(PacketLogisticsRequestToServer msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer serverPlayer = (ServerPlayer) ctx.player();
            var level = (ServerLevel) serverPlayer.level();
            int ui = 0;
            int remaining = msg.stacksize;
            while (remaining > 0) {
                ItemStack s = msg.stack.copy();
                s.setCount(Math.min(remaining, s.getMaxStackSize()));
                remaining -= s.getCount();
                if (msg.pos != null) {
                    GolemHelper.requestProvisioning(level, msg.pos, msg.side, s, ui);
                }
                else {
                    GolemHelper.requestProvisioning(level, serverPlayer, s, ui);
                }
                ++ui;
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
