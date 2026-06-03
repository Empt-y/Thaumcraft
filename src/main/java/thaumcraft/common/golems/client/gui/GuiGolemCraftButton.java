package thaumcraft.common.golems.client.gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;


public class GuiGolemCraftButton extends Button
{
    static Identifier tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_golembuilder.png");

    public GuiGolemCraftButton(int x, int y, Button.OnPress onPress) {
        super(Button.builder(Component.empty(), onPress).pos(x, y).size(24, 16));
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // Craft button: UV (216,64) normal, UV (216,40) disabled, 24×16
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, tex,
                this.getX(), this.getY(), 216, 64, 24, 16, 256, 256);
        if (!this.active) {
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, tex,
                    this.getX(), this.getY(), 216, 40, 24, 16, 256, 256);
        }
    }

}
