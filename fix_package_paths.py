#!/usr/bin/env python3
"""Fix remaining .level(). in package declarations and all declarations."""
import os, re

SRC = "/home/ash/geostrata/Thaumcraft/src/main/java"
changed = 0

def fix_file(path, content):
    c = content
    # Fix package declarations
    # Fix ANY remaining .level(). in non-method-call contexts
    # In declarations (package, import, class references), replace .level(). with .world.
    lines = c.split('\n')
    result = []
    for line in lines:
        stripped = line.strip()
        if stripped.startswith('package ') or stripped.startswith('import '):
            line = line.replace('.level().', '.world.')
        result.append(line)
    return '\n'.join(result)

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

process_dir(SRC)
print(f"Fixed {changed} files")
