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
        // rendering stub
    }

}
