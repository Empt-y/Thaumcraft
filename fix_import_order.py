#!/usr/bin/env python3
"""Fix import statements that were inserted before the package declaration."""
import os, re

SRC = "/home/ash/geostrata/Thaumcraft/src/main/java"
changed = 0

def fix_file(path, content):
    # Check if file starts with `import` before `package`
    lines = content.split('\n')
    if not lines or not lines[0].startswith('import '):
        return content
    
    # Find the package line index
    pkg_idx = None
    for i, line in enumerate(lines):
        if line.strip().startswith('package '):
            pkg_idx = i
            break
    
    if pkg_idx is None:
        return content
    
    # Collect all leading import lines (before the package)
    leading_imports = []
    for i in range(pkg_idx):
        if lines[i].strip().startswith('import '):
            leading_imports.append(lines[i])
    
    if not leading_imports:
        return content
    
    # Remove leading imports from their current position
    new_lines = [l for l in lines[:pkg_idx] if not l.strip().startswith('import ')]
    # Add package line
    new_lines.append(lines[pkg_idx])
    # Add the imports after the package line
    new_lines.extend(leading_imports)
    # Add rest of file
    new_lines.extend(lines[pkg_idx + 1:])
    
    return '\n'.join(new_lines)

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
