#!/usr/bin/env python3
"""Batch 8: Fix remaining symbol errors - armor/tool/food items, commands, capabilities, rendering."""
import os, re, sys

SRC = "/home/ash/geostrata/Thaumcraft/src/main/java"
changed = 0

def fix_file(path, content):
    c = content

    # ── 1. Remove `implements Object /* X removed */` from class declarations ──
    # These are non-interface objects used in implements which cause "interface expected here"
    # Remove ALL occurrences of `implements ..., Object /* ... removed */` or `, Object /* ... removed */`
    # and `implements Object /* ... removed */` patterns
    # Handle multi-item implements lists
    # First, remove any `, Object /* ... removed */` occurrences
    c = re.sub(r',\s*Object\s*/\*[^*]*removed[^*]*\*/', '', c)
    # Then remove any `implements Object /* ... removed */` that would leave bare implements
    c = re.sub(r'\bimplements\s+Object\s*/\*[^*]*removed[^*]*\*/', '', c)

    # Also handle `Object /* X removed */.Y` nested class access on stubs
    # Replace with just the inner class name or null
    c = re.sub(r'Object\s*/\*[^*]*removed[^*]*\*/\.\w+', 'null /* nested removed */', c)

    # ── 2. SwordItem / ItemSword → Item stub ──
    c = c.replace(
        'import net.minecraft.world.item.SwordItem;',
        '// import net.minecraft.world.item.SwordItem; // removed'
    )
    c = re.sub(r'\bSwordItem\b', 'Item /* SwordItem removed */', c)
    c = re.sub(r'\bItemSword\b', 'Item /* ItemSword removed */', c)

    # ── 3. ItemAxe / ItemHoe / ItemPickaxe / ItemShovel → DiggerItem stubs ──
    c = re.sub(r'\bItemAxe\b', 'Item /* ItemAxe removed */', c)
    c = re.sub(r'\bItemHoe\b', 'Item /* ItemHoe removed */', c)
    c = re.sub(r'\bItemShovel\b', 'Item /* ItemShovel removed */', c)

    # ── 4. ToolMaterial → Item.Properties stub ──
    # In MC 26.x, ToolMaterial is a record at net.minecraft.world.item.ToolMaterial
    # but old usage pattern `ToolMaterial.X` (enum values) is different
    c = re.sub(r'\bToolMaterial\.(\w+)\b', r'null /* ToolMaterial.\1 removed */', c)

    # ── 5. ItemFood → Item stub ──
    c = re.sub(r'\bextends\s+ItemFood\b', 'extends Item /* ItemFood removed */', c)
    c = re.sub(r'\bItemFood\b', 'Item /* ItemFood removed */', c)

    # ── 6. ArmorMaterial → stub ──
    # ArmorMaterial as type → Object
    c = re.sub(r'\bArmorMaterial\b', 'Object /* ArmorMaterial removed */', c)

    # ── 7. ArmorProperties → stub ──
    c = re.sub(r'\bArmorProperties\b', 'Object /* ArmorProperties removed */', c)

    # ── 8. EnumAction → ItemUseAnimation ──
    c = re.sub(r'\bEnumAction\b', 'ItemUseAnimation', c)
    if 'ItemUseAnimation' in c and 'import net.minecraft.world.item.ItemUseAnimation;' not in c:
        lines = c.split('\n')
        for i, l in enumerate(lines):
            if l.strip().startswith('import ') or l.strip().startswith('package '):
                last_idx = i
        lines.insert(last_idx + 1, 'import net.minecraft.world.item.ItemUseAnimation;')
        c = '\n'.join(lines)

    # ── 9. RenderType → fix import ──
    c = c.replace(
        'import net.minecraft.client.renderer.RenderType;',
        'import net.minecraft.client.renderer.rendertype.RenderType;'
    )
    if 'RenderType' in c and 'import net.minecraft.client.renderer.rendertype.RenderType;' not in c:
        # Try to add import if RenderType appears
        if re.search(r'\bRenderType\b', c):
            lines = c.split('\n')
            for i, l in enumerate(lines):
                if l.strip().startswith('import ') or l.strip().startswith('package '):
                    last_idx = i
            lines.insert(last_idx + 1, 'import net.minecraft.client.renderer.rendertype.RenderType;')
            c = '\n'.join(lines)

    # ── 10. Fix RegistryEvent → RegisterEvent ──
    c = c.replace(
        'import net.minecraftforge.registries.RegistryEvent;',
        'import net.neoforged.neoforge.registries.RegisterEvent;'
    )
    c = re.sub(
        r'\bRegistryEvent\.Register<SoundEvent>\b',
        'RegisterEvent',
        c
    )
    c = re.sub(r'\bRegistryEvent\b', 'RegisterEvent', c)
    if 'RegisterEvent' in c and 'import net.neoforged.neoforge.registries.RegisterEvent;' not in c:
        lines = c.split('\n')
        for i, l in enumerate(lines):
            if l.strip().startswith('import ') or l.strip().startswith('package '):
                last_idx = i
        lines.insert(last_idx + 1, 'import net.neoforged.neoforge.registries.RegisterEvent;')
        c = '\n'.join(lines)

    # Fix registerSounds method signature to use RegisterEvent
    c = re.sub(
        r'public static void registerSounds\(RegisterEvent\s+event\)',
        'public static void registerSounds(RegisterEvent event)',
        c
    )
    c = re.sub(
        r'private static SoundEvent getRegisteredSoundEvent\(RegisterEvent\s+event',
        'private static SoundEvent getRegisteredSoundEvent(RegisterEvent event',
        c
    )

    # ── 11. Fix Capability → stub ──
    # Old Forge/NeoForge `Capability<T>` from old package
    c = c.replace(
        'import net.neoforged.neoforge.capabilities.Capability;',
        '// import net.neoforged.neoforge.capabilities.Capability; // API changed'
    )
    c = c.replace(
        'import net.minecraftforge.common.capabilities.Capability;',
        '// removed import net.minecraftforge.common.capabilities.Capability'
    )
    c = re.sub(r'\bCapability<[^>]+>', 'Object /* Capability removed */', c)
    c = re.sub(r'\bCapability\b', 'Object /* Capability removed */', c)

    # ── 12. Fix ICapabilitySerializable → stub ──
    c = re.sub(r'\bICapabilitySerializable\b', 'Object /* ICapabilitySerializable removed */', c)

    # ── 13. Fix CommandBase / ICommandSender / CommandException → stub ──
    c = c.replace('import net.minecraft.command.CommandException;', '// CommandException removed')
    c = c.replace('import com.mojang.brigadier.CommandSender;', '// CommandSender removed')
    c = re.sub(r'\bCommandBase\b', 'Object /* CommandBase removed */', c)
    c = re.sub(r'\bICommandSender\b', 'Object /* ICommandSender removed */', c)
    c = re.sub(r'\bCommandException\b', 'Object /* CommandException removed */', c)
    c = re.sub(r'\bCommandSender\b', 'Object /* CommandSender removed */', c)

    # ── 14. Fix IForgeRegistryEntry → stub ──
    c = re.sub(r'\bIForgeRegistryEntry\b', 'Object /* IForgeRegistryEntry removed */', c)

    # ── 15. Fix Mth wrong import ──
    # If Mth is imported from wrong package
    if 'import net.minecraft.util.Mth;' in c and 'Mth' not in c.replace('import net.minecraft.util.Mth;', ''):
        c = c.replace('import net.minecraft.util.Mth;', '')
    # Ensure correct import for Mth
    if re.search(r'\bMth\b', c) and 'import net.minecraft.util.Mth;' not in c:
        lines = c.split('\n')
        for i, l in enumerate(lines):
            if l.strip().startswith('import ') or l.strip().startswith('package '):
                last_idx = i
        lines.insert(last_idx + 1, 'import net.minecraft.util.Mth;')
        c = '\n'.join(lines)

    # ── 16. Fix DiggerItem → check if exists ──
    # DiggerItem doesn't exist in MC 26.x, stub it
    c = re.sub(r'\bDiggerItem\b', 'Item /* DiggerItem removed */', c)

    # ── 17. Fix `extends Item /* ItemArmor removed */ implements` cleanup ──
    # The comment after `extends Item /* ItemArmor removed */` doesn't cause issues
    # but make sure no `/* comment */ implements` confusion

    # ── 18. Fix `super(new Item /* ItemArmor removed */.Properties())` → `super(new Item.Properties())` ──
    c = re.sub(
        r'super\s*\(\s*new\s+Item\s*/\*[^*]*\*/\s*\.\s*Properties\s*\(\s*\)\s*\)',
        'super(new Item.Properties())',
        c
    )

    # ── 19. Dedup imports ──
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
