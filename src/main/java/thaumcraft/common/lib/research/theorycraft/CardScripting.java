package thaumcraft.common.lib.research.theorycraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.common.tiles.crafting.TileResearchTable;


public class CardScripting extends TheorycraftCard
{
    @Override
    public int getInspirationCost() {
        return 1;
    }
    
    @Override
    public String getResearchCategory() {
        return "GOLEMANCY";
    }
    
    @Override
    public String getLocalizedName() {
        return Component.translatable("card.scripting.name").getString();
    }
    
    @Override
    public String getLocalizedText() {
        return Component.translatable("card.scripting.text").getString();
    }
    
    @Override
    public boolean activate(Player player, ResearchTableData data) {
        if (data.table != null && ((TileResearchTable)data.table).getItem(0) != null && ((TileResearchTable)data.table).getItem(0).getDamageValue() < ((TileResearchTable)data.table).getItem(0).getMaxDamage() && ((TileResearchTable)data.table).getItem(1) != null) {
            ((TileResearchTable)data.table).consumeInkFromTable();
            ((TileResearchTable)data.table).consumepaperFromTable();
            data.addTotal(getResearchCategory(), 25);
            return true;
        }
        return false;
    }
}
