package thaumcraft.api.research;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;


public class ScanBlockState implements IScanThing {
	
	String research;	
	BlockState blockState;
	
	public ScanBlockState(BlockState blockState) {
		research = "!"+blockState.toString();
		this.blockState = blockState;
	}
	
	public ScanBlockState(String research, BlockState blockState) {
		this.research = research;
		this.blockState = blockState;
	}
	
	public ScanBlockState(String research, BlockState blockState, boolean item) {
		this.research = research;
		this.blockState = blockState;
		if (item) 
			ScanningManager.addScannableThing(new ScanItem(research,
				new ItemStack(blockState.getBlock())));
	}

	@Override
	public boolean checkThing(Player player, Object obj) {		
		if (obj!=null && obj instanceof BlockPos && player.level().getBlockState((BlockPos) obj)==blockState) {
				return true;
		}
		return false;
	}
	
	@Override
	public String getResearchKey(Player player, Object object) {
		return research;
	}
}
