#!/usr/bin/env python3
"""Repair broken import statements caused by over-aggressive world. → level(). replacement."""
import os, re

SRC = "/home/ash/geostrata/Thaumcraft/src/main/java"
changed = 0

def fix_file(path, content):
    c = content
    # The bug replaced `net.minecraft.world.` with `net.minecraft.level().`
    # in import statements. Fix this back.
    c = c.replace('net.minecraft.level().', 'net.minecraft.world.')
    # Also fix package declarations if affected
    c = c.replace('net.neoforged.level().', 'net.neoforged.world.')
    # Fix EntityType references that got broken
    c = re.sub(r'net\.minecraft\.level\(\)\.', 'net.minecraft.world.', c)
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

process_dir(SRC)
print(f"Repaired {changed} files")
