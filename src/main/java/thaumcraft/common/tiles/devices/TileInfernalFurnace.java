package thaumcraft.common.tiles.devices;
import java.util.ArrayList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.internal.CommonInternals;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;


public class TileInfernalFurnace extends TileThaumcraftInventory
{
    public int furnaceCookTime;
    public int furnaceMaxCookTime;
    public int speedyTime;
    public int facingX;
    public int facingZ;
    
    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos().getX() - 1.3, getBlockPos().getY() - 1.3, getBlockPos().getZ() - 1.3, getBlockPos().getX() + 2.3, getBlockPos().getY() + 2.3, getBlockPos().getZ() + 2.3);
    }
    
    public TileInfernalFurnace(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(32);
        facingX = -5;
        facingZ = -5;
        furnaceCookTime = 0;
        furnaceMaxCookTime = 0;
        speedyTime = 0;
    }
    
    @Override
    public int[] getSlotsForFace(Direction par1) {
        return (par1 == Direction.UP) ? super.getSlotsForFace(par1) : new int[0];
    }
    
    @Override
    public boolean canExtractItem(int par1, ItemStack stack2, Direction par3) {
        return false;
    }
    
        @Override
    public void loadAdditional(net.minecraft.world.level.storage.ValueInput input) {
        super.loadAdditional(input);
        furnaceCookTime = input.getIntOr("CookTime", 0);
        speedyTime = input.getIntOr("SpeedyTime", 0);
    }

    @Override
    public void saveAdditional(net.minecraft.world.level.storage.ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("CookTime", furnaceCookTime);
        output.putInt("SpeedyTime", speedyTime);
    }
    
    @Override
    public void update() {
        super.update();
        if (facingX == -5) {
            setFacing();
        }
        if (!getLevel().isClientSide()) {
            boolean cookedflag = false;
            if (furnaceCookTime > 0) {
                --furnaceCookTime;
                cookedflag = true;
            }
            if (furnaceMaxCookTime <= 0) {
                furnaceMaxCookTime = calcCookTime();
            }
            if (furnaceCookTime > furnaceMaxCookTime) {
                furnaceCookTime = furnaceMaxCookTime;
            }
            if (furnaceCookTime <= 0 && cookedflag) {
                for (int a = 0; a < getSizeInventory(); ++a) {
                    if (getStackInSlot(a) != null && !getStackInSlot(a).isEmpty()) {
                        ItemStack itemstack = null; /* removed */
//
                        if (itemstack != null && !itemstack.isEmpty()) {
                            if (speedyTime > 0) {
                                --speedyTime;
                            }
                            ejectItem(itemstack.copy(), getStackInSlot(a));
                            getLevel().blockEvent(getBlockPos(), BlocksTC.infernalFurnace, 3, 0);
                            if (getLevel().getRandom().nextInt(20) == 0) {
                                AuraHelper.polluteAura(getLevel(), getBlockPos().relative(getFacing().getOpposite()), 1.0f, true);
                            }
                            decrStackSize(a, 1);
                            break;
                        }
                        setInventorySlotContents(a, ItemStack.EMPTY);
                    }
                }
            }
            if (speedyTime <= 0) {
                speedyTime = (int)AuraHelper.drainVis(getLevel(), getBlockPos(), 20.0f, false);
            }
            if (furnaceCookTime == 0 && !cookedflag) {
                for (int a = 0; a < getSizeInventory(); ++a) {
                    if (canSmelt(getStackInSlot(a))) {
                        furnaceMaxCookTime = calcCookTime();
                        furnaceCookTime = furnaceMaxCookTime;
                        break;
                    }
                }
            }
        }
    }
    
    private int getBellows() {
        int bellows = 0;
        for (Direction dir : Direction.values()) {
            if (dir != Direction.UP) {
                BlockPos p2 = getBlockPos().relative(dir, 2);
                BlockEntity tile = getLevel().getBlockEntity(p2);
                if (tile != null && tile instanceof TileBellows && BlockStateUtils.getFacing(getLevel().getBlockState(p2)) == dir.getOpposite() && getLevel().isBlockIndirectlyGettingPowered(p2) == 0) {
                    ++bellows;
                }
            }
        }
        return Math.min(4, bellows);
    }
    
    private int calcCookTime() {
        int b = getBellows();
        if (b > 0) {
            b *= 20 - (b - 1);
        }
        return Math.max(10, ((speedyTime > 0) ? 80 : 140) - b);
    }
    
    public ItemStack addItemsToInventory(ItemStack items) {
        if (canSmelt(items)) {
            items = ThaumcraftInvHelper.insertStackAt(getLevel(), getBlockPos(), Direction.UP, items, false);
        }
        else {
            destroyItem();
            items = ItemStack.EMPTY;
        }
        return items;
    }
    
    private void destroyItem() {
        getLevel().playLocalSound(getBlockPos(), SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.3f, 2.6f + (random.nextFloat() - random.nextFloat()) * 0.8f, false);
        // spawnParticle removed
    }
    
    public void ejectItem(ItemStack items, ItemStack furnaceItemStack) {
        if (items == null || items.isEmpty()) {
            return;
        }
        ArrayList<ItemStack> ejecti = new ArrayList<ItemStack>();
        ejecti.add(items.copy());
        int bellows = getBellows() + 1;
        float lx = 0.5f;
        lx += facingX * 1.2f;
        float lz = 0.5f;
        lz += facingZ * 1.2f;
        float mx = 0.0f;
        float mz = 0.0f;
        for (int a = 0; a < bellows; ++a) {
            ItemStack[] boni = getSmeltingBonus(furnaceItemStack);
            if (boni != null) {
                for (ItemStack bonus : boni) {
                    if (!bonus.isEmpty() && bonus.getCount() > 0) {
                        ejecti.add(bonus);
                    }
                }
            }
        }
        for (ItemStack outItem : ejecti) {
            if (outItem.isEmpty()) {
                continue;
            }
            Direction facing = getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING).getOpposite();
            InventoryUtils.ejectStackAt(getLevel(), getBlockPos(), facing, outItem);
        }
        // XP calculation removed (getExperience API no longer available on recipes directly)
        // No XP spawned from infernal furnace in this port
    }
    
    private ItemStack[] getSmeltingBonus(ItemStack in) {
        ArrayList<ItemStack> out = new ArrayList<ItemStack>();
        for (ThaumcraftApi.SmeltBonus bonus : CommonInternals.smeltingBonus) {
            if (bonus.in instanceof ItemStack) {
                if (in.getItem() != ((ItemStack)bonus.in).getItem() || (in.getDamageValue() != ((ItemStack)bonus.in).getDamageValue() && ((ItemStack)bonus.in).getDamageValue() != 32767) || random.nextFloat() > bonus.chance) {
                    continue;
                }
                ItemStack is = bonus.out.copy();
                if (is.getCount() < 1) {
                    is.setCount(1);
                }
                out.add(is);
            }
            // OreDictionary removed - tag-based matching not implemented here
        }
        return out.toArray(new ItemStack[0]);
    }
    
    private boolean canSmelt(ItemStack stack) {
        return false; /* canSmelt removed */
    }
    
    private void setFacing() {
        facingX = 0;
        facingZ = 0;
        Direction face = getFacing().getOpposite();
        facingX = face.getStepX();
        facingZ = face.getStepZ();
    }
    
    public boolean receiveClientEvent(int i, int j) {
        if (i == 3) {
            if (getLevel().isClientSide()) {
                for (int a = 0; a < 5; ++a) {
                    FXDispatcher.INSTANCE.furnaceLavaFx(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), facingX, facingZ);
                    getLevel().playLocalSound(getBlockPos(), SoundEvents.LAVA_POP, SoundSource.BLOCKS, 0.1f + random.nextFloat() * 0.1f, 0.9f + random.nextFloat() * 0.15f, false);
                }
            }
            return true;
        }
        return super.receiveClientEvent(i, j);
    }
}
