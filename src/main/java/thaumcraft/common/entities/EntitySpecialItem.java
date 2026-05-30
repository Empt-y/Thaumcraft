package thaumcraft.common.entities;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Level;


public class EntitySpecialItem extends ItemEntity
{
    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    public EntitySpecialItem(Level par1World, double par2, double par4, double par6, ItemStack par8ItemStack) {
        super(null, par1World);
        // Entity requires EntityType; use factory method
        // FIXME: setSize removed; dimensions set in EntityType builder
        setPos(par2, par4, par6);
        setItem(par8ItemStack);
        setYRot((float)(Math.random() * 360.0));
        setDeltaMovement((float)(Math.random() * 0.20000000298023224 - 0.10000000149011612), getDeltaMovement().y, getDeltaMovement().z);
        setDeltaMovement(getDeltaMovement().x, 0.20000000298023224, getDeltaMovement().z);
        setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, (float)(Math.random() * 0.20000000298023224 - 0.10000000149011612));
    }
    
    public EntitySpecialItem(net.minecraft.world.entity.EntityType<? extends EntitySpecialItem> type, Level par1World) {
        super(type, par1World);
        // FIXME: setSize removed; dimensions set in EntityType builder
    }
    
    public void tick() {
        if (tickCount > 1) {
            if (getDeltaMovement().y > 0.0) {
                setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y * 0.8999999761581421, getDeltaMovement().z);
            }
            setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y + 0.03999999910593033, getDeltaMovement().z);
            super.tick();
        }
    }
    
    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, DamageSource source, float damage) {
        if (source.is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION)) return false;
        return super.hurtServer(level, source, damage);
    }
}
