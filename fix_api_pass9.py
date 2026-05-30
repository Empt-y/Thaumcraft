#!/usr/bin/env python3
"""Pass 9: Comprehensive fix for remaining error categories."""
import os, re, sys, glob

SRC = "src/main/java"

def class_is_entity(content):
    m = re.search(r'class\s+\w+[^{]*extends\s+([\w<>]+)', content)
    if not m: return False
    base = m.group(1)
    return any(x in base for x in ['Entity','Projectile','Mob','Monster','Animal'])

def fix(path, content):
    # ── 1. random field in Item/Block classes → use method params ─────────────
    # Old Item classes used a 'random' field (from old Forge Item)
    # Fix: in item method bodies, replace bare 'random.' with local random
    # We can't fix this globally without context. Skip.

    # ── 2. variable rand remaining in entity classes ───────────────────────────
    if class_is_entity(content):
        content = re.sub(r'(?<![.\w])rand\.', 'random.', content)

    # ── 3. getBlock() called on Level → Level.getBlockState(pos).getBlock() ───
    # This was already done but some cases remain
    # Level.getBlock(pos) doesn't exist → getBlockState(pos).getBlock()
    # But if called on BlockPos: BlockPos doesn't have getBlock()
    # Let's look for level.getBlock() patterns specifically
    content = re.sub(
        r'\b(world|level|worldIn|worldObj|w)\b\.getBlock\(([^)]+)\)',
        r'\1.getBlockState(\2).getBlock()',
        content
    )

    # ── 4. BlockPos.x/z private access → getX()/getZ() ───────────────────────
    # Only for direct field access (not inside method chains)
    content = re.sub(r'(?<!\w)pos(?!\d)\.x\b(?!\w)', 'pos.getX()', content)
    content = re.sub(r'(?<!\w)pos(?!\d)\.z\b(?!\w)', 'pos.getZ()', content)
    content = re.sub(r'(?<!\w)pos(?!\d)\.y\b(?!\w)', 'pos.getY()', content)
    # sealPos.pos
    content = re.sub(r'(sealPos\.pos)\.x\b', r'\1.getX()', content)
    content = re.sub(r'(sealPos\.pos)\.z\b', r'\1.getZ()', content)
    content = re.sub(r'(sealPos\.pos)\.y\b', r'\1.getY()', content)

    # ── 5. Block.getBlockFromItem → done via Block registry ──────────────────
    # Block.getBlockFromItem(item) → Block.byItem(item) in modern MC
    content = re.sub(r'Block\.getBlockFromItem\(([^)]+)\)', r'Block.byItem(\1)', content)

    # ── 6. getEntity() on wrong event types ───────────────────────────────────
    # LivingDropsEvent.getEntity() doesn't exist → getEntity() should work for most
    # But some events use getSource() not getEntity()
    # Skip for now - context-dependent

    # ── 7. isDeadOrDying() ───────────────────────────────────────────────────
    content = re.sub(r'(?<!\w)isDead\(\)(?!\w)', 'isDeadOrDying()', content)

    # ── 8. Entity.getBlockPos() on entity vars → blockPosition() ─────────────
    content = re.sub(
        r'\b(golem|entity|mob|e|living|target|attacker|player|caster)\b\.getBlockPos\(\)',
        lambda m: m.group(1) + '.blockPosition()',
        content
    )
    content = re.sub(r'(\.getEntity\(\))\.getBlockPos\(\)', r'\1.blockPosition()', content)

    # ── 9. Level.dimension() method ─────────────────────────────────────────
    # Level.dimension() returns ResourceKey<Level> - this exists
    # But some code might be calling dimension() with wrong expectation
    # If error is "method dimension()" not found on Level → level doesn't have it?
    # Actually Level does have dimension() - check context
    # Maybe it's called on something that's not a Level
    content = re.sub(
        r'\b(event)\b\.dimension\(\)',
        r'\1.getLevel().dimension()',
        content
    )

    # ── 10. sendSystemMessage with non-literal ─────────────────────────────────
    # player.sendSystemMessage(someString) → wrap in Component.literal if String
    # But can't determine type safely without full type analysis

    # ── 11. getItem() on non-IItemHandler ─────────────────────────────────────
    # Container.getItem(slot) exists
    # CraftingContainer.getItem(slot) exists
    # If getItem is failing on some type, need to check

    # ── 12. Optional.get() with no args (from getInt/getString returning Optional) ─
    # .get() on Optional<Integer> or Optional<String> → need .orElse(default)
    # Find: Optional typed values being .get() called
    # e.g., .getInt("key").get() → .getIntOr("key", 0)
    content = re.sub(
        r'\.getInt\("([^"]+)"\)\.get\(\)',
        r'.getIntOr("\1", 0)',
        content
    )
    content = re.sub(
        r'\.getString\("([^"]+)"\)\.get\(\)',
        r'.getStringOr("\1", "")',
        content
    )

    # ── 13. ItemStack.getCompound → remove Optional wrapper ─────────────────
    content = re.sub(
        r'\.getCompound\("([^"]+)"\)\.orElse\(new net\.minecraft\.nbt\.CompoundTag\(\)\)',
        r'.getCompoundOrEmpty("\1")',
        content
    )
    # Remaining .getCompound("x") → .getCompoundOrEmpty("x")
    content = re.sub(
        r'\.getCompound\("([^"]+)"\)(?!\s*\.(?:orElse|isPresent|isEmpty|get\b|map|ifPresent|orElseGet|orElseThrow))',
        r'.getCompoundOrEmpty("\1")',
        content
    )

    # ── 14. Vec3i private field access (x,z,y on BlockPos/Vec3i) ─────────────
    content = re.sub(r'(\bvec\b|\bvec3i\b|\bblockPos\b|\bblockpos\b)\.x\b', r'\1.getX()', content)
    content = re.sub(r'(\bvec\b|\bvec3i\b|\bblockPos\b|\bblockpos\b)\.z\b', r'\1.getZ()', content)
    content = re.sub(r'(\bvec\b|\bvec3i\b|\bblockPos\b|\bblockpos\b)\.y\b', r'\1.getY()', content)

    # ── 15. BlockPos.relative(int,int,int) → .offset ─────────────────────────
    content = re.sub(
        r'\.relative\((-?[\w.]+),\s*(-?[\w.]+),\s*(-?[\w.]+)\)',
        r'.offset(\1, \2, \3)',
        content
    )

    # ── 16. Mth.getInt is wrong signature ─────────────────────────────────────
    # Mth.getInt(float) doesn't exist → use Mth.floor(float) or (int)float
    content = re.sub(r'Mth\.getInt\(([^)]+)\)', r'Mth.floor(\1)', content)

    # ── 17. isEnabled(Direction) on BlockState ────────────────────────────────
    # Old: blockState.isEnabled(direction) → removed
    # New: use specific property check
    # Can't fix generically

    # ── 18. getFacing(Direction) ─────────────────────────────────────────────
    # BlockStateUtils.getFacing(direction) is TC internal

    # ── 19. Component vs String incompatibility ────────────────────────────────
    # When String expected but Component returned: .getString()
    # This is complex - skip

    # ── 20. LevelAccessor cannot be converted to Level ────────────────────────
    # Cast: (Level)levelAccessor or use ServerLevel
    content = re.sub(
        r'LevelAccessor\s+(\w+)\)',
        lambda m: 'Level ' + m.group(1) + ')',
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
