package thaumcraft.common.golems.seals;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.seals.ISealConfigToggles;


public class SealEmptyAdvanced extends SealEmpty implements ISealConfigToggles
{
    Identifier icon;
    
    public SealEmptyAdvanced() {
        icon = Identifier.fromNamespaceAndPath("thaumcraft", "items/seals/seal_empty_advanced");
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:empty_advanced";
    }
    
    @Override
    public int getFilterSize() {
        return 9;
    }
    
    @Override
    public Identifier getSealIcon() {
        return icon;
    }
    
    @Override
    public NonNullList<ItemStack> getInv(int c) {
        if (getToggles()[4].value && !isBlacklist()) {
            ArrayList<ItemStack> w = new ArrayList<ItemStack>();
            for (ItemStack s : super.getInv()) {
                if (s != null && !s.isEmpty()) {
                    w.add(s);
                }
            }
            if (w.size() > 0) {
                int i = Math.abs(c % w.size());
                return NonNullList.withSize(1, w.get(i));
            }
        }
        return super.getInv();
    }
    
    @Override
    public NonNullList<ItemStack> getInv() {
        return super.getInv();
    }
    
    @Override
    public SealToggle[] getToggles() {
        return props;
    }
    
    @Override
    public int[] getGuiCategories() {
        return new int[] { 1, 3, 0, 4 };
    }
    
    @Override
    public void setToggle(int indx, boolean value) {
        props[indx].setValue(value);
    }
    
    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[] { EnumGolemTrait.SMART };
    }
}
