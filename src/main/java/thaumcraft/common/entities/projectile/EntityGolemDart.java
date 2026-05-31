package thaumcraft.common.entities.projectile;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;


public class EntityGolemDart extends AbstractArrow
{
    @Override
    public net.minecraft.world.item.ItemStack getDefaultPickupItem() {
        return net.minecraft.world.item.ItemStack.EMPTY;
    }

    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        }

    public EntityGolemDart(net.minecraft.world.entity.EntityType<? extends EntityGolemDart> type, Level par1World) {
        super(type, par1World);
    }
    
    public EntityGolemDart(Level par1World, double par2, double par4, double par6) {
        super(null, par1World);
    }
    
    public EntityGolemDart(Level par1World, LivingEntity par2LivingEntity) {
        super(null, par1World);
        setOwner(par2LivingEntity);
        setPos(par2LivingEntity.getX(), par2LivingEntity.getEyeY() - 0.1, par2LivingEntity.getZ());
    }
    
    protected ItemStack getArrowStack() {
        return new ItemStack(Items.ARROW);
    }
}
