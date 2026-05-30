package thaumcraft.api.casters;
import java.util.ArrayList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;



public abstract class FocusModSplit extends FocusMod {
	
	private ArrayList<FocusPackage> packages = new ArrayList<>(); 

	public ArrayList<FocusPackage> getSplitPackages() {
		return packages;
	}
	
	public void deserialize(CompoundTag nbt) {
		ListTag nodelist = nbt.getListOrEmpty("packages");
		packages.clear();
		for (int x=0;x<nodelist.size();x++) {
			CompoundTag nodenbt = nodelist.getCompoundOrEmpty(x);
			FocusPackage fp = new FocusPackage();
			fp.deserialize(nodenbt);
			packages.add(fp);
		}
	}
	
	public CompoundTag serialize() {
		CompoundTag nbt = new CompoundTag();		
		ListTag nodelist = new ListTag();
		for (FocusPackage node:packages) {
			nodelist.add(node.serialize());
		}
		nbt.put("packages", nodelist);		
		return nbt;
	}
	
	@Override
	public float getPowerMultiplier() {
		return .75f;
	}

}
