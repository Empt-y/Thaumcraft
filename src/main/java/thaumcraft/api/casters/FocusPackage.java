package thaumcraft.api.casters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.level.Level;
// DimensionManager removed - world reference must be passed at runtime



public class FocusPackage implements IFocusElement {
	
	@Override
	public String getResearch() {
		return null;
	}
	
	public Level world;
	private LivingEntity caster;	
	private UUID casterUUID;
	
	private float power = 1;
	private int complexity = 0;
	
	int index;
	UUID uid;
	
	public List<IFocusElement> nodes =  Collections.synchronizedList(new ArrayList<>());	
	
	public FocusPackage() {	}

	public FocusPackage(LivingEntity caster) {
		super();
		this.world = caster.level();
		this.caster = caster;
		casterUUID = caster.getUUID();
	}	
		
	@Override
	public String getKey() {
		return "thaumcraft.PACKAGE";
	}

	@Override
	public EnumUnitType getType() {
		return EnumUnitType.PACKAGE;
	}
	
	public int getComplexity() {
		return complexity;
	}

	public void setComplexity(int complexity) {
		this.complexity = complexity;
	}

	public UUID getUniqueID() {
		return uid;
	}

	public void setUniqueID(UUID id) {
		uid = id;
	}
	
	public int getExecutionIndex() {
		return index;
	}

	public void setExecutionIndex(int idx) {
		index = idx;
	}
	
	public void addNode(IFocusElement e) {
		nodes.add(e);
	}
	
	public UUID getCasterUUID() {
		if (caster!=null) casterUUID = caster.getUUID();
		return casterUUID;
	}

	public void setCasterUUID(UUID casterUUID) {
		this.casterUUID = casterUUID;
	}	
	
	public LivingEntity getCaster() {
		try {
			if (caster==null) {
				caster = world.getPlayerByUUID(getCasterUUID());
			}
			if (caster==null) {
				Entity found = world.getEntity(getCasterUUID());
				if (found instanceof LivingEntity le) caster = le;
			}
		} catch (Exception e) {}
		return caster;
	}
	
	public FocusEffect[] getFocusEffects() {		
		return getFocusEffectsPackage(this);
	}
	
	private FocusEffect[] getFocusEffectsPackage(FocusPackage fp) {
		ArrayList<FocusEffect> out = new ArrayList<>();
		for (IFocusElement el:fp.nodes) {
			if (el instanceof FocusEffect) out.add((FocusEffect) el);
			else
			if (el instanceof FocusPackage) {
				for (FocusEffect fep:getFocusEffectsPackage((FocusPackage) el))
					out.add(fep);
			} else 
			if (el instanceof FocusModSplit) {
				for (FocusPackage fsp:((FocusModSplit)el).getSplitPackages())
					for (FocusEffect fep:getFocusEffectsPackage(fsp))
						out.add(fep);
			}
		}
		return out.toArray(new FocusEffect[]{});
	}

