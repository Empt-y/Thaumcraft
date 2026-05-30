#!/usr/bin/env python3
"""Batch 4 fixes: wrong-package imports, Object stubs in class decls, and misc symbol errors."""
import os, re, sys

SRC = "/home/ash/geostrata/Thaumcraft/src/main/java"
changed = 0

def fix_file(path, content):
    c = content
    lines = c.split('\n')

    # ── 1. Remove import lines that are wrong-package duplicates or broken ──
    # These are known-bad import patterns
    BAD_IMPORT_EXACT = {
        # Wrong-package EquipmentSlot
        "import net.minecraft.world.inventory.EquipmentSlot;",
        # Wrong-package Zombie (zombie is in zombie sub-package now)
        "import net.minecraft.world.entity.monster.Zombie;",
        # Wrong-package ExperienceOrb (it moved to entity root)
        "import net.minecraft.world.entity.item.ExperienceOrb;",
        # Wrong-package TamableAnimal
        "import net.minecraft.world.entity.animal.TamableAnimal;",
        # Wrong-package Bat
        "import net.minecraft.world.entity.animal.Bat;",
        # Wrong-package Parrot
        "import net.minecraft.world.entity.animal.Parrot;",
        # Wrong-package AbstractGolem
        "import net.minecraft.world.entity.monster.AbstractGolem;",
        # Wrong-package SmallFireball (it's in hurtingprojectile sub-package)
        "import net.minecraft.world.entity.projectile.SmallFireball;",
        # Wrong-package AbstractIllager (it's in illager sub-package)
        "import net.minecraft.world.entity.monster.AbstractIllager;",
        # Wrong-package AI goals (missing .goal in path)
        "import net.minecraft.world.entity.ai.MoveTowardsRestrictionGoal;",
        "import net.minecraft.world.entity.ai.OpenDoorGoal;",
        "import net.minecraft.world.entity.ai.LeapAtTargetGoal;",
        # Wrong-package navigation (was in pathfinder, now in ai.navigation)
        "import net.minecraft.world.level.pathfinder.PathNavigate;",
        "import net.minecraft.world.level.pathfinder.PathNavigateClimber;",
        "import net.minecraft.world.level.pathfinder.GroundPathNavigation;",
        # Wrong-package ServerBossEvent
        "import net.minecraft.world.ServerBossEvent;",
        # Wrong-package PacketFlow
        "import net.minecraft.network.PacketFlow;",
        # Removed classes
        "import net.minecraft.util.ActionResult;",
        "import net.minecraft.client.util.ITooltipFlag;",
        "import net.minecraft.world.entity.ai.RandomPositionGenerator;",
        "import net.minecraft.util.EntitySelectors;",
        "import net.neoforged.neoforge.event.entity.living.EnderTeleportEvent;",
        "import net.minecraft.world.entity.ai.EntityAIMoveThroughVillage;",
        "import net.minecraft.world.entity.monster.EntityPigZombie;",
        "import net.minecraft.world.item.EnchantedBookItem;",
        "import net.minecraft.world.item.PickaxeItem;",
        "import net.neoforged.neoforge.client.model.data.ModelData;",
        "import net.minecraft.client.resources.model.ModelResourceLocation;",
        # Wrong-package LightLayer (correct is net.minecraft.world.level.LightLayer)
        "import net.minecraft.world.LightLayer;",
        # Wrong-package model data (removed in NeoForge 26.x)
        "import net.neoforged.neoforge.client.model.data.ModelDataManager;",
        "import net.neoforged.neoforge.client.model.data.IModelData;",
        # Wrong zombie imports
        "import net.minecraft.world.entity.monster.zombie.Zombie;",
    }

    new_lines = []
    for line in lines:
        stripped = line.strip()
        # Remove exact bad imports
        if stripped in BAD_IMPORT_EXACT:
            continue
        # Remove any import line containing '/* ' in the package path (broken Object stubs)
        if stripped.startswith('import ') and stripped.endswith(';') and '/*' in stripped:
            continue
        new_lines.append(line)
    c = '\n'.join(new_lines)

    # ── 2. Fix double-imported correct classes (add correct ones if missing) ──

    def ensure_import(text, imp):
        """Add import if not already present."""
        if imp not in text:
            # insert after last existing import or package
            lines2 = text.split('\n')
            idx = 0
            for i, l in enumerate(lines2):
                if l.strip().startswith('import ') or l.strip().startswith('package '):
                    idx = i
            lines2.insert(idx + 1, imp)
            return '\n'.join(lines2)
        return text

    # Ensure correct imports are present for things we use
    # (Only add if the class is actually referenced in the file)
    corrections = {
        'EquipmentSlot': 'import net.minecraft.world.entity.EquipmentSlot;',
        'ExperienceOrb': 'import net.minecraft.world.entity.ExperienceOrb;',
        'TamableAnimal': 'import net.minecraft.world.entity.TamableAnimal;',
        'Bat': 'import net.minecraft.world.entity.ambient.Bat;',
        'Parrot': 'import net.minecraft.world.entity.animal.parrot.Parrot;',
        'AbstractGolem': 'import net.minecraft.world.entity.animal.golem.AbstractGolem;',
        'SmallFireball': 'import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;',
        'AbstractIllager': 'import net.minecraft.world.entity.monster.illager.AbstractIllager;',
        'MoveTowardsRestrictionGoal': 'import net.minecraft.world.entity.ai.goal.MoveTowardsRestrictionGoal;',
        'OpenDoorGoal': 'import net.minecraft.world.entity.ai.goal.OpenDoorGoal;',
        'LeapAtTargetGoal': 'import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;',
        'GroundPathNavigation': 'import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;',
        'ServerBossEvent': 'import net.minecraft.server.level.ServerBossEvent;',
        'Zombie': 'import net.minecraft.world.entity.monster.zombie.Zombie;',
    }
    for classname, imp in corrections.items():
        # Only add if class name actually appears (not inside comments) and import not present
        # Simple check: if classname appears as a word and the specific import isn't there
        if re.search(r'\b' + classname + r'\b', c) and imp not in c:
            c = ensure_import(c, imp)

    # ── 3. Fix PacketFlow package ──
    c = c.replace(
        'import net.minecraft.network.PacketFlow;',
        'import net.minecraft.network.protocol.PacketFlow;'
    )
    # Also fix any remaining wrong PacketFlow imports added by corrections
    # (already handled by adding correct one)

    # ── 4. Fix PathNavigate → PathNavigation ──
    # In imports
    c = c.replace(
        'import net.minecraft.world.level.pathfinder.PathNavigation;',
        'import net.minecraft.world.entity.ai.navigation.PathNavigation;'
    )
    # Rename all occurrences of PathNavigate (old name) to PathNavigation
    c = re.sub(r'\bPathNavigate\b', 'PathNavigation', c)
    # Ensure PathNavigation import if used
    if 'PathNavigation' in c and 'import net.minecraft.world.entity.ai.navigation.PathNavigation;' not in c:
        c = ensure_import(c, 'import net.minecraft.world.entity.ai.navigation.PathNavigation;')

    # ── 5. Fix PathNodeType → PathType ──
    c = c.replace(
        'import net.minecraft.world.level.pathfinder.PathNodeType;',
        'import net.minecraft.world.level.pathfinder.PathType;'
    )
    c = re.sub(r'\bPathNodeType\b', 'PathType', c)

    # ── 6. Fix RandomPositionGenerator → RandomPos / DefaultRandomPos ──
    c = re.sub(r'\bRandomPositionGenerator\b', 'DefaultRandomPos /* RandomPositionGenerator removed */', c)
    if 'DefaultRandomPos' in c and 'import net.minecraft.world.entity.ai.util.DefaultRandomPos;' not in c:
        c = ensure_import(c, 'import net.minecraft.world.entity.ai.util.DefaultRandomPos;')

    # ── 7. Fix EntitySelectors → EntitySelector ──
    c = c.replace(
        'import net.minecraft.util.EntitySelectors;',
        'import net.minecraft.world.entity.EntitySelector;'
    )
    c = re.sub(r'\bEntitySelectors\b', 'EntitySelector', c)

    # ── 8. Fix EntityMoveHelper → MoveControl ──
    c = c.replace(
        'import net.minecraft.entity.ai.EntityMoveHelper;',
        'import net.minecraft.world.entity.ai.control.MoveControl;'
    )
    c = re.sub(r'\bEntityMoveHelper\b', 'MoveControl', c)
    # FlightMoveHelper extends EntityMoveHelper → extends MoveControl
    if 'MoveControl' in c and 'import net.minecraft.world.entity.ai.control.MoveControl;' not in c:
        c = ensure_import(c, 'import net.minecraft.world.entity.ai.control.MoveControl;')

    # ── 9. Fix ActionResult → InteractionResult ──
    c = re.sub(r'\bActionResult\b', 'InteractionResult', c)
    if 'InteractionResult' in c and 'import net.minecraft.world.InteractionResult;' not in c:
        c = ensure_import(c, 'import net.minecraft.world.InteractionResult;')

    # ── 10. Fix ITooltipFlag → TooltipFlag ──
    c = c.replace(
        'import net.minecraft.client.util.ITooltipFlag;',
        'import net.minecraft.world.item.TooltipFlag;'
    )
    c = re.sub(r'\bITooltipFlag\b', 'TooltipFlag', c)
    if 'TooltipFlag' in c and 'import net.minecraft.world.item.TooltipFlag;' not in c:
        c = ensure_import(c, 'import net.minecraft.world.item.TooltipFlag;')

    # ── 11. Fix ModelResourceLocation ──
    # Replace all uses with Object stub
    c = re.sub(r'\bModelResourceLocation\b', 'Object /* ModelResourceLocation removed */', c)

    # ── 12. Fix EnchantedBookItem → stub ──
    c = re.sub(r'\bEnchantedBookItem\b', 'Object /* EnchantedBookItem removed */', c)

    # ── 13. Fix PickaxeItem → stub ──
    c = re.sub(r'\bPickaxeItem\b', 'Object /* PickaxeItem removed */', c)
    c = re.sub(r'\bItemPickaxe\b', 'Object /* ItemPickaxe removed */', c)

    # ── 14. Fix EntityPigZombie → ZombifiedPiglin ──
    c = re.sub(r'\bEntityPigZombie\b', 'ZombifiedPiglin', c)
    if 'ZombifiedPiglin' in c and 'import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;' not in c:
        c = ensure_import(c, 'import net.minecraft.world.entity.monster.zombie.ZombifiedPiglin;')

    # ── 15. Fix EnderTeleportEvent → EntityTeleportEvent ──
    c = c.replace(
        'import net.neoforged.neoforge.event.entity.living.EnderTeleportEvent;',
        'import net.neoforged.neoforge.event.entity.EntityTeleportEvent;'
    )
    c = re.sub(r'\bEnderTeleportEvent\b', 'EntityTeleportEvent', c)

    # ── 16. Fix EntityAIMoveThroughVillage → stub ──
    c = re.sub(r'\bEntityAIMoveThroughVillage\b', 'Goal /* EntityAIMoveThroughVillage removed */', c)

    # ── 17. Fix ByteBuf: add import if missing ──
    if 'ByteBuf' in c and 'import io.netty.buffer.ByteBuf;' not in c:
        c = ensure_import(c, 'import io.netty.buffer.ByteBuf;')

    # ── 18. Fix LightLayer wrong package ──
    # LightLayer is at net.minecraft.world.level.LightLayer (already in BlockGrassAmbient)
    # The wrong one `import net.minecraft.world.LightLayer;` was removed in step 1
    # Ensure correct import is present
    if 'LightLayer' in c and 'import net.minecraft.world.level.LightLayer;' not in c:
        c = ensure_import(c, 'import net.minecraft.world.level.LightLayer;')

    # ── 19. Fix `implements Object /* ... removed */` in class declarations ──
    # Remove `implements Object /* X removed */` and `implements ..., Object /* X removed */`
    # and `, Object /* X removed */`
    c = re.sub(r',\s*Object\s*/\*[^*]*removed[^*]*\*/', '', c)
    c = re.sub(r'\bimplements\s+Object\s*/\*[^*]*removed[^*]*\*/', '', c)
    # Remove the stray `implements` keyword if it's now empty
    c = re.sub(r'\bimplements\s*\n', '\n', c)

    # ── 20. Fix `extends EntityFlying` → `extends Mob` ──
    c = re.sub(r'\bextends\s+EntityFlying\b', 'extends Mob', c)
    if 'extends Mob' in c and 'import net.minecraft.world.entity.Mob;' not in c:
        c = ensure_import(c, 'import net.minecraft.world.entity.Mob;')

    # ── 21. Fix Material usage in block static fields ──
    # Replace `public static Material X;` with `// public static Material X; // removed`
    c = re.sub(
        r'public\s+static\s+Material\s+(\w+)\s*;',
        r'// public static Material \1; // Material removed',
        c
    )
    # Replace `public static final Material X = ...;`
    c = re.sub(
        r'public\s+static\s+(?:final\s+)?Material\s+(\w+)\s*=\s*[^;]+;',
        r'// Material field removed',
        c
    )
    # Remove static blocks that reference barrierMat/FLUID_DEATH_MATERIAL etc.
    c = re.sub(r'\b\w+Mat\s*=\s*new\s+\w+[^;]*;', '// Material init removed', c)
    c = re.sub(r'\bFLUID_\w+_MATERIAL\s*=\s*new\s+[^;]+;', '// Material init removed', c)
    # Remove `private static class MaterialXxx extends Material { ... }`
    # This is complex, do a simple approach: replace references to barrierMat in super()
    c = re.sub(r'\bsuper\s*\(\s*BlockBarrier\.barrierMat\s*\)', 'super(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of())', c)
    c = re.sub(r'\bsuper\s*\(\s*\w+Mat\s*\)', 'super(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of())', c)
    c = re.sub(r'\bsuper\s*\(\s*\w+MATERIAL\s*\)', 'super(net.minecraft.world.level.block.state.BlockBehaviour.Properties.of())', c)

    # ── 22. Fix broken return type `Object /* X */.Object /* Y */` ──
    # e.g. `Object /* BlockPlanks removed */.Object /* EnumType removed */`
    c = re.sub(r'Object\s*/\*[^*]*\*/\s*\.\s*Object\s*/\*[^*]*\*/', 'Object /* nested removed */', c)

    # ── 23. Fix duplicate Component import ──
    # Remove exact duplicate import lines
    seen_imports = set()
    final_lines = []
    for line in c.split('\n'):
        stripped = line.strip()
        if stripped.startswith('import ') and stripped.endswith(';'):
            if stripped in seen_imports:
                continue
            seen_imports.add(stripped)
        final_lines.append(line)
    c = '\n'.join(final_lines)

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
                print(f"  FIXED: {path[len(SRC)+1:]}")

process_dir(SRC)
print(f"\nTotal files changed: {changed}")
