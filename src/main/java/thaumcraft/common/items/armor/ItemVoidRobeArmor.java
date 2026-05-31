package thaumcraft.common.items.armor;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.items.IGoggles;
import thaumcraft.api.items.IRevealer;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.renderers.models.gear.ModelRobe;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;


public class ItemVoidRobeArmor extends net.minecraft.world.item.Item implements IVisDiscountGear, IGoggles, IRevealer, IWarpingGear, IThaumcraftItems
{
    public ItemVoidRobeArmor(String name, Object enumarmormaterial, int j, EquipmentSlot k) {
        super(thaumcraft.common.config.TCItemInit.take());
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
    public Object getCustomModelResourceLocation(String variant) {
        return null;
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return (type == null) ? "thaumcraft:textures/entity/void_robe_armor_overlay.png" : "thaumcraft:textures/entity/void_robe_armor.png";
    }

    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.EPIC;
    }

    public boolean isValidRepairItem(ItemStack repairItem) {
        return ItemStack.isSameItem(repairItem, new ItemStack(ItemsTC.ingots, 1)) || false;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @javax.annotation.Nullable net.minecraft.world.entity.EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);
        if (!world.isClientSide() && (stack.getDamageValue() > 0) && entity.tickCount % 20 == 0 && entity instanceof LivingEntity) {
            // Self-repair: negative damage = heal durability. Use hurtAndBreak with -1 if on server.
            if (!world.isClientSide() && world instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                stack.hurtAndBreak(-1, serverLevel, null, item -> {});
            }
        }
    }

    public boolean showNodes(ItemStack itemstack, LivingEntity player) {
        EquipmentSlot type = EquipmentSlot.CHEST;
        return type == EquipmentSlot.HEAD;
    }

    public boolean showIngamePopups(ItemStack itemstack, LivingEntity player) {
        EquipmentSlot type = EquipmentSlot.CHEST;
        return type == EquipmentSlot.HEAD;
    }

    public int getVisDiscount(ItemStack stack, Player player) {
        return 5;
    }

    public boolean hasColor(ItemStack stack1) {
        return true;
    }

    public int getColor(ItemStack stack1) {
        CompoundTag nbttagcompound = stack1.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag();
        if (nbttagcompound.isEmpty()) {
            return 6961280;
        }
        CompoundTag nbttagcompound2 = nbttagcompound.getCompoundOrEmpty("display");
        return (nbttagcompound2 == null) ? 6961280 : (nbttagcompound2.contains("color") ? nbttagcompound2.getIntOr("color", 0) : 6961280);
    }

    public void removeColor(ItemStack stack1) {
        net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, stack1, nbt -> {
            CompoundTag display = nbt.read("display", net.minecraft.nbt.CompoundTag.CODEC).orElse(new net.minecraft.nbt.CompoundTag());
            display.remove("color");
            if (!display.isEmpty()) {
                nbt.put("display", display);
            }
        });
    }

    public void setColor(ItemStack stack1, int par2) {
        net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, stack1, nbt -> {
            CompoundTag display = nbt.read("display", net.minecraft.nbt.CompoundTag.CODEC).orElse(new net.minecraft.nbt.CompoundTag());
            display.putInt("color", par2);
            nbt.put("display", display);
        });
    }

    public void damageArmor(LivingEntity entity, ItemStack stack, DamageSource source, int damage, int slot) {
        if (!source.is(DamageTypeTags.IS_FALL)) {
            if (!entity.level().isClientSide() && entity.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                stack.hurtAndBreak(damage, serverLevel, null, item -> {});
            }
        }
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        InteractionHand hand = context.getHand();
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

    public int getWarp(ItemStack itemstack, Player player) {
        return 3;
    }
}
