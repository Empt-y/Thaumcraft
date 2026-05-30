package thaumcraft.common.lib.events;
import net.minecraft.world.entity.ExperienceOrb;
import java.util.Iterator;
import java.util.UUID;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.hurtingprojectile.Fireball;
import net.minecraft.world.entity.projectile.LlamaSpit;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.item.ItemExpireEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.bus.api.SubscribeEvent;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.damagesource.DamageSourceThaumcraft;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.common.config.ConfigEntities;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;
import thaumcraft.common.entities.monster.EntityBrainyZombie;
import thaumcraft.common.entities.monster.boss.EntityThaumcraftBoss;
import thaumcraft.common.entities.monster.cult.EntityCultist;
import thaumcraft.common.entities.monster.mods.ChampionModTainted;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import thaumcraft.common.items.armor.ItemFortressArmor;
import thaumcraft.common.items.consumables.ItemBathSalts;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXShield;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.lib.utils.InventoryUtils;
import net.minecraft.world.entity.monster.zombie.Zombie;


@net.neoforged.fml.common.EventBusSubscriber(modid = "thaumcraft")
public class EntityEvents
{
    @SubscribeEvent
    public static void itemExpire(ItemExpireEvent event) {
        ItemEntity itemEntity = (ItemEntity) event.getEntity();
        if (!itemEntity.getItem().isEmpty() && itemEntity.getItem().getItem() instanceof ItemBathSalts) {
            BlockPos bp = itemEntity.blockPosition();
            BlockState bs = itemEntity.level().getBlockState(bp);
            if (bs.is(Blocks.WATER)) {
                itemEntity.level().setBlockAndUpdate(bp, BlocksTC.purifyingFluid.defaultBlockState());
            }
        }
    }

    @SubscribeEvent
    public static void livingTick(LivingEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof PathfinderMob mob && !entity.isDeadOrDying()) {
            AttributeInstance champAttr = mob.getAttribute(net.minecraft.core.Holder.direct(ThaumcraftApiHelper.CHAMPION_MOD));
            if (champAttr != null) {
                int t = (int) champAttr.getValue();
                try {
                    if (t >= 0 && t < ChampionModifier.mods.length && ChampionModifier.mods[t].type == 0) {
                        ChampionModifier.mods[t].effect.performEffect(mob, null, null, 0.0f);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (t >= ChampionModifier.mods.length) mob.discard();
                }
            }
        }
    }

    @SubscribeEvent
    public static void entityHurt(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getContainer().getSource();
        float amount = event.getContainer().getNewDamage();

        if (source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE) && entity instanceof Player player
                && ThaumcraftCapabilities.knowsResearchStrict(player, "BASEAUROMANCY@2")
                && !ThaumcraftCapabilities.knowsResearch(player, "f_onfire")) {
            IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
            knowledge.addResearch("f_onfire");
            knowledge.sync((ServerPlayer) player);
            player.sendOverlayMessage(Component.literal(ChatFormatting.DARK_PURPLE + I18n.get("got.onfire")));
        }

        if (source.getDirectEntity() != null && entity instanceof Player player
                && ThaumcraftCapabilities.knowsResearchStrict(player, "FOCUSPROJECTILE@2")) {
            IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
            if (!ThaumcraftCapabilities.knowsResearch(player, "f_arrow") && source.getDirectEntity() instanceof AbstractArrow) {
                knowledge.addResearch("f_arrow");
                knowledge.sync((ServerPlayer) player);
                player.sendOverlayMessage(Component.literal(ChatFormatting.DARK_PURPLE + I18n.get("got.projectile")));
            }
            if (!ThaumcraftCapabilities.knowsResearch(player, "f_fireball") && source.getDirectEntity() instanceof Fireball) {
                knowledge.addResearch("f_fireball");
                knowledge.sync((ServerPlayer) player);
                player.sendOverlayMessage(Component.literal(ChatFormatting.DARK_PURPLE + I18n.get("got.projectile")));
            }
            if (!ThaumcraftCapabilities.knowsResearch(player, "f_spit") && source.getDirectEntity() instanceof LlamaSpit) {
                knowledge.addResearch("f_spit");
                knowledge.sync((ServerPlayer) player);
                player.sendOverlayMessage(Component.literal(ChatFormatting.DARK_PURPLE + I18n.get("got.projectile")));
            }
        }

        if (source.getEntity() instanceof Player leecher) {
            ItemStack helm = leecher.getInventory().getItem(36 + 3);
            if (helm != null && !helm.isEmpty() && helm.getItem() instanceof ItemFortressArmor
                    && helm.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("mask")
                    && helm.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getIntOr("mask", 0) == 2
                    && leecher.getRandom().nextFloat() < amount / 12.0f) {
                leecher.heal(1.0f);
            }
        }

