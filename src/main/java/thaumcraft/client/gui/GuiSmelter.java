package thaumcraft.client.gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.container.ContainerSmelter;
import thaumcraft.common.tiles.essentia.TileSmelter;


@OnlyIn(Dist.CLIENT)
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
        // rendering stub
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        // rendering stub
    }
}
