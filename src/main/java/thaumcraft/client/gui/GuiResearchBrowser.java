package thaumcraft.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketSyncProgressToServer;
import thaumcraft.common.lib.network.playerdata.PacketSyncResearchFlagsToServer;
import thaumcraft.common.lib.research.ResearchManager;


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
    static String selectedCategory;
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

    static {
        GuiResearchBrowser.lastX = -9999.0;
        GuiResearchBrowser.lastY = -9999.0;
        GuiResearchBrowser.selectedCategory = null;
        GuiResearchBrowser.searching = false;
        GuiResearchBrowser.catScrollPos = 0;
        GuiResearchBrowser.catScrollMax = 0;
    }

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

    @Override
    protected void init() {
        updateResearch();
    }

    private void updateResearch() {
        clearWidgets();

        // Search button
        addRenderableWidget(Button.builder(
                Component.translatable("tc.search"),
                b -> {
                    GuiResearchBrowser.selectedCategory = "";
                    GuiResearchBrowser.searching = true;
                    searchField.setVisible(true);
                    searchField.setCanLoseFocus(false);
                    searchField.setFocused(true);
                    searchField.setValue("");
                })
            .bounds(1, height - 17, 16, 16)
            .build());

        searchField = new EditBox(font, 20, 20, 89, font.lineHeight + 2,
                Component.literal(""));
        searchField.setMaxLength(15);
        searchField.setVisible(false);
        if (GuiResearchBrowser.searching) {
            searchField.setVisible(true);
            searchField.setCanLoseFocus(false);
            searchField.setFocused(true);
            searchField.setValue("");
        }
        addRenderableWidget(searchField);

        screenX = width - 32;
        screenY = height - 32;
        research.clear();

        if (GuiResearchBrowser.selectedCategory == null) {
            Collection<String> cats = ResearchCategories.researchCategories.keySet();
            if (!cats.isEmpty()) {
                GuiResearchBrowser.selectedCategory = cats.iterator().next();
            }
        }

        int limit = (int) Math.floor((screenY - 28) / 24.0f);
        addonShift = 0;
        int count = 0;
        categoriesTC.clear();
        categoriesOther.clear();

        outer:
        for (String rcl : ResearchCategories.researchCategories.keySet()) {
            ResearchCategory rc = ResearchCategories.getResearchCategory(rcl);
            if (rc.researchKey != null && !ThaumcraftCapabilities.knowsResearchStrict(player, rc.researchKey)) {
                continue;
            }

            // Compute completion percentage
            int rt = 0, rco = 0;
            for (ResearchEntry res : rc.research.values()) {
                if (res.hasMeta(ResearchEntry.EnumResearchMeta.AUTOUNLOCK)) continue;
                ++rt;
                if (ThaumcraftCapabilities.knowsResearch(player, res.getKey())) ++rco;
            }
            int completion = (rt > 0) ? (int)(rco / (float) rt * 100.0f) : 0;

            // Check if this is a TC core category
            for (String tcc : ConfigResearch.TCCategories) {
                if (tcc.equals(rcl)) {
                    categoriesTC.add(rcl);
                    final String catKey = rcl;
                    final String catName = rcl;
                    final boolean isLeft = true;
                    final int tcIdx = categoriesTC.size();
                    addRenderableWidget(Button.builder(
                            Component.translatable("tc.research_category." + rcl),
                            b -> switchCategory(catKey))
                        .bounds(1, 10 + tcIdx * 24, 16, 16)
                        .build());
                    continue outer;
                }
            }

            ++count;
            if (count > limit + GuiResearchBrowser.catScrollPos) continue;
            if (count - 1 < GuiResearchBrowser.catScrollPos) continue;

            categoriesOther.add(rcl);
            final String catKey = rcl;
            final int otherIdx = categoriesOther.size();
            addRenderableWidget(Button.builder(
                    Component.translatable("tc.research_category." + rcl),
                    b -> switchCategory(catKey))
                .bounds(width - 17, 10 + otherIdx * 24, 16, 16)
                .build());
        }

        // Scroll buttons for addon categories
        if (count > limit || count < GuiResearchBrowser.catScrollPos) {
            addonShift = (screenY - 28) % 24 / 2;
            addRenderableWidget(Button.builder(Component.literal("^"), b -> {
                if (GuiResearchBrowser.catScrollPos > 0) {
                    --GuiResearchBrowser.catScrollPos;
                    updateResearch();
                }
            }).bounds(width - 14, 20, 10, 11).build());
            addRenderableWidget(Button.builder(Component.literal("v"), b -> {
                if (GuiResearchBrowser.catScrollPos < GuiResearchBrowser.catScrollMax) {
                    ++GuiResearchBrowser.catScrollPos;
                    updateResearch();
                }
            }).bounds(width - 14, screenY + 1, 10, 11).build());
        }
        GuiResearchBrowser.catScrollMax = count - limit;

        // Load research entries for selected category
        if (GuiResearchBrowser.selectedCategory != null && !GuiResearchBrowser.selectedCategory.isEmpty()) {
            ResearchCategory selCat = ResearchCategories.getResearchCategory(GuiResearchBrowser.selectedCategory);
            if (selCat != null) {
                for (ResearchEntry res : selCat.research.values()) {
                    research.add(res);
                }
            }
        }

        // Compute bounds
        guiBoundsLeft = 99999;
        guiBoundsTop = 99999;
        guiBoundsRight = -99999;
        guiBoundsBottom = -99999;
        for (ResearchEntry res : research) {
            if (res != null && isVisible(res)) {
                int col24 = res.getDisplayColumn() * 24;
                int row24 = res.getDisplayRow() * 24;
                if (col24 - screenX + 48 < guiBoundsLeft)  guiBoundsLeft  = col24 - screenX + 48;
                if (col24 - 24              > guiBoundsRight) guiBoundsRight = col24 - 24;
                if (row24 - screenY + 48 < guiBoundsTop)   guiBoundsTop   = row24 - screenY + 48;
                if (row24 - 24             > guiBoundsBottom) guiBoundsBottom = row24 - 24;
            }
        }
        if (guiBoundsLeft == 99999)  guiBoundsLeft  = 0;
        if (guiBoundsTop == 99999)   guiBoundsTop   = 0;
        if (guiBoundsRight == -99999)  guiBoundsRight  = 200;
        if (guiBoundsBottom == -99999) guiBoundsBottom = 200;

        // Centre map if not previously saved
        if (GuiResearchBrowser.lastX == -9999.0 || guiMapX > guiBoundsRight || guiMapX < guiBoundsLeft) {
            double n = (guiBoundsLeft + guiBoundsRight) / 2.0;
            tempMapX = n; guiMapX = n;
        }
        if (GuiResearchBrowser.lastY == -9999.0 || guiMapY > guiBoundsBottom || guiMapY < guiBoundsTop) {
            double n2 = (guiBoundsTop + guiBoundsBottom) / 2.0;
            tempMapY = n2; guiMapY = n2;
        }
    }

    private void switchCategory(String key) {
        GuiResearchBrowser.searching = false;
        if (searchField != null) {
            searchField.setVisible(false);
            searchField.setCanLoseFocus(true);
            searchField.setFocused(false);
        }
        GuiResearchBrowser.selectedCategory = key;
        updateResearch();
        double n  = (guiBoundsLeft + guiBoundsRight) / 2.0;
        double n2 = (guiBoundsTop  + guiBoundsBottom) / 2.0;
        tempMapX = n;  guiMapX = n;
        tempMapY = n2; guiMapY = n2;
    }

    private boolean isVisible(ResearchEntry res) {
        if (ThaumcraftCapabilities.knowsResearch(player, res.getKey())) return true;
        if (invisible.contains(res.getKey())) return false;
        if (res.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN) && !canUnlockResearch(res)) return false;
        if (res.getParents() == null && res.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN)) return false;
        if (res.getParents() != null) {
            for (String r : res.getParents()) {
                ResearchEntry ri = ResearchCategories.getResearch(r);
                if (ri != null && !isVisible(ri)) {
                    invisible.add(r);
                    return false;
                }
            }
        }
        return true;
    }

    private boolean canUnlockResearch(ResearchEntry res) {
        return ResearchManager.doesPlayerHaveRequisites(player, res.getKey());
    }

    @Override
    public void onClose() {
        GuiResearchBrowser.lastX = guiMapX;
        GuiResearchBrowser.lastY = guiMapY;
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        // Smooth-scroll towards target position
        curMouseX = guiMapX;
        curMouseY = guiMapY;
        double vx = tempMapX - guiMapX;
        double vy = tempMapY - guiMapY;
        if (vx * vx + vy * vy < 4.0) {
            guiMapX += vx;
            guiMapY += vy;
        } else {
            guiMapX += vx * 0.85;
            guiMapY += vy * 0.85;
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (GuiResearchBrowser.searching && searchField.isFocused()) {
            // Let the editbox handle the event first
            if (searchField.keyPressed(event)) {
                return true;
            }
        }
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            onClose();
            return true;
        }
        // Check for inventory key
        if (this.minecraft != null && this.minecraft.options.keyInventory.matches(event)) {
            onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        if (GuiResearchBrowser.searching && searchField.isFocused()) {
            if (searchField.charTyped(event)) {
                return true;
            }
        }
        return super.charTyped(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (!GuiResearchBrowser.searching) {
            if (scrollY < 0) {
                screenZoom += 0.25f;
            } else if (scrollY > 0) {
                screenZoom -= 0.25f;
            }
            screenZoom = Mth.clamp(screenZoom, 1.0f, 2.0f);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (!GuiResearchBrowser.searching && event.button() == 0) {
            double mx = event.x(), my = event.y();
            if (mx >= startX && mx < startX + screenX && my >= startY && my < startY + screenY) {
                guiMapX -= dragX * screenZoom;
                guiMapY -= dragY * screenZoom;
                clampMap();
                curMouseX = guiMapX;
                tempMapX  = guiMapX;
                curMouseY = guiMapY;
                tempMapY  = guiMapY;
            }
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    private void clampMap() {
        if (tempMapX < guiBoundsLeft  * (double) screenZoom) tempMapX = guiBoundsLeft  * (double) screenZoom;
        if (tempMapY < guiBoundsTop   * (double) screenZoom) tempMapY = guiBoundsTop   * (double) screenZoom;
        if (tempMapX >= guiBoundsRight  * (double) screenZoom) tempMapX = guiBoundsRight  * screenZoom - 1.0;
        if (tempMapY >= guiBoundsBottom * (double) screenZoom) tempMapY = guiBoundsBottom * screenZoom - 1.0;
        guiMapX = tempMapX;
        guiMapY = tempMapY;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        popuptime = System.currentTimeMillis() - 1L;
        double mx = event.x(), my = event.y();

        // Category tab click handling
        if (!GuiResearchBrowser.searching) {
            int tcIdx = 0;
            for (String rcl : categoriesTC) {
                ++tcIdx;
                int tx = 1, ty = 10 + tcIdx * 24;
                if (mx >= tx - 3 && my >= ty - 3 && mx < tx + 19 && my < ty + 19) {
                    GuiResearchBrowser.selectedCategory = rcl;
                    updateResearch();
                    minecraft.player.playSound(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.value(), 0.4f, 1.0f);
                    return true;
                }
            }
            int otherIdx = 0;
            for (String rcl : categoriesOther) {
                ++otherIdx;
                int tx2 = width - 17, ty2 = 10 + otherIdx * 24;
                if (mx >= tx2 - 3 && my >= ty2 - 3 && mx < tx2 + 19 && my < ty2 + 19) {
                    GuiResearchBrowser.selectedCategory = rcl;
                    updateResearch();
                    minecraft.player.playSound(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.value(), 0.4f, 1.0f);
                    return true;
                }
            }
        }

        if (!GuiResearchBrowser.searching && currentHighlight != null) {
            if (!ThaumcraftCapabilities.knowsResearch(player, currentHighlight.getKey())
                    && canUnlockResearch(currentHighlight)) {
                PacketHandler.sendToServer(new PacketSyncProgressToServer(currentHighlight.getKey(), true));
                minecraft.setScreen(new GuiResearchPage(currentHighlight, null, guiMapX, guiMapY));
                return true;
            } else if (ThaumcraftCapabilities.knowsResearch(player, currentHighlight.getKey())) {
                ThaumcraftCapabilities.getKnowledge(player).clearResearchFlag(
                        currentHighlight.getKey(), IPlayerKnowledge.EnumResearchFlag.RESEARCH);
                ThaumcraftCapabilities.getKnowledge(player).clearResearchFlag(
                        currentHighlight.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE);
                PacketHandler.sendToServer(new PacketSyncResearchFlagsToServer(
                        minecraft.player, currentHighlight.getKey()));
                minecraft.setScreen(new GuiResearchPage(currentHighlight, null, guiMapX, guiMapY));
                return true;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    // -----------------------------------------------------------------------
    // Rendering
    // -----------------------------------------------------------------------

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mx, int my, float partialTick) {
        // solid dark background
        graphics.fill(0, 0, width, height, 0xFF101010);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mx, int my, float partialTick) {
        t = System.nanoTime() / 50000000L;

        // Interpolated scroll position
        int locX = Mth.floor(curMouseX + (guiMapX - curMouseX) * partialTick);
        int locY = Mth.floor(curMouseY + (guiMapY - curMouseY) * partialTick);
        locX = Mth.clamp(locX,
                (int)(guiBoundsLeft  * screenZoom),
                (int)(guiBoundsRight  * screenZoom - 1.0f));
        locY = Mth.clamp(locY,
                (int)(guiBoundsTop   * screenZoom),
                (int)(guiBoundsBottom * screenZoom - 1.0f));

        // Background texture (category-specific)
        if (GuiResearchBrowser.selectedCategory != null && !GuiResearchBrowser.selectedCategory.isEmpty()) {
            ResearchCategory rc = ResearchCategories.getResearchCategory(GuiResearchBrowser.selectedCategory);
            if (rc != null && rc.background != null) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, rc.background,
                        startX - 2, startY - 2, (float)(locX / 2.0),
                        (float)(locY / 2.0), screenX + 4, screenY + 4, 1024, 1024);
            }
        }

        // Clip rendering to the research area (software scissor via fill borders)
        graphics.pose().pushMatrix();
        graphics.pose().scale(1.0f / screenZoom, 1.0f / screenZoom);

        drawResearchConnections(graphics, locX, locY);
        drawResearchIcons(graphics, mx, my, locX, locY);

        graphics.pose().popMatrix();

        // Border frames from browser texture
        drawBorder(graphics);

        // Draw category tab icons
        drawCategoryTabs(graphics, mx, my);

        // Tooltip for hovered research
        if (currentHighlight != null) {
            drawResearchTooltip(graphics, currentHighlight, mx, my);
        }

        // Call super so button widgets are rendered
        super.extractRenderState(graphics, mx, my, partialTick);
    }

    private void drawResearchConnections(GuiGraphicsExtractor graphics, int locX, int locY) {
        if (GuiResearchBrowser.selectedCategory == null) return;
        IPlayerKnowledge know = ThaumcraftCapabilities.getKnowledge(player);
        if (know == null || know.getResearchList() == null) return;

        for (ResearchEntry source : research) {
            if (source.getParents() != null) {
                for (int a = 0; a < source.getParents().length; ++a) {
                    String parentKey = source.getParentsClean() != null ? source.getParentsClean()[a] : null;
                    if (parentKey == null) continue;
                    ResearchEntry parent = ResearchCategories.getResearch(parentKey);
                    if (parent == null || !parent.getCategory().equals(GuiResearchBrowser.selectedCategory)) continue;
                    if (parent.getSiblings() != null && Arrays.asList(parent.getSiblings()).contains(source.getKey())) continue;

                    boolean knowsParent = ThaumcraftCapabilities.knowsResearchStrict(player, source.getParents()[a]);
                    boolean visible = isVisible(source) && !source.getParents()[a].startsWith("~");
                    if (visible) {
                        int color = knowsParent ? 0xAA999999 : (isVisible(parent) ? 0xAA333333 : 0);
                        if (color != 0) {
                            drawConnectionLine(graphics,
                                source.getDisplayColumn(), source.getDisplayRow(),
                                parent.getDisplayColumn(), parent.getDisplayRow(),
                                color, locX, locY);
                        }
                    }
                }
            }
            if (source.getSiblings() != null) {
                for (int a = 0; a < source.getSiblings().length; ++a) {
                    String sibKey = source.getSiblings()[a];
                    if (sibKey == null || sibKey.startsWith("~")) continue;
                    ResearchEntry sibling = ResearchCategories.getResearch(sibKey);
                    if (sibling == null || !sibling.getCategory().equals(GuiResearchBrowser.selectedCategory)) continue;
                    if (!isVisible(source)) continue;
                    boolean knowsSibling = ThaumcraftCapabilities.knowsResearchStrict(player, sibling.getKey());
                    int color = knowsSibling ? 0xAA4D4D66 : (isVisible(sibling) ? 0xAA303040 : 0);
                    if (color != 0) {
                        drawConnectionLine(graphics,
                            sibling.getDisplayColumn(), sibling.getDisplayRow(),
                            source.getDisplayColumn(), source.getDisplayRow(),
                            color, locX, locY);
                    }
                }
            }
        }
    }

    /** Draw a simple line between two grid positions as small filled rectangles. */
    private void drawConnectionLine(GuiGraphicsExtractor graphics,
            int col1, int row1, int col2, int row2,
            int color, int locX, int locY) {
        int x1 = (int)((startX + col1 * 24 - locX + 8) * screenZoom);
        int y1 = (int)((startY + row1 * 24 - locY + 8) * screenZoom);
        int x2 = (int)((startX + col2 * 24 - locX + 8) * screenZoom);
        int y2 = (int)((startY + row2 * 24 - locY + 8) * screenZoom);
        // Walk from (x1,y1) to (x2,y2) in grid steps and draw 2x2 dots
        int dx = x2 - x1;
        int dy = y2 - y1;
        int steps = Math.max(Math.abs(dx), Math.abs(dy));
        if (steps == 0) return;
        for (int i = 0; i <= steps; i++) {
            int px = x1 + dx * i / steps;
            int py = y1 + dy * i / steps;
            graphics.fill(px, py, px + 2, py + 2, color);
        }
    }

    private void drawResearchIcons(GuiGraphicsExtractor graphics, int mx, int my, int locX, int locY) {
        currentHighlight = null;
        IPlayerKnowledge know = ThaumcraftCapabilities.getKnowledge(player);
        if (know == null) return;

        for (ResearchEntry iconResearch : research) {
            if (!isVisible(iconResearch)) continue;

            int var25 = (int)(iconResearch.getDisplayColumn() * 24 * screenZoom) - locX;
            int var26 = (int)(iconResearch.getDisplayRow()    * 24 * screenZoom) - locY;

            if (var25 < -24 || var26 < -24 || var25 > screenX * screenZoom || var26 > screenY * screenZoom) continue;

            int iconX = startX + var25;
            int iconY = startY + var26;

            boolean complete = know.isResearchComplete(iconResearch.getKey());
            boolean canUnlock = canUnlockResearch(iconResearch);

            // Draw background badge from browser texture
            // Normal badge: u=80,v=48, size 32x32 (centred on icon, so offset -8)
            int badgeU = 80, badgeV = 48;
            if (iconResearch.hasMeta(ResearchEntry.EnumResearchMeta.HIDDEN)) badgeV += 32;
            if (iconResearch.hasMeta(ResearchEntry.EnumResearchMeta.HEX))    badgeU += 32;
            int badgeAlpha = complete ? 0xFF : (canUnlock ? 0xCC : 0x4C);
            int tintColor = (badgeAlpha << 24) | 0xFFFFFF;
            graphics.blit(RenderPipelines.GUI_TEXTURED, tx1,
                    iconX - 8, iconY - 8, (float) badgeU, (float) badgeV, 32, 32, 256, 256);

            // Draw research icon
            drawResearchIcon(iconResearch, iconX, iconY, 0.0f, !canUnlock);

            // Hover detection (in unscaled space)
            if (mx >= startX && my >= startY && mx < startX + screenX && my < startY + screenY) {
                if (mx >= iconX / screenZoom - 2 && mx <= (iconX + 18) / screenZoom
                        && my >= iconY / screenZoom - 2 && my <= (iconY + 18) / screenZoom) {
                    currentHighlight = iconResearch;
                }
            }
        }
    }

    private void drawBorder(GuiGraphicsExtractor graphics) {
        // Top and bottom horizontal borders
        for (int c = 16; c < width - 16; c += 64) {
            int p = Math.min(64, width - 16 - c);
            if (p > 0) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, tx1, c, -2,    48, 13, p, 22, 256, 256);
                graphics.blit(RenderPipelines.GUI_TEXTURED, tx1, c, height - 20, 48, 13, p, 22, 256, 256);
            }
        }
        // Left and right vertical borders
        for (int c = 16; c < height - 16; c += 64) {
            int p = Math.min(64, height - 16 - c);
            if (p > 0) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, tx1, -2,         c, 13, 48, 22, p, 256, 256);
                graphics.blit(RenderPipelines.GUI_TEXTURED, tx1, width - 20, c, 13, 48, 22, p, 256, 256);
            }
        }
        // Corner pieces
        graphics.blit(RenderPipelines.GUI_TEXTURED, tx1, -2,         -2,          13, 13, 22, 22, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, tx1, -2,         height - 20, 13, 13, 22, 22, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, tx1, width - 20, -2,          13, 13, 22, 22, 256, 256);
        graphics.blit(RenderPipelines.GUI_TEXTURED, tx1, width - 20, height - 20, 13, 13, 22, 22, 256, 256);
    }

    private void drawCategoryTabs(GuiGraphicsExtractor graphics, int mx, int my) {
        int tcIdx = 0;
        for (String rcl : categoriesTC) {
            ++tcIdx;
            ResearchCategory rc = ResearchCategories.getResearchCategory(rcl);
            if (rc == null) continue;
            int tx = 1, ty = 10 + tcIdx * 24;
            boolean selected = rcl.equals(GuiResearchBrowser.selectedCategory);
            // Badge background
            graphics.blit(RenderPipelines.GUI_TEXTURED, tx1, tx - 3, ty - 3, 13, 13, 22, 22, 256, 256);
            // Category icon (textures are 32×32; draw full icon at 16×16 on screen)
            if (rc.icon != null) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, rc.icon, tx, ty, 0, 0, 16, 16, 32, 32);
            }
            // Selected highlight
            if (selected) {
                graphics.fill(tx - 1, ty - 1, tx + 17, ty + 17, 0x44FFFFFF);
            }
            // Tooltip on hover
            if (mx >= tx - 3 && my >= ty - 3 && mx < tx + 19 && my < ty + 19) {
                String label = net.minecraft.client.resources.language.I18n.get("tc.research_category." + rcl);
                graphics.text(font, label, tx + 22, ty + 4, selected ? 0xFFAAEEEE : 0xFFFFFFFF);
            }
        }

        int otherIdx = 0;
        for (String rcl : categoriesOther) {
            ++otherIdx;
            ResearchCategory rc = ResearchCategories.getResearchCategory(rcl);
            if (rc == null) continue;
            int tx = width - 17, ty = 10 + otherIdx * 24;
            boolean selected = rcl.equals(GuiResearchBrowser.selectedCategory);
            graphics.blit(RenderPipelines.GUI_TEXTURED, tx1, tx - 3, ty - 3, 13, 13, 22, 22, 256, 256);
            if (rc.icon != null) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, rc.icon, tx, ty, 0, 0, 16, 16, 32, 32);
            }
            if (selected) {
                graphics.fill(tx - 1, ty - 1, tx + 17, ty + 17, 0x44FFFFFF);
            }
            if (mx >= tx - 3 && my >= ty - 3 && mx < tx + 19 && my < ty + 19) {
                String label = net.minecraft.client.resources.language.I18n.get("tc.research_category." + rcl);
                int labelX = width - 17 - font.width(label) - 4;
                graphics.text(font, label, labelX, ty + 4, selected ? 0xFFAAEEEE : 0xFFFFFFFF);
            }
        }
    }

    private void drawResearchTooltip(GuiGraphicsExtractor graphics, ResearchEntry entry, int mx, int my) {
        List<Component> lines = new ArrayList<>();
        lines.add(Component.literal("§6" + entry.getLocalizedName()));
        IPlayerKnowledge know = ThaumcraftCapabilities.getKnowledge(player);
        if (canUnlockResearch(entry)) {
            if (know != null && !know.isResearchComplete(entry.getKey()) && entry.getStages() != null) {
                int stage = know.getResearchStage(entry.getKey());
                if (stage > 0) {
                    lines.add(Component.literal("§b" + net.minecraft.client.resources.language.I18n.get("tc.research.stage") + " " + stage + "/" + entry.getStages().length));
                } else {
                    lines.add(Component.literal("§a" + net.minecraft.client.resources.language.I18n.get("tc.research.begin")));
                }
            }
        } else {
            lines.add(Component.literal("§c" + net.minecraft.client.resources.language.I18n.get("tc.researchmissing")));
        }
        graphics.setTooltipForNextFrame(font, lines, Optional.empty(), mx + 3, my - 3);
    }

    // -----------------------------------------------------------------------
    // Static helpers
    // -----------------------------------------------------------------------

    public static void drawResearchIcon(ResearchEntry iconResearch, int iconX, int iconY, float zLevel, boolean bw) {
        if (iconResearch.getIcons() == null || iconResearch.getIcons().length == 0) return;
        int idx = (int)(System.currentTimeMillis() / 1000L % iconResearch.getIcons().length);
        Object icon = iconResearch.getIcons()[idx];
        GuiGraphicsExtractor gg = UtilsFX.currentGuiGraphics;
        if (gg == null) return;

        if (icon instanceof Identifier) {
            gg.blit(RenderPipelines.GUI_TEXTURED, (Identifier) icon,
                    iconX, iconY, 0, 0, 16, 16, 16, 16);
        } else if (icon instanceof ItemStack) {
            gg.item((ItemStack) icon, iconX, iconY);
        }
        // Focus-type icons are not ported here
    }

    public static void drawForbidden(double x, double y) {
        // Warp/corruption spiral effect — not ported in this milestone
    }

    // Used by ResearchToast and other callers that need to set the graphics context
    public static void setCurrentGraphics(GuiGraphicsExtractor gg) {
        UtilsFX.currentGuiGraphics = gg;
    }
}
