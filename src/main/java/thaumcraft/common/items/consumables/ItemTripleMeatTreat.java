package thaumcraft.common.items.consumables;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.effect.MobEffectInstance;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemTripleMeatTreat extends Item /* ItemFood removed */ implements IThaumcraftItems
{
    public ItemTripleMeatTreat() {
        super(new net.minecraft.world.item.Item.Properties());
        setAlwaysEdible();
        setMobEffectInstance(new MobEffectInstance(MobEffects.REGENERATION, 100, 0), 0.66f);
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
    }
    
    public Item getItem() {
        return this;
    }
    
    public String[] getVariantNames() {
        return new String[] { "normal" };
    }
    
    public int[] getVariantMeta() {
        return new int[] { 0 };
    }
    
    public Object /* ItemMeshDefinition removed */ getCustomMesh() {
        return null;
    }
    
    public Object /* ModelResourceLocation removed */ getCustomModelResourceLocation(String variant) {
        return null /* removed */;
    }
}
