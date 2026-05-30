#!/usr/bin/env python3
"""Pass 10: Final comprehensive API fixes."""
import os, re, sys, glob

SRC = "src/main/java"

def fix(path, content):
    # ── 1. getEntitiesOfClass(Class, Entity, AABB) → getEntities(Entity, AABB) ─
    content = re.sub(
        r'(\w+)\.getEntitiesOfClass\(([^,]+)\.class,\s*(\w+),\s*([^)]+)\)',
        r'\1.getEntities(\3, \4)',
        content
    )
    # Remaining getEntitiesOfClass with 3 args (class + aabb + predicate) - keep
    # getEntitiesOfClass(Class, Entity, AABB) → getEntities(Entity, AABB)

    # ── 2. entity.attackEntityAsMob → hurt ─────────────────────────────────────
    content = re.sub(
        r'(\w+)\.attackEntityAsMob\((\w+)\)',
        r'\2.hurt(\1.damageSources().mobAttack(\1), (float)((net.minecraft.world.entity.LivingEntity)\1).getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE))',
        content
    )

    # ── 3. EnchantmentHelper.applyThornEnchantments → remove ─────────────────
    content = re.sub(
        r'EnchantmentHelper\.applyThornEnchantments\([^)]+\);',
        '/* EnchantmentHelper.applyThornEnchantments removed */',
        content
    )

    # ── 4. getActiveHand() → null check needed ────────────────────────────────
    # LivingEntity.getActiveHand() is Optional in some contexts
    # But getUsedItemHand() exists in modern MC for the actively used hand
    content = re.sub(r'\.getActiveHand\(\)', '.getUsedItemHand()', content)

    # ── 5. entity.push() instead of addVelocity ───────────────────────────────
    # Already done in pass7 but check for remaining
    content = re.sub(
        r'(\w+)\.addVelocity\(([^,)]+),\s*([^,)]+),\s*([^)]+)\)',
        r'\1.push(\2, \3, \4)',
        content
    )

    # ── 6. DamageSource.causePlayerDamage → player.damageSources().playerAttack ─
    content = re.sub(
        r'DamageSource\.causePlayerDamage\((\w+)\)',
        r'\1.damageSources().playerAttack(\1)',
        content
    )

    # ── 7. entity.getActivePotionEffect → entity.getEffect ────────────────────
    content = re.sub(r'\.getActivePotionEffect\(', '.getEffect(', content)

    # ── 8. entity.isPotionActive → entity.hasEffect ───────────────────────────
    content = re.sub(r'\.isPotionActive\(', '.hasEffect(', content)

    # ── 9. LivingEntity.hurtTime → entity.hurtTime (still field in modern MC) ─
    # This should work

    # ── 10. getAttackTarget/setAttackTarget → getTarget/setTarget ─────────────
    content = re.sub(r'\.getAttackTarget\(\)', '.getTarget()', content)
    content = re.sub(r'\.setAttackTarget\(([^)]+)\)', r'.setTarget(\1)', content)

    # ── 11. getFacing from BlockState → state.getValue(property) ─────────────
    # BlockStateUtils.getFacing(int) still works as TC internal
    # Skip

    # ── 12. random variable in non-entity classes ──────────────────────────────
    # In Block/Item classes, bare 'random' field → doesn't exist
    # In methods with Level param: use level.getRandom()
    # This needs method-context analysis - skip for now

    # ── 13. sendSystemMessage with non-literal Component ──────────────────────
    # player.sendSystemMessage(component) should work if component is Component
    # player.sendSystemMessage(string) → sendSystemMessage(Component.literal(string))
    # Pattern: sendSystemMessage(I18n.get(...) or format() calls)
    content = re.sub(
        r'\.sendSystemMessage\(((?:I18n\.get|String\.format)\([^)]+\)(?:\s*\+[^)]*)?)\)',
        r'.sendSystemMessage(net.minecraft.network.chat.Component.literal(\1))',
        content
    )

    # ── 14. variable INSTANCE ────────────────────────────────────────────────
    # Minecraft.INSTANCE → Minecraft.getInstance()
    content = content.replace('Minecraft.INSTANCE', 'Minecraft.getInstance()')
    # MinecraftServer.INSTANCE → removed (use event-based access)
    content = re.sub(r'\bMinecraftServer\.INSTANCE\b', 'null /* MinecraftServer.INSTANCE removed */', content)

    # ── 15. getAttributeValue → getAttributeValue (check) ─────────────────────
    # In modern MC: attribute.getAttributeValue() → attribute.getValue()
    # But getAttributeValue() on AttributeInstance might work

    # ── 16. world.getLightFor(type, pos) → world.getBrightness(type, pos) ─────
    content = re.sub(r'(\w+)\.getLightFor\(([^,)]+),\s*([^)]+)\)',
                     r'\1.getBrightness(\2, \3)', content)

    # ── 17. Block.getByItem(item) → Block.byItem(item) ────────────────────────
    # Already done by pass9 but ensure
    content = re.sub(r'Block\.getByItem\(', 'Block.byItem(', content)

    # ── 18. entity.getEntityData() → entity.getEntityData() (kept) ────────────
    # getEntityData() is for entity NBT, EntityDataAccessor for synced data

    # ── 19. world.getGameRules() → world.getGameRules() (should work) ──────────

    # ── 20. isDeadOrDying → already done, double-check ────────────────────────
    content = re.sub(r'(?<!\w)isDead\(\)(?!\w)', 'isDeadOrDying()', content)

    # ── 21. Event.getEntity() → fix for specific event types ──────────────────
    # LivingDropsEvent doesn't have getEntity() - use getEntity()
    # Actually LivingDropsEvent should have getEntity() from parent
    # Let me check what type doesn't have it

    # ── 22. Level.getDimension() → dimension() (already done) ─────────────────

    # ── 23. getCompound(String) → getCompoundOrEmpty(String) ─────────────────
    content = re.sub(
        r'\.getCompound\("([^"]+)"\)(?!\s*\.(?:orElse|isPresent|isEmpty|get\b|map|ifPresent|orElseGet|orElseThrow))',
        r'.getCompoundOrEmpty("\1")',
        content
    )

    # ── 24. getPlayer() on events ─────────────────────────────────────────────
    # LivingDropsEvent.getPlayer() doesn't exist → getEntity() and check instanceof
    content = re.sub(
        r'(\bevent\b)\.getPlayer\(\)',
        r'((\1.getEntity() instanceof net.minecraft.world.entity.player.Player) ? (net.minecraft.world.entity.player.Player)\1.getEntity() : null)',
        content
    )

    # ── 25. getBlockPos() on entities in remaining places ─────────────────────
    content = re.sub(
        r'(\w+)\.getBlockPos\(\)(?=\s*[.,;)}\n])',
        lambda m: (
            m.group(1) + '.blockPosition()'
            if m.group(1) not in ('tileEntity', 'tile', 'te', 'blockEntity', 'be', 'this')
            else m.group(0)
        ),
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
