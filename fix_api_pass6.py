#!/usr/bin/env python3
"""Pass 6: Fix ItemStack NBT access, getPos(), getBlockPos on entities, sendSystemMessage."""
import os, re, sys, glob

SRC = "src/main/java"

DC_PREFIX = ('net.minecraft.world.item.component.CustomData.EMPTY).copyTag().')
DC_FULL   = ('getOrDefault(net.minecraft.core.component.DataComponents.CUSTOM_DATA, '
             'net.minecraft.world.item.component.CustomData.EMPTY).copyTag().')

# ItemStack instance variable name patterns (not CompoundTag)
IS_VARS = r'(?:stack\d*|focusstack|itemstack\d*|helm\d*|boots\d*|chest\d*|legs\d*|is|item\d*|'
IS_VARS += r'armor\d*|curio\d*|bauble\d*|wand\d*|mainhand|offhand|handStack|stackInSlot|'
IS_VARS += r'outStack|inStack|testStack|result|output|craftResult|lootBag)'

def is_itemstack_var(name):
    """Check if a variable name looks like an ItemStack, not a CompoundTag."""
    name_lower = name.lower()
    # Definitely CompoundTag
    if any(x in name_lower for x in ('nbt', 'tag', 'compound', 'data')):
        return False
    # Definitely ItemStack
    if any(x in name_lower for x in ('stack', 'item', 'helm', 'boot', 'chest', 'leg',
                                       'armor', 'curio', 'bauble', 'wand', 'hand', 'focus')):
        return True
    return False

def fix(path, content):
    # ── 1. ItemStack.contains("key") → DataComponents check ──────────────────
    def fix_is_contains(m):
        var = m.group(1)
        key = m.group(2)
        if is_itemstack_var(var):
            return (f'{var}.{DC_FULL}contains("{key}")')
        return m.group(0)

    content = re.sub(
        r'(\w+)\.contains\("([^"]+)"\)',
        fix_is_contains,
        content
    )

    # ── 2. ItemStack.getIntOr/getLongOr/getStringOr etc. → DataComponents ────
    for method in ('getIntOr', 'getLongOr', 'getByteOr', 'getShortOr', 'getFloatOr',
                   'getStringOr', 'getBooleanOr'):
        def fix_is_method(m, method=method):
            var = m.group(1)
            args = m.group(2)
            if is_itemstack_var(var):
                return f'{var}.{DC_FULL}{method}({args})'
            return m.group(0)

        content = re.sub(
            r'(\w+)\.' + method + r'\(([^)]+)\)',
            fix_is_method,
            content
        )

    # ── 3. BlockEntity.getPos() → getBlockPos() ───────────────────────────────
    # In BlockEntity subclasses (TileXxx classes)
    if '/tiles/' in path or '/tile/' in path.lower():
        content = re.sub(r'(?<![.\w])getPos\(\)', 'getBlockPos()', content)
        content = re.sub(r'(?<![.\w])this\.pos\b', 'this.getBlockPos()', content)
        content = re.sub(r'\bsuper\.getPos\(\)', 'super.getBlockPos()', content)

    # Also for golem and BlockEntity base
    content = re.sub(
        r'\b(te|tile|tileEntity|blockEntity|be)\b\.getPos\(\)',
        r'\1.getBlockPos()',
        content
    )

    # ── 4. sendSystemMessage(String) → sendSystemMessage(Component.literal()) ─
    content = re.sub(
        r'\.sendSystemMessage\(("(?:[^"\\]|\\.)*")\)',
        r'.sendSystemMessage(net.minecraft.network.chat.Component.literal(\1))',
        content
    )
    # Also sendMessage -> sendSystemMessage for Component args
    content = re.sub(
        r'\.sendMessage\((net\.minecraft\.network\.chat\.Component\.[^)]+)\)',
        r'.sendSystemMessage(\1)',
        content
    )

    # ── 5. getWorld() → level() for entities ──────────────────────────────────
    # Any entity variable's getWorld() → level()
    content = re.sub(
        r'\b(\w+)\b\.getWorld\(\)',
        lambda m: (
            m.group(1) + '.level()' if not m.group(1).startswith('event')
            else m.group(1) + '.getLevel()'
        ),
        content
    )

    # ── 6. Entity.getBlockPos() → blockPosition() (catch remaining) ───────────
    content = re.sub(
        r'\b(caster|shooter|thrower|owner|rider|vehicle|passenger)\b\.getBlockPos\(\)',
        lambda m: m.group(1) + '.blockPosition()',
        content
    )
    # In entity class: this.getBlockPos() that isn't BlockEntity
    if '/entities/' in path or '/golems/' in path:
        content = re.sub(r'(?<![.\w])getBlockPos\(\)', 'blockPosition()', content)

    # ── 7. Registration builder methods ───────────────────────────────────────
    content = re.sub(r'\s*\.setCreativeTab\s*\([^)]*\)', '', content)
    content = re.sub(r'\s*\.setUnlocalizedName\s*\([^)]*\)', '', content)
    content = re.sub(r'\s*\.setRegistryName\s*\([^)]*\)', '', content)
    content = re.sub(r'\s*\.setHasSubtypes\s*\([^)]*\)', '', content)
    content = re.sub(r'\s*\.setMaxStackSize\s*\([^)]*\)', '', content)

    # ── 8. isValidRepairItem — fix body variable references ───────────────────
    # These are already correct (stack1/stack2) — leave alone

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
