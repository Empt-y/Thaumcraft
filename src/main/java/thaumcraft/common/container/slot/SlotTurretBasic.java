package thaumcraft.common.container.slot;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.entities.construct.EntityTurretCrossbow;


public class SlotTurretBasic extends SlotMobEquipment
{
    public SlotTurretBasic(EntityTurretCrossbow turret, int par3, int par4, int par5) {
        super(turret, par3, par4, par5);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ArrowItem;
    }

    @Override
    public void setChanged() {
    }
}
