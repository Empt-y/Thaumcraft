package thaumcraft.common.lib.network.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundSource;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.client.lib.events.HudHandler;
import thaumcraft.client.lib.events.RenderEventHandler;
import thaumcraft.common.lib.SoundsTC;
import io.netty.buffer.ByteBuf;


public class PacketKnowledgeGain implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<PacketKnowledgeGain> TYPE =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("thaumcraft", "knowledge_gain"));

    public static final StreamCodec<FriendlyByteBuf, PacketKnowledgeGain> STREAM_CODEC =
        StreamCodec.of(PacketKnowledgeGain::encode, PacketKnowledgeGain::decode);

    private final byte type;
    private final String cat;

    public PacketKnowledgeGain(byte type, String value) {
        this.type = type;
        this.cat = (value == null) ? "" : value;
    }

    private static void encode(FriendlyByteBuf buf, PacketKnowledgeGain pkt) {
        buf.writeByte(pkt.type);
        buf.writeUtf(pkt.cat);
    }

    private static PacketKnowledgeGain decode(FriendlyByteBuf buf) {
        byte type = buf.readByte();
        String cat = buf.readUtf();
        return new PacketKnowledgeGain(type, cat);
    }

    public static void handle(PacketKnowledgeGain msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            processMessage(msg);
        });
    }

    private static void processMessage(PacketKnowledgeGain message) {
        Player p = Minecraft.getInstance().player;
        IPlayerKnowledge.EnumKnowledgeType type = IPlayerKnowledge.EnumKnowledgeType.values()[message.type];
        ResearchCategory cat = (message.cat.length() > 0) ? ResearchCategories.getResearchCategory(message.cat) : null;
        RenderEventHandler instance = RenderEventHandler.INSTANCE;
        RenderEventHandler.hudHandler.knowledgeGainTrackers.add(new HudHandler.KnowledgeGainTracker(type, cat, 40 + p.level().getRandom().nextInt(20), p.level().getRandom().nextLong()));
        p.level().playSound(p, p.getX(), p.getY(), p.getZ(), SoundsTC.learn, SoundSource.AMBIENT, 1.0f, 1.0f);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() { return TYPE; }
}
