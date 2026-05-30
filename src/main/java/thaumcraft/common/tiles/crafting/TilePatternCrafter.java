package thaumcraft.common.tiles.crafting;
import java.util.List;
import java.util.Random;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import thaumcraft.common.lib.crafting.ContainerFake;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
// ITickable removed - use BlockEntityTicker<T>
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TileThaumcraft;


public class TilePatternCrafter extends TileThaumcraft 
{
    public byte type;
    public int count;
    private CraftingContainer craftMatrix;
    float power;
    public float rot;
    public float rp;
    public int rotTicks;
    ItemStack outStack;
    
    public TilePatternCrafter(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        type = 0;
        count = new Random(System.currentTimeMillis()).nextInt(20);
        craftMatrix = new TransientCraftingContainer(new ContainerFake(), 3, 3);
        power = 0.0f;
        rotTicks = 0;
        outStack = null;
    }
    
    @Override
    public void readSyncNBT(CompoundTag nbt) {
        type = nbt.getByteOr("type", (byte)0);
    }
    
    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbt) {
        nbt.putByte("type", type);
        return nbt;
    }
    
        public void loadAdditional(CompoundTag nbt) {
        power = nbt.getFloatOr("power", 0.0f);
        /* super.loadAdditional removed - CompoundTag not compatible with ValueInput */
    }
    
        public CompoundTag saveAdditional(CompoundTag nbt) {
        nbt.putFloat("power", power);
        return nbt;
    }
    
    public void update() {
        if (getLevel().isClientSide()) {
            if (rotTicks > 0) {
                --rotTicks;
                if (rotTicks % Math.floor(Math.max(1.0f, rp)) == 0.0) {
                    getLevel().playSound(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5, getBlockPos().getZ() + 0.5, SoundsTC.clack, SoundSource.BLOCKS, 0.2f, 1.7f, false);
                }
                ++rp;
            }
            else {
                rp *= 0.8f;
            }
            rot += rp;
        }
        if (!getLevel().isClientSide() && count++ % 20 == 0 && BlockStateUtils.isEnabled(getBlockState().getBlockState())) {
            if (power <= 0.0f) {
                power += AuraHelper.drainVis(getLevel(), getBlockPos(), 5.0f, false);
            }
            int amt = 9;
            switch (type) {
                case 0: {
                    amt = 9;
                    break;
                }
                case 1: {
                    amt = 1;
                    break;
                }
                case 2:
                case 3: {
                    amt = 2;
                    break;
                }
                case 4: {
                    amt = 4;
                    break;
                }
                case 5:
                case 6: {
                    amt = 3;
                    break;
                }
                case 7:
                case 8: {
                    amt = 6;
                    break;
                }
                case 9: {
                    amt = 8;
                    break;
                }
            }
            IItemHandler above = ThaumcraftInvHelper.getItemHandlerAt(getLevel(), getBlockPos().above(), Direction.DOWN);
            IItemHandler below = ThaumcraftInvHelper.getItemHandlerAt(getLevel(), getBlockPos().below(), Direction.UP);
            if (above != null && below != null) {
                for (int a = 0; a < above.getSlots(); ++a) {
                    ItemStack testStack = above.getItem(a).copy();
                    if (!testStack.isEmpty()) {
                        testStack.setCount(amt);
                        if (InventoryUtils.removeStackFrom(getLevel(), getBlockPos().above(), Direction.DOWN, testStack.copy(), ThaumcraftInvHelper.InvFilter.BASEORE, true).getCount() == amt && craft(testStack) && power >= 1.0f && ItemHandlerHelper.insertItem(below, outStack.copy(), true).isEmpty()) {
                            boolean b = true;
                            for (int i = 0; i < 9; ++i) {
                                if (craftMatrix.getItem(i) != null && !ItemHandlerHelper.insertItem(below, craftMatrix.getItem(i).copy(), true).isEmpty()) {
                                    b = false;
                                    break;
                                }
                            }
                            if (b) {
                                ItemHandlerHelper.insertItem(below, outStack.copy(), false);
                                for (int i = 0; i < 9; ++i) {
                                    if (craftMatrix.getItem(i) != null) {
                                        ItemHandlerHelper.insertItem(below, craftMatrix.getItem(i).copy(), false);
                                    }
                                }
                                InventoryUtils.removeStackFrom(getLevel(), getBlockPos().above(), Direction.DOWN, testStack, ThaumcraftInvHelper.InvFilter.BASEORE, false);
                                getLevel().blockEvent(getBlockPos(), getBlockState().getBlock(), 1, 0);
                                --power;
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    private boolean craft(ItemStack inStack) {
        outStack = ItemStack.EMPTY;
        craftMatrix.clear();
        switch (type) {
            case 0: {
                for (int a = 0; a < 9; ++a) {
                    craftMatrix.setItem(a, (inStack).copyWithCount(1));
                }
                break;
            }
            case 1: {
                craftMatrix.setItem(0, (inStack).copyWithCount(1));
                break;
            }
            case 2: {
                for (int a = 0; a < 2; ++a) {
                    craftMatrix.setItem(a, (inStack).copyWithCount(1));
                }
                break;
            }
            case 3: {
                for (int a = 0; a < 2; ++a) {
                    craftMatrix.setItem(a * 3, (inStack).copyWithCount(1));
                }
                break;
            }
            case 4: {
                for (int a = 0; a < 2; ++a) {
                    for (int b = 0; b < 2; ++b) {
                        craftMatrix.setItem(a + b * 3, (inStack).copyWithCount(1));
                    }
                }
                break;
            }
            case 5: {
                for (int a = 0; a < 3; ++a) {
                    craftMatrix.setItem(a, (inStack).copyWithCount(1));
                }
                break;
            }
            case 6: {
                for (int a = 0; a < 3; ++a) {
                    craftMatrix.setItem(a * 3, (inStack).copyWithCount(1));
                }
                break;
            }
            case 7: {
                for (int a = 0; a < 6; ++a) {
                    craftMatrix.setItem(a, (inStack).copyWithCount(1));
                }
                break;
            }
            case 8: {
                for (int a = 0; a < 2; ++a) {
                    for (int b = 0; b < 3; ++b) {
                        craftMatrix.setItem(a + b * 3, (inStack).copyWithCount(1));
                    }
                }
                break;
            }
            case 9: {
                for (int a = 0; a < 9; ++a) {
                    if (a != 4) {
                        craftMatrix.setItem(a, (inStack).copyWithCount(1));
                    }
                }
                break;
            }
        }
        Recipe ir = null /* CraftingManager removed */;
        if (ir == null) {
            return false;
        }
        outStack = ir.getCraftingResult(craftMatrix);
        NonNullList<ItemStack> aitemstack = null /* CraftingManager removed */;
        for (int i = 0; i < aitemstack.size(); ++i) {
            ItemStack itemstack1 = craftMatrix.getItem(i);
            ItemStack itemstack2 = aitemstack.get(i);
            if (!itemstack1.isEmpty()) {
                craftMatrix.setItem(i, ItemStack.EMPTY);
            }
            if (!itemstack1.isEmpty() && craftMatrix.getItem(i).isEmpty()) {
                craftMatrix.setItem(i, itemstack2);
            }
        }
        return !outStack.isEmpty();
    }
    
    public void cycle() {
        ++type;
        if (type > 9) {
            type = 0;
        }
        syncTile(false);
        setChanged();
    }
    
    public boolean triggerEvent(int i, int j) {
        if (i == 1) {
            if (getLevel().isClientSide()) {
                rotTicks = 10;
            }
            return true;
        }
        return super.triggerEvent(i, j);
    }
    
    public HitResult rayTrace(Level world, Vec3 vec3d, Vec3 vec3d1, HitResult fullblock) {
        return fullblock;
    }
    
    public void addTraceableCuboids(List<IndexedCuboid6> cuboids) {
        Direction facing = getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
        cuboids.add(new IndexedCuboid6(0, getCuboidByFacing(facing)));
    }
    
    public Cuboid6 getCuboidByFacing(Direction facing) {
        switch (facing) {
            default: {
                return new Cuboid6(getBlockPos().getX() + 0.75, getBlockPos().getY() + 0.125, getBlockPos().getZ() + 0.375, getBlockPos().getX() + 0.875, getBlockPos().getY() + 0.375, getBlockPos().getZ() + 0.625);
            }
            case EAST: {
                return new Cuboid6(getBlockPos().getX() + 0.125, getBlockPos().getY() + 0.125, getBlockPos().getZ() + 0.375, getBlockPos().getX() + 0.25, getBlockPos().getY() + 0.375, getBlockPos().getZ() + 0.625);
            }
            case NORTH: {
                return new Cuboid6(getBlockPos().getX() + 0.375, getBlockPos().getY() + 0.125, getBlockPos().getZ() + 0.75, getBlockPos().getX() + 0.625, getBlockPos().getY() + 0.375, getBlockPos().getZ() + 0.875);
            }
            case SOUTH: {
                return new Cuboid6(getBlockPos().getX() + 0.375, getBlockPos().getY() + 0.125, getBlockPos().getZ() + 0.125, getBlockPos().getX() + 0.625, getBlockPos().getY() + 0.375, getBlockPos().getZ() + 0.25);
            }
        }
    }
}
