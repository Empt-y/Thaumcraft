package thaumcraft.common.lib.utils;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Containers;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Tuple;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
// removed: import net.minecraftforge.common.crafting.IngredientNBT;
import net.neoforged.neoforge.items.IItemHandler;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.crafting.IngredientNBTTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.EntityFollowingItem;


public class InventoryUtils
{
    public static ItemStack copyMaxedStack(ItemStack stack) {
        return copyLimitedStack(stack, stack.getMaxStackSize());
    }
    
    public static ItemStack copyLimitedStack(ItemStack stack, int limit) {
        if (stack == null) {
            return ItemStack.EMPTY;
        }
        ItemStack s = stack.copy();
        if (s.getCount() > limit) {
            s.setCount(limit);
        }
        return s;
    }
    
    public static boolean consumeItemsFromAdjacentInventoryOrPlayer(Level world, BlockPos pos, Player player, boolean sim, ItemStack... items) {
        for (ItemStack stack : items) {
            boolean b = checkAdjacentChests(world, pos, stack);
            if (!b) {
                b = isPlayerCarryingAmount(player, stack, true);
            }
            if (!b) {
                return false;
            }
        }
        if (!sim) {
            for (ItemStack stack : items) {
                if (!consumeFromAdjacentChests(world, pos, stack.copy())) {
                    consumePlayerItem(player, stack, true, true);
                }
            }
        }
        return true;
    }
    
