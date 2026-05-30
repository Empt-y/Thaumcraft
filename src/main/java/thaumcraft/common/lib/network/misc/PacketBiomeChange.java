package thaumcraft.common.lib.network.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;


public class PacketBiomeChange implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketBiomeChange> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "biome_change"));

    public static final StreamCodec<FriendlyByteBuf, PacketBiomeChange> STREAM_CODEC =
        StreamCodec.of(PacketBiomeChange::encode, PacketBiomeChange::decode);

    private final int x;
    private final int z;
    private final short biome;

    public PacketBiomeChange(int x, int z, short biome) {
        this.x = x;
        this.z = z;
        this.biome = biome;
    }

    private static void encode(FriendlyByteBuf buf, PacketBiomeChange pkt) {
        buf.writeInt(pkt.x);
        buf.writeInt(pkt.z);
        buf.writeShort(pkt.biome);
    }

    private static PacketBiomeChange decode(FriendlyByteBuf buf) {
        int x = buf.readInt();
        int z = buf.readInt();
        short biome = buf.readShort();
        return new PacketBiomeChange(x, z, biome);
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(PacketBiomeChange msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            // TODO: Biome.getBiome(id) does not exist in NeoForge 26.1.2; biome lookup requires registry access
            // Utils.setBiomeAt(Minecraft.getInstance().level, new BlockPos(msg.getX(), 0, msg.z), Biome.getBiome(msg.biome));
            // stub: biome change not applied until Biome registry lookup is resolved
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
