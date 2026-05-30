package thaumcraft.common.golems.seals;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import thaumcraft.api.golems.ISealDisplayer;
import thaumcraft.api.golems.seals.ISeal;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemTCBase;


public class ItemSealPlacer extends ItemTCBase implements ISealDisplayer
{
    public ItemSealPlacer() {
        super("seal", "blank");
        /* setMaxDamage removed - use Item.Properties */;
    }
    
    
    public void getSubItems(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == null /* SEARCH tab removed */) {
            String[] vn = getVariantNames();
            for (int a = 0; a < vn.length; ++a) {
                items.add(new ItemStack(this.asItem(), 1));
            }
        }
    }
    
    @Override
    public String[] getVariantNames() {
        if (SealHandler.types.size() + 1 != VARIANTS.length) {
            String[] rs = SealHandler.getRegisteredSeals();
            String[] out = new String[rs.length + 1];
            out[0] = "blank";
            int indx = 1;
            for (String s : rs) {
                String[] sp = s.split(":");
                out[indx] = ((sp.length > 1) ? sp[1] : sp[0]);
                ++indx;
            }
            VARIANTS = out;
            VARIANTS_META = new int[VARIANTS.length];
            for (int m = 0; m < VARIANTS.length; ++m) {
                VARIANTS_META[m] = m;
            }
        }
        return VARIANTS;
    }
    
    public static ItemStack getSealStack(String sealKey) {
        String[] rs = SealHandler.getRegisteredSeals();
        int indx = 1;
        for (String s : rs) {
            if (s.equals(sealKey)) {
                return new ItemStack(ItemsTC.seals.asItem(), 1);
            }
            ++indx;
        }
        return null;
    }
    
    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, net.minecraft.world.item.context.UseOnContext context) {
        Player player = context.getPlayer();
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();
        InteractionHand hand = context.getHand();
        if (player == null) return InteractionResult.PASS;
        if (world.isClientSide() || player.getItemInHand(hand).getDamageValue() == 0 || player.isCrouching()) {
            return InteractionResult.PASS;
        }
        if (!player.mayUseItemAt(pos, side, player.getItemInHand(hand))) {
            return InteractionResult.FAIL;
        }
        String[] rs = SealHandler.getRegisteredSeals();
        ISeal seal = null;
        try {
            seal = SealHandler.getSeal(rs[player.getItemInHand(hand).getDamageValue() - 1]).getClass().newInstance();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (seal == null || !seal.canPlaceAt(world, pos, side)) {
            return InteractionResult.FAIL;
        }
        if (SealHandler.addSealEntity(world, pos, side, seal, player) && !player.getAbilities().instabuild) {
            player.getItemInHand(hand).shrink(1);
        }
        return InteractionResult.SUCCESS;
    }
    
    public boolean doesSneakBypassUse(ItemStack stack, BlockGetter world, BlockPos pos, Player player) {
        return true;
    }
}
