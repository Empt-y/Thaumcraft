package thaumcraft.common.lib.events;
import net.minecraft.world.Container;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.ExperienceOrb;
// baubles import removed
import java.util.HashMap;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.bus.api.Event; // Event /* AttachCapabilitiesEvent removed */ removed
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.aspects.IEssentiaContainerItem;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.IVisDiscountGear;
import thaumcraft.api.items.IWarpingGear;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.config.ConfigResearch;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.items.curios.ItemThaumonomicon;
import thaumcraft.common.items.resources.ItemCrystalEssence;
import thaumcraft.common.lib.capabilities.PlayerKnowledge;
import thaumcraft.common.lib.capabilities.PlayerWarp;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;
import thaumcraft.common.lib.potions.PotionDeathGaze;
import thaumcraft.common.lib.potions.PotionUnnaturalHunger;
import thaumcraft.common.lib.potions.PotionWarpWard;
import thaumcraft.common.lib.research.ResearchManager;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.world.aura.AuraHandler;


@net.neoforged.fml.common.EventBusSubscriber(modid = "thaumcraft")
public class PlayerEvents
{
    static HashMap<Integer, Long> nextCycle;
    static HashMap<Integer, Integer> lastCharge;
    static HashMap<Integer, Integer> lastMaxCharge;
    static HashMap<Integer, Integer> runicInfo;
    static HashMap<String, Long> upgradeCooldown;
    public static HashMap<Integer, Float> prevStep;
    
