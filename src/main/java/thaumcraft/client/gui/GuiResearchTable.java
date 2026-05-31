package thaumcraft.client.gui;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.client.gui.plugins.GuiImageButton;
import thaumcraft.common.container.ContainerResearchTable;
import thaumcraft.common.tiles.crafting.TileResearchTable;


@OnlyIn(Dist.CLIENT)
public class GuiResearchTable extends AbstractContainerScreen<ContainerResearchTable>
{
    private float xSize_lo;
    private float ySize_lo;
    private TileResearchTable table;
    private String username;
    Player player;
    Identifier txBackground;
    Identifier txBase;
    Identifier txPaper;
    Identifier txPaperGilded;
    Identifier txQuestion;
    ResearchTableData.CardChoice lastDraw;
    float[] cardHover;
    float[] cardZoomOut;
    float[] cardZoomIn;
    boolean[] cardActive;
    boolean cardSelected;
    public HashMap<String, Integer> tempCatTotals;
    long nexCatCheck;
    long nextCheck;
    int dummyInspirationStart;
    Set<String> currentAids;
    Set<String> selectedAids;
    GuiImageButton buttonCreate;
    GuiImageButton buttonComplete;
    GuiImageButton buttonScrap;
    public ArrayList<ResearchTableData.CardChoice> cardChoices;

    public GuiResearchTable(ContainerResearchTable menu, Inventory inv, Component title) {
        super(menu, inv, title, 255, 255);
        txBackground = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_research_table.png");
        txBase = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_base.png");
        txPaper = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/paper.png");
        txPaperGilded = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/papergilded.png");
        txQuestion = Identifier.fromNamespaceAndPath("thaumcraft", "textures/aspects/_unknown.png");
        cardHover = new float[] { 0.0f, 0.0f, 0.0f };
        cardZoomOut = new float[] { 0.0f, 0.0f, 0.0f };
        cardZoomIn = new float[] { 0.0f, 0.0f, 0.0f };
        cardActive = new boolean[] { true, true, true };
        cardSelected = false;
        tempCatTotals = new HashMap<String, Integer>();
        nexCatCheck = 0L;
        nextCheck = 0L;
        dummyInspirationStart = 0;
        currentAids = new HashSet<String>();
        selectedAids = new HashSet<String>();
        cardChoices = new ArrayList<ResearchTableData.CardChoice>();
        table = menu.tileEntity;
        this.player = inv.player;
        username = inv.player.getName().getString();
        if (table.data != null) {
            for (String cat : table.data.categoryTotals.keySet()) {
                tempCatTotals.put(cat, table.data.categoryTotals.get(cat));
            }
            syncFromTableChoices();
            lastDraw = table.data.lastDraw;
        }
    }

    private void syncFromTableChoices() {
        cardChoices.clear();
        if (table.data != null) {
            for (ResearchTableData.CardChoice cc : table.data.cardChoices) {
                cardChoices.add(cc);
            }
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, txBackground, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}
