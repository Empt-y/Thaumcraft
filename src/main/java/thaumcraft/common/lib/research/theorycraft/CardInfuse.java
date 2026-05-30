package thaumcraft.common.lib.research.theorycraft;
import java.util.Random;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.common.items.consumables.ItemPhial;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;


public class CardInfuse extends TheorycraftCard
{
    Aspect aspect;
    ItemStack stack;
    static ItemStack[] options;
    
    public CardInfuse() {
        stack = ItemStack.EMPTY;
    }
    
    @Override
    public CompoundTag serialize() {
        CompoundTag nbt = super.serialize();
        nbt.putString("aspect", aspect.getTag());
        nbt.put("stack", stack.serializeNBT());
        return nbt;
    }
    
    @Override
    public void deserialize(CompoundTag nbt) {
        super.deserialize(nbt);
        aspect = Aspect.getAspect(nbt.getStringOr("aspect", ""));
        stack = new ItemStack(nbt.getCompoundOrEmpty("stack"));
    }
    
    @Override
    public boolean initialize(Player player, ResearchTableData data) {
        Random r = new Random(getSeed());
        int num = r.nextInt(Aspect.getCompoundAspects().size());
        aspect = Aspect.getCompoundAspects().get(num);
        stack = CardInfuse.options[r.nextInt(CardInfuse.options.length)].copy();
        return aspect != null && stack != null;
    }
    
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public String getResearchCategory() {
        return "INFUSION";
    }
    
    @Override
    public String getLocalizedName() {
        return Component.translatable("card.infuse.name").getString();
    }
    
    @Override
    public String getLocalizedText() {
        return Component.translatable("card.infuse.text", ChatFormatting.BOLD + aspect.getName() + ChatFormatting.RESET, stack.getDisplayName(), getVal()).getString();
    }
    
    private int getVal() {
        int q = 10;
        try {
            q += (int)(Math.sqrt(null /* getInfusionRecipeAspects removed */.visSize()) * 1.5);
        }
        catch (Exception ex) {}
        return q;
    }
    
    @Override
    public ItemStack[] getRequiredItems() {
        return new ItemStack[] { stack, ItemPhial.makeFilledPhial(aspect) };
    }
    
    @Override
    public boolean[] getRequiredItemsConsumed() {
        return new boolean[] { true, true };
    }
    
    @Override
    public boolean activate(Player player, ResearchTableData data) {
        data.addTotal(getResearchCategory(), getVal());
        return true;
    }
    
    static {
        CardInfuse.options = new ItemStack[] { new ItemStack(ItemsTC.alumentum), new ItemStack(BlocksTC.nitor.get(DyeColor.YELLOW)), new ItemStack(ItemsTC.amber), new ItemStack(ItemsTC.brain), new ItemStack(ItemsTC.fabric), new ItemStack(ItemsTC.salisMundus), new ItemStack(ItemsTC.ingots, 1), new ItemStack(ItemsTC.ingots, 1), new ItemStack(ItemsTC.quicksilver), new ItemStack(Items.GOLD_INGOT), new ItemStack(Items.IRON_INGOT), new ItemStack(Items.DIAMOND), new ItemStack(Items.EMERALD), new ItemStack(Items.BLAZE_ROD), new ItemStack(Items.LEATHER), new ItemStack(Blocks.WOOL), new ItemStack(Items.BRICK), new ItemStack(Items.ARROW), new ItemStack(Items.EGG), new ItemStack(Items.FEATHER), new ItemStack(Items.GLOWSTONE_DUST), new ItemStack(Items.REDSTONE), new ItemStack(Items.GHAST_TEAR), new ItemStack(Items.GUNPOWDER), new ItemStack(Items.BOW), new ItemStack(Items.GOLDEN_SWORD), new ItemStack(Items.IRON_SWORD), new ItemStack(Items.IRON_PICKAXE), new ItemStack(Items.GOLDEN_PICKAXE), new ItemStack(Items.QUARTZ), new ItemStack(Items.APPLE) };
    }
}
