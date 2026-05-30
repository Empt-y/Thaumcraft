package thaumcraft.common.entities.construct;
import java.util.UUID;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import net.minecraft.sounds.SoundEvent;
import thaumcraft.common.lib.SoundsTC;
import org.jspecify.annotations.Nullable;


public class EntityOwnedConstruct extends PathfinderMob
{
    protected static final EntityDataAccessor<Byte> TAMED =
        SynchedEntityData.defineId(EntityOwnedConstruct.class, EntityDataSerializers.BYTE);

    boolean validSpawn;
    private @Nullable UUID ownerUUID;

    public EntityOwnedConstruct(EntityType<? extends EntityOwnedConstruct> type, Level worldIn) {
        super(type, worldIn);
        validSpawn = false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(TAMED, (byte) 0);
    }

    public boolean isOwned() {
        return (this.entityData.get(TAMED) & 0x4) != 0;
    }

    public void setOwned(boolean tamed) {
        byte b0 = this.entityData.get(TAMED);
        if (tamed) {
            this.entityData.set(TAMED, (byte)(b0 | 0x4));
        } else {
            this.entityData.set(TAMED, (byte)(b0 & 0xFB));
        }
    }

    public @Nullable UUID getOwnerId() {
        return ownerUUID;
    }

    public void setOwnerId(@Nullable UUID uuid) {
        ownerUUID = uuid;
    }

    @Override
    protected int decreaseAirSupply(int air) {
        return air;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundsTC.clack;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
        return SoundsTC.clack;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundsTC.tool;
    }

    @Override
    public int getAmbientSoundInterval() {
        return 240;
    }

    protected boolean shouldDespawnInPeaceful() {
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        if (getTarget() != null && isAlliedToOwner(getTarget())) {
            setTarget(null);
        }
        if (!level().isClientSide() && !validSpawn) {
            discard();
        }
    }

    public void setValidSpawn() {
        validSpawn = true;
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.putBoolean("v", validSpawn);
        output.putString("OwnerUUID", ownerUUID != null ? ownerUUID.toString() : "");
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        validSpawn = input.getBooleanOr("v", false);
        String s = input.getStringOr("OwnerUUID", "");
        if (!s.isEmpty()) {
            try {
                setOwnerId(UUID.fromString(s));
                setOwned(true);
            } catch (Throwable var4) {
                setOwned(false);
            }
        }
    }

    public @Nullable LivingEntity getOwnerEntity() {
        try {
            UUID uuid = getOwnerId();
            return (uuid == null) ? null : level().getPlayerByUUID(uuid);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    public boolean isOwner(LivingEntity entityIn) {
        return entityIn == getOwnerEntity();
    }

    @Override
    public @Nullable PlayerTeam getTeam() {
        if (isOwned()) {
            LivingEntity owner = getOwnerEntity();
            if (owner != null) {
                return owner.getTeam();
            }
        }
        return super.getTeam();
    }

    public boolean isAlliedToOwner(Entity otherEntity) {
        if (isOwned()) {
            LivingEntity owner = getOwnerEntity();
            if (otherEntity == owner) {
                return true;
            }
            if (owner != null) {
                return owner.isAlliedTo(otherEntity);
            }
        }
        return super.isAlliedTo(otherEntity);
    }

    @Override
    public void die(DamageSource cause) {
        if (!level().isClientSide() && hasCustomName() && getOwnerEntity() instanceof net.minecraft.server.level.ServerPlayer sp) {
            sp.sendSystemMessage(getCombatTracker().getDeathMessage());
        }
        super.die(cause);
    }

    public Entity getOwner() {
        return getOwnerEntity();
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (isRemoved()) {
            return InteractionResult.PASS;
        }
        if (player.isCrouching() || (player.getMainHandItem().getItem() instanceof net.minecraft.world.item.NameTagItem)) {
            return InteractionResult.PASS;
        }
        if (!level().isClientSide() && !isOwner(player)) {
            player.sendSystemMessage(Component.translatable("tc.notowned"));
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }
}
