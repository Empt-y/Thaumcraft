package thaumcraft.common.golems.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.common.golems.client.gui.SealBaseContainer;


public class SealBaseGUI extends AbstractContainerScreen<SealBaseContainer> {

    public SealBaseGUI(SealBaseContainer container, Inventory playerInventory, Component title) {
        super(container, playerInventory, title);
    }

        protected void renderBg(GuiGraphicsExtractor graphics, float partialTick, int mouseX, int mouseY) {
        // FIXME: stub - GUI rendering not implemented
    }
}
