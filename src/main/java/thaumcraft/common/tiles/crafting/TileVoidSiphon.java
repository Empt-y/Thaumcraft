package thaumcraft.common.tiles.crafting;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;


public class TileVoidSiphon extends TileThaumcraftInventory
{
    private static final int[] SLOTS = new int[] { 0 };
    int counter;
    public int progress;
    public int PROGREQ = 2000;

    public TileVoidSiphon(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(1);
        counter = 0;
        progress = 0;
    }

    @Override
    public void update() {
        if (level == null) return;
        super.update();
        ++counter;
        if (!level.isClientSide() && counter % 20 == 0 && progress < 2000
            && (getItem(0).isEmpty() || (getItem(0).getItem() == ItemsTC.voidSeed
                && getItem(0).getCount() < getItem(0).getMaxStackSize()))) {
            List<EntityFluxRift> frl = getValidRifts();
            boolean b = false;
            for (EntityFluxRift fr : frl) {
                double d = Math.sqrt(fr.getRiftSize());
                progress += (int)d;
                fr.setRiftStability((float)(fr.getRiftStability() - d / 15.0));
                if (level.getRandom().nextInt(33) == 0) {
                    fr.setRiftSize(fr.getRiftSize() - 1);
                }
                b = (d >= 1.0);
            }
            if (b && counter % 40 == 0) {
                level.blockEvent(worldPosition, level.getBlockState(worldPosition).getBlock(), 5, counter);
            }
            b = false;
            while (progress >= 2000
                && (getItem(0).isEmpty() || (getItem(0).getItem() == ItemsTC.voidSeed
                    && getItem(0).getCount() < getItem(0).getMaxStackSize()))) {
                progress -= 2000;
                if (getItem(0).isEmpty()) {
                    setItem(0, new ItemStack(ItemsTC.voidSeed));
                } else {
                    getItem(0).setCount(getItem(0).getCount() + 1);
                }
                b = true;
            }
            if (b) {
                syncTile(false);
                setChanged();
            }
        }
    }

    private List<EntityFluxRift> getValidRifts() {
        ArrayList<EntityFluxRift> ret = new ArrayList<>();
        if (level == null) return ret;
        AABB range = new AABB(worldPosition).inflate(8.0);
        List<EntityFluxRift> frl = level.getEntitiesOfClass(EntityFluxRift.class, range);
        for (EntityFluxRift fr : frl) {
            if (!fr.isRemoved()) {
                if (fr.getRiftSize() < 2) continue;
                double xx = worldPosition.getX() + 0.5;
                double yy = worldPosition.getY() + 1;
                double zz = worldPosition.getZ() + 0.5;
                Vec3 v1 = new Vec3(xx, yy, zz);
                Vec3 v2 = new Vec3(fr.getX(), fr.getY(), fr.getZ());
                v1 = v1.add(v2.subtract(v1).normalize());
                if (!EntityUtils.canEntityBeSeen(fr, v1.x, v1.y, v1.z)) continue;
                ret.add(fr);
            }
        }
        return ret;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        /* super.loadAdditional removed - CompoundTag not compatible with ValueInput */
        progress = input.getShortOr("progress", (short)0);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        /* super.saveAdditional removed - CompoundTag not compatible with ValueOutput */
        output.putShort("progress", (short) progress);
    }

    @Override
    public int[] getSlotsForFace(Direction side) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int par1, ItemStack stack, Direction par3) {
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int par1, ItemStack stack2, Direction par3) {
        return true;
    }

    @Override
    public boolean triggerEvent(int i, int j) {
        if (i == 5) {
            if (level != null && level.isClientSide()) {
                List<EntityFluxRift> frl = getValidRifts();
                for (EntityFluxRift fr : frl) {
                    FXDispatcher.INSTANCE.voidStreak(fr.getX(), fr.getY(), fr.getZ(),
                        worldPosition.getX() + 0.5, worldPosition.getY() + 0.5625f,
                        worldPosition.getZ() + 0.5, j, 0.04f);
                }
            }
            return true;
        }
        return super.triggerEvent(i, j);
    }
}
