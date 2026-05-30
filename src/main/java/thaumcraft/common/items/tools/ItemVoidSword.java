package thaumcraft.common.items.tools;
import net.minecraft.world.item.ToolMaterial;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
// import net.minecraft.world.item.Item /* SwordItem removed */; // removed
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
// FML FMLCommonHandler removed
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;
import net.minecraft.world.item.TooltipFlag;


public class ItemVoidSword extends Item /* ItemSword removed */ implements IWarpingGear, IThaumcraftItems
{
    public ItemVoidSword(ToolMaterial enumtoolmaterial) {
        super(new net.minecraft.world.item.Item.Properties());
        // ItemTCBase constructor
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
    
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int p_77663_4_, boolean p_77663_5_) {
        super.inventoryTick(stack, world, entity, p_77663_4_, p_77663_5_);
        if ((stack.getDamageValue() > 0) && entity != null && entity.tickCount % 20 == 0 && entity instanceof LivingEntity) {
            if (stack.isDamageableItem()) stack.setDamageValue(Math.max(0, stack.getDamageValue() - 1));
        }
    }
    
    public boolean hitEntity(ItemStack is, LivingEntity target, LivingEntity hitter) {
        if (!target.level().isClientSide()) {
            if (!(target instanceof Player) || !(hitter instanceof Player) ) {
                try {
                    target.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60));
                }
                catch (Exception ex) {}
            }
        }
        return super.hitEntity(is, target, hitter);
    }
    
    public int getWarp(ItemStack itemstack, Player player) {
        return 1;
    }
    
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, java.util.List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(net.minecraft.network.chat.Component.literal("" + ChatFormatting.GOLD + I18n.get("enchantment.special.sapless")));
        super.appendHoverText(stack, context, tooltip, flagIn);
    }
}
