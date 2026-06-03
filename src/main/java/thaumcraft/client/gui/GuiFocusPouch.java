package thaumcraft.client.gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.common.container.ContainerFocusPouch;


public class GuiFocusPouch extends AbstractContainerScreen<ContainerFocusPouch>
{
    private Identifier tex;

    public GuiFocusPouch(ContainerFocusPouch menu, Inventory inv, Component title) {
        super(menu, inv, title);
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_focuspouch.png");
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft != null && this.minecraft.player != null
                && this.minecraft.player.getInventory().getSelectedItem().isEmpty()) {
            this.minecraft.player.closeContainer();
            return;
        }
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, tex, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int blockSlot = this.minecraft != null && this.minecraft.player != null
                ? this.minecraft.player.getInventory().getSelectedSlot() : 0;
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, tex, 8 + blockSlot * 18, 209, 240, 0, 16, 16, 256, 256);
    }
}
