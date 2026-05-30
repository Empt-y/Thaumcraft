
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
import thaumcraft.common.items.tools.ItemElementalShovel;
import io.netty.buffer.ByteBuf;


public class PacketItemKeyToServer implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketItemKeyToServer> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "item_key_to_server"));

    public static final StreamCodec<FriendlyByteBuf, PacketItemKeyToServer> STREAM_CODEC =
        StreamCodec.of(PacketItemKeyToServer::encode, PacketItemKeyToServer::decode);

    private final byte key;
    private final byte mod;

    public PacketItemKeyToServer(byte key, byte mod) {
        this.key = key;
        this.mod = mod;
    }

    public PacketItemKeyToServer(int key) {
        this.key = (byte) key;
        this.mod = 0;
    }

    public PacketItemKeyToServer(int key, int mod) {
        this.key = (byte) key;
        this.mod = (byte) mod;
    }

    private static void encode(FriendlyByteBuf buf, PacketItemKeyToServer pkt) {
        buf.writeByte(pkt.key);
        buf.writeByte(pkt.mod);
    }

    private static PacketItemKeyToServer decode(FriendlyByteBuf buf) {
        byte key = buf.readByte();
        byte mod = buf.readByte();
        return new PacketItemKeyToServer(key, mod);
    }

    public static void handle(PacketItemKeyToServer msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = (Player) ctx.player();
            var level = (ServerLevel) ((ServerPlayer) ctx.player()).level();
            if (level == null) {
                return;
            }
            if (player != null) {
                boolean flag = false;
                if (player.getMainHandItem() != null) {
                    if (msg.key == 1 && player.getMainHandItem().getItem() instanceof ICaster) {
                        CasterManager.toggleMisc(player.getMainHandItem(), level, player, msg.mod);
                        flag = true;
                    }
                    if (!flag && msg.key == 1 && player.getOffhandItem().getItem() instanceof ICaster) {
                        CasterManager.toggleMisc(player.getOffhandItem(), level, player, msg.mod);
                    }
                    if (msg.key == 1 && player.getMainHandItem().getItem() instanceof ItemElementalShovel) {
                        byte b = ItemElementalShovel.getOrientation(player.getMainHandItem());
                        ItemElementalShovel.setOrientation(player.getMainHandItem(), (byte)(b + 1));
                        flag = true;
                    }
                }
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
