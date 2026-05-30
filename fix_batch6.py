#!/usr/bin/env python3
"""Batch 6: Fix remaining symbol errors - armor items, rendering classes, baubles, etc."""
import os, re, sys

SRC = "/home/ash/geostrata/Thaumcraft/src/main/java"
changed = 0

def fix_file(path, content):
    c = content

    # ── 1. NodeProcessor → NodeEvaluator ──
    c = c.replace(
        'import net.minecraft.world.level.pathfinder.NodeProcessor;',
        'import net.minecraft.world.level.pathfinder.NodeEvaluator;'
    )
    c = re.sub(r'\bNodeProcessor\b', 'NodeEvaluator', c)

    # ── 2. ItemArmor → Item (stub - armorType field access will also need fixing) ──
    c = re.sub(r'\bextends\s+ItemArmor\b', 'extends Item /* ItemArmor removed */', c)
    c = re.sub(r'\bItemArmor\b', 'Item /* ItemArmor removed */', c)

    # ── 3. ArmorItem → Item stub (ArmorItem removed in MC 26.x) ──
    c = c.replace(
        'import net.minecraft.world.item.ArmorItem;',
        '// import net.minecraft.world.item.ArmorItem; // removed'
    )
    c = re.sub(r'\bArmorItem\b', 'Item /* ArmorItem removed */', c)

    # ── 4. ModelBiped → HumanoidModel ──
    c = c.replace(
        'import net.minecraft.client.model.ModelBiped;',
        'import net.minecraft.client.model.HumanoidModel;'
    )
    c = re.sub(r'\bModelBiped\b', 'HumanoidModel', c)

    # ── 5. UseAnim → ItemUseAnimation ──
    c = c.replace(
        'import net.minecraft.world.item.UseAnim;',
        'import net.minecraft.world.item.ItemUseAnimation;'
    )
    c = re.sub(r'\bUseAnim\b', 'ItemUseAnimation', c)

    # ── 6. InteractionHandSide → HumanoidArm ──
    c = c.replace(
        'import net.minecraft.world.InteractionHandSide;',
        'import net.minecraft.world.entity.HumanoidArm;'
    )
    c = re.sub(r'\bInteractionHandSide\b', 'HumanoidArm', c)

    # ── 7. RenderBiped / RenderManager → stub ──
    c = re.sub(r'\bRenderBiped\b', 'Object /* RenderBiped removed */', c)
    c = re.sub(r'\bRenderManager\b', 'Object /* RenderManager removed */', c)

    # ── 8. IAnimals → Animal (or stub) ──
    c = c.replace(
        'import net.minecraft.world.entity.animal.IAnimals;',
        '// import net.minecraft.world.entity.animal.IAnimals; // removed'
    )
    c = re.sub(r'\bIAnimals\b', 'Animal', c)
    if 'Animal' in c and 'import net.minecraft.world.entity.animal.Animal;' not in c:
        # Add import after any existing import
        lines = c.split('\n')
        for i, l in enumerate(lines):
            if l.strip().startswith('import ') or l.strip().startswith('package '):
                last_import_idx = i
        lines.insert(last_import_idx + 1, 'import net.minecraft.world.entity.animal.Animal;')
        c = '\n'.join(lines)

    # ── 9. Baubles API → stub everything ──
    c = re.sub(r'import\s+baubles\.api\.[^;]+;', '// baubles import removed', c)
    c = re.sub(r'\bIBauble\b', 'Object /* IBauble removed */', c)
    c = re.sub(r'\bBaubleType\b', 'Object /* BaubleType removed */', c)
    c = re.sub(r'\bIRenderBauble\b', 'Object /* IRenderBauble removed */', c)
    c = re.sub(r'\bBaublesApi\b', 'Object /* BaublesApi removed */', c)
    c = re.sub(r'\bIBaublesItemHandler\b', 'Object /* IBaublesItemHandler removed */', c)

    # ── 10. ISpecialArmor → stub ──
    c = re.sub(r'\bISpecialArmor\b', 'Object /* ISpecialArmor removed */', c)

    # ── 11. LightTexture → stub ──
    c = c.replace(
        'import net.minecraft.client.renderer.LightTexture;',
        '// import net.minecraft.client.renderer.LightTexture; // removed'
    )
    c = re.sub(r'\bLightTexture\b', 'Object /* LightTexture removed */', c)

    # ── 12. ItemCameraTransforms → stub ──
    c = c.replace(
        'import net.minecraft.client.renderer.block.model.ItemCameraTransforms;',
        '// import net.minecraft.client.renderer.block.model.ItemCameraTransforms; // removed'
    )
    c = re.sub(r'\bItemCameraTransforms\b', 'Object /* ItemCameraTransforms removed */', c)

    # ── 13. ModelManager wrong package → fix ──
    c = c.replace(
        'import net.minecraft.client.renderer.block.model.ModelManager;',
        'import net.minecraft.client.resources.model.ModelManager;'
    )

    # ── 14. InteractionResult<X> → InteractionResult (not generic) ──
    c = re.sub(r'\bInteractionResult\s*<[^>]+>', 'InteractionResult', c)

    # ── 15. Fix constructor calls that are now broken for Item stub ──
    # `super(ArmorMaterial, int, EquipmentSlot)` - old ItemArmor constructor
    # Just remove invalid super args by replacing with default Item constructor
    c = re.sub(
        r'super\s*\(\s*ThaumcraftMaterials\.\w+\s*,\s*\d+\s*,\s*EquipmentSlot\.\w+\s*\)',
        'super(new Item.Properties())',
        c
    )
    # Any other `super(ArmorMaterial.X, int, EquipmentSlot.Y)` patterns
    c = re.sub(
        r'super\s*\(\s*\w+\s*,\s*\d+\s*,\s*EquipmentSlot\.\w+\s*\)',
        'super(new Item.Properties())',
        c
    )

    # ── 16. Fix BlockBarrier - remove the private static class MaterialBarrier extends Material ──
    # Replace `private static class MaterialBarrier extends Material { ... }`
    # This is complex - just comment it out
    c = re.sub(
        r'private\s+static\s+class\s+MaterialBarrier\s+extends\s+Material\s*\{[^}]*\}',
        '/* private static class MaterialBarrier extends Material removed */',
        c,
        flags=re.DOTALL
    )
    # Also fix `barrierMat = new MaterialBarrier();` reference if it remains
    c = re.sub(r'barrierMat\s*=\s*new\s+MaterialBarrier\s*\(\s*\)\s*;', '// barrierMat init removed', c)

    # ── 17. Fix `Item /* ItemArmor removed */.armorType` field access ──
    c = re.sub(
        r'\(\(\s*Item\s*/\*[^*]*ItemArmor[^*]*\*/\s*\)\s*\w+\.getItem\(\)\s*\)\.armorType',
        'EquipmentSlot.CHEST /* armorType removed */',
        c
    )
    c = re.sub(
        r'\b\w+\.armorType\b',
        'EquipmentSlot.CHEST /* armorType removed */',
        c
    )

    # ── 18. Dedup imports ──
    seen = set()
    out = []
    for line in c.split('\n'):
        s = line.strip()
        if s.startswith('import ') and s.endswith(';'):
            if s in seen:
                continue
            seen.add(s)
        out.append(line)
    c = '\n'.join(out)

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
