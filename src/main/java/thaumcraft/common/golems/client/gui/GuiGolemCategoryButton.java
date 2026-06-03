package thaumcraft.common.golems.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;


class GuiGolemCategoryButton extends Button
{
    int icon;
    boolean active;
    static Identifier tex;

    public GuiGolemCategoryButton(int buttonId, int x, int y, int width, int height, String buttonText, int i, boolean act) {
        super(Button.builder(Component.literal(buttonText), b -> {}).pos(x, y).size(width, height));
        icon = i;
        active = act;
    }

    public GuiGolemCategoryButton(int buttonId, int x, int y, int width, int height, String buttonText, int i, boolean act, Button.OnPress onPress) {
        super(Button.builder(Component.literal(buttonText), onPress).pos(x, y).size(width, height));
        icon = i;
        active = act;
    }

    static {
        GuiGolemCategoryButton.tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_base.png");
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, tex, this.getX() - 8, this.getY() - 8, icon * 16, 120, 16, 16, 256, 256);
        if (isHoveredOrFocused()) {
            Minecraft mc = Minecraft.getInstance();
            String s = I18n.get(this.getMessage().getString());
            graphics.text(mc.font, s, this.getX() - 10 - mc.font.width(s), this.getY() - 4, 0xFFFFFF, false);
        }
    }
}
