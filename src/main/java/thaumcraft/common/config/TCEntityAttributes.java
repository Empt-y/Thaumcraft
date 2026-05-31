package thaumcraft.common.config;

import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import thaumcraft.Thaumcraft;
import thaumcraft.api.entities.EntitiesTC;

public class TCEntityAttributes {

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        // Mob (non-hostile base)
        event.put(EntitiesTC.WISP.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 22.0).add(Attributes.ATTACK_DAMAGE, 3.0)
            .add(Attributes.MOVEMENT_SPEED, 0.6).build());
        event.put(EntitiesTC.FIRE_BAT.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 5.0).add(Attributes.ATTACK_DAMAGE, 1.0)
            .add(Attributes.MOVEMENT_SPEED, 0.6).build());
        event.put(EntitiesTC.SPELL_BAT.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 5.0).add(Attributes.ATTACK_DAMAGE, 1.0)
            .add(Attributes.MOVEMENT_SPEED, 0.6).build());
        event.put(EntitiesTC.PECH.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 30.0).add(Attributes.ATTACK_DAMAGE, 6.0)
            .add(Attributes.MOVEMENT_SPEED, 0.5).add(Attributes.FOLLOW_RANGE, 16.0).build());
        event.put(EntitiesTC.MIND_SPIDER.get(), Spider.createAttributes()
            .add(Attributes.MAX_HEALTH, 1.0).add(Attributes.ATTACK_DAMAGE, 1.0).build());
        event.put(EntitiesTC.ELDRITCH_GUARDIAN.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 50.0).add(Attributes.ATTACK_DAMAGE, 7.0)
            .add(Attributes.MOVEMENT_SPEED, 0.28).build());
        event.put(EntitiesTC.ELDRITCH_CRAB.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 20.0).add(Attributes.ATTACK_DAMAGE, 4.0)
            .add(Attributes.MOVEMENT_SPEED, 0.3).build());

        // Zombie variants
        event.put(EntitiesTC.BRAINY_ZOMBIE.get(), Zombie.createAttributes()
            .add(Attributes.MAX_HEALTH, 25.0).add(Attributes.ATTACK_DAMAGE, 5.0).build());
        event.put(EntitiesTC.GIANT_BRAINY_ZOMBIE.get(), Zombie.createAttributes()
            .add(Attributes.MAX_HEALTH, 60.0).add(Attributes.ATTACK_DAMAGE, 7.0)
            .add(Attributes.MOVEMENT_SPEED, 0.3).build());
        event.put(EntitiesTC.INHABITED_ZOMBIE.get(), Zombie.createAttributes()
            .add(Attributes.MAX_HEALTH, 30.0).add(Attributes.ATTACK_DAMAGE, 5.0).build());
        event.put(EntitiesTC.TAUMIC_SLIME.get(), Mob.createMobAttributes()
            .add(Attributes.ATTACK_DAMAGE, 3.0).build());

        // Cultists
        event.put(EntitiesTC.CULTIST_KNIGHT.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 30.0).add(Attributes.ATTACK_DAMAGE, 4.0)
            .add(Attributes.MOVEMENT_SPEED, 0.3).build());
        event.put(EntitiesTC.CULTIST_CLERIC.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 24.0).add(Attributes.ATTACK_DAMAGE, 4.0)
            .add(Attributes.MOVEMENT_SPEED, 0.3).build());
        event.put(EntitiesTC.CULTIST_LEADER.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 150.0).add(Attributes.ATTACK_DAMAGE, 5.0)
            .add(Attributes.MOVEMENT_SPEED, 0.32).build());
        event.put(EntitiesTC.CULTIST_PORTAL_GREATER.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 500.0).add(Attributes.ATTACK_DAMAGE, 0.0)
            .add(Attributes.MOVEMENT_SPEED, 0.0).build());
        event.put(EntitiesTC.CULTIST_PORTAL_LESSER.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 100.0).add(Attributes.ATTACK_DAMAGE, 0.0)
            .add(Attributes.MOVEMENT_SPEED, 0.0).build());

        // Bosses
        event.put(EntitiesTC.ELDRITCH_WARDEN.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 400.0).add(Attributes.ATTACK_DAMAGE, 10.0)
            .add(Attributes.MOVEMENT_SPEED, 0.33).add(Attributes.ARMOR, 10.0).build());
        event.put(EntitiesTC.ELDRITCH_GOLEM.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 400.0).add(Attributes.ATTACK_DAMAGE, 10.0)
            .add(Attributes.MOVEMENT_SPEED, 0.3).add(Attributes.ARMOR, 8.0).build());
        event.put(EntitiesTC.TAINTACLE_GIANT.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 175.0).add(Attributes.ATTACK_DAMAGE, 9.0)
            .add(Attributes.MOVEMENT_SPEED, 0.0).build());

        // Tainted
        event.put(EntitiesTC.TAINT_CRAWLER.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 8.0).add(Attributes.ATTACK_DAMAGE, 2.0)
            .add(Attributes.MOVEMENT_SPEED, 0.275).build());
        event.put(EntitiesTC.TAINTACLE.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 50.0).add(Attributes.ATTACK_DAMAGE, 7.0)
            .add(Attributes.MOVEMENT_SPEED, 0.0).build());
        event.put(EntitiesTC.TAINTACLE_SMALL.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 5.0).add(Attributes.ATTACK_DAMAGE, 2.0)
            .add(Attributes.MOVEMENT_SPEED, 0.0).build());
        event.put(EntitiesTC.TAINT_SEED.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 75.0).add(Attributes.ATTACK_DAMAGE, 4.0)
            .add(Attributes.MOVEMENT_SPEED, 0.0).build());
        event.put(EntitiesTC.TAINT_SEED_PRIME.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 150.0).add(Attributes.ATTACK_DAMAGE, 7.0)
            .add(Attributes.MOVEMENT_SPEED, 0.0).build());
        event.put(EntitiesTC.TAINT_SWARM.get(), Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 8.0).add(Attributes.ATTACK_DAMAGE, 2.0)
            .add(Attributes.MOVEMENT_SPEED, 0.4).build());

        // Constructs/misc
        event.put(EntitiesTC.GOLEM.get(), net.minecraft.world.entity.PathfinderMob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.3)
            .add(Attributes.ATTACK_DAMAGE, 0.0).build());
        event.put(EntitiesTC.ARCANE_BORE.get(), net.minecraft.world.entity.PathfinderMob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 10.0).add(Attributes.MOVEMENT_SPEED, 0.0).build());
        event.put(EntitiesTC.TURRET_CROSSBOW.get(), net.minecraft.world.entity.PathfinderMob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0).add(Attributes.MOVEMENT_SPEED, 0.0).build());
        event.put(EntitiesTC.TURRET_CROSSBOW_ADVANCED.get(), net.minecraft.world.entity.PathfinderMob.createMobAttributes()
            .add(Attributes.MAX_HEALTH, 20.0).add(Attributes.MOVEMENT_SPEED, 0.0).build());
    }
}
