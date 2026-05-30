package thaumcraft.common.entities.monster.cult;
import net.minecraft.world.entity.monster.illager.AbstractIllager;
import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.ai.combat.AICultistHurtByTarget;
import thaumcraft.common.entities.monster.EntityEldritchGuardian;


public class EntityCultistKnight extends EntityCultist
{
    @SuppressWarnings("unchecked")
    public EntityCultistKnight(Level level) {
        super(null, level);
        // Entity requires EntityType; use factory method
    }

    public EntityCultistKnight(net.minecraft.world.entity.EntityType<? extends EntityCultistKnight> type, Level world) {
        super(type, world);
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        return net.minecraft.world.entity.monster.Monster.createMonsterAttributes()
            .add(Attributes.MAX_HEALTH, 30.0);
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloatGoal(this));
        goalSelector.addGoal(3, new MeleeAttackGoal(this, 1.0, false));
        goalSelector.addGoal(5, new OpenDoorGoal(this, true));
        goalSelector.addGoal(6, new MoveTowardsRestrictionGoal(this, 0.8));
        goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 0.8));
        goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        targetSelector.addGoal(1, new AICultistHurtByTarget(this, true));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, EntityEldritchGuardian.class, true));
        targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractIllager.class, true));
    }

    @Override
    protected void setLoot(DifficultyInstance diff) {
        setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemsTC.crimsonPlateHelm));
        setItemSlot(EquipmentSlot.CHEST, new ItemStack(ItemsTC.crimsonPlateChest));
        setItemSlot(EquipmentSlot.LEGS, new ItemStack(ItemsTC.crimsonPlateLegs));
        setItemSlot(EquipmentSlot.FEET, new ItemStack(ItemsTC.crimsonBoots));
        if (getRandom().nextFloat() < ((level().getDifficulty() == Difficulty.HARD) ? 0.05f : 0.01f)) {
            int i = net.minecraft.util.RandomSource.create().nextInt(5);
            if (i == 0) {
                setItemInHand(getUsedItemHand(), new ItemStack(ItemsTC.voidSword));
                setItemSlot(EquipmentSlot.HEAD, new ItemStack(ItemsTC.crimsonRobeHelm));
            } else {
                setItemInHand(getUsedItemHand(), new ItemStack(ItemsTC.thaumiumSword));
                if (getRandom().nextBoolean()) setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
            }
        } else {
            setItemInHand(getUsedItemHand(), new ItemStack(Items.IRON_SWORD));
        }
    }
}
