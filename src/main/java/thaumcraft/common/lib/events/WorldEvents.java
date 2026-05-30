package thaumcraft.common.lib.events;
import java.util.ArrayList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.NoteBlockEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;
import thaumcraft.common.golems.seals.SealHandler;
import thaumcraft.common.tiles.devices.TileArcaneEar;
import thaumcraft.common.world.aura.AuraHandler;


@net.neoforged.fml.common.EventBusSubscriber(modid = "thaumcraft")
public class WorldEvents
{
    public static WorldEvents INSTANCE;
    
    @SubscribeEvent
    public static void worldLoad(net.neoforged.neoforge.event.level.LevelEvent.Load event) {
        if (!event.getLevel().isClientSide()) {
            AuraHandler.addAuraWorld(event.getLevel() instanceof net.minecraft.world.level.Level _l ? _l.dimension().identifier().hashCode() : 0 /* getDimension removed */);
        }
    }
    
    @SubscribeEvent
    public static void worldSave(net.neoforged.neoforge.event.level.LevelEvent.Save event) {
        if (!event.getLevel().isClientSide()) {}
    }
    
    @SubscribeEvent
    public static void worldUnload(net.neoforged.neoforge.event.level.LevelEvent.Unload event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        SealHandler.sealEntities.remove(event.getLevel() instanceof net.minecraft.world.level.Level _l ? _l.dimension().identifier().hashCode() : 0 /* getDimension removed */);
        AuraHandler.removeAuraWorld(event.getLevel() instanceof net.minecraft.world.level.Level _l ? _l.dimension().identifier().hashCode() : 0 /* getDimension removed */);
    }
    
    @SubscribeEvent
    public static void placeBlockEvent(BlockEvent.EntityPlaceEvent event) {
        if (isNearActiveBoss(event.getLevel(), event.getEntity(), event.getPos().x, event.getPos().getY(), event.getPos().z)) {
            event.setCanceled(true);
        }
    }
    
    @SubscribeEvent
    public static void placeBlockEvent(BlockEvent.EntityMultiPlaceEvent event) {
        if (isNearActiveBoss(event.getLevel(), event.getEntity(), event.getPos().x, event.getPos().getY(), event.getPos().z)) {
            event.setCanceled(true);
        }
    }
    
    private static boolean isNearActiveBoss(Level world, Player player, int x, int y, int z) {
        return false;
    }
    
    @SubscribeEvent
    public static void noteEvent(net.neoforged.neoforge.event.level.NoteBlockEvent event) {
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (!TileArcaneEar.noteBlockEvents.containsKey(event.getLevel() instanceof net.minecraft.world.level.Level _l ? _l.dimension().identifier().hashCode() : 0 /* getDimension removed */)) {
            TileArcaneEar.noteBlockEvents.put(event.getLevel() instanceof net.minecraft.world.level.Level _l ? _l.dimension().identifier().hashCode() : 0 /* getDimension removed */, new ArrayList<Integer[]>());
        }
        ArrayList<Integer[]> list = TileArcaneEar.noteBlockEvents.get(event.getLevel() instanceof net.minecraft.world.level.Level _l ? _l.dimension().identifier().hashCode() : 0 /* getDimension removed */);
        list.add(new Integer[] { event.getPos().x, event.getPos().getY(), event.getPos().z, event.getInstrument().ordinal(), event.getVanillaNoteId() });
        TileArcaneEar.noteBlockEvents.put(event.getLevel() instanceof net.minecraft.world.level.Level _l ? _l.dimension().identifier().hashCode() : 0 /* getDimension removed */, list);
    }
    
    static {
        WorldEvents.INSTANCE = new WorldEvents();
    }
}
