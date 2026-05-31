package thaumcraft.common.entities.projectile;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.client.fx.FXDispatcher;


public class EntityBottleTaint extends ThrowableProjectile
{
    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        }

    public EntityBottleTaint(net.minecraft.world.entity.EntityType<? extends EntityBottleTaint> type, Level p_i1788_1_) {
        super(type, p_i1788_1_);
    }
    
    public EntityBottleTaint(Level p_i1790_1_, LivingEntity p_i1790_2) {
        super(null, p_i1790_1_);
        setOwner(p_i1790_2);
        setPos(p_i1790_2.getX(), p_i1790_2.getEyeY() - 0.1, p_i1790_2.getZ());
    }

    public EntityBottleTaint(Level worldIn, double x, double y, double z) {
        super(null, worldIn);
    }
    
    public void handleStatusUpdate(byte id) {
        if (id == 3) {
            for (int a = 0; a < 100; ++a) {
                FXDispatcher.INSTANCE.taintsplosionFX(this);
            }
            FXDispatcher.INSTANCE.bottleTaintBreak(getX(), getY(), getZ());
        }
    }
    
    protected void onImpact(HitResult ray) {
        if (!level().isClientSide()) {
            List ents = level().getEntitiesOfClass(LivingEntity.class, new AABB(getX(), getY(), getZ(), getX(), getY(), getZ()).inflate(5.0, 5.0, 5.0));
            if (ents.size() > 0) {
                for (Object ent : ents) {
                    LivingEntity el = (LivingEntity)ent;
                    if (!(el instanceof ITaintedMob) && !el.getType().builtInRegistryHolder().is(net.minecraft.tags.EntityTypeTags.UNDEAD)) {
                        el.addEffect(new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionFluxTaint.instance), 100, 0, false, true));
                    }
                }
            }
            for (int a = 0; a < 10; ++a) {
                int xx = (int)((getRandom().nextFloat() - getRandom().nextFloat()) * 4.0f);
                int zz = (int)((getRandom().nextFloat() - getRandom().nextFloat()) * 4.0f);
                BlockPos p = blockPosition().offset(xx, 0, zz);
                if (getRandom().nextBoolean()) {
                    if (level().getBlockState(p.below()).isSolidRender() && level().getBlockState(p).canBeReplaced()) {
                        level().setBlockAndUpdate(p, BlocksTC.fluxGoo.defaultBlockState());
                    } else {
                        p = p.below();
                        if (level().getBlockState(p.below()).isSolidRender() && level().getBlockState(p).canBeReplaced()) {
                            level().setBlockAndUpdate(p, BlocksTC.fluxGoo.defaultBlockState());
                        }
                    }
                }
            }
            level().broadcastEntityEvent(this, (byte)3);
            discard();
        }
    }
}
