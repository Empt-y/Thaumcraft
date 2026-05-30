package thaumcraft.common.entities.monster;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.Level;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.SoundsTC;


public class EntityThaumicSlime extends Slime implements ITaintedMob
{
    private boolean localWasOnGround = false;
    int launched;
    int spitCounter;
    
    @SuppressWarnings("unchecked")
    public EntityThaumicSlime(Level par1World) {
        super(null, par1World);
        // Entity requires EntityType; use factory method
    }

    public EntityThaumicSlime(net.minecraft.world.entity.EntityType<? extends EntityThaumicSlime> type, Level par1World) {
        super(type, par1World);
        launched = 10;
        spitCounter = 100;
        int i = 1 << 1 + getRandom().nextInt(3);
        // setSize removed - dimensions in EntityType
    }
    
    public EntityThaumicSlime(Level par1World, LivingEntity par2Mob, LivingEntity par3Mob) {
        super(null, par1World);
        // Entity requires EntityType; use factory method
        launched = 10;
        spitCounter = 100;
        // setSize removed - dimensions in EntityType
        setPos(getX(), (par2Mob.getBoundingBox().minY + par2Mob.getBoundingBox().maxY) / 2.0, getZ());
        double var6 = par3Mob.getX() - par2Mob.getX();
        double var7 = par3Mob.getBoundingBox().minY + par3Mob.getBbHeight() / 3.0f - getY();
        double var8 = par3Mob.getZ() - par2Mob.getZ();
        double var9 = Mth.sqrt((float)(var6 * var6 + var8 * var8));
        if (var9 >= 1.0E-7) {
            float var10 = (float)(Math.atan2(var8, var6) * 180.0 / 3.141592653589793) - 90.0f;
            float var11 = (float)(-(Math.atan2(var7, var9) * 180.0 / 3.141592653589793));
            double var12 = var6 / var9;
            double var13 = var8 / var9;
            moveTo(par2Mob.getX() + var12, getY(), par2Mob.getZ() + var13, var10, var11);
            float var14 = (float)var9 * 0.2f;
            shoot(var6, var7 + var14, var8, 1.5f, 1.0f);
        }
    }
    
