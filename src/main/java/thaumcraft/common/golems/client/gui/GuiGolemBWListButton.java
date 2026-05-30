package thaumcraft.common.golems.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import thaumcraft.api.golems.seals.ISealConfigFilter;


public class GuiGolemBWListButton extends Button
{
    public ISealConfigFilter filter;
    public static Identifier tex;

    public GuiGolemBWListButton(int buttonId, int x, int y, int width, int height, ISealConfigFilter filter) {
        super(Button.builder(Component.literal(""), b -> {}).pos(x, y).size(width, height));
        this.filter = filter;
    }

    static {
        GuiGolemBWListButton.tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_base.png");
    }

    @Override
    public void extractContents(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        // FIXME: stub - button rendering not implemented
    }
}
