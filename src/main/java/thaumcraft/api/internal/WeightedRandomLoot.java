package thaumcraft.api.internal;
import java.util.ArrayList;
import net.minecraft.world.item.ItemStack;


public class WeightedRandomLoot {

    public final int itemWeight;
    public ItemStack item;

    public WeightedRandomLoot(ItemStack stack, int weight) {
        this.itemWeight = weight;
        item = stack;
    }

    public static ArrayList<WeightedRandomLoot> lootBagCommon   = new ArrayList<>();
    public static ArrayList<WeightedRandomLoot> lootBagUncommon = new ArrayList<>();
    public static ArrayList<WeightedRandomLoot> lootBagRare     = new ArrayList<>();
}
