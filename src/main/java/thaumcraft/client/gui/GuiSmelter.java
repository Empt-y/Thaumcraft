package thaumcraft.client.gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.common.container.ContainerSmelter;
import thaumcraft.common.tiles.essentia.TileSmelter;


public class GuiSmelter extends AbstractContainerScreen<ContainerSmelter>
{
    private TileSmelter furnaceInventory;
    Identifier tex;

    public GuiSmelter(ContainerSmelter menu, Inventory inv, Component title) {
        super(menu, inv, title);
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_smelter.png");
        furnaceInventory = menu.getTile();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, tex, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        // Burn time indicator (fire icon, fills from bottom up)
        int burnScaled = furnaceInventory.getBurnTimeRemainingScaled(20);
        if (burnScaled > 0) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, tex, x + 80, y + 26 + (20 - burnScaled), 176, 20 - burnScaled, 16, burnScaled, 256, 256);
        }

        // Cook progress (vertical bar filling from bottom)
        int cookScaled = furnaceInventory.getCookProgressScaled(46);
        graphics.blit(RenderPipelines.GUI_TEXTURED, tex, x + 106, y + 13 + (46 - cookScaled), 216, 46 - cookScaled, 9, cookScaled, 256, 256);

        // Vis bar (vertical, fills from bottom)
        int visScaled = furnaceInventory.getVisScaled(48);
        graphics.blit(RenderPipelines.GUI_TEXTURED, tex, x + 61, y + 12 + (48 - visScaled), 200, 48 - visScaled, 8, visScaled, 256, 256);

        // Vis bar frame overlay
        graphics.blit(RenderPipelines.GUI_TEXTURED, tex, x + 60, y + 8, 232, 0, 10, 55, 256, 256);
    }
}
