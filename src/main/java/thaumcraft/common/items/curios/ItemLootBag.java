package thaumcraft.common.items.curios;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.Utils;
import net.minecraft.world.item.TooltipFlag;


public class ItemLootBag extends ItemTCBase
{
    public ItemLootBag() {
        super("loot_bag", "common", "uncommon", "rare");
    }
    
    public Rarity getRarity(ItemStack stack) {
        switch (stack.getDamageValue()) {
            case 1: {
                return Rarity.UNCOMMON;
            }
            case 2: {
                return Rarity.RARE;
            }
            default: {
                return Rarity.COMMON;
            }
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, java.util.List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        tooltip.add(net.minecraft.network.chat.Component.literal(I18n.get("tc.lootbag")));
    }
    
    public InteractionResult use(Level world, Player player, InteractionHand hand) {
        if (!world.isClientSide()) {
            for (int q = 8 + net.minecraft.util.RandomSource.create().nextInt(5), a = 0; a < q; ++a) {
                ItemStack is = Utils.generateLoot(player.getItemInHand(hand).getDamageValue(), world.getRandom());
                if (is != null && !is.isEmpty()) {
                    world.addFreshEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(), is.copy()));
                }
            }
            player.playSound(SoundsTC.coins, 0.75f, 1.0f);
        }
        player.getItemInHand(hand).shrink(1);
        return InteractionResult.SUCCESS;
    }
}
