#!/usr/bin/env python3
"""Fix import statements inserted before package declarations."""
import os, re

SRC = "/home/ash/geostrata/Thaumcraft/src/main/java"
changed = 0

def fix_file(path, content):
    lines = content.split('\n')
    pkg_idx = next((i for i, l in enumerate(lines) if l.strip().startswith('package ')), -1)
    if pkg_idx <= 0:
        return content
    # There's content before package - move imports after package
    before = lines[:pkg_idx]
    after = lines[pkg_idx:]
    imports_before = [l for l in before if l.strip().startswith('import ')]
    other_before = [l for l in before if not l.strip().startswith('import ')]
    # Remove trailing blanks from other_before
    while other_before and not other_before[-1].strip():
        other_before.pop()
    result = other_before + after[:1] + imports_before + after[1:]
    return '\n'.join(result)

def process_dir(root):
    global changed
    for dirpath, dirs, files in os.walk(root):
        for fname in files:
            if not fname.endswith('.java'):
                continue
            path = os.path.join(dirpath, fname)
            with open(path, 'r', encoding='utf-8', errors='replace') as f:
                orig = f.read()
            fixed = fix_file(path, orig)
            if fixed != orig:
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(fixed)
                changed += 1
                print(f"  FIXED: {path[len(root)+1:]}")

process_dir(SRC)
print(f"Fixed {changed} files")
