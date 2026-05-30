package thaumcraft.common.tiles.crafting;
import java.awt.Color;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IAspectContainer;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.container.InventoryFake;
import thaumcraft.common.entities.EntitySpecialItem;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileCrucible extends TileThaumcraft implements IFluidHandler, IAspectContainer
{
    public short heat;
    public AspectList aspects;
    public int maxTags = 500;
    int bellows;
    private int delay;
    private long counter;
    int prevcolor;
    int prevx;
    int prevy;
    public FluidTank tank;

    public static BlockEntityType<TileCrucible> TYPE;

    public TileCrucible(BlockPos pos, BlockState state) {
        super(TileCrucible.TYPE, pos, state);
        aspects = new AspectList();
        bellows = -1;
        delay = 0;
        counter = -100L;
        prevcolor = 0;
        prevx = 0;
        prevy = 0;
        tank = new FluidTank(1000);
        heat = 0;
    }

    // -------------------------------------------------------------------------
    // Disk persistence — ValueInput/ValueOutput
    // -------------------------------------------------------------------------

    @Override
    protected void saveAdditional(ValueOutput output) {
        /* super.saveAdditional removed - CompoundTag not compatible with ValueOutput */
        output.putShort("Heat", heat);
        output.putInt("TankAmount", tank.getFluidAmount());
        ValueOutput.ValueOutputList aspectsList = output.childrenList("Aspects");
        for (Aspect a : aspects.getAspects()) {
            ValueOutput child = aspectsList.addChild();
            child.putString("key", a.getTag());
            child.putInt("amount", aspects.getAmount(a));
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        /* super.loadAdditional removed - CompoundTag not compatible with ValueInput */
        heat = (short) input.getShortOr("Heat", (short)0);
        int tankAmount = input.getIntOr("TankAmount", 0);
        tank.setFluid(tankAmount > 0 ? new FluidStack(Fluids.WATER, tankAmount) : FluidStack.EMPTY);
        aspects = new AspectList();
        for (ValueInput child : input.childrenListOrEmpty("Aspects")) {
            String key = child.getStringOr("key", "");
            int amount = child.getIntOr("amount", 0);
            if (!key.isEmpty()) {
                Aspect a = Aspect.getAspect(key);
                if (a != null) aspects.add(a, amount);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Network sync — CompoundTag based
    // -------------------------------------------------------------------------

    @Override
    public void readSyncNBT(CompoundTag nbttagcompound) {
        heat = nbttagcompound.getShortOr("Heat", (short)0);
        int tankAmount = nbttagcompound.getIntOr("TankAmount", 0);
        tank.setFluid(tankAmount > 0 ? new FluidStack(Fluids.WATER, tankAmount) : FluidStack.EMPTY);
        if (nbttagcompound.contains("Empty")) {
            tank.setFluid(FluidStack.EMPTY);
        }
        aspects.loadAdditional(nbttagcompound);
    }

    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbttagcompound) {
        nbttagcompound.putShort("Heat", heat);
        nbttagcompound.putInt("TankAmount", tank.getFluidAmount());
        aspects.saveAdditional(nbttagcompound);
        return nbttagcompound;
    }

    // -------------------------------------------------------------------------
    // IFluidHandler — delegate to tank
    // -------------------------------------------------------------------------

    @Override
    public int getTanks() { return tank.getTanks(); }

    @Override
    public @Nonnull FluidStack getFluidInTank(int tankIndex) { return tank.getFluidInTank(tankIndex); }

    @Override
    public int getTankCapacity(int tankIndex) { return tank.getTankCapacity(tankIndex); }

    @Override
    public boolean isFluidValid(int tankIndex, @Nonnull FluidStack stack) { return tank.isFluidValid(tankIndex, stack); }

    @Override
    public int fill(FluidStack resource, FluidAction action) { return tank.fill(resource, action); }

    @Override
    public @Nonnull FluidStack drain(FluidStack resource, FluidAction action) { return tank.drain(resource, action); }

    @Override
    public @Nonnull FluidStack drain(int maxDrain, FluidAction action) { return tank.drain(maxDrain, action); }

    // -------------------------------------------------------------------------
    // Tick
    // -------------------------------------------------------------------------

    public void update() {
        ++counter;
        int prevheat = heat;
        if (!getLevel().isClientSide()) {
            if (tank.getFluidAmount() > 0) {
                BlockState block = getLevel().getBlockState(worldPosition.below());
                if (block.is(Blocks.LAVA) || block.is(Blocks.FIRE) ||
                        BlocksTC.nitor.containsValue(block) ||
                        block.is(Blocks.MAGMA_BLOCK)) {
                    if (heat < 200) {
                        ++heat;
                        if (prevheat < 151 && heat >= 151) {
                            setChanged();
                            syncTile(false);
                        }
                    }
                }
                else if (heat > 0) {
                    --heat;
                    if (heat == 149) {
                        setChanged();
                        syncTile(false);
                    }
                }
            }
            else if (heat > 0) {
                --heat;
            }
            if (aspects.visSize() > 500) {
                spillRandom();
            }
            if (counter >= 100L) {
                spillRandom();
                counter = 0L;
            }
        }
        else if (tank.getFluidAmount() > 0) {
            drawEffects();
        }
        if (getLevel().isClientSide() && prevheat < 151 && heat >= 151) {
            ++heat;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void drawEffects() {
        if (heat > 150) {
            FXDispatcher.INSTANCE.crucibleFroth(worldPosition.getX() + 0.2f + level.getRandom().nextFloat() * 0.6f, worldPosition.getY() + getFluidHeight(), worldPosition.getZ() + 0.2f + level.getRandom().nextFloat() * 0.6f);
            if (aspects.visSize() > 500) {
                for (int a = 0; a < 2; ++a) {
                    FXDispatcher.INSTANCE.crucibleFrothDown((float) worldPosition.getX(), (float)(worldPosition.getY() + 1), worldPosition.getZ() + level.getRandom().nextFloat());
                    FXDispatcher.INSTANCE.crucibleFrothDown((float)(worldPosition.getX() + 1), (float)(worldPosition.getY() + 1), worldPosition.getZ() + level.getRandom().nextFloat());
                    FXDispatcher.INSTANCE.crucibleFrothDown(worldPosition.getX() + level.getRandom().nextFloat(), (float)(worldPosition.getY() + 1), (float) worldPosition.getZ());
                    FXDispatcher.INSTANCE.crucibleFrothDown(worldPosition.getX() + level.getRandom().nextFloat(), (float)(worldPosition.getY() + 1), (float)(worldPosition.getZ() + 1));
                }
            }
        }
        if (level.getRandom().nextInt(6) == 0 && aspects.size() > 0) {
            int color = aspects.getAspects()[level.getRandom().nextInt(aspects.size())].getColor() - 16777216;
            int cx = 5 + level.getRandom().nextInt(22);
            int cy = 5 + level.getRandom().nextInt(22);
            delay = level.getRandom().nextInt(10);
            prevcolor = color;
            prevx = cx;
            prevy = cy;
            Color c = new Color(color);
            float r = c.getRed() / 255.0f;
            float g = c.getGreen() / 255.0f;
            float b = c.getBlue() / 255.0f;
            FXDispatcher.INSTANCE.crucibleBubble(worldPosition.getX() + cx / 32.0f + 0.015625f, worldPosition.getY() + 0.05f + getFluidHeight(), worldPosition.getZ() + cy / 32.0f + 0.015625f, r, g, b);
        }
    }

    public void ejectItem(ItemStack items) {
        boolean first = true;
        do {
            ItemStack spitout = items.copy();
            if (spitout.getCount() > spitout.getMaxStackSize()) {
                spitout.setCount(spitout.getMaxStackSize());
            }
            items.shrink(spitout.getCount());
            EntitySpecialItem entityitem = new EntitySpecialItem(level, worldPosition.getX() + 0.5f, worldPosition.getY() + 0.71f, worldPosition.getZ() + 0.5f, spitout);
            double vx = first ? 0.0 : (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.01f;
            double vy = 0.07500000298023224;
            double vz = first ? 0.0 : (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.01f;
            entityitem.setDeltaMovement(vx, vy, vz);
            getLevel().addFreshEntity(entityitem);
            first = false;
        } while (items.getCount() > 0);
    }

    public ItemStack attemptSmelt(ItemStack item, String username) {
        boolean bubble = false;
        boolean craftDone = false;
        int stacksize = item.getCount();
        Player player = null;
        if (!getLevel().isClientSide() && level instanceof net.minecraft.server.level.ServerLevel sl) {
            player = sl.getServer().getPlayerList().getPlayerByName(username);
        }
        for (int a = 0; a < stacksize; ++a) {
            CrucibleRecipe rc = null /* CraftingManager removed */;
            if (rc != null && tank.getFluidAmount() > 0) {
                ItemStack out = rc.getResultItem().copy();
                aspects = rc.removeMatching(aspects);
                tank.drain(50, IFluidHandler.FluidAction.EXECUTE);
                ejectItem(out);
                craftDone = true;
                --stacksize;
                counter = -250L;
            }
            else {
                AspectList ot = null /* CraftingManager removed */;
                if (ot != null) {
                    if (ot.size() != 0) {
                        for (Aspect tag : ot.getAspects()) {
                            aspects.add(tag, ot.getAmount(tag));
                        }
                        bubble = true;
                        --stacksize;
                        counter = -150L;
                    }
                }
            }
        }
        if (bubble) {
            getLevel().playSound(null, worldPosition, SoundsTC.bubble, SoundSource.BLOCKS, 0.2f, 1.0f + level.getRandom().nextFloat() * 0.4f);
            syncTile(false);
            getLevel().blockEvent(worldPosition, BlocksTC.crucible, 2, 1);
        }
        if (craftDone) {
            syncTile(false);
            getLevel().blockEvent(worldPosition, BlocksTC.crucible, 99, 0);
        }
        setChanged();
        if (stacksize <= 0) {
            return null;
        }
        item.setCount(stacksize);
        return item;
    }

    public void attemptSmelt(ItemEntity entity) {
        ItemStack item = entity.getItem();
        // Thrower UUID tracking removed — old CompoundTag-based approach gone
        String username = "";
        ItemStack res = attemptSmelt(item, username);
        if (res == null || res.getCount() <= 0) {
            entity.discard();
        }
        else {
            item.setCount(res.getCount());
            entity.setItem(item);
        }
    }

    public float getFluidHeight() {
        float base = 0.3f + 0.5f * (tank.getFluidAmount() / (float) tank.getCapacity());
        float out = base + aspects.visSize() / 500.0f * (1.0f - base);
        if (out > 1.0f) {
            out = 1.001f;
        }
        if (out == 1.0f) {
            out = 0.9999f;
        }
        return out;
    }

    public void spillRandom() {
        if (aspects.size() > 0) {
            Aspect tag = aspects.getAspects()[level.getRandom().nextInt(aspects.getAspects().length)];
            aspects.remove(tag, 1);
            AuraHelper.polluteAura(level, worldPosition, (tag == Aspect.FLUX) ? 1.0f : 0.25f, true);
        }
        setChanged();
        syncTile(false);
    }

    public void spillRemnants() {
        int vs = aspects.visSize();
        if (tank.getFluidAmount() > 0 || vs > 0) {
            tank.setFluid(FluidStack.EMPTY);
            AuraHelper.polluteAura(level, worldPosition, vs * 0.25f, true);
            int f = aspects.getAmount(Aspect.FLUX);
            if (f > 0) {
                AuraHelper.polluteAura(level, worldPosition, f * 0.75f, false);
            }
            aspects = new AspectList();
            getLevel().blockEvent(worldPosition, BlocksTC.crucible, 2, 5);
            setChanged();
            syncTile(false);
        }
    }

    @Override
    public boolean triggerEvent(int i, int j) {
        if (i == 99) {
            if (getLevel().isClientSide()) {
                FXDispatcher.INSTANCE.drawBamf(worldPosition.getX() + 0.5, worldPosition.getY() + 1.25f, worldPosition.getZ() + 0.5, true, true, net.minecraft.core.Direction.UP);
                ((net.minecraft.client.multiplayer.ClientLevel) level).playLocalSound(worldPosition.getX() + 0.5f, worldPosition.getY() + 0.5f, worldPosition.getZ() + 0.5f, SoundsTC.spill, SoundSource.BLOCKS, 0.2f, 1.0f, false);
            }
            return true;
        }
        if (i == 1) {
            if (getLevel().isClientSide()) {
                FXDispatcher.INSTANCE.drawBamf(worldPosition.above(), true, true, net.minecraft.core.Direction.UP);
            }
            return true;
        }
        if (i == 2) {
            ((net.minecraft.client.multiplayer.ClientLevel) level).playLocalSound(worldPosition.getX() + 0.5f, worldPosition.getY() + 0.5f, worldPosition.getZ() + 0.5f, SoundsTC.spill, SoundSource.BLOCKS, 0.2f, 1.0f, false);
            if (getLevel().isClientSide()) {
                for (int q = 0; q < 10; ++q) {
                    FXDispatcher.INSTANCE.crucibleBoil(worldPosition, this, j);
                }
            }
            return true;
        }
        return super.triggerEvent(i, j);
    }

    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), worldPosition.getX() + 1, worldPosition.getY() + 1, worldPosition.getZ() + 1);
    }

    // -------------------------------------------------------------------------
    // IAspectContainer
    // -------------------------------------------------------------------------

    @Override
    public AspectList getAspects() {
        return aspects;
    }

    @Override
    public void setAspects(AspectList aspects) {
    }

    @Override
    public int addToContainer(Aspect tag, int amount) {
        return 0;
    }

    @Override
    public boolean takeFromContainer(Aspect tag, int amount) {
        return false;
    }

    @Override
    public boolean takeFromContainer(AspectList ot) {
        return false;
    }

    @Override
    public boolean doesContainerContainAmount(Aspect tag, int amount) {
        return false;
    }

    @Override
    public boolean doesContainerContain(AspectList ot) {
        return false;
    }

    @Override
    public int containerContains(Aspect tag) {
        return 0;
    }

    @Override
    public boolean doesContainerAccept(Aspect tag) {
        return true;
    }
}
