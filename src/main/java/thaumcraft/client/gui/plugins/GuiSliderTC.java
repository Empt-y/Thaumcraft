package thaumcraft.client.gui.plugins;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;


public class GuiSliderTC extends AbstractButton
{
    private float sliderPosition;
    public boolean isMouseDown;
    private String name;
    private float min;
    private float max;
    private boolean vertical;
    static Identifier tex;

    public GuiSliderTC(int buttonId, int x, int y, int w, int h, String name, float min, float max, float defaultValue, boolean vertical) {
        super(x, y, w, h, Component.empty());
        sliderPosition = 1.0f;
        this.name = name;
        this.min = min;
        this.max = max;
        sliderPosition = (defaultValue - min) / (max - min);
        this.vertical = vertical;
    }

    public float getMax() { return max; }
    public float getMin() { return min; }
    public void setMax(float max) { this.max = max; sliderPosition = 0.0f; }
    public float getSliderValue() { return min + (max - min) * sliderPosition; }
    public void setSliderValue(float value, boolean b) { sliderPosition = (value - min) / (max - min); }
    public float getSliderPosition() { return sliderPosition; }
    public void setSliderPosition(float p) { sliderPosition = p; }

    @Override
    public void onPress(InputWithModifiers input) {
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        updateSlider(event.x(), event.y());
        isMouseDown = true;
    }

    @Override
    protected void onDrag(MouseButtonEvent event, double dragX, double dragY) {
        if (isMouseDown) {
            updateSlider(event.x(), event.y());
        }
    }

    @Override
    public void onRelease(MouseButtonEvent event) {
        isMouseDown = false;
    }

    private void updateSlider(double mouseX, double mouseY) {
        if (vertical) {
            sliderPosition = (float)((mouseY - (getY() + 4)) / (height - 8));
        } else {
            sliderPosition = (float)((mouseX - (getX() + 4)) / (width - 8));
        }
        sliderPosition = Math.max(0.0f, Math.min(1.0f, sliderPosition));
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        // Draw track
        if (vertical) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, tex,
                    getX() + 2, getY(), 240, 176, 4, height, 256, 256);
        } else {
            graphics.blit(RenderPipelines.GUI_TEXTURED, tex,
                    getX(), getY() + 2, 208, 176, width, 4, 256, 256);
        }
        // Draw thumb at UV (20, 20) size 8×8
        if (vertical) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, tex,
                    getX(), getY() + (int)(sliderPosition * (height - 8)), 20, 20, 8, 8, 256, 256);
        } else {
            graphics.blit(RenderPipelines.GUI_TEXTURED, tex,
                    getX() + (int)(sliderPosition * (width - 8)), getY(), 20, 20, 8, 8, 256, 256);
        }
    }

    @Override
    public void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    static {
        GuiSliderTC.tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_base.png");
    }

    public interface FormatHelper
    {
        String getText(int p0, String p1, float p2);
    }
}
