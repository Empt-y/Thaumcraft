package thaumcraft.common.lib.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.RegisterEvent;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;

import java.util.Set;

/**
 * NeoForge 26.x data attachment types for player warp and knowledge.
 * AttachmentType provides automatic save/load to the player's data file,
 * replacing the old ICapabilitySerializable approach.
 */
public class TCPlayerData {

    public static AttachmentType<IPlayerWarp>     WARP;
    public static AttachmentType<IPlayerKnowledge> KNOWLEDGE;

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(TCPlayerData::onRegister);
    }

    private static void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(net.neoforged.neoforge.registries.NeoForgeRegistries.Keys.ATTACHMENT_TYPES)) return;

        WARP = AttachmentType.builder(PlayerWarp::createDefault)
            .serialize(new IAttachmentSerializer<IPlayerWarp>() {
                @Override
                public IPlayerWarp read(IAttachmentHolder holder, net.minecraft.world.level.storage.ValueInput in) {
                    IPlayerWarp impl = PlayerWarp.createDefault();
                    CompoundTag tag = new CompoundTag();
                    tag.putIntArray("warp", in.getIntArray("warp").orElse(new int[0]));
                    tag.putInt("counter", in.getIntOr("counter", 0));
                    impl.deserializeNBT(tag);
                    return impl;
                }
                @Override
                public boolean write(IPlayerWarp warp, net.minecraft.world.level.storage.ValueOutput out) {
                    CompoundTag tag = warp.serializeNBT();
                    out.putIntArray("warp",   tag.getIntArray("warp").orElse(new int[0]));
                    out.putInt("counter", tag.getIntOr("counter", 0));
                    return true;
                }
            })
            .copyOnDeath()
            .build();

        KNOWLEDGE = AttachmentType.builder(PlayerKnowledge::createDefault)
            .serialize(new IAttachmentSerializer<IPlayerKnowledge>() {
                @Override
                public IPlayerKnowledge read(IAttachmentHolder holder, net.minecraft.world.level.storage.ValueInput in) {
                    IPlayerKnowledge impl = PlayerKnowledge.createDefault();
                    // Rebuild research list
                    CompoundTag rootTag = new CompoundTag();
                    ListTag researchList = new ListTag();
                    for (net.minecraft.world.level.storage.ValueInput entry :
                            in.childrenListOrEmpty("research")) {
                        CompoundTag rt = new CompoundTag();
                        rt.putString("key",   entry.getStringOr("key", ""));
                        int stage = entry.getIntOr("stage", 0);
                        if (stage > 0) rt.putInt("stage", stage);
                        String flags = entry.getStringOr("flags", "");
                        if (!flags.isEmpty()) rt.putString("flags", flags);
                        researchList.add(rt);
                    }
                    rootTag.put("research", researchList);
                    ListTag knowledgeList = new ListTag();
                    for (net.minecraft.world.level.storage.ValueInput entry :
                            in.childrenListOrEmpty("knowledge")) {
                        CompoundTag kt = new CompoundTag();
                        kt.putString("key",    entry.getStringOr("key", ""));
                        kt.putInt("amount", entry.getIntOr("amount", 0));
                        knowledgeList.add(kt);
                    }
                    rootTag.put("knowledge", knowledgeList);
                    impl.deserializeNBT(rootTag);
                    return impl;
                }

                @Override
                public boolean write(IPlayerKnowledge knowledge, net.minecraft.world.level.storage.ValueOutput out) {
                    CompoundTag rootTag = knowledge.serializeNBT();
                    // Write research entries
                    ListTag researchList = rootTag.getListOrEmpty("research");
                    net.minecraft.world.level.storage.ValueOutput.ValueOutputList rOut = out.childrenList("research");
                    for (int i = 0; i < researchList.size(); i++) {
                        CompoundTag rt = researchList.getCompoundOrEmpty(i);
                        net.minecraft.world.level.storage.ValueOutput entry = rOut.addChild();
                        entry.putString("key", rt.getStringOr("key", ""));
                        int stage = rt.getIntOr("stage", 0);
                        if (stage > 0) entry.putInt("stage", stage);
                        String flags = rt.getStringOr("flags", "");
                        if (!flags.isEmpty()) entry.putString("flags", flags);
                    }
                    // Write knowledge entries
                    ListTag knowledgeList = rootTag.getListOrEmpty("knowledge");
                    net.minecraft.world.level.storage.ValueOutput.ValueOutputList kOut = out.childrenList("knowledge");
                    for (int i = 0; i < knowledgeList.size(); i++) {
                        CompoundTag kt = knowledgeList.getCompoundOrEmpty(i);
                        net.minecraft.world.level.storage.ValueOutput entry = kOut.addChild();
                        entry.putString("key",    kt.getStringOr("key", ""));
                        entry.putInt("amount", kt.getIntOr("amount", 0));
                    }
                    return true;
                }
            })
            .copyOnDeath()
            .build();

        net.minecraft.core.Registry.register(
            net.neoforged.neoforge.registries.NeoForgeRegistries.ATTACHMENT_TYPES,
            Identifier.fromNamespaceAndPath("thaumcraft", "warp"), WARP);
        net.minecraft.core.Registry.register(
            net.neoforged.neoforge.registries.NeoForgeRegistries.ATTACHMENT_TYPES,
            Identifier.fromNamespaceAndPath("thaumcraft", "knowledge"), KNOWLEDGE);
    }
}
