#!/usr/bin/env python3
"""
Comprehensive final port fixes for Thaumcraft NeoForge 26.1.2.
Fixes the remaining ~2000 compiler errors.
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
                    'AmbientCreature','NeutralMob','Slime'}
    if base in ENTITY_BASES: return True
    if any(e in base for e in ['Entity','Projectile']): return True
    return False

def class_is_block_entity(content):
    m = re.search(r'class\s+\w+[^{]*extends\s+([\w<>]+)', content)
    if not m: return False
    base = m.group(1)
    return 'Tile' in base or base == 'BlockEntity' or base.endswith('BlockEntity')

def fix_level_in_methods(content):
    """
    Replace standalone level() within method bodies where the method
    declares a Level parameter. Uses a character-level brace-depth parser.
    """
    # Find all (method_start_pos, level_param_name, method_body_start) tuples
    # by scanning for method signatures
    result = list(content)
    i = 0
    n = len(content)

    while i < n:
        # Look for method signatures: lines with ( that contain "Level name"
        # We detect "Level <word>" or "ServerLevel <word>" as param
        if content[i] == '(':
            # Find the matching ) for this method signature
            sig_start = max(0, content.rfind('\n', 0, i) + 1)
            # Collect sig up to {
            j = i
            depth = 0
            sig_end = -1
            while j < n:
                if content[j] == '(':
                    depth += 1
                elif content[j] == ')':
                    depth -= 1
                    if depth == 0:
                        sig_end = j
                        break
                j += 1

            if sig_end < 0:
                i += 1
                continue

            sig = content[i:sig_end+1]

            # Extract Level param name
            param_m = re.search(r'\b(?:Server)?Level\s+(\w+)\b', sig)
            if param_m:
                level_param = param_m.group(1)
                if level_param in ('this','null','new','return','void','class',
                                   'extends','implements','throws','instanceof',
                                   'if','for','while','switch','try','catch','throw'):
                    i += 1
                    continue

                # Find the opening { of the method body
                k = sig_end + 1
                while k < n and content[k] in ' \t\n\r':
                    k += 1

                if k < n and content[k] == '{':
                    # Now scan the method body and replace standalone level()
                    body_start = k
                    brace_depth = 0
                    m_idx = k
                    while m_idx < n:
                        ch = content[m_idx]
                        if ch == '{':
                            brace_depth += 1
                        elif ch == '}':
                            brace_depth -= 1
                            if brace_depth == 0:
                                body_end = m_idx
                                break
                        # Replace level() → param name (standalone, not .level())
                        if (content[m_idx:m_idx+7] == 'level()'
                                and (m_idx == 0 or content[m_idx-1] != '.')
                                and (m_idx == 0 or not content[m_idx-1].isalnum())
                                and content[m_idx-1] != '_'):
                            # Replace in result
                            for ki in range(m_idx, m_idx + 7):
                                result[ki] = ''
                            replacement = level_param
                            result[m_idx] = replacement
                        m_idx += 1
                    i = body_end + 1
                    continue

        i += 1

    return ''.join(result)

def fix(path, content):
    is_entity = class_is_entity(content)
    is_be = class_is_block_entity(content)

    # ── 1. blockPosition() → getBlockPos() ──────────────────────────────────
    content = content.replace('.blockPosition()', '.getBlockPos()')
    if is_be:
        content = re.sub(r'(?<![.\w])blockPosition\(\)', 'getBlockPos()', content)

    # ── 2. level() replacements ──────────────────────────────────────────────
    if is_be:
        # BlockEntity: bare level() → getLevel()
        content = re.sub(r'(?<![.\w])level\(\)', 'getLevel()', content)
    elif not is_entity:
        # Block/Item/Container: replace level() with Level param in each method
        content = fix_level_in_methods(content)

    # ── 3. .world field → proper accessor ───────────────────────────────────
    content = re.sub(r'\b(event)\b([.\s]*)world\b', r'\1\2getLevel()', content)
    # tile.world → tile.getLevel()
    content = re.sub(r'(?<=\.)world\b(?!\s*=)', 'getLevel()', content)
    # this.world in BlockEntity → getLevel()
    if is_be:
        content = re.sub(r'\bthis\.world\b', 'getLevel()', content)
    # Bare 'world' as a field in entity classes → level()
    if is_entity:
        content = re.sub(r'\bthis\.world\b', 'this.level()', content)

    # ── 4. getMob() → getEntity() in events ─────────────────────────────────
    content = content.replace('.getMob()', '.getEntity()')

    # ── 5. getWorld() → getLevel() in BlockEntity/event context ─────────────
    if is_be:
        content = re.sub(r'(?<![.\w])getWorld\(\)', 'getLevel()', content)
    content = re.sub(r'\b(event)\.getWorld\(\)', r'\1.getLevel()', content)

    # ── 6. BlockPos.add(x,y,z) → .offset(x,y,z) ────────────────────────────
    content = re.sub(r'\.add\((-?[\w.]+),\s*(-?[\w.]+),\s*(-?[\w.]+)\)',
                     r'.offset(\1, \2, \3)', content)

    # ── 7. Remove obsolete registration builder methods ──────────────────────
    content = re.sub(r'\s*\.setRegistryName\([^)]*\)', '', content)
    content = re.sub(r'\s*\.setUnlocalizedName\([^)]*\)', '', content)
    content = re.sub(r'\s*\.setCreativeTab\([^)]*\)', '', content)
    content = re.sub(r'\s*\.setHasSubtypes\([^)]*\)', '', content)
    content = re.sub(r'\s*\.setMaxStackSize\([^)]*\)', '', content)

    # ── 8. getRandom() for entity random field ───────────────────────────────
    if is_entity:
        content = re.sub(r'\bthis\.random\b', 'this.getRandom()', content)
        # Standalone 'random' used as field → call getRandom()
        # Be careful not to change variable declarations
        content = re.sub(r'(?<!\w)(?<!= )random(?!\s*[=,)])(?=\s*\.)',
                         'getRandom()', content)

    # ── 9. getIntOr/getStringOr/getByteOr/getShortOr → inline ternary ───────
    content = re.sub(
        r'(\w+)\.getIntOr\("([^"]+)",\s*(-?\d+)\)',
        r'(\1.contains("\2") ? \1.getInt("\2") : \3)',
        content
    )
    content = re.sub(
        r'(\w+)\.getStringOr\("([^"]+)",\s*("[^"]*"|null)\)',
        r'(\1.contains("\2") ? \1.getString("\2") : \3)',
        content
    )
    content = re.sub(
        r'(\w+)\.getByteOr\("([^"]+)",\s*(\([^)]+\))?\s*(-?\d+)\)',
        r'(\1.contains("\2") ? \1.getByte("\2") : (\3)\4)',
        content
    )
    content = re.sub(
        r'(\w+)\.getShortOr\("([^"]+)",\s*(\([^)]+\))?\s*(-?\d+)\)',
        r'(\1.contains("\2") ? \1.getShort("\2") : (\3)\4)',
        content
    )

    # ── 10. event.getPlayer() → event.getEntity() (LivingDropsEvent etc.) ───
    content = re.sub(r'\bevent\.getPlayer\(\)', 'event.getEntity()', content)

    # ── 11. IItemHandler.getItem(slot) → getStackInSlot(slot) ───────────────
    content = re.sub(r'(\w+)\.getItem\((\w+)\)(?=\s*[;,.])',
                     r'\1.getStackInSlot(\2)', content)

    # ── 12. isItemDamaged() → getDamageValue() > 0 ──────────────────────────
    content = re.sub(r'(\w+)\.isItemDamaged\(\)', r'(\1.getDamageValue() > 0)', content)

    # ── 13. getSizeInventory() → getContainerSize() ──────────────────────────
    content = content.replace('.getSizeInventory()', '.getContainerSize()')

    # ── 14. addItemStackToInventory → add ────────────────────────────────────
    content = re.sub(r'\.addItemStackToInventory\(([^)]+)\)', r'.add(\1)', content)

    # ── 15. setInventorySlotContents → setItem ───────────────────────────────
    content = re.sub(
        r'\.setInventorySlotContents\(([^,)]+),\s*([^)]+)\)',
        r'.setItem(\1, \2)',
        content
    )

    # ── 16. getRecipeOutput() → getResultItem(RegistryAccess.EMPTY) ──────────
    content = content.replace(
        '.getRecipeOutput()',
        '.getResultItem(net.minecraft.core.RegistryAccess.EMPTY)'
    )

    # ── 17. getCraftedItem() → getResult() ───────────────────────────────────
    content = content.replace('.getCraftedItem()', '.getResult()')

    # ── 18. getWorldTime() → getGameTime() ───────────────────────────────────
    content = content.replace('.getWorldTime()', '.getGameTime()')

    # ── 19. isDead() → isDeadOrDying() ───────────────────────────────────────
    content = re.sub(r'(?<!\w)isDead\(\)(?!\w)', 'isDeadOrDying()', content)

    # ── 20. inventoryContainer → containerMenu ───────────────────────────────
    content = content.replace('inventoryContainer', 'containerMenu')

    # ── 21. getHarvester() → getPlayer().orElse(null) ────────────────────────
    content = re.sub(
        r'\bevent\.getHarvester\(\)',
        'event.getPlayer().orElse(null)',
        content
    )

    # ── 22. distanceToSqr(BlockPos) → distanceToSqr(pos.getCenter()) ─────────
    content = re.sub(
        r'\.distanceToSqr\((\w+(?:\.\w+\(\))?)\)(?=\s*(?:<=|>=|<|>|==|\n|;|\s))',
        lambda m: (
            '.distanceToSqr(' + m.group(1) + '.getCenter())'
            if 'Vec3' not in m.group(1) and '.getCenter' not in m.group(1)
            and '(' not in m.group(1)
            else m.group(0)
        ),
        content
    )

    # ── 23. getCompoundOrEmpty → getCompound ─────────────────────────────────
    content = re.sub(r'\.getCompoundOrEmpty\("([^"]+)"\)', r'.getCompound("\1")', content)

    # ── 24. craftMatrix private access fix ───────────────────────────────────
    # craftMatrix field in ItemCraftedEvent → use getCraftingMatrix() or similar
    # Skip - complex, needs manual fix

    # ── 25. BlockPos.relative with 3 args → relative(Direction).relative() ──
    content = re.sub(
        r'\.relative\((-?\d+),\s*(-?\d+),\s*(-?\d+)\)',
        lambda m: '.offset(' + m.group(1) + ', ' + m.group(2) + ', ' + m.group(3) + ')',
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
                print(f"  fixed: {path.split('/')[-1]}")
        except Exception as e:
            print(f"ERROR {path.split('/')[-1]}: {e}", file=sys.stderr)
    print(f"\nModified {changed}/{len(files)} files")

if __name__ == '__main__':
    main()
