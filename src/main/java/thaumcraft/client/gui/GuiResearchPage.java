package thaumcraft.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchAddendum;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.playerdata.PacketSyncProgressToServer;
import thaumcraft.common.lib.research.ResearchManager;


public class GuiResearchPage extends Screen
{
    // Static history for back-navigation
    public static LinkedList<Identifier> history = new LinkedList<>();
    static Identifier shownRecipe;
    static boolean cycleMultiblockLines;

    // Layout constants
    protected int paneWidth = 256;
    protected int paneHeight = 181;
    protected double guiMapX;
    protected double guiMapY;

    // Instance state
    private ResearchEntry research;
    private int currentStage;
    private int page;
    private int maxPages;
    private boolean isComplete;
    private boolean hasAllRequisites;
    private boolean hold;
    private long lastCheck;
    private int hrx;
    private int hry;

    // Knowledge
    private IPlayerKnowledge playerKnowledge;

    // Page parsing
    private ArrayList<Page> pages;
    private static final int PAGE_WIDTH = 140;

    // Textures
    private Identifier tex1;  // main book texture
    private Identifier tex2;  // overlay / recipe display

    // Requirements tracking
    private boolean[] hasItem;
    private boolean[] hasCraft;
    private boolean[] hasResearch;
    private boolean[] hasKnow;

    public HashMap<Integer, String> keyCache;

    public GuiResearchPage(ResearchEntry research, Identifier recipe, double x, double y) {
        super(Component.translatable("gui.researchpage"));
        this.guiMapX = x;
        this.guiMapY = y;
        this.research = research;
        this.keyCache = new HashMap<>();
        this.pages = new ArrayList<>();
        this.isComplete = false;
        this.hasAllRequisites = false;
        this.hold = false;
        this.lastCheck = 0L;
        this.hrx = 0;
        this.hry = 0;
        this.page = 0;
        this.maxPages = 0;
        this.currentStage = 0;
        this.tex1 = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_researchbook.png");
        this.tex2 = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_researchbook_overlay.png");

        if (recipe != null) {
            GuiResearchPage.shownRecipe = recipe;
        }

        Player player = Minecraft.getInstance().player;
        if (player != null) {
            this.playerKnowledge = ThaumcraftCapabilities.getKnowledge(player);
        }
    }

