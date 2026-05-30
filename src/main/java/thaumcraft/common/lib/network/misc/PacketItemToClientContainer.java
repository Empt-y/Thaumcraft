package thaumcraft.common.lib.network.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;


public class PacketItemToClientContainer implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketItemToClientContainer> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "item_to_client_container"));

    public static final StreamCodec<FriendlyByteBuf, PacketItemToClientContainer> STREAM_CODEC =
        StreamCodec.of(PacketItemToClientContainer::encode, PacketItemToClientContainer::decode);

    private final int windowId;
    private final int slot;
    private final ItemStack item;

    public PacketItemToClientContainer(int windowId, int slot, ItemStack item) {
        this.windowId = windowId;
        this.slot = slot;
        this.item = item;
    }

    private static void encode(FriendlyByteBuf buf, PacketItemToClientContainer pkt) {
        buf.writeInt(pkt.windowId);
        buf.writeInt(pkt.slot);
        Utils.writeItemStackToBuffer(buf, pkt.item);
    }

    private static PacketItemToClientContainer decode(FriendlyByteBuf buf) {
        int windowId = buf.readInt();
        int slot = buf.readInt();
        ItemStack item = Utils.readItemStackFromBuffer(buf);
        return new PacketItemToClientContainer(windowId, slot, item);
    }

    public static void handle(PacketItemToClientContainer msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            try {
                if (Minecraft.getInstance().player.containerMenu != null && Minecraft.getInstance().player.containerMenu.containerId == msg.windowId) {
                    Minecraft.getInstance().player.containerMenu.setItem(msg.slot, 0, msg.item);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
