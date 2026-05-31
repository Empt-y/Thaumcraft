package thaumcraft.common.lib;

import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import thaumcraft.common.entities.projectile.EntityAlumentum;


public class BehaviorDispenseAlumetum extends DefaultDispenseItemBehavior
{
    @Override
    public ItemStack execute(BlockSource source, ItemStack stack) {
        Direction direction = source.state().getValue(net.minecraft.world.level.block.DispenserBlock.FACING);
        EntityAlumentum alumentum = new EntityAlumentum(source.level(), source.center().x, source.center().y, source.center().z);
        alumentum.shoot(direction.getStepX(), direction.getStepY() + 0.1, direction.getStepZ(), 1.1f, 2.5f);
        source.level().addFreshEntity(alumentum);
        stack.shrink(1);
        return stack;
    }
}