    public void shoot(double par1, double par3, double par5, float par7, float par8) {
        float var9 = Mth.sqrt((float)(par1 * par1 + par3 * par3 + par5 * par5));
        par1 /= var9;
        par3 /= var9;
        par5 /= var9;
        par1 += getRandom().nextGaussian() * 0.007499999832361937 * par8;
        par3 += getRandom().nextGaussian() * 0.007499999832361937 * par8;
        par5 += getRandom().nextGaussian() * 0.007499999832361937 * par8;
        par1 *= par7;
        par3 *= par7;
        par5 *= par7;
        setDeltaMovement(par1, getDeltaMovement().y, getDeltaMovement().z);
        setDeltaMovement(getDeltaMovement().x, par3, getDeltaMovement().z);
        setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, par5);
        float var10 = Mth.sqrt((float)(par1 * par1 + par5 * par5));
        float n = (float)(Math.atan2(par1, par5) * 180.0 / 3.141592653589793);
        setYRot(n);
        yRotO = getYRot();
        float n2 = (float)(Math.atan2(par3, var10) * 180.0 / 3.141592653589793);
        setXRot(n2);
        xRotO = getXRot();
    }
    
    public SpawnGroupData finalizeSpawn(net.minecraft.world.level.ServerLevelAccessor accessor, DifficultyInstance p_180482_1_, net.minecraft.world.entity.EntitySpawnReason reason, SpawnGroupData p_180482_2_) {
        int i = net.minecraft.util.RandomSource.create().nextInt(3);
        if (i < 2 && getRandom().nextFloat() < 0.5f * p_180482_1_.getSpecialMultiplier()) {
            ++i;
        }
        int j = 1 << i;
        // setSize removed - dimensions in EntityType
        return super.finalizeSpawn(accessor, p_180482_1_, reason, p_180482_2_);
    }
    
    public void setSize(int par1, boolean t) {
                xpReward = par1 + 2;
    }
    
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput par1CompoundTag) {
        super.addAdditionalSaveData(par1CompoundTag);
    }
    
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput par1CompoundTag) {
        super.readAdditionalSaveData(par1CompoundTag);
    }
    
    public void tick() {
        int i = getSize();
        if (onGround() && !localWasOnGround) {
            localWasOnGround = true;
            float sa = squish;
            super.tick();
            squish = sa;
            if (level().isClientSide()) {
                for (int j = 0; j < i * 2; ++j) {
                    FXDispatcher.INSTANCE.slimeJumpFX(this, i);
                }
            }
            if (doPlayJumpSound()) {
                playSound(getJumpSound(), getSoundVolume(), ((getRandom().nextFloat() - getRandom().nextFloat()) * 0.2f + 1.0f) * 0.8f);
            }
            squish = -0.5f;
            localWasOnGround = onGround();
            decreaseSquish();
        }
        else {
            super.tick();
        }
        if (level().isClientSide()) {
            if (launched > 0) {
                --launched;
                for (int k = 0; k < i * (launched + 1); ++k) {
                    FXDispatcher.INSTANCE.slimeJumpFX(this, i);
                }
            }
            float ff = (float) getSize();
            // FIXME: setSize removed; dimensions set in EntityType builder
            // FIXME: setSize removed; dimensions set in EntityType builder
        }
        else if (!isDeadOrDying()) {
            Player entityplayer = level().getNearestPlayer(this, 16.0);
            if (entityplayer != null) {
                if (spitCounter > 0) {
                    --spitCounter;
                }
                /* faceEntity removed */;
                if (distanceTo(entityplayer) > 4.0f && spitCounter <= 0 && getSize() > 2) {
                    spitCounter = 101;
                    if (!level().isClientSide()) {
                        EntityThaumicSlime flyslime = new EntityThaumicSlime(null, this, entityplayer);
                        level().addFreshEntity(flyslime);
                    }
                    playSound(SoundsTC.gore, 1.0f, ((getRandom().nextFloat() - getRandom().nextFloat()) * 0.2f + 1.0f) * 0.8f);
                    setSize(getSize() - 1, true);
                }
            }
        }
    }
    
    protected EntityThaumicSlime createInstance() {
        return new EntityThaumicSlime(null);
    }
    
    @Override
    public void discard() {
        int i = getSize();
        if (!level().isClientSide() && i > 1 && getHealth() <= 0.0f) {
            for (int k = 0; k < i; ++k) {
                float f = (k % 2 - 0.5f) * i / 4.0f;
                float f2 = (k / 2 - 0.5f) * i / 4.0f;
                EntityThaumicSlime entityslime = createInstance();
                                entityslime.moveTo(getX() + f, getY() + 0.5, getZ() + f2, getRandom().nextFloat() * 360.0f, 0.0f);
                level().addFreshEntity(entityslime);
            }
        }
        discard();
    }
    
    @Override
    public boolean checkSpawnRules(net.minecraft.world.level.LevelAccessor level, net.minecraft.world.entity.EntitySpawnReason spawnReason) {
        return false;
    }
    
    protected int getAttackStrength() {
        return getSize() + 1;
    }
    
    protected boolean canDamagePlayer() {
        return true;
    }
    
    protected void dealDamage(LivingEntity p_175451_1_) {
        int i = getSize();
        if (launched > 0) {
            i += 2;
        }
        p_175451_1_.hurt(this.level().damageSources().mobAttack(this), (float) getAttackStrength());
        if (isAlive() && getSensing().hasLineOfSight(p_175451_1_) && distanceToSqr(p_175451_1_.getX() + 0.5, p_175451_1_.getY() + 0.5, p_175451_1_.getZ() + 0.5) < 0.6 * i * 0.6 * i) {
            playSound(SoundEvents.SLIME_ATTACK, 1.0f, (getRandom().nextFloat() - getRandom().nextFloat()) * 0.2f + 1.0f);
            /* applyEnchantments removed */ ;
        }
    }
    
    protected Item getDropItem() {
        return (getSize() > 1) ? ItemsTC.crystalEssence : net.minecraft.world.item.Items.AIR;
    }
    
    protected void dropFewItems(boolean flag, int i) {
        if (getSize() > 1) {
            if (level() instanceof net.minecraft.server.level.ServerLevel _sl) spawnAtLocation(_sl, ConfigItems.FLUX_CRYSTAL.copy());
        }
    }
}
