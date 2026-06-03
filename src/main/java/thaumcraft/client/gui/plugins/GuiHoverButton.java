package thaumcraft.client.gui.plugins;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.aspects.Aspect;


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
        int dx = this.getX() - width / 2;
        int dy = this.getY() - height / 2;
        if (tex instanceof Aspect aspect) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, aspect.getImage(), dx, dy, 0, 0, 16, 16, 16, 16);
        } else if (tex instanceof Identifier id) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, id, dx, dy, 0, 0, 16, 16, 16, 16);
        } else if (tex instanceof ItemStack stack) {
            graphics.item(stack, dx, dy);
        }
        if (isHoveredOrFocused()) {
            var font = Minecraft.getInstance().font;
            java.util.List<Component> tooltip = new java.util.ArrayList<>();
            String label = this.getMessage().getString();
            if (!label.isEmpty()) tooltip.add(Component.literal(label));
            if (description != null && !description.isEmpty()) tooltip.add(Component.literal("§o§9" + description));
            if (!tooltip.isEmpty()) {
                graphics.setTooltipForNextFrame(font, tooltip, java.util.Optional.empty(), mouseX, mouseY);
            }
        }
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
