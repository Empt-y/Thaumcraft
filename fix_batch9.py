#!/usr/bin/env python3
"""Batch 9: Fix issues introduced by batch8 - null stubs, double-stubs, broken imports, orphaned params."""
import os, re

SRC = "/home/ash/geostrata/Thaumcraft/src/main/java"
changed = 0

def fix_file(path, content):
    c = content

    # ── 1. Re-collapse double-stubs (batch8 operated inside comments again) ──
    for _ in range(3):
        c = re.sub(
            r'(\w+)\s*/\*\s*\1\s*/\*\s*([^*]*?removed[^*]*?)\s*\*/\s*removed\s*\*/',
            r'\1 /* \2 */',
            c
        )

    # ── 2. Fix broken import statements with embedded /* ... */ stubs ──
    c = re.sub(
        r'^(import\s+[^;]*?)/\*[^*]*removed[^*]*\*/[^;]*;',
        lambda m: '// ' + m.group(0).strip() + '; // broken import',
        c,
        flags=re.MULTILINE
    )

    # ── 3. Fix `null /* nested removed */.field` → `null` ──
    c = re.sub(r'null\s*/\*\s*nested removed\s*\*/\s*\.\s*\w+', 'null', c)

    # ── 4. Fix `null /* nested removed */().method(...)` chain → comment out statement ──
    c = re.sub(
        r'null\s*/\*\s*nested removed\s*\*/\s*\(\s*\)\s*\.[^;]+;',
        '/* removed statement with null chain */',
        c
    )

    # ── 5. Fix `new null /* nested removed */(...)` constructor → `null /* ctor removed */` ──
    # Handle up to one level of nested parens
    c = re.sub(
        r'new\s+null\s*/\*\s*nested removed\s*\*/\s*\([^()]*(?:\([^()]*\)[^()]*)*\)',
        'null /* ctor removed */',
        c
    )

    # ── 6. Fix `null /* nested removed */(...)` method call → `null /* call removed */` ──
    c = re.sub(
        r'null\s*/\*\s*nested removed\s*\*/\s*\([^()]*(?:\([^()]*\)[^()]*)*\)',
        'null /* call removed */',
        c
    )

    # ── 7. Fix return type `public/protected/private ... null /* nested removed */ method(` ──
    c = re.sub(
        r'\b((?:public|protected|private|static|final|abstract|synchronized)\s+)null\s*/\*\s*nested removed\s*\*/(?=\s+\w+\s*\()',
        r'\1Object /* return type removed */',
        c
    )

    # ── 8. Fix `implements Object /* removed */,` → `implements ` (keep remaining interfaces) ──
    c = re.sub(r'\bimplements\s+Object\s*/\*[^*]*removed[^*]*\*/\s*,\s*', 'implements ', c)

    # ── 9. Fix missing `implements` keyword: `extends Foo /* comment */ , Interface` ──
    # Match extends clause ending with optional comment followed by `, Interface`
    c = re.sub(
        r'(extends\s+\w+\s*/\*[^*]*\*/)\s*,\s*(\w)',
        r'\1 implements \2',
        c
    )
    # Also `extends Foo , Interface` (no comment)
    c = re.sub(
        r'(?m)^(public\s+(?:class|abstract\s+class)\s+\w+(?:\s*<[^>]*>)?\s+extends\s+\w+)\s*,\s*(\w)',
        r'\1 implements \2',
        c
    )

    # ── 10. Fix `Foo /* X removed */.Object /* Y removed */` nested type in type position ──
    c = re.sub(
        r'\b\w+\s*/\*[^*]*removed[^*]*\*/\s*\.\s*Object\s*/\*[^*]*removed[^*]*\*/',
        'Object /* nested class removed */',
        c
    )

    # ── 11. Fix orphaned `.Type var` in method param list ──
    # Pattern: valid `Type var` followed by `.CamelType anotherVar` (no comma before `.`)
    # e.g. `Player player.RenderType type` → `Player player`
    # Only when followed by `,` `)` or end of params (not field/method access)
    c = re.sub(
        r'(\b\w+\s+\w+)\.[A-Z]\w+\s+\w+(?=[,)\n])',
        r'\1',
        c
    )

    # ── 12. Dedup imports ──
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
