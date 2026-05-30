package thaumcraft.common.golems;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.common.lib.utils.InventoryUtils;


public class GolemInteractionHelper
{
    public static void golemClick(Level world, IGolemAPI golem, BlockPos pos, Direction face, ItemStack clickStack, boolean sneaking, boolean rightClick) {
        FakePlayer fp = FakePlayerFactory.get((ServerLevel)world, new GameProfile(null, "FakeThaumcraftGolem"));
        fp.setPos(golem.getGolemEntity().getX(), golem.getGolemEntity().getY(), golem.getGolemEntity().getZ());
        fp.setItemInHand(InteractionHand.MAIN_HAND, clickStack);
        fp.setShiftKeyDown(sneaking);
        if (!rightClick) {
            try {
                if (world instanceof ServerLevel sl) {
                    fp.gameMode.destroyBlock(pos);
                }
            }
            catch (Exception ex) {}
        }
        else {
            if (fp.getMainHandItem().getItem() instanceof BlockItem && !mayPlace(world, ((BlockItem)fp.getMainHandItem().getItem()).getBlock(), pos, face)) {
                golem.getGolemEntity().setPos(golem.getGolemEntity().getX() + face.getStepX(), golem.getGolemEntity().getY() + face.getStepY(), golem.getGolemEntity().getZ() + face.getStepZ());
            }
            try {
                if (world instanceof ServerLevel) {
                    Vec3 hitVec = Vec3.atCenterOf(pos);
                    BlockHitResult hit = new BlockHitResult(hitVec, face, pos, false);
                    fp.gameMode.useItemOn(fp, world, fp.getMainHandItem(), InteractionHand.MAIN_HAND, hit);
                }
            }
            catch (Exception ex2) {}
        }
        golem.addRankXp(1);
        if (!fp.getMainHandItem().isEmpty() && fp.getMainHandItem().getCount() <= 0) {
            fp.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
        dropSomeItems(fp, golem);
        golem.swing();
    }
    
    private static boolean mayPlace(Level world, Block blockIn, BlockPos pos, Direction side) {
        BlockState block = world.getBlockState(pos);
        AABB axisalignedbb = blockIn.getBoundingBox(blockIn.defaultBlockState(), world, pos);
        return axisalignedbb == null || world.noCollision(axisalignedbb);
    }
    
    private static void dropSomeItems(FakePlayer fp2, IGolemAPI golem) {
        for (int i = 0; i < fp2.getInventory().getContainerSize(); ++i) {
            if (!fp2.getInventory().getItem(i).isEmpty()) {
                if (golem.canCarry(fp2.getInventory().getItem(i), true)) {
                    fp2.getInventory().setItem(i, golem.holdItem(fp2.getInventory().getItem(i)));
                }
                if (!fp2.getInventory().getItem(i).isEmpty() && fp2.getInventory().getItem(i).getCount() > 0) {
                    InventoryUtils.dropItemAtEntity(golem.getGolemWorld(), fp2.getInventory().getItem(i), golem.getGolemEntity());
                }
                fp2.getInventory().setItem(i, ItemStack.EMPTY);
            }
        }
        for (int i = 0; i < 4 /* 4 */; ++i) {
            if (!fp2.getInventory().getItem(36 + i).isEmpty()) {
                if (golem.canCarry(fp2.getInventory().getItem(36 + i), true)) {
                    fp2.getInventory().setItem(36 + i, golem.holdItem(fp2.getInventory().getItem(36 + i)));
                }
                if (!fp2.getInventory().getItem(i).isEmpty() && fp2.getInventory().getItem(36 + i).getCount() > 0) {
                    InventoryUtils.dropItemAtEntity(golem.getGolemWorld(), fp2.getInventory().getItem(36 + i), golem.getGolemEntity());
                }
                fp2.getInventory().setItem(36 + i, ItemStack.EMPTY);
            }
        }
    }
}
