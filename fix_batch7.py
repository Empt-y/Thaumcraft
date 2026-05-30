#!/usr/bin/env python3
"""Batch 7: Fix nested block-comment stubs and double-stub class declarations."""
import os, re, sys

SRC = "/home/ash/geostrata/Thaumcraft/src/main/java"
changed = 0

def fix_file(path, content):
    c = content

    # ── 1. Fix nested `/* ... */` inside JavaDoc or other block comments ──
    # Replace `/* X removed */` inside `/** ... */` comment blocks with `[X removed]`
    # Strategy: find `/** ... */` blocks and clean up nested `/* */` within them
    def clean_javadoc(m):
        inner = m.group(0)
        # Replace nested /* ... */ with [...]
        inner = re.sub(r'/\*([^*]|(\*[^/]))*\*/', lambda mm: '[' + mm.group(0)[2:-2].strip() + ']', inner)
        return inner
    # This is complex - instead do a simpler approach:
    # Find `/* X removed */` inside text that's already inside `/**`
    # Actually simplest: replace any `Item /* ItemArmor removed */` in javadoc to `Item[ItemArmor removed]`
    c = re.sub(
        r'(/\*\*.*?\*/)',
        lambda m: re.sub(r'/\*([^*]*)\*/', r'[\1]', m.group(0)),
        c,
        flags=re.DOTALL
    )

    # ── 2. Collapse double-stub in class declarations ──
    # `extends Item /* Item /* ItemArmor removed */ removed */`
    # → `extends Item /* ItemArmor removed */`
    c = re.sub(
        r'\bextends\s+Item\s*/\*\s*Item\s*/\*\s*([^*]*?removed)\s*\*/\s*removed\s*\*/',
        r'extends Item /* \1 */',
        c
    )
    # Also fix `implements Object /* Object /* X removed */ removed */`
    c = re.sub(
        r'\bimplements\s+Object\s*/\*\s*Object\s*/\*\s*([^*]*?removed)\s*\*/\s*removed\s*\*/',
        r'/* implements \1 */',
        c
    )
    # General: `X /* X /* Y */ removed */` → `X /* Y */`
    c = re.sub(
        r'(\w+)\s*/\*\s*\1\s*/\*\s*([^*]*?)\s*\*/\s*removed\s*\*/',
        r'\1 /* \2 */',
        c
    )

    # ── 3. Fix `extends Item /* ItemArmor removed */` → `extends Item` ──
    # The `/* ItemArmor removed */` is a comment after Item which is valid Java
    # but check if it causes issues with `implements` that follows
    # Actually `class Foo extends Item /* comment */ implements Bar` is valid Java - OK

    # ── 4. Fix orphaned closing braces from MaterialBarrier removal ──
    # Already handled manually in BlockBarrier.java

    # ── 5. Fix `Item /* ItemArmor removed */.Properties()` → `Item.Properties()` ──
    c = re.sub(
        r'\bItem\s*/\*[^*]*\*/\.Properties\(\)',
        r'Item.Properties()',
        c
    )
    # Also fix `new Item.Properties()` if it has comments in it
    c = re.sub(
        r'new\s+Item\s*/\*[^*]*\*/\.\s*Properties\s*\(\s*\)',
        r'new Item.Properties()',
        c
    )

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
