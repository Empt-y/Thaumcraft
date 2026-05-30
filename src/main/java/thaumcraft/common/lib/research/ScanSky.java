package thaumcraft.common.lib.research;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.IScanThing;
import thaumcraft.common.lib.utils.InventoryUtils;


public class ScanSky implements IScanThing
{
    /** Celestial angle (0..360) derived from overworld clock time */
    private static int celestialAngle(Player player) {
        long ticks = player.level().getOverworldClockTime() % 24000L;
        return (int)((ticks / 24000.0 + 0.25) * 360.0) % 360;
    }

    /** Moon phase (0..7) derived from day count */
    private static int moonPhase(Player player) {
        return (int)(player.level().getOverworldClockTime() / 24000L) % 8;
    }

    /** Sky-exposure check replacing removed canSeeSky */
    private static boolean canSeeSky(Player player) {
        return player.level().getBrightness(LightLayer.SKY, player.blockPosition().above()) > 0;
    }

    @Override
    public boolean checkThing(Player player, Object obj) {
        if (obj != null || player.getXRot() > 0.0f || !canSeeSky(player)
                || !player.level().dimension().equals(Level.OVERWORLD)
                || !ThaumcraftCapabilities.knowsResearchStrict(player, "CELESTIALSCANNING")) {
            return false;
        }
        int yaw = (int)(player.getYRot() + 90.0f) % 360;
        int pitch = (int)Math.abs(player.getXRot());
        int ca = celestialAngle(player);
        boolean night = ca > 180;
        boolean inRangeYaw = false;
        boolean inRangePitch = false;
        if (night) {
            ca -= 180;
        }
        if (ca > 90) {
            inRangeYaw = (Math.abs(Math.abs(yaw) - 180) < 10);
            inRangePitch = (Math.abs(180 - ca - pitch) < 7);
        }
        else {
            inRangeYaw = (Math.abs(yaw) < 10);
            inRangePitch = (Math.abs(ca - pitch) < 7);
        }
        return (inRangeYaw && inRangePitch) || night;
    }

    @Override
    public void onSuccess(Player player, Object object) {
        if (object != null || player.getXRot() > 0.0f || !canSeeSky(player)
                || !ThaumcraftCapabilities.knowsResearchStrict(player, "CELESTIALSCANNING")) {
            return;
        }
        int yaw = (int)(player.getYRot() + 90.0f) % 360;
        int pitch = (int)Math.abs(player.getXRot());
        int ca = celestialAngle(player);
        boolean night = ca > 180;
        boolean inRangeYaw = false;
        boolean inRangePitch = false;
        if (night) {
            ca -= 180;
        }
        if (ca > 90) {
            inRangeYaw = (Math.abs(Math.abs(yaw) - 180) < 10);
            inRangePitch = (Math.abs(180 - ca - pitch) < 7);
        }
        else {
            inRangeYaw = (Math.abs(yaw) < 10);
            inRangePitch = (Math.abs(ca - pitch) < 7);
        }
        int worldDay = (int)(player.level().getOverworldClockTime() / 24000L);
        if (inRangeYaw && inRangePitch) {
            String pk = "CEL_" + worldDay + "_";
            String key = pk + (night ? ("Moon" + moonPhase(player)) : "Sun");
            if (ThaumcraftCapabilities.knowsResearch(player, key)) {
                player.sendOverlayMessage(Component.translatable("tc.celestial.fail.1", ""));
                return;
            }
            if (InventoryUtils.isPlayerCarryingAmount(player, new ItemStack(ItemsTC.scribingTools, 1), true) && InventoryUtils.consumePlayerItem(player, new ItemStack(Items.PAPER), false, true)) {
                ItemStack stack = new ItemStack(ItemsTC.celestialNotes.asItem(), 1);
                if (!player.getInventory().add(stack)) {
                    player.drop(stack, false);
                }
                ThaumcraftApi.internalMethods.progressResearch(player, key);
            }
            else {
                player.sendOverlayMessage(Component.translatable("tc.celestial.fail.2", ""));
            }
            cleanResearch(player, pk);
        }
        else {
            if (!night) {
                return;
            }
            // getDirection() returns the horizontal facing direction
            Direction face = player.getDirection();
            // get2DDataValue: S=0, W=1, N=2, E=3; subtract 2 for original behaviour
            int num = face.get2DDataValue() - 2;
            String pk2 = "CEL_" + worldDay + "_";
            String key2 = pk2 + "Star" + num;
            if (ThaumcraftCapabilities.knowsResearch(player, key2)) {
                player.sendOverlayMessage(Component.translatable("tc.celestial.fail.1", ""));
                return;
            }
            if (InventoryUtils.isPlayerCarryingAmount(player, new ItemStack(ItemsTC.scribingTools, 1), true) && InventoryUtils.consumePlayerItem(player, new ItemStack(Items.PAPER), false, true)) {
                ItemStack stack2 = new ItemStack(ItemsTC.celestialNotes.asItem(), 1);
                if (!player.getInventory().add(stack2)) {
                    player.drop(stack2, false);
                }
                ThaumcraftApi.internalMethods.progressResearch(player, key2);
            }
            else {
                player.sendOverlayMessage(Component.translatable("tc.celestial.fail.2", ""));
            }
            cleanResearch(player, pk2);
        }
    }

    private void cleanResearch(Player player, String pk) {
        ArrayList<String> list = new ArrayList<String>();
        for (String key : ThaumcraftCapabilities.getKnowledge(player).getResearchList()) {
            if (key.startsWith("CEL_") && !key.startsWith(pk)) {
                list.add(key);
            }
        }
        for (String key : list) {
            ThaumcraftCapabilities.getKnowledge(player).removeResearch(key);
        }
        ResearchManager.syncList.put(player.getName().getString(), true);
    }

    @Override
    public String getResearchKey(Player player, Object object) {
        return "";
    }
}
