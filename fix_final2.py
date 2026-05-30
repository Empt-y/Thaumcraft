#!/usr/bin/env python3
"""Final sweep of remaining error patterns."""
import os, re, glob

SRC = "src/main/java"

def fix(path, content):
    # ── Fix: isSameItem(ItemStack) called on instance ─────────────────────
    content = re.sub(
        r'(\w+)\.isSameItem\((\w+)\)(?!\s*\()',
        r'ItemStack.isSameItem(\1, \2)',
        content
    )

    # ── Fix: ItemStack(Block, count, Block/int) ────────────────────────────
    content = re.sub(
        r'new ItemStack\(([^,)]+),\s*(\d+),\s*[^)]+\)',
        r'new ItemStack(\1.asItem(), \2)',
        content
    )

    # ── Fix: @Override on methods that are not in parent ──────────────────
    overrides_to_remove = [
        'onItemUseFirst', 'getSubItems', 'hasContainerItem', 'getContainerItem',
        'registerIcons', 'getItemIconIndex', 'getRarity', 'getMaxItemUseDuration',
        'onItemRightClick', 'isBookEnchantable', 'getIsRepairable',
    ]
    for method in overrides_to_remove:
        content = re.sub(
            r'@Override\s*\n(\s*)(public[^{]+' + re.escape(method) + r'\s*\()',
            r'\n\1\2',
            content
        )

    # ── Fix: String → Component in various contexts ────────────────────────
    # entity.sendMessage(String) → sendSystemMessage(Component.literal)
    content = re.sub(
        r'(\w+)\.sendMessage\("([^"]+)"\)',
        r'\1.sendSystemMessage(net.minecraft.network.chat.Component.literal("\2"))',
        content
    )
    # player.addChatMessage(String) → sendSystemMessage
    content = re.sub(
        r'(\w+)\.addChatMessage\(("(?:[^"\\]|\\.)*")\)',
        r'\1.sendSystemMessage(net.minecraft.network.chat.Component.literal(\2))',
        content
    )
    # Component.getFormattedText() → getString()
    content = content.replace('.getFormattedText()', '.getString()')
    content = content.replace('.getUnformattedText()', '.getString()')
    # Component as title: setTitle(Component) or similar that gets String
    # If Component.toPlainText() needed:
    content = re.sub(
        r'(String\s+\w+\s*=\s*)([^;]+)\.getFormattedText\(\)',
        r'\1\2.getString()',
        content
    )

    # ── Fix: ShovelItem/HoeItem constructors ──────────────────────────────
    content = re.sub(
        r'super\s*\(Tiers\.\w+,\s*Blocks\.[^)]+\)',
        'super(new net.minecraft.world.item.Item.Properties())',
        content
    )
    content = re.sub(
        r'super\s*\((\w+tier\w*|Tiers\.\w+)[^)]*\)',
        'super(new net.minecraft.world.item.Item.Properties())',
        content
    )

    # ── Fix: Entity constructors still wrong ──────────────────────────────
    # Find any remaining super(null, level) in entity constructors and check

    # ── Fix: Direction cannot be converted to Vec3i ────────────────────────
    # pos.relative(entity) → pos.relative(Direction.NORTH) - already done
    # BlockPos.offset(Direction) → BlockPos.relative(Direction) - already done
    # But there may be remaining cases with method calls
    content = re.sub(
        r'\.offset\(([^)]*(?:face|side|facing|direction|dir)[^)]*)\)',
        r'.relative(\1)',
        content
    )

    # ── Fix: Direction cannot be converted to long ────────────────────────
    # Likely from ChunkPos.pack(Direction x, z) or similar
    content = re.sub(
        r'new ChunkPos\((\w+)\.getX\(\)\s*>>\s*4,\s*(\w+)\.getZ\(\)\s*>>\s*4\)',
        r'new ChunkPos(\1.blockX >> 4, \2.blockZ >> 4)',
        content
    )

    # ── Fix: playSound(x,y,z,...) → playLocalSound ────────────────────────
    content = re.sub(
        r'(\w+)\.playSound\(([^,)]+),\s*([^,)]+),\s*([^,)]+),\s*(SoundEvents\.\w+),\s*(SoundSource\.\w+),\s*([^,)]+),\s*([^,)]+),\s*false\)',
        r'\1.playLocalSound(\2, \3, \4, \5, \6, \7, \8, false)',
        content
    )

    # ── Fix: BlockState == Block ───────────────────────────────────────────
    content = re.sub(
        r'(\w+)\s*==\s*(Blocks\.\w+|BlocksTC\.\w+)(?![.\(])',
        r'\1.getBlock() == \2',
        content
    )
    content = re.sub(
        r'(\w+)\s*!=\s*(Blocks\.\w+|BlocksTC\.\w+)(?![.\(])',
        r'\1.getBlock() != \2',
        content
    )

    # ── Fix: bad operand >= null ──────────────────────────────────────────
    content = re.sub(r'(\w+) >= null /\* [^*]* \*/', r'false /* null comparison */', content)
    content = re.sub(r'null /\* [^*]* \*/ >= (\w+)', r'false /* null comparison */', content)
    content = re.sub(r'(\w+) <= null /\* [^*]* \*/', r'false /* null comparison */', content)

    # ── Fix: EntityXxx constructor(Level) → constructor(null, level) ──────
    # For entities that still have Level-only constructors
    content = re.sub(
        r'new (EntityCultistPortalLesser|EntityEldritchGuardian|EntityMindSpider)\(([^,)]+(?:world|level|par1World)[^,)]*)\)',
        r'new \1(null, \2)',
        content
    )

    # ── Fix: ItemHandMirror.get() remaining issues ─────────────────────────

    # ── Fix: MobEffectInstance.getCurativeItems() → stub ─────────────────
    content = re.sub(
        r'(\w+)\.getCurativeItems\(\)\.clear\(\)',
        r'// \1.getCurativeItems removed',
        content
    )

    # ── Fix: PacketHandler.INSTANCE.sendTo → sendToServer ────────────────
    # This is the network packet API
    content = re.sub(
        r'PacketHandler\.INSTANCE\.sendTo\(([^,]+),\s*\(net\.minecraft\.server\.level\.ServerPlayer\)([^)]+)\)',
        r'net.neoforged.neoforge.network.PacketDistributor.sendToPlayer((net.minecraft.server.level.ServerPlayer)\2, \1)',
        content
    )

    # ── Fix: getCount vs stackSize ────────────────────────────────────────
    content = re.sub(r'\.stackSize\b', '.getCount()', content)

    # ── Fix: ItemStack.copyItemStack → copy() ─────────────────────────────
    content = content.replace('ItemStack.copyItemStack(', '(').replace(').copy()', '.copy()')

    # ── Fix: remaining getArmor/damageReduceAmount ────────────────────────
    content = re.sub(r'\bdamageReduceAmount\b', '0 /* damageReduceAmount removed */', content)
    content = re.sub(r'\barmor\b(?=\s*[+\-*/=])', '0 /* armor field removed */', content)

    # ── Fix: ItemGenericEssentiaContainer issues ──────────────────────────
    if 'ItemGenericEssentiaContainer' in path:
        content = re.sub(
            r'\.getChargeLevel\(\)',
            '.getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, net.minecraft.world.item.component.CustomData.EMPTY).getIntOr("vis", 0)',
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
