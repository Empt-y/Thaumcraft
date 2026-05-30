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

    public GuiSpa(Inventory par1InventoryPlayer, TileSpa teSpa) {
        super(new ContainerSpa(par1InventoryPlayer, teSpa), par1InventoryPlayer,
            Component.translatable("gui.spa"));
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_spa.png");
        spa = teSpa;
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
