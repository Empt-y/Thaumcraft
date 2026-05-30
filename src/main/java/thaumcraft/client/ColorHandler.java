package thaumcraft.client;
import java.util.Arrays;
import java.util.List;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.GrassColor;
import net.minecraft.world.level.FoliageColor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.devices.BlockInlay;
import thaumcraft.common.blocks.devices.BlockStabilizer;
import thaumcraft.common.blocks.essentia.BlockTube;
import thaumcraft.common.blocks.world.ore.BlockCrystal;
import thaumcraft.common.tiles.devices.TileStabilizer;
import thaumcraft.common.tiles.essentia.TileTubeFilter;


@OnlyIn(Dist.CLIENT)
public class ColorHandler
{
    public static void registerColourHandlers() {
        BlockColors blockColors = Minecraft.getInstance().getBlockColors();
        registerBlockColourHandlers(blockColors);
        // Item colour handlers are now data-driven via ItemTintSource / model JSON in MC 26
    }

    private static void registerBlockColourHandlers(BlockColors blockColors) {
        BlockTintSource basicColourHandler = state -> state.getBlock().defaultMapColor().col;
        Block[] basicBlocks = new Block[BlocksTC.candles.size() + BlocksTC.banners.size() + BlocksTC.nitor.size()];
        int i = 0;
        for (Block b : BlocksTC.candles.values()) { basicBlocks[i] = b; ++i; }
        for (Block b : BlocksTC.banners.values()) { basicBlocks[i] = b; ++i; }
        for (Block b : BlocksTC.nitor.values())  { basicBlocks[i] = b; ++i; }
        blockColors.register(List.of(basicColourHandler), basicBlocks);

        BlockTintSource grassColourHandler = new BlockTintSource() {
            @Override public int color(BlockState state) { return GrassColor.get(0.5, 1.0); }
            @Override public int colorInWorld(BlockState state, BlockAndTintGetter blockAccess, BlockPos pos) {
                return BiomeColors.getAverageGrassColor(blockAccess, pos);
            }
        };
        blockColors.register(List.of(grassColourHandler), BlocksTC.grassAmbient);

        BlockTintSource leafColourHandler = new BlockTintSource() {
            @Override public int color(BlockState state) {
                return (state.getBlock() == BlocksTC.leafSilverwood) ? 16777215 : FoliageColor.FOLIAGE_DEFAULT;
            }
            @Override public int colorInWorld(BlockState state, BlockAndTintGetter blockAccess, BlockPos pos) {
                if (state.getBlock() == BlocksTC.leafSilverwood) return 16777215;
                return BiomeColors.getAverageFoliageColor(blockAccess, pos);
            }
        };
        blockColors.register(List.of(leafColourHandler), BlocksTC.leafGreatwood, BlocksTC.leafSilverwood);

        BlockTintSource crystalColourHandler = state ->
            (state.getBlock() instanceof BlockCrystal) ? ((BlockCrystal)state.getBlock()).aspect.getColor() : 16777215;
        blockColors.register(List.of(crystalColourHandler),
            BlocksTC.crystalAir, BlocksTC.crystalEarth, BlocksTC.crystalFire, BlocksTC.crystalWater,
            BlocksTC.crystalEntropy, BlocksTC.crystalOrder, BlocksTC.crystalTaint);

        BlockTintSource noTint = state -> 16777215;
        BlockTintSource tubeFilterSource = new BlockTintSource() {
            @Override public int color(BlockState state) { return 16777215; }
            @Override public int colorInWorld(BlockState state, BlockAndTintGetter blockAccess, BlockPos pos) {
                if (state.getBlock() instanceof BlockTube) {
                    BlockEntity te = blockAccess.getBlockEntity(pos);
                    if (te instanceof TileTubeFilter && ((TileTubeFilter)te).aspectFilter != null) {
                        return ((TileTubeFilter)te).aspectFilter.getColor();
                    }
                }
                return 16777215;
            }
        };
        blockColors.register(Arrays.asList(noTint, tubeFilterSource), BlocksTC.tubeFilter);

        BlockTintSource inlayColourHandler = state -> BlockInlay.colorMultiplier(0);
        blockColors.register(List.of(inlayColourHandler), BlocksTC.inlay);

        BlockTintSource stabilizerSource = new BlockTintSource() {
            @Override public int color(BlockState state) { return BlockStabilizer.colorMultiplier(0); }
            @Override public int colorInWorld(BlockState state, BlockAndTintGetter blockAccess, BlockPos pos) {
                int charge = 0;
                BlockEntity te = blockAccess.getBlockEntity(pos);
                if (te instanceof TileStabilizer) charge = ((TileStabilizer)te).getEnergy();
                return BlockStabilizer.colorMultiplier(charge);
            }
        };
        blockColors.register(List.of(stabilizerSource), BlocksTC.stabilizer);
    }
}
