package thaumcraft.common.lib.network.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;


public class PacketSealFilterToClient implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketSealFilterToClient> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "seal_filter_to_client"));

    public static final StreamCodec<FriendlyByteBuf, PacketSealFilterToClient> STREAM_CODEC =
        StreamCodec.of(PacketSealFilterToClient::encode, PacketSealFilterToClient::decode);

    final BlockPos pos;
    final Direction face;
    final byte filtersize;
    final NonNullList<ItemStack> filter;
    final NonNullList<Integer> filterStackSize;

    public PacketSealFilterToClient(BlockPos pos, Direction face, byte filtersize, NonNullList<ItemStack> filter, NonNullList<Integer> filterStackSize) {
        this.pos = pos;
        this.face = face;
        this.filtersize = filtersize;
        this.filter = filter;
        this.filterStackSize = filterStackSize;
    }

    public PacketSealFilterToClient(ISealEntity se) {
        this.pos = se.getSealPos().pos;
        this.face = se.getSealPos().face;
        if (se.getSeal() != null && se.getSeal() instanceof ISealConfigFilter) {
            ISealConfigFilter cp = (ISealConfigFilter) se.getSeal();
            this.filtersize = (byte) cp.getFilterSize();
            this.filter = cp.getInv();
            this.filterStackSize = cp.getSizes();
        }
        else {
            this.filtersize = 0;
            this.filter = NonNullList.create();
            this.filterStackSize = NonNullList.create();
        }
    }

    private static void encode(FriendlyByteBuf buf, PacketSealFilterToClient pkt) {
        buf.writeLong(pkt.pos.asLong());
        buf.writeByte(pkt.face.ordinal());
        buf.writeByte(pkt.filtersize);
        for (int a = 0; a < pkt.filtersize; ++a) {
            Utils.writeItemStackToBuffer(buf, pkt.filter.get(a));
            buf.writeShort(pkt.filterStackSize.get(a));
        }
    }

    private static PacketSealFilterToClient decode(FriendlyByteBuf buf) {
        BlockPos pos = BlockPos.of(buf.readLong());
        Direction face = Direction.values()[buf.readByte()];
        byte filtersize = buf.readByte();
        NonNullList<ItemStack> filter = NonNullList.withSize(filtersize, ItemStack.EMPTY);
        NonNullList<Integer> filterStackSize = NonNullList.withSize(filtersize, 0);
        for (int a = 0; a < filtersize; ++a) {
            filter.set(a, Utils.readItemStackFromBuffer(buf));
            filterStackSize.set(a, (int) buf.readShort());
        }
        return new PacketSealFilterToClient(pos, face, filtersize, filter, filterStackSize);
    }

    public static void handle(PacketSealFilterToClient msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            try {
                // TODO: SealHandler.getSealEntity may need dimension ID; client-side dimension lookup
                // using Minecraft.getInstance().level as fallback
                var level = Minecraft.getInstance().level;
                ISealEntity seal = SealHandler.getSealEntity(world.dimension().identifier().hashCode(), new SealPos(msg.pos, msg.face));
                if (seal != null && seal.getSeal() instanceof ISealConfigFilter) {
                    ISealConfigFilter cp = (ISealConfigFilter) seal.getSeal();
                    for (int a = 0; a < msg.filtersize; ++a) {
                        cp.setFilterSlot(a, msg.filter.get(a));
                        cp.setFilterSlotSize(a, msg.filterStackSize.get(a));
                    }
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
