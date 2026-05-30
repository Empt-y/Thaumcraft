package thaumcraft.common.lib.events;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.bus.api.SubscribeEvent;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.EntityFollowingItem;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXScanSource;
import thaumcraft.common.lib.network.fx.PacketFXSlash;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.lib.utils.Utils;


@net.neoforged.fml.common.EventBusSubscriber(modid = "thaumcraft")
public class ToolEvents
{
    static HashMap<Integer, Direction> lastFaceClicked;
    public static HashMap<Integer, ArrayList<BlockPos>> blockedBlocks;
    static boolean blockDestructiveRecursion;

    @SubscribeEvent
    public static void playerAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player.getUsedItemHand() == null) {
            return;
        }
        ItemStack heldItem = player.getItemInHand(player.getUsedItemHand());
        if (heldItem != null && !heldItem.isEmpty()) {
            List<EnumInfusionEnchantment> list = EnumInfusionEnchantment.getInfusionEnchantments(heldItem);
            if (list.contains(EnumInfusionEnchantment.ARCING) && event.getTarget().isAlive()) {
                int rank = EnumInfusionEnchantment.getInfusionEnchantmentLevel(heldItem, EnumInfusionEnchantment.ARCING);
                List<Entity> targets = player.level().getEntitiesOfClass(
                    Entity.class,
                    event.getTarget().getBoundingBox().grow(1.5 + rank, 1.0f + rank / 2.0f, 1.5 + rank),
                    e -> true
                );
                int count = 0;
                if (targets.size() > 1) {
                    for (Entity var10 : targets) {
                        if (var10.isAlive() && !EntityUtils.isFriendly(player, var10)) {
                            if (var10 instanceof Mob && var10.getId() != event.getTarget().getId()) {
                                if (!(var10 instanceof Player) || !var10.getName().getString().equals(player.getName())) {
                                    float f = player.getAttribute(Attributes.ATTACK_DAMAGE) != null
                                        ? (float) player.getAttribute(Attributes.ATTACK_DAMAGE).getValue() : 1.0f;
                                    if (var10.hurt(player.level().damageSources().playerAttack(player), f * 0.5f)) {
                                        try {
                                            if (var10 instanceof LivingEntity le) {
                                                // Thorn enchantment application removed in 1.21 refactor
                                            }
                                        } catch (Exception ex) {}
                                        var10.push(
                                            -Mth.sin(player.getYRot() * 3.1415927f / 180.0f) * 0.5f,
                                            0.1,
                                            Mth.cos(player.getYRot() * 3.1415927f / 180.0f) * 0.5f
                                        );
                                        ++count;
                                    }
                                }
                            }
                            if (count >= rank) {
                                break;
                            }
                        }
                    }
                    if (count > 0 && !player.level().isClientSide()) {
                        player.playSound(SoundsTC.wind, 1.0f, 0.9f + player.getRandom().nextFloat() * 0.2f);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getLevel().isClientSide() && event.getEntity() != null) {
            Player player = event.getEntity();
            ItemStack heldItem = player.getItemInHand(
                player.getUsedItemHand() == null ? InteractionHand.MAIN_HAND : player.getUsedItemHand()
            );
            if (heldItem != null && !heldItem.isEmpty()) {
                List<EnumInfusionEnchantment> list = EnumInfusionEnchantment.getInfusionEnchantments(heldItem);
                if (list.contains(EnumInfusionEnchantment.SOUNDING) && player.isCrouching()) {
                    heldItem.hurtAndBreak(5, player, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
                    event.getLevel().playSound(null, event.getPos().x + 0.5, event.getPos().getY() + 0.5, event.getPos().z + 0.5, SoundsTC.wandfail, SoundSource.BLOCKS, 0.2f, 0.2f + event.getLevel().getRandom().nextFloat() * 0.2f);
                    if (player instanceof ServerPlayer sp) {
                        PacketHandler.sendToPlayer(
                            new PacketFXScanSource(event.getPos(), EnumInfusionEnchantment.getInfusionEnchantmentLevel(heldItem, EnumInfusionEnchantment.SOUNDING)),
                            sp
                        );
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerInteract(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getEntity() != null) {
            ToolEvents.lastFaceClicked.put(event.getEntity().getId(), event.getFace());
        }
    }

    public static void addBlockedBlock(Level world, BlockPos pos) {
        int dimKey = world instanceof net.minecraft.server.level.ServerLevel sl ? sl.dimension().identifier().hashCode() : 0;
        ToolEvents.blockedBlocks.computeIfAbsent(dimKey, k -> new ArrayList<>());
        ArrayList<BlockPos> list = ToolEvents.blockedBlocks.get(dimKey);
        if (!list.contains(pos)) {
            list.add(pos);
        }
    }

    public static void clearBlockedBlock(Level world, BlockPos pos) {
        int dimKey = world instanceof net.minecraft.server.level.ServerLevel sl ? sl.dimension().identifier().hashCode() : 0;
        ArrayList<BlockPos> list = ToolEvents.blockedBlocks.computeIfAbsent(dimKey, k -> new ArrayList<>());
        list.remove(pos);
    }

    @SubscribeEvent
    public static void breakBlockEvent(net.neoforged.neoforge.event.level.block.BreakBlockEvent event) {
        Level level = (Level) event.getLevel();
        int dimKey = level instanceof net.minecraft.server.level.ServerLevel sl ? sl.dimension().identifier().hashCode() : 0;
        ArrayList<BlockPos> blockedList = ToolEvents.blockedBlocks.get(dimKey);
        if (blockedList != null && blockedList.contains(event.getPos())) {
            event.setCanceled(true);
        }
        Player player = event.getEntity();
        if (!level.isClientSide() && player != null) {
            ItemStack heldItem = player.getItemInHand(player.getUsedItemHand());
            if (heldItem != null && !heldItem.isEmpty()) {
                List<EnumInfusionEnchantment> list2 = EnumInfusionEnchantment.getInfusionEnchantments(heldItem);
                if (list2.contains(EnumInfusionEnchantment.BURROWING) && !player.isCrouching() && isValidBurrowBlock(level, event.getPos())) {
                    event.setCanceled(true);
                    if (!player.getName().getString().equals("FakeThaumcraftBore")) {
                        heldItem.hurtAndBreak(1, player, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
                    }
                    BlockUtils.breakFurthestBlock(level, event.getPos(), event.getState(), player);
                }
            }
        }
    }

    private static boolean isValidBurrowBlock(Level world, BlockPos pos) {
        return Utils.isWoodLog(world, pos) || Utils.isOreBlock(world, pos);
    }

    @SubscribeEvent
    public static void harvestBlockEvent(net.neoforged.neoforge.event.level.BlockDropsEvent event) {
        Level level = event.getLevel();
        boolean isSilk = EnchantmentHelper.hasSilkTouch(event.getTool());
        // Vis nugget drop from ores
        if (!level.isClientSide() && !isSilk && event.getState().getBlock() != null) {
            boolean isVisOre = (event.getState().is(Blocks.DIAMOND_ORE) && level.getRandom().nextFloat() < 0.05f)
                || (event.getState().is(Blocks.EMERALD_ORE) && level.getRandom().nextFloat() < 0.075f)
                || (event.getState().is(Blocks.LAPIS_ORE) && level.getRandom().nextFloat() < 0.01f)
                || (event.getState().is(Blocks.COAL_ORE) && level.getRandom().nextFloat() < 0.001f)
                || (event.getState().is(Blocks.REDSTONE_ORE) && level.getRandom().nextFloat() < 0.01f)
                || (event.getState().is(BlocksTC.oreAmber) && level.getRandom().nextFloat() < 0.05f)
                || (event.getState().is(BlocksTC.oreQuartz) && level.getRandom().nextFloat() < 0.05f);
            if (isVisOre) {
                ItemStack nugget = new ItemStack(ItemsTC.nuggets, 1);
                event.getDrops().add(new ItemEntity(level, event.getPos().x + 0.5, event.getPos().getY() + 0.5, event.getPos().z + 0.5, nugget));
            }
        }
        // Infusion enchantment processing
        Entity breaker = event.getBreaker();
        if (!level.isClientSide() && breaker instanceof Player harvesterPlayer) {
            ItemStack heldItem = harvesterPlayer.getItemInHand(harvesterPlayer.getUsedItemHand());
            if (heldItem != null && !heldItem.isEmpty()) {
                List<EnumInfusionEnchantment> list = EnumInfusionEnchantment.getInfusionEnchantments(heldItem);
                boolean effectiveTool = isSilk || heldItem.getItem() instanceof net.minecraft.world.item.DiggerItem;
                if (effectiveTool) {
                    if (list.contains(EnumInfusionEnchantment.REFINING)) {
                        int fortune = 1 + EnumInfusionEnchantment.getInfusionEnchantmentLevel(heldItem, EnumInfusionEnchantment.REFINING);
                        float chance = fortune * 0.125f;
                        boolean b = false;
                        for (int a = 0; a < event.getDrops().size(); ++a) {
                            ItemStack is = event.getDrops().get(a).getItem();
                            ItemStack smr = Utils.findSpecialMiningResult(is, chance, level.getRandom());
                            if (!ItemStack.isSameItem(is, smr)) {
                                event.getDrops().get(a).setItem(smr);
                                b = true;
                            }
                        }
                        if (b) {
                            level.playSound(null, event.getPos(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.2f, 0.7f + level.getRandom().nextFloat() * 0.2f);
                        }
                    }
                    if (!ToolEvents.blockDestructiveRecursion && list.contains(EnumInfusionEnchantment.DESTRUCTIVE) && !harvesterPlayer.isCrouching()) {
                        ToolEvents.blockDestructiveRecursion = true;
                        Direction face = ToolEvents.lastFaceClicked.get(harvesterPlayer.getId());
                        if (face == null) face = Direction.NORTH;
                        for (int aa = -1; aa <= 1; ++aa) {
                            for (int bb = -1; bb <= 1; ++bb) {
                                if (aa != 0 || bb != 0) {
                                    int xx = 0, yy = 0, zz = 0;
                                    if (face.ordinal() <= 1) { xx = aa; zz = bb; }
                                    else if (face.ordinal() <= 3) { xx = aa; yy = bb; }
                                    else { zz = aa; yy = bb; }
                                    BlockPos adjPos = event.getPos().offset(xx, yy, zz);
                                    BlockState bl = level.getBlockState(adjPos);
                                    if (bl.getDestroySpeed(level, adjPos) >= 0.0f && heldItem.getItem() instanceof net.minecraft.world.item.DiggerItem) {
                                        if (!harvesterPlayer.getName().getString().equals("FakeThaumcraftBore")) {
                                            heldItem.hurtAndBreak(1, harvesterPlayer, net.minecraft.world.entity.EquipmentSlot.MAINHAND);
                                        }
                                        BlockUtils.harvestBlock(level, harvesterPlayer, adjPos);
                                    }
                                }
                            }
                        }
                        ToolEvents.blockDestructiveRecursion = false;
                    }
                    if (list.contains(EnumInfusionEnchantment.COLLECTOR) && !harvesterPlayer.isCrouching()) {
                        // Convert drops to following items and clear
                        List<ItemStack> stacks = new ArrayList<>();
                        for (ItemEntity ie : event.getDrops()) stacks.add(ie.getItem().copy());
                        event.getDrops().clear();
                        InventoryUtils.dropHarvestsAtPos(level, event.getPos(), stacks, true, 10, harvesterPlayer);
                    }
                    if (list.contains(EnumInfusionEnchantment.LAMPLIGHT) && !harvesterPlayer.isCrouching() && harvesterPlayer instanceof ServerPlayer) {
                        // Place glimmer light at broken block position (run next tick)
                        if (level instanceof net.minecraft.server.level.ServerLevel sl) {
                            sl.scheduleTick(event.getPos(), BlocksTC.effectGlimmer, 1);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void livingDrops(LivingDropsEvent event) {
        if (event.getSource().getEntity() != null && event.getSource().getEntity() instanceof Player player) {
            ItemStack heldItem = player.getItemInHand(player.getUsedItemHand());
            if (heldItem != null && !heldItem.isEmpty()) {
                List<EnumInfusionEnchantment> list = EnumInfusionEnchantment.getInfusionEnchantments(heldItem);
                if (list.contains(EnumInfusionEnchantment.COLLECTOR)) {
                    for (int a = 0; a < event.getDrops().size(); ++a) {
                        ItemEntity ei = event.getDrops().get(a);
                        ItemStack is = ei.getItem().copy();
                        ItemEntity nei = new EntityFollowingItem(event.getEntity().level(), ei.getX(), ei.getY(), ei.getZ(), is, player, 10);
                        nei.setDeltaMovement(ei.getDeltaMovement());
                        nei.setPickUpDelay(10);
                        ei.discard();
                        event.getDrops().set(a, nei);
                    }
                }
                if (list.contains(EnumInfusionEnchantment.ESSENCE)) {
                    AspectList as = AspectHelper.getEntityAspects(event.getEntity());
                    if (as != null && as.size() > 0) {
                        AspectList aspects = as.copy();
                        int q = EnumInfusionEnchantment.getInfusionEnchantmentLevel(heldItem, EnumInfusionEnchantment.ESSENCE);
                        Aspect[] al = aspects.getAspects();
                        for (int b = (event.getEntity().getRandom().nextInt(5) < q) ? 0 : 99; b < q && al != null && al.length > 0; b += 1 + event.getEntity().getRandom().nextInt(2)) {
                            Aspect aspect = al[event.getEntity().getRandom().nextInt(al.length)];
                            if (aspects.getAmount(aspect) > 0) {
                                aspects.remove(aspect, 1);
                                ItemStack stack = ThaumcraftApiHelper.makeCrystal(aspect);
                                double ex = player.getX(), ey = player.getY() + player.getEyeHeight(), ez = player.getZ();
                                if (list.contains(EnumInfusionEnchantment.COLLECTOR)) {
                                    event.getDrops().add(new EntityFollowingItem(player.level(), ex, ey, ez, stack, player, 10));
                                } else {
                                    event.getDrops().add(new ItemEntity(player.level(), ex, ey, ez, stack));
                                }
                                ++b;
                            }
                            al = aspects.getAspects();
                        }
                    }
                }
            }
        }
    }

    static {
        ToolEvents.lastFaceClicked = new HashMap<>();
        ToolEvents.blockedBlocks = new HashMap<>();
        ToolEvents.blockDestructiveRecursion = false;
    }
}
