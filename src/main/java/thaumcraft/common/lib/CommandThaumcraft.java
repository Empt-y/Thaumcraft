package thaumcraft.common.lib;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.network.handling.IPayloadContext;
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
    private List aliases;

    public CommandThaumcraft() {
        (aliases = new ArrayList()).add("thaumcraft");
        aliases.add("thaum");
        aliases.add("tc");
    }

    public String getName() {
        return "thaumcraft";
    }

    public List<String> getAliases() {
        return aliases;
    }

    public String getUsage(CommandSourceStack icommandsender) {
        return "/thaumcraft <action> [<player> [<params>]]";
    }

    public int getRequiredPermissionLevel() {
        return 2;
    }

    public boolean isUsernameIndex(String[] astring, int i) {
        return i == 1;
    }

    public void execute(MinecraftServer server, CommandSourceStack sender, String[] args) {
        if (args.length == 0) {
            sender.sendFailure(Component.literal("§cInvalid arguments"));
            sender.sendFailure(Component.literal("§cUse /thaumcraft help to get help"));
            return;
        }
        if (args[0].equalsIgnoreCase("reload")) {
            for (ResearchCategory rc : ResearchCategories.researchCategories.values()) {
                rc.research.clear();
            }
            ResearchManager.parseAllResearch();
            sender.sendSuccess(() -> Component.literal("§5Success!"), false);
        }
        else if (args[0].equalsIgnoreCase("help")) {
            sender.sendSuccess(() -> Component.literal("§3You can also use /thaum or /tc instead of /thaumcraft."), false);
            sender.sendSuccess(() -> Component.literal("§3Use this to give research to a player."), false);
            sender.sendSuccess(() -> Component.literal("  /thaumcraft research <list|player> <list|all|reset|<research>>"), false);
            sender.sendSuccess(() -> Component.literal("§3Use this to remove research from a player."), false);
            sender.sendSuccess(() -> Component.literal("  /thaumcraft research <player> revoke <research>"), false);
            sender.sendSuccess(() -> Component.literal("§3Use this to give set a players warp world."), false);
            sender.sendSuccess(() -> Component.literal("  /thaumcraft warp <player> <add|set> <amount> <PERM|TEMP>"), false);
            sender.sendSuccess(() -> Component.literal("  not specifying perm or temp will just add normal warp"), false);
            sender.sendSuccess(() -> Component.literal("§3Use this to reload json research data"), false);
            sender.sendSuccess(() -> Component.literal("  /thaumcraft reload"), false);
        }
        else if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("research") && args[1].equalsIgnoreCase("list")) {
                listResearch(sender);
            }
            else {
                net.minecraft.server.level.ServerPlayer entityplayermp = server.getPlayerList().getPlayerByName(args[1]);
                if (entityplayermp == null) {
                    sender.sendFailure(Component.literal("§cPlayer not found: " + args[1]));
                    return;
                }
                if (args[0].equalsIgnoreCase("research")) {
                    if (args.length == 3) {
                        if (args[2].equalsIgnoreCase("list")) {
                            listAllResearch(sender, entityplayermp);
                        }
                        else if (args[2].equalsIgnoreCase("all")) {
                            giveAllResearch(sender, entityplayermp);
                        }
                        else if (args[2].equalsIgnoreCase("reset")) {
                            resetResearch(sender, entityplayermp);
                        }
                        else {
                            giveResearch(sender, entityplayermp, args[2]);
                        }
                    }
                    else if (args.length == 4) {
                        if (args[2].equalsIgnoreCase("revoke")) {
                            revokeResearch(sender, entityplayermp, args[3]);
                        }
                    }
                    else {
                        sender.sendFailure(Component.literal("§cInvalid arguments"));
                        sender.sendFailure(Component.literal("§cUse /thaumcraft research <list|player> <list|all|reset|<research>>"));
                    }
                }
                else if (args[0].equalsIgnoreCase("warp")) {
                    if (args.length >= 4 && args[2].equalsIgnoreCase("set")) {
                        int i = parseIntSafe(args[3]);
                        setWarp(sender, entityplayermp, i, (args.length == 5) ? args[4] : "");
                    }
                    else if (args.length >= 4 && args[2].equalsIgnoreCase("add")) {
                        int i = Mth.clamp(parseIntSafe(args[3]), -100, 100);
                        addWarp(sender, entityplayermp, i, (args.length == 5) ? args[4] : "");
                    }
                    else {
                        sender.sendFailure(Component.literal("§cInvalid arguments"));
                        sender.sendFailure(Component.literal("§cUse /thaumcraft warp <player> <add|set> <amount> <PERM|TEMP>"));
                    }
                }
                else {
                    sender.sendFailure(Component.literal("§cInvalid arguments"));
                    sender.sendFailure(Component.literal("§cUse /thaumcraft help to get help"));
                }
            }
        }
        else {
            sender.sendFailure(Component.literal("§cInvalid arguments"));
            sender.sendFailure(Component.literal("§cUse /thaumcraft help to get help"));
        }
    }

    private static int parseIntSafe(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return 0; }
    }

    private void setWarp(CommandSourceStack icommandsender, net.minecraft.server.level.ServerPlayer player, int i, String type) {
        if (type.equalsIgnoreCase("PERM")) {
            ThaumcraftCapabilities.getWarp(player).set(IPlayerWarp.EnumWarpType.PERMANENT, i);
        }
        else if (type.equalsIgnoreCase("TEMP")) {
            ThaumcraftCapabilities.getWarp(player).set(IPlayerWarp.EnumWarpType.TEMPORARY, i);
        }
        else {
            ThaumcraftCapabilities.getWarp(player).set(IPlayerWarp.EnumWarpType.NORMAL, i);
        }
        ThaumcraftCapabilities.getWarp(player).sync(player);
        player.sendSystemMessage(Component.literal("§5" + icommandsender.getTextName() + " set your warp to " + i));
        icommandsender.sendSuccess(() -> Component.literal("§5Success!"), false);
    }

    private void addWarp(CommandSourceStack icommandsender, net.minecraft.server.level.ServerPlayer player, int i, String type) {
        if (type.equalsIgnoreCase("PERM")) {
            ThaumcraftCapabilities.getWarp(player).add(IPlayerWarp.EnumWarpType.PERMANENT, i);
        }
        else if (type.equalsIgnoreCase("TEMP")) {
            ThaumcraftCapabilities.getWarp(player).add(IPlayerWarp.EnumWarpType.TEMPORARY, i);
        }
        else {
            ThaumcraftCapabilities.getWarp(player).add(IPlayerWarp.EnumWarpType.NORMAL, i);
        }
        ThaumcraftCapabilities.getWarp(player).sync(player);
        PacketHandler.sendToPlayer(new PacketWarpMessage(player, null, i), player);
        player.sendSystemMessage(Component.literal("§5" + icommandsender.getTextName() + " added " + i + " warp to your total."));
        icommandsender.sendSuccess(() -> Component.literal("§5Success!"), false);
    }

    private void listResearch(CommandSourceStack icommandsender) {
        Collection<ResearchCategory> rc = ResearchCategories.researchCategories.values();
        for (ResearchCategory cat : rc) {
            Collection<ResearchEntry> rl = cat.research.values();
            for (ResearchEntry ri : rl) {
                icommandsender.sendSuccess(() -> Component.literal("§5" + ri.getKey()), false);
            }
        }
    }

    void giveResearch(CommandSourceStack icommandsender, net.minecraft.server.level.ServerPlayer player, String research) {
        if (ResearchCategories.getResearch(research) != null) {
            giveRecursiveResearch(player, research);
            ThaumcraftCapabilities.getKnowledge(player).sync(player);
            player.sendSystemMessage(Component.literal("§5" + icommandsender.getTextName() + " gave you " + research + " research and its requisites."));
            icommandsender.sendSuccess(() -> Component.literal("§5Success!"), false);
        }
        else {
            icommandsender.sendFailure(Component.literal("§cResearch does not exist."));
        }
    }

    public static void giveRecursiveResearch(Player player, String research) {
        if (research.contains("@")) {
            int i = research.indexOf("@");
            research = research.substring(0, i);
        }
        ResearchEntry res = ResearchCategories.getResearch(research);
        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        if (!knowledge.isResearchComplete(research)) {
            if (res != null && res.getParents() != null) {
                for (String rsi : res.getParentsStripped()) {
                    giveRecursiveResearch(player, rsi);
                }
            }
            if (res != null && res.getStages() != null) {
                for (ResearchStage page : res.getStages()) {
                    if (page.getResearch() != null) {
                        for (String gr : page.getResearch()) {
                            ResearchManager.completeResearch(player, gr);
                        }
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
                for (String rsi : res.getSiblings()) {
                    giveRecursiveResearch(player, rsi);
                }
            }
        }
    }

    private void revokeResearch(CommandSourceStack icommandsender, net.minecraft.server.level.ServerPlayer player, String research) {
        if (ResearchCategories.getResearch(research) != null) {
            revokeRecursiveResearch(player, research);
            ThaumcraftCapabilities.getKnowledge(player).sync(player);
            player.sendSystemMessage(Component.literal("§5" + icommandsender.getTextName() + " removed " + research + " research and its children."));
            icommandsender.sendSuccess(() -> Component.literal("§5Success!"), false);
        }
        else {
            icommandsender.sendFailure(Component.literal("§cResearch does not exist."));
        }
    }

    public static void revokeRecursiveResearch(Player player, String research) {
        if (research.contains("@")) {
            int i = research.indexOf("@");
            research = research.substring(0, i);
        }
        ResearchEntry res = ResearchCategories.getResearch(research);
        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        if (knowledge.isResearchComplete(research)) {
            for (String rc : ResearchCategories.researchCategories.keySet()) {
                for (ResearchEntry ri : ResearchCategories.getResearchCategory(rc).research.values()) {
                    if (ri != null && ri.getParents() != null && knowledge.isResearchComplete(ri.getKey())) {
                        for (String rsi : ri.getParentsStripped()) {
                            if (rsi.equals(research)) {
                                revokeRecursiveResearch(player, ri.getKey());
                            }
                        }
                    }
                }
            }
            ThaumcraftCapabilities.getKnowledge(player).removeResearch(research);
        }
    }

    void listAllResearch(CommandSourceStack icommandsender, net.minecraft.server.level.ServerPlayer player) {
        String ss = "";
        for (String key : ThaumcraftCapabilities.getKnowledge(player).getResearchList()) {
            if (ss.length() != 0) {
                ss += ", ";
            }
            ss += key;
        }
        final String playerName = player.getName().getString();
        final String researchList = ss;
        icommandsender.sendSuccess(() -> Component.literal("§5Research for " + playerName), false);
        icommandsender.sendSuccess(() -> Component.literal("§5" + researchList), false);
    }

    void giveAllResearch(CommandSourceStack icommandsender, net.minecraft.server.level.ServerPlayer player) {
        Collection<ResearchCategory> rc = ResearchCategories.researchCategories.values();
        for (ResearchCategory cat : rc) {
            Collection<ResearchEntry> rl = cat.research.values();
            for (ResearchEntry ri : rl) {
                giveRecursiveResearch(player, ri.getKey());
            }
        }
        player.sendSystemMessage(Component.literal("§5" + icommandsender.getTextName() + " has given you all research."));
        icommandsender.sendSuccess(() -> Component.literal("§5Success!"), false);
    }

    void resetResearch(CommandSourceStack icommandsender, net.minecraft.server.level.ServerPlayer player) {
        ThaumcraftCapabilities.getKnowledge(player).clear();
        Collection<ResearchCategory> rc = ResearchCategories.researchCategories.values();
        for (ResearchCategory cat : rc) {
            Collection<ResearchEntry> res = cat.research.values();
            for (ResearchEntry ri : res) {
                if (ri.hasMeta(ResearchEntry.EnumResearchMeta.AUTOUNLOCK)) {
                    ResearchManager.completeResearch(player, ri.getKey(), false);
                }
            }
        }
        player.sendSystemMessage(Component.literal("§5" + icommandsender.getTextName() + " has reset all your research."));
        icommandsender.sendSuccess(() -> Component.literal("§5Success!"), false);
        ThaumcraftCapabilities.getKnowledge(player).sync(player);
    }
}
