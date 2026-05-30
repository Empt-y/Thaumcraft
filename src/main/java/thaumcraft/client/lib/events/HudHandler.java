package thaumcraft.client.lib.events;
import java.util.concurrent.LinkedBlockingQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.common.world.aura.AuraChunk;


public class HudHandler
{
    Identifier HUD;
    public LinkedBlockingQueue<KnowledgeGainTracker> knowledgeGainTrackers;
    public static Identifier BOOK;
    public static Identifier[] KNOW_TYPE;
    float kgFade;
    public static AuraChunk currentAura;
    private float VISCON = 525.0f;
    long nextsync;
    Identifier TAGBACK;

    public HudHandler() {
        HUD = Identifier.fromNamespaceAndPath("thaumcraft", "textures/gui/hud.png");
        knowledgeGainTrackers = new LinkedBlockingQueue<KnowledgeGainTracker>();
        kgFade = 0.0f;
        nextsync = 0L;
        TAGBACK = Identifier.fromNamespaceAndPath("thaumcraft", "textures/aspects/_back.png");
    }

    @OnlyIn(Dist.CLIENT)
    void renderHuds(Minecraft mc, float renderTickTime, Player player, long time) {
        // TODO: rewrite with modern rendering API
    }

    @OnlyIn(Dist.CLIENT)
    void renderHudsInGUI(Minecraft mc, float renderTickTime, Player player, long time, int ww, int hh) {
        // TODO: rewrite with modern rendering API
    }

    @OnlyIn(Dist.CLIENT)
    void renderKnowledgeGains(Minecraft mc, float renderTickTime, Player player, long time, int ww, int hh) {
        // TODO: rewrite with modern rendering API
    }

    @OnlyIn(Dist.CLIENT)
    void renderThaumometerHud(Minecraft mc, float partialTicks, Player player, long time, int ww, int hh, int shifty) {
        // TODO: rewrite with modern rendering API
    }

    @OnlyIn(Dist.CLIENT)
    void renderSanityHud(Minecraft mc, Float partialTicks, Player player, long time, int shifty) {
        // TODO: rewrite with modern rendering API
    }

    @OnlyIn(Dist.CLIENT)
    void renderChargeMeters(Minecraft mc, float renderTickTime, Player player, long time, int ww, int hh) {
        // TODO: rewrite with modern rendering API
    }

    @OnlyIn(Dist.CLIENT)
    void renderCastingWandHud(Minecraft mc, float partialTicks, Player player, long time, ItemStack wandstack, int shifty) {
        // TODO: rewrite with modern rendering API
    }

    @OnlyIn(Dist.CLIENT)
    public void renderWandTradeHud(float partialTicks, Player player, long time, ItemStack picked) {
        // TODO: rewrite with modern rendering API
    }

    public void renderAspectsInGui(AbstractContainerScreen gui, Player player, ItemStack stack, int sd, int sx, int sy) {
        // TODO: rewrite with modern rendering API
    }

    private boolean isMouseOverSlot(Slot par1Slot, int par2, int par3, int par4, int par5) {
        return false;
    }

    static {
        BOOK = Identifier.fromNamespaceAndPath("thaumcraft", "textures/items/thaumonomicon.png");
        KNOW_TYPE = new Identifier[] { Identifier.fromNamespaceAndPath("thaumcraft", "textures/research/knowledge_theory.png"), Identifier.fromNamespaceAndPath("thaumcraft", "textures/research/knowledge_observation.png") };
        HudHandler.currentAura = new AuraChunk(null, (short)0, 0.0f, 0.0f);
    }

    public static class KnowledgeGainTracker
    {
        IPlayerKnowledge.EnumKnowledgeType type;
        ResearchCategory category;
        int progress;
        int max;
        long seed;
        boolean sparks;

        public KnowledgeGainTracker(IPlayerKnowledge.EnumKnowledgeType type, ResearchCategory category, int progress, long seed) {
            sparks = false;
            this.type = type;
            this.category = category;
            if (type == IPlayerKnowledge.EnumKnowledgeType.THEORY) {
                progress += 10;
            }
            this.progress = progress;
            max = progress;
            this.seed = seed;
        }
    }
}
