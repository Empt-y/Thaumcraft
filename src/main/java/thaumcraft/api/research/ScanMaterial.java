package thaumcraft.api.research;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;


// TODO: Material was removed in 1.20; this class needs to be replaced with tag-based block scanning
public class ScanMaterial implements IScanThing {

    String research;
    TagKey<Block>[] tags;

    @SuppressWarnings("unchecked")
    public ScanMaterial(TagKey<Block> tag) {
        research = "!tag." + tag.location();
        this.tags = new TagKey[]{tag};
    }

    @SuppressWarnings("unchecked")
    public ScanMaterial(String research, TagKey<Block>... tags) {
        this.research = research;
        this.tags = tags;
    }

    @Override
    public boolean checkThing(Player player, Object obj) {
        if (obj instanceof BlockPos pos) {
            for (TagKey<Block> tag : tags) {
                if (player.level().getBlockState(pos).is(tag)) return true;
            }
        }
        return false;
    }

    @Override
    public String getResearchKey(Player player, Object object) {
        return research;
    }
}
