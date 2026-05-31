package thaumcraft.client.gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.container.ContainerVoidSiphon;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;


@OnlyIn(Dist.CLIENT)
public class GuiVoidSiphon extends AbstractContainerScreen<ContainerVoidSiphon>
{
    private TileVoidSiphon inventory;
    private static Identifier starsTexture;
    Identifier tex;

    static {
        starsTexture = Identifier.fromNamespaceAndPath("minecraft", "textures/entity/end_portal.png");
    }

    public GuiVoidSiphon(ContainerVoidSiphon menu, Inventory inv, Component title) {
        super(menu, inv, title);
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_void_siphon.png");
        inventory = menu.getTile();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {}
}
