package thaumcraft.common.golems.seals;
import net.minecraft.resources.Identifier;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.seals.ISealConfigToggles;


public class SealPickupAdvanced extends SealPickup implements ISealConfigToggles
{
    Identifier icon;
    
    public SealPickupAdvanced() {
        icon = Identifier.fromNamespaceAndPath("thaumcraft", "items/seals/seal_pickup_advanced");
    }
    
    @Override
    public String getKey() {
        return "thaumcraft:pickup_advanced";
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
        return new int[] { 2, 1, 3, 0, 4 };
    }
    
    @Override
    public EnumGolemTrait[] getRequiredTags() {
        return new EnumGolemTrait[] { EnumGolemTrait.SMART };
    }
    
    @Override
    public SealToggle[] getToggles() {
        return props;
    }
    
    @Override
    public void setToggle(int indx, boolean value) {
        props[indx].setValue(value);
    }
}
