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
        // Background: hud.png region (0, 224) size 160x32
        extractor.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, tex, 0, 0, 0, 224, 160, 32, 256, 256);
        // Research entry icon at (6, 8)
        if (entry != null) {
            GuiResearchBrowser.drawResearchIcon(entry, 6, 8, 0.0f, false);
        }
        // "Research Complete" label
        extractor.text(font, net.minecraft.client.resources.language.I18n.get("research.complete"), 30, 7, 0xA2D169, false);
        // Research name (possibly scaled)
        if (entry != null) {
            String s = entry.getLocalizedName();
            int sw = font.width(s);
            if (sw > 124) {
                // Clamp: just draw truncated
                extractor.text(font, s, 30, 18, 0xFFCC55, false);
            } else {
                extractor.text(font, s, 30, 18, 0xFFCC55, false);
            }
        }
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
