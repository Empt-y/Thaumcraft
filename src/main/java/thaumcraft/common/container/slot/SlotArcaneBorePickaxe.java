package thaumcraft.common.container.slot;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.entities.construct.EntityArcaneBore;


public class SlotArcaneBorePickaxe extends SlotMobEquipment
{
    public SlotArcaneBorePickaxe(EntityArcaneBore turret, int par3, int par4, int par5) {
        super(turret, par3, par4, par5);
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return !stack.isEmpty() && stack.is(net.minecraft.tags.ItemTags.PICKAXES);
    }

    @Override
    public void setChanged() {
    }
}
