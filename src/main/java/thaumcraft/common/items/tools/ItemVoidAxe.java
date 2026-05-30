package thaumcraft.common.items.tools;
import net.minecraft.world.item.ToolMaterial;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
// FML FMLCommonHandler removed
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemVoidAxe extends Item /* ItemAxe removed */ implements IWarpingGear, IThaumcraftItems
{
    public ItemVoidAxe(ToolMaterial enumtoolmaterial) {
        super(new net.minecraft.world.item.Item.Properties());
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
    
    public Set<String> getToolClasses(ItemStack stack) {
        return ImmutableSet.of("axe");
    }
    
    public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @javax.annotation.Nullable net.minecraft.world.entity.EquipmentSlot p_77663_4_) {
        super.inventoryTick(stack, world, entity, p_77663_4_);
        if ((stack.getDamageValue() > 0) && entity != null && entity.tickCount % 20 == 0 && entity instanceof LivingEntity) {
            if (stack.isDamageableItem()) stack.setDamageValue(Math.max(0, stack.getDamageValue() - 1));
        }
    }
    
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (!player.level().isClientSide() && entity instanceof LivingEntity) {
            if (!(entity instanceof Player) ) {
                ((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80));
            }
        }
        return super.onLeftClickEntity(stack, player, entity);
    }
    
    public int getWarp(ItemStack itemstack, Player player) {
        return 1;
    }
}
