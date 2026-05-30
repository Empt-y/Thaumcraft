package thaumcraft.client.gui.plugins;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;


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
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        // rendering stub
    }

    @Override
    public void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    static {
        GuiSliderTC.tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_base.png");
    }

    @OnlyIn(Dist.CLIENT)
    public interface FormatHelper
    {
        String getText(int p0, String p1, float p2);
    }
}
