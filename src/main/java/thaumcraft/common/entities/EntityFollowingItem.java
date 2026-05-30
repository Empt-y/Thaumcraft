package thaumcraft.common.entities;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
// removed: import net.minecraftforge.fml.common.registry.Object /* IEntityAdditionalSpawnData removed */;
import thaumcraft.client.fx.FXDispatcher;


public class EntityFollowingItem extends EntitySpecialItem 
{
    @Override
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    double targetX;
    double targetY;
    double targetZ;
    int type;
    public Entity target;
    int age;
    public double gravity;
    
    public EntityFollowingItem(Level par1World, double par2, double par4, double par6, ItemStack par8ItemStack) {
        super(null, par1World);
        // Entity requires EntityType; use factory method
        targetX = 0.0;
        targetY = 0.0;
        targetZ = 0.0;
        type = 3;
        target = null;
        age = 20;
        gravity = 0.03999999910593033;
        // FIXME: setSize removed; dimensions set in EntityType builder
        setPos(par2, par4, par6);
        setItem(par8ItemStack);
        setYRot((float)(Math.random() * 360.0));
    }
    
    public EntityFollowingItem(Level par1World, double par2, double par4, double par6, ItemStack par8ItemStack, Entity target, int t) {
        this(par1World, par2, par4, par6, par8ItemStack);
        this.target = target;
        targetX = target.getX();
        targetY = target.getBoundingBox().minY + target.getBbHeight() / 2.0f;
        targetZ = target.getZ();
        type = t;
        noPhysics = true;
    }
    
    public EntityFollowingItem(Level par1World, double par2, double par4, double par6, ItemStack par8ItemStack, double tx, double ty, double tz) {
        this(par1World, par2, par4, par6, par8ItemStack);
        targetX = tx;
        targetY = ty;
        targetZ = tz;
    }
    
    public EntityFollowingItem(net.minecraft.world.entity.EntityType<? extends EntityFollowingItem> type, Level par1World) {
        super(type, par1World);
        // Entity requires EntityType; use factory method
        targetX = 0.0;
        targetY = 0.0;
        targetZ = 0.0;
        type = 3;
        target = null;
        age = 20;
        gravity = 0.03999999910593033;
        // FIXME: setSize removed; dimensions set in EntityType builder
    }
    
    @Override
    public void tick() {
        if (target != null) {
            targetX = target.getX();
            targetY = target.getBoundingBox().minY + target.getBbHeight() / 2.0f;
            targetZ = target.getZ();
        }
        if (targetX != 0.0 || targetY != 0.0 || targetZ != 0.0) {
            float xd = (float)(targetX - getX());
            float yd = (float)(targetY - getY());
            float zd = (float)(targetZ - getZ());
            if (age > 1) {
                --age;
            }
            double distance = Mth.sqrt((float)(xd * xd + yd * yd + zd * zd));
            if (distance > 0.5) {
                distance *= age;
                setDeltaMovement(xd / distance, getDeltaMovement().y, getDeltaMovement().z);
                setDeltaMovement(getDeltaMovement().x, yd / distance, getDeltaMovement().z);
                setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, zd / distance);
            }
            else {
                setDeltaMovement(getDeltaMovement().x * 0.10000000149011612, getDeltaMovement().y, getDeltaMovement().z);
                setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y * 0.10000000149011612, getDeltaMovement().z);
                setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z * 0.10000000149011612);
                targetX = 0.0;
                targetY = 0.0;
                targetZ = 0.0;
                target = null;
                noPhysics = false;
            }
            if (level().isClientSide()) {
                float h = (float)((getBoundingBox().maxY - getBoundingBox().minY) / 2.0) + Mth.sin(getAge() / 10.0f + hoverStart) * 0.1f + 0.1f;
                if (type != 10) {
                    FXDispatcher.INSTANCE.drawNitorCore((float) xo + (random.nextFloat() - random.nextFloat()) * 0.125f, (float) yo + h + (random.nextFloat() - random.nextFloat()) * 0.125f, (float) zo + (random.nextFloat() - random.nextFloat()) * 0.125f, random.nextGaussian() * 0.009999999776482582, random.nextGaussian() * 0.009999999776482582, random.nextGaussian() * 0.009999999776482582);
                }
                else {
                    FXDispatcher.INSTANCE.crucibleBubble((float) xo + (random.nextFloat() - random.nextFloat()) * 0.125f, (float) yo + h + (random.nextFloat() - random.nextFloat()) * 0.125f, (float) zo + (random.nextFloat() - random.nextFloat()) * 0.125f, 0.33f, 0.33f, 1.0f);
                }
            }
        }
        else {
            setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y - gravity, getDeltaMovement().z);
        }
        super.tick();
    }
    
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput par1CompoundTag) {
        super.addAdditionalSaveData(par1CompoundTag);
        par1CompoundTag.putShort("type", (short) type);
    }
    
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput par1CompoundTag) {
        super.readAdditionalSaveData(par1CompoundTag);
        type = par1CompoundTag.getShortOr("type", (short)0);
    }
}
