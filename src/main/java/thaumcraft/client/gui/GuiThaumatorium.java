package thaumcraft.client.gui;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.container.ContainerThaumatorium;
import net.minecraft.client.input.MouseButtonEvent;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketSelectThaumotoriumRecipeToServer;
import thaumcraft.common.tiles.crafting.TileThaumatorium;


public class GuiThaumatorium extends AbstractContainerScreen<ContainerThaumatorium>
{
    private TileThaumatorium inventory;
    private ContainerThaumatorium container;
    private int index;
    private int lastSize;
    private Player player;
    Identifier tex;
    ArrayList<Integer> hashList;
    long lastHLUpdate;
    static HashMap<Integer, CrucibleRecipe> recipeCache;

    public GuiThaumatorium(ContainerThaumatorium menu, Inventory inv, Component title) {
        super(menu, inv, title, 175, 216);
        container = null;
        index = 0;
        lastSize = 0;
        player = null;
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_thaumatorium.png");
        hashList = new ArrayList<Integer>();
        lastHLUpdate = 0L;
        inventory = menu.getTile();
        container = menu;
        player = inv.player;
        if (inventory != null) inventory.updateRecipes(player);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, tex, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        long t = System.currentTimeMillis();

        // Refresh hash list every 500ms
        if (t > lastHLUpdate) {
            hashList.clear();
            hashList = inventory.generateRecipeHashlist();
            lastHLUpdate = t + 500L;
        }

        // Scroll arrows
        if (hashList.size() > 6) {
            if (index > 0) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, tex, x + 82, y + 56, 176, 56, 8, 11, 256, 256);
            }
            if (index < hashList.size() / 2.0f - 3.0f) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, tex, x + 82, y + 93, 176, 93, 8, 11, 256, 256);
            }
        }

        // Recipe count overlay
        if (inventory.maxRecipes > 1) {
            String text = inventory.recipeHash.size() + "/" + inventory.maxRecipes;
            int tw = this.font.width(text);
            graphics.text(this.font, text, x + 64 - tw / 2, y + 48, 0xFFFFFF, true);
        }

        UtilsFX.currentGuiGraphics = graphics;
        drawAspects(graphics, x, y, t);
        drawOutput(graphics, x, y, mouseX, mouseY, t);
        UtilsFX.currentGuiGraphics = null;
    }

    private static CrucibleRecipe getRecipeCached(int hash) {
        if (recipeCache.containsKey(hash)) {
            return recipeCache.get(hash);
        }
        CrucibleRecipe cr = ThaumcraftApi.getCrucibleRecipeFromHash(hash);
        if (cr != null) {
            recipeCache.put(hash, cr);
        }
        return cr;
    }

    private void drawAspects(GuiGraphicsExtractor graphics, int x, int y, long time) {
        if (inventory.recipeHash == null || inventory.recipeHash.size() <= 0) return;

        int hash = inventory.recipeHash.get((int)(time / 1000L % inventory.recipeHash.size()));
        CrucibleRecipe cr = getRecipeCached(hash);
        if (cr == null) return;

        int count = 0, px = 0, py = 0;
        for (Aspect aspect : cr.getAspects().getAspectsSortedByName()) {
            // Gray background bar
            graphics.blit(RenderPipelines.GUI_TEXTURED, tex, x + 98 + 16 * px, y + 40 + 20 * py, 176, 4, 12, 3, 256, 256);
            // Colored fill bar
            int filled = (int)(inventory.essentia.getAmount(aspect) / (float)cr.getAspects().getAmount(aspect) * 12.0f);
            if (filled > 0) {
                Color c = new Color(aspect.getColor());
                // Use tinted blit (fill approximation for progress)
                int argb = (0xFF << 24) | ((c.getRed() & 0xFF) << 16) | ((c.getGreen() & 0xFF) << 8) | (c.getBlue() & 0xFF);
                graphics.fill(x + 98 + 16 * px, y + 40 + 20 * py, x + 98 + 16 * px + filled, y + 43 + 20 * py, argb);
            }
            // Aspect tag
            UtilsFX.drawTag(x + 96 + 16 * px, y + 24 + 20 * py, aspect, (float)cr.getAspects().getAmount(aspect), 0, 0.0);

            if (++px > 1) { px = 0; ++py; }
            if (++count >= 8) break;
        }
    }

    private void drawOutput(GuiGraphicsExtractor graphics, int x, int y, int mx, int my, long time) {
        int px = 0, py = 0, q = 0, idx = 0;
        for (int hash : hashList) {
            if (q++ < index * 2) continue;
            CrucibleRecipe cr = getRecipeCached(hash);
            if (cr == null) continue;

            int ix = x + 48 + px * 16;
            int iy = y + 56 + py * 16;

            // Highlight current recipe
            if (inventory.recipeHash != null && inventory.recipeHash.contains(hash)) {
                int activeHash = inventory.recipeHash.get((int)(time / 1000L % inventory.recipeHash.size()));
                if (hash == activeHash) {
                    graphics.blit(RenderPipelines.GUI_TEXTURED, tex, ix, iy, 176, 8, 16, 16, 256, 256);
                }
            }

            // Item icon
            graphics.item(cr.getRecipeOutput(), ix, iy);
            graphics.itemDecorations(this.font, cr.getRecipeOutput(), ix, iy);

            if (++px > 1) { px = 0; ++py; }
            if (++idx >= 6) break;
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        boolean result = super.mouseClicked(event, doubleClick);
        double mx = event.x(), my = event.y();
        int gx = (this.width - this.imageWidth) / 2;
        int gy = (this.height - this.imageHeight) / 2;
        int px = 0, py = 0, q = 0, idx = 0;

        for (int hash : hashList) {
            if (q++ < index * 2) continue;
            CrucibleRecipe cr = getRecipeCached(hash);
            if (cr == null) continue;
            int hx = (int)mx - (gx + 48 + px * 16);
            int hy = (int)my - (gy + 56 + py * 16);
            if (hx >= 0 && hy >= 0 && hx < 16 && hy < 16) {
                PacketHandler.sendToServer(new PacketSelectThaumotoriumRecipeToServer(player, inventory.getBlockPos(), hash));
                lastHLUpdate = 0L;
                break;
            }
            if (++px > 1) { px = 0; ++py; }
            if (++idx >= 6) break;
        }

        // Scroll buttons
        if (hashList.size() > 6) {
            if (index > 0) {
                int x2 = (int)mx - (gx + 82), y2 = (int)my - (gy + 56);
                if (x2 >= 0 && y2 >= 0 && x2 < 8 && y2 < 11) { --index; lastHLUpdate = 0L; }
            }
            if (index < hashList.size() / 2.0f - 3.0f) {
                int x2 = (int)mx - (gx + 82), y2 = (int)my - (gy + 93);
                if (x2 >= 0 && y2 >= 0 && x2 < 8 && y2 < 11) { ++index; lastHLUpdate = 0L; }
            }
        }
        return result;
    }

    static {
        recipeCache = new HashMap<Integer, CrucibleRecipe>();
    }
}
