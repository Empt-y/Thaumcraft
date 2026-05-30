package thaumcraft.common.items.tools;
import net.minecraft.world.item.ToolMaterial;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.world.level.block.Block;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
// import net.minecraft.world.item.Item /* DiggerItem removed */;; // broken import
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;


public class ItemPrimalCrusher extends net.minecraft.world.item.Item implements IWarpingGear, IThaumcraftItems
{
    private static final float EFFICIENCY = 8.0f;
    private static Set<Block> isEffective;

    public ItemPrimalCrusher() {
        super(new net.minecraft.world.item.Item.Properties());
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
    
    public boolean canHarvestBlock(BlockState p_150897_1_) {
        return true /* getMaterial check removed */ && true /* getMaterial check removed */ && true /* getMaterial check removed */;
    }
    
    public float getDestroySpeed(ItemStack stack, BlockState state) {
        return isEffective.contains(state.getBlock()) ? EFFICIENCY : super.getDestroySpeed(stack, state);
    }
    
    public Set<String> getToolClasses(ItemStack stack) {
        return ImmutableSet.of("shovel", "pickaxe");
    }
    
    private boolean isEffectiveAgainst(Block block) {
        return ItemPrimalCrusher.isEffective.contains(block);
    }
    
    public int getItemEnchantability() {
        return 20;
    }
    
    public int getWarp(ItemStack itemstack, Player player) {
        return 2;
    }
    
    public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @javax.annotation.Nullable net.minecraft.world.entity.EquipmentSlot p_77663_4_) {
        super.inventoryTick(stack, world, entity, p_77663_4_);
        if ((stack.getDamageValue() > 0) && entity != null && entity.tickCount % 20 == 0 && entity instanceof LivingEntity) {
            if (stack.isDamageableItem()) stack.setDamageValue(Math.max(0, stack.getDamageValue() - 1));
        }
    }
    
    public void getSubItems(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == null /* SEARCH tab removed */) {
            ItemStack w1 = new ItemStack(this);
            EnumInfusionEnchantment.addInfusionEnchantment(w1, EnumInfusionEnchantment.DESTRUCTIVE, 1);
            EnumInfusionEnchantment.addInfusionEnchantment(w1, EnumInfusionEnchantment.REFINING, 1);
            items.add(w1);
        }
    }
    
    static {
        isEffective = Sets.newHashSet(Blocks.COBBLESTONE, Blocks.STONE, Blocks.SANDSTONE, Blocks.MOSSY_COBBLESTONE,
            Blocks.IRON_ORE, Blocks.IRON_BLOCK, Blocks.COAL_ORE, Blocks.GOLD_BLOCK, Blocks.GOLD_ORE,
            Blocks.DIAMOND_ORE, Blocks.DIAMOND_BLOCK, Blocks.ICE, Blocks.NETHERRACK, Blocks.LAPIS_ORE,
            Blocks.LAPIS_BLOCK, Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE, Blocks.RAIL,
            Blocks.DETECTOR_RAIL, Blocks.POWERED_RAIL, Blocks.ACTIVATOR_RAIL, Blocks.GRASS_BLOCK,
            Blocks.DIRT, Blocks.SAND, Blocks.GRAVEL, Blocks.SNOW, Blocks.CLAY, Blocks.FARMLAND,
            Blocks.SOUL_SAND, Blocks.MYCELIUM,
            BlocksTC.taintCrust, BlocksTC.taintRock, BlocksTC.taintSoil, BlocksTC.taintFeature,
            BlocksTC.taintLog, BlocksTC.taintFibre, Blocks.OBSIDIAN);
    }
}
