package thaumcraft.common.lib;
import java.util.Arrays;
import java.util.Collection;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.api.research.ResearchCategory;
import thaumcraft.api.research.ResearchEntry;
import thaumcraft.api.research.ResearchStage;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketWarpMessage;
import thaumcraft.common.lib.research.ResearchManager;


public class CommandThaumcraft
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("thaumcraft").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .then(Commands.literal("help")
                .executes(ctx -> help(ctx.getSource())))
            .then(Commands.literal("reload")
                .executes(ctx -> reload(ctx.getSource())))
            .then(Commands.literal("research")
                .then(Commands.literal("list")
                    .executes(ctx -> listResearch(ctx.getSource())))
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.literal("all")
                        .executes(ctx -> giveAllResearch(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"))))
                    .then(Commands.literal("list")
                        .executes(ctx -> listAllResearch(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"))))
                    .then(Commands.literal("reset")
                        .executes(ctx -> resetResearch(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"))))
                    .then(Commands.literal("revoke")
                        .then(Commands.argument("key", StringArgumentType.word())
                            .executes(ctx -> revokeResearch(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), StringArgumentType.getString(ctx, "key")))))
                    .then(Commands.argument("key", StringArgumentType.word())
                        .executes(ctx -> giveResearch(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), StringArgumentType.getString(ctx, "key"))))))
            .then(Commands.literal("warp")
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.literal("add")
                        .then(Commands.argument("amount", IntegerArgumentType.integer(-100, 100))
                            .executes(ctx -> addWarp(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "amount"), ""))
                            .then(Commands.argument("type", StringArgumentType.word())
                                .executes(ctx -> addWarp(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "amount"), StringArgumentType.getString(ctx, "type"))))))
                    .then(Commands.literal("set")
                        .then(Commands.argument("amount", IntegerArgumentType.integer())
                            .executes(ctx -> setWarp(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "amount"), ""))
                            .then(Commands.argument("type", StringArgumentType.word())
                                .executes(ctx -> setWarp(ctx.getSource(), EntityArgument.getPlayer(ctx, "player"), IntegerArgumentType.getInteger(ctx, "amount"), StringArgumentType.getString(ctx, "type"))))))))
        );
        // Aliases
        dispatcher.register(Commands.literal("tc").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .redirect(dispatcher.getRoot().getChild("thaumcraft")));
        dispatcher.register(Commands.literal("thaum").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
            .redirect(dispatcher.getRoot().getChild("thaumcraft")));
    }

    private static int help(CommandSourceStack src) {
        src.sendSuccess(() -> Component.literal("§3/thaumcraft research <player> all  — give all research"), false);
        src.sendSuccess(() -> Component.literal("§3/thaumcraft research <player> reset — reset research"), false);
        src.sendSuccess(() -> Component.literal("§3/thaumcraft research <player> <key> — give specific research"), false);
        src.sendSuccess(() -> Component.literal("§3/thaumcraft research <player> revoke <key> — revoke research"), false);
        src.sendSuccess(() -> Component.literal("§3/thaumcraft warp <player> add|set <amount> [PERM|TEMP]"), false);
        src.sendSuccess(() -> Component.literal("§3/thaumcraft reload — reload research JSON"), false);
        return 1;
    }

    private static int reload(CommandSourceStack src) {
        for (ResearchCategory rc : ResearchCategories.researchCategories.values()) {
            rc.research.clear();
        }
        ResearchManager.parseAllResearch();
        src.sendSuccess(() -> Component.literal("§5Research reloaded."), false);
        return 1;
    }

    private static int listResearch(CommandSourceStack src) {
        for (ResearchCategory cat : ResearchCategories.researchCategories.values()) {
            for (ResearchEntry ri : cat.research.values()) {
                src.sendSuccess(() -> Component.literal("§5" + ri.getKey()), false);
            }
        }
        return 1;
    }

    private static int listAllResearch(CommandSourceStack src, ServerPlayer player) {
        StringBuilder sb = new StringBuilder();
        for (String key : ThaumcraftCapabilities.getKnowledge(player).getResearchList()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(key);
        }
        final String name = player.getName().getString();
        final String list = sb.toString();
        src.sendSuccess(() -> Component.literal("§5Research for " + name + ": " + list), false);
        return 1;
    }

    private static int giveResearch(CommandSourceStack src, ServerPlayer player, String research) {
        if (ResearchCategories.getResearch(research) == null) {
            src.sendFailure(Component.literal("§cUnknown research: " + research));
            return 0;
        }
        giveRecursiveResearch(player, research);
        ThaumcraftCapabilities.getKnowledge(player).sync(player);
        src.sendSuccess(() -> Component.literal("§5Gave " + research + " to " + player.getName().getString()), false);
        return 1;
    }

    private static int giveAllResearch(CommandSourceStack src, ServerPlayer player) {
        for (ResearchCategory cat : ResearchCategories.researchCategories.values()) {
            for (ResearchEntry ri : cat.research.values()) {
                giveRecursiveResearch(player, ri.getKey());
            }
        }
        ThaumcraftCapabilities.getKnowledge(player).sync(player);
        src.sendSuccess(() -> Component.literal("§5Gave all research to " + player.getName().getString()), false);
        return 1;
    }

    private static int resetResearch(CommandSourceStack src, ServerPlayer player) {
        ThaumcraftCapabilities.getKnowledge(player).clear();
        for (ResearchCategory cat : ResearchCategories.researchCategories.values()) {
            for (ResearchEntry ri : cat.research.values()) {
                if (ri.hasMeta(ResearchEntry.EnumResearchMeta.AUTOUNLOCK)) {
                    ResearchManager.completeResearch(player, ri.getKey(), false);
                }
            }
        }
        ThaumcraftCapabilities.getKnowledge(player).sync(player);
        src.sendSuccess(() -> Component.literal("§5Reset research for " + player.getName().getString()), false);
        return 1;
    }

    private static int revokeResearch(CommandSourceStack src, ServerPlayer player, String research) {
        if (ResearchCategories.getResearch(research) == null) {
            src.sendFailure(Component.literal("§cUnknown research: " + research));
            return 0;
        }
        revokeRecursiveResearch(player, research);
        ThaumcraftCapabilities.getKnowledge(player).sync(player);
        src.sendSuccess(() -> Component.literal("§5Revoked " + research + " from " + player.getName().getString()), false);
        return 1;
    }

    private static int setWarp(CommandSourceStack src, ServerPlayer player, int amount, String type) {
        if (type.equalsIgnoreCase("PERM")) ThaumcraftCapabilities.getWarp(player).set(IPlayerWarp.EnumWarpType.PERMANENT, amount);
        else if (type.equalsIgnoreCase("TEMP")) ThaumcraftCapabilities.getWarp(player).set(IPlayerWarp.EnumWarpType.TEMPORARY, amount);
        else ThaumcraftCapabilities.getWarp(player).set(IPlayerWarp.EnumWarpType.NORMAL, amount);
        ThaumcraftCapabilities.getWarp(player).sync(player);
        src.sendSuccess(() -> Component.literal("§5Set warp to " + amount + " for " + player.getName().getString()), false);
        return 1;
    }

    private static int addWarp(CommandSourceStack src, ServerPlayer player, int amount, String type) {
        if (type.equalsIgnoreCase("PERM")) ThaumcraftCapabilities.getWarp(player).add(IPlayerWarp.EnumWarpType.PERMANENT, amount);
        else if (type.equalsIgnoreCase("TEMP")) ThaumcraftCapabilities.getWarp(player).add(IPlayerWarp.EnumWarpType.TEMPORARY, amount);
        else ThaumcraftCapabilities.getWarp(player).add(IPlayerWarp.EnumWarpType.NORMAL, amount);
        ThaumcraftCapabilities.getWarp(player).sync(player);
        PacketHandler.sendToPlayer(new PacketWarpMessage(player, (byte)0, amount), player);
        src.sendSuccess(() -> Component.literal("§5Added " + amount + " warp to " + player.getName().getString()), false);
        return 1;
    }

    // -----------------------------------------------------------------------
    // Static helpers used by ItemThaumonomicon and elsewhere
    // -----------------------------------------------------------------------

    public static void giveRecursiveResearch(Player player, String research) {
        if (research.contains("@")) research = research.substring(0, research.indexOf("@"));
        ResearchEntry res = ResearchCategories.getResearch(research);
        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        if (!knowledge.isResearchComplete(research)) {
            if (res != null && res.getParents() != null) {
                for (String rsi : res.getParentsStripped()) giveRecursiveResearch(player, rsi);
            }
            if (res != null && res.getStages() != null) {
                for (ResearchStage page : res.getStages()) {
                    if (page.getResearch() != null) {
                        for (String gr : page.getResearch()) ResearchManager.completeResearch(player, gr);
                    }
                }
            }
            ResearchManager.completeResearch(player, research);
            for (String rc : ResearchCategories.researchCategories.keySet()) {
                for (ResearchEntry ri : ResearchCategories.getResearchCategory(rc).research.values()) {
                    if (ri.getStages() != null) {
                        for (ResearchStage stage : ri.getStages()) {
                            if (stage.getResearch() != null && Arrays.asList(stage.getResearch()).contains(research)) {
                                ThaumcraftCapabilities.getKnowledge(player).setResearchFlag(ri.getKey(), IPlayerKnowledge.EnumResearchFlag.PAGE);
                                break;
                            }
                        }
                    }
                }
            }
            if (res != null && res.getSiblings() != null) {
                for (String rsi : res.getSiblings()) giveRecursiveResearch(player, rsi);
            }
        }
    }

    public static void revokeRecursiveResearch(Player player, String research) {
        if (research.contains("@")) research = research.substring(0, research.indexOf("@"));
        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        if (knowledge.isResearchComplete(research)) {
            for (String rc : ResearchCategories.researchCategories.keySet()) {
                for (ResearchEntry ri : ResearchCategories.getResearchCategory(rc).research.values()) {
                    if (ri != null && ri.getParents() != null && knowledge.isResearchComplete(ri.getKey())) {
                        for (String rsi : ri.getParentsStripped()) {
                            if (rsi.equals(research)) revokeRecursiveResearch(player, ri.getKey());
                        }
                    }
                }
            }
            ThaumcraftCapabilities.getKnowledge(player).removeResearch(research);
        }
    }
}
