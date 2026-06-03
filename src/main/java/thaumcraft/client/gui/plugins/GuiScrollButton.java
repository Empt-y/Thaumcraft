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
        int u = vertical ? 67 : (minus ? 20 : 30);
        int v = vertical ? (minus ? 0 : 10) : 0;
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, tex,
                this.getX(), this.getY(), u, v, 10, 10, 256, 256);
    }

    @Override
    public void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    static {
        GuiScrollButton.tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_base.png");
    }
}
