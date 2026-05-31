package thaumcraft.client.gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.container.ContainerPotionSprayer;
import thaumcraft.common.tiles.devices.TilePotionSprayer;


public class GuiPotionSprayer extends AbstractContainerScreen<ContainerPotionSprayer>
{
    private TilePotionSprayer inventory;
    private ContainerPotionSprayer container;
    private Player player;
    Identifier tex;
    int startAspect;

    public GuiPotionSprayer(ContainerPotionSprayer menu, Inventory inv, Component title) {
        super(menu, inv, title, 192, 233);
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
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, tex, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}
