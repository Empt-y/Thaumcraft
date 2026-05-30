#!/usr/bin/env python3
"""Fix major remaining patterns in Thaumcraft port."""

import os, re, glob

SRC = "src/main/java"

def fix(path, content):
    orig = content

    # ── Fix: Vec3.x/y/z assignment (Vec3 is immutable) ───────────────────
    # getDeltaMovement().x /= N → setDeltaMovement scaled
    content = re.sub(
        r'(\w+)\.getDeltaMovement\(\)\.([xyz])\s*/=\s*([^;]+);',
        lambda m: f'{m.group(1)}.setDeltaMovement({m.group(1)}.getDeltaMovement().'
                  f'{"multiply" if m.group(2) != "y" else "multiply"}('
                  f'{1 if m.group(2) != "y" else 1}));  // FIXME: /= not supported on Vec3',
        content
    )
    # Simple: entity.getDeltaMovement().y = val → entity.setDeltaMovement(x, val, z)
    content = re.sub(
        r'(\w+)\.getDeltaMovement\(\)\.y\s*=\s*([^;]+);',
        r'\1.setDeltaMovement(\1.getDeltaMovement().x, \2, \1.getDeltaMovement().z);',
        content
    )
    content = re.sub(
        r'(\w+)\.getDeltaMovement\(\)\.x\s*=\s*([^;]+);',
        r'\1.setDeltaMovement(\2, \1.getDeltaMovement().y, \1.getDeltaMovement().z);',
        content
    )
    content = re.sub(
        r'(\w+)\.getDeltaMovement\(\)\.z\s*=\s*([^;]+);',
        r'\1.setDeltaMovement(\1.getDeltaMovement().x, \1.getDeltaMovement().y, \2);',
        content
    )
    # Also: var.getDeltaMovement().x -= expr
    content = re.sub(
        r'(\w+)\.getDeltaMovement\(\)\.x\s*-=\s*([^;]+);',
        r'\1.setDeltaMovement(\1.getDeltaMovement().x - (\2), \1.getDeltaMovement().y, \1.getDeltaMovement().z);',
        content
    )
    content = re.sub(
        r'(\w+)\.getDeltaMovement\(\)\.x\s*\+=\s*([^;]+);',
        r'\1.setDeltaMovement(\1.getDeltaMovement().x + (\2), \1.getDeltaMovement().y, \1.getDeltaMovement().z);',
        content
    )
    content = re.sub(
        r'(\w+)\.getDeltaMovement\(\)\.z\s*\+=\s*([^;]+);',
        r'\1.setDeltaMovement(\1.getDeltaMovement().x, \1.getDeltaMovement().y, \1.getDeltaMovement().z + (\2));',
        content
    )
    content = re.sub(
        r'(\w+)\.getDeltaMovement\(\)\.y\s*\+=\s*([^;]+);',
        r'\1.setDeltaMovement(\1.getDeltaMovement().x, \1.getDeltaMovement().y + (\2), \1.getDeltaMovement().z);',
        content
    )
    # getDeltaMovement().x /= N patterns
    content = re.sub(
        r'(\w+)\.getDeltaMovement\(\)\.x\s*/=\s*([^;]+);',
        r'\1.setDeltaMovement(\1.getDeltaMovement().x / (\2), \1.getDeltaMovement().y, \1.getDeltaMovement().z);',
        content
    )
    content = re.sub(
        r'(\w+)\.getDeltaMovement\(\)\.z\s*/=\s*([^;]+);',
        r'\1.setDeltaMovement(\1.getDeltaMovement().x, \1.getDeltaMovement().y, \1.getDeltaMovement().z / (\2));',
        content
    )
    content = re.sub(
        r'(\w+)\.getDeltaMovement\(\)\.y\s*/=\s*([^;]+);',
        r'\1.setDeltaMovement(\1.getDeltaMovement().x, \1.getDeltaMovement().y / (\2), \1.getDeltaMovement().z);',
        content
    )

    # ── Fix: ItemEntity getDeltaMovement().x/y/z assignments in constructors ─
    # New ItemEntity: setDeltaMovement(...) not field access

    # ── Fix: Level.random → Level.getRandom() ────────────────────────────
    content = re.sub(r'\b(level|world|worldIn|par1World|par2World|par3World)\s*\.\s*random\b(?!\()',
                     r'\1.getRandom()', content)

    # ── Fix: Enchantment == constant → use IS_SILK_TOUCH etc. ────────────
    # In modern MC, Enchantments constants are ResourceKey<Enchantment>
    # The comparison e == Enchantments.X should be e.is(Enchantments.X)
    content = re.sub(
        r'(\w+)\s*==\s*Enchantments\.(\w+)',
        r'\1.is(net.minecraft.core.registries.BuiltInRegistries.ENCHANTMENT.getKey(Enchantments.\2))',
        content
    )
    # Actually in modern MC: net.minecraft.tags.EnchantmentTags or Holder<Enchantment>.is(ResourceKey)

    # ── Fix: TileThaumcraft constructor ───────────────────────────────────
    # BlockEntity constructor: TileThaumcraft(BlockPos, BlockState)
    # Old: TileThaumcraft() no-arg or TileThaumcraft(String name)
    # This is complex - skip for now

    # ── Fix: CompoundTag → ValueOutput in addAdditionalSaveData ──────────
    # The method signature is addAdditionalSaveData(ValueOutput nbt)
    # But old code passes CompoundTag. This usually means the code is calling
    # a tile entity method like tile.readFrom/saveTo with wrong arg type
    # Skip for now - complex

    # ── Fix: ItemStack(CompoundTag) → ItemStack.parse ────────────────────
    content = re.sub(
        r'new ItemStack\(([^,)]+CompoundTag[^)]*)\)',
        r'net.minecraft.world.item.ItemStack.parseOptional(null, \1)',
        content
    )

    # ── Fix: AttributeModifier constructor ────────────────────────────────
    # Old: new AttributeModifier(UUID, String, double, Operation)
    # New: new AttributeModifier(ResourceLocation, double, Operation)
    content = re.sub(
        r'new AttributeModifier\(([^,]+),\s*"([^"]+)",\s*([^,]+),\s*AttributeModifier\.Operation\.(\w+)\)',
        r'new AttributeModifier(net.minecraft.resources.Identifier.fromNamespaceAndPath("thaumcraft", "\2"), \3, AttributeModifier.Operation.\4)',
        content
    )

    # ── Fix: Attribute → Holder<Attribute> ───────────────────────────────
    # When adding to AttributeMap: getOrCreateAttribute(Holder<Attribute>)
    # getAttribute(Attribute) → getAttribute(Holder<Attribute>)
    # Modern: Attributes.X is already Holder<Attribute>
    # The error "Attribute cannot be converted to Holder<Attribute>" means
    # something is typed as Attribute not Holder<Attribute>
    # Custom attributes need to be registered as Holder

    # ── Fix: player.crafting field in ItemCraftedEvent ────────────────────
    content = re.sub(
        r'event\.crafting\b(?!\()',
        'event.getCraftedItem()',
        content
    )
    content = re.sub(
        r'event\.player\b(?!\()',
        'event.getEntity()',
        content
    )

    # ── Fix: ChunkPos.x/z private ────────────────────────────────────────
    content = re.sub(r'\bchunkPos\.x\b(?!\()', 'chunkPos.x', content)  # These are actually public
    # Actually in modern MC, ChunkPos has .x and .z as public final fields

    # ── Fix: Level.playSound wrong signature ─────────────────────────────
    # Old: world.playSound(x, y, z, sound, source, vol, pitch, bool)
    # New: world.playLocalSound(x, y, z, sound, source, vol, pitch, bool) - client only
    # OR: world.playSound(Player, x, y, z, sound, source, vol, pitch)
    content = re.sub(
        r'(\w+)\.playSound\(([^,]+\.getX\(\)),\s*([^,]+\.getY\(\)),\s*([^,]+\.getZ\(\)),\s*([^,]+),\s*([^,]+),\s*([^,]+),\s*([^,]+),\s*false\)',
        r'\1.playLocalSound(\2, \3, \4, \5, \6, \7, \8, false)',
        content
    )

    # ── Fix: @Override on removed methods ────────────────────────────────
    # Remove @Override from getSubItems, getHasSubtypes, etc.
    content = re.sub(r'@Override\s+\n\s+public void getSubItems\(', '    public void getSubItems(', content)

    # ── Fix: removeEffect/addEffect MobEffect → Holder ────────────────────
    content = re.sub(
        r'\.removeEffect\(([^)]+\.instance)\)',
        r'.removeEffect(net.minecraft.core.Holder.direct(\1))',
        content
    )

    # ── Fix: canBeReplaced signature ─────────────────────────────────────
    # BlockState.canBeReplaced() vs canBeReplaced(BlockPlaceContext)
    # No-arg should still work

    # ── Fix: ItemStack.getItem() != null → !stack.isEmpty() ──────────────
    # In modern MC, ItemStack.getItem() never returns null
    content = re.sub(r'(\w+)\.getItem\(\)\s*!=\s*null', r'!\1.isEmpty()', content)
    content = re.sub(r'(\w+)\.getItem\(\)\s*==\s*null', r'\1.isEmpty()', content)

    # ── Fix: getDrops(BlockState, ServerLevel, BlockPos, BlockEntity) ─────
    # In modern MC: getDrops(ServerLevel level, BlockPos pos, BlockState state, Player breaker)
    # Old: getDrops() with many args

    # ── Fix: world.getBlockState(pos) == block comparison ─────────────────
    # Still needed - BlockState == Block is wrong, use .getBlock() ==
    content = re.sub(
        r'(\w+)\.getBlockState\(([^)]+)\)\s*==\s*((?:Blocks\.\w+|BlocksTC\.\w+))',
        r'\1.getBlockState(\2).getBlock() == \3',
        content
    )
    content = re.sub(
        r'(\w+)\.getBlockState\(([^)]+)\)\s*!=\s*((?:Blocks\.\w+|BlocksTC\.\w+))',
        r'\1.getBlockState(\2).getBlock() != \3',
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
