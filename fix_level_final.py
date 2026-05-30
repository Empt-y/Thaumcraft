#!/usr/bin/env python3
"""
Final level() / getLevel() / getBlockPos() / getBlockMetadata() fixes.
Uses method-scoped replacement finding both Level params AND Level local variables.
"""
import os, re, sys, glob

SRC = "src/main/java"

def class_is_entity(content):
    m = re.search(r'class\s+\w+[^{]*extends\s+([\w<>]+)', content)
    if not m: return False
    base = m.group(1)
    ENTITY_BASES = {'Entity','LivingEntity','Mob','PathfinderMob','Monster','Animal',
                    'FlyingMob','ThrowableProjectile','AbstractArrow','Projectile',
                    'AbstractHurtingProjectile','TamableAnimal','Golem','WaterAnimal',
                    'AmbientCreature','NeutralMob','Slime','FlyingAnimal'}
    if base in ENTITY_BASES: return True
    if 'Entity' in base or 'Projectile' in base: return True
    return False

def class_is_block_entity(content):
    m = re.search(r'class\s+\w+[^{]*extends\s+([\w<>]+)', content)
    if not m: return False
    base = m.group(1)
    return 'BlockEntity' == base or base.endswith('BlockEntity') or 'Tile' in base

def extract_level_name(method_text):
    """Find the Level variable name in a method (param or local var)."""
    # Check for Level param in signature
    m = re.search(r'\b(?:Server)?Level\s+(\w+)\b', method_text[:500])  # first 500 chars = signature area
    if m:
        name = m.group(1)
        if name not in ('this','null','new','return','void','class','extends','implements'):
            return name
    # Check for Level local variable
    m = re.search(r'Level\s+(\w+)\s*=', method_text)
    if m:
        name = m.group(1)
        if name not in ('this','null','new','return','void'):
            return name
    return None

def fix_methods_in_content(content, replace_level, replace_getlevel):
    """
    For each method in the content, find the Level variable name and
    replace level() and/or getLevel() with it.
    """
    # Simple approach: scan through the file character by character
    # tracking method boundaries (brace depth)
    result = []
    i = 0
    lines = content.split('\n')
    n = len(lines)

    i = 0
    while i < n:
        line = lines[i]

        # Detect method start: line has '(' and 'Level' or we see Level local var soon
        # We look for method signatures (return type + name + '(' with body following)
        level_name = None
        if '(' in line:
            # Accumulate signature
            sig = line
            j = i
            while j < min(n, i + 10) and '{' not in sig:
                j += 1
                if j < n:
                    sig += '\n' + lines[j]
            if '{' in sig:
                level_name = extract_level_name(sig)

        result.append(line)
        i += 1

        if level_name:
            # Find the opening brace of the method
            depth = 0
            brace_found = False
            for k in range(len(result)-1, max(len(result)-15, -1), -1):
                if '{' in result[k]:
                    brace_found = True
                    break

            if not brace_found:
                # Also scan ahead for the first local Level var
                # (for methods that have Level world = ... inside)
                pass
            else:
                depth = 1
                method_lines = []
                while i < n and depth > 0:
                    mline = lines[i]

                    # Check for Level local variable that might be more specific
                    local_m = re.search(r'Level\s+(\w+)\s*=', mline)
                    if local_m and local_m.group(1) not in ('this', 'null'):
                        # Update to the local variable name for the rest of the method
                        level_name = local_m.group(1)

                    if replace_level:
                        mline = re.sub(r'(?<![.\w])level\(\)', level_name, mline)
                    if replace_getlevel:
                        mline = re.sub(r'(?<![.\w])getLevel\(\)', level_name, mline)

                    method_lines.append(mline)
                    in_str = False
                    for ch in mline:
                        if ch == '"': in_str = not in_str
                        elif not in_str:
                            if ch == '{': depth += 1
                            elif ch == '}': depth -= 1
                    i += 1
                result.extend(method_lines)

    return '\n'.join(result)

def fix(path, content):
    is_entity = class_is_entity(content)
    is_be = class_is_block_entity(content)

    # For Block/Item/Container classes: replace level() and getLevel() in methods
    if not is_entity and not is_be:
        content = fix_methods_in_content(content, replace_level=True, replace_getlevel=True)

    # For BlockEntity classes: bare level() → getLevel()
    if is_be:
        content = re.sub(r'(?<![.\w])level\(\)', 'getLevel()', content)

    # ── getBlockMetadata() → getBlockState().get(FACING) or stub ───────────────
    # In BlockEntity tiles: getBlockMetadata() is old, replaced by getBlockState()
    if is_be:
        # The most common use was for facing direction
        content = re.sub(
            r'getBlockMetadata\(\)',
            'getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING)',
            content
        )
    else:
        content = re.sub(r'getBlockMetadata\(\)', '0 /* getBlockMetadata removed */', content)

    # ── Entity/BlockEntity .level() vs .getLevel() ─────────────────────────────
    # tile.level() on a BlockEntity → tile.getLevel()
    content = re.sub(
        r'\b(tile\d*|te\d*|tileEntity|blockEntity|be)\b\.level\(\)',
        lambda m: m.group(1) + '.getLevel()',
        content
    )

    # ── BlockPos.add(x,y,z) → offset(x,y,z) ───────────────────────────────────
    content = re.sub(
        r'\.add\((-?[\w.]+),\s*(-?[\w.]+),\s*(-?[\w.]+)\)',
        r'.offset(\1, \2, \3)',
        content
    )

    # ── getBlock() on BlockHitResult → getBlockPos() ──────────────────────────
    # mop.getBlock() or similar → mop.getBlockPos()
    content = re.sub(r'(\bmop\b|\brtr\b|\bhit\b)\.getBlock\(\)', r'\1.getBlockPos()', content)

    # ── bi.getBlock() on BlockState → bi (BlockState IS the state) ────────────
    # OLD: bs.getBlock().someMethod() — Block still has those methods
    # But some like .damageDropped(), .canHarvestBlock(), .removedByPlayer() etc. are removed
    # Leave getBlock() calls on BlockState — they're valid in modern MC

    # ── getStackInSlot on CraftingInput → getItem ─────────────────────────────
    content = re.sub(
        r'\b(inv|input|crafting|matrix)\b\.getStackInSlot\((\w+)\)',
        r'\1.getItem(\2)',
        content
    )

    # ── sendSystemMessage ─────────────────────────────────────────────────────
    content = re.sub(
        r'(?<!\.)sendSystemMessage\(("(?:[^"\\]|\\.)*")\)',
        r'sendSystemMessage(net.minecraft.network.chat.Component.literal(\1))',
        content
    )

    # ── getBlockPos() → blockPosition() for entities within golem/entity code ─
    if is_entity or '/golems/' in path:
        # Only for entity variables, not BlockEntity refs
        content = re.sub(
            r'\b(golem|mob|entity|living|player)\b\.getBlockPos\(\)',
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
