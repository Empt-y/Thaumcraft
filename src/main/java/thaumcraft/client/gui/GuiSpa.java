package thaumcraft.client.gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.common.container.ContainerSpa;
import thaumcraft.common.tiles.devices.TileSpa;


public class GuiSpa extends AbstractContainerScreen<ContainerSpa>
{
    private TileSpa spa;
    Identifier tex;

    public GuiSpa(ContainerSpa menu, Inventory inv, Component title) {
        super(menu, inv, title);
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_spa.png");
        spa = menu.getTile();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, tex, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}
