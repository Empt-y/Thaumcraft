#!/usr/bin/env python3
"""Fix recursive constructor invocations introduced by batch12."""
import os, re

SRC = "/home/ash/geostrata/Thaumcraft/src/main/java"
changed = 0

def fix_file(path, content):
    c = content
    cls = re.search(r'/(\w+)\.java$', path)
    if not cls:
        return c
    cls = cls.group(1)

    # Pattern: a constructor that takes EntityType as first param but calls this(null, ...)
    # which causes recursion. We need to revert to super(type, ...)
    #
    # The bad pattern looks like:
    #   public ClassName(net.minecraft.world.entity.EntityType<? extends ClassName> type, Level par1World) {
    #       this((net.minecraft.world.entity.EntityType<? extends ClassName>) null, par1World);
    #
    # We need to change it back to:
    #       super(type, par1World);

    # Match: constructor with EntityType type param + this(null cast, ...) body
    # This regex handles the specific pattern
    pattern = (
        r'(public\s+' + re.escape(cls) + r'\s*\(\s*'
        r'net\.minecraft\.world\.entity\.EntityType<\?\s+extends\s+' + re.escape(cls) + r'>\s+type\s*,\s*'
        r'(\w+)\s+(\w+)\s*\)\s*\{[^}]{0,50})'
        r'this\(\(net\.minecraft\.world\.entity\.EntityType<\?\s+extends\s+' + re.escape(cls) + r'>\)\s+null\s*,\s*(\w+)\s*\);'
    )

    def fix_constructor(m):
        full = m.group(0)
        world_type = m.group(2)
        world_var = m.group(3)
        call_var = m.group(4)
        # Replace this(null, var) with super(type, var)
        new_call = f'super(type, {call_var});'
        return full.replace(
            f'this((net.minecraft.world.entity.EntityType<? extends {cls}>) null, {call_var});',
            new_call
        )

    c = re.sub(pattern, fix_constructor, c, flags=re.DOTALL)

    # Also handle simpler patterns where the Level param has different names
    # General pattern: constructor(EntityType<? extends CLS> type, Level ...) {
    #     this((EntityType<? extends CLS>) null, var);
    # → super(type, var)
    c = re.sub(
        r'(\bpublic\s+' + re.escape(cls) + r'\s*\(\s*(?:net\.minecraft\.world\.entity\.)?EntityType<\?\s+extends\s+' + re.escape(cls) + r'>\s+type\b[^)]*\)\s*\{[^}]*?)'
        r'\bthis\(\((?:net\.minecraft\.world\.entity\.)?EntityType<\?\s+extends\s+' + re.escape(cls) + r'>\)\s+null\s*,\s*(\w+)\)',
        lambda m: m.group(1) + f'super(type, {m.group(2)})',
        c,
        flags=re.DOTALL
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
                print(f"  FIXED: {path[len(root)+1:]}")

process_dir(SRC)
print(f"\nTotal files changed: {changed}")
