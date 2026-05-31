package thaumcraft.proxies;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

// This class was the old Forge IGuiHandler pattern, superseded by MenuProvider + openMenu() in NeoForge.
public class ProxyGUI {

    public Object getClientGuiElement(int ID, Player player, Level world, int x, int y, int z) {
        return null;
    }

    public Object getServerGuiElement(int ID, Player player, Level world, int x, int y, int z) {
        return null;
    }
}
