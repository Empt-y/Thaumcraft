package thaumcraft.common.tiles.devices;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.core.Direction;
// ITickable removed - use BlockEntityTicker<T>
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileBellows extends TileThaumcraft 
{
    public float inflation;
    boolean direction;
    boolean firstrun;
    public int delay;
    
    public TileBellows(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        inflation = 1.0f;
        direction = false;
        firstrun = true;
        delay = 0;
    }
    
    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos().getX() - 0.3, getBlockPos().getY() - 0.3, getBlockPos().getZ() - 0.3, getBlockPos().getX() + 1.3, getBlockPos().getY() + 1.3, getBlockPos().getZ() + 1.3);
    }
    
    public void update() {
        if (getLevel().isClientSide()) {
            if (BlockStateUtils.isEnabled(getBlockState())) {
                if (firstrun) {
                    inflation = 0.35f + net.minecraft.util.RandomSource.create().nextFloat() * 0.55f;
                }
                firstrun = false;
                if (inflation > 0.35f && !direction) {
                    inflation -= 0.075f;
                }
                if (inflation <= 0.35f && !direction) {
                    direction = true;
                }
                if (inflation < 1.0f && direction) {
                    inflation += 0.025f;
                }
                if (inflation >= 1.0f && direction) {
                    direction = false;
                    getLevel().playSound(null, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), SoundEvents.GHAST_SHOOT, SoundSource.BLOCKS, 0.01f, 0.5f + (net.minecraft.util.RandomSource.create().nextFloat() - net.minecraft.util.RandomSource.create().nextFloat()) * 0.2f);
                }
            }
        }
        else if (BlockStateUtils.isEnabled(getBlockState())) {
            ++delay;
            if (delay >= 2) {
                delay = 0;
                BlockEntity tile = getLevel().getBlockEntity(getBlockPos().offset(getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING)));
                if (tile != null && tile instanceof AbstractFurnaceBlockEntity) {
                    AbstractFurnaceBlockEntity tf = (AbstractFurnaceBlockEntity)tile;
                    int ct = getCooktime(tf);
                    if (ct > 0 && ct < 199) {
                        setCooktime(tf, ct + 1);
                    }
                }
            }
        }
    }
    
    public void setCooktime(AbstractFurnaceBlockEntity ent, int hit) {
        ent.cookTime = hit;
    }
    
    public int getCooktime(AbstractFurnaceBlockEntity ent) {
        return ent.cookTime;
    }
    
    public static int getBellows(Level world, BlockPos pos, Direction[] directions) {
        int bellows = 0;
        for (Direction dir : directions) {
            BlockEntity tile = world.getBlockEntity(pos.relative(dir));
            try {
                if (tile != null && tile instanceof TileBellows && tile.getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING) == dir.getOpposite() && BlockStateUtils.isEnabled(tile.getBlockState())) {
                    ++bellows;
                }
            }
            catch (Exception ex) {}
        }
        return bellows;
    }
    
    public boolean canRenderBreaking() {
        return true;
    }
}
