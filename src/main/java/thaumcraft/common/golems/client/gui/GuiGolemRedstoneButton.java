package thaumcraft.common.golems.client.gui;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import thaumcraft.api.golems.seals.ISealEntity;


public class GuiGolemRedstoneButton extends AbstractButton
{
    ISealEntity seal;
    Consumer<AbstractButton> onPressCallback;
    static Identifier tex;

    public GuiGolemRedstoneButton(int buttonId, int x, int y, int width, int height, ISealEntity seal) {
        super(x, y, width, height, Component.empty());
        this.seal = seal;
    }

    public GuiGolemRedstoneButton(int buttonId, int x, int y, int width, int height, ISealEntity seal, Consumer<AbstractButton> callback) {
        super(x, y, width, height, Component.empty());
        this.seal = seal;
        this.onPressCallback = callback;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        if (onPressCallback != null) onPressCallback.accept(this);
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
        GuiGolemRedstoneButton.tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_base.png");
    }
}
