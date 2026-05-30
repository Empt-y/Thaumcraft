package thaumcraft.client.gui.plugins;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import thaumcraft.api.casters.NodeSetting;


public class GuiFocusSettingSpinnerButton extends AbstractButton
{
    private NodeSetting setting;
    static Identifier tex;

    public GuiFocusSettingSpinnerButton(int buttonId, int x, int y, int width, NodeSetting ns) {
        super(x, y, width, 10, Component.empty());
        setting = ns;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        // position-based click handled in onClick
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        int mouseX = (int) event.x();
        int mouseY = (int) event.y();
        if (active && visible) {
            if (mouseX >= getX() && mouseY >= getY() && mouseX < getX() + 10 && mouseY < getY() + height) {
                setting.decrement();
            } else if (mouseX >= getX() + width && mouseY >= getY() && mouseX < getX() + width + 10 && mouseY < getY() + height) {
                setting.increment();
            }
        }
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
        GuiFocusSettingSpinnerButton.tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_base.png");
    }
}
