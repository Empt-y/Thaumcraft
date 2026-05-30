package thaumcraft.common.lib.network.fx;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.monster.EntityWisp;
import io.netty.buffer.ByteBuf;


public class PacketFXWispZap implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketFXWispZap> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "fx_wisp_zap"));

    public static final StreamCodec<FriendlyByteBuf, PacketFXWispZap> STREAM_CODEC =
        StreamCodec.of(PacketFXWispZap::encode, PacketFXWispZap::decode);

    private final int source;
    private final int target;

    public PacketFXWispZap(int source, int target) {
        this.source = source;
        this.target = target;
    }

    private static void encode(FriendlyByteBuf buf, PacketFXWispZap pkt) {
        buf.writeInt(pkt.source);
        buf.writeInt(pkt.target);
    }

    private static PacketFXWispZap decode(FriendlyByteBuf buf) {
        int source = buf.readInt();
        int target = buf.readInt();
        return new PacketFXWispZap(source, target);
    }

    public static void handle(PacketFXWispZap msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            var level = mc.level;
            Entity var2 = getEntityByID(msg.source, mc, level);
            Entity var3 = getEntityByID(msg.target, mc, level);
            if (var2 != null && var3 != null) {
                float r = 1.0f;
                float g = 1.0f;
                float b = 1.0f;
                if (var2 instanceof EntityWisp) {
                    Color c = new Color(Aspect.getAspect(((EntityWisp) var2).getWispType()).getColor());
                    r = c.getRed() / 255.0f;
                    g = c.getGreen() / 255.0f;
                    b = c.getBlue() / 255.0f;
                }
                FXDispatcher.INSTANCE.arcBolt(var2.getX(), var2.getY(), var2.getZ(), var3.getX(), var3.getY(), var3.getZ(), r, g, b, 0.6f);
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
