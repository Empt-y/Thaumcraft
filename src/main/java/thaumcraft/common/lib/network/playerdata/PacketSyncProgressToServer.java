package thaumcraft.common.lib.network.playerdata;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.utils.InventoryUtils;
import io.netty.buffer.ByteBuf;


public class PacketSyncProgressToServer implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketSyncProgressToServer> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "sync_progress_to_server"));

    public static final StreamCodec<FriendlyByteBuf, PacketSyncProgressToServer> STREAM_CODEC =
        StreamCodec.of(PacketSyncProgressToServer::encode, PacketSyncProgressToServer::decode);

    private final String key;
    private final boolean first;
    private final boolean checks;
    private final boolean noFlags;

    public PacketSyncProgressToServer(String key, boolean first, boolean checks, boolean noFlags) {
        this.key = key;
        this.first = first;
        this.checks = checks;
        this.noFlags = noFlags;
    }

    public PacketSyncProgressToServer(String key, boolean first) {
        this(key, first, false, true);
    }

    private static void encode(FriendlyByteBuf buf, PacketSyncProgressToServer pkt) {
        buf.writeUtf(pkt.key);
        buf.writeBoolean(pkt.first);
        buf.writeBoolean(pkt.checks);
        buf.writeBoolean(pkt.noFlags);
    }

    private static PacketSyncProgressToServer decode(FriendlyByteBuf buf) {
        String key = buf.readUtf();
        boolean first = buf.readBoolean();
        boolean checks = buf.readBoolean();
        boolean noFlags = buf.readBoolean();
        return new PacketSyncProgressToServer(key, first, checks, noFlags);
    }

    public static void handle(PacketSyncProgressToServer msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = (Player) ctx.player();
            if (player != null && msg.first != ThaumcraftCapabilities.knowsResearch(player, msg.key)) {
                if (msg.checks && !checkRequisites(player, msg.key)) {
                    return;
                }
                if (msg.noFlags) {
                    ResearchManager.noFlags = true;
                }
                ResearchManager.progressResearch(player, msg.key);
            }
        });
    }

    private static boolean checkRequisites(Player player, String key) {
        ResearchEntry research = ResearchCategories.getResearch(key);
        if (research.getStages() != null) {
            int currentStage = ThaumcraftCapabilities.getKnowledge(player).getResearchStage(key) - 1;
            if (currentStage < 0) {
                return false;
            }
            if (currentStage >= research.getStages().length) {
                return true;
            }
            ResearchStage stage = research.getStages()[currentStage];
            Object[] o = stage.getObtain();
            if (o != null) {
                for (int a = 0; a < o.length; ++a) {
                    ItemStack ts = ItemStack.EMPTY;
                    boolean ore = false;
                    if (o[a] instanceof ItemStack) {
                        ts = (ItemStack) o[a];
                    }
                    else {
                        // TODO: OreDictionary.getOres() doesn't exist in NeoForge 26.1.2
                        // Replace with tag-based lookup if needed; skipping ore-dict items for now
                        ore = true;
                        continue;
                    }
                    if (!InventoryUtils.isPlayerCarryingAmount(player, ts, ore)) {
                        return false;
                    }
                }
                for (int a = 0; a < o.length; ++a) {
                    boolean ore2 = false;
                    ItemStack ts2 = ItemStack.EMPTY;
                    if (o[a] instanceof ItemStack) {
                        ts2 = (ItemStack) o[a];
                    }
                    else {
                        // TODO: OreDictionary.getOres() doesn't exist in NeoForge 26.1.2
                        ore2 = true;
                        continue;
                    }
                    InventoryUtils.consumePlayerItem(player, ts2, true, ore2);
                }
            }
            Object[] c = stage.getCraft();
            if (c != null) {
                for (int a2 = 0; a2 < c.length; ++a2) {
                    if (!ThaumcraftCapabilities.getKnowledge(player).isResearchKnown("[#]" + stage.getCraftReference()[a2])) {
                        return false;
                    }
                }
            }
            String[] r = stage.getResearch();
            if (r != null) {
                for (int a3 = 0; a3 < r.length; ++a3) {
                    if (!ThaumcraftCapabilities.knowsResearchStrict(player, r[a3])) {
                        return false;
                    }
                }
            }
            ResearchStage.Knowledge[] k = stage.getKnow();
            if (k != null) {
                for (int a4 = 0; a4 < k.length; ++a4) {
                    int pk = ThaumcraftCapabilities.getKnowledge(player).getKnowledge(k[a4].type, k[a4].category);
                    if (pk < k[a4].amount) {
                        return false;
                    }
                }
                for (int a4 = 0; a4 < k.length; ++a4) {
                    ResearchManager.addKnowledge(player, k[a4].type, k[a4].category, -k[a4].amount * k[a4].type.getProgression());
                }
            }
        }
        return true;
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
