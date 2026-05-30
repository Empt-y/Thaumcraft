package thaumcraft.common.golems.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import thaumcraft.api.golems.seals.ISealConfigToggles;


public class GuiGolemPropButton extends Button
{
    ISealConfigToggles.SealToggle prop;
    static Identifier tex;

    public GuiGolemPropButton(int buttonId, int x, int y, int width, int height, String buttonText, ISealConfigToggles.SealToggle prop) {
        super(Button.builder(Component.literal(buttonText), b -> {}).pos(x, y).size(width, height));
        this.prop = prop;
    }

    static {
        GuiGolemPropButton.tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_base.png");
    }

    @Override
    public void extractContents(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        // FIXME: stub - button rendering not implemented
    }
}
