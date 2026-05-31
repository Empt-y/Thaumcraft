package thaumcraft.common.lib.network.fx;

import java.awt.Color;
import java.util.ArrayList;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.client.fx.ParticleEngine;
import thaumcraft.client.fx.particles.FXGeneric;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;


public class PacketFXScanSource implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketFXScanSource> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "fx_scan_source"));

    public static final StreamCodec<FriendlyByteBuf, PacketFXScanSource> STREAM_CODEC =
        StreamCodec.of(PacketFXScanSource::encode, PacketFXScanSource::decode);

    private final long loc;
    private final int size;

    public PacketFXScanSource(long loc, int size) {
        this.loc = loc;
        this.size = size;
    }

    public PacketFXScanSource(BlockPos pos, int size) {
        this(pos.asLong(), size);
    }

    private static void encode(FriendlyByteBuf buf, PacketFXScanSource pkt) {
        buf.writeLong(pkt.loc);
        buf.writeByte(pkt.size);
    }

    private static PacketFXScanSource decode(FriendlyByteBuf buf) {
        long loc = buf.readLong();
        int size = buf.readByte();
        return new PacketFXScanSource(loc, size);
    }

    public static void handle(PacketFXScanSource msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var level = Minecraft.getInstance().level;
            startScan(level, BlockPos.of(msg.loc), msg.size);
        });
    }

    private static void startScan(net.minecraft.world.level.Level world, BlockPos pos, int r) {
        int range = 4 + r * 4;
        ArrayList<BlockPos> positions = new ArrayList<BlockPos>();
        for (int xx = -range; xx <= range; ++xx) {
            for (int yy = -range; yy <= range; ++yy) {
                for (int zz = -range; zz <= range; ++zz) {
                    BlockPos p = pos.offset(xx, yy, zz);
                    if (Utils.isOreBlock(world, p)) {
                        positions.add(p);
                    }
                }
            }
        }
        while (!positions.isEmpty()) {
            BlockPos start = positions.get(0);
            ArrayList<BlockPos> coll = new ArrayList<BlockPos>();
            coll.add(start);
            positions.remove(0);
            calcGroup(world, start, coll, positions);
            if (!coll.isEmpty()) {
                int c = getOreColor(world, start);
                double x = 0.0;
                double y = 0.0;
                double z = 0.0;
                for (BlockPos p2 : coll) {
                    x += p2.getX() + 0.5;
                    y += p2.getY() + 0.5;
                    z += p2.getZ() + 0.5;
                }
                x /= coll.size();
                y /= coll.size();
                z /= coll.size();
                double dis = Math.sqrt(pos.distSqr(new BlockPos((int)x, (int)y, (int)z)));
                FXGeneric fb = new FXGeneric(world, x, y, z, 0.0, 0.0, 0.0);
                fb.setMaxAge(44);
                Color cc = new Color(c);
                fb.setRBGColorF(cc.getRed() / 255.0f, cc.getGreen() / 255.0f, cc.getBlue() / 255.0f);
                float q = (cc.getRed() / 255.0f + cc.getGreen() / 255.0f + cc.getBlue() / 255.0f) / 3.0f;
                fb.setAlphaF(0.0f, 1.0f, 0.8f, 0.0f);
                fb.setParticles(240, 15, 1);
                fb.setGridSize(16);
                fb.setLoop(true);
                fb.setScale(9.0f);
                fb.setLayer((q < 0.25f) ? 3 : 2);
                fb.setRotationSpeed(0.0f);
                ParticleEngine.addEffectWithDelay(world, fb, (int)(dis * 3.0));
            }
        }
    }

    private static void calcGroup(net.minecraft.world.level.Level world, BlockPos start, ArrayList<BlockPos> coll, ArrayList<BlockPos> positions) {
        BlockState bs = world.getBlockState(start);
        Label_0132:
        for (int x = -1; x <= 1; ++x) {
            for (int y = -1; y <= 1; ++y) {
                for (int z = -1; z <= 1; ++z) {
                    BlockPos t = new BlockPos(start).offset(x, y, z);
                    BlockState ts = world.getBlockState(t);
                    if (ts.equals(bs) && positions.contains(t)) {
                        positions.remove(t);
                        coll.add(t);
                        if (positions.isEmpty()) {
                            break Label_0132;
                        }
                        calcGroup(world, t, coll, positions);
                    }
                }
            }
        }
    }

    /**
     * Replaces OreDictionary color lookup using block registry path keywords.
     */
    private static int getOreColor(net.minecraft.world.level.Level world, BlockPos pos) {
        BlockState bi = world.getBlockState(pos);
        if (bi.getBlock() == Blocks.AIR || bi.getBlock() == Blocks.BEDROCK) {
            return 12632256;
        }
        Identifier blockId = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(bi.getBlock());
        if (blockId == null) return 12632256;
        String path = blockId.getPath().toLowerCase();
        if (path.contains("iron"))     return 14200723;
        if (path.contains("coal"))     return 1052688;
        if (path.contains("redstone")) return 16711680;
        if (path.contains("gold"))     return 16576075;
        if (path.contains("lapis"))    return 1328572;
        if (path.contains("diamond"))  return 6155509;
        if (path.contains("emerald"))  return 1564002;
        if (path.contains("quartz"))   return 15064789;
        if (path.contains("silver"))   return 14342653;
        if (path.contains("tin"))      return 15724539;
        if (path.contains("copper"))   return 16620629;
        if (path.contains("amber"))    return 16626469;
        if (path.contains("cinnabar")) return 10159368;
        return 12632256;
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
