package thaumcraft.common.lib.network.fx;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.events.ServerEvents;
import io.netty.buffer.ByteBuf;


public class PacketFXBoreDig implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketFXBoreDig> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "fx_bore_dig"));

    public static final StreamCodec<FriendlyByteBuf, PacketFXBoreDig> STREAM_CODEC =
        StreamCodec.of(PacketFXBoreDig::encode, PacketFXBoreDig::decode);

    private final int x;
    private final int y;
    private final int z;
    private final int bore;
    private final int delay;

    public PacketFXBoreDig(int x, int y, int z, int bore, int delay) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.bore = bore;
        this.delay = delay;
    }

    public PacketFXBoreDig(BlockPos pos, Entity bore, int delay) {
        this(pos.getX(), pos.getY(), pos.getZ(), bore.getId(), delay);
    }

    private static void encode(FriendlyByteBuf buf, PacketFXBoreDig pkt) {
        buf.writeInt(pkt.x);
        buf.writeInt(pkt.y);
        buf.writeInt(pkt.z);
        buf.writeInt(pkt.bore);
        buf.writeInt(pkt.delay);
    }

    private static PacketFXBoreDig decode(FriendlyByteBuf buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        int bore = buf.readInt();
        int delay = buf.readInt();
        return new PacketFXBoreDig(x, y, z, bore, delay);
    }

    public static void handle(PacketFXBoreDig msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            processMessage(msg);
        });
    }

    @OnlyIn(Dist.CLIENT)
    private static void processMessage(PacketFXBoreDig message) {
        try {
            var level = Minecraft.getInstance().level;
            BlockPos pos = new BlockPos(message.x, message.y, message.z);
            Entity entity = level.getEntity(message.bore);
            if (entity == null) {
                return;
            }
            BlockState ts = level.getBlockState(pos);
            if (ts.getBlock() == Blocks.AIR) {
                return;
            }
            for (int a = 0; a < message.delay; ++a) {
                ServerEvents.addRunnableClient(level, new Runnable() {
                    @Override
                    public void run() {
                        FXDispatcher.INSTANCE.boreDigFx(pos.getX(), pos.getY(), pos.getZ(), entity, ts, 0, message.delay);
                    }
                }, a);
            }
        }
        catch (Exception ex) {}
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
