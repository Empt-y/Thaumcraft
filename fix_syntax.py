#!/usr/bin/env python3
"""Fix bad syntax produced by fix_final.py and pre-existing parse errors."""

import os, re

SRC = "src/main/java"

def fix(path, content):
    orig = content

    # ── Fix: entity.40) → 40) (broken getVerticalFaceSpeed replacements) ──
    content = re.sub(r'\b\w+\.40\b', '40', content)

    # ── Fix: ByteTag.valueOf((byte))N) → ByteTag.valueOf((byte)N) ─────────
    # Pattern: ByteTag.valueOf((byte))
    content = re.sub(
        r'net\.minecraft\.nbt\.ByteTag\.valueOf\(\(byte\)\)(\d+)\)',
        r'net.minecraft.nbt.ByteTag.valueOf((byte)\1)',
        content
    )

    # ── Fix: double-commented stepHeight (ran script twice) ───────────────
    content = re.sub(
        r'/\* /\* stepHeight = ([^;]+); \*/ // TODO: override maxUpStep\(\) \*/ // TODO: override maxUpStep\(\)',
        r'/* stepHeight = \1; */ // TODO: override maxUpStep()',
        content
    )
    # Fix: player./* stepHeight... */ → /* player.stepHeight... */
    content = re.sub(
        r'(\w+)\.(/\* stepHeight = [^;]+; \*/ // TODO: override maxUpStep\(\))',
        r'/* \1.stepHeight removed */ \2',
        content
    )
    # Just remove bad stepHeight comment lines entirely
    content = re.sub(
        r'\s*player\./\* stepHeight[^*]*\*/[^\n]*\n',
        '\n',
        content
    )

    # ── Fix: SealBreaker OreDictionary.itemMatches broken replacement ─────
    # false /* OreDictionary removed */[0].value)) → false /* OreDictionary.itemMatches removed */
    content = re.sub(
        r'false /\* OreDictionary removed \*/(\[[^\]]+\]\.\w+)\)',
        r'false /* OreDictionary.itemMatches removed */',
        content
    )

    # ── Fix: TaskHandler distToPoint missing closing paren ────────────────
    if 'TaskHandler.java' in path:
        # Fix: distToPoint(new Vec3(x, y, z); → distToPoint(new Vec3(x, y, z));
        content = re.sub(
            r'(distToPoint\(new net\.minecraft\.world\.phys\.Vec3\([^)]+\))\s*;',
            r'\1);',
            content
        )
        # Fix: distToPoint(new Vec3(x(), y, z) extra ) after first arg
        content = re.sub(
            r'(new net\.minecraft\.world\.phys\.Vec3\([^)]+)\), ([\w.]+\(\)), ([\w.]+\(\))\)',
            r'\1, \2, \3)',
            content
        )
        # Fix: if (d < d2)) → if (d < d2)
        content = re.sub(r'\(d < d2\)\)', '(d < d2)', content)
        content = re.sub(r'\(d2 < d\)\)', '(d2 < d)', content)

    # ── Fix: FocusEffectCurse distToPoint missing ) before <= ─────────────
    if 'FocusEffectCurse.java' in path or 'FocusEffectFrost.java' in path:
        # distToPoint(new Vec3(x, y, z) <= f  → distToPoint(new Vec3(x, y, z)) <= f
        content = re.sub(
            r'(distToPoint\(new net\.minecraft\.world\.phys\.Vec3\([^)]+\))\s*<=',
            r'\1) <=',
            content
        )
        # Fix: getPackage()).world → getPackage().world
        content = content.replace('getPackage()).world', 'getPackage().world')
        # Fix: setBlockAndUpdate(block.above()), nextBlock) → setBlockAndUpdate(block.above(), nextBlock)
        content = re.sub(
            r'setBlockAndUpdate\(([^,]+)\), ([^)]+)\)',
            r'setBlockAndUpdate(\1, \2)',
            content
        )

    # ── Fix: FocusEffectAir and similar: getPackage().world.scheduleUpdate → scheduleTick
    # Already done in fix_final.py

    # ── Fix: CasterManager.get() → Optional.get() issue ──────────────────
    # The error was "no suitable method found for get(no arguments)"
    # The old code calls .get() on Optional which needs orElse or similar
    if 'CasterManager.java' in path:
        # .get() on Optional → .orElse(null) or .orElseThrow()
        content = re.sub(r'(\w+)\s*\.\s*get\(\)(?!\s*\()', r'\1.orElse(null)', content)

    # ── Fix: AuraChunk.get("") → .get() ──────────────────────────────────
    if 'AuraChunk.java' in path:
        content = content.replace('.get("")', '.get()')

    # ── Fix: ItemGrappleGun stack.get() → use orElse ─────────────────────
    if 'ItemGrappleGun.java' in path:
        content = re.sub(r'\bstack\.get\(\)\.', 'stack.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).getUnsafe().', content)

    return content

def process():
    changed = 0
    for root, dirs, files in os.walk(SRC):
        dirs[:] = [d for d in dirs if d not in ['.git', 'build']]
        for fn in files:
            if not fn.endswith('.java'):
                continue
            path = os.path.join(root, fn)
            with open(path, 'r', encoding='utf-8', errors='replace') as f:
                content = f.read()
            new = fix(path, content)
            if new != content:
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(new)
                changed += 1
                print(f"Fixed: {path[len(SRC)+1:]}")
    print(f"\nTotal: {changed}")

if __name__ == '__main__':
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    process()
