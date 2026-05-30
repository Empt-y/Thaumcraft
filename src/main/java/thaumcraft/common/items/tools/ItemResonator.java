package thaumcraft.common.items.tools;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.HitResult;
import net.minecraft.network.chat.Component;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.tiles.devices.TileCondenser;
import thaumcraft.common.tiles.essentia.TileTubeBuffer;


public class ItemResonator extends ItemTCBase
{
    public ItemResonator() {
        super("resonator");
    }
    
    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.UNCOMMON;
    }
    
    public boolean hasEffect(ItemStack stack1) {
        return !stack1.isEmpty();
    }
    
    public InteractionResult onItemUseFirst(Player player, Level world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, InteractionHand hand) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile == null || !(tile instanceof IEssentiaTransport)) {
            return InteractionResult.FAIL;
        }
        if (world.isClientSide()) {
            player.swing(hand);
            return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
        }
        IEssentiaTransport et = (IEssentiaTransport)tile;
        HitResult hit = RayTracer.retraceBlock(world, player, pos);
        if (hit != null && hit.getType() != net.minecraft.world.phys.HitResult.Type.MISS && true) {
            side = Direction.values()[0];
        }
        if (!(tile instanceof TileTubeBuffer) && et.getEssentiaType(side) != null) {
            player.sendSystemMessage(Component.translatable("tc.resonator1", "" + et.getEssentiaAmount(side), et.getEssentiaType(side).getName()));
        }
        else if (tile instanceof TileTubeBuffer && ((IAspectContainer)tile).getAspects().size() > 0) {
            for (Aspect aspect : ((IAspectContainer)tile).getAspects().getAspectsSortedByName()) {
                player.sendSystemMessage(Component.translatable("tc.resonator1", "" + ((IAspectContainer)tile).getAspects().getAmount(aspect), aspect.getName()));
            }
        }
        String s = I18n.get("tc.resonator3");
        if (et.getSuctionType(side) != null) {
            s = et.getSuctionType(side).getName();
        }
        player.sendSystemMessage(Component.translatable("tc.resonator2", "" + et.getSuctionAmount(side), s));
        world.playLocalSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.ITEM_SHIELD_BLOCK, SoundSource.BLOCKS, 0.5f, 1.9f + net.minecraft.util.RandomSource.create().nextFloat() * 0.1f);
        if (tile != null && tile instanceof TileCondenser) {
            TileCondenser tc = (TileCondenser)tile;
            player.sendSystemMessage(Component.translatable("tc.condenser1", "" + tc.cost));
            int s2 = tc.interval / 20;
            player.sendSystemMessage(Component.translatable("tc.condenser2", "" + tc.interval, "" + s2));
        }
        return InteractionResult.SUCCESS;
    }
}
