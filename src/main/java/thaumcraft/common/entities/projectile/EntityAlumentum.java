package thaumcraft.common.entities.projectile;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import thaumcraft.client.fx.FXDispatcher;


public class EntityAlumentum extends ThrowableProjectile
{
    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        }

    public EntityAlumentum(net.minecraft.world.entity.EntityType<? extends EntityAlumentum> type, Level par1World) {
        super(type, par1World);
    }
    
    public EntityAlumentum(Level par1World, LivingEntity par2Mob) {
        super(null, par1World);
        setOwner(par2Mob);
        setPos(par2Mob.getX(), par2Mob.getEyeY() - 0.1, par2Mob.getZ());
    }
    
    public EntityAlumentum(Level par1World, double par2, double par4, double par6) {
        super(null, par1World);
    }
    
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, 0.75f, inaccuracy);
    }
    
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            for (double i = 0.0; i < 3.0; ++i) {
                double coeff = i / 3.0;
                FXDispatcher.INSTANCE.drawAlumentum((float)(xo + (getX() - xo) * coeff), (float)(yo + (getY() - yo) * coeff) + getBbHeight() / 2.0f, (float)(zo + (getZ() - zo) * coeff), 0.0125f * (getRandom().nextFloat() - 0.5f), 0.0125f * (getRandom().nextFloat() - 0.5f), 0.0125f * (getRandom().nextFloat() - 0.5f), getRandom().nextFloat() * 0.2f, getRandom().nextFloat() * 0.1f, getRandom().nextFloat() * 0.1f, 0.5f, 4.0f);
                FXDispatcher.INSTANCE.drawGenericParticles(getX() + getRandom().nextGaussian() * 0.20000000298023224, getY() + getRandom().nextGaussian() * 0.20000000298023224, getZ() + getRandom().nextGaussian() * 0.20000000298023224, 0.0, 0.0, 0.0, 1.0f, 1.0f, 1.0f, 0.7f, false, 448, 8, 1, 8, 0, 0.3f, 0.0f, 1);
            }
        }
    }
    
    protected void onImpact(HitResult par1HitResult) {
        if (!level().isClientSide()) {
            level().explode(this, getX(), getY(), getZ(), 1.1f, net.minecraft.world.level.Level.ExplosionInteraction.BLOCK);
            discard();
        }
    }
}
