package thaumcraft.common.lib.network;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
// FML FMLCommonHandler removed
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.api.distmarker.Dist;
import thaumcraft.common.lib.network.playerdata.PacketSyncKnowledge;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;


@net.neoforged.fml.common.EventBusSubscriber(modid = "thaumcraft")
public class EventHandlerNetwork
{
    @SubscribeEvent
    public void playerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        Side side = null /* FMLCommonHandler removed */;
        if (side == Side.SERVER) {
            Player p = event.getEntity();
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer((net.minecraft.server.level.ServerPlayer)p, new PacketSyncWarp(p));
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer((net.minecraft.server.level.ServerPlayer)p, new PacketSyncKnowledge(p));
        }
    }
}
