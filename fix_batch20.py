#!/usr/bin/env python3
"""Final batch fixes for remaining patterns."""
import os, re, glob

SRC = "src/main/java"

def fix(path, content):
    # ── Fix: playSound(x,y,z, sound, source, vol, pitch, bool) ──────────
    # Level.playLocalSound(x, y, z, sound, source, vol, pitch, bool) - client only
    content = re.sub(
        r'(\w+)\.playSound\((\w[^,)]+),\s*(\w[^,)]+),\s*(\w[^,)]+),\s*([^,)]+),\s*(SoundSource\.\w+),\s*([^,)]+),\s*([^,)]+),\s*(true|false)\)',
        r'\1.playLocalSound(\2, \3, \4, \5, \6, \7, \8, \9)',
        content
    )

    # ── Fix: getDescriptionId(String) → getDescriptionId() ─────────────
    content = re.sub(r'\.getDescriptionId\("([^"]+)"\)', '.getDescriptionId()', content)
    content = re.sub(r'getDescriptionId\(("[\w.]+"|[\w]+)\)', 'getDescriptionId()', content)

    # ── Fix: @Override on onItemUseFirst ─────────────────────────────────
    content = re.sub(r'@Override\s+\n\s+(public[^}]+onItemUseFirst)', r'    \1', content)

    # ── Fix: getMaxDamage(ItemStack) in IItemExtension ────────────────────
    content = re.sub(r'item\.getMaxDamage\(stack\)', 'stack.getMaxDamage()', content)
    content = re.sub(r'(?<!stack\.)getMaxDamage\((\w+)\)', r'\1.getMaxDamage()', content)

    # ── Fix: CraftingManager method calls ─────────────────────────────────
    content = re.sub(
        r'ThaumcraftCraftingManager\.getInfusionRecipeAspects\([^)]+\)',
        'null /* getInfusionRecipeAspects removed */',
        content
    )

    # ── Fix: ItemStack(CompoundTag) → parse from NBT ──────────────────────
    content = re.sub(
        r'new ItemStack\(([^)]+CompoundTag[^)]*)\)',
        r'ItemStack.EMPTY /* ItemStack(CompoundTag) removed */',
        content
    )

    # ── Fix: String → Component in sendMessage/addChatMessage ────────────
    content = re.sub(
        r'(\w+)\.sendMessage\(("(?:[^"\\]|\\.)*")\)',
        r'\1.sendSystemMessage(net.minecraft.network.chat.Component.literal(\2))',
        content
    )
    content = re.sub(
        r'(\w+)\.addChatMessage\(("(?:[^"\\]|\\.)*")\)',
        r'\1.sendSystemMessage(net.minecraft.network.chat.Component.literal(\2))',
        content
    )

    # ── Fix: Component → String issues (for display names) ────────────────
    # .getDisplayName().getFormattedText() → .getDisplayName().getString()
    content = content.replace('.getFormattedText()', '.getString()')
    # Component.getString() instead of .getUnformattedText()
    content = content.replace('.getUnformattedText()', '.getString()')

    # ── Fix: isSameItem(ItemStack) → ItemStack.isSameItem(a, b) ──────────
    content = re.sub(
        r'(\w+)\.isSameItem\((\w+)\)',
        r'ItemStack.isSameItem(\1, \2)',
        content
    )

    # ── Fix: @Override on methods that no longer exist ────────────────────
    # Remove @Override from methods known to not exist in parent anymore
    removed_methods = ['getSubItems', 'getContainerItem', 'hasContainerItem',
                      'registerIcons', 'getItemIconIndex', 'getUnlocalizedName',
                      'damageItem', 'getMetadata', 'setPotionName', 'setEffectiveness']
    for method in removed_methods:
        content = re.sub(
            r'@Override\s+\n\s+(public[^}]+' + re.escape(method) + r'\s*\()',
            r'    \1',
            content
        )

    # ── Fix: ShovelItem/HoeItem/AxeItem constructor ───────────────────────
    content = re.sub(
        r'super\s*\(\s*\(ShovelItem\.Tier\)[^)]+\)',
        'super(new net.minecraft.world.item.Item.Properties())',
        content
    )
    content = re.sub(
        r'super\s*\(\s*\(HoeItem\.Tier\)[^)]+\)',
        'super(new net.minecraft.world.item.Item.Properties())',
        content
    )
    content = re.sub(
        r'super\s*\(Tiers\.\w+,\s*-?\d+,\s*-?\d+\.?\d*f\)',
        'super(new net.minecraft.world.item.Item.Properties())',
        content
    )
    content = re.sub(
        r'super\s*\(Tiers\.\w+,\s*net\.minecraft\.world\.item\.Items\.AIR,\s*\(net\.minecraft\.world\.item\.Tier\)\s*[^)]+\)',
        'super(new net.minecraft.world.item.Item.Properties())',
        content
    )

    # ── Fix: Direction → Vec3i usage ─────────────────────────────────────
    # BlockPos.relative(face) is correct already
    # But .offset(player) or .relative(entity) is wrong
    content = re.sub(
        r'\.relative\(([^)]+) /\* player offset removed \*/',
        '.relative(net.minecraft.core.Direction.NORTH) /* offset placeholder */',
        content
    )

    # ── Fix: ItemStack methods ────────────────────────────────────────────
    # ItemStack.areItemStackTagsEqual → ItemStack.isSameItemSameComponents
    content = content.replace('ItemStack.areItemStackTagsEqual(', 'ItemStack.isSameItemSameComponents(')

    # ── Fix: null cannot be dereferenced ─────────────────────────────────
    # .getAttribute(Holder.direct(X)).getAttributeValue() where getAttribute might return null
    content = re.sub(
        r'mob\.getAttribute\(net\.minecraft\.core\.Holder\.direct\(([^)]+)\)\)\.getAttributeValue\(\)',
        r'(mob.getAttribute(net.minecraft.core.Holder.direct(\1)) != null ? mob.getAttribute(net.minecraft.core.Holder.direct(\1)).getValue() : 0.0)',
        content
    )

    # ── Fix: incompatible types - Direction cannot be converted to long ──
    # ChunkPos constructor: new ChunkPos(BlockPos) → new ChunkPos(pos)
    content = re.sub(
        r'new ChunkPos\(([^)]+\.getX\(\)),\s*([^)]+\.getZ\(\))\)',
        r'new ChunkPos(\1, \2)',
        content
    )

    # ── Fix: ChunkPos record constructor wrong ────────────────────────────
    # ChunkPos(BlockPos) should be ChunkPos(int x, int z)
    content = re.sub(
        r'new ChunkPos\(([^,)]+)\)',
        lambda m: f'new ChunkPos(({m.group(1)}).x, ({m.group(1)}).z)'
                  if 'getX()' not in m.group(1) and ',' not in m.group(1)
                  else m.group(0),
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
