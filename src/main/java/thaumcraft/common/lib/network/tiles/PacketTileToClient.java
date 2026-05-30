package thaumcraft.common.lib.network.tiles;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.common.lib.utils.Utils;
import thaumcraft.common.tiles.TileThaumcraft;
import io.netty.buffer.ByteBuf;


public class PacketTileToClient implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketTileToClient> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "tile_to_client"));

    public static final StreamCodec<FriendlyByteBuf, PacketTileToClient> STREAM_CODEC =
        StreamCodec.of(PacketTileToClient::encode, PacketTileToClient::decode);

    private final long pos;
    private final CompoundTag nbt;

    public PacketTileToClient(long pos, CompoundTag nbt) {
        this.pos = pos;
        this.nbt = nbt;
    }

    public PacketTileToClient(BlockPos pos, CompoundTag nbt) {
        this(pos.asLong(), nbt);
    }

    private static void encode(FriendlyByteBuf buf, PacketTileToClient pkt) {
        buf.writeLong(pkt.pos);
        Utils.writeCompoundTagToBuffer(buf, pkt.nbt);
    }

    private static PacketTileToClient decode(FriendlyByteBuf buf) {
        long pos = buf.readLong();
        CompoundTag nbt = Utils.readCompoundTagFromBuffer(buf);
        return new PacketTileToClient(pos, nbt);
    }

    public static void handle(PacketTileToClient msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var level = Minecraft.getInstance().level;
            BlockPos bp = BlockPos.of(msg.pos);
            if (level != null && bp != null) {
                BlockEntity te = level.getBlockEntity(bp);
                if (te != null && te instanceof TileThaumcraft) {
                    ((TileThaumcraft) te).messageFromServer((msg.nbt == null) ? new CompoundTag() : msg.nbt);
                }
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
