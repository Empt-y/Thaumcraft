package thaumcraft.client.gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.container.ContainerSpa;
import thaumcraft.common.tiles.devices.TileSpa;


@OnlyIn(Dist.CLIENT)
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
        // rendering stub
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        // rendering stub
    }
}
