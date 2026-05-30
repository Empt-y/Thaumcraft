package thaumcraft.client.renderers.entity.mob;
import java.util.ArrayList;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
// TODO: Rewrite for MC 1.21.5 LayerRenderer system
@OnlyIn(Dist.CLIENT)
public class LayerTainted {
    public static ArrayList<Integer> taintLayers = new ArrayList<>();
}
