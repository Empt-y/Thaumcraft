package thaumcraft.client.gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.container.ContainerArcaneBore;
import thaumcraft.common.entities.construct.EntityArcaneBore;


@OnlyIn(Dist.CLIENT)
public class GuiArcaneBore extends AbstractContainerScreen<ContainerArcaneBore>
{
    EntityArcaneBore turret;
    Identifier tex;

    public GuiArcaneBore(Inventory par1InventoryPlayer, Level world, EntityArcaneBore t) {
        super(new ContainerArcaneBore(par1InventoryPlayer, world, t), par1InventoryPlayer,
            Component.translatable("gui.arcanebore"), 175, 232);
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_arcanebore.png");
        turret = t;
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
