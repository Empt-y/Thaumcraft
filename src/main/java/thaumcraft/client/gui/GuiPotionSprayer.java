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

    public GuiPotionSprayer(Inventory par1InventoryPlayer, TilePotionSprayer tilePotionSprayer) {
        super(new ContainerPotionSprayer(par1InventoryPlayer, tilePotionSprayer), par1InventoryPlayer,
            Component.translatable("gui.potionsprayer"), 192, 233);
        container = null;
        player = null;
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_potion_sprayer.png");
        startAspect = 0;
        inventory = tilePotionSprayer;
        container = (ContainerPotionSprayer) menu;
        player = par1InventoryPlayer.player;
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
