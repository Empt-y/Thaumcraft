#!/usr/bin/env python3
"""
Second pass API fixes:
1. Entity.getBlockPos() → Entity.blockPosition()
2. getLevel() in Block subclasses → Level param name
3. CompoundTag.getCompound() → .getCompound().orElse(new CompoundTag())
4. getResultItem(RegistryAccess.EMPTY) → getResultItem() for custom recipes
5. Various small fixes
"""
import os, re, sys, glob

SRC = "src/main/java"

ENTITY_VAR_NAMES = r'(?:entity|entities|e|mob|golem|player|playerIn|entityplayer|entityplayermp|living|bat|pech|warden|cultist|bore|crossbow|taint|swarm|fireBat|fireSlime|slime|leader|spirit|wisp|spellBat|item|itemEntity|fireWisp)'

def class_is_entity(content):
    m = re.search(r'class\s+\w+[^{]*extends\s+([\w<>]+)', content)
    if not m: return False
    base = m.group(1)
    ENTITY_BASES = {'Entity','LivingEntity','Mob','PathfinderMob','Monster','Animal',
                    'FlyingMob','ThrowableProjectile','AbstractArrow','Projectile',
                    'AbstractHurtingProjectile','TamableAnimal','Golem','WaterAnimal',
                    'AmbientCreature','NeutralMob','Slime','SpiderType','ZombieType'}
    if base in ENTITY_BASES: return True
    if any(e in base for e in ['Entity','Projectile']): return True
    return False

def class_is_block_entity(content):
    m = re.search(r'class\s+\w+[^{]*extends\s+([\w<>]+)', content)
    if not m: return False
    base = m.group(1)
    return 'BlockEntity' == base or base.endswith('BlockEntity')

def get_level_param_for_method(method_sig):
    """Extract Level parameter name from method signature."""
    m = re.search(r'\b(?:Server)?Level\s+(\w+)\b', method_sig)
    if not m: return None
    name = m.group(1)
    if name in ('this','null','new','return','void','class','extends','implements',
                'throws','instanceof','if','for','while','switch','try','catch',
                'throw','else','case','break','continue','final','static','public',
                'private','protected','abstract','interface','enum','super'):
        return None
    return name

def fix_level_in_block_methods_v2(content):
    """
    Method-by-method fix: replace level() with the Level parameter name.
    Uses simple bracket counting.
    """
    result = []
    i = 0
    lines = content.split('\n')
    n = len(lines)

    while i < n:
        line = lines[i]

        # Detect method signature: line with ( that contains Level param
        # and eventually ends with { (possibly on next line)
        level_param = None
        if '(' in line and ('Level ' in line or 'ServerLevel ' in line):
            # Collect full signature
            sig = line
            j = i
            # Look for { up to 5 lines ahead (for long signatures)
            while j < min(n, i + 8) and '{' not in sig:
                j += 1
                if j < n:
                    sig += ' ' + lines[j]
            if '{' in sig:
                level_param = get_level_param_for_method(sig)

        result.append(line)
        i += 1

        if level_param:
            # Find the method opening brace
            # Count braces in lines so far
            depth = 0
            brace_found = False
            # Check if { is in the last few appended lines
            for k in range(len(result)-1, max(len(result)-10, -1), -1):
                c = result[k].count('{') - result[k].count('}')
                depth += c
                if '{' in result[k]:
                    brace_found = True
                    break

            if not brace_found or depth <= 0:
                continue

            # Process method body
            while i < n and depth > 0:
                mline = lines[i]

                # Replace standalone level() → level_param
                # Don't touch .level() (entity calls)
                fixed = re.sub(r'(?<![.\w])level\(\)', level_param, mline)
                result.append(fixed)

                # Track brace depth
                in_str = False
                for ch in mline:
                    if ch == '"': in_str = not in_str
                    elif not in_str:
                        if ch == '{': depth += 1
                        elif ch == '}': depth -= 1
                i += 1

    return '\n'.join(result)

