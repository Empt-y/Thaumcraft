package thaumcraft.common.lib.network.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.client.lib.events.RenderEventHandler;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.lib.SoundsTC;
import io.netty.buffer.ByteBuf;


public class PacketMiscEvent implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketMiscEvent> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "misc_event"));

    public static final StreamCodec<FriendlyByteBuf, PacketMiscEvent> STREAM_CODEC =
        StreamCodec.of(PacketMiscEvent::encode, PacketMiscEvent::decode);

    public static byte WARP_EVENT = 0;
    public static byte MIST_EVENT = 1;
    public static byte MIST_EVENT_SHORT = 2;

    private final byte type;
    private final int value;

    public PacketMiscEvent(byte type) {
        this.type = type;
        this.value = 0;
    }

    public PacketMiscEvent(byte type, int value) {
        this.type = type;
        this.value = value;
    }

    private static void encode(FriendlyByteBuf buf, PacketMiscEvent pkt) {
        buf.writeByte(pkt.type);
        if (pkt.value != 0) {
            buf.writeInt(pkt.value);
        }
    }

    private static PacketMiscEvent decode(FriendlyByteBuf buf) {
        byte type = buf.readByte();
        int value = 0;
        if (buf.isReadable()) {
            value = buf.readInt();
        }
        return new PacketMiscEvent(type, value);
    }

    public static void handle(PacketMiscEvent msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            processMessage(msg);
        });
    }

    private static void processMessage(PacketMiscEvent message) {
        Player p = Minecraft.getInstance().player;
        switch (message.type) {
            case 0: {
                if (!ModConfig.CONFIG_GRAPHICS.nostress) {
                    p.level().playSound(p, p.getX(), p.getY(), p.getZ(), SoundsTC.heartbeat, SoundSource.AMBIENT, 1.0f, 1.0f);
                    break;
                }
                break;
            }
            case 1: {
                RenderEventHandler.fogFiddled = true;
                RenderEventHandler.fogDuration = 2400;
                break;
            }
            case 2: {
                RenderEventHandler.fogFiddled = true;
                if (RenderEventHandler.fogDuration < 200) {
                    RenderEventHandler.fogDuration = 200;
                    break;
                }
                break;
            }
        }
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
