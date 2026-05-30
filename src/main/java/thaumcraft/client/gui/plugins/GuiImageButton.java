package thaumcraft.client.gui.plugins;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;


public class GuiImageButton extends AbstractButton
{
    Screen screen;
    Identifier loc;
    int lx;
    int ly;
    int ww;
    int hh;
    public String description;
    public int color;

    public GuiImageButton(Screen screen, int buttonId, int x, int y, int width, int height, String buttonText, String description, Identifier loc, int lx, int ly, int ww, int hh) {
        super(x, y, width, height, Component.literal(buttonText != null ? buttonText : ""));
        this.description = description;
        this.screen = screen;
        color = 16777215;
        this.loc = loc;
        this.lx = lx;
        this.ly = ly;
        this.ww = ww;
        this.hh = hh;
    }

    public GuiImageButton(Screen screen, int buttonId, int x, int y, int width, int height, String buttonText, String description, Identifier loc, int lx, int ly, int ww, int hh, int color) {
        super(x, y, width, height, Component.literal(buttonText != null ? buttonText : ""));
        this.description = description;
        this.screen = screen;
        this.color = color;
        this.loc = loc;
        this.lx = lx;
        this.ly = ly;
        this.ww = ww;
        this.hh = hh;
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
