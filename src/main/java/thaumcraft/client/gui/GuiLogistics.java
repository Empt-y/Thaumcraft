package thaumcraft.client.gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.client.gui.plugins.GuiImageButton;
import thaumcraft.client.gui.plugins.GuiPlusMinusButton;
import thaumcraft.client.gui.plugins.GuiScrollButton;
import thaumcraft.client.gui.plugins.GuiSliderTC;
import thaumcraft.common.container.ContainerLogistics;


@OnlyIn(Dist.CLIENT)
public class GuiLogistics extends AbstractContainerScreen<ContainerLogistics>
{
    int selectedSlot;
    ContainerLogistics con;
    Level world;
    Player player;
    BlockPos target;
    Direction side;
    Identifier tex;
    long lu;
    int lastStackSize;
    int stackSize;
    boolean stacksizeUpdated;
    ItemStack selectedStack;
    int lastScrollPos;
    GuiSliderTC scrollbar;
    GuiSliderTC countbar;
    GuiPlusMinusButton countbutton1;
    GuiPlusMinusButton countbutton2;
    GuiImageButton requestbutton;
    EditBox searchField;

    public GuiLogistics(Inventory par1InventoryPlayer, Level world, BlockPos pos, Direction side) {
        super(new ContainerLogistics(par1InventoryPlayer, world), par1InventoryPlayer,
            Component.translatable("gui.logistics"), 215, 215);
        selectedSlot = -1;
        con = null;
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_logistics.png");
        lu = 0L;
        lastStackSize = 1;
        stackSize = 1;
        stacksizeUpdated = false;
        selectedStack = null;
        lastScrollPos = 0;
        this.world = world;
        this.player = par1InventoryPlayer.player;
        this.con = (ContainerLogistics) menu;
        this.target = pos;
        this.side = side;
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
