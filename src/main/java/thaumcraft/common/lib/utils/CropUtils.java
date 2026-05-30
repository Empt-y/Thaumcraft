package thaumcraft.common.lib.utils;
import net.minecraft.world.level.block.BonemealableBlock;
import java.util.ArrayList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;


public class CropUtils
{
    public static ArrayList<String> clickableCrops;
    public static ArrayList<String> standardCrops;
    public static ArrayList<String> stackedCrops;
    public static ArrayList<String> lampBlacklist;
    
    public static void addStandardCrop(ItemStack stack, int grownMeta) {
        Block block = Block.byItem(stack.getItem());
        if (block == null) {
            return;
        }
        addStandardCrop(block, grownMeta);
    }
    
    public static void addStandardCrop(Block block, int grownMeta) {
        if (grownMeta == 32767) {
            for (int a = 0; a < 16; ++a) {
                CropUtils.standardCrops.add(block.getDescriptionId() + a);
            }
        }
        else {
            CropUtils.standardCrops.add(block.getDescriptionId() + grownMeta);
        }
        if (block instanceof net.minecraft.world.level.block.CropBlock && grownMeta != 7) {
            CropUtils.standardCrops.add(block.getDescriptionId() + "7");
        }
    }
    
    public static void addClickableCrop(ItemStack stack, int grownMeta) {
        if (Block.byItem(stack.getItem()) == null) {
            return;
        }
        if (grownMeta == 32767) {
            for (int a = 0; a < 16; ++a) {
                CropUtils.clickableCrops.add(Block.byItem(stack.getItem()).getDescriptionId() + a);
            }
        }
        else {
            CropUtils.clickableCrops.add(Block.byItem(stack.getItem()).getDescriptionId() + grownMeta);
        }
        if (Block.byItem(stack.getItem()) instanceof net.minecraft.world.level.block.CropBlock && grownMeta != 7) {
            CropUtils.clickableCrops.add(Block.byItem(stack.getItem()).getDescriptionId() + "7");
        }
    }
    
    public static void addStackedCrop(ItemStack stack, int grownMeta) {
        if (Block.byItem(stack.getItem()) == null) {
            return;
        }
        addStackedCrop(Block.byItem(stack.getItem()), grownMeta);
    }
    
    public static void addStackedCrop(Block block, int grownMeta) {
        if (grownMeta == 32767) {
            for (int a = 0; a < 16; ++a) {
                CropUtils.stackedCrops.add(block.getDescriptionId() + a);
            }
        }
        else {
            CropUtils.stackedCrops.add(block.getDescriptionId() + grownMeta);
        }
        if (block instanceof net.minecraft.world.level.block.CropBlock && grownMeta != 7) {
            CropUtils.stackedCrops.add(block.getDescriptionId() + "7");
        }
    }
    
    public static boolean isGrownCrop(Level world, BlockPos pos) {
        if (world.isEmptyBlock(pos)) {
            return false;
        }
        boolean found = false;
        BlockState bs = world.getBlockState(pos);
        Block bi = bs.getBlock();
        int md = 0 /* getMetaFromState removed */;
        if (CropUtils.standardCrops.contains(bi.getDescriptionId() + md) || CropUtils.clickableCrops.contains(bi.getDescriptionId() + md) || CropUtils.stackedCrops.contains(bi.getDescriptionId() + md)) {
            found = true;
        }
        Block biB = world.getBlockState(pos.below()).getBlock();
        return (bi instanceof BonemealableBlock && !((BonemealableBlock)bi).isValidBonemealTarget(world, pos, world.getBlockState(pos)) && !(bi instanceof net.minecraft.world.level.block.StemBlock)) || (bi instanceof net.minecraft.world.level.block.CropBlock && md == 7 && !found) || CropUtils.standardCrops.contains(bi.getDescriptionId() + md) || CropUtils.clickableCrops.contains(bi.getDescriptionId() + md) || (CropUtils.stackedCrops.contains(bi.getDescriptionId() + md) && biB == bi);
    }
    
    public static void blacklistLamp(ItemStack stack, int meta) {
        if (Block.byItem(stack.getItem()) == null) {
            return;
        }
        if (meta == 32767) {
            for (int a = 0; a < 16; ++a) {
                CropUtils.lampBlacklist.add(Block.byItem(stack.getItem()).getDescriptionId() + a);
            }
        }
        else {
            CropUtils.lampBlacklist.add(Block.byItem(stack.getItem()).getDescriptionId() + meta);
        }
    }

    public static boolean doesLampGrow(Level world, BlockPos pos) {
        Block bi = world.getBlockState(pos).getBlock();
        int md = 0 /* getMetaFromState removed */;
        return !CropUtils.lampBlacklist.contains(bi.getDescriptionId() + md);
    }
    
    static {
        CropUtils.clickableCrops = new ArrayList<String>();
        CropUtils.standardCrops = new ArrayList<String>();
        CropUtils.stackedCrops = new ArrayList<String>();
        CropUtils.lampBlacklist = new ArrayList<String>();
    }
}