        if (entity instanceof Player player) {
            if (source.getEntity() instanceof LivingEntity attacker) {
                ItemStack helm2 = player.getInventory().getItem(36 + 3);
                if (helm2 != null && !helm2.isEmpty() && helm2.getItem() instanceof ItemFortressArmor
                        && helm2.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("mask")
                        && helm2.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getIntOr("mask", 0) == 1
                        && player.level().getRandom().nextFloat() < amount / 10.0f) {
                    try {
                        attacker.addEffect(new MobEffectInstance(MobEffects.WITHER, 80));
                    } catch (Exception ex) {}
                }
            }
        } else {
            if (!entity.level().isClientSide() && entity.getHealth() < 2.0f && !entity.isUndead() && !entity.isDeadOrDying()
                    && !(entity instanceof EntityOwnedConstruct) && !(entity instanceof ITaintedMob)
                    && entity.hasEffect(net.minecraft.core.Holder.direct(PotionFluxTaint.instance))
                    && entity.getRandom().nextBoolean()) {
                EntityUtils.makeTainted(entity);
                return;
            }
            if (entity instanceof Monster mob) {
                AttributeInstance cai = mob.getAttribute(net.minecraft.core.Holder.direct(ThaumcraftApiHelper.CHAMPION_MOD));
                if ((cai != null && cai.getValue() >= 0.0) || entity instanceof IEldritchMob) {
                    int t = (int) (cai != null ? cai.getValue() : 0.0);
                    if ((t == 5 || entity instanceof IEldritchMob) && mob.getAbsorptionAmount() > 0.0f) {
                        entity.playSound(SoundsTC.runicShieldCharge, 0.66f, 1.1f + entity.getRandom().nextFloat() * 0.1f);
                    } else if (cai != null && t >= 0 && t < ChampionModifier.mods.length
                            && ChampionModifier.mods[t].type == 2 && source.getEntity() instanceof LivingEntity attacker) {
                        event.getContainer().setNewDamage(ChampionModifier.mods[t].effect.performEffect(mob, attacker, source, amount));
                    }
                }
                if (amount > 0.0f && source.getEntity() instanceof Monster attacker) {
                    AttributeInstance attackerChamp = attacker.getAttribute(net.minecraft.core.Holder.direct(ThaumcraftApiHelper.CHAMPION_MOD));
                    if (attackerChamp != null && attackerChamp.getValue() >= 0.0) {
                        int t = (int) attackerChamp.getValue();
                        if (t >= 0 && t < ChampionModifier.mods.length && ChampionModifier.mods[t].type == 1) {
                            event.getContainer().setNewDamage(ChampionModifier.mods[t].effect.performEffect(attacker, entity, source, amount));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void itemPickup(ItemEntityPickupEvent.Pre event) {
        if (event.getEntity().getName().getString().startsWith("FakeThaumcraft")) {
            event.setCanPickup(net.minecraft.util.TriState.FALSE);
        }
    }

    @SubscribeEvent
    public static void entityConstuct(EntityEvent.EntityConstructing event) {
        // Champion attribute registration handled during entity attribute creation
    }

    @SubscribeEvent
    public static void livingDrops(LivingDropsEvent event) {
        LivingEntity entity = event.getEntity();
        boolean fakeplayer = event.getSource().getEntity() instanceof FakePlayer;
        if (!entity.level().isClientSide() && event.isRecentlyHit() && !fakeplayer
                && entity instanceof Monster && !(entity instanceof EntityThaumcraftBoss)) {
            AttributeInstance champAttr = ((Monster)entity).getAttribute(net.minecraft.core.Holder.direct(ThaumcraftApiHelper.CHAMPION_MOD));
            double champVal = champAttr != null ? champAttr.getValue() : -100.0;
            if (champVal >= 0.0 && champVal != 13.0) {
                int i = 5 + entity.getRandom().nextInt(3);
                while (i > 0) {
                    int j = ExperienceOrb.getExperienceValue(i);
                    i -= j;
                    entity.level().addFreshEntity(new ExperienceOrb(entity.level(), entity.getX(), entity.getY(), entity.getZ(), j));
                }
                event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ(), new ItemStack(ItemsTC.lootBag.asItem(), 1)));
            }
        }
        if (entity instanceof Zombie && !(entity instanceof EntityBrainyZombie) && event.isRecentlyHit()
                && entity.getRandom().nextInt(10) - event.getLootingLevel() < 1) {
            event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ(), new ItemStack(ItemsTC.brain)));
        }
        if (entity instanceof EntityCultist && !fakeplayer && event.getSource().getEntity() instanceof Player p) {
            int c = ThaumcraftCapabilities.getKnowledge(p).isResearchKnown("!CrimsonCultist@2") ? 20 : 4;
            if (InventoryUtils.getPlayerSlotFor(p, new ItemStack(ItemsTC.curio, 1)) >= 0) c = 50;
            if (entity.getRandom().nextInt(c) == 0) {
                event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ(), new ItemStack(ItemsTC.curio, 1)));
            }
        }
        if (event.getSource().is(DamageSourceThaumcraft.DISSOLVE_TAG)) {
            AspectList aspects = AspectHelper.getEntityAspects(entity);
            if (aspects != null && aspects.size() > 0) {
                Aspect[] al = aspects.getAspects();
                int q = Mth.randomBetweenInclusive(entity.level().getRandom(), 1, 1 + aspects.visSize() / 10);
                for (int a = 0; a < q && al != null && al.length > 0; ++a) {
                    Aspect aspect = al[entity.level().getRandom().nextInt(al.length)];
                    ItemStack stack = ThaumcraftApiHelper.makeCrystal(aspect);
                    event.getDrops().add(new ItemEntity(entity.level(), entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ(), stack));
                }
            }
        }
    }

    @SubscribeEvent
    public static void entitySpawns(EntityJoinLevelEvent event) {
        Level level = event.getLevel();
        Entity entity = event.getEntity();
        if (!level.isClientSide()) {
            if (entity instanceof PathfinderMob mob) {
                AttributeInstance champAttr = mob.getAttribute(net.minecraft.core.Holder.direct(ThaumcraftApiHelper.CHAMPION_MOD));
                if (champAttr != null && champAttr.getValue() == 13.0) {
                    AttributeInstance taintAttr = mob.getAttribute(net.minecraft.core.Holder.direct(ChampionModTainted.TAINTED_MOD));
                    if (taintAttr != null) {
                        taintAttr.removeModifier(net.minecraft.resources.Identifier.parse("thaumcraft:istainted"));
                        taintAttr.addPermanentModifier(new AttributeModifier(
                            net.minecraft.resources.Identifier.parse("thaumcraft:istainted"),
                            0.0, AttributeModifier.Operation.ADD_VALUE));
                    }
                }
            }
            if (entity instanceof Monster mob) {
                AttributeInstance champAttr = mob.getAttribute(net.minecraft.core.Holder.direct(ThaumcraftApiHelper.CHAMPION_MOD));
                double champVal = champAttr != null ? champAttr.getValue() : 0.0;
                if (champVal < -1.0) {
                    int c = level.getRandom().nextInt(100);
                    if (level.getDifficulty() == Difficulty.EASY || !ModConfig.CONFIG_WORLD.allowChampionMobs) c += 2;
                    if (level.getDifficulty() == Difficulty.HARD) c -= (ModConfig.CONFIG_WORLD.allowChampionMobs ? 2 : 0);
                    if (level instanceof net.minecraft.server.level.ServerLevel sl
                            && sl.dimension().identifier().hashCode() == ModConfig.CONFIG_WORLD.dimensionOuterId) c -= 3;
                    if (isDangerousLocation(mob.level(), Mth.ceil(mob.getX()), Mth.ceil(mob.getY()), Mth.ceil(mob.getZ()))) {
                        c -= (ModConfig.CONFIG_WORLD.allowChampionMobs ? 10 : 3);
                    }
                    int cc = 0;
                    boolean whitelisted = false;
                    for (Class clazz : ConfigEntities.championModWhitelist.keySet()) {
                        if (clazz.isAssignableFrom(entity.getClass())) {
                            whitelisted = true;
                            if (!ModConfig.CONFIG_WORLD.allowChampionMobs && !(entity instanceof EntityThaumcraftBoss)) continue;
                            cc = Math.max(cc, ConfigEntities.championModWhitelist.get(clazz) - 1);
                        }
                    }
                    c -= cc;
                    if (whitelisted && c <= 0 && mob.getAttribute(Attributes.MAX_HEALTH) != null
                            && mob.getAttribute(Attributes.MAX_HEALTH).getBaseValue() >= 10.0) {
                        EntityUtils.makeChampion(mob, false);
                    } else if (champAttr != null) {
                        champAttr.removeModifier(ChampionModifier.ATTRIBUTE_MOD_NONE);
                        champAttr.addPermanentModifier(ChampionModifier.ATTRIBUTE_MOD_NONE);
                    }
                }
            }
        }
    }

    private static boolean isDangerousLocation(Level world, int x, int y, int z) {
        return false;
    }
}
