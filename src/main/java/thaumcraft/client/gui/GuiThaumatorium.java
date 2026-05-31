package thaumcraft.client.gui;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.common.container.ContainerThaumatorium;
import thaumcraft.common.tiles.crafting.TileThaumatorium;


public class GuiThaumatorium extends AbstractContainerScreen<ContainerThaumatorium>
{
    private TileThaumatorium inventory;
    private ContainerThaumatorium container;
    private int index;
    private int lastSize;
    private Player player;
    Identifier tex;
    ArrayList<Integer> hashList;
    long lastHLUpdate;
    static HashMap<Integer, CrucibleRecipe> recipeCache;

    public GuiThaumatorium(ContainerThaumatorium menu, Inventory inv, Component title) {
        super(menu, inv, title, 175, 216);
        container = null;
        index = 0;
        lastSize = 0;
        player = null;
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_thaumatorium.png");
        hashList = new ArrayList<Integer>();
        lastHLUpdate = 0L;
        inventory = menu.getTile();
        container = menu;
        player = inv.player;
        if (inventory != null) inventory.updateRecipes(player);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        graphics.blit(RenderPipelines.GUI_TEXTURED, tex, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }
}