def fix(path, content):
    is_entity = class_is_entity(content)
    is_be = class_is_block_entity(content)

    # ── 1. Entity.getBlockPos() → Entity.blockPosition() ───────────────────
    if is_entity:
        # this.getBlockPos() → this.blockPosition()
        content = content.replace('this.getBlockPos()', 'this.blockPosition()')
        # bare getBlockPos() in entity class → blockPosition()
        content = re.sub(r'(?<![.\w])getBlockPos\(\)', 'blockPosition()', content)
    # Common entity variable patterns
    content = re.sub(
        r'\b(' + ENTITY_VAR_NAMES + r')\.getBlockPos\(\)',
        r'\1.blockPosition()',
        content
    )
    # .getEntity().getBlockPos() → .getEntity().blockPosition()
    content = re.sub(
        r'(\.getEntity\(\))\.getBlockPos\(\)',
        r'\1.blockPosition()',
        content
    )

    # ── 2. BlockPos.relative(int,int,int) → offset ─────────────────────────
    # Already done by main script, but still appears
    content = re.sub(
        r'\.relative\((-?[\w.]+),\s*(-?[\w.]+),\s*(-?[\w.]+)\)',
        r'.offset(\1, \2, \3)',
        content
    )

    # ── 3. getLevel() in Block (non-BlockEntity) classes → fix to world ─────
    # If this is NOT a BlockEntity and NOT an Entity, getLevel() in methods
    # should use the method's Level param
    if not is_entity and not is_be:
        content = fix_level_in_block_methods_v2_for_getlevel(content)

    # ── 4. CompoundTag.getCompound() now returns Optional<CompoundTag> ───────
    # .getCompound("key") → .getCompound("key").orElse(new CompoundTag())
    content = re.sub(
        r'\.getCompound\("([^"]+)"\)(?!\s*\.(?:orElse|isPresent|get|map|ifPresent|isEmpty|orElseThrow))',
        r'.getCompound("\1").orElse(new net.minecraft.nbt.CompoundTag())',
        content
    )

    # ── 5. getResultItem(RegistryAccess.EMPTY) → getResultItem() ─────────────
    # For custom TC recipes (CrucibleRecipe, InfusionRecipe etc.) that don't
    # take RegistryAccess args
    content = re.sub(
        r'\.getResultItem\(net\.minecraft\.core\.RegistryAccess\.EMPTY\)',
        '.getResultItem()',
        content
    )
    # For vanilla recipe interface (RecipeHolder etc.) keep EMPTY
    # InfusionRecipe specifically - check if it has its own getResultItem()

    # ── 6. getStringOr(key, null) → getStringOr(key, "") ───────────────────
    content = re.sub(
        r'\.getStringOr\("([^"]+)",\s*null\)',
        r'.getStringOr("\1", "")',
        content
    )

    # ── 7. getLevel() on Block/event/non-entity contexts ─────────────────────
    # event.getLevel() in NeoForge events - this IS the correct API
    # So don't change event.getLevel()

    # ── 8. getWorld() on events → getLevel() ────────────────────────────────
    content = re.sub(r'(\bevent\b)\.getWorld\(\)', r'\1.getLevel()', content)

    # ── 9. level() in ItemStack constructor → remove ─────────────────────────
    # ItemStack(Optional<CompoundTag>) - Optional was returned by getCompound
    # Already handled by fix 4 above (getCompound now returns orElse)

    # ── 10. ItemStack.EMPTY context ──────────────────────────────────────────
    # ItemStack(CompoundTag) → ItemStack.EMPTY (already done by earlier scripts)

    # ── 11. Remove getStackInSlot where wrong ────────────────────────────────
    # CraftingContainer.getItem() → correct, not getStackInSlot()
    # The fix_port_final script replaced craftMatrix.getItem(i) → .getStackInSlot(i)
    # which is wrong. CraftingContainer (which is a Container) has getItem() not getStackInSlot()
    content = re.sub(
        r'craftMatrix\.getStackInSlot\((\w+)\)',
        r'craftMatrix.getItem(\1)',
        content
    )
    # Also fix baubles.getStackInSlot incorrectly hitting Container
    content = re.sub(
        r'baubles\.getStackInSlot\((\w+)\)',
        r'baubles.getItem(\1)',
        content
    )

    # ── 12. entity.getBlockPos() catch-all → entity.blockPosition() ──────────
    # In Task class and GolemHelper
    content = re.sub(r'(\w+Entity\w*)\.getBlockPos\(\)', r'\1.blockPosition()', content)
    content = re.sub(r'(\bgolem\b)\.getBlockPos\(\)', r'\1.blockPosition()', content)

    return content

def fix_level_in_block_methods_v2_for_getlevel(content):
    """
    Replace getLevel() with level param in Block/Container/Item methods
    that declare a Level parameter.
    """
    lines = content.split('\n')
    result = []
    i = 0
    n = len(lines)

    while i < n:
        line = lines[i]

        level_param = None
        if '(' in line and ('Level ' in line or 'ServerLevel ' in line):
            sig = line
            j = i
            while j < min(n, i + 8) and '{' not in sig:
                j += 1
                if j < n:
                    sig += ' ' + lines[j]
            if '{' in sig:
                level_param = get_level_param_for_method(sig)

        result.append(line)
        i += 1

        if level_param:
            depth = 0
            brace_found = False
            for k in range(len(result)-1, max(len(result)-10, -1), -1):
                c = result[k].count('{') - result[k].count('}')
                depth += c
                if '{' in result[k]:
                    brace_found = True
                    break

            if not brace_found or depth <= 0:
                continue

            while i < n and depth > 0:
                mline = lines[i]

                # Replace standalone getLevel() with level param
                # (not .getLevel() which is a chained call on an object)
                fixed = re.sub(r'(?<![.\w])getLevel\(\)', level_param, mline)
                result.append(fixed)

                in_str = False
                for ch in mline:
                    if ch == '"': in_str = not in_str
                    elif not in_str:
                        if ch == '{': depth += 1
                        elif ch == '}': depth -= 1
                i += 1

    return '\n'.join(result)

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
