package thaumcraft.common.blocks.world;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.world.level.block.SoundType;
// import net.minecraft.world.level.material.Material; // removed in 1.20
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.utils.Utils;


public class BlockLoot extends BlockTC
{
    LootType type;
    net.minecraft.util.RandomSource rand;
    
    public BlockLoot(Object mat /* Material removed */, String name, LootType type) {
        super(mat, name);
        rand = net.minecraft.util.RandomSource.create();
        setHardness(0.15f);
        setResistance(0.0f);
        this.type = type;
    }
    
    // Sound type is set via Properties in constructor; this getter is unused in 1.21+
    
    public boolean isOpaqueCube(BlockState state) {
        return false;
    }
    
    public boolean isFullCube(BlockState state) {
        return false;
    }
    
    protected boolean canSilkHarvest() {
        return true;
    }
    
    public boolean canHarvestBlock(BlockGetter world, BlockPos pos, Player player) {
        return true;
    }
    
    public AABB getBoundingBox(BlockState state, BlockGetter source, BlockPos pos) {
        // Urns (stone) use a smaller box; crates (wood) use larger
        if (this.type != null && this.type.ordinal() >= 0 && state.getSoundType() == net.minecraft.world.level.block.SoundType.STONE) {
            return new AABB(0.125, 0.0625, 0.125, 0.875, 0.8125, 0.875);
        }
        return new AABB(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375);
    }
    
    public List<ItemStack> getDrops(BlockGetter world, BlockPos pos, BlockState state, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        for (int q = 1 + type.ordinal() + rand.nextInt(3), a = 0; a < q; ++a) {
            ItemStack is = Utils.generateLoot(type.ordinal(), rand);
            if (is != null && !is.isEmpty()) {
                ret.add(is.copy());
            }
        }
        return ret;
    }
    
    public enum LootType
    {
        COMMON, 
        UNCOMMON, 
        RARE;
    }
}
