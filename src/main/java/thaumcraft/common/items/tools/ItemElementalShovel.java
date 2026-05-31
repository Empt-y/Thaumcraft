package thaumcraft.common.items.tools;
import net.minecraft.world.item.ToolMaterial;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Set;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.Level;
import thaumcraft.api.items.IArchitect;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.lib.utils.Utils;


public class ItemElementalShovel extends ShovelItem implements IArchitect, IThaumcraftItems
{
    private static Block[] isEffective;
    Direction side;

    public ItemElementalShovel(ToolMaterial enumtoolmaterial) {
        super(enumtoolmaterial, 1.5f, -3.0f, thaumcraft.common.config.TCItemInit.take());
        side = Direction.DOWN;
        ConfigItems.ITEM_VARIANT_HOLDERS.add(this);
    }

    public Item getItem() {
        return this;
    }

    public String[] getVariantNames() {
        return new String[] { "normal" };
    }

    public int[] getVariantMeta() {
        return new int[] { 0 };
    }

    public Object getCustomModelResourceLocation(String variant) {
        return null;
    }

    public Set<String> getToolClasses(ItemStack stack) {
        return ImmutableSet.of("shovel");
    }

    public Rarity getRarity(ItemStack itemstack) {
        return Rarity.RARE;
    }

    public boolean isValidRepairItem(ItemStack repairItem) {
        return ItemStack.isSameItem(repairItem, new ItemStack(ItemsTC.ingots, 1)) || false;
    }

