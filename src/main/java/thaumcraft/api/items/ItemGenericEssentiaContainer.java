package thaumcraft.api.items;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;


public class ItemGenericEssentiaContainer extends Item implements IEssentiaContainerItem
{

	public ItemGenericEssentiaContainer(int base)
    {
        super(new Item.Properties().stacksTo(64));
        this.base = base;
    }

	protected int base = 1;

	private static CompoundTag getTag(ItemStack itemstack) {
		CustomData cd = itemstack.get(DataComponents.CUSTOM_DATA);
		return cd != null ? cd.copyTag() : null;
	}

	private static boolean hasTag(ItemStack itemstack) {
		return itemstack.has(DataComponents.CUSTOM_DATA);
	}

	private static void setTag(ItemStack itemstack, CompoundTag nbt) {
		itemstack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
	}

    @Override
	public AspectList getAspects(ItemStack itemstack) {
		if (hasTag(itemstack)) {
			CompoundTag tag = getTag(itemstack);
			if (tag != null) {
				AspectList aspects = new AspectList();
				aspects.loadAdditional(tag);
				return aspects.size()>0?aspects:null;
			}
		}
		return null;
	}

	@Override
	public void setAspects(ItemStack itemstack, AspectList aspects) {
		CompoundTag nbt = hasTag(itemstack) ? getTag(itemstack) : new CompoundTag();
		if (nbt == null) nbt = new CompoundTag();
		aspects.saveAdditional(nbt);
		setTag(itemstack, nbt);
	}

	@Override
	public boolean ignoreContainedAspects() {return false;}

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, EquipmentSlot slot) {
		if (!hasTag(stack)) {
			Aspect[] displayAspects = Aspect.aspects.values().toArray(new Aspect[]{});
			setAspects(stack, new AspectList().add(displayAspects[world.getRandom().nextInt(displayAspects.length)], base));
		}
		super.inventoryTick(stack, world, entity, slot);
	}

	@Override
	public void onCraftedBy(ItemStack stack, Player player) {
		if (!hasTag(stack)) {
			Aspect[] displayAspects = Aspect.aspects.values().toArray(new Aspect[]{});
			setAspects(stack, new AspectList().add(displayAspects[player.getRandom().nextInt(displayAspects.length)], base));
		}
	}
}
