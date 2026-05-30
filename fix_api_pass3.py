#!/usr/bin/env python3
"""
Pass 3: Fix specific remaining patterns identified from build log.
"""
import os, re, sys, glob

SRC = "src/main/java"

def fix(path, content):
    filename = os.path.basename(path)

    # ── 1. CraftingInput.getStackInSlot → getItem ───────────────────────────
    # CraftingInput (used in recipe matches) has getItem(), not getStackInSlot()
    content = re.sub(r'\b(inv)\b\.getStackInSlot\((\w+)\)', r'\1.getItem(\2)', content)

    # ── 2. Optional<FluidStack>.getOrDefault(DataComponents...) → .get() ────
    # This is wrong: fsOpt.getOrDefault(DataComponents...).copyTag()
    content = re.sub(
        r'(\w+)\.getOrDefault\(net\.minecraft\.core\.component\.DataComponents\.CUSTOM_DATA[^)]+\)\.copyTag\(\)',
        r'\1.get()',
        content
    )

    # ── 3. .world field on FocusPackage → .world (it's a public field) ──────
    # p.getLevel() where p is FocusPackage → p.world
    # (The .world field was corrupted to .getLevel() by a prior script)
    # This needs per-file fixing - skip for now

    # ── 4. pack.getLevel() → pack.world ─────────────────────────────────────
    content = re.sub(
        r'\b(pack|p\d?)\b\.getLevel\(\)',
        r'\1.world',
        content
    )

    # ── 5. golem.getGolemEntity().getLevel() → golem.level() ────────────────
    content = re.sub(
        r'\.getGolemEntity\(\)\.getLevel\(\)',
        r'.getGolemEntity().level()',
        content
    )

    # ── 6. tile.world.dimension() → tile.getLevel().dimension() ──────────────
    content = re.sub(
        r'(\w+)\.world\.dimension\(\)',
        r'\1.getLevel().dimension()',
        content
    )

    # ── 7. Remove setCreativeTab, setUnlocalizedName, setRegistryName ────────
    # These should already be removed, but check for any missed
    content = re.sub(r'\.\s*setCreativeTab\s*\([^)]*\)', '', content)
    content = re.sub(r'\.\s*setUnlocalizedName\s*\([^)]*\)', '', content)
    content = re.sub(r'\.\s*setRegistryName\s*\([^)]*\)', '', content)
    content = re.sub(r'\.\s*setHasSubtypes\s*\([^)]*\)', '', content)
    content = re.sub(r'\.\s*setMaxStackSize\s*\([^)]*\)', '', content)

    # ── 8. CustomData.getIntOr/getStringOr/etc → getCompoundTag().getXxxOr ──
    # stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).getIntOr(...)
    # → stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getIntOr(...)
    content = re.sub(
        r'(getOrDefault\(net\.minecraft\.core\.component\.DataComponents\.CUSTOM_DATA[^)]+\))\s*\.\s*(getIntOr|getStringOr|getByteOr|getShortOr|getFloatOr|getLongOr|contains|getInt|getString|getByte|getShort|getFloat|getLong|putInt|putString|putByte|putFloat|putLong|getBoolean|putBoolean)\b',
        r'\1.copyTag().\2',
        content
    )

    # ── 9. ItemStack.get().getIntOr → just ItemStack.getIntOr (wrong) ────────
    # player.getItemInHand(hand).get().getIntOr → needs DataComponents
    # For now just remove the .get() on ItemStack
    content = re.sub(r'(getItemInHand\([^)]+\))\.get\(\)', r'\1', content)
    content = re.sub(r'(getMainHandItem\(\))\.get\(\)', r'\1', content)
    content = re.sub(r'(getOffhandItem\(\))\.get\(\)', r'\1', content)
    content = re.sub(r'(getItemBySlot\([^)]+\))\.get\(\)', r'\1', content)

    # Then fix ItemStack.getIntOr/getStringOr → DataComponents access
    content = re.sub(
        r'(\w+)\.(getIntOr|getStringOr|getByteOr|getShortOr)\(("[^"]+"),\s*([^)]+)\)',
        lambda m: (
            f'{m.group(1)}.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, '
            f'net.minecraft.world.item.component.CustomData.EMPTY).copyTag().{m.group(2)}({m.group(3)}, {m.group(4)})'
            if m.group(1) not in ('nbt', 'tag', 'compound', 'nbttagcompound', 'rs', 'nbtCompound', 'par1CompoundTag',
                                   'input', 'output', 'data', 'stackNBT', 'itemNBT', 'nbtData')
            else m.group(0)
        ),
        content
    )

    # ── 10. FMLCommonHandler.instance().getSide() → DistExecutor pattern ──
    # Remove FMLCommonHandler usages
    content = re.sub(r'\bFMLCommonHandler\.instance\(\)\.getSide\(\)', 'dist', content)
    content = re.sub(r'\bFMLCommonHandler\.instance\(\)[.\w()]*', 'null /* FMLCommonHandler removed */', content)

    # ── 11. ENCHANTMENT registry variable ─────────────────────────────────────
    # Old: Registries.ENCHANTMENT → net.minecraft.core.registries.Registries.ENCHANTMENT
    content = content.replace(
        'ENCHANTMENT',
        'net.minecraft.core.registries.Registries.ENCHANTMENT'
    )
    # But fix double replacements
    content = content.replace(
        'net.minecraft.core.registries.Registries.net.minecraft.core.registries.Registries.ENCHANTMENT',
        'net.minecraft.core.registries.Registries.ENCHANTMENT'
    )

    # ── 12. Registries.ENCHANTMENT in context ─────────────────────────────────
    # If ENCHANTMENT was used as a registry key → keep the fix above

    # ── 13. .relative(int,int,int) → .offset(int,int,int) ────────────────────
    content = re.sub(
        r'\.relative\((-?[\w.]+),\s*(-?[\w.]+),\s*(-?[\w.]+)\)',
        r'.offset(\1, \2, \3)',
        content
    )

    # ── 14. getPackage() on FocusEffect/FocusMedium ──────────────────────────
    # TC specific: getPackage() on focus effects → this.pack or this.getPackage()
    # If the class has a field 'pack' of type FocusPackage, use pack directly
    # This is complex; leave for now

    # ── 15. Entity.getEntity() wrong → need entity var ───────────────────────
    # This is usually ticket.getEntity() which should work if Task has getEntity()

    # ── 16. getCompound("key") → getCompound("key").orElse(new CompoundTag())
    # Already done in pass2, but check for duplicates
    content = re.sub(
        r'\.orElse\(new net\.minecraft\.nbt\.CompoundTag\(\)\)\.orElse\(',
        '.orElse(',
        content
    )

    # ── 17. world variable as old field (entity.world, tile.world) ────────────
    # tile.world after getLevel() fix → already done

    # ── 18. getWorld() in block entities → getLevel() ────────────────────────
    if 'extends Tile' in content or 'extends BlockEntity' in content:
        content = re.sub(r'(?<![.\w])getWorld\(\)', 'getLevel()', content)

    # ── 19. isDeadOrDying() ───────────────────────────────────────────────────
    content = re.sub(r'(?<!\w)isDead\(\)(?!\w)', 'isDeadOrDying()', content)

    # ── 20. getUnlocalizedName() → getDescriptionId() ────────────────────────
    content = content.replace('.getUnlocalizedName()', '.getDescriptionId()')

    # ── 21. sendMessage(Component) → sendSystemMessage(Component) ────────────
    # But only when arg is already a Component, not a String
    content = re.sub(
        r'\.sendMessage\(Component\.',
        '.sendSystemMessage(Component.',
        content
    )

    # ── 22. Remove random = ... initialization with old random ────────────────
    # Items no longer have a random field

    # ── 23. .getMob() → .getEntity() (already done in pass1, double-check) ───
    content = content.replace('.getMob()', '.getEntity()')

    # ── 24. ItemStack(CompoundTag) constructor removed ───────────────────────
    # No easy fix; leave as stub

    return content

def process_file(path):
    with open(path, 'r', encoding='utf-8', errors='replace') as f:
        content = f.read()
    fixed = fix(path, content)
    if fixed != content:
        with open(path, 'w', encoding='utf-8') as f:
            f.write(fixed)
        return True
    return False

def main():
    changed = 0
    files = glob.glob(f"{SRC}/**/*.java", recursive=True)
    for path in sorted(files):
        try:
            if process_file(path):
                changed += 1
        except Exception as e:
            print(f"ERROR {path.split('/')[-1]}: {e}", file=sys.stderr)
    print(f"Modified {changed}/{len(files)} files")

if __name__ == '__main__':
    main()