    public static boolean checkAdjacentChests(Level world, BlockPos pos, ItemStack itemStack) {
        int c = itemStack.getCount();
        for (Direction face : Direction.values()) {
            if (face != Direction.UP) {
                c -= ThaumcraftInvHelper.countTotalItemsIn(world, pos.relative(face), face.getOpposite(), itemStack.copy(), ThaumcraftInvHelper.InvFilter.BASEORE);
                if (c <= 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean consumeFromAdjacentChests(Level world, BlockPos pos, ItemStack itemStack) {
        for (Direction face : Direction.values()) {
            if (face != Direction.UP) {
                if (!itemStack.isEmpty()) {
                    ItemStack os = removeStackFrom(world, pos.relative(face), face.getOpposite(), itemStack, ThaumcraftInvHelper.InvFilter.BASEORE, false);
                    itemStack.setCount(itemStack.getCount() - os.getCount());
                    if (itemStack.isEmpty()) {
                        break;
                    }
                }
            }
        }
        return itemStack.isEmpty();
    }
    
    @Deprecated
    public static ItemStack insertStackAt(Level world, BlockPos pos, Direction side, ItemStack stack, boolean simulate) {
        return ThaumcraftInvHelper.insertStackAt(world, pos, side, stack, simulate);
    }
    
    public static void ejectStackAt(Level world, BlockPos pos, Direction side, ItemStack out) {
        ejectStackAt(world, pos, side, out, false);
    }
    
    public static ItemStack ejectStackAt(Level world, BlockPos pos, Direction side, ItemStack out, boolean smart) {
        out = ThaumcraftInvHelper.insertStackAt(world, pos.relative(side), side.getOpposite(), out, false);
        if (smart && ThaumcraftInvHelper.getItemHandlerAt(world, pos.relative(side), side.getOpposite()) != null) {
            return out;
        }
        if (!out.isEmpty()) {
            if (world.getBlockState(pos.relative(side)).canOcclude()) {
                pos = pos.relative(side.getOpposite());
            }
            ItemEntity entityitem2 = new ItemEntity(world, (float)pos.getX() + 0.5 + 1 * side.getStepX(), pos.getY() + (float)(1 * side.getStepY()), (float)pos.getZ() + 0.5 + 1 * side.getStepZ(), out);
            entityitem2.setDeltaMovement(0.3 * side.getStepX(), entityitem2.getDeltaMovement().y, entityitem2.getDeltaMovement().z);
            entityitem2.setDeltaMovement(entityitem2.getDeltaMovement().x, 0.3 * side.getStepY(), entityitem2.getDeltaMovement().z);
            entityitem2.setDeltaMovement(entityitem2.getDeltaMovement().x, entityitem2.getDeltaMovement().y, 0.3 * side.getStepZ());
            world.addFreshEntity(entityitem2);
        }
        return ItemStack.EMPTY;
    }
    
    public static ItemStack removeStackFrom(Level world, BlockPos pos, Direction side, ItemStack stack, ThaumcraftInvHelper.InvFilter filter, boolean simulate) {
        return removeStackFrom(ThaumcraftInvHelper.getItemHandlerAt(world, pos, side), stack, filter, simulate);
    }
    
    public static ItemStack removeStackFrom(IItemHandler inventory, ItemStack stack, ThaumcraftInvHelper.InvFilter filter, boolean simulate) {
        int amount = stack.getCount();
        int removed = 0;
        if (inventory != null) {
            for (int a = 0; a < inventory.getSlots(); ++a) {
                if (areItemStacksEqual(stack, inventory.getStackInSlot(a), filter)) {
                    int s = Math.min(amount - removed, inventory.getStackInSlot(a).getCount());
                    ItemStack es = inventory.extractItem(a, s, simulate);
                    if (es != null && !es.isEmpty()) {
                        removed += es.getCount();
                    }
                }
                if (removed >= amount) {
                    break;
                }
            }
        }
        if (removed == 0) {
            return ItemStack.EMPTY;
        }
        ItemStack s2 = stack.copy();
        s2.setCount(removed);
        return s2;
    }
    
    public static int countStackInWorld(Level world, BlockPos pos, ItemStack stack, double range, ThaumcraftInvHelper.InvFilter filter) {
        int count = 0;
        List<ItemEntity> l = EntityUtils.getEntitiesInRange(world, pos, null, ItemEntity.class, range);
        for (ItemEntity ei : l) {
            if (!ei.getItem().isEmpty() && areItemStacksEqual(stack, ei.getItem(), filter)) {
                count += ei.getItem().getCount();
            }
        }
        return count;
    }
    
    public static void dropItems(Level world, BlockPos pos) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (!(tileEntity instanceof Container)) {
            return;
        }
        Container inventory = (Container)tileEntity;
        Containers.dropContents(world, pos, inventory);
    }
    
    public static boolean consumePlayerItem(Player player, ItemStack item, boolean nocheck, boolean ore) {
        if (!nocheck && !isPlayerCarryingAmount(player, item, ore)) {
            return false;
        }
        int count = item.getCount();
        for (int var2 = 0; var2 < player.getInventory().getContainerSize(); ++var2) {
            if (checkEnchantedPlaceholder(item, player.getInventory().getItem(var2)) || areItemStacksEqual(player.getInventory().getItem(var2), item, new ThaumcraftInvHelper.InvFilter(false, item.isEmpty(), ore, false).setRelaxedNBT())) {
                if (player.getInventory().getItem(var2).getCount() > count) {
                    player.getInventory().getItem(var2).shrink(count);
                    count = 0;
                }
                else {
                    count -= player.getInventory().getItem(var2).getCount();
                    player.getInventory().setItem(var2, ItemStack.EMPTY);
                }
                if (count <= 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean consumePlayerItem(Player player, Item item, int md, int amt) {
        if (!isPlayerCarryingAmount(player, new ItemStack(item, amt), false)) {
            return false;
        }
        int count = amt;
        for (int var2 = 0; var2 < player.getInventory().getContainerSize(); ++var2) {
            if (player.getInventory().getItem(var2).getItem() == item && player.getInventory().getItem(var2).getDamageValue() == md) {
                if (player.getInventory().getItem(var2).getCount() > count) {
                    player.getInventory().getItem(var2).shrink(count);
                    count = 0;
                }
                else {
                    count -= player.getInventory().getItem(var2).getCount();
                    player.getInventory().setItem(var2, ItemStack.EMPTY);
                }
                if (count <= 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean consumePlayerItem(Player player, Item item, int md) {
        for (int var2 = 0; var2 < player.getInventory().getContainerSize(); ++var2) {
            if (player.getInventory().getItem(var2).getItem() == item && player.getInventory().getItem(var2).getDamageValue() == md) {
                player.getInventory().getItem(var2).shrink(1);
                if (player.getInventory().getItem(var2).getCount() <= 0) {
                    player.getInventory().setItem(var2, ItemStack.EMPTY);
                }
                return true;
            }
        }
        return false;
    }
    
    public static boolean isPlayerCarryingAmount(Player player, ItemStack stack, boolean ore) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        int count = stack.getCount();
        for (int var2 = 0; var2 < player.getInventory().getContainerSize(); ++var2) {
            if (checkEnchantedPlaceholder(stack, player.getInventory().getItem(var2)) || areItemStacksEqual(player.getInventory().getItem(var2), stack, new ThaumcraftInvHelper.InvFilter(false, stack.isEmpty(), ore, false).setRelaxedNBT())) {
                count -= player.getInventory().getItem(var2).getCount();
                if (count <= 0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static boolean checkEnchantedPlaceholder(ItemStack stack, ItemStack stack2) {
        if (stack.getItem() != ItemsTC.enchantedPlaceholder) {
            return false;
        }
        net.minecraft.world.item.enchantment.ItemEnchantments en = EnchantmentHelper.getEnchantmentsForCrafting(stack);
        boolean b = !en.isEmpty();
        for (net.minecraft.core.Holder<Enchantment> e : en.keySet()) {
            net.minecraft.world.item.enchantment.ItemEnchantments en2 = EnchantmentHelper.getEnchantmentsForCrafting(stack2);
            if (en2.isEmpty()) {
                return false;
            }
            b = false;
            for (net.minecraft.core.Holder<Enchantment> e2 : en2.keySet()) {
                if (!e2.equals(e)) {
                    continue;
                }
                b = true;
                if (en2.getLevel(e2) < en.getLevel(e)) {
                    b = false;
                    break;
                }
            }
        }
        return b;
    }
    
    public static EquipmentSlot isHoldingItem(Player player, Item item) {
        if (player == null || item == null) {
            return null;
        }
        if (player.getMainHandItem() != null && player.getMainHandItem().getItem() == item) {
            return EquipmentSlot.MAINHAND;
        }
        if (player.getOffhandItem() != null && player.getOffhandItem().getItem() == item) {
            return EquipmentSlot.OFFHAND;
        }
        return null;
    }
    
    public static EquipmentSlot isHoldingItem(Player player, Class item) {
        if (player == null || item == null) {
            return null;
        }
        if (player.getMainHandItem() != null && item.isAssignableFrom(player.getMainHandItem().getItem().getClass())) {
            return EquipmentSlot.MAINHAND;
        }
        if (player.getOffhandItem() != null && item.isAssignableFrom(player.getOffhandItem().getItem().getClass())) {
            return EquipmentSlot.OFFHAND;
        }
        return null;
    }
    
    public static int getPlayerSlotFor(Player player, ItemStack stack) {
        for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
            if (!player.getInventory().getItem(i).isEmpty() && stackEqualExact(stack, player.getInventory().getItem(i))) {
                return i;
            }
        }
        return -1;
    }
    
    public static boolean stackEqualExact(ItemStack stack1, ItemStack stack2) {
        return stack1.getItem() == stack2.getItem() && ItemStack.isSameItemSameComponents(stack1, stack2);
    }
    
    public static boolean areItemStacksEqualStrict(ItemStack stack0, ItemStack stack1) {
        return areItemStacksEqual(stack0, stack1, ThaumcraftInvHelper.InvFilter.STRICT);
    }
    
    public static ItemStack findFirstMatchFromFilter(NonNullList<ItemStack> filterStacks, boolean blacklist, IItemHandler inv, Direction face, ThaumcraftInvHelper.InvFilter filter) {
        return findFirstMatchFromFilter(filterStacks, blacklist, inv, face, filter, false);
    }
    
    public static ItemStack findFirstMatchFromFilter(NonNullList<ItemStack> filterStacks, boolean blacklist, IItemHandler inv, Direction face, ThaumcraftInvHelper.InvFilter filter, boolean leaveOne) {
    Label_0181:
        for (int a = 0; a < inv.getSlots(); ++a) {
            ItemStack is = inv.getStackInSlot(a);
            if (is != null && !is.isEmpty()) {
                if (is.getCount() > 0) {
                    if (!leaveOne || ThaumcraftInvHelper.countTotalItemsIn(inv, is, filter) >= 2) {
                        boolean allow = false;
                        boolean allEmpty = true;
                        for (ItemStack fs : filterStacks) {
                            if (fs != null) {
                                if (fs.isEmpty()) {
                                    continue;
                                }
                                allEmpty = false;
                                boolean r = areItemStacksEqual(fs.copy(), is.copy(), filter);
                                if (blacklist) {
                                    if (r) {
                                        continue Label_0181;
                                    }
                                    allow = true;
                                }
                                else {
                                    if (r) {
                                        return is;
                                    }
                                    continue;
                                }
                            }
                        }
                        if (blacklist && (allow || allEmpty)) {
                            return is;
                        }
                    }
                }
            }
        }
        return ItemStack.EMPTY;
    }
    
    public static boolean matchesFilters(NonNullList<ItemStack> nonNullList, boolean blacklist, ItemStack is, ThaumcraftInvHelper.InvFilter filter) {
        if (is == null || is.isEmpty() || is.getCount() <= 0) {
            return false;
        }
        boolean allow = false;
        boolean allEmpty = true;
        for (ItemStack fs : nonNullList) {
            if (fs != null) {
                if (fs.isEmpty()) {
                    continue;
                }
                allEmpty = false;
                boolean r = areItemStacksEqual(fs.copy(), is.copy(), filter);
                if (blacklist) {
                    if (r) {
                        return false;
                    }
                    allow = true;
                }
                else {
                    if (r) {
                        return true;
                    }
                    continue;
                }
            }
        }
        return blacklist && (allow || allEmpty);
    }
    
    public static ItemStack findFirstMatchFromFilter(NonNullList<ItemStack> filterStacks, NonNullList<Integer> filterStacksSizes, boolean blacklist, NonNullList<ItemStack> itemStacks, ThaumcraftInvHelper.InvFilter filter) {
        return findFirstMatchFromFilterTuple(filterStacks, filterStacksSizes, blacklist, itemStacks, filter).getA();
    }
    
    public static Tuple<ItemStack, Integer> findFirstMatchFromFilterTuple(NonNullList<ItemStack> filterStacks, NonNullList<Integer> filterStacksSizes, boolean blacklist, NonNullList<ItemStack> stacks, ThaumcraftInvHelper.InvFilter filter) {
    Label_0006:
        for (ItemStack is : stacks) {
            if (is != null && !is.isEmpty()) {
                if (is.getCount() <= 0) {
                    continue;
                }
                boolean allow = false;
                boolean allEmpty = true;
                for (int idx = 0; idx < filterStacks.size(); ++idx) {
                    ItemStack fs = filterStacks.get(idx);
                    if (fs != null) {
                        if (!fs.isEmpty()) {
                            allEmpty = false;
                            boolean r = areItemStacksEqual(fs.copy(), is.copy(), filter);
                            if (blacklist) {
                                if (r) {
                                    continue Label_0006;
                                }
                                allow = true;
                            }
                            else if (r) {
                                return (Tuple<ItemStack, Integer>)new net.minecraft.util.Tuple<>(is, filterStacksSizes.get(idx));
                            }
                        }
                    }
                }
                if (blacklist && (allow || allEmpty)) {
                    return (Tuple<ItemStack, Integer>)new net.minecraft.util.Tuple<>(is, 0);
                }
                continue;
            }
        }
        return (Tuple<ItemStack, Integer>)new net.minecraft.util.Tuple<>(ItemStack.EMPTY, 0);
    }
    
    public static boolean areItemStacksEqual(ItemStack stack0, ItemStack stack1, ThaumcraftInvHelper.InvFilter filter) {
        if (stack0 == null && stack1 != null) {
            return false;
        }
        if (stack0 != null && stack1 == null) {
            return false;
        }
        if (stack0 == null && stack1 == null) {
            return true;
        }
        if (stack0.isEmpty() && !stack1.isEmpty()) {
            return false;
        }
        if (!stack0.isEmpty() && stack1.isEmpty()) {
            return false;
        }
        if (stack0.isEmpty() && stack1.isEmpty()) {
            return true;
        }
        if (filter.useMod) {
            String m1 = "A";
            String m2 = "B";
            net.minecraft.resources.Identifier id0 = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack0.getItem());
            if (id0 != null) m1 = id0.getNamespace();
            net.minecraft.resources.Identifier id1 = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack1.getItem());
            if (id1 != null) m2 = id1.getNamespace();
            return m1.equals(m2);
        }
        // OreDictionary removed — useOre filter not supported in modern port
        boolean t1 = true;
        if (!filter.igNBT) {
            t1 = (filter.relaxedNBT ? ThaumcraftInvHelper.areItemStackTagsEqualRelaxed(stack0, stack1) : ItemStack.isSameItemSameComponents(stack0, stack1));
        }
        if (stack0.getDamageValue() == 32767 || stack1.getDamageValue() == 32767) {
            filter.igDmg = true;
        }
        boolean t2 = !filter.igDmg && stack0.getDamageValue() != stack1.getDamageValue();
        return stack0.getItem() == stack1.getItem() && !t2 && t1;
    }
    
    public static void dropHarvestsAtPos(Level worldIn, BlockPos pos, List<ItemStack> list) {
        dropHarvestsAtPos(worldIn, pos, list, false, 0, null);
    }
    
    public static void dropHarvestsAtPos(Level worldIn, BlockPos pos, List<ItemStack> list, boolean followItem, int color, Entity target) {
        for (ItemStack item : list) {
            if (!worldIn.isClientSide()) {
                float f = 0.5f;
                double d0 = worldIn.getRandom().nextFloat() * f + (1.0f - f) * 0.5;
                double d2 = worldIn.getRandom().nextFloat() * f + (1.0f - f) * 0.5;
                double d3 = worldIn.getRandom().nextFloat() * f + (1.0f - f) * 0.5;
                ItemEntity entityitem = null;
                if (followItem) {
                    entityitem = new EntityFollowingItem(worldIn, pos.getX() + d0, pos.getY() + d2, pos.getZ() + d3, item, target, color);
                }
                else {
                    entityitem = new ItemEntity(worldIn, pos.getX() + d0, pos.getY() + d2, pos.getZ() + d3, item);
                }
                entityitem.setDefaultPickUpDelay();
                worldIn.addFreshEntity(entityitem);
            }
        }
    }
    
    public static void dropItemAtPos(Level world, ItemStack item, BlockPos pos) {
        if (!world.isClientSide() && item != null && !item.isEmpty() && item.getCount() > 0) {
            ItemEntity entityItem = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, item.copy());
            world.addFreshEntity(entityItem);
        }
    }
    
    public static void dropItemAtEntity(Level world, ItemStack item, Entity entity) {
        if (!world.isClientSide() && item != null && !item.isEmpty() && item.getCount() > 0) {
            ItemEntity entityItem = new ItemEntity(world, entity.getX(), entity.getY() + entity.getEyeHeight() / 2.0f, entity.getZ(), item.copy());
            world.addFreshEntity(entityItem);
        }
    }
    
    public static void dropItemsAtEntity(Level world, BlockPos pos, Entity entity) {
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (!(tileEntity instanceof Container) || world.isClientSide()) {
            return;
        }
        Container inventory = (Container)tileEntity;
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack item = inventory.getItem(i);
            if (!item.isEmpty() && item.getCount() > 0) {
                ItemEntity entityItem = new ItemEntity(world, entity.getX(), entity.getY() + entity.getEyeHeight() / 2.0f, entity.getZ(), item.copy());
                world.addFreshEntity(entityItem);
                inventory.setItem(i, ItemStack.EMPTY);
            }
        }
    }
    
    public static ItemStack cycleItemStack(Object input) {
        return cycleItemStack(input, 0);
    }
    
    public static ItemStack cycleItemStack(Object input, int counter) {
        ItemStack it = ItemStack.EMPTY;
        if (input instanceof Ingredient) {
            boolean b = !(input instanceof IngredientNBTTC);
            input = ((Ingredient)input).items().map(h -> new ItemStack(h.value())).toArray(ItemStack[]::new);
            if (b) {
                ItemStack[] q = (ItemStack[])input;
                ItemStack[] r = new ItemStack[q.length];
                for (int a = 0; a < q.length; ++a) {
                    (r[a] = q[a].copy()).setDamageValue(32767);
                }
                input = r;
            }
        }
        if (input instanceof ItemStack[]) {
            ItemStack[] q2 = (ItemStack[])input;
            if (q2 != null && q2.length > 0) {
                int idx = (int)((counter + System.currentTimeMillis() / 1000L) % q2.length);
                it = cycleItemStack(q2[idx], counter++);
            }
        }
        else if (input instanceof ItemStack) {
            it = (ItemStack)input;
            if (it != null && !it.isEmpty() && !it.isEmpty() && it.isDamageableItem() && it.getDamageValue() == 32767) {
                int q3 = 5000 / it.getMaxDamage();
                int md = (int)((counter + System.currentTimeMillis() / q3) % it.getMaxDamage());
                ItemStack it2 = it.copy();
                it2.setDamageValue(md);
                it = it2;
            }
        }
        else if (input instanceof List) {
            List<ItemStack> q4 = (List<ItemStack>)input;
            if (q4 != null && q4.size() > 0) {
                int idx = (int)((counter + System.currentTimeMillis() / 1000L) % q4.size());
                it = cycleItemStack(q4.get(idx), counter++);
            }
        }
        // OreDictionary.getOres removed — string-based ingredient cycling not supported
        return it;
    }
}
