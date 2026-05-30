package thaumcraft.common.blocks.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.blocks.crafting.BlockGolemBuilder;
import thaumcraft.common.blocks.devices.BlockInfernalFurnace;

public class BlockPlaceholder extends BlockTC
{
    public BlockPlaceholder() {
        super(BlockBehaviour.Properties.of()
                .strength(2.5f)
                .sound(SoundType.STONE)
                .noLootTable());
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        if (state.getBlock() == BlocksTC.placeholderCauldron) {
            return 13;
        }
        return super.getLightEmission(state, world, pos);
    }

    /** The drop item for each placeholder variant — referenced from loot table JSONs. */
    public Item getDropItem(BlockState state) {
        if (state.getBlock() == BlocksTC.placeholderNetherbrick) return Blocks.NETHER_BRICKS.asItem();
        if (state.getBlock() == BlocksTC.placeholderObsidian)    return Blocks.OBSIDIAN.asItem();
        if (state.getBlock() == BlocksTC.placeholderBars)        return Blocks.IRON_BARS.asItem();
        if (state.getBlock() == BlocksTC.placeholderAnvil)       return Blocks.ANVIL.asItem();
        if (state.getBlock() == BlocksTC.placeholderCauldron)    return Blocks.CAULDRON.asItem();
        if (state.getBlock() == BlocksTC.placeholderTable)       return BlocksTC.tableStone.asItem();
        return Items.AIR;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (state.getBlock() != BlocksTC.placeholderNetherbrick && state.getBlock() != BlocksTC.placeholderObsidian) {
            for (int a = -1; a <= 1; ++a) {
                for (int b = -1; b <= 1; ++b) {
                    for (int c = -1; c <= 1; ++c) {
                        BlockState s = world.getBlockState(pos.offset(a, b, c));
                        if (s.getBlock() == BlocksTC.golemBuilder) {
                            // TODO: open golem builder menu via modern openMenu
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return super.useWithoutItem(state, world, pos, player, hit);
    }

    /**
     * onRemove was removed in 26.x. affectNeighborsAfterRemoval fires when the block
     * leaves the world, giving a ServerLevel for write access.
     */
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel world, BlockPos pos, boolean movedByPiston) {
        if ((state.getBlock() == BlocksTC.placeholderNetherbrick || state.getBlock() == BlocksTC.placeholderObsidian)
                && !BlockInfernalFurnace.ignore) {
            for (int a = -1; a <= 1; ++a) {
                for (int b = -1; b <= 1; ++b) {
                    for (int c = -1; c <= 1; ++c) {
                        BlockState s = world.getBlockState(pos.offset(a, b, c));
                        if (s.getBlock() == BlocksTC.infernalFurnace) {
                            BlockInfernalFurnace.destroyFurnace(world, pos.offset(a, b, c), s, pos);
                            super.affectNeighborsAfterRemoval(state, world, pos, movedByPiston);
                            return;
                        }
                    }
                }
            }
        } else if (state.getBlock() != BlocksTC.placeholderNetherbrick
                && state.getBlock() != BlocksTC.placeholderObsidian
                && !BlockGolemBuilder.ignore) {
            for (int a = -1; a <= 1; ++a) {
                for (int b = -1; b <= 1; ++b) {
                    for (int c = -1; c <= 1; ++c) {
                        BlockState s = world.getBlockState(pos.offset(a, b, c));
                        if (s.getBlock() == BlocksTC.golemBuilder) {
                            BlockGolemBuilder.destroyGolem(world, pos.offset(a, b, c), s, pos);
                            super.affectNeighborsAfterRemoval(state, world, pos, movedByPiston);
                            return;
                        }
                    }
                }
            }
        }
        super.affectNeighborsAfterRemoval(state, world, pos, movedByPiston);
    }
}
