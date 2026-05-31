package thaumcraft.client.gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.container.ContainerPotionSprayer;
import thaumcraft.common.tiles.devices.TilePotionSprayer;


@OnlyIn(Dist.CLIENT)
public class GuiPotionSprayer extends AbstractContainerScreen<ContainerPotionSprayer>
{
    private TilePotionSprayer inventory;
    private ContainerPotionSprayer container;
    private Player player;
    Identifier tex;
    int startAspect;

    public GuiPotionSprayer(ContainerPotionSprayer menu, Inventory inv, Component title) {
        super(menu, inv, title);
        container = null;
        player = null;
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_potion_sprayer.png");
        startAspect = 0;
        inventory = menu.getTile();
        container = menu;
        player = inv.player;
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
