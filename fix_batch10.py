#!/usr/bin/env python3
"""Batch 10: Fix remaining null-stub syntax errors from batch8/9."""
import os, re

SRC = "/home/ash/geostrata/Thaumcraft/src/main/java"
changed = 0

def fix_file(path, content):
    c = content

    # ── 1. Fix `X.null` field access on null → `null` ──
    # e.g. `BlockTallGrass.null /* nested removed */` → `null`
    # e.g. `Item.null /* ToolMaterial.X removed */` → `null /* ToolMaterial.X removed */`
    c = re.sub(r'\b\w+\.null\b', 'null', c)

    # ── 2. Fix `ItemStack varname = /* removed */` (assignment to block comment) ──
    # These were `Foo varname = method().chain();` where the chain was commented out
    c = re.sub(
        r'(\w+\s+\w+\s*=\s*)/\*\s*removed statement with null chain\s*\*/',
        r'\1null; /* removed */\n//',
        c
    )

    # ── 3. Fix `null /* nested removed */ varname = ...;` local variable declaration ──
    # Replace null as a type in local variable declarations
    c = re.sub(
        r'\bnull\s*/\*\s*nested removed\s*\*/\s+(\w+)(\s*=)',
        r'Object /* removed */ \1\2',
        c
    )

    # ── 4. Fix bare `null(args)` — null used as callable (args may have nested parens) ──
    # As a statement by itself: `null(args);` → `/* null call removed */;`
    # Match with 0-2 levels of nested parens
    def fix_null_call_stmt(m):
        return '/* null call removed */;'
    c = re.sub(
        r'\bnull\s*\([^()]*(?:\([^()]*(?:\([^()]*\)[^()]*)*\)[^()]*)*\)\s*;',
        fix_null_call_stmt,
        c
    )

    # ── 5. Fix `if (null(args))` → `if (false /* null call removed */)` ──
    c = re.sub(
        r'\bif\s*\(\s*!?\s*null\s*\([^()]*(?:\([^()]*(?:\([^()]*\)[^()]*)*\)[^()]*)*\)\s*',
        'if (false /* null call removed */ ',
        c
    )

    # ── 6. Fix `null(args).isCanceled()` in if condition → `false` ──
    c = re.sub(
        r'\bnull\s*\([^()]*(?:\([^()]*(?:\([^()]*\)[^()]*)*\)[^()]*)*\)\s*\.\w+\(\s*\)',
        'false /* null call removed */',
        c
    )

    # ── 7. Fix `Type varname = null(complex args)` assignment RHS ──
    # Already handled by step 4 if ends with `;`
    # Additional: `= null(args)` in any expression
    c = re.sub(
        r'=\s*null\s*\([^()]*(?:\([^()]*(?:\([^()]*\)[^()]*)*\)[^()]*)*\)',
        '= null /* call removed */',
        c
    )

    # ── 8. Fix `null /* nested removed */` still used as return type ──
    # e.g. `public null /* nested removed */ getProperties(`
    c = re.sub(
        r'\b((?:public|protected|private|static|final|abstract)\s+(?:(?:public|protected|private|static|final|abstract)\s+)*)null\s*/\*[^*]*removed[^*]*\*/\s+(?=\w+\s*\()',
        r'\1Object /* return type removed */ ',
        c
    )

    # ── 9. Fix `/* removed statement with null chain */` that broke assignments ──
    # Clean up any remaining orphaned `/* ... */` that aren't complete statements
    c = re.sub(
        r'(?m)^\s*/\*\s*removed statement with null chain\s*\*/\s*$',
        '        /* removed statement */',
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
