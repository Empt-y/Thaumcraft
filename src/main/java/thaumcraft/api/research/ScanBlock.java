package thaumcraft.api.research;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;


public class ScanBlock implements IScanThing {
	
	String research;	
	Block[] blocks;
	
	public ScanBlock(Block block) {
		this("!"+BuiltInRegistries.BLOCK.getKey(block).toString(), block);
	}

	public ScanBlock(String research, Block ... blocks) {
		this.research = research;
		this.blocks = blocks;
		for (Block block:blocks)
			ScanningManager.addScannableThing(new ScanItem(research, new ItemStack(block)));
	}		
	
	@Override
	public boolean checkThing(Player player, Object obj) {		
		if (obj!=null && obj instanceof BlockPos) {
			for (Block block:blocks) 
				if (player.level().getBlockState((BlockPos) obj).getBlock()==block) 
					return true;
		}
		return false;
	}
	
	@Override
	public String getResearchKey(Player player, Object object) {		
		return research;
	}
}
