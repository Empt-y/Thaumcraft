package thaumcraft.common.tiles.crafting;
import java.util.HashMap;
import java.util.Iterator;
import net.minecraft.nbt.CompoundTag;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusNode;
import thaumcraft.api.casters.IFocusElement;


public class FocusElementNode
{
    public int x;
    public int y;
    public int id;
    public boolean target;
    public boolean trajectory;
    public int parent;
    public int[] children;
    public float complexityMultiplier;
    public FocusNode node;
    
    public FocusElementNode() {
        target = false;
        trajectory = false;
        parent = -1;
        children = new int[0];
        complexityMultiplier = 1.0f;
        node = null;
    }
    
    public float getPower(HashMap<Integer, FocusElementNode> data) {
        if (node == null) {
            return 1.0f;
        }
        float pow = node.getPowerMultiplier();
        FocusElementNode p = data.get(parent);
        if (p != null && p.node != null) {
            pow *= p.getPower(data);
        }
        return pow;
    }
    
    public void deserialize(CompoundTag nbt) {
        x = nbt.getIntOr("x", 0);
        y = nbt.getIntOr("y", 0);
        id = nbt.getIntOr("id", 0);
        target = nbt.getBooleanOr("target", false);
        trajectory = nbt.getBooleanOr("trajectory", false);
        parent = nbt.getIntOr("parent", 0);
        children = nbt.getIntArray("children").orElse(new int[0]);
        complexityMultiplier = nbt.getFloatOr("complexity", 0.0f);
        IFocusElement fe = FocusEngine.getElement(nbt.getStringOr("key", ""));
        if (fe != null) {
            node = (FocusNode)fe;
            ((FocusNode)fe).initialize();
            if (((FocusNode)fe).getSettingList() != null) {
                for (String ns : ((FocusNode)fe).getSettingList()) {
                    ((FocusNode)fe).getSetting(ns).setValue(nbt.getInt("setting." + ns));
                }
            }
        }
    }
    
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("x", x);
        nbt.putInt("y", y);
        nbt.putInt("id", id);
        nbt.putBoolean("target", target);
        nbt.putBoolean("trajectory", trajectory);
        nbt.putInt("parent", parent);
        nbt.putIntArray("children", children);
        nbt.putFloat("complexity", complexityMultiplier);
        if (node != null) {
            nbt.putString("key", node.getKey());
            if (node.getSettingList() != null) {
                for (String ns : node.getSettingList()) {
                    nbt.putInt("setting." + ns, node.getSettingValue(ns));
                }
            }
        }
        return nbt;
    }
}
