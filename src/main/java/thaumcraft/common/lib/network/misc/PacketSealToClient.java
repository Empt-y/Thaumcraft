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
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.golems.seals.ISealConfigArea;
import thaumcraft.api.golems.seals.ISealConfigFilter;
import thaumcraft.api.golems.seals.ISealConfigToggles;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.api.golems.seals.SealPos;
import thaumcraft.common.golems.seals.SealEntity;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;


public class PacketSealToClient implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketSealToClient> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "seal_to_client"));

    public static final StreamCodec<FriendlyByteBuf, PacketSealToClient> STREAM_CODEC =
        StreamCodec.of(PacketSealToClient::encode, PacketSealToClient::decode);

    final BlockPos pos;
    final Direction face;
    final String type;
    final long area;
    final boolean[] props;
    final boolean blacklist;
    final byte filtersize;
    final NonNullList<ItemStack> filter;
    final NonNullList<Integer> filterStackSize;
    final byte priority;
    final byte color;
    final boolean locked;
    final boolean redstone;
    final String owner;

    // Full-field constructor for decode
    public PacketSealToClient(BlockPos pos, Direction face, String type, long area, boolean[] props,
                               boolean blacklist, byte filtersize, NonNullList<ItemStack> filter,
                               NonNullList<Integer> filterStackSize, byte priority, byte color,
                               boolean locked, boolean redstone, String owner) {
        this.pos = pos;
        this.face = face;
        this.type = type;
        this.area = area;
        this.props = props;
        this.blacklist = blacklist;
        this.filtersize = filtersize;
        this.filter = filter;
        this.filterStackSize = filterStackSize;
        this.priority = priority;
        this.color = color;
        this.locked = locked;
        this.redstone = redstone;
        this.owner = owner;
    }

    public PacketSealToClient(ISealEntity se) {
        this.pos = se.getSealPos().pos;
        this.face = se.getSealPos().face;
        this.type = (se.getSeal() == null) ? "REMOVE" : se.getSeal().getKey();
        long areaVal = 0L;
        if (se.getSeal() != null && se.getSeal() instanceof ISealConfigArea) {
            areaVal = se.getArea().asLong();
        }
        this.area = areaVal;
        boolean[] propsVal = null;
        if (se.getSeal() != null && se.getSeal() instanceof ISealConfigToggles) {
            ISealConfigToggles cp = (ISealConfigToggles) se.getSeal();
            propsVal = new boolean[cp.getToggles().length];
            for (int a = 0; a < cp.getToggles().length; ++a) {
                propsVal[a] = cp.getToggles()[a].getValue();
            }
        }
        this.props = propsVal;
        boolean blacklistVal = false;
        byte filtersizeVal = 0;
        NonNullList<ItemStack> filterVal = NonNullList.create();
        NonNullList<Integer> filterStackSizeVal = NonNullList.create();
        if (se.getSeal() != null && se.getSeal() instanceof ISealConfigFilter) {
            ISealConfigFilter cp2 = (ISealConfigFilter) se.getSeal();
            blacklistVal = cp2.isBlacklist();
            filtersizeVal = (byte) cp2.getFilterSize();
            filterVal = cp2.getInv();
            filterStackSizeVal = cp2.getSizes();
        }
        this.blacklist = blacklistVal;
        this.filtersize = filtersizeVal;
        this.filter = filterVal;
        this.filterStackSize = filterStackSizeVal;
        this.priority = se.getPriority();
        this.color = se.getColor();
        this.locked = se.isLocked();
        this.redstone = se.isRedstoneSensitive();
        this.owner = se.getOwner();
    }

    private static void encode(FriendlyByteBuf buf, PacketSealToClient pkt) {
        buf.writeLong(pkt.pos.asLong());
        buf.writeByte(pkt.face.ordinal());
        buf.writeByte(pkt.priority);
        buf.writeByte(pkt.color);
        buf.writeBoolean(pkt.locked);
        buf.writeBoolean(pkt.redstone);
        buf.writeUtf(pkt.owner);
        buf.writeUtf(pkt.type);
        buf.writeBoolean(pkt.blacklist);
        buf.writeByte(pkt.filtersize);
        for (int a = 0; a < pkt.filtersize; ++a) {
            Utils.writeItemStackToBuffer(buf, pkt.filter.get(a));
            buf.writeShort(pkt.filterStackSize.get(a));
        }
        if (pkt.area != 0L) {
            buf.writeLong(pkt.area);
        }
        if (pkt.props != null) {
            for (boolean b : pkt.props) {
                buf.writeBoolean(b);
            }
        }
    }

    private static PacketSealToClient decode(FriendlyByteBuf buf) {
        BlockPos pos = BlockPos.of(buf.readLong());
        Direction face = Direction.values()[buf.readByte()];
        byte priority = buf.readByte();
        byte color = buf.readByte();
        boolean locked = buf.readBoolean();
        boolean redstone = buf.readBoolean();
        String owner = buf.readUtf();
        String type = buf.readUtf();
        boolean blacklist = buf.readBoolean();
        byte filtersize = buf.readByte();
        NonNullList<ItemStack> filter = NonNullList.withSize(filtersize, ItemStack.EMPTY);
        NonNullList<Integer> filterStackSize = NonNullList.withSize(filtersize, 0);
        for (int a = 0; a < filtersize; ++a) {
            filter.set(a, Utils.readItemStackFromBuffer(buf));
            filterStackSize.set(a, (int) buf.readShort());
        }
        long area = 0L;
        boolean[] props = null;
        if (!type.equals("REMOVE") && SealHandler.getSeal(type) != null) {
            if (SealHandler.getSeal(type) instanceof ISealConfigArea) {
                try {
                    area = buf.readLong();
                } catch (Exception ex) {}
            }
            if (SealHandler.getSeal(type) instanceof ISealConfigToggles) {
                try {
                    ISealConfigToggles cp = (ISealConfigToggles) SealHandler.getSeal(type);
                    props = new boolean[cp.getToggles().length];
                    for (int a2 = 0; a2 < cp.getToggles().length; ++a2) {
                        props[a2] = buf.readBoolean();
                    }
                } catch (Exception ex2) {}
            }
        }
        return new PacketSealToClient(pos, face, type, area, props, blacklist, filtersize,
                filter, filterStackSize, priority, color, locked, redstone, owner);
    }

    public static void handle(PacketSealToClient msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var level = Minecraft.getInstance().level;
            if (msg.type.equals("REMOVE")) {
                SealHandler.removeSealEntity(level, new SealPos(msg.pos, msg.face), true);
            }
            else {
                try {
                    SealEntity seal = new SealEntity(level, new SealPos(msg.pos, msg.face), SealHandler.getSeal(msg.type).getClass().newInstance());
                    if (msg.area != 0L) {
                        seal.setArea(BlockPos.of(msg.area));
                    }
                    if (msg.props != null && seal.getSeal() instanceof ISealConfigToggles) {
                        ISealConfigToggles cp = (ISealConfigToggles) seal.getSeal();
                        for (int a = 0; a < msg.props.length; ++a) {
                            cp.setToggle(a, msg.props[a]);
                        }
                    }
                    if (seal.getSeal() instanceof ISealConfigFilter) {
                        ISealConfigFilter cp2 = (ISealConfigFilter) seal.getSeal();
                        cp2.setBlacklist(msg.blacklist);
                        for (int a = 0; a < msg.filtersize; ++a) {
                            cp2.setFilterSlot(a, msg.filter.get(a));
                            cp2.setFilterSlotSize(a, msg.filterStackSize.get(a));
                        }
                    }
                    seal.setPriority(msg.priority);
                    seal.setColor(msg.color);
                    seal.setLocked(msg.locked);
                    seal.setRedstoneSensitive(msg.redstone);
                    seal.setOwner(msg.owner);
                    SealHandler.addSealEntity(level, seal);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
