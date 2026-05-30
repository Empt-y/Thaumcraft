package thaumcraft.client.gui.plugins;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;


public class GuiScrollButton extends AbstractButton
{
    boolean minus;
    boolean vertical;
    static Identifier tex;

    public GuiScrollButton(int buttonId, int x, int y, int width, int height, boolean minus, boolean vertical) {
        super(x, y, width, height, Component.empty());
        this.minus = minus;
        this.vertical = vertical;
    }

    public GuiScrollButton(int buttonId, int x, int y, int width, int height, boolean minus) {
        super(x, y, width, height, Component.empty());
        this.minus = minus;
        this.vertical = false;
    }

    @Override
    public void onPress(InputWithModifiers input) {
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        // rendering stub
    }

    @Override
    public void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    static {
        GuiScrollButton.tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_base.png");
    }
}
