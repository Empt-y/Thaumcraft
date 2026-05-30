package thaumcraft.common.lib.crafting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.block.Block;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
// FML FMLCommonHandler removed
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.api.crafting.Part;
import thaumcraft.common.blocks.IBlockFacingHorizontal;
import thaumcraft.common.container.InventoryFake;
import thaumcraft.common.lib.events.ServerEvents;
import thaumcraft.common.lib.events.ToolEvents;
import thaumcraft.common.lib.utils.BlockUtils;


public class DustTriggerMultiblock implements IDustTrigger
{
    Part[][][] blueprint;
    String research;
    int ySize;
    int xSize;
    int zSize;

    public DustTriggerMultiblock(String research, Part[][][] blueprint) {
        this.blueprint = blueprint;
        this.research = research;
        ySize = this.blueprint.length;
        xSize = this.blueprint[0].length;
        zSize = this.blueprint[0][0].length;
    }

    @Override
    public Placement getValidFace(Level world, Player player, BlockPos pos, Direction face) {
        if (research != null && !ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(research)) {
            return null;
        }
        for (int yy = -ySize; yy <= 0; ++yy) {
            for (int xx = -xSize; xx <= 0; ++xx) {
                for (int zz = -zSize; zz <= 0; ++zz) {
                    BlockPos p2 = pos.offset(xx, yy, zz);
                    Direction f = fitMultiblock(world, p2);
                    if (f != null) {
                        return new Placement(xx, yy, zz, f);
                    }
                }
            }
        }
        return null;
    }

    private Direction fitMultiblock(Level world, BlockPos pos) {
        Direction[] horizontals = Direction.Plane.HORIZONTAL.stream().toArray(Direction[]::new);
        int length = horizontals.length;
        int i = 0;
    Label_0011:
        while (i < length) {
            Direction face = horizontals[i];
            for (int y = 0; y < ySize; ++y) {
                Matrix matrix = new Matrix(blueprint[y]);
                matrix.Rotate90DegRight(3 - face.get2DDataValue());
                for (int x = 0; x < matrix.rows; ++x) {
                    for (int z = 0; z < matrix.cols; ++z) {
                        if (matrix.matrix[x][z] != null) {
                            BlockState bsWo = world.getBlockState(pos.offset(x, -y + (ySize - 1), z));
                            Label_0382: {
                                if (!(matrix.matrix[x][z].getSource() instanceof Block) || bsWo.getBlock() == matrix.matrix[x][z].getSource()) {
                                    // Material removed in 1.20+ — skip material checks (always pass)
                                    if (matrix.matrix[x][z].getSource() instanceof ItemStack) {
                                        ItemStack srcStack = (ItemStack)matrix.matrix[x][z].getSource();
                                        if (bsWo.getBlock() != Block.byItem(srcStack.getItem())) {
                                            break Label_0382;
                                        }
                                        // getDamageValue() comparison vs block removed — metadata gone in 1.13+
                                    }
                                    if (!(matrix.matrix[x][z].getSource() instanceof BlockState) || bsWo == matrix.matrix[x][z].getSource()) {
                                        continue;
                                    }
                                }
                            }
                            ++i;
                            continue Label_0011;
                        }
                    }
                }
            }
            return face;
        }
        return null;
    }

    @Override
    public List<BlockPos> sparkle(Level world, Player player, BlockPos pos, Placement placement) {
        BlockPos p2 = pos.offset(placement.getXOffset(), placement.getYOffset(), placement.getZOffset());
        ArrayList<BlockPos> list = new ArrayList<BlockPos>();
        for (int y = 0; y < ySize; ++y) {
            Matrix matrix = new Matrix(blueprint[y]);
            matrix.Rotate90DegRight(3 - placement.getFacing().get2DDataValue());
            for (int x = 0; x < matrix.rows; ++x) {
                for (int z = 0; z < matrix.cols; ++z) {
                    if (matrix.matrix[x][z] != null) {
                        BlockPos p3 = p2.offset(x, -y + (ySize - 1), z);
                        if (matrix.matrix[x][z].getSource() != null && BlockUtils.isBlockExposed(world, p3)) {
                            list.add(p3);
                        }
                    }
                }
            }
        }
        return list;
    }

    @Override
    public void execute(Level world, Player player, BlockPos pos, Placement placement, Direction side) {
        if (!world.isClientSide()) {
            BlockPos p2 = pos.offset(placement.getXOffset(), placement.getYOffset(), placement.getZOffset());
            for (int y = 0; y < ySize; ++y) {
                Matrix matrix = new Matrix(blueprint[y]);
                matrix.Rotate90DegRight(3 - placement.getFacing().get2DDataValue());
                for (int x = 0; x < matrix.rows; ++x) {
                    for (int z = 0; z < matrix.cols; ++z) {
                        if (matrix.matrix[x][z] != null && matrix.matrix[x][z].getTarget() != null) {
                            ItemStack targetObject;
                            if (matrix.matrix[x][z].getTarget() instanceof Block) {
                                int meta = 0;
                                Direction side2 = side;
                                if (matrix.matrix[x][z].getTarget() instanceof IBlockFacingHorizontal) {
                                    if (side2.get2DDataValue() < 0) {
                                        side2 = player.getDirection().getOpposite();
                                    }
                                    BlockState state = ((Block)matrix.matrix[x][z].getTarget()).defaultBlockState().setValue((Property)IBlockFacingHorizontal.FACING, (Comparable)(matrix.matrix[x][z].getApplyPlayerFacing() ? side2 : (matrix.matrix[x][z].isOpp() ? placement.getFacing().getOpposite() : placement.getFacing())));
                                    meta = 0; // getMetaFromState removed in 1.13+
                                }
                                targetObject = new ItemStack(((Block)matrix.matrix[x][z].getTarget()).asItem(), 1);
                            }
                            else if (matrix.matrix[x][z].getTarget() instanceof ItemStack) {
                                targetObject = ((ItemStack)matrix.matrix[x][z].getTarget()).copy();
                            }
                            else {
                                targetObject = null;
                            }
                            BlockPos p3 = p2.offset(x, -y + (ySize - 1), z);
                            Object sourceObject;
                            if (matrix.matrix[x][z].getSource() instanceof Block) {
                                sourceObject = world.getBlockState(p3);
                            }
                            else if (matrix.matrix[x][z].getSource() instanceof BlockState) {
                                sourceObject = matrix.matrix[x][z].getSource();
                            }
                            else {
                                sourceObject = null;
                            }
                            ToolEvents.addBlockedBlock(world, p3);
                            ServerEvents.addRunnableServer(world, new Runnable() {
                                @Override
                                public void run() {
                                    ServerEvents.addSwapper(world, p3, sourceObject, targetObject, false, 0, player, true, false, -9999, false, false, 0, ServerEvents.DEFAULT_PREDICATE, 0.0f);
                                    ToolEvents.clearBlockedBlock(world, p3);
                                }
                            }, matrix.matrix[x][z].getPriority());
                        }
                    }
                }
            }
        }
    }
}
