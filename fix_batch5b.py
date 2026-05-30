#!/usr/bin/env python3
"""Batch 5b: Revert the broken instanceof fix, then apply targeted method-param fixes."""
import os, re, sys

SRC = "/home/ash/geostrata/Thaumcraft/src/main/java"
changed = 0

def fix_file(path, content):
    c = content

    # ── 1. Revert `instanceof, Object X` → `instanceof X` ──
    # The batch5 param-type regex wrongly converted `instanceof X` → `instanceof, Object X`
    c = re.sub(r'\binstanceof,\s*Object\s+(\w+)', r'instanceof \1', c)

    # ── 2. Fix specific known bad method signatures with missing types ──
    # Pattern: `preRender(LivingEntity boss renderLivingBase)` in ChampionMod files
    # The original had `RenderLivingBase` as second param type - stub it to Object
    c = re.sub(
        r'\bpreRender\(LivingEntity\s+(\w+)\s+(\w+)\)',
        r'preRender(LivingEntity \1, Object \2)',
        c
    )
    # The interface version
    c = re.sub(
        r'\bpreRender\(LivingEntity\s+(\w+)\s+(\w+)\)',
        r'preRender(LivingEntity \1, Object \2)',
        c
    )

    # ── 3. Fix `canRenderInLayer(BlockState state layer)` ──
    c = re.sub(
        r'\bcanRenderInLayer\(BlockState\s+(\w+)\s+(\w+)\)',
        r'canRenderInLayer(BlockState \1, Object \2)',
        c
    )

    # ── 4. Any remaining wrong `(Type name name)` patterns ONLY in method declarations ──
    # Only in method declaration context: preceded by `) ` or `(Type ...`
    # Use a strict pattern: we need to find places where we have TWO words without comma
    # inside a method parameter that's at the START of a method (preceded by return type keyword)
    # This is complex; skip for now and handle manually

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
