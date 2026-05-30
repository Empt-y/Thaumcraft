package thaumcraft.common.tiles.misc;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.blocks.basic.BlockBannerTC;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileBanner extends TileThaumcraft
{
    private byte facing;
    private Aspect aspect;
    private boolean onWall;
    
    public TileBanner(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        facing = 0;
        aspect = null;
        onWall = false;
    }
    
    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos().getX(), getBlockPos().getY() - 1, getBlockPos().getZ(), getBlockPos().getX() + 1, getBlockPos().getY() + 2, getBlockPos().getZ() + 1);
    }
    
    public byte getBannerFacing() {
        return facing;
    }
    
    public void setBannerFacing(byte face) {
        facing = face;
        setChanged();
    }
    
    public boolean getWall() {
        return onWall;
    }
    
    public void setWall(boolean b) {
        onWall = b;
        setChanged();
    }
    
    @Override
    public void readSyncNBT(CompoundTag nbttagcompound) {
        facing = nbttagcompound.getByteOr("facing", (byte)0);
        String as = nbttagcompound.getStringOr("aspect", "");
        if (as != null && as.length() > 0) {
            setAspect(Aspect.getAspect(as));
        }
        else {
            aspect = null;
        }
        onWall = nbttagcompound.getBooleanOr("wall", false);
    }
    
    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbttagcompound) {
        nbttagcompound.putByte("facing", facing);
        nbttagcompound.putString("aspect", (getAspect() == null) ? "" : getAspect().getTag());
        nbttagcompound.putBoolean("wall", onWall);
        return nbttagcompound;
    }
    
    public Aspect getAspect() {
        return aspect;
    }
    
    public void setAspect(Aspect aspect) {
        this.aspect = aspect;
    }
    
    @OnlyIn(Dist.CLIENT)
    public int getColor() {
        return (getBlockState().getBlock() == null || !(getBlockState().getBlock() instanceof BlockBannerTC) || ((BlockBannerTC) getBlockState().getBlock()).dye == null) ? -1 : ((BlockBannerTC) getBlockState().getBlock()).dye.getColorValue();
    }
}
