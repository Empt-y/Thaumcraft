#!/usr/bin/env python3
"""Pass 8: Fix remaining old Level API calls."""
import os, re, sys, glob

SRC = "src/main/java"

def fix(path, content):
    # ── 1. Level.rand → Level.getRandom() ─────────────────────────────────────
    content = re.sub(r'(\bworld\b|\blevel\b|\bworldIn\b|\bworldObj\b|\bw\b)\.rand\b', r'\1.getRandom()', content)
    content = re.sub(r'(?<!\w)rand\b(?=\.next)', 'getRandom()', content)

    # ── 2. Level.rayTraceBlocks → Level.clip(ClipContext) ─────────────────────
    content = re.sub(
        r'(\w+)\.rayTraceBlocks\(([^,)]+),\s*([^)]+)\)',
        r'\1.clip(new net.minecraft.world.level.ClipContext(\2, \3, net.minecraft.world.level.ClipContext.Block.COLLIDER, net.minecraft.world.level.ClipContext.Fluid.NONE, null))',
        content
    )

    # ── 3. Level.getChunkFromBlockCoords → Level.getChunkAt ──────────────────
    content = re.sub(r'(\w+)\.getChunkFromBlockCoords\(([^)]+)\)', r'\1.getChunkAt(\2)', content)

    # ── 4. Level.sendBlockBreakProgress → Level.destroyBlockProgress ──────────
    content = re.sub(
        r'(\w+)\.sendBlockBreakProgress\(([^,)]+),\s*([^,)]+),\s*([^)]+)\)',
        r'\1.destroyBlockProgress(\2, \3, \4)',
        content
    )

    # ── 5. Level.provider → dimension stuff (usually just dimensionType) ───────
    content = re.sub(r'(\bworld\b|\blevel\b)\.provider\b', r'\1.dimensionType()', content)

    # ── 6. Level.getWorldInfo → getLevelData ─────────────────────────────────
    content = re.sub(r'(\w+)\.getWorldInfo\(\)', r'\1.getLevelData()', content)

    # ── 7. Level.canMineBlockBody → Level.mayInteract ─────────────────────────
    content = re.sub(
        r'(\w+)\.canMineBlockBody\(([^,)]+),\s*([^)]+)\)',
        r'\1.mayInteract(\2, \3)',
        content
    )

    # ── 8. Level.playerEntities → Level.players() ─────────────────────────────
    content = re.sub(r'(\w+)\.playerEntities\b', r'\1.players()', content)

    # ── 9. Level.notifyBlockUpdate → Level.sendBlockUpdated ──────────────────
    content = re.sub(
        r'(\w+)\.notifyBlockUpdate\(([^,)]+),\s*([^,)]+),\s*([^,)]+),\s*([^)]+)\)',
        r'\1.sendBlockUpdated(\2, \3, \4, \5)',
        content
    )

    # ── 10. Level.getDifficultyForLocation → Level.getCurrentDifficultyAt ────
    content = re.sub(
        r'(\w+)\.getDifficultyForLocation\(([^)]+)\)',
        r'\1.getCurrentDifficultyAt(\2)',
        content
    )

    # ── 11. Level.getChunkProvider → Level.getChunkSource ────────────────────
    content = re.sub(r'(\w+)\.getChunkProvider\(\)', r'\1.getChunkSource()', content)

    # ── 12. Level.getBiomeProvider → Level.getBiomeManager ───────────────────
    content = re.sub(r'(\w+)\.getBiomeProvider\(\)', r'\1.getBiomeManager()', content)

    # ── 13. Level.isCollisionShapeFullBlock(bs, pos) → bs.isCollisionShapeFullBlock ─
    content = re.sub(
        r'(\w+)\.isCollisionShapeFullBlock\(([^,)]+),\s*([^)]+)\)',
        r'\2.isCollisionShapeFullBlock(\1, \3)',
        content
    )

    # ── 14. Entity.getX/Y/Z on Vec3 → .x/.y/.z ────────────────────────────────
    # For Vec3 type: .getX() → .x
    # But only if the type is Vec3 - we can detect common Vec3 var names
    for var in ('vec', 'look', 'dir', 'direction', 'start', 'end', 'src', 'dst',
                'hit', 'point', 'vec3', 'hitVec', 'lookVec'):
        content = re.sub(r'\b' + var + r'\d*\.getX\(\)', var + '.x', content)
        content = re.sub(r'\b' + var + r'\d*\.getY\(\)', var + '.y', content)
        content = re.sub(r'\b' + var + r'\d*\.getZ\(\)', var + '.z', content)

    # ── 15. getBlock() on world → getBlockState().getBlock() (already done in pass7) ─
    # But check for remaining cases
    content = re.sub(
        r'(\bworld\b|\blevel\b|\bworldIn\b)\.getBlock\(([^)]+)\)',
        r'\1.getBlockState(\2).getBlock()',
        content
    )

    # ── 16. Block.getBoundsFromPool → outdated ────────────────────────────────
    # Skip

    # ── 17. sendSystemMessage with string variable ────────────────────────────
    # Pattern: player.sendSystemMessage(someVar) where someVar is string
    # Only fix if it's a direct string concatenation
    content = re.sub(
        r'\.sendSystemMessage\(("(?:[^"\\]|\\.)*"(?:\s*\+[^)]+)?)\)',
        r'.sendSystemMessage(net.minecraft.network.chat.Component.literal(\1))',
        content
    )

    # ── 18. getRandom() errors - Entity.random field in item context ──────────
    # In items: old code used random field. In modern MC, use world/level random
    # When: method has Level param and calls random.next → use level.getRandom()
    # Skip - too complex to fix safely

    # ── 19. getPackage() in focus effects → package is stored field ───────────
    # FocusEffect/FocusMedium have a 'pack' FocusPackage field, accessed via getPackage()
    # If getPackage() doesn't exist → skip for now

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
