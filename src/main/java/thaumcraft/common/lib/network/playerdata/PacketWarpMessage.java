package thaumcraft.common.lib.network.playerdata;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.lib.SoundsTC;
import io.netty.buffer.ByteBuf;


public class PacketWarpMessage implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketWarpMessage> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "warp_message"));

    public static final StreamCodec<FriendlyByteBuf, PacketWarpMessage> STREAM_CODEC =
        StreamCodec.of(PacketWarpMessage::encode, PacketWarpMessage::decode);

    protected final int data;
    protected final byte type;

    public PacketWarpMessage(int data, byte type) {
        this.data = data;
        this.type = type;
    }

    public PacketWarpMessage(Player player, byte type, int change) {
        this.data = change;
        this.type = type;
    }

    private static void encode(FriendlyByteBuf buf, PacketWarpMessage pkt) {
        buf.writeInt(pkt.data);
        buf.writeByte(pkt.type);
    }

    private static PacketWarpMessage decode(FriendlyByteBuf buf) {
        int data = buf.readInt();
        byte type = buf.readByte();
        return new PacketWarpMessage(data, type);
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(PacketWarpMessage msg, IPayloadContext ctx) {
        if (msg.data != 0) {
            ctx.enqueueWork(() -> {
                processMessage(msg);
            });
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static void processMessage(PacketWarpMessage message) {
        Player player = Minecraft.getInstance().player;
        if (message.type == 0 && message.data > 0) {
            // tc.addwarp / tc.removewarp text — use translatable component
            String key = (message.data < 0) ? "tc.removewarp" : "tc.addwarp";
            if (message.data > 0) {
                player.playSound(SoundsTC.whispers, 0.5f, 1.0f);
            }
        }
        else if (message.type == 1) {
            String key = (message.data < 0) ? "tc.removewarpsticky" : "tc.addwarpsticky";
            if (message.data > 0) {
                player.playSound(SoundsTC.whispers, 0.5f, 1.0f);
            }
            player.sendOverlayMessage(Component.translatable(key));
        }
        else if (message.data > 0) {
            String key = (message.data < 0) ? "tc.removewarptemp" : "tc.addwarptemp";
            player.sendOverlayMessage(Component.translatable(key));
        }
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
