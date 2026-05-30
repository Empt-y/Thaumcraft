package thaumcraft.common.items.consumables;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.potions.PotionWarpWard;


public class ItemSanitySoap extends ItemTCBase
{
    public ItemSanitySoap() {
        super("sanity_soap");
    }
    
    public int getMaxItemUseDuration(ItemStack p_77626_1_) {
        return 100;
    }
    
    public ItemUseAnimation getItemUseAction(ItemStack p_77661_1_) {
        return ItemUseAnimation.BLOCK;
    }
    
    public InteractionResult use(Level p_77659_2_, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.SUCCESS;
    }
    
    public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
        int ticks = getMaxItemUseDuration(stack) - count;
        if (ticks > 95) {
            player.stopActiveHand();
        }
        if (player.level().isClientSide()) {
            if (player.level().getRandom().nextFloat() < 0.2f) {
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CHORUS_FLOWER_DEATH, SoundSource.PLAYERS, 0.1f, 1.5f + player.level().getRandom().nextFloat() * 0.2f);
            }
            for (int a = 0; a < 10; ++a) {
                FXDispatcher.INSTANCE.crucibleBubble((float)player.getX() - 0.5f + player.level().getRandom().nextFloat(), (float)player.getBoundingBox().minY + player.level().getRandom().nextFloat() * player.getBbHeight(), (float)player.getZ() - 0.5f + player.level().getRandom().nextFloat(), 1.0f, 0.8f, 0.9f);
            }
        }
    }
    
    public void onPlayerStoppedUsing(ItemStack stack, Level world, LivingEntity player, int timeLeft) {
        int qq = getMaxItemUseDuration(stack) - timeLeft;
        if (qq > 95 && player instanceof Player) {
            stack.shrink(1);
            if (!world.isClientSide()) {
                IPlayerWarp warp = ThaumcraftCapabilities.getWarp((Player)player);
                int amt = 1;
                if (player.hasEffect(net.minecraft.core.Holder.direct(PotionWarpWard.instance))) {
                    ++amt;
                }
                int i = Mth.floor(player.getX());
                int j = Mth.floor(player.getY());
                int k = Mth.floor(player.getZ());
                if (world.getBlockState(new BlockPos(i, j, k)).getBlock() == BlocksTC.purifyingFluid) {
                    ++amt;
                }
                if (warp.get(IPlayerWarp.EnumWarpType.NORMAL) > 0) {
                    ThaumcraftApi.internalMethods.addWarpToPlayer((Player)player, -amt, IPlayerWarp.EnumWarpType.NORMAL);
                }
                if (warp.get(IPlayerWarp.EnumWarpType.TEMPORARY) > 0) {
                    ThaumcraftApi.internalMethods.addWarpToPlayer((Player)player, -warp.get(IPlayerWarp.EnumWarpType.TEMPORARY), IPlayerWarp.EnumWarpType.TEMPORARY);
                }
            }
            else {
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundsTC.craftstart, SoundSource.PLAYERS, 0.25f, 1.0f);
                for (int a = 0; a < 40; ++a) {
                    FXDispatcher.INSTANCE.crucibleBubble((float)player.getX() - 0.5f + player.level().getRandom().nextFloat() * 1.5f, (float)player.getBoundingBox().minY + player.level().getRandom().nextFloat() * player.getBbHeight(), (float)player.getZ() - 0.5f + player.level().getRandom().nextFloat() * 1.5f, 1.0f, 0.7f, 0.9f);
                }
            }
        }
    }
}
