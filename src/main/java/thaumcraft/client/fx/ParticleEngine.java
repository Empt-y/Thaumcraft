package thaumcraft.client.fx;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;


public class ParticleEngine
{
    /** Particle index → atlas sprite, populated when the particle atlas is stitched. */
    public static final Map<Integer, TextureAtlasSprite> particleSprites = new HashMap<>();

    private static final int[] REGISTERED_INDICES = {
        // FXSmokeSpiral row 0, cols 1-5
        1,2,3,4,5,
        // FXFireMote index 7
        7,
        // FXGeneric rows 0
        8,9,10,11,12,13,14,15,
        24,25,26,27,28,29,30,31,
        40,41,42,43,44,45,46,47,
        56,57,58,59,60,61,62,63,64,
        72,73,74,75,77,
        108,109,110,111,
        123,124,125,126,127,
        160,161,162,163,164,165,
        // FXWispEG row 3, cols 0-12
        192,193,194,195,196,197,198,199,200,201,202,203,204,
        208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,223,
        224,225,226,227,228,229,230,231,232,233,234,235,236,237,238,
        // FXBoreSparkle row 4, cols 0-3
        256,257,258,259,
        264,265,266,267,268,269,270,271,
        320,321,322,323,324,325,326,327,328,329,330,331,332,333,334,335,
        457,
        // FXVisSparkle and FXGeneric row 8
        512,513,514,515,516,517,518,519,520,521,522,523,524,525,526,527
    };

    /** Call this after the particle atlas is stitched to populate the sprite map. */
    public static void loadParticleSprites(TextureAtlas atlas) {
        particleSprites.clear();
        for (int n : REGISTERED_INDICES) {
            Identifier id = Identifier.fromNamespaceAndPath("thaumcraft", "tc_particle/p" + n);
            TextureAtlasSprite sprite = atlas.getSprite(id);
            if (sprite != null) {
                particleSprites.put(n, sprite);
            }
        }
    }

    public static TextureAtlasSprite getSprite(int index) {
        return particleSprites.getOrDefault(index, null);
    }

    public static void addEffect(Level world, Particle fx) {
        if (Minecraft.getInstance().level != null && fx != null) {
            Minecraft.getInstance().particleEngine.add(fx);
        }
    }

    public static void addEffectWithDelay(Level world, Particle fx, int delay) {
        addEffect(world, fx);
    }
}
