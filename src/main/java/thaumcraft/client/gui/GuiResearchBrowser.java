package thaumcraft.client.gui;
import java.util.ArrayList;
import java.util.LinkedList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;
import thaumcraft.api.research.ResearchEntry;


@OnlyIn(Dist.CLIENT)
public class GuiResearchBrowser extends Screen
{
    private static int guiBoundsLeft;
    private static int guiBoundsTop;
    private static int guiBoundsRight;
    private static int guiBoundsBottom;
    protected int mouseX;
    protected int mouseY;
    protected float screenZoom;
    protected double curMouseX;
    protected double curMouseY;
    protected double guiMapX;
    protected double guiMapY;
    protected double tempMapX;
    protected double tempMapY;
    private int isMouseButtonDown;
    public static double lastX;
    public static double lastY;
    GuiResearchBrowser instance;
    private int screenX;
    private int screenY;
    private int startX;
    private int startY;
    long t;
    private LinkedList<ResearchEntry> research;
    ResearchEntry currentHighlight;
    private Player player;
    long popuptime;
    String popupmessage;
    private EditBox searchField;
    private static boolean searching;
    private ArrayList<String> categoriesTC;
    private ArrayList<String> categoriesOther;
    static int catScrollPos;
    static int catScrollMax;
    public int addonShift;
    private ArrayList<String> invisible;
    ArrayList<Pair<String, Object>> searchResults;
    Identifier tx1;

    public GuiResearchBrowser() {
        super(Component.translatable("gui.researchbrowser"));
        mouseX = 0;
        mouseY = 0;
        screenZoom = 1.0f;
        isMouseButtonDown = 0;
        instance = null;
        startX = 16;
        startY = 16;
        t = 0L;
        research = new LinkedList<ResearchEntry>();
        currentHighlight = null;
        player = null;
        popuptime = 0L;
        popupmessage = "";
        categoriesTC = new ArrayList<String>();
        categoriesOther = new ArrayList<String>();
        addonShift = 0;
        invisible = new ArrayList<String>();
        searchResults = new ArrayList<>();
        tx1 = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_research_browser.png");
        double lx = GuiResearchBrowser.lastX;
        tempMapX = lx;
        guiMapX = lx;
        curMouseX = lx;
        double ly = GuiResearchBrowser.lastY;
        tempMapY = ly;
        guiMapY = ly;
        curMouseY = ly;
        player = Minecraft.getInstance().player;
        instance = this;
    }

    public GuiResearchBrowser(double x, double y) {
        super(Component.translatable("gui.researchbrowser"));
        mouseX = 0;
        mouseY = 0;
        screenZoom = 1.0f;
        isMouseButtonDown = 0;
        startX = 16;
        startY = 16;
        t = 0L;
        research = new LinkedList<ResearchEntry>();
        currentHighlight = null;
        player = null;
        popuptime = 0L;
        popupmessage = "";
        categoriesTC = new ArrayList<String>();
        categoriesOther = new ArrayList<String>();
        addonShift = 0;
        invisible = new ArrayList<String>();
        searchResults = new ArrayList<>();
        tx1 = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_research_browser.png");
        guiMapX = x;
        guiMapY = y;
        tempMapX = x;
        tempMapY = y;
        curMouseX = x;
        curMouseY = y;
        player = Minecraft.getInstance().player;
        instance = this;
    }

    public static void drawResearchIcon(ResearchEntry iconResearch, int iconX, int iconY, float zLevel, boolean bw) {
        // rendering stub
    }

    public static void drawForbidden(double x, double y) {
        // rendering stub
    }
}
