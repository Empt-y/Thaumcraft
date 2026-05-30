#!/usr/bin/env python3
"""Batch 5: Fix syntax errors introduced by batch 4 (double stubs, broken expressions, missing param types)."""
import os, re, sys

SRC = "/home/ash/geostrata/Thaumcraft/src/main/java"
changed = 0

def fix_file(path, content):
    c = content

    # ── 1. Collapse double stubs ──
    # Pattern: SomeWord /* SomeWord /* Original removed */ removed */
    # Or:      Object /* Object /* Original removed */ removed */
    # These were created by running regex on text inside comments
    def collapse_double_stubs(text):
        # Replace `X /* X /* Y removed */ removed */` → `Object /* Y removed */`
        text = re.sub(
            r'(\w+)\s*/\*\s*\1\s*/\*\s*([^*]*?removed)\s*\*/\s*removed\s*\*/',
            r'Object /* \2 */',
            text
        )
        # Also handle `Object /* Object /* Y removed */ removed */`
        text = re.sub(
            r'Object\s*/\*\s*Object\s*/\*\s*([^*]*?removed)\s*\*/\s*removed\s*\*/',
            r'Object /* \1 */',
            text
        )
        return text

    c = collapse_double_stubs(c)

    # ── 2. Fix broken method parameter lists where type is missing ──
    # Pattern: `(Type name name)` where second name has no type
    # These come from old `RenderLivingBase` / other removed params
    # Fix: `void preRender(LivingEntity p0 p1)` → `void preRender(LivingEntity p0, Object p1)`
    # Fix: `void preRender(LivingEntity boss renderLivingBase)` → `void preRender(LivingEntity boss, Object renderLivingBase)`
    # Approach: find parameter-style patterns where two identifiers appear without comma
    # This is a simple heuristic: "word word word)" inside a method signature
    c = re.sub(
        r'(\(\w+(?:<[^>]*>)?\s+\w+)\s+(\w+\))',
        r'\1, Object \2',
        c
    )

    # ── 3. Fix `2.getEnchantedItemStack(...)` → `ItemStack.EMPTY /* enchanted book removed */` ──
    # The original was `EnchantedBookItem.getEnchantedItemStack(new EnchantmentData(...))`
    # which became `Object /* ... */.getEnchantedItemStack(...)` which became `2.getEnchantedItemStack`
    # Wait, it became `Object /* EnchantedBookItem removed */.getEnchantedItemStack(...)`
    # Actually the way this happened: `(2, EnchantedBookItem.getEnchantedItemStack(...))` -
    # the `EnchantedBookItem` part was replaced by `Object /* ... */`
    # The broken pattern is `\d+.getEnchantedItemStack` - replace the whole argument
    c = re.sub(
        r'\d+\.getEnchantedItemStack\([^)]*\)',
        r'ItemStack.EMPTY /* enchanted book removed */',
        c
    )

    # ── 4. Fix `new Goal /* ... */(...)` → comment out ──
    # `new Goal /* ... removed */(arg1, arg2, arg3)` is invalid
    # Replace with `null /* goal removed */`
    c = re.sub(
        r'\bnew\s+Goal\s*/\*[^*]*removed[^*]*\*/\s*\([^)]*\)',
        r'null /* goal removed */',
        c
    )

    # ── 5. Fix `canRenderInLayer(BlockState state layer)` - add type ──
    # These are methods where a BlockRenderLayer/BlockRenderType param type was removed
    # Look for `(Type name name)` pattern in method signatures that's still wrong
    # (Step 2 above should handle this, but let's also handle 3-param cases)

    # ── 6. Fix `Object /* ModelResourceLocation removed */.CUTOUT` ──
    # `Object /* BlockRenderLayer removed */.CUTOUT` etc - field access on Object stub
    # This is already non-compilable but not introducing new errors beyond "cannot find symbol"
    # For now, remove the whole condition or replace with `true`
    # Pattern: `layer == Object /* BlockRenderLayer removed */.CUTOUT`
    c = re.sub(
        r'layer\s*==\s*Object\s*/\*[^*]*removed[^*]*\*/\s*\.\w+',
        r'true /* layer check removed */',
        c
    )

    # ── 7. Fix `getToolClasses(stack).contains(...)` which doesn't exist ──
    # Replace the whole `instanceof Object /* PickaxeItem removed */ || stack.getToolClasses(...)`
    # with just `true /* pickaxe check stubbed */`
    c = re.sub(
        r'stack\.getItem\(\)\s*instanceof\s*Object\s*/\*[^*]*PickaxeItem[^*]*\*/\s*\|\|\s*stack\.getItem\(\)\.getToolClasses\(\w+\)\.contains\("pickaxe"\)',
        r'true /* pickaxe check stubbed */',
        c
    )
    c = re.sub(
        r'getHeldItemMainhand\(\)\.getItem\(\)\s*instanceof\s*Object\s*/\*[^*]*PickaxeItem[^*]*\*/\s*\|\|\s*getHeldItemMainhand\(\)\.getItem\(\)\.getToolClasses\([^)]*\)\.contains\("pickaxe"\)',
        r'true /* pickaxe check stubbed */',
        c
    )

    # ── 8. Fix `DefaultRandomPos /* ... */.findRandom...` → `null /* random pos removed */` ──
    c = re.sub(
        r'DefaultRandomPos\s*/\*[^*]*removed[^*]*\*/\s*\.\s*\w+\([^)]*\)',
        r'null /* random pos removed */',
        c
    )

    # ── 9. Dedup imports again after all changes ──
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
