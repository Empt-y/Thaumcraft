package thaumcraft.common.tiles.devices;
import net.minecraft.world.entity.ExperienceOrb;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.tiles.essentia.TileJar;


public class TileJarBrain extends TileJar
{
    public float field_40063_b;
    public float field_40061_d;
    public float field_40059_f;
    public float field_40066_q;
    public float rota;
    public float rotb;
    public int xp;
    public int xpMax;
    public int eatDelay;
    long lastsigh;

    public TileJarBrain(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        xp = 0;
        xpMax = 2000;
        eatDelay = 0;
        lastsigh = System.currentTimeMillis() + 1500L;
    }

    @Override
    public void readSyncNBT(CompoundTag nbttagcompound) {
        xp = nbttagcompound.getIntOr("XP", 0);
    }

    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbttagcompound) {
        nbttagcompound.putInt("XP", xp);
        return nbttagcompound;
    }

    @Override
    public void update() {
        Entity entity = null;
        if (xp > xpMax) {
            xp = xpMax;
        }
        if (xp < xpMax) {
            entity = getClosestXPOrb();
            if (entity != null && eatDelay == 0) {
                double var3 = (this.worldPosition.getX() + 0.5 - entity.getX()) / 25.0;
                double var4 = (this.worldPosition.getY() + 0.5 - entity.getY()) / 25.0;
                double var5 = (this.worldPosition.getZ() + 0.5 - entity.getZ()) / 25.0;
                double var6 = Math.sqrt(var3 * var3 + var4 * var4 + var5 * var5);
                double var7 = 1.0 - var6;
                if (var7 > 0.0) {
                    var7 *= var7;
                    Vec3 motion = entity.getDeltaMovement();
                    entity.setDeltaMovement(
                        motion.x + var3 / var6 * var7 * 0.3,
                        motion.y + var4 / var6 * var7 * 0.5,
                        motion.z + var5 / var6 * var7 * 0.3
                    );
                }
            }
        }
        if (level.isClientSide()) {
            rotb = rota;
            if (entity == null) {
                entity = level.getNearestPlayer(this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5, 6.0, null);
                if (entity != null && lastsigh < System.currentTimeMillis()) {
                    level.playSound(null, this.worldPosition, SoundsTC.brain, SoundSource.AMBIENT, 0.15f, 0.8f + level.getRandom().nextFloat() * 0.4f);
                    lastsigh = System.currentTimeMillis() + 5000L + level.getRandom().nextInt(25000);
                }
            }
            if (entity != null) {
                double d = entity.getX() - (this.worldPosition.getX() + 0.5f);
                double d2 = entity.getZ() - (this.worldPosition.getZ() + 0.5f);
                field_40066_q = (float)Math.atan2(d2, d);
                field_40059_f += 0.1f;
                if (field_40059_f < 0.5f || level.getRandom().nextInt(40) == 0) {
                    float f3 = field_40061_d;
                    do {
                        field_40061_d += level.getRandom().nextInt(4) - level.getRandom().nextInt(4);
                    } while (f3 == field_40061_d);
                }
            }
            else {
                field_40066_q += 0.01f;
            }
            while (rota >= 3.141593f) {
                rota -= 6.283185f;
            }
            while (rota < -3.141593f) {
                rota += 6.283185f;
            }
            while (field_40066_q >= 3.141593f) {
                field_40066_q -= 6.283185f;
            }
            while (field_40066_q < -3.141593f) {
                field_40066_q += 6.283185f;
            }
            float f4;
            for (f4 = field_40066_q - rota; f4 >= 3.141593f; f4 -= 6.283185f) {}
            while (f4 < -3.141593f) {
                f4 += 6.283185f;
            }
            rota += f4 * 0.04f;
        }
        if (eatDelay > 0) {
            --eatDelay;
        }
        else if (xp < xpMax) {
            BlockPos bp = this.worldPosition;
            List<ExperienceOrb> ents = level.getEntitiesOfClass(ExperienceOrb.class, new AABB(bp.getX() - 0.1, bp.getY() - 0.1, bp.getZ() - 0.1, bp.getX() + 1.1, bp.getY() + 1.1, bp.getZ() + 1.1));
            if (ents.size() > 0) {
                for (ExperienceOrb eo : ents) {
                    xp += eo.getValue();
                    eo.playSound(SoundEvents.GENERIC_EAT.value(), 0.1f, (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.2f + 1.0f);
                    eo.discard();
                }
                syncTile(false);
                setChanged();
            }
        }
    }

    public Entity getClosestXPOrb() {
        double cdist = Double.MAX_VALUE;
        Entity orb = null;
        BlockPos bp = this.worldPosition;
        List<ExperienceOrb> ents = level.getEntitiesOfClass(ExperienceOrb.class, new AABB(bp.getX(), bp.getY(), bp.getZ(), bp.getX() + 1, bp.getY() + 1, bp.getZ() + 1).inflate(8.0, 8.0, 8.0));
        if (ents.size() > 0) {
            for (ExperienceOrb eo : ents) {
                double d = eo.distanceToSqr(bp.getX() + 0.5, bp.getY() + 0.5, bp.getZ() + 0.5);
                if (d < cdist) {
                    orb = eo;
                    cdist = d;
                }
            }
        }
        return orb;
    }
}
