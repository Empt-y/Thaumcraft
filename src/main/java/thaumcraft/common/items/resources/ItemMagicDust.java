package thaumcraft.common.items.resources;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.EntityUtils;


public class ItemMagicDust extends ItemTCBase
{
    public ItemMagicDust() {
        super("salis_mundus");
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.UNCOMMON;
    }
    
    public InteractionResult onItemUseFirst(Player player, Level world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, InteractionHand hand) {
        if (!player.canPlayerEdit(pos, side, player.getItemInHand(hand))) {
            return InteractionResult.FAIL;
        }
        if (player.isCrouching()) {
            return InteractionResult.PASS;
        }
        player.swing(hand);
        for (IDustTrigger trigger : IDustTrigger.triggers) {
            IDustTrigger.Placement place = trigger.getValidFace(world, player, pos, side);
            if (place != null) {
                if (!player.getAbilities().instabuild) {
                    player.getItemInHand(hand).shrink(1);
                }
                trigger.execute(world, player, pos, place, side);
                if (world.isClientSide()) {
                    doSparkles(player, world, pos, hitX, hitY, hitZ, hand, trigger, place);
                    break;
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }
    
    private void doSparkles(Player player, Level world, BlockPos pos, float hitX, float hitY, float hitZ, InteractionHand hand, IDustTrigger trigger, IDustTrigger.Placement place) {
        Vec3 v1 = EntityUtils.posToHand(player, hand);
        Vec3 v2 = new Vec3(pos);
        v2 = v2.add(0.5, 0.5, 0.5);
        v2 = v2.subtract(v1);
        for (int cnt = 50, a = 0; a < cnt; ++a) {
            boolean floaty = a < cnt / 3;
            float r = Mth.randomBetweenInclusive(world.getRandom(), 255, 255) / 255.0f;
            float g = Mth.randomBetweenInclusive(world.getRandom(), 189, 255) / 255.0f;
            float b = Mth.randomBetweenInclusive(world.getRandom(), 64, 255) / 255.0f;
            FXDispatcher.INSTANCE.drawSimpleSparkle(world.getRandom(), v1.x, v1.y, v1.z, v2.x / 6.0 + world.getRandom().nextGaussian() * 0.05, v2.y / 6.0 + world.getRandom().nextGaussian() * 0.05 + (floaty ? 0.05 : 0.15), v2.z / 6.0 + world.getRandom().nextGaussian() * 0.05, 0.5f, r, g, b, net.minecraft.util.RandomSource.create().nextInt(5), floaty ? (0.3f + net.minecraft.util.RandomSource.create().nextFloat() * 0.5f) : 0.85f, floaty ? 0.2f : 0.5f, 16);
        }
        world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundsTC.dust, SoundSource.PLAYERS, 0.33f, 1.0f + (float)world.getRandom().nextGaussian() * 0.05f, false);
        List<BlockPos> sparkles = trigger.sparkle(world, player, pos, place);
        if (sparkles != null) {
            Vec3 v3 = new Vec3(pos).add(hitX, hitY, hitZ);
            for (BlockPos p : sparkles) {
                FXDispatcher.INSTANCE.drawBlockSparkles(p, v3);
            }
        }
    }
}