    @SubscribeEvent
    public static void onFallDamage(LivingIncomingDamageEvent event) {
        if (event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_FALL) && event.getEntity() instanceof Player) {
            if (((Player)event.getEntity()).getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET).getItem() == ItemsTC.travellerBoots) {
                float f = Math.max(0.0f, event.getAmount() / 2.0f - 1.0f);
                if (f < 1.0f) {
                    event.setCanceled(true);
                    event.setAmount(0.0f);
                }
                else {
                    event.setAmount(f);
                }
            }
            if (false) {
                float f = Math.max(0.0f, event.getAmount() / 3.0f - 2.0f);
                if (f < 1.0f) {
                    event.setCanceled(true);
                    event.setAmount(0.0f);
                }
                else if (f < event.getAmount()) {
                    event.setAmount(f);
                }
                if (event.getAmount() < 1.0f) {
                    event.setCanceled(true);
                    event.setAmount(0.0f);
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void livingTick(net.neoforged.neoforge.event.tick.EntityTickEvent.Post event) {
        if (event.getEntity() instanceof Player player) {
            handleMisc(player);
            handleSpeedMods(player);
            if (!player.level().isClientSide()) {
                handleRunicArmor(player);
                handleWarp(player);
                if (player.tickCount % 20 == 0 && ResearchManager.syncList.remove(player.getName().getString()) != null) {
                    IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
                    knowledge.sync((net.minecraft.server.level.ServerPlayer)player);
                }
                if (player.tickCount % 200 == 0) {
                    ConfigResearch.checkPeriodicStuff(player);
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void pickupItem(ItemEntityPickupEvent.Pre event) {
        if (event.getPlayer() != null && !event.getPlayer().level().isClientSide() && !event.getItemEntity().getItem().isEmpty() && event.getItemEntity().getItem() != null) {
            IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(event.getPlayer());
            if (event.getItemEntity().getItem().getItem() instanceof ItemCrystalEssence && !knowledge.isResearchKnown("!gotcrystals")) {
                knowledge.addResearch("!gotcrystals");
                knowledge.sync((net.minecraft.server.level.ServerPlayer)event.getPlayer());
                ((net.minecraft.server.level.ServerPlayer)event.getPlayer()).sendSystemMessage(net.minecraft.network.chat.Component.literal(ChatFormatting.DARK_PURPLE + "" + I18n.get("got.crystals")));
                if (ModConfig.CONFIG_MISC.noSleep && !knowledge.isResearchKnown("!gotdream")) {
                    giveDreamJournal(event.getPlayer());
                }
            }
            if (event.getItemEntity().getItem().getItem() instanceof ItemThaumonomicon && !knowledge.isResearchKnown("!gotthaumonomicon")) {
                knowledge.addResearch("!gotthaumonomicon");
                knowledge.sync((net.minecraft.server.level.ServerPlayer)event.getPlayer());
            }
        }
    }
    
    @SubscribeEvent
    public static void wakeUp(PlayerWakeUpEvent event) {
        Player wakePlayer = (Player) event.getEntity();
        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(wakePlayer);
        if (wakePlayer != null && !wakePlayer.level().isClientSide() && knowledge.isResearchKnown("!gotcrystals") && !knowledge.isResearchKnown("!gotdream")) {
            giveDreamJournal(wakePlayer);
        }
    }
    
    private static void giveDreamJournal(Player player) {
        IPlayerKnowledge knowledge = ThaumcraftCapabilities.getKnowledge(player);
        knowledge.addResearch("!gotdream");
        knowledge.sync((net.minecraft.server.level.ServerPlayer)player);
        ItemStack book = ConfigItems.startBook.copy();
        // Store author in custom NBT data
        net.minecraft.world.item.component.CustomData customData = book.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY);
        net.minecraft.nbt.CompoundTag bookTag = customData.copyTag();
        bookTag.putString("author", player.getName().getString());
        book.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.of(bookTag));
        if (!player.getInventory().add(book)) {
            InventoryUtils.dropItemAtEntity(player.level(), book, player);
        }
        try {
            if (player instanceof net.minecraft.server.level.ServerPlayer) {
                ((net.minecraft.server.level.ServerPlayer)player).sendSystemMessage(net.minecraft.network.chat.Component.literal(ChatFormatting.DARK_PURPLE + "" + I18n.get("got.dream")));
            }
        }
        catch (Exception ex) {}
    }
    
    private static void handleMisc(Player player) {
        if (player.level().dimension().identifier().hashCode() == ModConfig.CONFIG_WORLD.dimensionOuterId && player.tickCount % 20 == 0 && !player.isSpectator() && !player.getAbilities().instabuild && player.getAbilities().flying) {
            player.getAbilities().flying = false;
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal("" + ChatFormatting.ITALIC + ChatFormatting.GRAY + I18n.get("tc.break.fly")));
        }
    }
    
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void tooltipEvent(ItemTooltipEvent event) {
        try {
            int charge = getRunicCharge(event.getItemStack());
            if (charge > 0) {
                event.getToolTip().add(net.minecraft.network.chat.Component.literal(ChatFormatting.GOLD + I18n.get("item.runic.charge") + " +" + charge));
            }
            int warp = getFinalWarp(event.getItemStack(), event.getEntity());
            if (warp > 0) {
                event.getToolTip().add(net.minecraft.network.chat.Component.literal(ChatFormatting.DARK_PURPLE + I18n.get("item.warping") + " " + warp));
            }
            int al = getFinalDiscount(event.getItemStack(), event.getEntity());
            if (al > 0) {
                event.getToolTip().add(net.minecraft.network.chat.Component.literal(ChatFormatting.DARK_PURPLE + I18n.get("tc.visdiscount") + ": " + al + "%"));
            }
            if (event.getItemStack() != null) {
                if (event.getItemStack().getItem() instanceof IRechargable) {
                    int c = Math.round((float)RechargeHelper.getCharge(event.getItemStack()));
                    if (c >= 0) {
                        event.getToolTip().add(net.minecraft.network.chat.Component.literal(ChatFormatting.YELLOW + I18n.get("tc.charge") + " " + c));
                    }
                }
                if (event.getItemStack().getItem() instanceof IEssentiaContainerItem) {
                    AspectList aspects = ((IEssentiaContainerItem)event.getItemStack().getItem()).getAspects(event.getItemStack());
                    if (aspects != null && aspects.size() > 0) {
                        for (Aspect tag : aspects.getAspectsSortedByName()) {
                            event.getToolTip().add(net.minecraft.network.chat.Component.literal(tag.getName() + " x" + aspects.getAmount(tag)));
                        }
                    }
                }
                ListTag nbttaglist = EnumInfusionEnchantment.getInfusionEnchantmentTagList(event.getItemStack());
                if (nbttaglist != null) {
                    for (int j = 0; j < nbttaglist.size(); ++j) {
                        int k = nbttaglist.getCompoundOrEmpty(j).getShortOr("id", (short)0);
                        int l = nbttaglist.getCompoundOrEmpty(j).getShortOr("lvl", (short)0);
                        if (k >= 0 && k < EnumInfusionEnchantment.values().length) {
                            String s = ChatFormatting.GOLD + I18n.get("enchantment.infusion." + EnumInfusionEnchantment.values()[k].toString());
                            if (EnumInfusionEnchantment.values()[k].maxLevel > 1) {
                                s = s + " " + I18n.get("enchantment.level()." + l);
                            }
                            event.getToolTip().add(1, net.minecraft.network.chat.Component.literal(s));
                        }
                    }
                }
            }
        }
        catch (Exception ex) {}
    }
    
    private static void handleRunicArmor(Player player) {
        if (player.tickCount % 20 == 0) {
            int max = 0;
            for (int a = 0; a < 4; ++a) {
                max += getRunicCharge(player.getInventory().getItem(36 + a));
            }
            // baubles removed - skip baubles loop
            if (PlayerEvents.lastMaxCharge.containsKey(player.getId())) {
                int charge = PlayerEvents.lastMaxCharge.get(player.getId());
                if (charge > max) {
                    player.setAbsorptionAmount(player.getAbsorptionAmount() - (charge - max));
                }
                if (max <= 0) {
                    PlayerEvents.lastMaxCharge.remove(player.getId());
                }
            }
            if (max > 0) {
                PlayerEvents.runicInfo.put(player.getId(), max);
                PlayerEvents.lastMaxCharge.put(player.getId(), max);
            }
            else {
                PlayerEvents.runicInfo.remove(player.getId());
            }
        }
        if (PlayerEvents.runicInfo.containsKey(player.getId())) {
            if (!PlayerEvents.nextCycle.containsKey(player.getId())) {
                PlayerEvents.nextCycle.put(player.getId(), 0L);
            }
            long time = System.currentTimeMillis();
            int charge = (int)player.getAbsorptionAmount();
            if (charge == 0 && PlayerEvents.lastCharge.containsKey(player.getId()) && PlayerEvents.lastCharge.get(player.getId()) > 0) {
                PlayerEvents.nextCycle.put(player.getId(), time + ModConfig.CONFIG_MISC.shieldWait);
                PlayerEvents.lastCharge.put(player.getId(), 0);
            }
            if (charge < PlayerEvents.runicInfo.get(player.getId()) && PlayerEvents.nextCycle.get(player.getId()) < time && !AuraHandler.shouldPreserveAura(player.level(), player, player.blockPosition()) && AuraHelper.getVis(player.level(), player.blockPosition()) >= ModConfig.CONFIG_MISC.shieldCost) {
                AuraHandler.drainVis(player.level(), player.blockPosition(), (float)ModConfig.CONFIG_MISC.shieldCost, false);
                PlayerEvents.nextCycle.put(player.getId(), time + ModConfig.CONFIG_MISC.shieldRecharge);
                player.setAbsorptionAmount((float)(charge + 1));
                PlayerEvents.lastCharge.put(player.getId(), charge + 1);
            }
        }
    }
    
    public static int getRunicCharge(ItemStack stack) {
        int base = 0;
        if (!stack.isEmpty() && stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("TC.RUNIC")) {
            base += stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getByteOr("TC.RUNIC", (byte)0);
        }
        return base;
    }
    
    public static int getFinalWarp(ItemStack stack, Player player) {
        if (stack == null || stack.isEmpty()) {
            return 0;
        }
        int warp = 0;
        if (stack.getItem() instanceof IWarpingGear) {
            IWarpingGear armor = (IWarpingGear)stack.getItem();
            warp += armor.getWarp(stack, player);
        }
        if (!stack.isEmpty() && stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().contains("TC.WARP")) {
            warp += stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getByteOr("TC.WARP", (byte)0);
        }
        return warp;
    }
    
    public static int getFinalDiscount(ItemStack stack, Player player) {
        if (stack == null || stack.isEmpty() || !(stack.getItem() instanceof IVisDiscountGear)) {
            return 0;
        }
        IVisDiscountGear gear = (IVisDiscountGear)stack.getItem();
        return gear.getVisDiscount(stack, player);
    }
    
    private static void handleSpeedMods(Player player) {
        if (player.level().isClientSide() && (player.isCrouching() || player.getInventory().getItem(36 + 0).getItem() != ItemsTC.travellerBoots) && PlayerEvents.prevStep.containsKey(player.getId())) {
            /* player.stepHeight removed */ /* stepHeight = PlayerEvents.prevStep.get(player.getId()); */ // TODO: override maxUpStep()
            PlayerEvents.prevStep.remove(player.getId());
        }
    }
    
    @SubscribeEvent
    public static void playerJumps(LivingEvent.LivingJumpEvent event) {
        if (event.getEntity() instanceof Player && ((Player)event.getEntity()).getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET).getItem() == ItemsTC.travellerBoots) {
            ItemStack is = ((Player)event.getEntity()).getItemBySlot(net.minecraft.world.entity.EquipmentSlot.FEET);
            if (RechargeHelper.getCharge(is) > 0) {
                LivingEntity entityLiving = event.getEntity();
                entityLiving.setDeltaMovement(entityLiving.getDeltaMovement().x, entityLiving.getDeltaMovement().y + 0.2750000059604645, entityLiving.getDeltaMovement().z);
            }
        }
    }
    
    private static void handleWarp(Player player) {
        if (!ModConfig.CONFIG_MISC.wussMode && player.tickCount > 0 && player.tickCount % 2000 == 0 && !player.hasEffect(net.minecraft.core.Holder.direct(PotionWarpWard.instance))) {
            WarpEvents.checkWarpEvent(player);
        }
        if (player.tickCount % 20 == 0 && player.hasEffect(net.minecraft.core.Holder.direct(PotionDeathGaze.instance))) {
            WarpEvents.checkDeathGaze(player);
        }
    }
    
    @SubscribeEvent
    public static void droppedItem(ItemTossEvent event) {
        CompoundTag itemData = ((net.minecraft.world.entity.item.ItemEntity)event.getEntity()).getPersistentData();
        itemData.putString("thrower", event.getPlayer().getName().getString());
    }
    
    @SubscribeEvent
    public static void finishedUsingItem(LivingEntityUseItemEvent.Finish event) {
        if (!event.getEntity().level().isClientSide() && event.getEntity().hasEffect(net.minecraft.core.Holder.direct(PotionUnnaturalHunger.instance))) {
            if (ItemStack.isSameItem(event.getItem(), new ItemStack(Items.ROTTEN_FLESH)) || ItemStack.isSameItem(event.getItem(), new ItemStack(ItemsTC.brain))) {
                MobEffectInstance pe = event.getEntity().getEffect(net.minecraft.core.Holder.direct(PotionUnnaturalHunger.instance));
                int amp = pe.getAmplifier() - 1;
                int duration = pe.getDuration() - 600;
                event.getEntity().removeEffect(net.minecraft.core.Holder.direct(PotionUnnaturalHunger.instance));
                if (duration > 0 && amp >= 0) {
                    pe = new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionUnnaturalHunger.instance), duration, amp, true, false);
                    event.getEntity().addEffect(pe);
                }
                if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer) {
                    ((net.minecraft.server.level.ServerPlayer)event.getEntity()).sendSystemMessage(net.minecraft.network.chat.Component.literal("§2§o" + I18n.get("warp.text.hunger.2")));
                }
            }
            else if (event.getItem().getItem() instanceof Item) {
                if (event.getEntity() instanceof net.minecraft.server.level.ServerPlayer) {
                    ((net.minecraft.server.level.ServerPlayer)event.getEntity()).sendSystemMessage(net.minecraft.network.chat.Component.literal("§4§o" + I18n.get("warp.text.hunger.1")));
                }
            }
        }
    }
    
    // TODO: AttachCapabilitiesEvent no longer exists in NeoForge 26.1.2.
    // Capability attachment is handled via data attachments (AttachmentType).
    // This method is a stub until capabilities are migrated.
    @SubscribeEvent
    public static void attachCapabilitiesPlayer(net.neoforged.neoforge.event.entity.EntityJoinLevelEvent event) {
        // stub - capabilities replaced by attachments in NeoForge 26.1.2
    }
    
    @SubscribeEvent
    public static void playerJoin(EntityJoinLevelEvent event) {
        if (!event.getLevel().isClientSide() && event.getEntity() instanceof net.minecraft.server.level.ServerPlayer) {
            net.minecraft.server.level.ServerPlayer player = (net.minecraft.server.level.ServerPlayer)event.getEntity();
            IPlayerKnowledge pk = ThaumcraftCapabilities.getKnowledge(player);
            IPlayerWarp pw = ThaumcraftCapabilities.getWarp(player);
            if (pk != null) {
                pk.sync(player);
            }
            if (pw != null) {
                pw.sync(player);
            }
        }
    }
    
    @SubscribeEvent
    public static void cloneCapabilitiesEvent(PlayerEvent.Clone event) {
        try {
            CompoundTag nbtKnowledge = ThaumcraftCapabilities.getKnowledge(event.getOriginal()).serializeNBT();
            ThaumcraftCapabilities.getKnowledge(event.getEntity()).deserializeNBT(nbtKnowledge);
            CompoundTag nbtWarp = ThaumcraftCapabilities.getWarp(event.getOriginal()).serializeNBT();
            ThaumcraftCapabilities.getWarp(event.getEntity()).deserializeNBT(nbtWarp);
        }
        catch (Exception e) {
            Thaumcraft.log.error("Could not clone player [" + event.getOriginal().getName().getString() + "] knowledge when changing dimensions");
        }
    }
    
    @SubscribeEvent
    public static void pickupXP(PlayerXpEvent.PickupXp event) {
        if (event.getEntity() != null && !event.getEntity().level().isClientSide() && event.getOrb().getValue() > 1) {
            int d = event.getOrb().getValue() / 2;
            ExperienceOrb orb = event.getOrb();
            // reduce xp value - field is package-private so we work around by just using value
            float r = event.getEntity().getRandom().nextFloat();
            if (r < 0.05 * d) {
                String[] s = ResearchCategories.researchCategories.keySet().toArray(new String[0]);
                String cat = s[event.getEntity().getRandom().nextInt(s.length)];
                ThaumcraftApi.internalMethods.addKnowledge(event.getEntity(), IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory(cat), 1);
            }
            else if (r < 0.2 * d) {
                String[] s = ResearchCategories.researchCategories.keySet().toArray(new String[0]);
                String cat = s[event.getEntity().getRandom().nextInt(s.length)];
                ThaumcraftApi.internalMethods.addKnowledge(event.getEntity(), IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory(cat), 1);
            }
        }
    }
    
    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        // baubles totem check removed - vanilla handles totem of undying
    }
    
    static {
        PlayerEvents.nextCycle = new HashMap<Integer, Long>();
        PlayerEvents.lastCharge = new HashMap<Integer, Integer>();
        PlayerEvents.lastMaxCharge = new HashMap<Integer, Integer>();
        PlayerEvents.runicInfo = new HashMap<Integer, Integer>();
        PlayerEvents.upgradeCooldown = new HashMap<String, Long>();
        PlayerEvents.prevStep = new HashMap<Integer, Float>();
    }
}