	public void deserialize(CompoundTag nbt) {
		uid = nbt.contains("uid") ? net.minecraft.core.UUIDUtil.uuidFromIntArray(nbt.getIntArray("uid").orElse(new int[4])) : null;
		index = nbt.getIntOr("index", 0);
		int dim = nbt.getIntOr("dim", 0);
		world = null; // set by initialize(caster) before use; dim hash stored but not resolved here
		setCasterUUID(nbt.contains("casterUUID") ? net.minecraft.core.UUIDUtil.uuidFromIntArray(nbt.getIntArray("casterUUID").orElse(new int[4])) : null);
		power = nbt.getFloatOr("power", 0.0f);
		complexity = nbt.getIntOr("complexity", 0);
				
		ListTag nodelist = nbt.getListOrEmpty("nodes");
		nodes.clear();
		for (int x=0;x<nodelist.size();x++) {
			CompoundTag nodenbt = nodelist.getCompoundOrEmpty(x);
			EnumUnitType ut = EnumUnitType.valueOf(nodenbt.getStringOr("type", ""));
			if (ut!=null) {
				if (ut==EnumUnitType.PACKAGE) {
					FocusPackage fp = new FocusPackage();
					fp.deserialize(nodenbt.read("package", net.minecraft.nbt.CompoundTag.CODEC).orElse(new net.minecraft.nbt.CompoundTag()));
					nodes.add(fp);
					break;
				} else {
					IFocusElement fn = FocusEngine.getElement(nodenbt.getStringOr("key", "")); 
					if (fn!=null) {						
						if (fn instanceof FocusNode) {
							((FocusNode)fn).initialize();
							if (((FocusNode)fn).getSettingList()!=null)
								for (String ns : ((FocusNode)fn).getSettingList()) {
									((FocusNode)fn).getSetting(ns).setValue(nodenbt.getIntOr("setting."+ns, 0));
								}
							
							if (fn instanceof FocusModSplit) {								
								((FocusModSplit)fn).deserialize(nodenbt.read("packages", net.minecraft.nbt.CompoundTag.CODEC).orElse(new net.minecraft.nbt.CompoundTag()));		
							}
						}
						addNode(fn);
					}
				}
			}
		}
		
	}

	public CompoundTag serialize() {
		CompoundTag nbt = new CompoundTag();
		if (uid!=null) nbt.putIntArray("uid", net.minecraft.core.UUIDUtil.uuidToIntArray(uid));
		nbt.putInt("index", index);
		if (getCasterUUID() != null) nbt.putIntArray("casterUUID", net.minecraft.core.UUIDUtil.uuidToIntArray(getCasterUUID()));
		if (world!=null) nbt.putInt("dim", (world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).dimension().identifier().hashCode() : 0));
		nbt.putFloat("power", power);
		nbt.putInt("complexity", complexity);
		
		//nodes
		ListTag nodelist = new ListTag();
		synchronized (nodes) {
			for (IFocusElement node:nodes) {
				if (node==null || node.getType() ==null) continue;
				CompoundTag nodenbt = new CompoundTag();
				nodenbt.putString("type", node.getType().name());
				nodenbt.putString("key", node.getKey());
				if (node.getType() ==EnumUnitType.PACKAGE) {
					nodenbt.store("package", net.minecraft.nbt.CompoundTag.CODEC, ((FocusPackage)node).serialize());
					nodelist.add(nodenbt);
					break;
				} else {				
					if (node instanceof FocusNode && ((FocusNode)node).getSettingList()!=null)
						for (String ns : ((FocusNode)node).getSettingList()) {
							nodenbt.putInt("setting."+ns, ((FocusNode)node).getSettingValue(ns));
						}
					if (node instanceof FocusModSplit) {	
						nodenbt.store("packages", net.minecraft.nbt.CompoundTag.CODEC, ((FocusModSplit)node).serialize());	
					}
					nodelist.add(nodenbt);
				}			
			}
		}
		nbt.put("nodes", nodelist);					
		
		return nbt;
	}

	public float getPower() {
		return power;
	}

	public void multiplyPower(float pow) {
		power *= pow;
	}

	public FocusPackage copy(LivingEntity caster) {
		FocusPackage fp = new FocusPackage(caster);
		fp.deserialize(serialize());
		return fp;
	}
	
	public void initialize(LivingEntity caster) {
		world=caster.level();
		IFocusElement node = nodes.get(0);
		if (node instanceof FocusMediumRoot && ((FocusMediumRoot)node).supplyTargets()==null) {
			((FocusMediumRoot)node).setupFromCaster(caster);
		}
	}

	public int getSortingHelper() {
		String s="";
		for (IFocusElement k: nodes) {
			s+=k.getKey();
			if (k instanceof FocusNode && ((FocusNode)k).getSettingList()!=null)
				for (String ns : ((FocusNode)k).getSettingList()) {
					s += ""+((FocusNode)k).getSettingValue(ns);
				}
		}		
		return s.hashCode();
	}

	
	
	

	
	
	
	
	

}
