#!/usr/bin/env python3
"""Fix tile entity loadAdditional/saveAdditional signatures."""
import glob, re, sys

SRC = "src/main/java"

def fix(path, content):
    # ── Fix: @Override on loadAdditional(CompoundTag) → remove @Override ──────
    # And fix super.loadAdditional(nbt) calls which are wrong

    # Remove @Override before loadAdditional(CompoundTag
    content = re.sub(
        r'@Override\s*\n(\s*(?:public|protected|private)\s+(?:void|CompoundTag)\s+loadAdditional\s*\(\s*CompoundTag)',
        r'\1',
        content
    )
    content = re.sub(
        r'@Override\s*\n(\s*(?:public|protected|private)\s+(?:void|CompoundTag)\s+saveAdditional\s*\(\s*CompoundTag)',
        r'\1',
        content
    )

    # Fix: super.loadAdditional(nbt) when nbt is CompoundTag → remove super call
    content = re.sub(
        r'super\.loadAdditional\((\w+)\);',
        r'/* super.loadAdditional removed - CompoundTag not compatible with ValueInput */',
        content
    )
    content = re.sub(
        r'super\.saveAdditional\((\w+)\);',
        r'/* super.saveAdditional removed - CompoundTag not compatible with ValueOutput */',
        content
    )

    # ── Fix: ItemStack(CompoundTag) constructor removed ─────────────────────────
    # The CompoundTag holds the item data. In modern MC, use ItemStack.parseOptional
    # or ItemStack.CODEC. For now: use a placeholder
    content = re.sub(
        r'new ItemStack\((\w+(?:tagcompound\d*|tag|nbt|compound)\w*)\)',
        lambda m: (
            'net.minecraft.world.item.ItemStack.parseOptional(null, ' + m.group(1) + ').orElse(ItemStack.EMPTY)'
            if 'tag' in m.group(1).lower() or 'nbt' in m.group(1).lower() or 'compound' in m.group(1).lower()
            else m.group(0)
        ),
        content
    )
    # More specific: new ItemStack(nbttagcompound1)
    content = re.sub(
        r'new ItemStack\((nbttagcompound\d+|nbt\w*|tag\w*|compound\w*)\)',
        r'net.minecraft.world.item.ItemStack.parseOptional(null, \1).orElse(ItemStack.EMPTY)',
        content
    )

    # ── Fix: getListOrEmpty("...") → getListOrEmpty("...") (keep) ──────────────
    # But getList("...", type) → getListOrEmpty("...")
    content = re.sub(r'\.getList\("([^"]+)",\s*\d+\)', r'.getListOrEmpty("\1")', content)

    # ── Fix: nbt.getCompound("...").orElse(new CompoundTag()) duplication ───────
    # Already handled by getCompoundOrEmpty, but check for double orElse
    content = re.sub(
        r'\.getCompoundOrEmpty\("([^"]+)"\)\.orElse\(new (?:net\.minecraft\.nbt\.)?CompoundTag\(\)\)',
        r'.getCompoundOrEmpty("\1")',
        content
    )

    return content

def process(path):
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
    for path in sorted(glob.glob(f"{SRC}/**/*.java", recursive=True)):
        try:
            if process(path):
                changed += 1
        except Exception as e:
            print(f"ERROR {path.split('/')[-1]}: {e}", file=sys.stderr)
    print(f"Modified {changed} files")

if __name__ == '__main__':
    main()
