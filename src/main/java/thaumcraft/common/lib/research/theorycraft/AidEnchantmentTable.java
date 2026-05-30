package thaumcraft.common.lib.research.theorycraft;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.research.theorycraft.ITheorycraftAid;
import thaumcraft.api.research.theorycraft.TheorycraftCard;


public class AidEnchantmentTable implements ITheorycraftAid
{
    @Override
    public Object getAidObject() {
        return new ItemStack(Blocks.ENCHANTING_TABLE);
    }
    
    @Override
    public Class<TheorycraftCard>[] getCards() {
        return new Class[] { CardEnchantment.class };
    }
}
