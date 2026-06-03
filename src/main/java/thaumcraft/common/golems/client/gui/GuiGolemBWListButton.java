package thaumcraft.common.golems.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import thaumcraft.api.golems.seals.ISealConfigFilter;


public class GuiGolemBWListButton extends Button
{
    public ISealConfigFilter filter;
    public static Identifier tex;

    public GuiGolemBWListButton(int buttonId, int x, int y, int width, int height, ISealConfigFilter filter) {
        super(Button.builder(Component.literal(""), b -> {}).pos(x, y).size(width, height));
        this.filter = filter;
    }

    public GuiGolemBWListButton(int buttonId, int x, int y, int width, int height, ISealConfigFilter filter, Button.OnPress onPress) {
        super(Button.builder(Component.literal(""), onPress).pos(x, y).size(width, height));
        this.filter = filter;
    }

    static {
        GuiGolemBWListButton.tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_base.png");
    }

    @Override
    public void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int u = filter.isBlacklist() ? 0 : 16;
        graphics.blit(RenderPipelines.GUI_TEXTURED, tex, this.getX(), this.getY(), u, 136, 16, 16, 256, 256);
        if (isHoveredOrFocused()) {
            Minecraft mc = Minecraft.getInstance();
            String label = I18n.get(filter.isBlacklist() ? "button.bl" : "button.wl");
            graphics.text(mc.font, label, this.getX() + 8 - mc.font.width(label) / 2, this.getY() + 17, 0xFFFFFF, false);
        }
    }
}
