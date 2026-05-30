package thaumcraft.client.gui;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.resources.Identifier;
import thaumcraft.api.research.ResearchEntry;


public class ResearchToast implements Toast
{
    ResearchEntry entry;
    private long firstDrawTime;
    private boolean newDisplay = true;
    private boolean done = false;
    Identifier tex;

    public ResearchToast(ResearchEntry entry) {
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/hud.png");
        this.entry = entry;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor extractor, Font font, long timeSinceLastVisible) {
        if (newDisplay) {
            firstDrawTime = timeSinceLastVisible;
            newDisplay = false;
        }
        // TODO: render toast using extractor
        if (timeSinceLastVisible - firstDrawTime >= 5000L) {
            done = true;
        }
    }

    @Override
    public Toast.Visibility getWantedVisibility() {
        return done ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
    }

    @Override
    public void update(net.minecraft.client.gui.components.toasts.ToastManager manager, long timeSinceLastVisible) {
    }
}
