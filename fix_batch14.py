#!/usr/bin/env python3
"""Batch 14: Fix remaining systematic compile errors."""
import os, re

SRC = "/home/ash/geostrata/Thaumcraft/src/main/java"
changed = 0

def fix_file(path, content):
    c = content

    # ── 1. move(MoverType, x, y, z) → move(MoverType, new Vec3(x, y, z)) ──
    # This is a 4-arg move call; modern MC takes Vec3
    c = re.sub(
        r'\bmove\(MoverType\.(\w+),\s*([^,]+),\s*([^,]+),\s*([^)]+)\)',
        r'move(MoverType.\1, new net.minecraft.world.phys.Vec3(\2, \3, \4))',
        c
    )

    # ── 2. new BlockPos(entity) → entity.blockPosition() ──
    c = re.sub(r'\bnew BlockPos\((\w+)\)(?!\s*\{)', lambda m: (
        f'{m.group(1)}.blockPosition()' if 'entity' in m.group(1).lower() or 'mob' in m.group(1).lower() or m.group(1) == 'this'
        else m.group(0)
    ), c)

    # ── 3. BlockState == Block comparisons → .getBlock() == ──
    # level().getBlockState(pos) == BlocksTC.something → .getBlock() == BlocksTC.something
    c = re.sub(
        r'level\(\)\.getBlockState\(([^)]+)\)\s*==\s*(BlocksTC\.\w+|Blocks\.\w+)',
        r'level().getBlockState(\1).getBlock() == \2',
        c
    )
    c = re.sub(
        r'level\(\)\.getBlockState\(([^)]+)\)\s*!=\s*(BlocksTC\.\w+|Blocks\.\w+)',
        r'level().getBlockState(\1).getBlock() != \2',
        c
    )
    # Also handle world.getBlockState(pos) == Block
    c = re.sub(
        r'world\.getBlockState\(([^)]+)\)\s*==\s*(BlocksTC\.\w+|Blocks\.\w+)',
        r'world.getBlockState(\1).getBlock() == \2',
        c
    )

    # ── 4. Blocks.PISTON_EXTENSION / PISTON_HEAD → modern equivalents ──
    c = re.sub(r'\bBlocks\.PISTON_EXTENSION\b', 'Blocks.PISTON', c)
    # PISTON_HEAD is still in modern MC
    # MOVING_PISTON is the replacement for PISTON_EXTENSION in some contexts
    c = re.sub(r'\bBlocks\.MOVING_PISTON\b', 'Blocks.MOVING_PISTON', c)  # keep as is

    # ── 5. fallTile == Blocks.AIR → fallTile.isAir() ──
    c = re.sub(r'\bfallTile\s*==\s*Blocks\.AIR\b', 'fallTile.isAir()', c)
    c = re.sub(r'\bfallTile\s*==\s*null\s*\|\|\s*fallTile\s*==\s*Blocks\.AIR\b', 'fallTile == null || fallTile.isAir()', c)

    # ── 6. canBeCollidedWith() → isPickable() ──
    c = re.sub(r'\bcanBeCollidedWith\(\)', 'isPickable()', c)
    c = re.sub(r'\bpublic boolean isPickable\(\)', 'public boolean isPickable()', c)  # correct, no change needed
    c = re.sub(r'\bpublic boolean canBeCollidedWith\(\)', 'public boolean isPickable()', c)

    # ── 7. writeEntityToNBT → addAdditionalSaveData ──
    c = re.sub(
        r'\bprotected void writeEntityToNBT\(CompoundTag\s+(\w+)\)',
        r'public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput \1)',
        c
    )
    c = re.sub(
        r'\bpublic void writeEntityToNBT\(CompoundTag\s+(\w+)\)',
        r'public void addAdditionalSaveData(net.minecraft.world.level.storage.ValueOutput \1)',
        c
    )

    # ── 8. readEntityFromNBT → readAdditionalSaveData ──
    c = re.sub(
        r'\bprotected void readEntityFromNBT\(CompoundTag\s+(\w+)\)',
        r'public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput \1)',
        c
    )
    c = re.sub(
        r'\bpublic void readEntityFromNBT\(CompoundTag\s+(\w+)\)',
        r'public void readAdditionalSaveData(net.minecraft.world.level.storage.ValueInput \1)',
        c
    )

    # ── 9. addEntityCrashInfo → fillCrashReportCategory ──
    c = re.sub(r'\baddEntityCrashInfo\b', 'fillCrashReportCategory', c)
    c = re.sub(r'\bpublic void fillCrashReportCategory\(CrashReportCategory (\w+)\)',
               r'public void fillCrashReportCategory(net.minecraft.CrashReportCategory \1)', c)

    # ── 10. getItemStackFromSlot → getItemBySlot ──
    c = re.sub(r'\bgetItemStackFromSlot\(', 'getItemBySlot(', c)

    # ── 11. getHeldItem(hand) → getItemInHand(hand) ──
    c = re.sub(r'\bgetHeldItem\(', 'getItemInHand(', c)
    c = re.sub(r'\bgetHeldItemMainhand\(\)', 'getMainHandItem()', c)
    c = re.sub(r'\bgetHeldItemOffhand\(\)', 'getOffhandItem()', c)

    # ── 12. setMutexBits → setFlags ──
    # In Goal: setMutexBits(n) → setFlags(EnumSet.of(...))
    # For simple cases, stub with comment
    c = re.sub(r'\bsetMutexBits\((\d+)\)', r'/* setMutexBits(\1) - use setFlags(EnumSet.of(...)) */', c)

    # ── 13. getLookHelper() → getLookControl() ──
    c = re.sub(r'\bgetLookHelper\(\)', 'getLookControl()', c)

    # ── 14. tasks.addTask / targetTasks.addTask ──
    c = re.sub(r'\btasks\.addTask\(', 'goalSelector.addGoal(', c)
    c = re.sub(r'\btargetTasks\.addTask\(', 'targetSelector.addGoal(', c)

    # ── 15. PathPoint → Node ──
    c = re.sub(r'\bPathPoint\b', 'Node', c)

    # ── 16. PathNodeType → PathType ──
    c = re.sub(r'\bPathNodeType\b', 'PathType', c)

    # ── 17. GolemNodeProcessor/FlightNodeProcessor: entity → mob, blockaccess fixes ──
    # In NodeEvaluator subclasses, 'entity' field renamed to 'mob'
    if ('extends NodeEvaluator' in c or 'extends WalkNodeEvaluator' in c or
        'GolemNodeProcessor' in path or 'FlightNodeProcessor' in path):
        # Replace 'entity.' method calls carefully
        c = re.sub(r'\bentity\.(?=getX|getY|getZ|getBound|getBbW|getBbH|isInW|onGround|setPath|getPath|distanceTo|level|getId)',
                   'mob.', c)
        c = re.sub(r'\bentity\.(?=getX|getY|getZ|getBound|getBbW|getBbH|isInW|onGround|setPath|getPath|distanceTo|level|getId)',
                   'mob.', c)
        # blockaccess.getBlockState → currentContext.getBlockState (PathfindingContext has level)
        c = re.sub(r'\bblockaccess\.getBlockState\(', 'currentContext.getBlockState(', c)
        c = re.sub(r'\bblockaccess\.isAirBlock\(', 'currentContext.getBlockState(', c)
        # init → prepare
        c = re.sub(r'\bpublic void init\(BlockGetter\s+\w+,\s*Mob\s+\w+\)',
                   'public void prepare(net.minecraft.world.level.PathNavigationRegion p, Mob entity)', c)
        c = re.sub(r'\bsuper\.init\(sourceIn,\s*mob\)', 'super.prepare(p, entity)', c)
        # postProcess → done
        c = re.sub(r'\bpublic void postProcess\(\)', 'public void done()', c)
        c = re.sub(r'\bsuper\.postProcess\(\)', 'super.done()', c)

    # ── 18. PathNavigate → PathNavigation ──
    c = re.sub(r'\bextends PathNavigate\b', 'extends net.minecraft.world.entity.ai.navigation.PathNavigation', c)
    c = re.sub(r'\bextends GroundPathNavigation\b', 'extends net.minecraft.world.entity.ai.navigation.GroundPathNavigation', c)
    c = re.sub(r'\bextends FlyingPathNavigation\b', 'extends net.minecraft.world.entity.ai.navigation.FlyingPathNavigation', c)

    # ── 19. getEquipmentAndArmor → inventory related ──
    c = re.sub(r'\bgetEquipmentAndArmor\(\)', 'getAllSlots()', c)

    # ── 20. Block.REGISTRY.getNameForObject → BuiltInRegistries.BLOCK.getKey ──
    c = re.sub(
        r'\bBlock\.REGISTRY\.getNameForObject\((\w+)\)',
        r'net.minecraft.core.registries.BuiltInRegistries.BLOCK.getKey(\1)',
        c
    )

    # ── 21. Block.getBlockFromName → stub ──
    c = re.sub(
        r'\bBlock\.getBlockFromName\(([^)]+)\)',
        r'net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(net.minecraft.resources.ResourceLocation.parse(\1))',
        c
    )

    # ── 22. block.getMetaFromState / getStateFromMeta → remove/stub ──
    c = re.sub(r'\b\w+\.getMetaFromState\([^)]+\)', '0 /* getMetaFromState removed */', c)
    c = re.sub(r'\b\w+\.getStateFromMeta\([^)]+\)', 'net.minecraft.world.level.block.Blocks.AIR.defaultBlockState() /* getStateFromMeta removed */', c)

    # ── 23. Block.getBlockById / Block.getIdFromBlock → stubs ──
    c = re.sub(r'\bBlock\.getBlockById\(([^)]+)\)',
               r'net.minecraft.core.registries.BuiltInRegistries.BLOCK.byId(\1)', c)
    c = re.sub(r'\bBlock\.getIdFromBlock\(([^)]+)\)',
               r'net.minecraft.core.registries.BuiltInRegistries.BLOCK.getId(\1)', c)

    # ── 24. addCrashSection → addDetail ──
    c = re.sub(r'\baddCrashSection\(', 'addDetail(', c)

    # ── 25. level().mayPlace(block, pos, ...) → level().isUnobstructed(blockstate, pos, CollisionContext.empty()) ──
    c = re.sub(r'\blevel\(\)\.mayPlace\([^)]+\)', 'true /* mayPlace stubbed */', c)

    # ── 26. super.init(sourceIn, mob) / init patterns ──
    c = re.sub(r'\bsuper\.init\((\w+),\s*(\w+)\)', r'super.prepare(\1, \2)', c)

    # ── 27. taskOwner field in Goal classes → use stored mob reference ──
    # Goals in 1.21 store 'mob' not 'taskOwner'
    if 'extends Goal' in c or 'extends TargetGoal' in c or 'extends NearestAttackableTargetGoal' in c:
        c = re.sub(r'\btaskOwner\b', 'mob', c)

    # ── 28. WalkNodeProcessor → WalkNodeEvaluator ──
    c = re.sub(r'\bWalkNodeProcessor\b', 'WalkNodeEvaluator', c)

    # ── 29. (AbstractGoalSelector → goalSelector) ──

    # ── 30. isEntityUndead → isUndead ──
    c = re.sub(r'\bisEntityUndead\(\)', 'isUndead()', c)

    # ── 31. getAttackTarget / setAttackTarget ──
    c = re.sub(r'\bgetAttackTarget\(\)', 'getTarget()', c)
    c = re.sub(r'\bsetAttackTarget\(', 'setTarget(', c)

    # ── 32. motionY/X/Z remaining ──
    c = re.sub(r'\bmotionY\b(?!\s*[+\-\*\/]?=)', 'getDeltaMovement().y', c)
    c = re.sub(r'\bmotionX\b(?!\s*[+\-\*\/]?=)', 'getDeltaMovement().x', c)
    c = re.sub(r'\bmotionZ\b(?!\s*[+\-\*\/]?=)', 'getDeltaMovement().z', c)
    c = re.sub(r'\bmotionY\s*-=\s*([^;]+);',
               r'setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y - \1, getDeltaMovement().z);', c)
    c = re.sub(r'\bmotionY\s*\+=\s*([^;]+);',
               r'setDeltaMovement(getDeltaMovement().x, getDeltaMovement().y + \1, getDeltaMovement().z);', c)

    # ── 33. getGetLevel() / entity.getLevel() → entity.level() ──
    c = re.sub(r'\b(\w+)\.getLevel\(\)', r'\1.level()', c)

    # ── 34. World remaining references ──
    # world.* in non-entity classes (blocks, etc.) - these usually have a Level parameter
    # This is context-specific, handle per-class

    # ── 35. CrashReportCategory import ──
    if 'CrashReportCategory' in c and 'import net.minecraft.CrashReportCategory' not in c and 'import net.minecraft.world.level.block.entity.BlockEntity' not in c:
        pass  # CrashReportCategory may already be imported

    # ── 36. Identifier → ResourceLocation ──
    c = re.sub(r'\bIdentifier\b', 'net.minecraft.resources.ResourceLocation', c)
    c = re.sub(r'^import net\.minecraft\.resources\.Identifier;',
               'import net.minecraft.resources.ResourceLocation;', c, flags=re.MULTILINE)

    # ── 37. BlockGetter in NodeEvaluator ──
    if 'NodeEvaluator' in c:
        c = re.sub(r'\bBlockGetter\s+sourceIn\b', 'net.minecraft.world.level.PathNavigationRegion sourceIn', c)

    # ── 38. Fix PathfindingContext access ──
    if 'GolemNodeProcessor' in path or 'FlightNodeProcessor' in path:
        # currentContext is the PathfindingContext, get level from mob
        c = re.sub(r'\bblockaccess\b', 'mob.level()', c)

    # ── 39. speed field in entities ──
    # 'this.speed = value' in entity - not a field in 1.21
    # Only in PathNavigate/FlightMoveHelper contexts
    if 'FlightMoveHelper' in path or 'PathNavigate' in path:
        c = re.sub(r'\bthis\.speed\b', 'this.speedModifier', c)

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
