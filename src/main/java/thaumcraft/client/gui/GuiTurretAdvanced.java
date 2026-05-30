package thaumcraft.client.gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.container.ContainerTurretAdvanced;
import thaumcraft.common.entities.construct.EntityTurretCrossbowAdvanced;


@OnlyIn(Dist.CLIENT)
public class GuiTurretAdvanced extends AbstractContainerScreen<ContainerTurretAdvanced>
{
    EntityTurretCrossbowAdvanced turret;
    public static Identifier tex;

    static {
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_turret_advanced.png");
    }

    public GuiTurretAdvanced(Inventory par1InventoryPlayer, Level world, EntityTurretCrossbowAdvanced t) {
        super(new ContainerTurretAdvanced(par1InventoryPlayer, world, t), par1InventoryPlayer,
            Component.translatable("gui.turretadvanced"), 175, 232);
        turret = t;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {}
}
