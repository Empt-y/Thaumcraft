#!/usr/bin/env python3
"""Batch 12: Fix entity/projectile/AI files - modernize 1.12 Forge API patterns."""
import os, re

SRC = "/home/ash/geostrata/Thaumcraft/src/main/java"
changed = 0

# Files that extend ThrowableProjectile and need constructor + defineSynchedData fixes
THROWABLE_CLASSES = [
    "EntityAlumentum", "EntityBottleTaint", "EntityCausalityCollapser",
    "EntityEldritchOrb", "EntityFocusCloud", "EntityFocusMine",
    "EntityFocusProjectile", "EntityGolemOrb", "EntityGrapple",
    "EntityHomingShard", "EntityRiftBlast",
]

# Files that extend AbstractArrow
ARROW_CLASSES = ["EntityGolemDart"]

def get_simple_class(path):
    m = re.search(r'/(\w+)\.java$', path)
    return m.group(1) if m else ""

def fix_file(path, content):
    c = content
    cls = get_simple_class(path)

    # ── 0. defineSynchedData: add if missing ──
    # ThrowableProjectile and other direct Entity subclasses need this
    if ("extends ThrowableProjectile" in c or "extends AbstractArrow" in c or
        ("extends Entity" in c and "class EntityFallingTaint" in c) or
        ("extends ItemEntity" in c) or
        ("class EntitySpecialItem" in c) or ("class EntityFollowingItem" in c)):
        if "defineSynchedData" not in c:
            # Add after last import or after class opening
            insert = (
                "\n    @Override\n"
                "    protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {\n"
                "        super.defineSynchedData(builder);\n"
                "    }\n"
            )
            # Insert after the first { of the class body
            c = re.sub(r'(public class \w[^\{]*\{)', r'\1' + insert, c, count=1)

    # ── 1. Fix old-style super(type, world) where type is undefined ──
    # Pattern: secondary constructors call super(type, varname) but type is not a param
    # We replace super(type, X) with this((EntityType<? extends CLS>) null, X)
    if cls:
        c = re.sub(
            r'\bsuper\(type,\s*(\w+)\)',
            f'this((net.minecraft.world.entity.EntityType<? extends {cls}>) null, \\1)',
            c
        )

    # ── 2. Fix ThrowableProjectile old constructors ──
    # super(par1World, par2Mob) → this(null, par1World); this.setOwner(par2Mob); this.setPos(...)
    # super(worldIn, p) → similar
    # We handle the specific patterns seen in these files
    for pat in [
        (r'\bsuper\((\w+),\s*(\w+)\);\s*$',  # super(world, livingEntity)
         lambda m: f'this((net.minecraft.world.entity.EntityType<? extends {cls}>) null, {m.group(1)});\n        this.setOwner({m.group(2)});\n        this.setPos({m.group(2)}.getX(), {m.group(2)}.getEyeY() - 0.1, {m.group(2)}.getZ());'),
    ]:
        pass  # complex, handle below

    # ── 3. onUpdate() → tick(), onLivingUpdate() → aiStep() ──
    c = re.sub(r'\bpublic void onUpdate\(\)', 'public void tick()', c)
    c = re.sub(r'\bpublic void onLivingUpdate\(\)', 'protected void aiStep()', c)
    c = re.sub(r'\bsuper\.onUpdate\(\)', 'super.tick()', c)
    c = re.sub(r'\bsuper\.onLivingUpdate\(\)', 'super.aiStep()', c)

    # ── 4. motionX/Y/Z reads → getDeltaMovement().x/y/z ──
    # For reads (not assignments), replace motionX/Y/Z
    # Be careful: this is complex for statements like motionX += ...
    # We handle simple patterns first
    c = re.sub(r'\bmotionX\b(?!\s*[+\-\*\/]?=)', 'getDeltaMovement().x', c)
    c = re.sub(r'\bmotionY\b(?!\s*[+\-\*\/]?=)', 'getDeltaMovement().y', c)
    c = re.sub(r'\bmotionZ\b(?!\s*[+\-\*\/]?=)', 'getDeltaMovement().z', c)

    # Now fix motionX = expr → accumulate via delta movement
    # motionX = value; motionY = value2; motionZ = value3;
    # → setDeltaMovement(value, value2, value3);
    # But we can't do this multi-line without complex parsing
    # For now: motionX = expr → __mx__ = expr (temp var), then fix manually
    # Actually: just wrap: motionX = X → setDeltaMovement(X, getDeltaMovement().y, getDeltaMovement().z)
    c = re.sub(r'\bmotionX\s*=\s*([^;]+);',
               r'setDeltaMovement(\1, getDeltaMovement().y, getDeltaMovement().z);', c)
    c = re.sub(r'\bmotionY\s*=\s*([^;]+);',
               r'setDeltaMovement(getDeltaMovement().x, \1, getDeltaMovement().z);', c)
    c = re.sub(r'\bmotionZ\s*=\s*([^;]+);',
               r'setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, \1);', c)

    # motionX += / -= → wrapped
    c = re.sub(r'\bmotionX\s*\+=\s*([^;]+);',
               r'setDeltaMovement(getDeltaMovement().x + \1, getDeltaMovement().y, getDeltaMovement().z);', c)
    c = re.sub(r'\bmotionY\s*\+=\s*([^;]+);',
               r'setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y + \1, getDeltaMovement().z);', c)
    c = re.sub(r'\bmotionZ\s*\+=\s*([^;]+);',
               r'setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z + \1);', c)
    c = re.sub(r'\bmotionX\s*\*=\s*([^;]+);',
               r'setDeltaMovement(getDeltaMovement().x * \1, getDeltaMovement().y, getDeltaMovement().z);', c)
    c = re.sub(r'\bmotionY\s*\*=\s*([^;]+);',
               r'setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y * \1, getDeltaMovement().z);', c)
    c = re.sub(r'\bmotionZ\s*\*=\s*([^;]+);',
               r'setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y, getDeltaMovement().z * \1);', c)

    # ── 5. getYRot() = X → setYRot(X) ──
    c = re.sub(r'\bgetYRot\(\)\s*=\s*([^;]+);', r'setYRot(\1);', c)
    c = re.sub(r'\bgetXRot\(\)\s*=\s*([^;]+);', r'setXRot(\1);', c)

    # ── 6. world.* → level().* ──
    c = re.sub(r'\bworld\.random\b', 'random', c)
    c = re.sub(r'\bworld\.getEntitiesWithinAABBExcludingEntity\b', 'level().getEntitiesOfClass /* excl */)', c)  # approximate
    c = re.sub(r'\bworld\.getEntitiesWithinAABB\(([^,]+),', r'level().getEntitiesOfClass(\1,', c)
    c = re.sub(r'\bworld\.getEntityByID\(', 'level().getEntity(', c)
    c = re.sub(r'\bworld\.getClosestPlayerToEntity\(', 'level().getNearestPlayer(', c)
    c = re.sub(r'\bworld\.getDifficulty\(\)', 'level().getDifficulty()', c)
    c = re.sub(r'\bworld\.isThundering\(\)', 'level().isThundering()', c)
    c = re.sub(r'\bworld\.createExplosion\(', 'level().explode(', c)
    c = re.sub(r'\bworld\.spawnEntity\(', 'level().addFreshEntity(', c)
    c = re.sub(r'\bworld\.setBlock\(([^,]+),\s*([^,]+),\s*(\d+)\)', r'level().setBlock(\1, \2, \3)', c)
    c = re.sub(r'\bworld\.setBlock\(([^,]+),\s*([^)]+)\)', r'level().setBlockAndUpdate(\1, \2)', c)
    c = re.sub(r'\bworld\.isBlockNormalCube\([^)]+\)', 'true /* isBlockNormalCube removed */', c)
    c = re.sub(r'\bworld\.getLightFor\(', 'level().getBrightness(', c)
    c = re.sub(r'\bworld\.getLightFromNeighbors\(', 'level().getMaxLocalRawBrightness(', c)
    c = re.sub(r'\bworld\.getHeight\(\)', 'level().getMaxY()', c)
    # Remaining bare world. references
    c = re.sub(r'\bworld\.(\w)', r'level().\1', c)

    # ── 7. Method renames ──
    c = c.replace('setDead()', 'discard()')
    c = re.sub(r'\bsetDead\(\)', 'discard()', c)
    c = re.sub(r'\bisEntityAlive\(\)', 'isAlive()', c)
    c = re.sub(r'\bisEntityUndead\(\)', 'isUndead()', c)
    c = re.sub(r'\bgetEntityId\(\)', 'getId()', c)
    c = re.sub(r'\bgetAttackTarget\(\)', 'getTarget()', c)
    c = re.sub(r'\bsetAttackTarget\(', 'setTarget(', c)
    c = re.sub(r'\bgetThrower\(\)', 'getOwner()', c)
    c = re.sub(r'\bgetRNG\(\)', 'random', c)
    c = re.sub(r'\bgetDistanceSq\(', 'distanceToSqr(', c)
    c = re.sub(r'\bgetDistanceSqToCenter\(', 'distanceToSqr(', c)
    c = re.sub(r'\bgetDistance\(', 'distanceTo(', c)
    c = re.sub(r'\bgetDefaultState\(\)', 'defaultBlockState()', c)
    c = re.sub(r'\bisEntityInvulnerable\(', 'isInvulnerableTo(', c)
    c = re.sub(r'\bcanEntityBeSeen\(', 'getSensing().hasLineOfSight(', c)
    c = re.sub(r'\bgetPosition\(\)', 'blockPosition()', c)
    c = re.sub(r'\baddMobEffectInstance\(', 'addEffect(', c)
    c = re.sub(r'\bgetCanSpawnHere\(\)', 'checkSpawnRules(level(), net.minecraft.world.entity.EntitySpawnReason.NATURAL)', c)
    c = re.sub(r'\bgetSlimeSize\(\)', 'getSize()', c)
    c = re.sub(r'\bsetSlimeSize\(', 'setSize(', c)
    c = re.sub(r'\bgetEntityAttribute\(', 'getAttribute(', c)
    c = re.sub(r'\b\.getAttributeValue\(\)', '.getValue()', c)
    c = re.sub(r'\bgetAttributeValue\(', 'getAttributeValue(', c)  # already correct
    c = re.sub(r'\bapplyEnchantments\(this,\s*(\w+)\)', r'/* applyEnchantments removed */ ', c)
    c = re.sub(r'\bapplyEnchantments\([^)]+\)', '/* applyEnchantments removed */', c)
    c = re.sub(r'\bgetLookVec\(\)', 'getLookAngle()', c)
    c = re.sub(r'\bsetLocationAndAngles\(', 'moveTo(', c)
    c = re.sub(r'\bfaceEntity\([^)]+\)', '/* faceEntity removed */', c)
    c = re.sub(r'\bentityDropItem\(', 'spawnAtLocation(level(), ', c)
    c = re.sub(r'\bmakesSoundOnJump\(\)', 'doPlayJumpSound()', c)
    c = re.sub(r'\balterSquishAmount\(\)', 'decreaseSquish()', c)
    c = re.sub(r'\bsquishAmount\b', 'squish', c)
    c = re.sub(r'\bonInitialSpawn\b', 'finalizeSpawn', c)
    c = re.sub(r'\bgetClampedAdditionalDifficulty\(\)', 'getSpecialMultiplier()', c)
    c = re.sub(r'\bnoClip\b', 'noPhysics', c)

    # ── 8. isDead field ──
    c = re.sub(r'\bisDeadOrDying\b\(\)', 'isDeadOrDying()', c)  # already correct
    c = re.sub(r'\b!isDead\b', '!isDeadOrDying()', c)
    c = re.sub(r'\bisDead\b(?!\s*\()', 'isDeadOrDying()', c)

    # ── 9. height field → getBbHeight() ──
    # Only on entity objects: "entity.height" or just "height" in context of bounding box
    c = re.sub(r'(\w+)\.height\b(?!\s*=)', r'\1.getBbHeight()', c)
    # Bare "height" used as entity height field
    c = re.sub(r'\bheight\s*/\s*2', r'getBbHeight() / 2', c)

    # ── 10. rotationPitch → getXRot(), prevRotation* ──
    c = re.sub(r'\bprevRotationYaw\b', 'yRotO', c)
    c = re.sub(r'\bprevRotationPitch\b', 'xRotO', c)
    c = re.sub(r'\brotationPitch\b(?!\s*=)', 'getXRot()', c)
    c = re.sub(r'\brotationPitch\s*=\s*([^;]+);', r'setXRot(\1);', c)
    c = re.sub(r'\brenderYawOffset\b', 'yBodyRot', c)

    # ── 11. Sound event renames ──
    c = re.sub(r'\bSoundEvents\.ENTITY_SLIME_ATTACK\b', 'SoundEvents.SLIME_ATTACK', c)
    c = re.sub(r'\bSoundEvents\.ENTITY_SLIME_HURT\b', 'SoundEvents.SLIME_HURT', c)
    c = re.sub(r'\bSoundEvents\.ENTITY_SLIME_SQUISH\b', 'SoundEvents.SLIME_SQUISH', c)
    c = re.sub(r'\bSoundEvents\.ENTITY_SLIME_DEATH\b', 'SoundEvents.SLIME_DEATH', c)
    c = re.sub(r'\bSoundEvents\.ENTITY_SLIME_JUMP\b', 'SoundEvents.SLIME_JUMP', c)
    c = re.sub(r'\bSoundEvents\.BLOCK_LAVA_EXTINGUISH\b', 'SoundEvents.LAVA_EXTINGUISH', c)

    # ── 12. BlockPos: p.down() → p.below() ──
    c = re.sub(r'\.down\(\)', '.below()', c)
    c = re.sub(r'\.up\((\d+)\)', r'.above(\1)', c)
    c = re.sub(r'\.up\(\)', '.above()', c)

    # ── 13. .isReplaceable(world, pos) → canBeReplaced() ──
    c = re.sub(r'\.isReplaceable\([^)]+\)', '.canBeReplaced()', c)

    # ── 14. getEyeHeight() → getEyeHeight() (no change) ──
    # getPosition(float) → position(partialTick) - this is a different method
    c = re.sub(r'\bgetPosition\((\w+)\)', r'getPosition(\1)', c)  # keep as is for now

    # ── 15. MobEffect → Holder<MobEffect> issue ──
    # PotionFluxTaint.instance needs wrapping
    c = re.sub(
        r'new MobEffectInstance\(PotionFluxTaint\.instance\b',
        'new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(PotionFluxTaint.instance)',
        c
    )
    c = re.sub(
        r'new MobEffectInstance\((\w+)\.instance\b',
        r'new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(\1.instance)',
        c
    )

    # ── 16. Fix canTriggerWalking → isIgnoringBlockTriggers ──
    c = re.sub(
        r'protected boolean canTriggerWalking\(\)\s*\{[\s\n]*return false;\s*\}',
        'public boolean isIgnoringBlockTriggers() { return true; }',
        c
    )

    # ── 17. EntityThaumicSlime: super(type, par1World) in 3rd constructor ──
    # handled by rule 1 above

    # ── 18. getEntityId() → getId() (already handled above) ──

    # ── 19. preventEntitySpawning → noPhysics (it's the correct rename) ──
    c = re.sub(r'\bpreventEntitySpawning\b', 'noPhysics', c)

    # ── 20. p_175451_1_.hurt(...) that returns void ──
    # In context of if condition - already handled by NeoForge hurt() returning void
    # The pattern: canEntityBeSeen... && entity.hurt(source, dmg)
    # hurt() returns void now, so it can't be in if condition
    # We'll let it compile error and handle per-file

    # ── 21. DamageSource static methods (old) ──
    c = re.sub(r'\bDamageSource\.causeIndirectMagicDamage\(([^,]+),\s*([^)]+)\)',
               r'level().damageSources().indirectMagic(\1, \2)', c)
    c = re.sub(r'\bDamageSource\.causeExplosionDamage\b.*?\)',
               r'level().damageSources().explosion(this)', c)

    # ── 22. getEntityBoundingBox() → getBoundingBox() ──
    c = re.sub(r'\bgetEntityBoundingBox\(\)', 'getBoundingBox()', c)

    # ── 23. getTrueSource() → getEntity() ──
    c = re.sub(r'\.getTrueSource\(\)', '.getEntity()', c)

    # ── 24. Entity.getType() clash avoidance - already handled for EntityWisp ──

    # ── 25. HitResult.typeOfHit → hitResult.getType() ──
    c = re.sub(r'\b(\w+)\.typeOfHit\b', r'\1.getType()', c)
    c = re.sub(r'\b(\w+)\.entityHit\b', r'((net.minecraft.world.phys.EntityHitResult)\1).getEntity()', c)

    # ── 26. Remove ByteBuf spawn data methods (old Forge pattern) ──
    # writeSpawnData / readSpawnData - just comment out
    c = re.sub(
        r'public void writeSpawnData\(ByteBuf[^}]+\}',
        '/* writeSpawnData removed */',
        c,
        flags=re.DOTALL
    )
    c = re.sub(
        r'public void readSpawnData\(ByteBuf[^}]+\}',
        '/* readSpawnData removed */',
        c,
        flags=re.DOTALL
    )

    # ── 27. AbstractArrow: capabilities.disableDamage → getAbilities().invulnerable ──
    c = re.sub(r'\.capabilities\.disableDamage\b', '.getAbilities().invulnerable', c)

    return c


def process_dir(root):
    global changed
    for dirpath, dirs, files in os.walk(root):
        for fname in files:
            if not fname.endswith('.java'):
                continue
            path = os.path.join(dirpath, fname)
            with open(path, 'r', encoding='utf-8', errors='replace') as f:
                original = f.read()
            fixed = fix_file(path, original)
            if fixed != original:
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(fixed)
                changed += 1
                print(f"  FIXED: {path[len(root)+1:]}")


process_dir(SRC)
print(f"\nTotal files changed: {changed}")
