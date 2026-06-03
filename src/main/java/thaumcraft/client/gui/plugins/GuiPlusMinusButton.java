package thaumcraft.client.gui.plugins;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;


public class GuiPlusMinusButton extends AbstractButton
{
    boolean minus;
    Consumer<AbstractButton> onPressCallback;
    static Identifier tex;

    public GuiPlusMinusButton(int buttonId, int x, int y, int width, int height, boolean left) {
        super(x, y, width, height, Component.empty());
        minus = left;
    }

    public GuiPlusMinusButton(int buttonId, int x, int y, int width, int height, boolean left, Consumer<AbstractButton> callback) {
        super(x, y, width, height, Component.empty());
        minus = left;
        onPressCallback = callback;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        if (onPressCallback != null) onPressCallback.accept(this);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        // minus=true → left arrow at u=0, plus → right arrow at u=10; both at v=0, 10×10
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, tex,
                this.getX(), this.getY(), minus ? 0 : 10, 0, 10, 10, 256, 256);
    }

    @Override
    public void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    static {
        GuiPlusMinusButton.tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_base.png");
    }
}