    @Override
    protected void init() {
        // Parse pages on init when font is available
        parsePages();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void tick() {
        long nano = System.nanoTime();
        if (nano > lastCheck) {
            parsePages();
            lastCheck = nano + (hold ? 250_000_000L : 2_000_000_000L);
        }
    }

    // -----------------------------------------------------------------------
    // Key/Mouse input
    // -----------------------------------------------------------------------

    @Override
    public boolean keyPressed(KeyEvent event) {
        int k = event.key();
        if (k == GLFW.GLFW_KEY_ESCAPE) {
            handleBack();
            return true;
        }
        // Inventory key
        if (this.minecraft != null && this.minecraft.options.keyInventory.matches(event)) {
            handleBack();
            return true;
        }
        // Left / up / page-up => prev page
        if (k == GLFW.GLFW_KEY_LEFT || k == GLFW.GLFW_KEY_UP || k == GLFW.GLFW_KEY_PAGE_UP) {
            prevPage();
            return true;
        }
        // Right / down / page-down => next page
        if (k == GLFW.GLFW_KEY_RIGHT || k == GLFW.GLFW_KEY_DOWN || k == GLFW.GLFW_KEY_PAGE_DOWN) {
            nextPage();
            return true;
        }
        // Backspace => go back
        if (k == GLFW.GLFW_KEY_BACKSPACE) {
            goBack();
            return true;
        }
        return super.keyPressed(event);
    }

    private void handleBack() {
        if (GuiResearchPage.shownRecipe != null) {
            GuiResearchPage.shownRecipe = null;
        } else {
            history.clear();
            minecraft.setScreen(new GuiResearchBrowser(guiMapX, guiMapY));
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mx = event.x(), my = event.y();
        int button = event.button();
        checkRequisites();
        int sw = (width - paneWidth) / 2;
        int sh = (height - paneHeight) / 2;

        // Click on "complete stage" button
        if (!hold && hasAllRequisites && GuiResearchPage.shownRecipe == null) {
            int relX = (int) mx - hrx;
            int relY = (int) my - hry;
            if (relX >= 0 && relY >= 0 && relX < 64 && relY < 12) {
                Player player = minecraft.player;
                PacketHandler.sendToServer(new PacketSyncProgressToServer(research.getKey(), false, true, true));
                if (player != null) player.playSound(SoundsTC.write, 0.66f, 1.0f);
                lastCheck = 0L;
                hold = true;
                keyCache.clear();
            }
        }

        // Click the "back" / return button
        {
            int bx = sw + 118, by = sh + 190;
            if (mx >= bx && my >= by && mx < bx + 20 && my < by + 12) {
                goBack();
                return true;
            }
        }

        // Previous page arrow
        if (GuiResearchPage.shownRecipe == null) {
            int bx = sw - 16, by = sh + 190;
            if (page > 0 && mx >= bx && my >= by && mx < bx + 12 && my < by + 8) {
                prevPage();
                return true;
            }
        }

        // Next page arrow
        if (GuiResearchPage.shownRecipe == null) {
            int bx = sw + 262, by = sh + 190;
            if (page < maxPages - 2 && mx >= bx && my >= by && mx < bx + 12 && my < by + 8) {
                nextPage();
                return true;
            }
        }

        return super.mouseClicked(event, doubleClick);
    }

    private void nextPage() {
        if (page < maxPages - 2) {
            page += 2;
            Player player = minecraft.player;
            if (player != null) player.playSound(SoundsTC.page, 0.66f, 1.0f);
        }
    }

    private void prevPage() {
        if (page >= 2) {
            page -= 2;
            Player player = minecraft.player;
            if (player != null) player.playSound(SoundsTC.page, 0.66f, 1.0f);
        }
    }

    private void goBack() {
        if (!GuiResearchPage.history.isEmpty()) {
            Player player = minecraft.player;
            if (player != null) player.playSound(SoundsTC.page, 0.66f, 1.0f);
            GuiResearchPage.shownRecipe = GuiResearchPage.history.pop();
        } else {
            GuiResearchPage.shownRecipe = null;
        }
    }

    // -----------------------------------------------------------------------
    // Rendering
    // -----------------------------------------------------------------------

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mx, int my, float partialTick) {
        // Draw the main book texture (scaled 1.3×)
        int sw = (width - paneWidth) / 2;
        int sh = (height - paneHeight) / 2;
        float var10 = (width  - paneWidth  * 1.3f) / 2.0f;
        float var11 = (height - paneHeight * 1.3f) / 2.0f;

        graphics.pose().pushMatrix();
        graphics.pose().translate(var10, var11);
        graphics.pose().scale(1.3f, 1.3f);
        graphics.blit(RenderPipelines.GUI_TEXTURED, tex1, 0, 0, 0, 0, paneWidth, paneHeight, 256, 256);
        graphics.pose().popMatrix();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mx, int my, float partialTick) {
        // Store for drawTag calls
        UtilsFX.currentGuiGraphics = graphics;

        // Background
        extractBackground(graphics, mx, my, partialTick);

        int sw = (width - paneWidth) / 2;
        int sh = (height - paneHeight) / 2;

        // Draw the two visible pages
        for (int a = 0; a < pages.size(); ++a) {
            int pageNum = a;
            if ((pageNum == page || pageNum == page + 1) && pageNum < maxPages) {
                drawPage(graphics, pages.get(a), pageNum % 2, sw, sh - 10, mx, my);
            }
            if (pageNum > page + 1) break;
        }

        // Navigation arrows (bobbing effect)
        float bob = (float)(Math.sin(minecraft.player != null
                ? minecraft.player.tickCount / 3.0 : 0) * 0.2 + 0.1);

        // Back-to-browser button (small back arrow)
        if (!GuiResearchPage.history.isEmpty()) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, tex1,
                    sw + 118, sh + 190, 38, 202, 20, 12, 256, 256);
        }
        // Prev page arrow
        if (page > 0 && GuiResearchPage.shownRecipe == null) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, tex1,
                    sw - 16, sh + 190, 0, 184, 12, 8, 256, 256);
        }
        // Next page arrow
        if (page < maxPages - 2 && GuiResearchPage.shownRecipe == null) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, tex1,
                    sw + 262, sh + 190, 12, 184, 12, 8, 256, 256);
        }

        // Call super to render button widgets (if any)
        super.extractRenderState(graphics, mx, my, partialTick);

        UtilsFX.currentGuiGraphics = null;
    }

    private void drawPage(GuiGraphicsExtractor graphics, Page pageParm, int side, int x, int y, int mx, int my) {
        int sw = (width - paneWidth) / 2;
        int sh = (height - paneHeight) / 2;

        // Title on the first page (left side, page 0)
        if (page == 0 && side == 0 && research != null) {
            // Draw underline decorations above and below title
            graphics.blit(RenderPipelines.GUI_TEXTURED, tex1, x + 4, y - 7, 24, 184, 96, 4, 256, 256);
            graphics.blit(RenderPipelines.GUI_TEXTURED, tex1, x + 4, y + 10, 24, 184, 96, 4, 256, 256);

            String title = research.getLocalizedName();
            int titleWidth = font.width(title);
            int titleX = x - 15 + PAGE_WIDTH / 2 - titleWidth / 2;
            if (titleWidth <= PAGE_WIDTH) {
                graphics.text(font, title, titleX, y, 0x202020);
            } else {
                float scale = (float) PAGE_WIDTH / titleWidth;
                graphics.pose().pushMatrix();
                graphics.pose().translate(x - 15 + PAGE_WIDTH / 2 - titleWidth / 2 * scale, y + 1.0f * scale);
                graphics.pose().scale(scale, scale);
                graphics.text(font, title, 0, 0, 0x202020);
                graphics.pose().popMatrix();
            }
            y += 28;
        }

        // Render text content
        for (Object content : pageParm.contents) {
            if (content instanceof String) {
                String ss = ((String) content).replace("~B", "");
                graphics.text(font, ss, x - 15 + side * 152, y - 6, 0x202020);
                y += font.lineHeight;
                if (((String) content).endsWith("~B")) {
                    y += (int)(font.lineHeight * 0.66);
                }
            } else if (content instanceof PageImage) {
                PageImage pi = (PageImage) content;
                int pad = (PAGE_WIDTH - pi.aw) / 2;
                graphics.pose().pushMatrix();
                graphics.pose().translate(x - 15 + side * 152 + pad, y - 5);
                graphics.pose().scale(pi.scale, pi.scale);
                graphics.blit(RenderPipelines.GUI_TEXTURED, pi.loc, 0, 0, (float) pi.x, (float) pi.y, pi.w, pi.h, 256, 256);
                graphics.pose().popMatrix();
                y += pi.ah + 2;
            }
        }

        // Draw requirements section on page 0, side 0 if not complete
        if (page == 0 && side == 0 && !isComplete && research != null && research.getStages() != null
                && currentStage < research.getStages().length) {
            ResearchStage stage = research.getStages()[currentStage];
            drawRequirements(graphics, sw, mx, my, stage);
        }
    }

    private void drawRequirements(GuiGraphicsExtractor graphics, int x, int mx, int my, ResearchStage stage) {
        int y = (height - paneHeight) / 2 - 16 + 210;
        boolean any = false;

        // Research prereqs
        if (stage.getResearch() != null && stage.getResearch().length > 0) {
            y -= 18;
            any = true;
            graphics.blit(RenderPipelines.GUI_TEXTURED, tex1, x - 12, y - 1, 200, 232, 56, 16, 256, 256);
            int shift = 24;
            int ss = Math.max(18, (stage.getResearch().length <= 6) ? 18 : 110 / stage.getResearch().length);
            for (int a = 0; a < stage.getResearch().length; ++a) {
                ResearchEntry re = ResearchCategories.getResearch(stage.getResearch()[a]);
                if (re != null && re.getIcons() != null && re.getIcons().length > 0) {
                    Object icon = re.getIcons()[(int)(System.currentTimeMillis() / 1000L % re.getIcons().length)];
                    if (icon instanceof Identifier) {
                        graphics.blit(RenderPipelines.GUI_TEXTURED, (Identifier) icon,
                                x - 15 + shift, y, 0, 0, 16, 16, 16, 16);
                    } else if (icon instanceof ItemStack) {
                        graphics.item((ItemStack) icon, x - 15 + shift, y);
                    }
                } else {
                    // Unknown icon placeholder
                    Identifier unknown = Identifier.fromNamespaceAndPath("thaumcraft", "textures/aspects/_unknown.png");
                    graphics.blit(RenderPipelines.GUI_TEXTURED, unknown,
                            x - 15 + shift, y, 0, 0, 16, 16, 16, 16);
                }
                // Checkmark if requirement met
                if (hasResearch != null && a < hasResearch.length && hasResearch[a]) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, tex1,
                            x - 15 + shift + 8, y, 159, 207, 10, 10, 256, 256);
                }
                shift += ss;
            }
        }

        // Item obtain prereqs
        if (stage.getObtain() != null && stage.getObtain().length > 0) {
            y -= 18;
            any = true;
            graphics.blit(RenderPipelines.GUI_TEXTURED, tex1, x - 12, y - 1, 200, 216, 56, 16, 256, 256);
            int shift = 24;
            int ss = Math.max(18, (stage.getObtain().length <= 6) ? 18 : 110 / stage.getObtain().length);
            for (int a = 0; a < stage.getObtain().length; ++a) {
                Object obj = stage.getObtain()[a];
                if (obj instanceof ItemStack) {
                    graphics.item((ItemStack) obj, x - 15 + shift, y);
                }
                if (hasItem != null && a < hasItem.length && hasItem[a]) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, tex1,
                            x - 15 + shift + 8, y, 159, 207, 10, 10, 256, 256);
                }
                shift += ss;
            }
        }

        // Craft prereqs
        if (stage.getCraft() != null && stage.getCraft().length > 0) {
            y -= 18;
            any = true;
            graphics.blit(RenderPipelines.GUI_TEXTURED, tex1, x - 12, y - 1, 200, 200, 56, 16, 256, 256);
            int shift = 24;
            int ss = Math.max(18, (stage.getCraft().length <= 6) ? 18 : 110 / stage.getCraft().length);
            for (int a = 0; a < stage.getCraft().length; ++a) {
                Object obj = stage.getCraft()[a];
                if (obj instanceof ItemStack) {
                    graphics.item((ItemStack) obj, x - 15 + shift, y);
                }
                if (hasCraft != null && a < hasCraft.length && hasCraft[a]) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, tex1,
                            x - 15 + shift + 8, y, 159, 207, 10, 10, 256, 256);
                }
                shift += ss;
            }
        }

        if (any) {
            y -= 12;
            graphics.blit(RenderPipelines.GUI_TEXTURED, tex1, x + 4, y - 2, 24, 184, 96, 8, 256, 256);

            if (hasAllRequisites) {
                hrx = x + 20;
                hry = y - 6;
                if (hold) {
                    String s = net.minecraft.client.resources.language.I18n.get("tc.stage.hold");
                    graphics.text(font, s, x + 52 - font.width(s) / 2, y - 4, 0xFFFFFF, true);
                } else {
                    // Clickable "complete" button
                    graphics.blit(RenderPipelines.GUI_TEXTURED, tex1,
                            hrx, hry, 84, 216, 64, 12, 256, 256);
                    String s = net.minecraft.client.resources.language.I18n.get("tc.stage.complete");
                    graphics.text(font, s, x + 52 - font.width(s) / 2, y - 4, 0xFFFFFF, true);
                }
            }
        }
    }

    // -----------------------------------------------------------------------
    // Page parsing
    // -----------------------------------------------------------------------

    private void parsePages() {
        if (research == null || minecraft == null) return;
        checkRequisites();
        pages.clear();

        if (research.getStages() == null) {
            maxPages = 0;
            return;
        }

        Player player = minecraft.player;
        if (player == null) return;

        isComplete = playerKnowledge != null && playerKnowledge.isResearchComplete(research.getKey());
        currentStage = (playerKnowledge != null)
                ? playerKnowledge.getResearchStage(research.getKey()) - 1
                : 0;
        while (currentStage >= research.getStages().length) --currentStage;
        if (currentStage < 0) currentStage = 0;

        ResearchStage stage = research.getStages()[currentStage];

        // Gather raw text
        String rawText = stage.getTextLocalized();
        if (rawText == null) rawText = "";

        // Addenda
        ResearchAddendum[] addenda = (research.getAddenda() != null && isComplete) ? research.getAddenda() : null;
        if (addenda != null) {
            int ac = 0;
            for (ResearchAddendum addendum : addenda) {
                if (ThaumcraftCapabilities.knowsResearchStrict(player, addendum.getResearch())) {
                    ++ac;
                    String addTitle = net.minecraft.client.resources.language.I18n.get("tc.addendumtext", ac);
                    rawText = rawText + "<PAGE>" + addTitle + "<BR>" + addendum.getTextLocalized();
                }
            }
        }

        // Normalise markup
        rawText = rawText.replace("<BR>", "~B\n\n")
                         .replace("<BR/>", "~B\n\n")
                         .replace("<LINE>", "~L").replace("<LINE/>", "~L")
                         .replace("<DIV>", "~D").replace("<DIV/>", "~D")
                         .replace("<PAGE>", "~P").replace("<PAGE/>", "~P")
                         .replace("<IMG>", "").replace("</IMG>", "");

        // Word-wrap: split on newlines; font.split returns FormattedCharSequence in this MC version
        List<String> parsedLines = new ArrayList<>(java.util.Arrays.asList(rawText.split("\n")));

        int lineH = font.lineHeight;
        int heightRemaining = 182;

        // Subtract space for requirements on first page
        if (!isComplete) {
            if (stage.getResearch() != null) { heightRemaining -= 18; }
            if (stage.getObtain()   != null) { heightRemaining -= 18; }
            if (stage.getCraft()    != null) { heightRemaining -= 18; }
            if (stage.getKnow()     != null) { heightRemaining -= 18; }
            if (heightRemaining < 182) heightRemaining -= 15;
        }

        Page page1 = new Page();
        for (String line : parsedLines) {
            if (line.contains("~P")) {
                heightRemaining = 210;
                pages.add(page1.copy());
                page1 = new Page();
                continue;
            }
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                page1.contents.add(trimmed);
                heightRemaining -= lineH;
                if (trimmed.endsWith("~B")) heightRemaining -= (int)(lineH * 0.66);
            }
            if (heightRemaining < lineH && !page1.contents.isEmpty()) {
                heightRemaining = 210;
                pages.add(page1.copy());
                page1 = new Page();
            }
        }
        if (!page1.contents.isEmpty()) {
            pages.add(page1.copy());
        }
        maxPages = pages.size();
        if (maxPages == 0) maxPages = 1; // always at least one page (title)
    }

    private void checkRequisites() {
        if (research == null || research.getStages() == null || playerKnowledge == null) return;
        isComplete = playerKnowledge.isResearchComplete(research.getKey());
        while (currentStage >= research.getStages().length) --currentStage;
        if (currentStage < 0) return;

        hasAllRequisites = true;
        hasItem     = null;
        hasCraft    = null;
        hasResearch = null;
        hasKnow     = null;

        Player player = (minecraft != null) ? minecraft.player : null;
        if (player == null) return;

        ResearchStage stage = research.getStages()[currentStage];

        // Research prereqs
        String[] r = stage.getResearch();
        if (r != null) {
            hasResearch = new boolean[r.length];
            for (int a = 0; a < r.length; ++a) {
                hasResearch[a] = ThaumcraftCapabilities.knowsResearchStrict(player, r[a]);
                if (!hasResearch[a]) hasAllRequisites = false;
            }
        }

        // Craft prereqs
        Object[] c = stage.getCraft();
        if (c != null) {
            hasCraft = new boolean[c.length];
            for (int a = 0; a < c.length; ++a) {
                hasCraft[a] = playerKnowledge.isResearchKnown("[#]" + stage.getCraftReference()[a]);
                if (!hasCraft[a]) hasAllRequisites = false;
            }
        }

        // Knowledge prereqs
        ResearchStage.Knowledge[] k = stage.getKnow();
        if (k != null) {
            hasKnow = new boolean[k.length];
            for (int a = 0; a < k.length; ++a) {
                int pk = playerKnowledge.getKnowledge(k[a].type, k[a].category);
                hasKnow[a] = (pk >= k[a].amount);
                if (!hasKnow[a]) hasAllRequisites = false;
            }
        }
    }

    // -----------------------------------------------------------------------
    // Inner classes
    // -----------------------------------------------------------------------

    private static class Page {
        ArrayList<Object> contents = new ArrayList<>();

        Page copy() {
            Page p = new Page();
            p.contents.addAll(contents);
            return p;
        }
    }

    private static class PageImage {
        int x, y, w, h, aw, ah;
        float scale;
        Identifier loc;

        static PageImage parse(String text) {
            String[] s = text.split(":");
            if (s.length != 7) return null;
            try {
                PageImage pi = new PageImage();
                pi.loc   = Identifier.fromNamespaceAndPath(s[0], s[1]);
                pi.x     = Integer.parseInt(s[2]);
                pi.y     = Integer.parseInt(s[3]);
                pi.w     = Integer.parseInt(s[4]);
                pi.h     = Integer.parseInt(s[5]);
                pi.scale = Float.parseFloat(s[6]);
                pi.aw    = (int)(pi.w * pi.scale);
                pi.ah    = (int)(pi.h * pi.scale);
                if (pi.ah > 208 || pi.aw > 140) return null;
                return pi;
            } catch (Exception ex) {
                return null;
            }
        }
    }
}
