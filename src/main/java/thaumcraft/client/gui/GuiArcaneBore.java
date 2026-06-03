package thaumcraft.client.gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import thaumcraft.common.container.ContainerArcaneBore;
import thaumcraft.common.entities.construct.EntityArcaneBore;


public class GuiArcaneBore extends AbstractContainerScreen<ContainerArcaneBore>
{
    EntityArcaneBore turret;
    Identifier tex;

    public GuiArcaneBore(ContainerArcaneBore menu, Inventory inv, Component title) {
        super(menu, inv, title, 175, 232);
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_arcanebore.png");
        turret = menu.turret;
    }

    public GuiArcaneBore(Inventory par1InventoryPlayer, Level world, EntityArcaneBore t) {
        super(new ContainerArcaneBore(0, par1InventoryPlayer, t), par1InventoryPlayer,
            Component.translatable("gui.arcanebore"), 175, 232);
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_arcanebore.png");
        turret = t;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, tex, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
    }
}
