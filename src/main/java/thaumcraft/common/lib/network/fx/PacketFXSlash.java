package thaumcraft.common.lib.network.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.client.fx.FXDispatcher;
import io.netty.buffer.ByteBuf;


public class PacketFXSlash implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketFXSlash> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "fx_slash"));

    public static final StreamCodec<FriendlyByteBuf, PacketFXSlash> STREAM_CODEC =
        StreamCodec.of(PacketFXSlash::encode, PacketFXSlash::decode);

    private final int source;
    private final int target;

    public PacketFXSlash(int source, int target) {
        this.source = source;
        this.target = target;
    }

    private static void encode(FriendlyByteBuf buf, PacketFXSlash pkt) {
        buf.writeInt(pkt.source);
        buf.writeInt(pkt.target);
    }

    private static PacketFXSlash decode(FriendlyByteBuf buf) {
        int source = buf.readInt();
        int target = buf.readInt();
        return new PacketFXSlash(source, target);
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(PacketFXSlash msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            var level = mc.level;
            Entity var2 = getEntityByID(msg.source, mc, level);
            Entity var3 = getEntityByID(msg.target, mc, level);
            if (var2 != null && var3 != null) {
                FXDispatcher.INSTANCE.drawSlash(var2.getX(), var2.getBoundingBox().minY + var2.getBbHeight() / 2.0f, var2.getZ(), var3.getX(), var3.getBoundingBox().minY + var3.getBbHeight() / 2.0f, var3.getZ(), 8);
            }
        });
    }

    @OnlyIn(Dist.CLIENT)
    private static Entity getEntityByID(int par1, Minecraft mc, net.minecraft.client.multiplayer.ClientLevel level) {
        return (par1 == mc.player.getId()) ? mc.player : level.getEntity(par1);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
