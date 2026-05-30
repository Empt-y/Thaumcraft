package thaumcraft.common.entities;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.CrashReportCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.blocks.world.taint.BlockTaint;
import thaumcraft.common.lib.SoundsTC;


public class EntityFallingTaint extends Entity
{
    @Override
    public boolean hurtServer(net.minecraft.server.level.ServerLevel level, net.minecraft.world.damagesource.DamageSource source, float damage) {
        return false; // Entity is abstract, no super to call
    }

    public BlockState fallTile;
    BlockPos oldPos;
    public int fallTime;
    private int fallHurtMax;
    private float fallHurtAmount;
    
    public BlockState getBlock() {
        return fallTile;
    }
    
    public EntityFallingTaint(net.minecraft.world.entity.EntityType<? extends EntityFallingTaint> type, Level par1World) {
        super(type, par1World);
        fallTime = 0;
        fallHurtMax = 40;
        fallHurtAmount = 2.0f;
    }
    
    public EntityFallingTaint(Level par1World, double par2, double par4, double par6, BlockState par8, BlockPos o) {
        super(null, par1World);
        // Entity requires EntityType; use factory method
        fallTime = 0;
        fallHurtMax = 40;
        fallHurtAmount = 2.0f;
        fallTile = par8;
        noPhysics = true;
        // FIXME: setSize removed; dimensions set in EntityType builder
        setPos(par2, par4, par6);
        setDeltaMovement(0.0, getDeltaMovement().y, getDeltaMovement().z);
        setDeltaMovement(getDeltaMovement().x, 0.0, getDeltaMovement().z);
        setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, 0.0);
        xo = par2;
        yo = par4;
        zo = par6;
        oldPos = o;
    }
    
    public boolean isIgnoringBlockTriggers() { return true; }
    
    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
    }
    
    public boolean isPickable() {
        return !isDeadOrDying();
    }
    
    public void tick() {
        if (fallTile == null || fallTile.isAir()) {
            discard();
        }
        else {
            xo = getX();
            yo = getY();
            zo = getZ();
            ++fallTime;
            setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y - 0.03999999910593033, getDeltaMovement().z);
            move(MoverType.SELF, getDeltaMovement());
            setDeltaMovement(getDeltaMovement().x * 0.9800000190734863, getDeltaMovement().y, getDeltaMovement().z);
            setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y * 0.9800000190734863, getDeltaMovement().z);
            setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z * 0.9800000190734863);
            BlockPos bp = this.blockPosition();
            if (!level().isClientSide()) {
                if (fallTime == 1) {
                    if (level().getBlockState(oldPos) != fallTile) {
                        discard();
                        return;
                    }
                    level().removeBlock(oldPos, false);
                }
                if (onGround() || level().getBlockState(bp.below()).getBlock() == BlocksTC.fluxGoo) {
                    setDeltaMovement(getDeltaMovement().x * 0.699999988079071, getDeltaMovement().y, getDeltaMovement().z);
                    setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z * 0.699999988079071);
                    setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y * -0.5, getDeltaMovement().z);
                    if (level().getBlockState(bp).getBlock() != Blocks.PISTON && level().getBlockState(bp).getBlock() != Blocks.PISTON && level().getBlockState(bp).getBlock() != Blocks.PISTON_HEAD) {
                        playSound(SoundsTC.gore, 0.5f, ((random.nextFloat() - random.nextFloat()) * 0.2f + 1.0f) * 0.8f);
                        discard();
                        if (canPlace(bp) && !BlockTaint.canFallBelow(level(), bp.below()) && level().setBlockAndUpdate(bp, fallTile)) {}
                    }
                }
                else if ((fallTime > 100 && !level().isClientSide() && (bp.getY() < 1 || bp.getY() > 256)) || fallTime > 600) {
                    discard();
                }
            }
            else if (onGround() || fallTime == 1) {
                for (int j = 0; j < 10; ++j) {
                    FXDispatcher.INSTANCE.taintLandFX(this);
                }
            }
        }
    }
    
    private boolean canPlace(BlockPos pos) {
        return level().getBlockState(pos).getBlock() == BlocksTC.taintFibre || level().getBlockState(pos).getBlock() == BlocksTC.fluxGoo || true /* mayPlace stubbed */;
    }
    
    public void fall(float distance, float damageMultiplier) {
    }
    
    public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput par1CompoundTag) {
        Block block = (fallTile != null) ? fallTile.getBlock() : Blocks.AIR;
        Identifier resourcelocation = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(block);
        par1CompoundTag.putString("Block", (resourcelocation == null) ? "" : resourcelocation.toString());
        par1CompoundTag.putByte("Data", (byte)0 /* getMetaFromState removed */);
        par1CompoundTag.putByte("Time", (byte) fallTime);
        par1CompoundTag.putFloat("FallHurtAmount", fallHurtAmount);
        par1CompoundTag.putInt("FallHurtMax", fallHurtMax);
        par1CompoundTag.putLong("Old", oldPos.asLong());
    }
    
    public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput par1CompoundTag) {
        int i = par1CompoundTag.getByteOr("Data", (byte)0) & 0xFF;
        if (par1CompoundTag.getString("Block").isPresent()) {
            fallTile = net.minecraft.core.registries.BuiltInRegistries.BLOCK.getValue(net.minecraft.resources.Identifier.parse(par1CompoundTag.getStringOr("Block", "minecraft:air"))).defaultBlockState();
        }
        else if (par1CompoundTag.getInt("TileID").isPresent()) {
            fallTile = net.minecraft.core.registries.BuiltInRegistries.BLOCK.byId(par1CompoundTag.getIntOr("TileID", 0)).defaultBlockState();
        }
        else {
            fallTile = net.minecraft.core.registries.BuiltInRegistries.BLOCK.byId(par1CompoundTag.getByteOr("Tile", (byte)0) & 0xFF).defaultBlockState();
        }
        fallTime = (par1CompoundTag.getByteOr("Time", (byte)0) & 0xFF);
        oldPos = BlockPos.of(par1CompoundTag.getLongOr("Old", 0L));
        if (par1CompoundTag.contains("HurtEntities")) {
            fallHurtAmount = par1CompoundTag.getFloatOr("FallHurtAmount", 0.0f);
            fallHurtMax = par1CompoundTag.getIntOr("FallHurtMax", 0);
        }
        if (fallTile == null) {
            fallTile = Blocks.SAND.defaultBlockState();
        }
    }
    
    public void fillCrashReportCategory(net.minecraft.CrashReportCategory par1CrashReportCategory) {
        super.fillCrashReportCategory(par1CrashReportCategory);
        par1CrashReportCategory.setDetail("Immitating block ID", () -> net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(fallTile.getBlock()).toString());
        par1CrashReportCategory.addDetail("Immitating block data", fallTile.getBlock());
    }
    
    public SoundSource getSoundSource() {
        return SoundSource.BLOCKS;
    }
    
    @OnlyIn(Dist.CLIENT)
    public Level getLevel() {
        return world;
    }
    
    @OnlyIn(Dist.CLIENT)
    public boolean canRenderOnFire() {
        return false;
    }
}
