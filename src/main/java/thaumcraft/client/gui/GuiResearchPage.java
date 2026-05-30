package thaumcraft.client.gui;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.research.ResearchEntry;


@OnlyIn(Dist.CLIENT)
public class GuiResearchPage extends Screen
{
    public static LinkedList<Identifier> history = new LinkedList<>();
    static Identifier shownRecipe;
    static boolean cycleMultiblockLines;
    public HashMap<Integer, String> keyCache;

    protected int paneWidth = 256;
    protected int paneHeight = 181;
    protected double guiMapX;
    protected double guiMapY;

    public GuiResearchPage(ResearchEntry research, Identifier recipe, double x, double y) {
        super(Component.translatable("gui.researchpage"));
        this.guiMapX = x;
        this.guiMapY = y;
        this.keyCache = new HashMap<>();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
