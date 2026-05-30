package thaumcraft.client.gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.container.ContainerArcaneWorkbench;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;


@OnlyIn(Dist.CLIENT)
public class GuiArcaneWorkbench extends AbstractContainerScreen<ContainerArcaneWorkbench>
{
    private TileArcaneWorkbench tileEntity;
    Identifier tex;

    public GuiArcaneWorkbench(Inventory par1InventoryPlayer, TileArcaneWorkbench e) {
        super(new ContainerArcaneWorkbench(par1InventoryPlayer, e), par1InventoryPlayer,
            Component.translatable("gui.arcaneworkbench"), 190, 234);
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/arcaneworkbench.png");
        tileEntity = e;
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
