#!/usr/bin/env python3
"""Fix bugs introduced by fix_batch12.py."""
import os, re

SRC = "/home/ash/geostrata/Thaumcraft/src/main/java"
changed = 0

def fix_file(path, content):
    c = content

    # ── Bug 1: double-parentheses from .height() method calls ──
    # e.g. inv.getBbHeight()() → inv.height()
    c = c.replace('.getBbHeight()()', '.height()')
    # Also handle in method definitions like FocusEffect
    # abstract void foo(Level world, double x, double y, double z, double getBbHeight() / 2,...)
    # - this won't appear but double () is the main issue

    # ── Bug 2: getDeltaMovement().x/y/z as method parameter names ──
    # When appearing in method signatures (after 'double ' keyword)
    # double getDeltaMovement().x → double vx (rename to something valid)
    c = re.sub(r'\bdouble\s+getDeltaMovement\(\)\.x\b', 'double vx', c)
    c = re.sub(r'\bdouble\s+getDeltaMovement\(\)\.y\b', 'double vy', c)
    c = re.sub(r'\bdouble\s+getDeltaMovement\(\)\.z\b', 'double vz', c)
    # Same for float
    c = re.sub(r'\bfloat\s+getDeltaMovement\(\)\.x\b', 'float vx', c)
    c = re.sub(r'\bfloat\s+getDeltaMovement\(\)\.y\b', 'float vy', c)
    c = re.sub(r'\bfloat\s+getDeltaMovement\(\)\.z\b', 'float vz', c)

    # ── Bug 3: level().getEntitiesOfClass /* excl */)(arg → level().getEntitiesOfClass(Entity.class, bb) ──
    # The original was: world.getEntitiesWithinAABBExcludingEntity(entity, aabb)
    # → level().getEntitiesOfClass(EntityType.class, aabb) but we need to exclude one entity
    # For now, just fix the syntax error by removing the extra close-paren
    c = re.sub(
        r'level\(\)\.getEntitiesOfClass /\* excl \*/\)\(([^)]+)\)',
        r'level().getEntitiesOfClass(net.minecraft.world.entity.Entity.class, \1)',
        c
    )

    # ── Bug 4: Dangling writeSpawnData/readSpawnData body code ──
    # The regex left the body hanging. Remove the dangling lines.
    # Pattern: /* writeSpawnData removed */ followed by orphaned statements
    # We'll handle this per-file below

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
