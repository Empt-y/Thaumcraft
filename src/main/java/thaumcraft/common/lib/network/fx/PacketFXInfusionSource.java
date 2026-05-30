package thaumcraft.common.lib.network.fx;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;
import thaumcraft.common.tiles.crafting.TilePedestal;
import io.netty.buffer.ByteBuf;


public class PacketFXInfusionSource implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketFXInfusionSource> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "fx_infusion_source"));

    public static final StreamCodec<FriendlyByteBuf, PacketFXInfusionSource> STREAM_CODEC =
        StreamCodec.of(PacketFXInfusionSource::encode, PacketFXInfusionSource::decode);

    private final long p1;
    private final long p2;
    private final int color;

    public PacketFXInfusionSource(long p1, long p2, int color) {
        this.p1 = p1;
        this.p2 = p2;
        this.color = color;
    }

    public PacketFXInfusionSource(BlockPos pos, BlockPos pos2, int color) {
        this(pos.asLong(), pos2.asLong(), color);
    }

    private static void encode(FriendlyByteBuf buf, PacketFXInfusionSource pkt) {
        buf.writeLong(pkt.p1);
        buf.writeLong(pkt.p2);
        buf.writeInt(pkt.color);
    }

    private static PacketFXInfusionSource decode(FriendlyByteBuf buf) {
        long p1 = buf.readLong();
        long p2 = buf.readLong();
        int color = buf.readInt();
        return new PacketFXInfusionSource(p1, p2, color);
    }

    public static void handle(PacketFXInfusionSource msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            BlockPos bp1 = BlockPos.of(msg.p1);
            BlockPos bp2 = BlockPos.of(msg.p2);
            String key = bp2.getX() + ":" + bp2.getY() + ":" + bp2.getZ() + ":" + msg.color;
            var level = Minecraft.getInstance().level;
            BlockEntity tile = getLevel().getBlockEntity(bp1);
            if (tile != null && tile instanceof TileInfusionMatrix) {
                int count = 15;
                if (getLevel().getBlockEntity(bp2) != null && getLevel().getBlockEntity(bp2) instanceof TilePedestal) {
                    count = 60;
                }
                TileInfusionMatrix is = (TileInfusionMatrix) tile;
                if (is.sourceFX.containsKey(key)) {
                    TileInfusionMatrix.SourceFX sf = is.sourceFX.get(key);
                    sf.ticks = count;
                    is.sourceFX.put(key, sf);
                }
                else {
                    is.sourceFX.put(key, is.new SourceFX(bp2, count, msg.color));
                }
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
