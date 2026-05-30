package thaumcraft.common.lib.events;
// baubles import removed
import java.util.List;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.client.resources.language.I18n;
// FML FMLCommonHandler removed
import net.neoforged.neoforge.network.handling.IPayloadContext;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.potions.PotionVisExhaust;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;
import thaumcraft.common.entities.monster.EntityMindSpider;
import thaumcraft.common.entities.monster.cult.EntityCultistPortalLesser;
import thaumcraft.common.items.armor.ItemFortressArmor;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.misc.PacketMiscEvent;
import thaumcraft.common.lib.potions.PotionBlurredVision;
import thaumcraft.common.lib.potions.PotionDeathGaze;
import thaumcraft.common.lib.potions.PotionInfectiousVisExhaust;
import thaumcraft.common.lib.potions.PotionSunScorned;
import thaumcraft.common.lib.potions.PotionThaumarhia;
import thaumcraft.common.lib.potions.PotionUnnaturalHunger;
import thaumcraft.common.lib.utils.EntityUtils;


public class WarpEvents
{
    public static void checkWarpEvent(Player player) {
        IPlayerWarp wc = ThaumcraftCapabilities.getWarp(player);
        ThaumcraftApi.internalMethods.addWarpToPlayer(player, -1, IPlayerWarp.EnumWarpType.TEMPORARY);
        int tw = wc.get(IPlayerWarp.EnumWarpType.TEMPORARY);
        int nw = wc.get(IPlayerWarp.EnumWarpType.NORMAL);
        int pw = wc.get(IPlayerWarp.EnumWarpType.PERMANENT);
        int warp = tw + nw + pw;
        int actualwarp = pw + nw;
        int gearWarp = getWarpFromGear(player);
        warp += gearWarp;
        int warpCounter = wc.getCounter();
        int r = player.level().getRandom().nextInt(100);
        if (warpCounter > 0 && warp > 0 && r <= Math.sqrt(warpCounter)) {
            warp = Math.min(100, (warp + warp + warpCounter) / 3);
            warpCounter -= (int)Math.max(5.0, Math.sqrt(warpCounter) * 2.0 - gearWarp * 2);
            wc.setCounter(warpCounter);
            int eff = player.level().getRandom().nextInt(warp) + gearWarp;
            ItemStack helm = player.getInventory().getItem(36 + 3);
            if (helm.getItem() instanceof ItemFortressArmor && !helm.isEmpty() && helm.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("mask") && helm.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getIntOr("mask", 0) == 0) {
                eff -= 2 + player.level().getRandom().nextInt(4);
            }
            net.neoforged.neoforge.network.PacketDistributor.sendToPlayer((net.minecraft.server.level.ServerPlayer)player, new PacketMiscEvent((byte)0));
            if (eff > 0) {
                if (eff <= 4) {
                    if (!ModConfig.CONFIG_GRAPHICS.nostress) {
                        player.level().playSound(player, player.blockPosition(), SoundEvents.CREEPER_PRIMED, SoundSource.AMBIENT, 1.0f, 0.5f);
                    }
                }
                else if (eff <= 8) {
                    if (!ModConfig.CONFIG_GRAPHICS.nostress) {
                        player.level().playSound(player, player.getX() + (player.level().getRandom().nextFloat() - player.level().getRandom().nextFloat()) * 10.0f, player.getY() + (player.level().getRandom().nextFloat() - player.level().getRandom().nextFloat()) * 10.0f, player.getZ() + (player.level().getRandom().nextFloat() - player.level().getRandom().nextFloat()) * 10.0f, SoundEvents.GENERIC_EXPLODE, SoundSource.AMBIENT, 4.0f, (1.0f + (player.level().getRandom().nextFloat() - player.level().getRandom().nextFloat()) * 0.2f) * 0.7f);
                    }
                }
                else if (eff <= 12) {
                    player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("§5§o" + I18n.get("warp.text.11")));
                }
                else if (eff <= 16) {
                    MobEffectInstance pe = new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionVisExhaust.instance), 5000, Math.min(3, warp / 15), true, true);
                    try {
                        player.addEffect(pe);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("§5§o" + I18n.get("warp.text.1")));
                }
                else if (eff <= 20) {
                    MobEffectInstance pe = new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionThaumarhia.instance), Math.min(32000, 10 * warp), 0, true, true);
                    try {
                        player.addEffect(pe);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("§5§o" + I18n.get("warp.text.15")));
                }
                else if (eff <= 24) {
                    MobEffectInstance pe = new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionUnnaturalHunger.instance), 5000, Math.min(3, warp / 15), true, true);
                    try {
                        player.addEffect(pe);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("§5§o" + I18n.get("warp.text.2")));
                }
                else if (eff <= 28) {
                    player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("§5§o" + I18n.get("warp.text.12")));
                }
                else if (eff <= 32) {
                    spawnMist(player, warp, 1);
                }
                else if (eff <= 36) {
                    try {
                        player.addEffect(new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionBlurredVision.instance), Math.min(32000, 10 * warp), 0, true, true));
                    }
                    catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                else if (eff <= 40) {
                    MobEffectInstance pe = new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionSunScorned.instance), 5000, Math.min(3, warp / 15), true, true);
                    try {
                        player.addEffect(pe);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("§5§o" + I18n.get("warp.text.5")));
                }
                else if (eff <= 44) {
                    try {
                        player.addEffect(new MobEffectInstance(MobEffects.MINING_FATIGUE, 1200, Math.min(3, warp / 15), true, true));
                    }
                    catch (Exception e2) {
                        e2.printStackTrace();
                    }
                    player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("§5§o" + I18n.get("warp.text.9")));
                }
                else if (eff <= 48) {
                    MobEffectInstance pe = new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionInfectiousVisExhaust.instance), 6000, Math.min(3, warp / 15));
                    try {
                        player.addEffect(pe);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("§5§o" + I18n.get("warp.text.1")));
                }
                else if (eff <= 52) {
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, Math.min(40 * warp, 6000), 0, true, true));
                    player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("§5§o" + I18n.get("warp.text.10")));
                }
                else if (eff <= 56) {
                    MobEffectInstance pe = new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionDeathGaze.instance), 6000, Math.min(3, warp / 15), true, true);
                    try {
                        player.addEffect(pe);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("§5§o" + I18n.get("warp.text.4")));
                }
                else if (eff <= 60) {
                    suddenlySpiders(player, warp, false);
                }
                else if (eff <= 64) {
                    player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("§5§o" + I18n.get("warp.text.13")));
                }
                else if (eff <= 68) {
                    spawnMist(player, warp, warp / 30);
                }
                else if (eff <= 72) {
                    try {
                        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, Math.min(32000, 5 * warp), 0, true, true));
                    }
                    catch (Exception e2) {
                        e2.printStackTrace();
                    }
                }
                else if (eff == 76) {
                    if (nw > 0) {
                        ThaumcraftApi.internalMethods.addWarpToPlayer(player, -1, IPlayerWarp.EnumWarpType.NORMAL);
                    }
                    player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("§5§o" + I18n.get("warp.text.14")));
                }
                else if (eff <= 80) {
                    MobEffectInstance pe = new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionUnnaturalHunger.instance), 6000, Math.min(3, warp / 15), true, true);
                    try {
                        player.addEffect(pe);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("§5§o" + I18n.get("warp.text.2")));
                }
                else if (eff <= 88) {
                    spawnPortal(player);
                }
                else if (eff <= 92) {
                    suddenlySpiders(player, warp, true);
                }
                else {
                    spawnMist(player, warp, warp / 15);
                }
            }
            if (actualwarp > 10 && !ThaumcraftCapabilities.knowsResearch(player, "BATHSALTS") && !ThaumcraftCapabilities.knowsResearch(player, "!BATHSALTS")) {
                player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("§5§o" + I18n.get("warp.text.8")));
                ThaumcraftApi.internalMethods.completeResearch(player, "!BATHSALTS");
            }
            if (actualwarp > 25 && !ThaumcraftCapabilities.knowsResearch(player, "ELDRITCHMINOR")) {
                ThaumcraftApi.internalMethods.completeResearch(player, "ELDRITCHMINOR");
            }
            if (actualwarp > 50 && !ThaumcraftCapabilities.knowsResearch(player, "ELDRITCHMAJOR")) {
                ThaumcraftApi.internalMethods.completeResearch(player, "ELDRITCHMAJOR");
            }
        }
    }

    private static void spawnMist(Player player, int warp, int guardian) {
        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer((net.minecraft.server.level.ServerPlayer)player, new PacketMiscEvent((byte)1));
        if (guardian > 0) {
            guardian = Math.min(8, guardian);
            for (int a = 0; a < guardian; ++a) {
                spawnGuardian(player);
            }
        }
        player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("§5§o" + I18n.get("warp.text.6")));
    }

    private static void spawnPortal(Player player) {
        if (!(player.level() instanceof ServerLevel sl)) return;
        EntityCultistPortalLesser eg = new EntityCultistPortalLesser(null, sl);
        int i = Mth.floor(player.getX());
        int j = Mth.floor(player.getY());
        int k = Mth.floor(player.getZ());
        for (int l = 0; l < 50; ++l) {
            int i2 = i + Mth.randomBetweenInclusive(sl.getRandom(), 7, 24) * Mth.randomBetweenInclusive(sl.getRandom(), -1, 1);
            int j2 = j + Mth.randomBetweenInclusive(sl.getRandom(), 7, 24) * Mth.randomBetweenInclusive(sl.getRandom(), -1, 1);
            int k2 = k + Mth.randomBetweenInclusive(sl.getRandom(), 7, 24) * Mth.randomBetweenInclusive(sl.getRandom(), -1, 1);
            eg.setPos(i2 + 0.5, j2 + 1.0, k2 + 0.5);
            BlockPos groundPos = new BlockPos(i2, j2 - 1, k2);
            if (sl.getBlockState(groundPos).isSolid()
                    && sl.noCollision(eg, eg.getBoundingBox())
                    && !sl.isFluidAtPosition(new BlockPos(i2, j2, k2), fs -> !fs.isEmpty())) {
                eg.finalizeSpawn(sl, sl.getCurrentDifficultyAt(eg.blockPosition()), EntitySpawnReason.MOB_SUMMONED, null);
                sl.addFreshEntity(eg);
                player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("§5§o" + I18n.get("warp.text.16")));
                break;
            }
        }
    }

    private static void spawnGuardian(Player player) {
        if (!(player.level() instanceof ServerLevel sl)) return;
        EntityEldritchGuardian eg = new EntityEldritchGuardian(null, sl);
        int i = Mth.floor(player.getX());
        int j = Mth.floor(player.getY());
        int k = Mth.floor(player.getZ());
        for (int l = 0; l < 50; ++l) {
            int i2 = i + Mth.randomBetweenInclusive(sl.getRandom(), 7, 24) * Mth.randomBetweenInclusive(sl.getRandom(), -1, 1);
            int j2 = j + Mth.randomBetweenInclusive(sl.getRandom(), 7, 24) * Mth.randomBetweenInclusive(sl.getRandom(), -1, 1);
            int k2 = k + Mth.randomBetweenInclusive(sl.getRandom(), 7, 24) * Mth.randomBetweenInclusive(sl.getRandom(), -1, 1);
            if (sl.getBlockState(new BlockPos(i2, j2 - 1, k2)).isCollisionShapeFullBlock(sl, new BlockPos(i2, j2 - 1, k2))) {
                eg.setPos(i2, j2, k2);
                if (sl.noCollision(eg, eg.getBoundingBox())) {
                    eg.setTarget(player);
                    sl.addFreshEntity(eg);
                    break;
                }
            }
        }
    }

    private static void suddenlySpiders(Player player, int warp, boolean real) {
        if (!(player.level() instanceof ServerLevel sl)) return;
        for (int spawns = Math.min(50, warp), a = 0; a < spawns; ++a) {
            EntityMindSpider spider = new EntityMindSpider(null, sl);
            int i = Mth.floor(player.getX());
            int j = Mth.floor(player.getY());
            int k = Mth.floor(player.getZ());
            boolean success = false;
            for (int l = 0; l < 50; ++l) {
                int i2 = i + Mth.randomBetweenInclusive(sl.getRandom(), 7, 24) * Mth.randomBetweenInclusive(sl.getRandom(), -1, 1);
                int j2 = j + Mth.randomBetweenInclusive(sl.getRandom(), 7, 24) * Mth.randomBetweenInclusive(sl.getRandom(), -1, 1);
                int k2 = k + Mth.randomBetweenInclusive(sl.getRandom(), 7, 24) * Mth.randomBetweenInclusive(sl.getRandom(), -1, 1);
                if (sl.getBlockState(new BlockPos(i2, j2 - 1, k2)).isCollisionShapeFullBlock(sl, new BlockPos(i2, j2 - 1, k2))) {
                    spider.setPos(i2, j2, k2);
                    if (sl.noCollision(spider, spider.getBoundingBox())) {
                        success = true;
                        break;
                    }
                }
            }
            if (success) {
                spider.setTarget(player);
                if (!real) {
                    spider.setViewer(player.getName().getString());
                    spider.setHarmless(true);
                }
                sl.addFreshEntity(spider);
            }
        }
        player.sendOverlayMessage(net.minecraft.network.chat.Component.literal("§5§o" + I18n.get("warp.text.7")));
    }

    public static void checkDeathGaze(Player player) {
        MobEffectInstance pe = player.getEffect(net.minecraft.core.Holder.direct(PotionDeathGaze.instance));
        if (pe == null) {
            return;
        }
        int level = pe.getAmplifier();
        int range = Math.min(8 + level * 3, 24);
        // getEntities(excludedEntity, aabb, predicate) replaces getEntitiesWithinAABBExcludingEntity
        List<Entity> list = player.level().getEntities(player, player.getBoundingBox().inflate(range, range, range), e -> true);
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (entity.isPickable() && entity instanceof LivingEntity) {
                if (entity.isAlive()) {
                    if (EntityUtils.isVisibleTo(0.75f, player, entity, (float)range)) {
                        // Player does not have getSensing(); use LivingEntity.hasLineOfSight instead
                        if (entity != null && player.hasLineOfSight(entity) && !(entity instanceof Player) && !((LivingEntity)entity).hasEffect(MobEffects.WITHER)) {
                            ((LivingEntity)entity).setLastHurtByMob(player);
                            ((LivingEntity)entity).setLastHurtMob(player);
                            if (entity instanceof PathfinderMob) {
                                ((PathfinderMob)entity).setTarget(player);
                            }
                            ((LivingEntity)entity).addEffect(new MobEffectInstance(MobEffects.WITHER, 80));
                        }
                    }
                }
            }
        }
    }

    private static int getWarpFromGear(Player player) {
        int w = PlayerEvents.getFinalWarp(player.getMainHandItem(), player);
        for (int a = 0; a < 4; ++a) {
            w += PlayerEvents.getFinalWarp(player.getInventory().getItem(36 + a), player);
        }
        // Baubles API removed; no bauble warp contribution
        return w;
    }
}
