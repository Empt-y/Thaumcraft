package thaumcraft.common.golems.seals;
import net.minecraft.resources.Identifier;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.seals.ISealConfigToggles;


public class SealFillAdvanced extends SealFill implements ISealConfigToggles
{
    Identifier icon;
    
    public SealFillAdvanced() {
        icon = Identifier.fromNamespaceAndPath("thaumcraft", "items/seals/seal_fill_advanced");
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:fill_advanced";
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
    public int[] getGuiCategories() {
        return new int[] { 1, 3, 0, 4 };
    }
    
    @Override
    public SealToggle[] getToggles() {
        return props;
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
