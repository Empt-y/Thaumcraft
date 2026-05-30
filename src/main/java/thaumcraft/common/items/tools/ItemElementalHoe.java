package thaumcraft.common.items.tools;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.HoeItem; // OK
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;
import thaumcraft.common.lib.utils.Utils;


public class ItemElementalHoe extends HoeItem implements IThaumcraftItems
{
    public ItemElementalHoe(ToolMaterial enumtoolmaterial) {
        super(enumtoolmaterial, -3, -1.0f, new net.minecraft.world.item.Item.Properties());
        // Entity requires EntityType; use factory method
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
    
    public int getItemEnchantability() {
        return 5;
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.RARE;
    }
    
    public boolean isValidRepairItem(ItemStack repairItem) {
        return ItemStack.isSameItem(repairItem, new ItemStack(ItemsTC.ingots, 1)) || false;
    }
    
    public InteractionResult onItemUse(Player player, Level world, BlockPos pos, InteractionHand hand, Direction facing, float hitX, float hitY, float hitZ) {
        if (player.isCrouching()) {
            return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
        }
        boolean did = false;
        for (int xx = -1; xx <= 1; ++xx) {
            for (int zz = -1; zz <= 1; ++zz) {
                if (super.onItemUse(player, world, pos.offset(xx, 0, zz), hand, facing, hitX, hitY, hitZ) == InteractionResult.SUCCESS) {
                    if (world.isClientSide()) {
                        BlockPos pp = pos.offset(xx, 0, zz);
                        FXDispatcher.INSTANCE.drawBamf(pp.getX() + 0.5, pp.getY() + 1.01, pp.getZ() + 0.5, 0.3f, 0.12f, 0.1f, xx == 0 && zz == 0, false, Direction.UP);
                    }
                    if (!did) {
                        did = true;
                    }
                }
            }
        }
        if (!did) {
            did = Utils.useBonemealAtLoc(world, player, pos);
            if (did) {
                player.getItemInHand(hand).hurtAndBreak(3, player, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
                if (!world.isClientSide()) {
                    world.levelEvent(2005, pos, 0);
                }
                else {
                    FXDispatcher.INSTANCE.drawBlockMistParticles(pos, 4259648);
                }
            }
        }
        return InteractionResult.SUCCESS;
    }
}
