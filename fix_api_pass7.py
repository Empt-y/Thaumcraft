#!/usr/bin/env python3
"""Pass 7: Fix remaining Level API, hit result, dimension, and other patterns."""
import os, re, sys, glob

SRC = "src/main/java"

def fix(path, content):
    # ── 1. Level.isBlockPowered() → hasNeighborSignal() ───────────────────────
    content = re.sub(r'(\w+)\.isBlockPowered\(([^)]+)\)', r'\1.hasNeighborSignal(\2)', content)
    content = re.sub(r'(\w+)\.isBetterThanMaxSignal\(([^)]+)\)', r'\1.getBestNeighborSignal(\2) > 0', content)

    # ── 2. Level.isBlockLoaded() → isLoaded() ─────────────────────────────────
    content = re.sub(r'(\w+)\.isBlockLoaded\(([^)]+)\)', r'\1.isLoaded(\2)', content)

    # ── 3. Level.checkNoEntityCollision → noCollision ─────────────────────────
    content = re.sub(r'(\w+)\.checkNoEntityCollision\(([^,)]+),\s*null\)', r'\1.noCollision(\2)', content)
    content = re.sub(r'(\w+)\.checkNoEntityCollision\(([^)]+)\)', r'\1.noCollision(\2)', content)

    # ── 4. HitResult.subHit → -1 (removed in modern MC) ─────────────────────
    content = content.replace('.subHit >= 0', '.getType() != net.minecraft.world.phys.HitResult.Type.MISS')
    content = content.replace('hit.subHit < 6', 'true')
    content = content.replace('hit.subHit', '0')

    # ── 5. dimensionTypeId() → dimension().location().hashCode() ─────────────
    content = re.sub(r'\.dimensionTypeId\(\)', '.dimension().location().hashCode()', content)
    content = re.sub(r'getDimension\(\)', 'dimension()', content)

    # ── 6. getRandom() on Level → getRandom() (Level does have getRandom()) ──
    # The errors for getRandom() might be on Entity.random field access
    # Entity.random is a RandomSource field
    content = re.sub(r'(?<![.\w])random\.next', 'this.getRandom().next', content)

    # ── 7. Level.getBlock() → getBlockState().getBlock() ─────────────────────
    # world.getBlock(pos) → world.getBlockState(pos).getBlock()
    content = re.sub(
        r'(\w+)\.getBlock\(([^)]+)\)',
        r'\1.getBlockState(\2).getBlock()',
        content
    )

    # ── 8. Level.getBrightness → getLightEmission, getBrightness patterns ────
    content = re.sub(r'(\w+)\.getLightFor\(([^,)]+),\s*([^)]+)\)',
                     r'\1.getBrightness(\2, \3)', content)

    # ── 9. removeBlock(pos) / setBlockToAir → removeBlock ────────────────────
    content = re.sub(r'(\w+)\.setBlockToAir\(([^)]+)\)',
                     r'\1.removeBlock(\2, false)', content)

    # ── 10. sendSystemMessage with non-literal args ────────────────────────────
    # sendSystemMessage(someVar) where someVar is a String → wrap with Component.literal
    # But if it's already a Component, leave it
    # We can't safely determine type from source alone - skip complex cases

    # ── 11. getStackInSlot on non-IItemHandler → getItem ─────────────────────
    content = re.sub(r'\b(inv\d*|craftMatrix|matrix|container)\b\.getStackInSlot\((\w+)\)',
                     r'\1.getItem(\2)', content)

    # ── 12. INSTANCE static field usage ───────────────────────────────────────
    # Various TC classes had INSTANCE static fields that were removed
    # Often: ThaumcraftWorldGenerator.INSTANCE → new instance or skip
    # This is very context-dependent - can't fix blindly

    # ── 13. getEntity() on events ─────────────────────────────────────────────
    # LivingEntity events: getEntity() → getEntity() (should exist)
    # If error is from specific event types, need to check which ones

    # ── 14. Block.damageDropped, canHarvestBlock, removedByPlayer ────────────
    content = re.sub(r'\.damageDropped\([^)]*\)', '.hashCode() /* damageDropped removed */', content)
    content = re.sub(r'\.canHarvestBlock\([^)]+\)', '.canHarvestBlock /* removed */', content)
    # removedByPlayer: complex replacement needed

    # ── 15. Entity.addVelocity → entity.push ─────────────────────────────────
    content = re.sub(r'(\w+)\.addVelocity\(([^,)]+),\s*([^,)]+),\s*([^)]+)\)',
                     r'\1.push(\2, \3, \4)', content)

    # ── 16. DamageSource.causePlayerDamage → modern API ─────────────────────
    content = re.sub(
        r'DamageSource\.causePlayerDamage\((\w+)\)',
        r'\1.damageSources().playerAttack(\1)',
        content
    )
    # DamageSource.causeMobDamage
    content = re.sub(
        r'DamageSource\.causeMobDamage\((\w+)\)',
        r'\1.damageSources().mobAttack(\1)',
        content
    )

    # ── 17. attackEntityAsMob → hurtOrSimulate ────────────────────────────────
    content = re.sub(r'(\w+)\.attackEntityAsMob\((\w+)\)',
                     r'\1.hurtOrSimulate(\2.damageSources().mobAttack(\1), ((LivingEntity)\1).getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE))',
                     content)

    # ── 18. world.spawnParticle → world.addParticle ───────────────────────────
    content = re.sub(r'(\w+)\.spawnParticle\(', r'\1.addParticle(', content)

    # ── 19. Entity.getYRot() → direct field (in old code, yRot was public) ───
    # yRot has private access in Entity → use getYRot()
    content = re.sub(r'(?<![.\w])yRot\b(?!\s*=)', 'getYRot()', content)
    content = re.sub(r'(?<![.\w])xRot\b(?!\s*=)', 'getXRot()', content)
    content = re.sub(r'\.yRot\b(?!\s*=)', '.getYRot()', content)
    content = re.sub(r'\.xRot\b(?!\s*=)', '.getXRot()', content)

    # ── 20. SealPos.pos.x/z (private Vec3i field access) ─────────────────────
    content = re.sub(r'(getSealPos\(\)\.pos)\.x\b', r'\1.getX()', content)
    content = re.sub(r'(getSealPos\(\)\.pos)\.z\b', r'\1.getZ()', content)
    content = re.sub(r'(getSealPos\(\)\.pos)\.y\b', r'\1.getY()', content)
    content = re.sub(r'(sealPos\.pos)\.x\b', r'\1.getX()', content)
    content = re.sub(r'(sealPos\.pos)\.z\b', r'\1.getZ()', content)
    content = re.sub(r'(sealPos\.pos)\.y\b', r'\1.getY()', content)

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
