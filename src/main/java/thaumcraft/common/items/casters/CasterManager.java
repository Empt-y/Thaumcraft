package thaumcraft.common.items.casters;
import net.minecraft.world.Container;
// baubles import removed
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;
import thaumcraft.api.casters.FocusEngine;
import thaumcraft.api.casters.FocusPackage;
import thaumcraft.api.casters.ICaster;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.potions.PotionVisExhaust;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.potions.PotionInfectiousVisExhaust;


public class CasterManager
{
    static HashMap<Integer, Long> cooldownServer;
    static HashMap<Integer, Long> cooldownClient;
    
    public static float getTotalVisDiscount(Player player) {
        int total = 0;
        if (player == null) {
            return 0.0f;
        }
        Container baubles = null /* call removed */;
        for (int a = 0; a < baubles.getContainerSize(); ++a) {
            if (baubles.getItem(a) != null && baubles.getItem(a).getItem() instanceof IVisDiscountGear) {
                total += ((IVisDiscountGear)baubles.getItem(a).getItem()).getVisDiscount(baubles.getItem(a), player);
            }
        }
        for (int a = 0; a < 4; ++a) {
            if (player.getInventory().getItem(36 + a).getItem() instanceof IVisDiscountGear) {
                total += ((IVisDiscountGear) player.getInventory().getItem(36 + a).getItem()).getVisDiscount(player.getInventory().getItem(36 + a), player);
            }
        }
        if (player.hasEffect(net.minecraft.core.Holder.direct(PotionVisExhaust.instance)) || player.hasEffect(net.minecraft.core.Holder.direct(PotionInfectiousVisExhaust.instance))) {
            int level1 = 0;
            int level2 = 0;
            if (player.hasEffect(net.minecraft.core.Holder.direct(PotionVisExhaust.instance))) {
                level1 = player.getEffect(net.minecraft.core.Holder.direct(PotionVisExhaust.instance)).getAmplifier();
            }
            if (player.hasEffect(net.minecraft.core.Holder.direct(PotionInfectiousVisExhaust.instance))) {
                level2 = player.getEffect(net.minecraft.core.Holder.direct(PotionInfectiousVisExhaust.instance)).getAmplifier();
            }
            total -= (Math.max(level1, level2) + 1) * 10;
        }
        return total / 100.0f;
    }
    
