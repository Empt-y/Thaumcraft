package thaumcraft.api;
import net.minecraft.world.Container;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaTransport;
import thaumcraft.api.crafting.IngredientNBTTC;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.api.items.ItemsTC;
import io.netty.buffer.ByteBuf;



public class ThaumcraftApiHelper {

	public static Attribute CHAMPION_MOD = new RangedAttribute("tc.mobmod", -2D, -2D, 100D);

	/** @deprecated Use {@link ThaumcraftInvHelper#areItemsEqual} instead */
	public static boolean areItemsEqual(ItemStack s1, ItemStack s2) {
		return ThaumcraftInvHelper.areItemsEqual(s1, s2);
	}

	/** @deprecated Use {@link ThaumcraftInvHelper#containsMatch} instead */
	public static boolean containsMatch(boolean strict, ItemStack[] inputs, List<ItemStack> targets) {
		return ThaumcraftInvHelper.containsMatch(strict, inputs, targets);
	}

	/** @deprecated Use {@link ThaumcraftInvHelper#areItemStacksEqualForCrafting} instead */
	public static boolean areItemStacksEqualForCrafting(ItemStack stack0, Object in) {
		return ThaumcraftInvHelper.areItemStacksEqualForCrafting(stack0, in);
	}

	/** @deprecated Use {@link ThaumcraftInvHelper#areItemStackTagsEqualForCrafting} instead */
	public static boolean areItemStackTagsEqualForCrafting(ItemStack slotItem, ItemStack recipeItem) {
		return ThaumcraftInvHelper.areItemStackTagsEqualForCrafting(slotItem, recipeItem);
	}

    public static BlockEntity getConnectableTile(Level world, BlockPos pos, Direction face) {
		BlockEntity te = world.getBlockEntity(pos.relative(face));
		if (te instanceof IEssentiaTransport && ((IEssentiaTransport)te).isConnectable(face.getOpposite()))
			return te;
		else
			return null;
	}

    public static BlockEntity getConnectableTile(BlockGetter world, BlockPos pos, Direction face) {
		BlockEntity te = world.getBlockEntity(pos.relative(face));
		if (te instanceof IEssentiaTransport && ((IEssentiaTransport)te).isConnectable(face.getOpposite()))
			return te;
		else
			return null;
	}

	public static HitResult rayTraceIgnoringSource(Level world, Vec3 v1, Vec3 v2,
			boolean bool1, boolean bool2, boolean bool3) {
		net.minecraft.world.level.ClipContext.Fluid fluid = bool1
			? net.minecraft.world.level.ClipContext.Fluid.ANY
			: net.minecraft.world.level.ClipContext.Fluid.NONE;
		return world.clip(new net.minecraft.world.level.ClipContext(
			v1, v2,
			net.minecraft.world.level.ClipContext.Block.COLLIDER,
			fluid,
			net.minecraft.world.phys.shapes.CollisionContext.empty()));
	}

	public static Object getNBTDataFromId(CompoundTag nbt, byte id, String key) {
		switch (id) {
		case 1: return nbt.getByteOr(key, (byte)0);
		case 2: return nbt.getShortOr(key, (short)0);
		case 3: return nbt.getIntOr(key, 0);
		case 4: return nbt.getLongOr(key, 0L);
		case 5: return nbt.getFloatOr(key, 0.0f);
		case 6: return nbt.getDoubleOr(key, 0.0);
		case 7: return nbt.getByteArray(key).orElse(new byte[0]);
		case 8: return nbt.getStringOr(key, "");
		case 9: return nbt.getListOrEmpty(key);
		case 10: return nbt.get(key);
		case 11: return nbt.getIntArray(key).orElse(new int[0]);
		default: return null;
		}
	}

	public static int setByteInInt(int data, byte b, int index) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(0, data);
		bb.put(index, b);
	    return bb.getInt(0);
	}

	public static byte getByteInInt(int data, int index) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(0, data);
		return bb.get(index);
	}

	public static long setByteInLong(long data, byte b, int index) {
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.putLong(0, data);
		bb.put(index, b);
	    return bb.getLong(0);
	}

	public static byte getByteInLong(long data, int index) {
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.putLong(0, data);
		return bb.get(index);
	}

	public static int setNibbleInInt(int data, int nibble, int nibbleIndex) {
	    int shift = nibbleIndex * 4;
	    return (data & ~(0xf << shift)) | (nibble << shift);
	}

	public static int getNibbleInInt(int data, int nibbleIndex) {
		return (data >> (nibbleIndex << 2)) & 0xF;
	}

	public static ItemStack makeCrystal(Aspect aspect, int stackSize) {
		if (aspect == null) return null;
		ItemStack is = new ItemStack(ItemsTC.crystalEssence, stackSize);
		((ItemGenericEssentiaContainer)ItemsTC.crystalEssence).setAspects(is, new AspectList().add(aspect, 1));
		return is;
	}

	public static ItemStack makeCrystal(Aspect aspect) {
		return makeCrystal(aspect, 1);
	}

	// TODO: replace OreDictionary with tag-based lookup
	public static List<ItemStack> getOresWithWildCards(String oreDict) {
		return new ArrayList<>();
	}

	public static Ingredient getIngredient(Object obj) {
		if (obj instanceof Ingredient) return (Ingredient) obj;
        if (obj instanceof ItemStack is && is.has(net.minecraft.core.component.DataComponents.CUSTOM_DATA))
            return new IngredientNBTTC(is).toVanilla();
        if (obj instanceof ItemStack is)
            return Ingredient.of(is.getItem());
        return null;
    }

	/** @deprecated Use {@link ThaumcraftInvHelper#getItemHandlerAt} instead */
	public static IItemHandler getItemHandlerAt(Level world, BlockPos pos, Direction side) {
		return ThaumcraftInvHelper.getItemHandlerAt(world, pos, side);
	}

	/** @deprecated Use {@link ThaumcraftInvHelper#wrapInventory} instead */
	public static IItemHandler wrapInventory(Container inventory, Direction side) {
		return ThaumcraftInvHelper.wrapInventory(inventory, side);
	}

	/** @deprecated Use {@link ThaumcraftInvHelper#areItemStackTagsEqualRelaxed} instead */
	public static boolean areItemStackTagsEqualRelaxed(ItemStack prime, ItemStack other) {
		return ThaumcraftInvHelper.areItemStackTagsEqualRelaxed(prime, other);
	}

	/** @deprecated Use {@link ThaumcraftInvHelper#compareTagsRelaxed} instead */
	public static boolean compareTagsRelaxed(CompoundTag prime, CompoundTag other) {
		return ThaumcraftInvHelper.compareTagsRelaxed(prime, other);
	}

}
