package thaumcraft.common.lib.crafting;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.IDustTrigger;
import thaumcraft.common.lib.events.ServerEvents;


public class DustTriggerOre implements IDustTrigger
{
    String target;
    ItemStack result;
    String research;

    public DustTriggerOre(String research, String target, ItemStack result) {
        this.target = target;
        this.result = result;
        this.research = research;
    }

    @Override
    public Placement getValidFace(Level world, Player player, BlockPos pos, Direction face) {
        if (research != null && !ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(research)) {
            return null;
        }
        BlockState bs = world.getBlockState(pos);
        try {
            // Convert old OreDict name to tag: "oreIron" -> "c:ores/iron"
            String tagPath = target.replaceAll("^ore", "ores/").toLowerCase();
            TagKey<Item> tag = TagKey.create(net.minecraft.core.registries.Registries.ITEM, Identifier.fromNamespaceAndPath("c", tagPath));
            ItemStack blockStack = new ItemStack(bs.getBlock());
            if (blockStack.is(tag)) {
                return new Placement(0, 0, 0, null);
            }
        } catch (Exception ex) {}
        return null;
    }

    @Override
    public void execute(Level world, Player player, BlockPos pos, Placement placement, Direction side) {
        BlockState state = world.getBlockState(pos);
        ServerEvents.addRunnableServer(world, new Runnable() {
            @Override
            public void run() {
                ServerEvents.addSwapper(world, pos, state, result, false, 0, player, true, true, -9999, false, false, 0, ServerEvents.DEFAULT_PREDICATE, 0.0f);
            }
        }, 50);
    }
}
