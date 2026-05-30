#!/usr/bin/env python3
"""Pass 5: Fix specific patterns from build log analysis."""
import os, re, sys, glob

SRC = "src/main/java"

def fix(path, content):
    # ── 1. FocusPackage.getLevel() → .world ───────────────────────────────────
    content = content.replace('getPackage().getLevel()', 'getPackage().world')
    content = content.replace('getPackage().getPackage().getLevel()', 'getPackage().getPackage().world')

    # ── 2. Entity.getLevel() → Entity.level() ──────────────────────────────────
    # For entity variables accessed via event/common patterns
    content = re.sub(r'(\.getEntity\(\))\.getLevel\(\)', r'\1.level()', content)
    content = re.sub(r'(\.getItemEntity\(\))\.getLevel\(\)', r'\1.level()', content)
    content = re.sub(r'(\.getCaster\(\))\.getLevel\(\)', r'\1.level()', content)
    content = re.sub(r'(\.getGolemEntity\(\))\.getLevel\(\)', r'\1.level()', content)
    # entity.getLevel() where entity is a local Entity variable
    content = re.sub(
        r'\b(entity|mob|living|golem|attacker|defender|target|hitter|player|entityIn|e)\b\.getLevel\(\)',
        lambda m: m.group(1) + '.level()',
        content
    )

    # ── 3. Random field in non-entity Item classes ─────────────────────────────
    # itemRand used in old Item classes — replace with new RandomSource
    content = re.sub(r'\b(\w+)\.itemRand\b', r'\1.getRandom()', content)
    content = re.sub(r'\bItemCurio\.itemRand\b', 'net.minecraft.util.RandomSource.create()', content)
    content = re.sub(r'\bItemPechWand\.itemRand\b', 'net.minecraft.util.RandomSource.create()', content)
    # Bare 'random' in non-entity Item methods where it's used as field
    # In these contexts: random.nextFloat() → new java.util.Random().nextFloat()
    # But better: use level.getRandom() when level is available as param
    content = re.sub(
        r'(?<![.\w])random\.nextFloat\(\)(?!\s*\()',
        'net.minecraft.util.RandomSource.create().nextFloat()',
        content
    )
    content = re.sub(
        r'(?<![.\w])random\.nextInt\((\w+)\)(?!\s*\()',
        r'net.minecraft.util.RandomSource.create().nextInt(\1)',
        content
    )

    # ── 4. getIntOr on ItemStack directly ─────────────────────────────────────
    # stack.getIntOr("key", 0) → CustomData access
    content = re.sub(
        r'\b(\w+)\.getIntOr\("([^"]+)",\s*(-?\d+)\)',
        lambda m: (
            m.group(1) + '.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, '
            'net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getIntOr("'
            + m.group(2) + '", ' + m.group(3) + ')'
            if m.group(1) not in ('nbt', 'tag', 'compound', 'nbttagcompound', 'rs', 'nbtCompound',
                                   'par1CompoundTag', 'input', 'output', 'data', 'nodenbt',
                                   'nbtIn', 'nbtOut', 'compoundtag', 'tileData', 'tagCompound',
                                   'saveTag', 'readTag', 'nbtData')
            else m.group(0)
        ),
        content
    )
    content = re.sub(
        r'\b(\w+)\.getStringOr\("([^"]+)",\s*("(?:[^"\\]|\\.)*")\)',
        lambda m: (
            m.group(1) + '.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, '
            'net.minecraft.world.item.component.CustomData.EMPTY).copyTag().getStringOr("'
            + m.group(2) + '", ' + m.group(3) + ')'
            if m.group(1) not in ('nbt', 'tag', 'compound', 'nbttagcompound', 'rs', 'nbtCompound',
                                   'par1CompoundTag', 'input', 'output', 'data', 'nodenbt',
                                   'nbtIn', 'nbtOut', 'compoundtag', 'tileData', 'tagCompound',
                                   'saveTag', 'readTag', 'nbtData')
            else m.group(0)
        ),
        content
    )

    # ── 5. Registration methods not removed yet ────────────────────────────────
    content = re.sub(r'\s*\.setCreativeTab\s*\([^)]*\)', '', content)
    content = re.sub(r'\s*\.setUnlocalizedName\s*\([^)]*\)', '', content)
    content = re.sub(r'\s*\.setRegistryName\s*\([^)]*\)', '', content)
    content = re.sub(r'\s*\.setHasSubtypes\s*\([^)]*\)', '', content)
    content = re.sub(r'\s*\.setMaxStackSize\s*\([^)]*\)', '', content)

    # ── 6. getWorld() → level() for entity context ─────────────────────────────
    # Entity.getWorld() doesn't exist → entity.level()
    content = re.sub(
        r'\b(entity|mob|living|golem|player|entityIn)\b\.getWorld\(\)',
        lambda m: m.group(1) + '.level()',
        content
    )
    # For block events: event.getWorld() → event.getLevel()
    content = re.sub(r'\bevent\.getWorld\(\)', 'event.getLevel()', content)

    # ── 7. contains(String) on Optional<CompoundTag> ───────────────────────────
    # After getCompound() → getCompoundOrEmpty(), the .orElse().contains() chain
    # But getCompoundOrEmpty() should return CompoundTag directly, so contains() should work.
    # The issue is older .getCompound() calls that weren't replaced returning Optional.
    # These should have been caught by pass4. Skip for now.

    # ── 8. CraftingInput.getStackInSlot → getItem ──────────────────────────────
    content = re.sub(r'\b(inv|input|craftingInput)\b\.getStackInSlot\((\w+)\)', r'\1.getItem(\2)', content)

    # ── 9. sendSystemMessage with String arg ──────────────────────────────────
    # player.sendSystemMessage(string) → player.sendSystemMessage(Component.literal(string))
    content = re.sub(
        r'\.sendSystemMessage\(("(?:[^"\\]|\\.)*")\)',
        r'.sendSystemMessage(net.minecraft.network.chat.Component.literal(\1))',
        content
    )

    # ── 10. getPackage() method on non-FocusEffect types ──────────────────────
    # FocusNode/FocusEffect has getPackage() returning FocusPackage
    # Leave as-is since it's TC internal API

    # ── 11. FocusPackage.world usage fix ──────────────────────────────────────
    content = re.sub(r'getPackage\(\)\.world\.', 'getPackage().world.', content)

    # ── 12. BlockEntity worldPosition field ───────────────────────────────────
    # Some code uses this.pos which is wrong; in BlockEntity it's worldPosition accessed via getBlockPos()
    # Don't blanket replace — this is context-dependent

    # ── 13. IItemExtension.isValidRepairItem signature ──────────────────────────
    # Modern NeoForge: isValidRepairItem(ItemStack toRepair, ItemStack repair) — same as old
    # The @Override might be wrong if method doesn't exist in interface
    # Remove @Override from isValidRepairItem in non-Item classes if needed

    # ── 14. entity.getBlockPos() for Entity → blockPosition() ─────────────────
    # Already handled in pass2 and pass4, but catch remaining cases
    content = re.sub(
        r'\b(event\.getEntity\(\)|event\.getAttacker\(\)|event\.getSource\(\)\.getEntity\(\))\b'
        r'\.getBlockPos\(\)',
        lambda m: m.group(1) + '.blockPosition()',
        content
    )

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
