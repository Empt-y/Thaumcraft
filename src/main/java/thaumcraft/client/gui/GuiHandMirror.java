package thaumcraft.client.gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import thaumcraft.common.container.ContainerHandMirror;


public class GuiHandMirror extends AbstractContainerScreen<ContainerHandMirror>
{
    int ci;
    Identifier tex;

    public GuiHandMirror(Inventory par1InventoryPlayer, Level world, int x, int y, int z) {
        super(new ContainerHandMirror(par1InventoryPlayer, world, x, y, z), par1InventoryPlayer,
            Component.translatable("gui.handmirror"));
        ci = par1InventoryPlayer.getSelectedSlot();
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_handmirror.png");
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
