package thaumcraft.common.golems.client.gui;
import java.util.function.Consumer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import thaumcraft.api.golems.seals.ISealEntity;


public class GuiGolemLockButton extends AbstractButton
{
    ISealEntity seal;
    Consumer<AbstractButton> onPressCallback;
    static Identifier tex;

    public GuiGolemLockButton(int buttonId, int x, int y, int width, int height, ISealEntity seal) {
        super(x, y, width, height, Component.empty());
        this.seal = seal;
    }

    public GuiGolemLockButton(int buttonId, int x, int y, int width, int height, ISealEntity seal, Consumer<AbstractButton> callback) {
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
        // Locked: UV (32,136), unlocked: UV (48,136), 16×16
        int u = seal.isLocked() ? 32 : 48;
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, tex,
                this.getX(), this.getY(), u, 136, 16, 16, 256, 256);
        if (isHoveredOrFocused()) {
            var font = net.minecraft.client.Minecraft.getInstance().font;
            String s = net.minecraft.client.resources.language.I18n.get(seal.isLocked() ? "golem.prop.lock" : "golem.prop.unlock");
            graphics.text(font, s, this.getX() + 8 - font.width(s) / 2, this.getY() + 17, 0xFFFFFF, false);
        }
    }

    @Override
    public void updateWidgetNarration(net.minecraft.client.gui.narration.NarrationElementOutput output) {
        this.defaultButtonNarrationText(output);
    }

    static {
        GuiGolemLockButton.tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_base.png");
    }
}
