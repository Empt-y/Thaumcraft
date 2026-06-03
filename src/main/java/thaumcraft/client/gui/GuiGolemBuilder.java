package thaumcraft.client.gui;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.api.golems.parts.GolemAddon;
import thaumcraft.api.golems.parts.GolemArm;
import thaumcraft.api.golems.parts.GolemHead;
import thaumcraft.api.golems.parts.GolemLeg;
import thaumcraft.api.golems.parts.GolemMaterial;
import thaumcraft.common.container.ContainerGolemBuilder;
import thaumcraft.common.golems.GolemProperties;
import thaumcraft.common.golems.client.gui.GuiGolemCraftButton;
import thaumcraft.common.tiles.crafting.TileGolemBuilder;


public class GuiGolemBuilder extends AbstractContainerScreen<ContainerGolemBuilder>
{
    private TileGolemBuilder builder;
    private Player player;
    Identifier tex;
    ArrayList<GolemHead> valHeads;
    ArrayList<GolemMaterial> valMats;
    ArrayList<GolemArm> valArms;
    ArrayList<GolemLeg> valLegs;
    ArrayList<GolemAddon> valAddons;
    static int headIndex;
    static int matIndex;
    static int armIndex;
    static int legIndex;
    static int addonIndex;
    IGolemProperties props;
    float hearts;
    float armor;
    float damage;
    GuiGolemCraftButton craftButton;
    Identifier matIcon;
    int cost;
    boolean allfound;
    ItemStack[] components;
    boolean[] owns;
    boolean disableAll;

    public GuiGolemBuilder(ContainerGolemBuilder menu, Inventory inv, Component title) {
        super(menu, inv, title, 208, 224);
        this.builder = menu.builder;
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_golembuilder.png");
        valHeads = new ArrayList<GolemHead>();
        valMats = new ArrayList<GolemMaterial>();
        valArms = new ArrayList<GolemArm>();
        valLegs = new ArrayList<GolemLeg>();
        valAddons = new ArrayList<GolemAddon>();
        props = GolemProperties.fromLong(0L);
        hearts = 0.0f;
        damage = 0.0f;
        craftButton = null;
        matIcon = Identifier.fromNamespaceAndPath("thaumcraft", "textures/items/golem.png");
        cost = 0;
        allfound = false;
        components = null;
        owns = null;
        disableAll = false;
    }

    public GuiGolemBuilder(Inventory par1InventoryPlayer, TileGolemBuilder table) {
        super(new ContainerGolemBuilder(par1InventoryPlayer, table), par1InventoryPlayer,
            Component.translatable("gui.golembuilder"), 208, 224);
        this.builder = table;
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_golembuilder.png");
        valHeads = new ArrayList<GolemHead>();
        valMats = new ArrayList<GolemMaterial>();
        valArms = new ArrayList<GolemArm>();
        valLegs = new ArrayList<GolemLeg>();
        valAddons = new ArrayList<GolemAddon>();
        props = GolemProperties.fromLong(0L);
        hearts = 0.0f;
        
        damage = 0.0f;
        craftButton = null;
        matIcon = Identifier.fromNamespaceAndPath("thaumcraft", "textures/items/golem.png");
        cost = 0;
        allfound = false;
        components = null;
        owns = null;
        disableAll = false;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        int x = this.leftPos;
        int y = this.topPos;
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, tex, x, y, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
        // Highlight missing components with a "not owned" overlay
        if (components != null && owns != null) {
            int row = 1, col = 0;
            for (int a = 0; a < components.length; a++) {
                if (!owns[a]) {
                    graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, tex,
                            x + 144 + col * 16, y + 16 + 16 * row, 240, 0, 16, 16, 256, 256);
                }
                if (++row > 3) { row = 0; col++; }
            }
        }
        // Crafting progress bar
        if (builder != null && builder.cost > 0) {
            int barW = (int)(46.0f * (1.0f - builder.cost / (float) Math.max(1, builder.maxCost)));
            graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, tex, x + 145, y + 89, 209, 89, barW, 6, 256, 256);
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (components != null && components.length > 0) {
            String costStr = "" + cost;
            graphics.text(this.font, costStr, 162 - this.font.width(costStr), 24, 0xFFFFFF, true);
        }
    }
}
