package thaumcraft.client.gui;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.common.container.ContainerThaumatorium;
import thaumcraft.common.tiles.crafting.TileThaumatorium;


@OnlyIn(Dist.CLIENT)
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

    public GuiThaumatorium(Inventory par1InventoryPlayer, TileThaumatorium par2BlockEntityFurnace) {
        super(new ContainerThaumatorium(par1InventoryPlayer, par2BlockEntityFurnace), par1InventoryPlayer,
            Component.translatable("gui.thaumatorium"), 175, 216);
        container = null;
        index = 0;
        lastSize = 0;
        player = null;
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_thaumatorium.png");
        hashList = new ArrayList<Integer>();
        lastHLUpdate = 0L;
        inventory = par2BlockEntityFurnace;
        container = (ContainerThaumatorium) menu;
        player = par1InventoryPlayer.player;
        inventory.updateRecipes(player);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        // rendering stub
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        // rendering stub
    }
}
