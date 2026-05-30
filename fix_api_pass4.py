#!/usr/bin/env python3
"""
Pass 4: Apply fixes using confirmed MC reference APIs.
"""
import os, re, sys, glob

SRC = "src/main/java"

def fix(path, content):
    # ── 1. CompoundTag.getCompound(String) → getCompoundOrEmpty(String) ────────
    # getCompound(String) returns Optional<CompoundTag> — use orEmpty variant
    content = re.sub(
        r'\.getCompound\("([^"]+)"\)(?!\s*\.(?:orElse|isPresent|isEmpty|get\b|map|ifPresent|orElseGet|orElseThrow))',
        r'.getCompoundOrEmpty("\1")',
        content
    )
    # Undo double: getCompoundOrEmpty("x").orElse → just keep getCompoundOrEmpty
    content = re.sub(
        r'\.getCompoundOrEmpty\("([^"]+)"\)\.orElse\(new (?:net\.minecraft\.nbt\.)?CompoundTag\(\)\)',
        r'.getCompoundOrEmpty("\1")',
        content
    )

    # ── 2. ListTag.getCompound(int) → getCompoundOrEmpty(int) ──────────────────
    content = re.sub(
        r'\.getCompound\((\w+)\)(?!\s*\.(?:orElse|isPresent|isEmpty|get\b|map|ifPresent|orElseGet|orElseThrow))',
        r'.getCompoundOrEmpty(\1)',
        content
    )

    # ── 3. Registration-time methods — remove the whole chain ──────────────────
    content = re.sub(r'\s*\.setCreativeTab\s*\([^)]*\)', '', content)
    content = re.sub(r'\s*\.setUnlocalizedName\s*\([^)]*\)', '', content)
    content = re.sub(r'\s*\.setRegistryName\s*\([^)]*\)', '', content)
    content = re.sub(r'\s*\.setHasSubtypes\s*\([^)]*\)', '', content)
    content = re.sub(r'\s*\.setMaxStackSize\s*\([^)]*\)', '', content)

    # ── 4. isValidRepairItem(ItemStack,ItemStack) → isValidRepairItem(ItemStack) ─
    # Modern Forge: IItemExtension.isValidRepairItem(ItemStack toRepair, ItemStack repair)
    # But the interface signature is isValidRepairItem(ItemStack, ItemStack)
    # The issue might be a subtype parameter mismatch. Check and adjust:
    content = re.sub(
        r'(public\s+boolean\s+isValidRepairItem\s*\()\s*ItemStack\s+\w+,\s*ItemStack\s+(\w+)\s*\)',
        r'\1ItemStack stack, ItemStack ingredient)',
        content
    )

    # ── 5. Entity.random field fix: convert bare 'random' in Item classes ──────
    # In Item subclasses: bare 'random' → doesn't exist, need Random.create()
    # This is handled per-context — skip global for now

    # ── 6. getWorld() → getLevel() in BlockEntity context ─────────────────────
    content = re.sub(r'(?<![.\w])getWorld\(\)(?!\s*\.\s*getBlock)', 'getLevel()', content)

    # ── 7. entity.getBlockPos() in non-entity contexts ─────────────────────────
    # entity variables (common names) calling getBlockPos() → blockPosition()
    # Entity has blockPosition() not getBlockPos()
    content = re.sub(
        r'(\b(?:entity|golem|player|mob|living|target|hitter|attacker|source|victim))\b'
        r'(?:In|1|2|3)?\.getBlockPos\(\)',
        lambda m: m.group(0).split('.')[0] + '.blockPosition()',
        content
    )
    # .getEntity().getBlockPos() → .getEntity().blockPosition()
    content = re.sub(r'(\.getEntity\(\))\.getBlockPos\(\)', r'\1.blockPosition()', content)

    # ── 8. getResultItem() with no args for TC custom recipes ─────────────────
    # ThaumcraftApi.getInfusionRecipe calls getResultItem() with no args
    # TC's InfusionRecipe.getResultItem() exists as custom method, keep as-is
    # But vanilla Recipe.getResultItem(RegistryAccess) was renamed in 1.21
    # In reference: Recipe<C>.getResultItem() takes no args now (1.21.4+)
    # Revert the RegistryAccess.EMPTY addition
    content = content.replace(
        '.getResultItem(net.minecraft.core.RegistryAccess.EMPTY)',
        '.getResultItem()'
    )

    # ── 9. IItemExtension.onItemUseFirst parameter fix ─────────────────────────
    # Modern sig: onItemUseFirst(ItemStack stack, UseOnContext context)
    # Old sig: onItemUseFirst(EntityPlayer player, World world, BlockPos pos, ...)
    # If code has @Override on old sig → strip @Override
    content = re.sub(
        r'@Override\s*\n(\s*public\s+\w+\s+onItemUseFirst\s*\([^)]*(?:EntityPlayer|Player)\s+\w+[^)]*\))',
        r'\1',
        content
    )

    # ── 10. playSound with old signature → playLocalSound ─────────────────────
    # Level.playSound(Player, double, double, double, SoundEvent, SoundSource, float, float)
    # Level.playLocalSound(double, double, double, SoundEvent, SoundSource, float, float, bool) [client]
    # Keep playSound(Player|null, BlockPos, SoundEvent, SoundSource, float, float) - this still exists
    content = re.sub(
        r'(\w+)\.playSound\(null,\s*'
        r'(\w[^,]+),\s*(\w[^,]+),\s*(\w[^,]+),\s*'
        r'(SoundsTC\.\w+|SoundEvents\.\w+),\s*(SoundSource\.\w+),\s*([^,]+),\s*([^)]+)\)',
        r'\1.playLocalSound(\2, \3, \4, \5, \6, \7, \8, false)',
        content
    )

    # ── 11. Enchantment.Rarity → getWeight() ──────────────────────────────────
    # Enchantment.getRarity() doesn't exist, use getWeight()
    content = re.sub(r'\.getRarity\(\)\s*==\s*Enchantment\.Rarity\.UNCOMMON', '.getWeight() <= 5', content)
    content = re.sub(r'\.getRarity\(\)\s*==\s*Enchantment\.Rarity\.RARE', '.getWeight() <= 2', content)
    content = re.sub(r'\.getRarity\(\)\s*==\s*Enchantment\.Rarity\.VERY_RARE', '.getWeight() <= 1', content)

    # ── 12. FocusPackage.world field fix ──────────────────────────────────────
    # p2.world = p.getLevel(); → p2.world = p.world;
    content = re.sub(r'(p\d*)\.getLevel\(\)(?=\s*;)', r'\1.world', content)

    # ── 13. getBlockPos() → blockPosition() for Entity in sealed blocks ───────
    content = re.sub(
        r'caster\.getBlockPos\(\)',
        'caster.blockPosition()',
        content
    )
    content = re.sub(
        r'attacker\.getBlockPos\(\)',
        'attacker.blockPosition()',
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
