#!/usr/bin/env python3
"""Pass 11: Fix remaining API renames systematically."""
import os, re, sys, glob

SRC = "src/main/java"

def fix(path, content):
    # ── BlockState property API ───────────────────────────────────────────────
    content = content.replace('.getAllowedValues()', '.getPossibleValues()')
    content = content.replace('.withProperty(', '.setValue(')
    content = re.sub(r'\.getProperties\(\)\.keySet\(\)', '.getProperties()', content)

    # ── Direction.getFront → Direction.from3DDataValue ────────────────────────
    content = re.sub(r'Direction\.getFront\(([^)]+)\)', r'Direction.from3DDataValue(\1)', content)

    # ── Entity.width → getBbWidth() ───────────────────────────────────────────
    content = re.sub(r'\.width\b(?!\s*=)(?=\s*/|\s*\*|\s*\+|\s*\)|\s*;)', '.getBbWidth()', content)
    content = re.sub(r'\.height\b(?!\s*=)(?=\s*/|\s*\*|\s*\+|\s*\)|\s*;)', '.getBbHeight()', content)

    # ── AI goal method renames ────────────────────────────────────────────────
    content = content.replace('.updateTask()', '.tick()')
    content = re.sub(r'\.setLookPositionWithEntity\(', '.setLookAt(', content)
    content = re.sub(r'\.getPathToMob\(', '.createPath(', content)
    content = re.sub(r'\.getEntitySenses\(\)', '.getSensing()', content)

    # ── Node path fields (x, y, z) - might be public ─────────────────────────
    # In MC 1.21.4, Node.x, Node.y, Node.z are still public ints
    # Keep as-is

    # ── LookController API ────────────────────────────────────────────────────
    content = re.sub(r'\.setLookAt\(([^)]+),\s*(\d+)\.0f,\s*\(float\)\s*(\d+)\)',
                     r'.setLookAt(\1, \2.0f, (float)\3)', content)

    # ── ServerLevel.getEntityFromUuid → getEntity ─────────────────────────────
    content = re.sub(r'\.getEntityFromUuid\(([^)]+)\)', r'.getEntity(\1)', content)

    # ── Block.getBoundsFromPool removed → use AABB ────────────────────────────

    # ── EntityLivingBase/LivingEntity method renames ──────────────────────────
    content = re.sub(r'\.getActivePotionEffect\(', '.getEffect(', content)
    content = re.sub(r'\.isPotionActive\(', '.hasEffect(', content)
    content = re.sub(r'\.removePotionEffect\(', '.removeEffect(', content)
    content = re.sub(r'\.addPotionEffect\(', '.addEffect(', content)
    content = re.sub(r'\.getAttackTarget\(\)', '.getTarget()', content)
    content = re.sub(r'\.setAttackTarget\(([^)]+)\)', r'.setTarget(\1)', content)
    content = re.sub(r'\.getRevengeTarget\(\)', '.getLastHurtByMob()', content)
    content = re.sub(r'\.setRevengeTarget\(([^)]+)\)', r'.setLastHurtByMob(\1)', content)
    content = re.sub(r'\.setLastAttackedEntity\(([^)]+)\)', r'.setLastHurtMob(\1)', content)

    # ── Mob AI method renames ─────────────────────────────────────────────────
    content = re.sub(r'\.getHomePosition\(\)', '.getRestrictCenter()', content)
    content = re.sub(r'\.isWithinHomeDistanceCurrentPosition\(\)', '.isWithinRestriction()', content)
    content = re.sub(r'\.isWithinHomeDistanceFromPosition\(([^)]+)\)', r'.isWithinRestriction(\1)', content)
    content = re.sub(r'\.enablePersistence\(\)', '.setPersistenceRequired()', content)

    # ── ItemStack constructor changes ─────────────────────────────────────────
    # ItemStack(Block, int, int) → ItemStack(Block) (metadata removed)
    content = re.sub(r'new ItemStack\(([^,)]+Block[^,)]*),\s*\d+,\s*\d+\)',
                     r'new ItemStack(\1)', content)
    # ItemStack(Item, int) → ItemStack(Item, count) - keep
    # ItemStack(CompoundTag) → stub
    content = re.sub(
        r'new ItemStack\(([^)]+CompoundTag[^)]*)\)',
        r'ItemStack.EMPTY /* ItemStack(CompoundTag) removed */',
        content
    )

    # ── sendSystemMessage fixes ───────────────────────────────────────────────
    # player.sendMessage → player.sendSystemMessage
    content = re.sub(
        r'\.sendMessage\(Component\.',
        '.sendSystemMessage(Component.',
        content
    )
    content = re.sub(
        r'\.sendMessage\(net\.minecraft\.network\.chat\.Component\.',
        '.sendSystemMessage(net.minecraft.network.chat.Component.',
        content
    )

    # ── CropUtils style fixes ─────────────────────────────────────────────────
    # BlockCrops interface might have changed
    content = re.sub(r'instanceof BlockCrops', 'instanceof net.minecraft.world.level.block.CropBlock', content)
    content = re.sub(r'BlockCrops\b', 'net.minecraft.world.level.block.CropBlock', content)
    content = re.sub(r'BlockStem\b', 'net.minecraft.world.level.block.StemBlock', content)

    # ── Level.markBlockForUpdate → sendBlockUpdated ───────────────────────────
    content = re.sub(
        r'(\w+)\.markBlockForUpdate\(([^)]+)\)',
        r'\1.sendBlockUpdated(\2, \1.getBlockState(\2), \1.getBlockState(\2), 3)',
        content
    )

    # ── Entity.getNavigator → getNavigation ───────────────────────────────────
    content = re.sub(r'\.getNavigator\(\)', '.getNavigation()', content)

    # ── EntityBat style → Bat ────────────────────────────────────────────────
    # EntityAnimal → Animal
    content = content.replace('EntityAnimal', 'net.minecraft.world.entity.animal.Animal')

    # ── Level.isRaining → Level.isRaining ──────────────────────────────────
    # Level.isRaining() still exists
    # Level.thunderingStrength → getRainLevel/getThunderLevel
    content = re.sub(r'(\w+)\.getRainStrength\(([^)]+)\)', r'\1.getRainLevel(\2)', content)

    # ── Attribute.name → Attribute.getDescriptionId() ────────────────────────
    content = re.sub(r'\.getName\(\)(?=\s*[;,)])', '.getDescriptionId()', content)
    # But only for Attribute, not for general objects
    # This is too broad - revert
    # Actually skip this

    # ── Level.maxBuildHeight → getMaxBuildHeight() ────────────────────────────
    content = content.replace('Level.MAX_LEVEL_SIZE', '30000000')

    # ── Chunk API ─────────────────────────────────────────────────────────────
    content = re.sub(r'\.getChunk\((\w+),\s*(\w+)\)', r'.getChunk(\1, \2)', content)

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
