package thaumcraft.common.items.armor;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemRobeArmor extends net.minecraft.world.item.Item implements IVisDiscountGear, IThaumcraftItems
{
    private final EquipmentSlot armorType;

    public ItemRobeArmor(String name, Object enumarmormaterial, int j, EquipmentSlot k) {
        super(new net.minecraft.world.item.Item.Properties());
        this.armorType = k;
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

    @OnlyIn(Dist.CLIENT)
    public Object getCustomMesh() {
        return null;
    }

    public Object getCustomModelResourceLocation(String variant) {
        return null;
    }

    public boolean hasColor(ItemStack stack1) {
        return true;
    }

    public int getColor(ItemStack stack1) {
        DyedItemColor dyed = stack1.get(DataComponents.DYED_COLOR);
        return dyed != null ? dyed.rgb() : 6961280;
    }

    public void removeColor(ItemStack stack1) {
        stack1.remove(DataComponents.DYED_COLOR);
    }

    public void setColor(ItemStack stack1, int par2) {
        stack1.set(DataComponents.DYED_COLOR, new DyedItemColor(par2));
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (stack.getItem() == ItemsTC.clothChest || stack.getItem() == ItemsTC.clothBoots) {
            return (type == null) ? "thaumcraft:textures/entity/armor/robes_1.png" : "thaumcraft:textures/entity/armor/robes_1_overlay.png";
        }
        if (stack.getItem() == ItemsTC.clothLegs) {
            return (type == null) ? "thaumcraft:textures/entity/armor/robes_2.png" : "thaumcraft:textures/entity/armor/robes_2_overlay.png";
        }
        return (type == null) ? "thaumcraft:textures/entity/armor/robes_1.png" : "thaumcraft:textures/entity/armor/robes_1_overlay.png";
    }

    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.UNCOMMON;
    }

    public boolean isValidRepairItem(ItemStack repairItem) {
        return ItemStack.isSameItem(repairItem, new ItemStack(ItemsTC.fabric));
    }

    public int getVisDiscount(ItemStack stack, Player player) {
        return (armorType == EquipmentSlot.FEET) ? 2 : 3;
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, net.minecraft.world.item.context.UseOnContext context) {
        Player player = context.getPlayer();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        InteractionHand hand = context.getHand();
        if (player == null) return InteractionResult.PASS;
        BlockState bs = world.getBlockState(pos);
        if (bs.getBlock() == Blocks.WATER_CAULDRON) {
            int level = bs.getValue(LayeredCauldronBlock.LEVEL);
            if (!world.isClientSide() && level > 0) {
                removeColor(player.getItemInHand(hand));
                LayeredCauldronBlock.lowerFillLevel(bs, world, pos);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
