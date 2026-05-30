package thaumcraft.common.lib.network;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;


public class FakeNetHandlerPlayServer extends ServerGamePacketListenerImpl
{
    public FakeNetHandlerPlayServer(MinecraftServer server, Connection networkManagerIn, ServerPlayer playerIn) {
        super(server, networkManagerIn, playerIn);
    }

    @Override
    public void tick() {
    }
}
