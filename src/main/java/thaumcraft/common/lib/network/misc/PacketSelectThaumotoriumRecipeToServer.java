package thaumcraft.common.lib.network.misc;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.common.tiles.crafting.TileThaumatorium;
import io.netty.buffer.ByteBuf;


public class PacketSelectThaumotoriumRecipeToServer implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketSelectThaumotoriumRecipeToServer> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "select_thaumatorium_recipe_to_server"));

    public static final StreamCodec<FriendlyByteBuf, PacketSelectThaumotoriumRecipeToServer> STREAM_CODEC =
        StreamCodec.of(PacketSelectThaumotoriumRecipeToServer::encode, PacketSelectThaumotoriumRecipeToServer::decode);

    private final long pos;
    private final int hash;

    public PacketSelectThaumotoriumRecipeToServer(long pos, int hash) {
        this.pos = pos;
        this.hash = hash;
    }

    public PacketSelectThaumotoriumRecipeToServer(Player player, BlockPos pos, int recipeHash) {
        this.pos = pos.asLong();
        this.hash = recipeHash;
    }

    private static void encode(FriendlyByteBuf buf, PacketSelectThaumotoriumRecipeToServer pkt) {
        buf.writeLong(pkt.pos);
        buf.writeInt(pkt.hash);
    }

    private static PacketSelectThaumotoriumRecipeToServer decode(FriendlyByteBuf buf) {
        long pos = buf.readLong();
        int hash = buf.readInt();
        return new PacketSelectThaumotoriumRecipeToServer(pos, hash);
    }

    public static void handle(PacketSelectThaumotoriumRecipeToServer msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer serverPlayer = (ServerPlayer) ctx.player();
            var level = serverPlayer.serverLevel();
            Player player = serverPlayer;
            BlockPos bp = BlockPos.of(msg.pos);
            if (level != null && player != null && bp != null) {
                BlockEntity te = getLevel().getBlockEntity(bp);
                if (te != null && te instanceof TileThaumatorium) {
                    TileThaumatorium thaumatorium = (TileThaumatorium) te;
                    int i = 0;
                    boolean flag = false;
                    for (int hash : thaumatorium.recipeHash) {
                        if (msg.hash == hash) {
                            thaumatorium.recipeEssentia.remove(i);
                            thaumatorium.recipePlayer.remove(i);
                            thaumatorium.recipeHash.remove(i);
                            thaumatorium.currentCraft = -1;
                            flag = true;
                            break;
                        }
                        ++i;
                    }
                    if (!flag && thaumatorium.recipeHash.size() < thaumatorium.maxRecipes) {
                        for (CrucibleRecipe cr : thaumatorium.recipes) {
                            if (cr.hash == msg.hash) {
                                thaumatorium.recipeEssentia.add(cr.getAspects().copy());
                                thaumatorium.recipePlayer.add(player.getName());
                                thaumatorium.recipeHash.add(cr.hash);
                                flag = true;
                                break;
                            }
                        }
                    }
                    if (flag) {
                        thaumatorium.setChanged();
                        thaumatorium.syncTile(false);
                    }
                }
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
