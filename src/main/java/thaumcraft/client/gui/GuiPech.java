package thaumcraft.client.gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.container.ContainerPech;
import thaumcraft.common.entities.monster.EntityPech;


@OnlyIn(Dist.CLIENT)
public class GuiPech extends AbstractContainerScreen<ContainerPech>
{
    EntityPech pech;
    Identifier tex;

    public GuiPech(Inventory par1InventoryPlayer, Level world, EntityPech pech) {
        super(new ContainerPech(par1InventoryPlayer, world, pech), par1InventoryPlayer,
            Component.translatable("gui.pech"), 175, 232);
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_pech.png");
        this.pech = pech;
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
