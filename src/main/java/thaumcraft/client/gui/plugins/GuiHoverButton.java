package thaumcraft.client.gui.plugins;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class GuiHoverButton extends AbstractButton
{
    String description;
    Screen screen;
    int color;
    Object tex;

    public GuiHoverButton(Screen screen, int buttonId, int x, int y, int width, int height, String buttonText, String description, Object tex) {
        super(x, y, width, height, Component.literal(buttonText != null ? buttonText : ""));
        this.description = description;
        this.tex = tex;
        this.screen = screen;
        color = 16777215;
    }

    public GuiHoverButton(Screen screen, int buttonId, int x, int y, int width, int height, String buttonText, String description, Object tex, int color) {
        super(x, y, width, height, Component.literal(buttonText != null ? buttonText : ""));
        this.description = description;
        this.tex = tex;
        this.screen = screen;
        this.color = color;
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

    public void drawButton(int xx, int yy) {
    }

    public void drawButtonForegroundLayer(int xx, int yy) {
    }
}
