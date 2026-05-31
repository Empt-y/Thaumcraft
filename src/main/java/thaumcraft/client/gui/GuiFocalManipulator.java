package thaumcraft.client.gui;
import java.text.DecimalFormat;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.client.gui.plugins.GuiImageButton;
import thaumcraft.client.gui.plugins.GuiSliderTC;
import thaumcraft.common.container.ContainerFocalManipulator;
import thaumcraft.common.tiles.crafting.TileFocalManipulator;


@OnlyIn(Dist.CLIENT)
public class GuiFocalManipulator extends AbstractContainerScreen<ContainerFocalManipulator>
{
    private TileFocalManipulator table;
    private float xSize_lo;
    private float ySize_lo;
    private int isMouseButtonDown;
    protected int mouseX;
    protected int mouseY;
    protected double curMouseX;
    protected double curMouseY;
    Identifier tex;
    Identifier tex2;
    Identifier tex3;
    Identifier texbase;
    GuiImageButton buttonConfirm;
    GuiSliderTC scrollbarParts;
    GuiSliderTC scrollbarMainSide;
    GuiSliderTC scrollbarMainBottom;
    private EditBox nameField;
    int totalComplexity;
    int maxComplexity;
    int lastNodeHover;
    DecimalFormat myFormatter;
    ArrayList<String> shownParts;
    int partsStart;
    ItemStack[] components;
    boolean valid;
    static Identifier iMedium;
    static Identifier iEffect;
    private int nodeID;
    int sMinX;
    int sMinY;
    int sMaxX;
    int sMaxY;
    int selectedNode;
    float costCast;
    int costXp;
    int costVis;
    int scrollX;
    int scrollY;

    public GuiFocalManipulator(ContainerFocalManipulator menu, Inventory inv, Component title) {
        super(menu, inv, title, 231, 231);
        this.table = menu.getTile();
        isMouseButtonDown = 0;
        mouseX = 0;
        mouseY = 0;
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_wandtable.png");
        tex2 = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_wandtable2.png");
        tex3 = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_wandtable3.png");
        texbase = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_base.png");
        totalComplexity = 0;
        maxComplexity = 0;
        lastNodeHover = -1;
        myFormatter = new DecimalFormat("#######.##");
        shownParts = new ArrayList<String>();
        partsStart = 0;
        components = null;
        valid = false;
        nodeID = 0;
        sMinX = 0;
        sMinY = 0;
        sMaxX = 0;
        sMaxY = 0;
        selectedNode = -1;
        costCast = 0.0f;
        costXp = 0;
        costVis = 0;
        scrollX = 0;
        scrollY = 0;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, tex, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}