    public static boolean consumeVisFromInventory(Player player, float cost) {
        for (int a = player.getInventory().getContainerSize() - 1; a >= 0; --a) {
            ItemStack item = player.getInventory().getItem(a);
            if (item.getItem() instanceof ICaster) {
                boolean done = ((ICaster)item.getItem()).consumeVis(item, player, cost, true, false);
                if (done) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void changeFocus(ItemStack is, Level w, Player player, String focus) {
        ICaster wand = (ICaster)is.getItem();
        TreeMap<String, Integer> foci = new TreeMap<String, Integer>();
        HashMap<Integer, Integer> pouches = new HashMap<Integer, Integer>();
        int pouchcount = 0;
        ItemStack item = ItemStack.EMPTY;
        Container baubles = null /* call removed */;
        for (int a = 0; a < baubles.getContainerSize(); ++a) {
            if (baubles.getItem(a).getItem() instanceof ItemFocusPouch) {
                ++pouchcount;
                item = baubles.getItem(a);
                pouches.put(pouchcount, a - 4);
                NonNullList<ItemStack> inv = ((ItemFocusPouch)item.getItem()).getInventory(item);
                for (int q = 0; q < inv.size(); ++q) {
                    item = inv.get(q);
                    if (item.getItem() instanceof ItemFocus) {
                        String sh = ((ItemFocus)item.getItem()).getSortingHelper(item);
                        if (sh != null) {
                            foci.put(sh, q + pouchcount * 1000);
                        }
                    }
                }
            }
        }
        for (int a = 0; a < 36; ++a) {
            item = player.getInventory().getItem(a);
            if (item.getItem() instanceof ItemFocus) {
                String sh2 = ((ItemFocus)item.getItem()).getSortingHelper(item);
                if (sh2 == null) {
                    continue;
                }
                foci.put(sh2, a);
            }
            if (item.getItem() instanceof ItemFocusPouch) {
                ++pouchcount;
                pouches.put(pouchcount, a);
                NonNullList<ItemStack> inv = ((ItemFocusPouch)item.getItem()).getInventory(item);
                for (int q = 0; q < inv.size(); ++q) {
                    item = inv.get(q);
                    if (item.getItem() instanceof ItemFocus) {
                        String sh = ((ItemFocus)item.getItem()).getSortingHelper(item);
                        if (sh != null) {
                            foci.put(sh, q + pouchcount * 1000);
                        }
                    }
                }
            }
        }
        if (focus.equals("REMOVE") || foci.size() == 0) {
            if (wand.getFocus(is) != null && (addFocusToPouch(player, wand.getFocusStack(is).copy(), pouches) || player.getInventory().add(wand.getFocusStack(is).copy()))) {
                wand.setFocus(is, null);
                player.playSound(SoundsTC.ticks, 0.3f, 0.9f);
            }
            return;
        }
        if (foci != null && foci.size() > 0 && focus != null) {
            String newkey = focus;
            if (foci.get(newkey) == null) {
                newkey = foci.higherKey(newkey);
            }
            if (newkey == null || foci.get(newkey) == null) {
                newkey = foci.firstKey();
            }
            if (foci.get(newkey) < 1000 && foci.get(newkey) >= 0) {
                item = player.getInventory().getItem(foci.get(newkey)).copy();
            }
            else {
                int pid = foci.get(newkey) / 1000;
                if (pouches.containsKey(pid)) {
                    int pouchslot = pouches.get(pid);
                    int focusslot = foci.get(newkey) - pid * 1000;
                    ItemStack tmp = ItemStack.EMPTY;
                    if (pouchslot >= 0) {
                        tmp = player.getInventory().getItem(pouchslot).copy();
                    }
                    else {
                        tmp = baubles.getItem(pouchslot + 4).copy();
                    }
                    item = fetchFocusFromPouch(player, focusslot, tmp, pouchslot);
                }
            }
            if (item == null || item.isEmpty()) {
                return;
            }
            if (foci.get(newkey) < 1000 && foci.get(newkey) >= 0) {
                player.getInventory().setItem(foci.get(newkey), ItemStack.EMPTY);
            }
            player.playSound(SoundsTC.ticks, 0.3f, 1.0f);
            if (wand.getFocus(is) != null && (addFocusToPouch(player, wand.getFocusStack(is).copy(), pouches) || player.getInventory().add(wand.getFocusStack(is).copy()))) {
                wand.setFocus(is, ItemStack.EMPTY);
            }
            if (wand.getFocus(is) == null) {
                wand.setFocus(is, item);
            }
            else if (!addFocusToPouch(player, item, pouches)) {
                player.getInventory().add(item);
            }
        }
    }
    
    private static ItemStack fetchFocusFromPouch(Player player, int focusid, ItemStack pouch, int pouchslot) {
        ItemStack focus = ItemStack.EMPTY;
        NonNullList<ItemStack> inv = ((ItemFocusPouch)pouch.getItem()).getInventory(pouch);
        ItemStack contents = inv.get(focusid);
        if (contents.getItem() instanceof ItemFocus) {
            focus = contents.copy();
            inv.set(focusid, ItemStack.EMPTY);
            ((ItemFocusPouch)pouch.getItem()).setInventory(pouch, inv);
            if (pouchslot >= 0) {
                player.getInventory().setItem(pouchslot, pouch);
                player.getInventory().setChanged();
            }
            else {
                Container baubles = null /* call removed */;
                baubles.setItem(pouchslot + 4, pouch);
                
                baubles.setChanged();
            }
        }
        return focus;
    }
    
    private static boolean addFocusToPouch(Player player, ItemStack focus, HashMap<Integer, Integer> pouches) {
        Container baubles = null /* call removed */;
        for (Integer pouchslot : pouches.values()) {
            ItemStack pouch;
            if (pouchslot >= 0) {
                pouch = player.getInventory().getItem(pouchslot);
            }
            else {
                pouch = baubles.getItem(pouchslot + 4);
            }
            NonNullList<ItemStack> inv = ((ItemFocusPouch)pouch.getItem()).getInventory(pouch);
            for (int q = 0; q < inv.size(); ++q) {
                ItemStack contents = inv.get(q);
                if (contents.isEmpty()) {
                    inv.set(q, focus.copy());
                    ((ItemFocusPouch)pouch.getItem()).setInventory(pouch, inv);
                    if (pouchslot >= 0) {
                        player.getInventory().setItem(pouchslot, pouch);
                        player.getInventory().setChanged();
                    }
                    else {
                        baubles.setItem(pouchslot + 4, pouch);
                        
                        baubles.setChanged();
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    public static void toggleMisc(ItemStack itemstack, Level world, Player player, int mod) {
        if (!(itemstack.getItem() instanceof ICaster)) {
            return;
        }
        ICaster caster = (ICaster)itemstack.getItem();
        ItemFocus focus = (ItemFocus)caster.getFocus(itemstack);
        FocusPackage fp = ItemFocus.getPackage(caster.getFocusStack(itemstack));
        if (fp != null && FocusEngine.doesPackageContainElement(fp, "thaumcraft.PLAN")) {
            int dim = getAreaDim(itemstack);
            if (mod == 0) {
                int areax = getAreaX(itemstack);
                int areay = getAreaY(itemstack);
                int areaz = getAreaZ(itemstack);
                int max = getAreaSize(itemstack);
                if (dim == 0) {
                    ++areax;
                    ++areaz;
                    ++areay;
                }
                else if (dim == 1) {
                    ++areax;
                }
                else if (dim == 2) {
                    ++areaz;
                }
                else if (dim == 3) {
                    ++areay;
                }
                if (areax > max) {
                    areax = 0;
                }
                if (areaz > max) {
                    areaz = 0;
                }
                if (areay > max) {
                    areay = 0;
                }
                setAreaX(itemstack, areax);
                setAreaY(itemstack, areay);
                setAreaZ(itemstack, areaz);
            }
            if (mod == 1) {
                if (++dim > 3) {
                    dim = 0;
                }
                setAreaDim(itemstack, dim);
            }
        }
    }
    
    private static int getAreaSize(ItemStack itemstack) {
        boolean pot = false;
        if (!(itemstack.getItem() instanceof ICaster)) {
            return 0;
        }
        ICaster caster = (ICaster)itemstack.getItem();
        ItemFocus focus = (ItemFocus)caster.getFocus(itemstack);
        return pot ? 6 : 3;
    }
    
    public static int getAreaDim(ItemStack stack) {
        if (!stack.isEmpty() && stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("aread")) {
            return stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getIntOr("aread", 0);
        }
        return 0;
    }
    
    public static int getAreaX(ItemStack stack) {
        ICaster wand = (ICaster)stack.getItem();
        if (!stack.isEmpty() && stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("areax")) {
            int a = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getIntOr("areax", 0);
            if (a > getAreaSize(stack)) {
                a = getAreaSize(stack);
            }
            return a;
        }
        return getAreaSize(stack);
    }
    
    public static int getAreaY(ItemStack stack) {
        ICaster wand = (ICaster)stack.getItem();
        if (!stack.isEmpty() && stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("areay")) {
            int a = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getIntOr("areay", 0);
            if (a > getAreaSize(stack)) {
                a = getAreaSize(stack);
            }
            return a;
        }
        return getAreaSize(stack);
    }
    
    public static int getAreaZ(ItemStack stack) {
        ICaster wand = (ICaster)stack.getItem();
        if (!stack.isEmpty() && stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("areaz")) {
            int a = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getIntOr("areaz", 0);
            if (a > getAreaSize(stack)) {
                a = getAreaSize(stack);
            }
            return a;
        }
        return getAreaSize(stack);
    }
    
    public static void setAreaX(ItemStack stack, int area) {
        if (!stack.isEmpty()) {
            {net.minecraft.nbt.CompoundTag _t = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag(); _t.putInt("areax", area); stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(_t));}
        }
    }
    
    public static void setAreaY(ItemStack stack, int area) {
        if (!stack.isEmpty()) {
            {net.minecraft.nbt.CompoundTag _t = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag(); _t.putInt("areay", area); stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(_t));}
        }
    }
    
    public static void setAreaZ(ItemStack stack, int area) {
        if (!stack.isEmpty()) {
            {net.minecraft.nbt.CompoundTag _t = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag(); _t.putInt("areaz", area); stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(_t));}
        }
    }
    
    public static void setAreaDim(ItemStack stack, int dim) {
        if (!stack.isEmpty()) {
            {net.minecraft.nbt.CompoundTag _t = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag(); _t.putInt("aread", dim); stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(_t));}
        }
    }
    
    static boolean isOnCooldown(LivingEntity entityLiving) {
        if (entityLiving.level().isClientSide() && CasterManager.cooldownClient.containsKey(entityLiving.getId())) {
            return CasterManager.cooldownClient.get(entityLiving.getId()) > System.currentTimeMillis();
        }
        return !entityLiving.level().isClientSide() && CasterManager.cooldownServer.containsKey(entityLiving.getId()) && CasterManager.cooldownServer.get(entityLiving.getId()) > System.currentTimeMillis();
    }
    
    public static float getCooldown(LivingEntity entityLiving) {
        if (entityLiving.level().isClientSide() && CasterManager.cooldownClient.containsKey(entityLiving.getId())) {
            return (CasterManager.cooldownClient.get(entityLiving.getId()) - System.currentTimeMillis()) / 1000.0f;
        }
        return 0.0f;
    }
    
    public static void setCooldown(LivingEntity entityLiving, int cd) {
        if (cd == 0) {
            CasterManager.cooldownClient.remove(entityLiving.getId());
            CasterManager.cooldownServer.remove(entityLiving.getId());
        }
        else if (entityLiving.level().isClientSide()) {
            CasterManager.cooldownClient.put(entityLiving.getId(), System.currentTimeMillis() + cd * 50);
        }
        else {
            CasterManager.cooldownServer.put(entityLiving.getId(), System.currentTimeMillis() + cd * 50);
        }
    }
    
    static {
        CasterManager.cooldownServer = new HashMap<Integer, Long>();
        CasterManager.cooldownClient = new HashMap<Integer, Long>();
    }
}
