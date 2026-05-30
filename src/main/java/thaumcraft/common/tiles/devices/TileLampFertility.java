package thaumcraft.common.tiles.devices;
import net.minecraft.world.level.LightLayer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
// ITickable removed - use BlockEntityTicker<T>
import net.minecraft.world.phys.AABB;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.common.blocks.IBlockEnabled;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraft;


public class TileLampFertility extends TileThaumcraft implements IEssentiaTransport
{
    public int charges;
    int count;
    int drawDelay;
    
    public TileLampFertility(net.minecraft.world.level.block.entity.BlockEntityType<?> type, net.minecraft.core.BlockPos pos, net.minecraft.world.level.block.state.BlockState state) {
        super(type, pos, state);
        charges = 0;
        count = 0;
        drawDelay = 0;
    }
    
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        if (world != null && getLevel().isClientSide()) {
            this.level.checkLightFor(LightLayer.BLOCK, getBlockPos());
        }
    }
    
    public void update() {
        if (!getLevel().isClientSide()) {
            if (charges < 10) {
                if (drawEssentia()) {
                    ++charges;
                    setChanged();
                    syncTile(true);
                }
                if (charges <= 1) {
                    if (BlockStateUtils.isEnabled(getBlockState().getBlockState())) {
                        getLevel().setBlock(getBlockPos(), getLevel().getBlockState(getBlockPos()).setValue((Property)IBlockEnabled.ENABLED, (Comparable)false), 3);
                    }
                }
                else if (!gettingPower() && !BlockStateUtils.isEnabled(getBlockState().getBlockState())) {
                    getLevel().setBlock(getBlockPos(), getLevel().getBlockState(getBlockPos()).setValue((Property)IBlockEnabled.ENABLED, (Comparable)true), 3);
                }
            }
            if (!gettingPower() && charges > 1 && count++ % 300 == 0) {
                updateAnimals();
            }
        }
    }
    
    private void updateAnimals() {
        int distance = 7;
        List<net.minecraft.world.entity.animal.Animal> var5 = getLevel().getEntitiesOfClass(net.minecraft.world.entity.animal.Animal.class, new AABB(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), getBlockPos().getX() + 1, getBlockPos().getY() + 1, getBlockPos().getZ() + 1).inflate(distance, distance, distance));
    Label_0314:
        for (LivingEntity var8 : var5) {
            net.minecraft.world.entity.animal.Animal var7 = (net.minecraft.world.entity.animal.Animal)var8;
            if (var7.getGrowingAge() == 0) {
                if (var7.isInLove()) {
                    continue;
                }
                ArrayList<net.minecraft.world.entity.animal.Animal> sa = new ArrayList<net.minecraft.world.entity.animal.Animal>();
                for (LivingEntity var9 : var5) {
                    if (var9.getClass().equals(var8.getClass())) {
                        sa.add((net.minecraft.world.entity.animal.Animal)var9);
                    }
                }
                if (sa != null && sa.size() > 9) {
                    continue;
                }
                Iterator<net.minecraft.world.entity.animal.Animal> var10 = sa.iterator();
                net.minecraft.world.entity.animal.Animal partner = null;
                while (var10.hasNext()) {
                    net.minecraft.world.entity.animal.Animal var11 = var10.next();
                    if (var11.getGrowingAge() == 0) {
                        if (var11.isInLove()) {
                            continue;
                        }
                        if (partner != null) {
                            charges -= 5;
                            var11.setInLove(null);
                            partner.setInLove(null);
                            break Label_0314;
                        }
                        partner = var11;
                    }
                }
            }
        }
    }
    
    @Override
    public void readSyncNBT(CompoundTag nbttagcompound) {
        charges = nbttagcompound.getIntOr("charges", 0);
    }
    
    @Override
    public CompoundTag writeSyncNBT(CompoundTag nbttagcompound) {
        nbttagcompound.putInt("charges", charges);
        return nbttagcompound;
    }
    
    boolean drawEssentia() {
        if (++drawDelay % 5 != 0) {
            return false;
        }
        BlockEntity te = ThaumcraftApiHelper.getConnectableTile(world, getBlockPos(), getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING));
        if (te != null) {
            IEssentiaTransport ic = (IEssentiaTransport)te;
            if (!ic.canOutputTo(getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING).getOpposite())) {
                return false;
            }
            if (ic.getSuctionAmount(getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING).getOpposite()) < getSuctionAmount(getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING)) && ic.takeEssentia(Aspect.DESIRE, 1, getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING).getOpposite()) == 1) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isConnectable(Direction face) {
        return face == getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
    }
    
    @Override
    public boolean canInputFrom(Direction face) {
        return face == getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
    }
    
    @Override
    public boolean canOutputTo(Direction face) {
        return false;
    }
    
    @Override
    public void setSuction(Aspect aspect, int amount) {
    }
    
    @Override
    public int getMinimumSuction() {
        return 0;
    }
    
    @Override
    public Aspect getSuctionType(Direction face) {
        return Aspect.DESIRE;
    }
    
    @Override
    public int getSuctionAmount(Direction face) {
        return (face == getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING)) ? (128 - charges * 10) : 0;
    }
    
    @Override
    public Aspect getEssentiaType(Direction loc) {
        return null;
    }
    
    @Override
    public int getEssentiaAmount(Direction loc) {
        return 0;
    }
    
    @Override
    public int takeEssentia(Aspect aspect, int amount, Direction facing) {
        return 0;
    }
    
    @Override
    public int addEssentia(Aspect aspect, int amount, Direction facing) {
        return 0;
    }
}
