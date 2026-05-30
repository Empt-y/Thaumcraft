package thaumcraft.common.items.armor;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
// import net.minecraft.world.item.Item /* ArmorItem removed */; // removed
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemRobeArmor extends net.minecraft.world.item.Item implements IVisDiscountGear, IThaumcraftItems
{
    public ItemRobeArmor(String name, Object /* nested class removed */ enumarmormaterial, int j, EquipmentSlot k) {
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
    
    @OnlyIn(Dist.CLIENT)
    public Object /* ItemMeshDefinition removed */ getCustomMesh() {
        return null;
    }
    
    public Object /* ModelResourceLocation removed */ getCustomModelResourceLocation(String variant) {
        return null /* removed */;
    }
    
    public boolean hasColor(ItemStack stack1) {
        return true;
    }
    
    public int getColor(ItemStack stack1) {
        CompoundTag nbttagcompound = stack1.get();
        if (nbttagcompound == null) {
            return 6961280;
        }
        CompoundTag nbttagcompound2 = nbttagcompound.getCompoundOrEmpty("display");
        return (nbttagcompound2 == null) ? 6961280 : (nbttagcompound2.contains("color") ? nbttagcompound2.getIntOr("color", 0) : 6961280);
    }
    
    public void removeColor(ItemStack stack1) {
        CompoundTag nbttagcompound = stack1.get();
        if (nbttagcompound != null) {
            CompoundTag nbttagcompound2 = nbttagcompound.getCompoundOrEmpty("display");
            if (nbttagcompound2.contains("color")) {
                nbttagcompound2.removeTag("color");
            }
        }
    }
    
    public void setColor(ItemStack stack1, int par2) {
        CompoundTag nbttagcompound = stack1.get();
        if (nbttagcompound == null) {
            nbttagcompound = new CompoundTag();
            stack1.put(nbttagcompound);
        }
        CompoundTag nbttagcompound2 = nbttagcompound.getCompoundOrEmpty("display");
        if (!nbttagcompound.contains("display")) {
            nbttagcompound.put("display", nbttagcompound2);
        }
        nbttagcompound2.putInt("color", par2);
    }
    
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (stack.getItem() == ItemsTC.clothChest || stack.getItem() == ItemsTC.clothBoots) {
            return (type == null) ? "thaumcraft:textures/entity/0 /* armor field removed *//robes_1.png" : "thaumcraft:textures/entity/0 /* armor field removed *//robes_1_overlay.png";
        }
        if (stack.getItem() == ItemsTC.clothLegs) {
            return (type == null) ? "thaumcraft:textures/entity/0 /* armor field removed *//robes_2.png" : "thaumcraft:textures/entity/0 /* armor field removed *//robes_2_overlay.png";
        }
        return (type == null) ? "thaumcraft:textures/entity/0 /* armor field removed *//robes_1.png" : "thaumcraft:textures/entity/0 /* armor field removed *//robes_1_overlay.png";
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.UNCOMMON;
    }
    
    public boolean isValidRepairItem(ItemStack repairItem) {
        return ItemStack.isSameItem(repairItem, new ItemStack(ItemsTC.fabric)) || false;
    }
    
    public int getVisDiscount(ItemStack stack, Player player) {
        return (armorType == EquipmentSlot.FEET) ? 2 : 3;
    }
    
    public InteractionResult onItemUseFirst(Player player, Level world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, InteractionHand hand) {
        BlockState bs = world.getBlockState(pos);
        if (bs.getBlock() == Blocks.CAULDRON) {
            BlockState blockState = bs;
            BlockCauldron cauldron = Blocks.CAULDRON;
            int i = (int)blockState.getValue(LiquidBlock.LEVEL);
            if (!world.isClientSide() && i > 0) {
                removeColor(player.getItemInHand(hand));
                Blocks.CAULDRON.setWaterLevel(world, pos, bs, i - 1);
                return InteractionResult.SUCCESS;
            }
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }
}
