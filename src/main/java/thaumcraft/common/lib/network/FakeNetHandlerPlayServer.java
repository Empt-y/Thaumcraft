package thaumcraft.common.lib.network;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketFlow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public class FakeNetHandlerPlayServer extends ServerGamePacketListenerImpl {
    private static final Connection DUMMY_CONNECTION = new Connection(PacketFlow.CLIENTBOUND);

    public FakeNetHandlerPlayServer(MinecraftServer server, ServerPlayer player) {
        super(server, DUMMY_CONNECTION, player, CommonListenerCookie.createInitial(player.getGameProfile(), false));
    }

    @Override
    public void tick() {}
}
