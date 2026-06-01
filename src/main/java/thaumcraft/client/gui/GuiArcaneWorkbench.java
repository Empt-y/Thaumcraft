package thaumcraft.client.gui;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.blocks.world.ore.ShardType;
import thaumcraft.common.container.ContainerArcaneWorkbench;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;


public class GuiArcaneWorkbench extends AbstractContainerScreen<ContainerArcaneWorkbench>
{
    private TileArcaneWorkbench tileEntity;
    Identifier tex;

    public GuiArcaneWorkbench(ContainerArcaneWorkbench menu, Inventory inv, Component title) {
        super(menu, inv, title, 190, 234);
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/arcaneworkbench.png");
        tileEntity = menu.getTile();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, tex, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);

        IArcaneRecipe result = ThaumcraftCraftingManager.findMatchingArcaneRecipe(tileEntity.inventoryCraft, this.minecraft.player);
        AspectList crystals = null;
        int cost = 0;
        if (result != null) {
            float df = CasterManager.getTotalVisDiscount(this.minecraft.player);
            cost = (int)(result.getVis() * (1.0f - df));
            crystals = result.getCrystals();
        }

        // Draw crystal glow overlays for each required aspect
        if (crystals != null) {
            long ticks = this.minecraft.player.tickCount;
            for (Aspect a : crystals.getAspects()) {
                int id = ShardType.getMetaByAspect(a);
                Color col = new Color(a.getColor());
                // Pulsing alpha based on time
                float alpha = 0.25f + 0.1f * (float)Math.sin((ticks + partialTick) * 0.2);
                int argb = (int)(alpha * 255) << 24 | ((col.getRed() & 0xFF) << 16) | ((col.getGreen() & 0xFF) << 8) | (col.getBlue() & 0xFF);
                int cx = x + ContainerArcaneWorkbench.xx[id];
                int cy = y + ContainerArcaneWorkbench.yy[id];
                // Draw colored overlay at the crystal slot position (16×16 glow)
                graphics.fill(cx - 4, cy - 4, cx + 20, cy + 20, argb);
            }
        }

        // Vis cost label
        if (cost > 0) {
            String label = "Vis: " + cost;
            graphics.text(this.font, label, x + 152 - this.font.width(label) / 2, y + 8, 0x55AAFF, true);
        }
    }
}
