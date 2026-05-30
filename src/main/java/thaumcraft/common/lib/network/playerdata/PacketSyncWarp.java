package thaumcraft.common.lib.network.playerdata;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;


public class PacketSyncWarp implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketSyncWarp> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "sync_warp"));

    public static final StreamCodec<FriendlyByteBuf, PacketSyncWarp> STREAM_CODEC =
        StreamCodec.of(PacketSyncWarp::encode, PacketSyncWarp::decode);

    protected final CompoundTag data;

    public PacketSyncWarp(CompoundTag data) {
        this.data = data;
    }

    public PacketSyncWarp(Player player) {
        IPlayerWarp pk = ThaumcraftCapabilities.getWarp(player);
        this.data = pk.serializeNBT();
    }

    private static void encode(FriendlyByteBuf buf, PacketSyncWarp pkt) {
        Utils.writeCompoundTagToBuffer(buf, pkt.data);
    }

    private static PacketSyncWarp decode(FriendlyByteBuf buf) {
        CompoundTag data = Utils.readCompoundTagFromBuffer(buf);
        return new PacketSyncWarp(data);
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(PacketSyncWarp msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            IPlayerWarp pk = ThaumcraftCapabilities.getWarp(player);
            pk.deserializeNBT(msg.data);
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
