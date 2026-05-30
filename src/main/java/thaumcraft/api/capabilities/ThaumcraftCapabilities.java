package thaumcraft.api.capabilities;
import javax.annotation.Nonnull;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.EntityCapability;


/**
 * @author Azanor (ported to NeoForge 26.1.2 EntityCapability API)
 */
public class ThaumcraftCapabilities {

	public static final EntityCapability<IPlayerKnowledge, Void> KNOWLEDGE =
		EntityCapability.createVoid(
			Identifier.fromNamespaceAndPath("thaumcraft", "knowledge"),
			IPlayerKnowledge.class
		);

	public static final EntityCapability<IPlayerWarp, Void> WARP =
		EntityCapability.createVoid(
			Identifier.fromNamespaceAndPath("thaumcraft", "warp"),
			IPlayerWarp.class
		);

	public static IPlayerKnowledge getKnowledge(@Nonnull Player player) {
		return player.getCapability(KNOWLEDGE);
	}

	public static IPlayerWarp getWarp(@Nonnull Player player) {
		return player.getCapability(WARP);
	}

	public static boolean knowsResearch(@Nonnull Player player, @Nonnull String... research) {
		IPlayerKnowledge k = getKnowledge(player);
		if (k == null) return false;
		for (String r : research) {
			if (r.contains("&&")) {
				if (!knowsResearch(player, r.split("&&"))) return false;
			} else if (r.contains("||")) {
				boolean any = false;
				for (String str : r.split("\\|\\|")) if (knowsResearch(player, str)) { any = true; break; }
				if (!any) return false;
			} else {
				if (!k.isResearchKnown(r)) return false;
			}
		}
		return true;
	}

	public static boolean knowsResearchStrict(@Nonnull Player player, @Nonnull String... research) {
		IPlayerKnowledge k = getKnowledge(player);
		if (k == null) return false;
		for (String r : research) {
			if (r.contains("&&")) {
				if (!knowsResearchStrict(player, r.split("&&"))) return false;
			} else if (r.contains("||")) {
				boolean any = false;
				for (String str : r.split("\\|\\|")) if (knowsResearchStrict(player, str)) { any = true; break; }
				if (!any) return false;
			} else if (r.contains("@")) {
				if (!k.isResearchKnown(r)) return false;
			} else {
				if (!k.isResearchComplete(r)) return false;
			}
		}
		return true;
	}

}
