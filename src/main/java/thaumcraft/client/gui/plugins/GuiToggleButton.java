package thaumcraft.client.gui.plugins;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;


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
        // Toggle button: draw small icon at UV (192,16) or (192,24) depending on toggle state, 8×8
        int v = toggled ? 24 : 16;
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED,
                Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_base.png"),
                this.getX(), this.getY(), 192, v, 8, 8, 256, 256);
    }

    @Override
    public void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }
}
