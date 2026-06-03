package thaumcraft.common.golems.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import thaumcraft.api.golems.seals.ISealConfigToggles;


public class GuiGolemPropButton extends Button
{
    ISealConfigToggles.SealToggle prop;
    static Identifier tex;

    public GuiGolemPropButton(int buttonId, int x, int y, int width, int height, String buttonText, ISealConfigToggles.SealToggle prop) {
        super(Button.builder(Component.literal(buttonText), b -> {}).pos(x, y).size(width, height));
        this.prop = prop;
    }

    public GuiGolemPropButton(int buttonId, int x, int y, int width, int height, String buttonText, ISealConfigToggles.SealToggle prop, Button.OnPress onPress) {
        super(Button.builder(Component.literal(buttonText), onPress).pos(x, y).size(width, height));
        this.prop = prop;
    }

    static {
        GuiGolemPropButton.tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_base.png");
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, tex, this.getX() - 2, this.getY() - 2, 2, 18, 12, 12, 256, 256);
        if (prop.getValue()) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, tex, this.getX() - 2, this.getY() - 2, 18, 18, 12, 12, 256, 256);
        }
        Minecraft mc = Minecraft.getInstance();
        graphics.text(mc.font, I18n.get(this.getMessage().getString()), this.getX() + 12, this.getY(), 0xFFFFFF, false);
    }
}
