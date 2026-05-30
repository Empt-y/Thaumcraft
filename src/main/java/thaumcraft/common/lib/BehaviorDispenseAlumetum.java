package thaumcraft.common.lib;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.core.BlockPos; // net.minecraft.core.dispenser.BlockSource /* IPosition removed */ removed
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import thaumcraft.common.entities.projectile.EntityAlumentum;


public class BehaviorDispenseAlumetum extends ProjectileDispenseBehavior
{
    protected Projectile getProjectileEntity(Level worldIn, net.minecraft.core.dispenser.BlockSource /* IPosition removed */ position, ItemStack stackIn) {
        return new EntityAlumentum(worldIn, position.center().x, position.center().y, position.center().z);
    }
}
