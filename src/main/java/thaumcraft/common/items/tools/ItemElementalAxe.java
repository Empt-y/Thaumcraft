package thaumcraft.common.items.tools;
import net.minecraft.world.item.ToolMaterial;
import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;
import thaumcraft.common.lib.utils.EntityUtils;


public class ItemElementalAxe extends Item /* ItemAxe removed */ implements IThaumcraftItems
{
    public ItemElementalAxe(ToolMaterial enumtoolmaterial) {
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
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.RARE;
    }
    
    public boolean isValidRepairItem(ItemStack repairItem) {
        return ItemStack.isSameItem(repairItem, new ItemStack(ItemsTC.ingots, 1)) || false;
    }
    
    public ItemUseAnimation getItemUseAction(ItemStack itemstack) {
        return ItemUseAnimation.BOW;
    }
    
    public int getMaxItemUseDuration(ItemStack p_77626_1_) {
        return 72000;
    }
    
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand hand) {
        playerIn.startUsingItem(hand);
        return InteractionResult.SUCCESS;
    }
    
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        List<ItemEntity> stuff = EntityUtils.getEntitiesInRange(player.level(), player.getX(), player.getY(), player.getZ(), player, ItemEntity.class, 10.0);
        if (stuff != null && stuff.size() > 0) {
            for (ItemEntity e : stuff) {
                if (!e.isDeadOrDying()) {
                    double d6 = e.getX() - player.getX();
                    double d7 = e.getY() - player.getY() + player.getBbHeight() / 2.0f;
                    double d8 = e.getZ() - player.getZ();
                    double d9 = Mth.sqrt((float)(d6 * d6 + d7 * d7 + d8 * d8));
                    d6 /= d9;
                    d7 /= d9;
                    d8 /= d9;
                    double d10 = 0.3;
                    ItemEntity entityItem = e;
                    entityItem.setDeltaMovement(entityItem.getDeltaMovement().x - (d6 * d10), entityItem.getDeltaMovement().y, entityItem.getDeltaMovement().z);
                    ItemEntity entityItem2 = e;
                    entityItem2.getDeltaMovement().y -= d7 * d10 - 0.1;
                    ItemEntity entityItem3 = e;
                    entityItem3.getDeltaMovement().z -= d8 * d10;
                    if (e.getDeltaMovement().x > 0.25) {
                        e.setDeltaMovement(0.25, e.getDeltaMovement().y, e.getDeltaMovement().z);
                    }
                    if (e.getDeltaMovement().x < -0.25) {
                        e.setDeltaMovement(-0.25, e.getDeltaMovement().y, e.getDeltaMovement().z);
                    }
                    if (e.getDeltaMovement().y > 0.25) {
                        e.setDeltaMovement(e.getDeltaMovement().x, 0.25, e.getDeltaMovement().z);
                    }
                    if (e.getDeltaMovement().y < -0.25) {
                        e.setDeltaMovement(e.getDeltaMovement().x, -0.25, e.getDeltaMovement().z);
                    }
                    if (e.getDeltaMovement().z > 0.25) {
                        e.setDeltaMovement(e.getDeltaMovement().x, e.getDeltaMovement().y, 0.25);
                    }
                    if (e.getDeltaMovement().z < -0.25) {
                        e.setDeltaMovement(e.getDeltaMovement().x, e.getDeltaMovement().y, -0.25);
                    }
                    if (!player.level().isClientSide()) {
                        continue;
                    }
                    FXDispatcher.INSTANCE.crucibleBubble((float)e.getX() + (player.level().getRandom().nextFloat() - player.level().getRandom().nextFloat()) * 0.2f, (float)e.getY() + e.getBbHeight() + (player.level().getRandom().nextFloat() - player.level().getRandom().nextFloat()) * 0.2f, (float)e.getZ() + (player.level().getRandom().nextFloat() - player.level().getRandom().nextFloat()) * 0.2f, 0.33f, 0.33f, 1.0f);
                }
            }
        }
    }
    
    public void getSubItems(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == null /* SEARCH tab removed */) {
            ItemStack w1 = new ItemStack(this);
            EnumInfusionEnchantment.addInfusionEnchantment(w1, EnumInfusionEnchantment.BURROWING, 1);
            EnumInfusionEnchantment.addInfusionEnchantment(w1, EnumInfusionEnchantment.COLLECTOR, 1);
            items.add(w1);
        }
        else {
            super.getSubItems(tab, items);
        }
    }
}
