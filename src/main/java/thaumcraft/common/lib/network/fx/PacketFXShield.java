package thaumcraft.common.lib.network.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.util.Mth;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.client.fx.other.FXShieldRunes;
import io.netty.buffer.ByteBuf;


public class PacketFXShield implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketFXShield> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "fx_shield"));

    public static final StreamCodec<FriendlyByteBuf, PacketFXShield> STREAM_CODEC =
        StreamCodec.of(PacketFXShield::encode, PacketFXShield::decode);

    private final int source;
    private final int target;

    public PacketFXShield(int source, int target) {
        this.source = source;
        this.target = target;
    }

    private static void encode(FriendlyByteBuf buf, PacketFXShield pkt) {
        buf.writeInt(pkt.source);
        buf.writeInt(pkt.target);
    }

    private static PacketFXShield decode(FriendlyByteBuf buf) {
        int source = buf.readInt();
        int target = buf.readInt();
        return new PacketFXShield(source, target);
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(PacketFXShield msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var level = Minecraft.getInstance().level;
            Entity p = level().getEntity(msg.source);
            if (p == null) {
                return;
            }
            float pitch = 0.0f;
            float yaw = 0.0f;
            if (msg.target >= 0) {
                Entity t = level().getEntity(msg.target);
                if (t != null) {
                    double d0 = p.getX() - t.getX();
                    double d2 = (p.getBoundingBox().minY + p.getBoundingBox().maxY) / 2.0 - (t.getBoundingBox().minY + t.getBoundingBox().maxY) / 2.0;
                    double d3 = p.getZ() - t.getZ();
                    double d4 = Mth.sqrt((float)(d0 * d0 + d3 * d3));
                    float f = (float)(Math.atan2(d3, d0) * 180.0 / 3.141592653589793) - 90.0f;
                    float f2 = pitch = (float)(-(Math.atan2(d2, d4) * 180.0 / 3.141592653589793));
                    yaw = f;
                }
                else {
                    pitch = 90.0f;
                    yaw = 0.0f;
                }
                FXShieldRunes fb = new FXShieldRunes(level, p.getX(), p.getY(), p.getZ(), p, 8, yaw, pitch);
                Minecraft.getInstance().particleEngine.add(fb);
            }
            else if (msg.target == -1) {
                FXShieldRunes fb2 = new FXShieldRunes(level, p.getX(), p.getY(), p.getZ(), p, 8, 0.0f, 90.0f);
                Minecraft.getInstance().particleEngine.add(fb2);
                fb2 = new FXShieldRunes(level, p.getX(), p.getY(), p.getZ(), p, 8, 0.0f, 270.0f);
                Minecraft.getInstance().particleEngine.add(fb2);
            }
            else if (msg.target == -2) {
                FXShieldRunes fb2 = new FXShieldRunes(level, p.getX(), p.getY(), p.getZ(), p, 8, 0.0f, 270.0f);
                Minecraft.getInstance().particleEngine.add(fb2);
            }
            else if (msg.target == -3) {
                FXShieldRunes fb2 = new FXShieldRunes(level, p.getX(), p.getY(), p.getZ(), p, 8, 0.0f, 90.0f);
                Minecraft.getInstance().particleEngine.add(fb2);
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
