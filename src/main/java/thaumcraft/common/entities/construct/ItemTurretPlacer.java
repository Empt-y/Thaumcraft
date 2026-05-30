package thaumcraft.common.entities.construct;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import thaumcraft.common.items.ItemTCBase;


public class ItemTurretPlacer extends ItemTCBase
{
    public ItemTurretPlacer() {
        super("turret", "basic", "advanced", "bore");
    }
    
    public InteractionResult onItemUseFirst(Player player, Level world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, InteractionHand hand) {
        if (side == Direction.DOWN) {
            return InteractionResult.PASS;
        }
        boolean flag = world.getBlockState(pos).isAir();
        BlockPos blockpos = flag ? pos : pos.relative(side);
        if (!player.mayUseItemAt(blockpos, side, player.getItemInHand(hand))) {
            return InteractionResult.PASS;
        }
        BlockPos blockpos2 = blockpos.above();
        boolean flag2 = !world.isEmptyBlock(blockpos);
        flag2 |= !world.isEmptyBlock(blockpos2);
        if (flag2) {
            return InteractionResult.PASS;
        }
        double d0 = blockpos.getX();
        double d2 = blockpos.getY();
        double d3 = blockpos.getZ();
        List<Entity> list = world.getEntities(null, new AABB(d0, d2, d3, d0 + 1.0, d2 + 2.0, d3 + 1.0));
        if (!list.isEmpty()) {
            return InteractionResult.PASS;
        }
        if (!world.isClientSide()) {
            world.removeBlock(blockpos, false);
            world.removeBlock(blockpos2, false);
            EntityOwnedConstruct turret = null;
            switch (player.getItemInHand(hand).getDamageValue()) {
                case 0: turret = new EntityTurretCrossbow(world, blockpos); break;
                case 1: turret = new EntityTurretCrossbowAdvanced(world, blockpos); break;
                case 2: turret = new EntityArcaneBore(world, blockpos, player.getDirection()); break;
            }
            if (turret != null) {
                world.addFreshEntity(turret);
                turret.setOwned(true);
                turret.setValidSpawn();
                turret.setOwnerId(player.getUUID());
                world.playLocalSound(turret.getX(), turret.getY(), turret.getZ(), SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75f, 0.8f, false);
            }
            player.getItemInHand(hand).shrink(1);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
}