    public InteractionResult onItemUse(Player player, Level world, BlockPos pos, InteractionHand hand, Direction side, float par8, float par9, float par10) {
        BlockState bs = world.getBlockState(pos);
        BlockEntity te = world.getBlockEntity(pos);
        if (te == null) {
            for (int aa = -1; aa <= 1; ++aa) {
                for (int bb = -1; bb <= 1; ++bb) {
                    int xx = 0;
                    int yy = 0;
                    int zz = 0;
                    byte o = getOrientation(player.getItemInHand(hand));
                    if (o == 1) {
                        yy = bb;
                        if (side.ordinal() <= 1) {
                            int l = Mth.floor(player.getYRot() * 4.0f / 360.0f + 0.5) & 0x3;
                            if (l == 0 || l == 2) {
                                xx = aa;
                            }
                            else {
                                zz = aa;
                            }
                        }
                        else if (side.ordinal() <= 3) {
                            zz = aa;
                        }
                        else {
                            xx = aa;
                        }
                    }
                    else if (o == 2) {
                        if (side.ordinal() <= 1) {
                            int l = Mth.floor(player.getYRot() * 4.0f / 360.0f + 0.5) & 0x3;
                            yy = bb;
                            if (l == 0 || l == 2) {
                                xx = aa;
                            }
                            else {
                                zz = aa;
                            }
                        }
                        else {
                            zz = bb;
                            xx = aa;
                        }
                    }
                    else if (side.ordinal() <= 1) {
                        xx = aa;
                        zz = bb;
                    }
                    else if (side.ordinal() <= 3) {
                        xx = aa;
                        yy = bb;
                    }
                    else {
                        zz = aa;
                        yy = bb;
                    }
                    BlockPos p2 = pos.relative(side).offset(xx, yy, zz);
                    BlockState b2 = world.getBlockState(p2);
                    if (b2.canSurvive(world, p2)) {
                        if (player.getAbilities().instabuild || InventoryUtils.consumePlayerItem(player, bs.getBlock().asItem(), 0)) {
                            world.playSound(null, p2, bs.getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.6f, 0.9f + net.minecraft.util.RandomSource.create().nextFloat() * 0.2f);
                            world.setBlockAndUpdate(p2, bs);
                            if (!world.isClientSide() && world instanceof net.minecraft.server.level.ServerLevel sl) {
                                player.getItemInHand(hand).hurtAndBreak(1, player, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
                            }
                            if (world.isClientSide()) {
                                FXDispatcher.INSTANCE.drawBamf(p2, 8401408, false, false, side);
                            }
                            player.swing(hand);
                        }
                        else if (bs.getBlock() == Blocks.GRASS_BLOCK && (player.getAbilities().instabuild || InventoryUtils.consumePlayerItem(player, Blocks.DIRT.asItem(), 0))) {
                            world.playSound(null, p2, bs.getSoundType().getBreakSound(), SoundSource.BLOCKS, 0.6f, 0.9f + net.minecraft.util.RandomSource.create().nextFloat() * 0.2f);
                            world.setBlockAndUpdate(p2, Blocks.DIRT.defaultBlockState());
                            if (!world.isClientSide() && world instanceof net.minecraft.server.level.ServerLevel sl) {
                                player.getItemInHand(hand).hurtAndBreak(1, player, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
                            }
                            if (world.isClientSide()) {
                                FXDispatcher.INSTANCE.drawBamf(p2, 8401408, false, false, side);
                            }
                            player.swing(hand);
                            if (player.getItemInHand(hand).isEmpty()) {
                                break;
                            }
                            if (player.getItemInHand(hand).getCount() < 1) {
                                break;
                            }
                        }
                    }
                }
            }
        }
        return InteractionResult.FAIL;
    }

    private boolean isEffectiveAgainst(Block block) {
        for (int var3 = 0; var3 < ItemElementalShovel.isEffective.length; ++var3) {
            if (ItemElementalShovel.isEffective[var3] == block) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<BlockPos> getArchitectBlocks(ItemStack focusstack, Level world, BlockPos pos, Direction side, Player player) {
        ArrayList<BlockPos> b = new ArrayList<BlockPos>();
        if (!player.isCrouching()) {
            return b;
        }
        BlockState bs = world.getBlockState(pos);
        for (int aa = -1; aa <= 1; ++aa) {
            for (int bb = -1; bb <= 1; ++bb) {
                int xx = 0;
                int yy = 0;
                int zz = 0;
                byte o = getOrientation(focusstack);
                if (o == 1) {
                    yy = bb;
                    if (side.ordinal() <= 1) {
                        int l = Mth.floor(player.getYRot() * 4.0f / 360.0f + 0.5) & 0x3;
                        if (l == 0 || l == 2) {
                            xx = aa;
                        }
                        else {
                            zz = aa;
                        }
                    }
                    else if (side.ordinal() <= 3) {
                        zz = aa;
                    }
                    else {
                        xx = aa;
                    }
                }
                else if (o == 2) {
                    if (side.ordinal() <= 1) {
                        int l = Mth.floor(player.getYRot() * 4.0f / 360.0f + 0.5) & 0x3;
                        yy = bb;
                        if (l == 0 || l == 2) {
                            xx = aa;
                        }
                        else {
                            zz = aa;
                        }
                    }
                    else {
                        zz = bb;
                        xx = aa;
                    }
                }
                else if (side.ordinal() <= 1) {
                    xx = aa;
                    zz = bb;
                }
                else if (side.ordinal() <= 3) {
                    xx = aa;
                    yy = bb;
                }
                else {
                    zz = aa;
                    yy = bb;
                }
                BlockPos p2 = pos.relative(side).offset(xx, yy, zz);
                BlockState b2 = world.getBlockState(p2);
                if (b2.canSurvive(world, p2)) {
                    b.add(p2);
                }
            }
        }
        return b;
    }

    public boolean showAxis(ItemStack stack, Level world, Player player, Direction side, EnumAxis axis) {
        return false;
    }

    public static byte getOrientation(ItemStack stack) {
        if (!stack.isEmpty() && stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("or")) {
            return stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getByteOr("or", (byte)0);
        }
        return 0;
    }

    public static void setOrientation(ItemStack stack, byte o) {
        if (!stack.isEmpty()) {
            net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, stack, nbt -> nbt.putByte("or", (byte)(o % 3)));
        }
    }

    public HitResult getArchitectMOP(ItemStack stack, Level world, LivingEntity player) {
        return Utils.rayTrace(world, player, false);
    }

    public boolean useBlockHighlight(ItemStack stack) {
        return true;
    }

    static {
        isEffective = new Block[] {Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.SAND, Blocks.GRAVEL, Blocks.SNOW, Blocks.CLAY, Blocks.FARMLAND, Blocks.SOUL_SAND, Blocks.MYCELIUM};
    }
}
