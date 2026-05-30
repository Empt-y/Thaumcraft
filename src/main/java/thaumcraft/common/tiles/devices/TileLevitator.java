package thaumcraft.common.tiles.devices;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.animal.equine.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
// ITickable removed - use BlockEntityTicker<T>
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.network.chat.Component;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.codechicken.lib.raytracer.IndexedCuboid6;
import thaumcraft.codechicken.lib.vec.Cuboid6;
import thaumcraft.codechicken.lib.vec.Vector3;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileLevitator extends TileThaumcraft 
{
    private int[] ranges;
    private int range;
    private int rangeActual;
    private int counter;
    private int vis;
    
    public TileLevitator(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        ranges = new int[] { 4, 8, 16, 32 };
        range = 1;
        rangeActual = 0;
        counter = 0;
        vis = 0;
    }
    
    public void update() {
        Direction facing = getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
        if (rangeActual > ranges[range]) {
            rangeActual = 0;
        }
        int p = counter % ranges[range];
        if (getLevel().getBlockState(getBlockPos().relative(facing, 1 + p)).canOcclude()) {
            if (1 + p < rangeActual) {
                rangeActual = 1 + p;
            }
            counter = -1;
        }
        else if (1 + p > rangeActual) {
            rangeActual = 1 + p;
        }
        ++counter;
        if (!getLevel().isClientSide() && vis < 10) {
            vis += (int)(AuraHelper.drainVis(getLevel(), getBlockPos(), 1.0f, false) * 1200.0f);
            setChanged();
            syncTile(false);
        }
        if (rangeActual > 0 && vis > 0 && BlockStateUtils.isEnabled(getBlockState())) {
            List<Entity> targets = getLevel().getEntitiesOfClass(Entity.class, new AABB(getBlockPos().getX() - ((facing.getStepX() < 0) ? rangeActual : 0), getBlockPos().getY() - ((facing.getStepY() < 0) ? rangeActual : 0), getBlockPos().getZ() - ((facing.getStepZ() < 0) ? rangeActual : 0), getBlockPos().getX() + 1 + ((facing.getStepX() > 0) ? rangeActual : 0), getBlockPos().getY() + 1 + ((facing.getStepY() > 0) ? rangeActual : 0), getBlockPos().getZ() + 1 + ((facing.getStepZ() > 0) ? rangeActual : 0)));
            boolean lifted = false;
            if (targets.size() > 0) {
                for (Entity e : targets) {
                    if (!(e instanceof ItemEntity) && !e.canBePushed() && !(e instanceof EntityHorse)) {
                        continue;
                    }
                    lifted = true;
                    drawFXAt(e);
                    drawFX(facing, 0.6);
                    if (e.isCrouching() && facing == Direction.UP) {
                        if (e.getDeltaMovement().y < 0.0) {
                            Entity entity = e;
                            e.setDeltaMovement(e.getDeltaMovement().x, e.getDeltaMovement().y * 0.9, e.getDeltaMovement().z);
                        }
                    }
                    else {
                        Entity entity2 = e;
                        entity2.setDeltaMovement(entity2.getDeltaMovement().x + 0.1f * facing.getStepX(), entity2.getDeltaMovement().y, entity2.getDeltaMovement().z);
                        Entity entity3 = e;
                        entity3.setDeltaMovement(entity3.getDeltaMovement().x, entity3.getDeltaMovement().y + 0.1f * facing.getStepY(), entity3.getDeltaMovement().z);
                        Entity entity4 = e;
                        entity4.setDeltaMovement(entity4.getDeltaMovement().x, entity4.getDeltaMovement().y, entity4.getDeltaMovement().z + 0.1f * facing.getStepZ());
                        if (facing.getAxis() != Direction.Axis.Y && !e.onGround()) {
                            if (e.getDeltaMovement().y < 0.0) {
                                Entity entity5 = e;
                                entity5.setDeltaMovement(entity5.getDeltaMovement().x, entity5.getDeltaMovement().y * 0.9, entity5.getDeltaMovement().z);
                            }
                            Entity entity6 = e;
                            entity6.setDeltaMovement(entity6.getDeltaMovement().x, entity6.getDeltaMovement().y + 0.07999999821186066, entity6.getDeltaMovement().z);
                        }
                        if (e.getDeltaMovement().x > 0.3499999940395355) {
                            e.setDeltaMovement(0.3499999940395355, e.getDeltaMovement().y, e.getDeltaMovement().z);
                        }
                        if (e.getDeltaMovement().y > 0.3499999940395355) {
                            e.setDeltaMovement(e.getDeltaMovement().x, 0.3499999940395355, e.getDeltaMovement().z);
                        }
                        if (e.getDeltaMovement().z > 0.3499999940395355) {
                            e.setDeltaMovement(e.getDeltaMovement().x, e.getDeltaMovement().y, 0.3499999940395355);
                        }
                        if (e.getDeltaMovement().x < -0.3499999940395355) {
                            e.setDeltaMovement(-0.3499999940395355, e.getDeltaMovement().y, e.getDeltaMovement().z);
                        }
                        if (e.getDeltaMovement().y < -0.3499999940395355) {
                            e.setDeltaMovement(e.getDeltaMovement().x, -0.3499999940395355, e.getDeltaMovement().z);
                        }
                        if (e.getDeltaMovement().z < -0.3499999940395355) {
                            e.setDeltaMovement(e.getDeltaMovement().x, e.getDeltaMovement().y, -0.3499999940395355);
                        }
                    }
                    e.fallDistance = 0.0f;
                    vis -= getCost();
                    if (vis <= 0) {
                        break;
                    }
                }
            }
            drawFX(facing, 0.1);
            if (lifted && !getLevel().isClientSide() && counter % 20 == 0) {
                setChanged();
            }
        }
    }
    
    private void drawFX(Direction facing, double c) {
        if (getLevel().isClientSide() && net.minecraft.util.RandomSource.create().nextFloat() < c) {
            float x = getBlockPos().getX() + 0.25f + net.minecraft.util.RandomSource.create().nextFloat() * 0.5f;
            float y = getBlockPos().getY() + 0.25f + net.minecraft.util.RandomSource.create().nextFloat() * 0.5f;
            float z = getBlockPos().getZ() + 0.25f + net.minecraft.util.RandomSource.create().nextFloat() * 0.5f;
            FXDispatcher.INSTANCE.drawLevitatorParticles(x, y, z, facing.getStepX() / 50.0, facing.getStepY() / 50.0, facing.getStepZ() / 50.0);
        }
    }
    
    private void drawFXAt(Entity e) {
        if (getLevel().isClientSide() && net.minecraft.util.RandomSource.create().nextFloat() < 0.1f) {
            float x = (float)(e.getX() + (net.minecraft.util.RandomSource.create().nextFloat() - net.minecraft.util.RandomSource.create().nextFloat()) * e.getBbWidth());
            float y = (float)(e.getY() + net.minecraft.util.RandomSource.create().nextFloat() * e.getBbHeight());
            float z = (float)(e.getZ() + (net.minecraft.util.RandomSource.create().nextFloat() - net.minecraft.util.RandomSource.create().nextFloat()) * e.getBbWidth());
            FXDispatcher.INSTANCE.drawLevitatorParticles(x, y, z, (net.minecraft.util.RandomSource.create().nextFloat() - net.minecraft.util.RandomSource.create().nextFloat()) * 0.01, (net.minecraft.util.RandomSource.create().nextFloat() - net.minecraft.util.RandomSource.create().nextFloat()) * 0.01, (net.minecraft.util.RandomSource.create().nextFloat() - net.minecraft.util.RandomSource.create().nextFloat()) * 0.01);
        }
    }
    
    @Override
    public void readSyncNBT(CompoundTag nbt) {
        range = nbt.getByteOr("range", (byte)0);
        vis = nbt.getIntOr("vis", 0);
    }
    
    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbt) {
        nbt.putByte("range", (byte) range);
        nbt.putInt("vis", vis);
        return nbt;
    }
    
    public int getCost() {
        return ranges[range] * 2;
    }
    
    public void increaseRange(Player playerIn) {
        rangeActual = 0;
        if (!getLevel().isClientSide()) {
            ++range;
            if (range >= ranges.length) {
                range = 0;
            }
            setChanged();
            syncTile(false);
            playerIn.sendSystemMessage(net.minecraft.network.chat.Component.literal(String.format(I18n.get("tc.levitator"), ranges[range], getCost())));
        }
    }
    
    public HitResult rayTrace(Level level, Vec3 vec3d, Vec3 vec3d1, HitResult fullblock) {
        return fullblock;
    }
    
    public void addTraceableCuboids(List<IndexedCuboid6> cuboids) {
        Direction facing = getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
        cuboids.add(new IndexedCuboid6(0, getCuboidByFacing(facing).add(new Vector3(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ()))));
    }
    
    public Cuboid6 getCuboidByFacing(Direction facing) {
        switch (facing) {
            default: {
                return new Cuboid6(0.375, 0.0625, 0.375, 0.625, 0.125, 0.625);
            }
            case DOWN: {
                return new Cuboid6(0.375, 0.875, 0.375, 0.625, 0.9375, 0.625);
            }
            case EAST: {
                return new Cuboid6(0.0625, 0.375, 0.375, 0.125, 0.625, 0.625);
            }
            case WEST: {
                return new Cuboid6(0.875, 0.375, 0.375, 0.9375, 0.625, 0.625);
            }
            case SOUTH: {
                return new Cuboid6(0.375, 0.375, 0.0625, 0.625, 0.625, 0.125);
            }
            case NORTH: {
                return new Cuboid6(0.375, 0.375, 0.875, 0.625, 0.625, 0.9375);
            }
        }
    }
}
