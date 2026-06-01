package thaumcraft.client.gui;
import java.awt.Color;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.client.lib.UtilsFX;
import thaumcraft.common.container.ContainerPotionSprayer;
import thaumcraft.common.tiles.devices.TilePotionSprayer;


public class GuiPotionSprayer extends AbstractContainerScreen<ContainerPotionSprayer>
{
    private TilePotionSprayer inventory;
    private ContainerPotionSprayer container;
    private Player player;
    Identifier tex;
    int startAspect;

    public GuiPotionSprayer(ContainerPotionSprayer menu, Inventory inv, Component title) {
        super(menu, inv, title, 192, 233);
        container = null;
        player = null;
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_potion_sprayer.png");
        startAspect = 0;
        inventory = menu.getTile();
        container = menu;
        player = inv.player;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, tex, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        // Colored charge bar: 8 charges, each 9 pixels tall, fills from top
        if (inventory.charges > 0) {
            Color c = new Color(inventory.color != 0 ? inventory.color : 0xFFFFFF);
            int r = c.getRed(), g = c.getGreen(), b = c.getBlue();
            int argb = (0xFF << 24) | (r << 16) | (g << 8) | b;
            int scroll = (int)(System.currentTimeMillis() / 50L) % 256;
            // Draw with tint: use fill as approximation of colored texture
            // The charge bar sits at x+128, y+36+(8-charges)*9, width=8, height=charges*9
            int barY = y + 36 + (8 - inventory.charges) * 9;
            int barH = inventory.charges * 9;
            graphics.fill(x + 128, barY, x + 136, barY + barH, argb);
        }

        drawAspects(graphics, x, y);

        // Vis frame overlay
        graphics.blit(RenderPipelines.GUI_TEXTURED, tex, x + 125, y + 28, 205, 28, 14, 88, 256, 256);
    }

    private void drawAspects(GuiGraphicsExtractor graphics, int x, int y) {
        if (inventory.recipe == null || inventory.recipe.size() == 0) return;
        UtilsFX.currentGuiGraphics = graphics;
        int pos = 0;
        for (Aspect aspect : inventory.recipe.getAspectsSortedByName()) {
            Color c = new Color(aspect.getColor());
            int filled = (int)(inventory.recipeProgress.getAmount(aspect) / (float) inventory.recipe.getAmount(aspect) * 14.0f);
            // Gray background bar
            graphics.fill(x + 96 + 22 * (pos % 2), y + 32 + 16 * (pos / 2), x + 98 + 22 * (pos % 2), y + 46 + 16 * (pos / 2), 0xFF333333);
            // Colored fill bar from bottom
            if (filled > 0) {
                int argb = (0xFF << 24) | ((c.getRed() & 0xFF) << 16) | ((c.getGreen() & 0xFF) << 8) | (c.getBlue() & 0xFF);
                graphics.fill(x + 96 + 22 * (pos % 2), y + 46 + 16 * (pos / 2) - filled, x + 98 + 22 * (pos % 2), y + 46 + 16 * (pos / 2), argb);
            }
            // Aspect tag icon
            UtilsFX.drawTag(x + 79 + 22 * (pos % 2), y + 31 + 16 * (pos / 2), aspect, (float) inventory.recipe.getAmount(aspect), 0, 0.0);
            pos++;
        }
        UtilsFX.currentGuiGraphics = null;
    }
}
