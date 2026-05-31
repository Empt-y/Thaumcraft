package thaumcraft.common.lib.capabilities;
import javax.annotation.Nonnull;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
// import net.neoforged.neoforge.capabilities.Object /* Capability removed */; // API changed
// removed: import net.minecraftforge.common.capabilities.Object /* ICapabilitySerializable removed */;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncWarp;


public class PlayerWarp
{
    public static void preInit() {
    }

    public static IPlayerWarp createDefault() {
        return new DefaultImpl();
    }
    
    private static class DefaultImpl implements IPlayerWarp
    {
        private int[] warp;
        private int counter;
        
        private DefaultImpl() {
            warp = new int[EnumWarpType.values().length];
        }
        
        @Override
        public void clear() {
            warp = new int[EnumWarpType.values().length];
            counter = 0;
        }
        
        @Override
        public int get(@Nonnull EnumWarpType type) {
            return warp[type.ordinal()];
        }
        
        @Override
        public void set(EnumWarpType type, int amount) {
            warp[type.ordinal()] = Mth.clamp(amount, 0, 500);
        }
        
        @Override
        public int add(@Nonnull EnumWarpType type, int amount) {
            return warp[type.ordinal()] = Mth.clamp(warp[type.ordinal()] + amount, 0, 500);
        }
        
        @Override
        public int reduce(@Nonnull EnumWarpType type, int amount) {
            return warp[type.ordinal()] = Mth.clamp(warp[type.ordinal()] - amount, 0, 500);
        }
        
        @Override
        public void sync(@Nonnull net.minecraft.server.level.ServerPlayer player) {
            PacketHandler.sendToPlayer(new PacketSyncWarp(player), (net.minecraft.server.level.ServerPlayer)player);
        }
        
        public CompoundTag serializeNBT() {
            CompoundTag properties = new CompoundTag();
            properties.putIntArray("warp", warp);
            properties.putInt("counter", getCounter());
            return properties;
        }
        
        public void deserializeNBT(CompoundTag properties) {
            if (properties == null) {
                return;
            }
            clear();
            int[] ba = properties.getIntArray("warp").orElse(new int[0]);
            if (ba != null) {
                int l = EnumWarpType.values().length;
                if (ba.length < l) {
                    l = ba.length;
                }
                for (int a = 0; a < l; ++a) {
                    warp[a] = ba[a];
                }
            }
            setCounter(properties.getIntOr("counter", 0));
        }
        
        @Override
        public int getCounter() {
            return counter;
        }
        
        @Override
        public void setCounter(int amount) {
            counter = amount;
        }
    }
    
    public static class Provider
    {
        public static Identifier NAME;
        private DefaultImpl warp;
        
        public Provider() {
            warp = new DefaultImpl();
        }
        
        public boolean hasCapability(Object /* Capability removed */ capability, Direction facing) {
            return capability == ThaumcraftCapabilities.WARP;
        }
        
        public <T> T getCapability(Object /* Capability removed */ capability, Direction facing) {
            if (capability == ThaumcraftCapabilities.WARP) {
                return (T)warp;
            }
            return null;
        }
        
        public CompoundTag serializeNBT() {
            return warp.serializeNBT();
        }
        
        public void deserializeNBT(CompoundTag nbt) {
            warp.deserializeNBT(nbt);
        }
        
        static {
            NAME = Identifier.fromNamespaceAndPath("thaumcraft", "warp");
        }
    }
}
