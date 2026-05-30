package thaumcraft.common.tiles.misc;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
// ITickable removed - use BlockEntityTicker<T>
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import thaumcraft.api.blocks.BlocksTC;


public class TileBarrierStone extends BlockEntity 
{
    int count;
    
    public TileBarrierStone(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, this.worldPosition, state);
        count = 0;
    }
    
    public boolean gettingPower() {
        return this.level.isBlockIndirectlyGettingPowered(this.worldPosition) > 0;
    }
    
    public void update() {
        if (!getLevel().isClientSide()) {
            if (count == 0) {
                count = net.minecraft.util.RandomSource.create().nextInt(100);
            }
            if (count % 5 == 0 && !gettingPower()) {
                List<LivingEntity> targets = this.level.getEntitiesOfClass(LivingEntity.class, new AABB(this.worldPosition.getX(), this.worldPosition.getY(), this.worldPosition.getZ(), this.worldPosition.getX() + 1, this.worldPosition.getY() + 3, this.worldPosition.getZ() + 1).inflate(0.1, 0.1, 0.1));
                if (targets.size() > 0) {
                    for (LivingEntity e : targets) {
                        if (!e.onGround() && !(e instanceof Player)) {
                            e.push(-Mth.sin((e.getYRot() + 180.0f) * 3.1415927f / 180.0f) * 0.2f, -0.1, Mth.cos((e.getYRot() + 180.0f) * 3.1415927f / 180.0f) * 0.2f);
                        }
                    }
                }
            }
            if (++count % 100 == 0) {
                if (getLevel().getBlockState(this.worldPosition.above(1)) != BlocksTC.barrier.defaultBlockState() && getLevel().isEmptyBlock(this.worldPosition.above(1))) {
                    this.level.setBlock(this.worldPosition.above(1), BlocksTC.barrier.defaultBlockState(), 3);
                }
                if (getLevel().getBlockState(this.worldPosition.above(2)) != BlocksTC.barrier.defaultBlockState() && getLevel().isEmptyBlock(this.worldPosition.above(2))) {
                    this.level.setBlock(this.worldPosition.above(2), BlocksTC.barrier.defaultBlockState(), 3);
                }
            }
        }
    }
}
