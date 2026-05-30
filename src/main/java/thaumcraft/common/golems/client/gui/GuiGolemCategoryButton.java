package thaumcraft.common.golems.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;


class GuiGolemCategoryButton extends Button
{
    int icon;
    boolean active;
    static Identifier tex;

    public GuiGolemCategoryButton(int buttonId, int x, int y, int width, int height, String buttonText, int i, boolean act) {
        super(Button.builder(Component.literal(buttonText), b -> {}).pos(x, y).size(width, height));
        icon = i;
        active = act;
    }

    static {
        GuiGolemCategoryButton.tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_base.png");
    }

    @Override
    public void extractContents(GuiGraphicsExtractor extractor, int mouseX, int mouseY, float partialTick) {
        // FIXME: stub - button rendering not implemented
    }
}
