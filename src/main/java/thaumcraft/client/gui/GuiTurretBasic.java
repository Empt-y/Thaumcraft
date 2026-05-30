package thaumcraft.client.gui;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.container.ContainerTurretBasic;
import thaumcraft.common.entities.construct.EntityTurretCrossbow;


@OnlyIn(Dist.CLIENT)
public class GuiTurretBasic extends AbstractContainerScreen<ContainerTurretBasic>
{
    EntityTurretCrossbow turret;
    Identifier tex;

    public GuiTurretBasic(Inventory par1InventoryPlayer, Level world, EntityTurretCrossbow t) {
        super(new ContainerTurretBasic(par1InventoryPlayer, world, t), par1InventoryPlayer,
            Component.translatable("gui.turretbasic"), 175, 232);
        tex = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/gui_turret_basic.png");
        turret = t;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {}

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {}
}
