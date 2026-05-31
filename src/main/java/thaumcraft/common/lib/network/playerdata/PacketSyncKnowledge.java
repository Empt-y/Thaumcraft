package thaumcraft.common.lib.network.playerdata;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.client.gui.ResearchToast;
import thaumcraft.common.lib.utils.Utils;
import io.netty.buffer.ByteBuf;


public class PacketSyncKnowledge implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketSyncKnowledge> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "sync_knowledge"));

    public static final StreamCodec<FriendlyByteBuf, PacketSyncKnowledge> STREAM_CODEC =
        StreamCodec.of(PacketSyncKnowledge::encode, PacketSyncKnowledge::decode);

    protected final CompoundTag data;

    public PacketSyncKnowledge(CompoundTag data) {
        this.data = data;
    }

    public PacketSyncKnowledge(Player player) {
        IPlayerKnowledge pk = ThaumcraftCapabilities.getKnowledge(player);
        this.data = pk.serializeNBT();
        for (String key : pk.getResearchList()) {
            pk.clearResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.POPUP);
        }
    }

    private static void encode(FriendlyByteBuf buf, PacketSyncKnowledge pkt) {
        Utils.writeCompoundTagToBuffer(buf, pkt.data);
    }

    private static PacketSyncKnowledge decode(FriendlyByteBuf buf) {
        CompoundTag data = Utils.readCompoundTagFromBuffer(buf);
        return new PacketSyncKnowledge(data);
    }

    public static void handle(PacketSyncKnowledge msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;
            IPlayerKnowledge pk = ThaumcraftCapabilities.getKnowledge(player);
            pk.deserializeNBT(msg.data);
            for (String key : pk.getResearchList()) {
                if (pk.hasResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.POPUP)) {
                    ResearchEntry ri = ResearchCategories.getResearch(key);
                    if (ri != null) {
                        Minecraft.getInstance().getToastManager().addToast(new ResearchToast(ri));
                    }
                }
                pk.clearResearchFlag(key, IPlayerKnowledge.EnumResearchFlag.POPUP);
            }
        });
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
