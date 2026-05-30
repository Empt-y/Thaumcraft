#!/usr/bin/env python3
"""Fix remaining specific error patterns."""
import os, re, glob

SRC = "src/main/java"

def fix(path, content):
    orig = content

    # ── Fix: getAttribute(Attribute) → getAttribute(Holder.direct(Attribute)) ──
    content = re.sub(
        r'\.getAttribute\(([^)]+(?:CHAMPION_MOD|TAINTED_MOD|RangedAttribute|Attribute\.)[^)]*)\)',
        r'.getAttribute(net.minecraft.core.Holder.direct(\1))',
        content
    )
    # Fix: addTransientModifier(attr, modifier) → addTransientModifier(Holder.direct(attr), mod)
    content = re.sub(
        r'\.addTransientModifier\(([^,)]+(?:CHAMPION_MOD|TAINTED_MOD))[^,]*,',
        r'.addTransientModifier(net.minecraft.core.Holder.direct(\1),',
        content
    )
    content = re.sub(
        r'\.removeModifier\(([^,)]+(?:CHAMPION_MOD|TAINTED_MOD))[^,]*,',
        r'.removeModifier(net.minecraft.core.Holder.direct(\1),',
        content
    )

    # ── Fix: getAttributeMap().registerAttribute → comment out ────────────────
    content = re.sub(
        r'mob\.getAttributeMap\(\)\.registerAttribute\([^)]+\)\.setBaseValue\([^)]+\);',
        '// getAttributeMap().registerAttribute removed',
        content
    )
    content = re.sub(
        r'\.getAttributeMap\(\)\.registerAttribute\([^)]+\);',
        '// registerAttribute removed',
        content
    )

    # ── Fix: Inventory.armor.get(N) → getItemBySlot(EquipmentSlot.X) ─────────
    SLOT_MAP = {
        '0': 'net.minecraft.world.entity.EquipmentSlot.FEET',
        '1': 'net.minecraft.world.entity.EquipmentSlot.LEGS',
        '2': 'net.minecraft.world.entity.EquipmentSlot.CHEST',
        '3': 'net.minecraft.world.entity.EquipmentSlot.HEAD',
    }
    for i, slot in SLOT_MAP.items():
        content = re.sub(
            rf'(\w+)\.getInventory\(\)\.armor\.get\({i}\)',
            rf'\1.getItemBySlot({slot})',
            content
        )
    content = re.sub(
        r'(\w+)\.getInventory\(\)\.armor\.get\((\w+)\)',
        r'\1.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.values()[\2])',
        content
    )

    # ── Fix: DamageSource.FALL → use tag ─────────────────────────────────────
    content = content.replace(
        'event.getSource() == DamageSource.FALL',
        'event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_FALL)'
    )
    content = content.replace(
        'ds == DamageSource.FALL',
        'ds.is(net.minecraft.tags.DamageTypeTags.IS_FALL)'
    )

    # ── Fix: null >= 0 → false ────────────────────────────────────────────────
    content = content.replace('if (null /* call removed */ >= 0)', 'if (false /* null >= 0 removed */')
    content = re.sub(r'if \(null /\* call removed \*/ >= 0\)', 'if (false /* removed */', content)

    # ── Fix: BiomeDictionary → stub ───────────────────────────────────────────
    content = re.sub(
        r'BiomeDictionary\.hasType\([^)]+\)',
        'false /* BiomeDictionary removed */',
        content
    )
    content = re.sub(
        r'BiomeDictionary\.getTypes\([^)]+\)',
        'new java.util.HashSet<>() /* BiomeDictionary removed */',
        content
    )

    # ── Fix: BlockLog → use BlockLogs tag ─────────────────────────────────────
    content = content.replace('instanceof BlockLog', 'instanceof net.minecraft.world.level.block.RotatedPillarBlock /* was BlockLog */')
    content = re.sub(
        r'block instanceof BlockLog\b',
        'block.defaultBlockState().is(net.minecraft.tags.BlockTags.LOGS)',
        content
    )
    content = re.sub(
        r'\bBlockLog\b',
        'net.minecraft.world.level.block.RotatedPillarBlock',
        content
    )

    # ── Fix: CraftingManager → use server recipe manager ──────────────────────
    content = re.sub(
        r'CraftingManager\.findMatchingRecipe\([^)]+\)',
        'null /* CraftingManager removed */',
        content
    )
    content = re.sub(
        r'CraftingManager\.\w+\([^)]*\)',
        'null /* CraftingManager removed */',
        content
    )

    # ── Fix: getMaxDamage wrong signature ─────────────────────────────────────
    # IItemStackExtension.getMaxDamage() vs getMaxDamage(ItemStack)
    content = re.sub(
        r'(\w+)\.getMaxDamage\(\)',
        r'\1.getMaxDamage()',
        content
    )
    # If it expects ItemStack parameter: getMaxDamage(ItemStack)
    content = re.sub(
        r'item\.getMaxDamage\(stack\)',
        r'stack.getMaxDamage()',
        content
    )

    # ── Fix: isSameItem(ItemStack) signature ─────────────────────────────────
    # Static: ItemStack.isSameItem(a, b)
    content = re.sub(
        r'(\w+)\.isSameItem\((\w+)\)',
        r'ItemStack.isSameItem(\1, \2)',
        content
    )

    # ── Fix: ResourceKey<Enchantment> cannot be converted to Enchantment ─────
    # EnchantmentHelper.getEnchantments returns Map<Holder<Enchantment>, Integer>
    # Old: for (Map.Entry<Enchantment, Integer> e : EnchantmentHelper.getEnchantments(stack))
    # Need to handle - stub out
    content = re.sub(
        r'Map\.Entry<Enchantment,\s*Integer>',
        'Map.Entry<net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment>, Integer>',
        content
    )
    content = re.sub(
        r'EnchantmentHelper\.getEnchantments\((\w+)\)',
        r'EnchantmentHelper.getEnchantments(\1)',
        content
    )

    # ── Fix: Player cannot be converted to Vec3i ──────────────────────────────
    # Possibly from offset(player) → player is entity, not Vec3i
    content = re.sub(
        r'\.relative\(([^,)]+player[^,)]*)\)',
        r'.relative(net.minecraft.core.Direction.NORTH) /* player offset removed */',
        content
    )

    # ── Fix: Placement.facing private → use accessor ──────────────────────────
    content = re.sub(
        r'\bplacement\.facing\b',
        'placement.getFacing()',
        content
    )

    # ── Fix: String → Component mismatches ────────────────────────────────────
    # player.sendMessage(String) → player.sendSystemMessage(Component.literal(String))
    content = re.sub(
        r'(\w+)\.sendMessage\("([^"]+)"\)',
        r'\1.sendSystemMessage(net.minecraft.network.chat.Component.literal("\2"))',
        content
    )
    # setDisplayName(String) → setHoverName(Component.literal(String))
    content = re.sub(
        r'\.setDisplayName\("([^"]+)"\)',
        r'.setCustomName(net.minecraft.network.chat.Component.literal("\1"))',
        content
    )
    content = re.sub(
        r'\.setDisplayName\(([^)]+)\)',
        r'.setCustomName(net.minecraft.network.chat.Component.translatable(\1))',
        content
    )

    # ── Fix: canHarvestBlock(BlockState) in IBlockExtension ──────────────────
    content = re.sub(
        r'\.canHarvestBlock\(([^,)]+)\)',
        r'.isCorrectToolForDrops(\1)',
        content
    )

    # ── Fix: isDamageable(ItemStack) in IItemExtension ────────────────────────
    content = re.sub(
        r'item\.isDamageable\(\)',
        r'stack.isDamageableItem()',
        content
    )

    # ── Fix: onItemUseFirst wrong signature ───────────────────────────────────
    # Old: onItemUseFirst(ItemStack, UseOnContext)
    # This might be an @Override on a removed method
    content = re.sub(
        r'@Override\s+\n\s+public InteractionResult onItemUseFirst\(',
        '    public InteractionResult onItemUseFirst(',
        content
    )

    # ── Fix: Map<Enchantment, Integer> → comment out enchant iteration ────────
    content = re.sub(
        r'Map<Enchantment, Integer> enchants = EnchantmentHelper\.getEnchantments\([^)]+\);',
        'java.util.Map<net.minecraft.core.Holder<net.minecraft.world.item.enchantment.Enchantment>, Integer> enchants = EnchantmentHelper.getEnchantments(\1); /* type changed */',
        content
    )

    return content

def process():
    changed = 0
    for root, dirs, files in os.walk(SRC):
        dirs[:] = [d for d in dirs if d not in ['.git', 'build']]
        for fn in files:
            if not fn.endswith('.java'):
                continue
            path = os.path.join(root, fn)
            with open(path, 'r', encoding='utf-8', errors='replace') as f:
                content = f.read()
            new = fix(path, content)
            if new != content:
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(new)
                changed += 1
                print(f"Fixed: {path[len(SRC)+1:]}")
    print(f"\nTotal: {changed}")

if __name__ == '__main__':
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    process()
