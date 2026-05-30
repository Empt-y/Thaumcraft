package thaumcraft.common.lib.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import thaumcraft.Thaumcraft;
import thaumcraft.common.lib.network.fx.*;
import thaumcraft.common.lib.network.misc.*;
import thaumcraft.common.lib.network.playerdata.*;
import thaumcraft.common.lib.network.tiles.*;

public class PacketHandler {

    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar reg = event.registrar(Thaumcraft.MODID);

        // fx - client bound
        reg.playToClient(PacketFXBlockArc.TYPE,   PacketFXBlockArc.STREAM_CODEC,   PacketFXBlockArc::handle);
        reg.playToClient(PacketFXBlockBamf.TYPE,  PacketFXBlockBamf.STREAM_CODEC,  PacketFXBlockBamf::handle);
        reg.playToClient(PacketFXBlockMist.TYPE,  PacketFXBlockMist.STREAM_CODEC,  PacketFXBlockMist::handle);
        reg.playToClient(PacketFXBoreDig.TYPE,    PacketFXBoreDig.STREAM_CODEC,    PacketFXBoreDig::handle);
        reg.playToClient(PacketFXEssentiaSource.TYPE, PacketFXEssentiaSource.STREAM_CODEC, PacketFXEssentiaSource::handle);
        reg.playToClient(PacketFXFocusEffect.TYPE,          PacketFXFocusEffect.STREAM_CODEC,          PacketFXFocusEffect::handle);
        reg.playToClient(PacketFXFocusPartImpact.TYPE,      PacketFXFocusPartImpact.STREAM_CODEC,      PacketFXFocusPartImpact::handle);
        reg.playToClient(PacketFXFocusPartImpactBurst.TYPE, PacketFXFocusPartImpactBurst.STREAM_CODEC, PacketFXFocusPartImpactBurst::handle);
        reg.playToClient(PacketFXInfusionSource.TYPE, PacketFXInfusionSource.STREAM_CODEC, PacketFXInfusionSource::handle);
        reg.playToClient(PacketFXPollute.TYPE,    PacketFXPollute.STREAM_CODEC,    PacketFXPollute::handle);
        reg.playToClient(PacketFXScanSource.TYPE, PacketFXScanSource.STREAM_CODEC, PacketFXScanSource::handle);
        reg.playToClient(PacketFXShield.TYPE,     PacketFXShield.STREAM_CODEC,     PacketFXShield::handle);
        reg.playToClient(PacketFXSlash.TYPE,      PacketFXSlash.STREAM_CODEC,      PacketFXSlash::handle);
        reg.playToClient(PacketFXSonic.TYPE,      PacketFXSonic.STREAM_CODEC,      PacketFXSonic::handle);
        reg.playToClient(PacketFXWispZap.TYPE,    PacketFXWispZap.STREAM_CODEC,    PacketFXWispZap::handle);
        reg.playToClient(PacketFXZap.TYPE,        PacketFXZap.STREAM_CODEC,        PacketFXZap::handle);

        // misc - client bound
        reg.playToClient(PacketAuraToClient.TYPE,          PacketAuraToClient.STREAM_CODEC,          PacketAuraToClient::handle);
        reg.playToClient(PacketBiomeChange.TYPE,           PacketBiomeChange.STREAM_CODEC,           PacketBiomeChange::handle);
        reg.playToClient(PacketItemToClientContainer.TYPE, PacketItemToClientContainer.STREAM_CODEC, PacketItemToClientContainer::handle);
        reg.playToClient(PacketKnowledgeGain.TYPE,         PacketKnowledgeGain.STREAM_CODEC,         PacketKnowledgeGain::handle);
        reg.playToClient(PacketMiscEvent.TYPE,             PacketMiscEvent.STREAM_CODEC,             PacketMiscEvent::handle);
        reg.playToClient(PacketNote.TYPE,                  PacketNote.STREAM_CODEC,                  PacketNote::handle);
        reg.playToClient(PacketSealFilterToClient.TYPE,    PacketSealFilterToClient.STREAM_CODEC,    PacketSealFilterToClient::handle);
        reg.playToClient(PacketSealToClient.TYPE,          PacketSealToClient.STREAM_CODEC,          PacketSealToClient::handle);

        // misc - server bound
        reg.playToServer(PacketFocusChangeToServer.TYPE,              PacketFocusChangeToServer.STREAM_CODEC,              PacketFocusChangeToServer::handle);
        reg.playToServer(PacketItemKeyToServer.TYPE,                   PacketItemKeyToServer.STREAM_CODEC,                   PacketItemKeyToServer::handle);
        reg.playToServer(PacketLogisticsRequestToServer.TYPE,          PacketLogisticsRequestToServer.STREAM_CODEC,          PacketLogisticsRequestToServer::handle);
        reg.playToServer(PacketMiscStringToServer.TYPE,                PacketMiscStringToServer.STREAM_CODEC,                PacketMiscStringToServer::handle);
        reg.playToServer(PacketSelectThaumotoriumRecipeToServer.TYPE,  PacketSelectThaumotoriumRecipeToServer.STREAM_CODEC,  PacketSelectThaumotoriumRecipeToServer::handle);
        reg.playToServer(PacketStartTheoryToServer.TYPE,               PacketStartTheoryToServer.STREAM_CODEC,               PacketStartTheoryToServer::handle);

        // playerdata - client bound
        reg.playToClient(PacketSyncKnowledge.TYPE, PacketSyncKnowledge.STREAM_CODEC, PacketSyncKnowledge::handle);
        reg.playToClient(PacketSyncWarp.TYPE,      PacketSyncWarp.STREAM_CODEC,      PacketSyncWarp::handle);
        reg.playToClient(PacketWarpMessage.TYPE,   PacketWarpMessage.STREAM_CODEC,   PacketWarpMessage::handle);

        // playerdata - server bound
        reg.playToServer(PacketFocusNameToServer.TYPE,          PacketFocusNameToServer.STREAM_CODEC,          PacketFocusNameToServer::handle);
        reg.playToServer(PacketFocusNodesToServer.TYPE,         PacketFocusNodesToServer.STREAM_CODEC,         PacketFocusNodesToServer::handle);
        reg.playToServer(PacketPlayerFlagToServer.TYPE,         PacketPlayerFlagToServer.STREAM_CODEC,         PacketPlayerFlagToServer::handle);
        reg.playToServer(PacketSyncProgressToServer.TYPE,       PacketSyncProgressToServer.STREAM_CODEC,       PacketSyncProgressToServer::handle);
        reg.playToServer(PacketSyncResearchFlagsToServer.TYPE,  PacketSyncResearchFlagsToServer.STREAM_CODEC,  PacketSyncResearchFlagsToServer::handle);

        // tiles
        reg.playToClient(PacketTileToClient.TYPE, PacketTileToClient.STREAM_CODEC, PacketTileToClient::handle);
        reg.playToServer(PacketTileToServer.TYPE, PacketTileToServer.STREAM_CODEC, PacketTileToServer::handle);
    }

    /** Send a packet to the server. */
    public static <T extends CustomPacketPayload> void sendToServer(T packet) {
        PacketDistributor.sendToServer(packet);
    }

    /** Send a packet to a specific player. */
    public static <T extends CustomPacketPayload> void sendToPlayer(T packet, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    /** Send a packet to all players. */
    public static <T extends CustomPacketPayload> void sendToAll(T packet) {
        PacketDistributor.sendToAllPlayers(packet);
    }
}
