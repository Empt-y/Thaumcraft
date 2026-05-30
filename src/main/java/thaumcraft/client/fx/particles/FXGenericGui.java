package thaumcraft.client.fx.particles;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.Level;


public class FXGenericGui extends FXGeneric
{
    boolean inGame;

    public FXGenericGui(Level world, double x, double y, double z) {
        super(world, x, y, z);
        inGame = Minecraft.getInstance().screen == null;
    }

    public FXGenericGui(Level world, double x, double y, double z, double xx, double yy, double zz) {
        super(world, x, y, z, xx, yy, zz);
        inGame = Minecraft.getInstance().screen == null;
    }

    @Override
    public void tick() {
        super.tick();
        if (!inGame && Minecraft.getInstance().screen == null) {
            remove();
        }
    }
}
