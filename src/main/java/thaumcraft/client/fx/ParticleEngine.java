package thaumcraft.client.fx;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.level.Level;


// Simplified particle engine: routes to MC's built-in particle manager.
// All Thaumcraft particles use NO_RENDER so they tick but don't visually render.
public class ParticleEngine
{
    public static void addEffect(Level world, Particle fx) {
        if (Minecraft.getInstance().level != null && fx != null) {
            Minecraft.getInstance().particleEngine.add(fx);
        }
    }

    public static void addEffectWithDelay(Level world, Particle fx, int delay) {
        // Delay ignored; just add immediately (cosmetic simplification)
        addEffect(world, fx);
    }
}
