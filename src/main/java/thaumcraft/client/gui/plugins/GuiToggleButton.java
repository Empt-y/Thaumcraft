package thaumcraft.client.gui.plugins;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;


public class GuiToggleButton extends AbstractButton
{
    Runnable runnable;
    public static boolean toggled;

    public GuiToggleButton(int buttonId, int x, int y, int width, int height, String buttonText, Runnable runnable) {
        super(x, y, width, height, Component.literal(buttonText != null ? buttonText : ""));
        this.runnable = runnable;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        if (runnable != null) runnable.run();
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        // rendering stub
    }

    @Override
    public void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}
