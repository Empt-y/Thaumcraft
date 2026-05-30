#!/usr/bin/env python3
"""Fix remaining script-introduced bugs."""
import os, re

SRC = "/home/ash/geostrata/Thaumcraft/src/main/java"
changed = 0

def fix_file(path, content):
    c = content

    # ── Fix 1: import paths with level(). ──
    # The repair script only fixed net.minecraft.level(). but missed thaumcraft.*.level().
    # Also fix any remaining level(). in import statements (not in net.minecraft path)
    # Replace all occurrences of .level(). in import paths (not in method calls)
    def fix_import_level(line):
        if line.strip().startswith('import '):
            return line.replace('.level().', '.world.')
        return line

    lines = c.split('\n')
    lines = [fix_import_level(l) for l in lines]
    c = '\n'.join(lines)

    # ── Fix 2: /* faceEntity removed */ with dangling args ──
    # Pattern: /* faceEntity removed */(stuff)
    # Remove the entire statement including the dangling parens
    c = re.sub(r'/\* faceEntity removed \*/[^;]+;', '/* faceEntity removed */', c)
    # Also handle multi-paren cases that were already comment+args on same line
    c = re.sub(r'/\* faceEntity removed \*/', '/* faceEntity removed */', c)

    # ── Fix 3: EntityUtils.java - remaining getEntitiesOfClass issues ──
    # The fix_script_bugs.py may not have caught all patterns
    c = re.sub(
        r'level\(\)\.getEntitiesOfClass /\* excl \*/\)\(([^)]+)\)',
        r'level().getEntitiesOfClass(net.minecraft.world.entity.Entity.class, \1)',
        c
    )

    # ── Fix 4: Double-paren patterns not caught before ──
    # .getBbHeight()() → .height()
    c = c.replace('.getBbHeight()()', '.height()')

    # ── Fix 5: level(). in non-import contexts where it shouldn't be ──
    # If any import still has level(). in a package path:
    def fix_import_full(match):
        # Fix any remaining package.level(). → package.world.
        return match.group(0).replace('.level().', '.world.')

    c = re.sub(r'^import [^;]+;', fix_import_full, c, flags=re.MULTILINE)

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
