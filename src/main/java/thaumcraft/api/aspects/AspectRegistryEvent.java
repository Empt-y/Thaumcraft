package thaumcraft.api.aspects;
import net.neoforged.bus.api.Event;


/**
 * This event is called when Thaumcraft is ready to accept the registration of aspects.
 * Subscribe to this event on the NeoForge mod event bus.
 */
public class AspectRegistryEvent extends Event {

	/** this should always be set by TC itself - do not assign your own proxy */
	public AspectEventProxy register;

	public AspectRegistryEvent() {}
}
