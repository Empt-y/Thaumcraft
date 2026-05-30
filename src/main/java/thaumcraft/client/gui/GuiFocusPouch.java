package thaumcraft.client.gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.container.ContainerFocusPouch;


@OnlyIn(Dist.CLIENT)
public class GuiFocusPouch extends AbstractContainerScreen<ContainerFocusPouch>
{
    private int blockSlot;
    Identifier tex;

    public GuiFocusPouch(Inventory par1InventoryPlayer, Level world, int x, int y, int z) {
        super(new ContainerFocusPouch(par1InventoryPlayer, world, x, y, z), par1InventoryPlayer,
            Component.translatable("gui.focuspouch"), 175, 232);
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_focuspouch.png");
        blockSlot = 0; // hotbar slot tracking stubbed — selected field is private in newer MC
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
