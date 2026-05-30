#!/usr/bin/env python3
"""Batch 13: Fix remaining API migration issues."""
import os, re

SRC = "/home/ash/geostrata/Thaumcraft/src/main/java"
changed = 0

def fix_file(path, content):
    c = content

    # ── 1. NodeEvaluator field renames ──
    # entity → mob (in subclasses of NodeEvaluator)
    if 'NodeEvaluator' in c or 'nodeprocessor' in path.lower() or 'FlightNodeProcessor' in path or 'GolemNodeProcessor' in path:
        # Only rename standalone 'entity.' field accesses in these classes, not in Entity subclasses
        # The field 'entity' in NodeEvaluator became 'mob'
        # Be careful: only rename 'entity' as a standalone field name, not 'entity' in other contexts
        # These classes extend NodeEvaluator, so 'entity' = inherited field
        # Pattern: bare 'entity' as a reference to the mob field
        pass  # Handle per-file below

    # ── 2. getItemStackFromSlot → getItemBySlot ──
    c = re.sub(r'\bgetItemStackFromSlot\(', 'getItemBySlot(', c)

    # ── 3. getDataManager() → entityData ──
    c = re.sub(r'\bgetDataManager\(\)', 'entityData', c)
    # Also handle: getDataManager().register → entityData (handled by defineSynchedData)
    # getDataManager().set → entityData.set
    # getDataManager().get → entityData.get
    # These are already covered since getDataManager() → entityData

    # ── 4. getNavigator() → getNavigation() ──
    c = re.sub(r'\bgetNavigator\(\)', 'getNavigation()', c)

    # ── 5. SharedMonsterAttributes → Attributes ──
    c = re.sub(r'\bSharedMonsterAttributes\b', 'Attributes', c)
    if 'Attributes' in c and 'import net.minecraft.world.entity.ai.attributes.Attributes' not in c:
        if 'import net.minecraft.world.entity.ai.attributes' not in c:
            c = c.replace('package ', 'import net.minecraft.world.entity.ai.attributes.Attributes;\npackage ', 1)

    # ── 6. getPositionVector() → position() ──
    c = re.sub(r'\bgetPositionVector\(\)', 'position()', c)

    # ── 7. rotationYaw field → yRot ──
    c = re.sub(r'\brotationYaw\b', 'yRot', c)

    # ── 8. HitResult.hitVec → mop.getLocation() ──
    # hitVec is not a direct field anymore
    c = re.sub(r'\b(\w+)\.hitVec\b', r'\1.getLocation()', c)

    # ── 9. HitResult.sideHit ──
    # sideHit was removed; for block hits use ((BlockHitResult)mop).getDirection()
    c = re.sub(r'\b(\w+)\.sideHit\b', r'((net.minecraft.world.phys.BlockHitResult)\1).getDirection()', c)

    # ── 10. setMutexBits → setFlags ──
    c = re.sub(r'\bsetMutexBits\(', 'setFlags(', c)

    # ── 11. getLookHelper() → getLookControl() ──
    c = re.sub(r'\bgetLookHelper\(\)', 'getLookControl()', c)

    # ── 12. setPosition(x,y,z) → setPos(x,y,z) ──
    c = re.sub(r'\bsetPosition\(', 'setPos(', c)

    # ── 13. onGround field → onGround() method (when used as read) ──
    # Be careful: don't affect 'wasOnGround' or 'onGround ='
    # Replace '.onGround' when preceded by entity reference
    c = re.sub(r'\b(\w+)\.onGround\b(?!\s*=)', r'\1.onGround()', c)
    # Bare 'onGround' in entity context (read only, not assignment)
    c = re.sub(r'(?<!\w)onGround\b(?!\s*=)(?!\()(?!\s*\b)', 'onGround()', c)

    # ── 14. tasks.addTask → goalSelector.addGoal ──
    c = re.sub(r'\btasks\.addTask\(', 'goalSelector.addGoal(', c)
    c = re.sub(r'\btargetTasks\.addTask\(', 'targetSelector.addGoal(', c)

    # ── 15. getAttackTarget() / setAttackTarget() already done ──

    # ── 16. speed field in LivingEntity ──
    # 'this.speed' or 'speed =' → need to check context
    # In Goal/AI classes, speed usually refers to a local variable or mob speed
    # speed = value → this is likely a local assignment, not the field
    # entity.speed → doesn't exist anymore; use entity.getSpeed()
    c = re.sub(r'\b(\w+)\.speed\b(?!\s*=)', r'\1.getSpeed()', c)

    # ── 17. spawnAtLocation(Level, ItemStack, float) → spawnAtLocation(Level, ItemStack) ──
    # In modern MC, spawnAtLocation doesn't take a y-offset
    c = re.sub(r'\bspawnAtLocation\(level\(\),\s*([^,]+),\s*[^)]+\)', r'spawnAtLocation(level(), \1)', c)
    c = re.sub(r'\bspawnAtLocation\(([^,\)]+),\s*([^,\)]+),\s*[a-zA-Z0-9._/\s]+\)', r'spawnAtLocation(\1, \2)', c)

    # ── 18. AbstractGoal/PathfinderGoal renames ──
    # In 1.21, some AI goal constructors changed
    # taskOwner → is typically the 'mob' field in Goal subclasses
    # Actually in 1.21, Goals have a constructor that takes the mob
    # taskOwner → whatever the Goal stores

    # ── 19. HitResult.Type.ENTITY_RESULT → ENTITY ──
    c = re.sub(r'\bHitResult\.Type\.ENTITY_RESULT\b', 'HitResult.Type.ENTITY', c)

    # ── 20. isDeadOrDying() = true ──
    # This is an invalid assignment; should be discard() + super handling
    c = re.sub(r'\bisDeadOrDying\(\)\s*=\s*true\s*;', 'discard();', c)

    # ── 21. getX() = / getY() = / getZ() = ──
    c = re.sub(r'\bgetX\(\)\s*-?=\s*([^;]+);', r'setPos(\1, getY(), getZ());', c)
    c = re.sub(r'\bgetY\(\)\s*-?=\s*([^;]+);', r'setPos(getX(), \1, getZ());', c)
    c = re.sub(r'\bgetZ\(\)\s*-?=\s*([^;]+);', r'setPos(getX(), getY(), \1);', c)

    # ── 22. AABB.grow() → inflate() / expand() ──
    c = re.sub(r'\.grow\(([^)]+)\)', r'.inflate(\1)', c)

    # ── 23. getEntityWorld() → level() ──
    c = re.sub(r'\bgetEntityWorld\(\)', 'level()', c)

    # ── 24. getLevel() → level() (on entities) ──
    c = re.sub(r'\.getLevel\(\)', '.level()', c)
    c = re.sub(r'\bengine\.level\(\)', 'entity.level()', c)

    # ── 25. BlockPos.setPos → set ──
    c = re.sub(r'\.setPos\((\d+),\s*(\d+),\s*(\d+)\)', r'.set(\1, \2, \3)', c)
    c = re.sub(r'blockpos\$mutableblockpos\.setPos\(', 'blockpos$mutableblockpos.set(', c)

    # ── 26. getBlockState().getBlock() comparisons ──
    # block == Blocks.FLOWING_WATER → this was removed (mc doesn't have FLOWING variants anymore)
    c = re.sub(r'\bBlocks\.FLOWING_WATER\b', 'Blocks.WATER', c)
    c = re.sub(r'\bBlocks\.FLOWING_LAVA\b', 'Blocks.LAVA', c)

    # ── 27. getEntityBoundingBox → getBoundingBox ──
    c = re.sub(r'\bgetEntityBoundingBox\(\)', 'getBoundingBox()', c)

    # ── 28. world.setBlockToAir → world.removeBlock ──
    c = re.sub(r'\bworld\.setBlockToAir\(([^)]+)\)', r'world.removeBlock(\1, false)', c)
    c = re.sub(r'\blevel\(\)\.setBlockToAir\(([^)]+)\)', r'level().removeBlock(\1, false)', c)

    # ── 29. world.getBlockState/setBlock remaining ──
    # bare 'world.' that didn't get caught (in static contexts or other contexts)
    # These appear in tile entities and blocks
    if 'extends BlockEntity' in c or 'extends Block' in c:
        pass  # Context-specific world. → level() handled elsewhere

    # ── 30. NodeEvaluator: 'entity' field → 'mob' ──
    if 'extends NodeEvaluator' in c or 'extends WalkNodeEvaluator' in c:
        # Only rename 'entity' field references where entity is the inherited mob field
        # This is safe since these classes use 'entity' exclusively for the mob
        # But be careful not to rename 'entity' in EntityType<T extends Entity> patterns
        c = re.sub(r'\bentity\.(getX|getY|getZ|getBoundingBox|getBbWidth|getBbHeight|isInWater|onGround|setPathPriority|getPathPriority|distanceToSqr|distanceTo|getId|level)\b',
                   lambda m: 'mob.' + m.group(1), c)

    # ── 31. PathFinder/Navigator renames ──
    # WalkNodeProcessor → WalkNodeEvaluator (older name)
    c = re.sub(r'\bWalkNodeProcessor\b', 'WalkNodeEvaluator', c)
    # PathPoint → Node
    c = re.sub(r'\bPathPoint\b', 'Node', c)
    # PathNodeType → PathType
    c = re.sub(r'\bPathNodeType\b', 'PathType', c)

    # ── 32. getItemIdentifier() → getDescriptionId() or similar ──

    # ── 33. addPotionEffect → addEffect ──
    c = re.sub(r'\baddPotionEffect\(', 'addEffect(', c)

    # ── 34. contains(String) on CompoundTag ──
    # In 1.21: nbt.contains(String) is still valid
    # contains(String, int) → hasTag(String, TagType.INT) or contains(String, Tag.TAG_INT)
    c = re.sub(r'\.contains\("(\w+)",\s*(\d+)\)', r'.contains("\1")', c)

    # ── 35. isDeadOrDying() used as boolean expression returning void ──
    # If isDeadOrDying() appears where a boolean is expected but it's actually void...
    # Actually isDeadOrDying() returns boolean so this might be a case like:
    # hurt(...) which returns void in 1.21+ (the "void" type not allowed errors)

    # ── 36. hurt() returns void now (server-side is hurtServer) ──
    # if (entity.hurt(...)) → split: entity.hurt(...); if (true)
    # This is a complex transformation - just wrap

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
