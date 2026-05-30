package thaumcraft.common.golems;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.LongTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.IGolemProperties;
import thaumcraft.api.golems.ISealDisplayer;
import thaumcraft.api.golems.parts.GolemArm;
import thaumcraft.api.golems.parts.GolemHead;
import thaumcraft.api.golems.parts.GolemMaterial;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.ItemTCBase;
import net.minecraft.world.item.TooltipFlag;


public class ItemGolemPlacer extends ItemTCBase implements ISealDisplayer
{
    public ItemGolemPlacer() {
        super("golem");
    }
    
    
    public void getSubItems(CreativeModeTab tab, NonNullList<ItemStack> items) {
        if (tab == ConfigItems.TABTC || tab == null /* SEARCH tab removed */) {
            ItemStack is = new ItemStack(this, 1);
            net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, is, t -> t.putLong("props", 0L));
            items.add(is.copy());
            IGolemProperties props = new GolemProperties();
            props.setHead(GolemHead.getHeads()[1]);
            props.setArms(GolemArm.getArms()[1]);
            net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, is, t -> t.putLong("props", props.toLong()));
            items.add(is.copy());
            props = new GolemProperties();
            props.setMaterial(GolemMaterial.getMaterials()[1]);
            props.setHead(GolemHead.getHeads()[1]);
            props.setArms(GolemArm.getArms()[2]);
            net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, is, t -> t.putLong("props", props.toLong()));
            items.add(is.copy());
            props = new GolemProperties();
            props.setMaterial(GolemMaterial.getMaterials()[4]);
            props.setHead(GolemHead.getHeads()[1]);
            props.setArms(GolemArm.getArms()[3]);
            net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, is, t -> t.putLong("props", props.toLong()));
            items.add(is.copy());
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext context, java.util.List<net.minecraft.network.chat.Component> tooltip, TooltipFlag flagIn) {
        if (!stack.isEmpty() && stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("props")) {
            IGolemProperties props = GolemProperties.fromLong(stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getLongOr("props", 0L));
            if (props.hasTrait(EnumGolemTrait.SMART)) {
                if (props.getRank() >= 10) {
                    tooltip.add(net.minecraft.network.chat.Component.literal("§6" + I18n.get("golem.rank") + " " + props.getRank()));
                }
                else {
                    int rx = stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getIntOr("xp", 0);
                    int xn = (props.getRank() + 1) * (props.getRank() + 1) * 1000;
                    tooltip.add(net.minecraft.network.chat.Component.literal("§6" + I18n.get("golem.rank") + " " + props.getRank() + " §2(" + rx + "/" + xn + ")"));
                }
            }
            tooltip.add(net.minecraft.network.chat.Component.literal("§a" + props.getMaterial().getLocalizedName()));
            for (EnumGolemTrait tag : props.getTraits()) {
                tooltip.add(net.minecraft.network.chat.Component.literal("§9-" + tag.getLocalizedName()));
            }
        }
        super.appendHoverText(stack, context, tooltip, flagIn);
    }
    
    /* TODO: port to useOn(UseOnContext)
    public InteractionResult onItemUseFirst_TODO(Player player, Level world, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, InteractionHand hand) {
        BlockState bs = world.getBlockState(pos);
        if (!bs.isSolid()) {
            return InteractionResult.FAIL;
        }
        if (world.isClientSide()) {
            return InteractionResult.PASS;
        }
        pos = pos.relative(side);
        bs = world.getBlockState(pos);
        if (!player.mayUseItemAt(pos, side, player.getItemInHand(hand))) {
            return InteractionResult.FAIL;
        }
        EntityThaumcraftGolem golem = new EntityThaumcraftGolem(null, world);
        golem.setPositionAndRotation(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0f, 0.0f);
        if (golem != null && world.addFreshEntity(golem)) {
            golem.setOwned(true);
            golem.setValidSpawn();
            golem.setOwnerId(player.getUUID());
            if (!player.getItemInHand(hand).getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().isEmpty() && player.getItemInHand(hand).getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("props")) {
                golem.setProperties(GolemProperties.fromLong(player.getItemInHand(hand).getLongOr("props", 0L)));
            }
            if (!player.getItemInHand(hand).getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().isEmpty() && player.getItemInHand(hand).getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("xp")) {
                golem.rankXp = player.getItemInHand(hand).getIntOr("xp", 0);
            }
            golem.finalizeSpawn((world instanceof net.minecraft.server.level.ServerLevel ? ((net.minecraft.server.level.ServerLevel)world).getCurrentDifficultyAt(pos) : new net.minecraft.world.DifficultyInstance(net.minecraft.world.Difficulty.NORMAL, 0L, 0L, 0.0f)), net.minecraft.world.entity.MobSpawnType.MOB_SUMMONED, null);
            if (!player.getAbilities().instabuild) {
                player.getItemInHand(hand).shrink(1);
            }
        }
        return InteractionResult.SUCCESS;
    }
    */
}
