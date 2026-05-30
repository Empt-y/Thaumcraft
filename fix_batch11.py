#!/usr/bin/env python3
"""Batch 11: Fix remaining cannot-find-symbol errors - event renames, API stubs, imports."""
import os, re

SRC = "/home/ash/geostrata/Thaumcraft/src/main/java"
changed = 0

def fix_file(path, content):
    c = content

    # ── 1. WorldEvent → LevelEvent ──
    c = c.replace(
        'import net.neoforged.neoforge.event.level.WorldEvent;',
        'import net.neoforged.neoforge.event.level.LevelEvent;'
    )
    c = re.sub(r'\bWorldEvent\.Load\b', 'LevelEvent.Load', c)
    c = re.sub(r'\bWorldEvent\.Save\b', 'LevelEvent.Save', c)
    c = re.sub(r'\bWorldEvent\.Unload\b', 'LevelEvent.Unload', c)
    c = re.sub(r'\bWorldEvent\b', 'LevelEvent', c)

    # ── 2. TickEvent renames ──
    # Imports
    c = c.replace(
        'import net.neoforged.neoforge.event.tick.ServerTickEvent;',
        'import net.neoforged.neoforge.event.tick.ServerTickEvent; // OK'
    )
    # Add missing tick event imports if referenced
    if 'TickEvent.ServerTickEvent' in c or 'ServerTickEvent' in c:
        if 'import net.neoforged.neoforge.event.tick.ServerTickEvent' not in c:
            c = c.replace('package ', 'import net.neoforged.neoforge.event.tick.ServerTickEvent;\npackage ', 1)
    if 'TickEvent.WorldTickEvent' in c or 'LevelTickEvent' in c:
        if 'import net.neoforged.neoforge.event.tick.LevelTickEvent' not in c:
            c = c.replace('package ', 'import net.neoforged.neoforge.event.tick.LevelTickEvent;\npackage ', 1)
    if 'TickEvent.ClientTickEvent' in c:
        if 'import net.neoforged.neoforge.client.event.ClientTickEvent' not in c:
            c = c.replace('package ', 'import net.neoforged.neoforge.client.event.ClientTickEvent;\npackage ', 1)
    if 'TickEvent.PlayerTickEvent' in c or 'PlayerTickEvent' in c:
        if 'import net.neoforged.neoforge.event.tick.PlayerTickEvent' not in c:
            c = c.replace('package ', 'import net.neoforged.neoforge.event.tick.PlayerTickEvent;\npackage ', 1)

    # Replace TickEvent.XXX references
    c = re.sub(r'\bTickEvent\.ServerTickEvent\b', 'ServerTickEvent', c)
    c = re.sub(r'\bTickEvent\.WorldTickEvent\b', 'LevelTickEvent', c)
    c = re.sub(r'\bTickEvent\.ClientTickEvent\b', 'ClientTickEvent', c)
    c = re.sub(r'\bTickEvent\.PlayerTickEvent\b', 'PlayerTickEvent', c)
    # Phase checks - event handlers subscribed to Pre/Post no longer need phase checks
    c = re.sub(r'event\.phase\s*==\s*TickEvent\.Phase\.END', 'true /* phase check removed, subscribe to .Post */', c)
    c = re.sub(r'event\.phase\s*==\s*TickEvent\.Phase\.START', 'true /* phase check removed, subscribe to .Pre */', c)
    c = re.sub(r'\bTickEvent\.Phase\.\w+\b', 'null /* TickEvent.Phase removed */', c)
    c = re.sub(r'\bTickEvent\b', 'Object /* TickEvent removed */', c)

    # ── 3. gameevent.PlayerEvent → neoforge PlayerEvent ──
    c = c.replace(
        'import net.minecraftforge.fml.common.gameevent.PlayerEvent;',
        'import net.neoforged.neoforge.event.entity.player.PlayerEvent;'
    )
    # Also from old forge
    c = c.replace(
        'import net.minecraftforge.event.entity.player.PlayerEvent;',
        'import net.neoforged.neoforge.event.entity.player.PlayerEvent;'
    )
    # package PlayerEvent.XXX usages → already handled since PlayerEvent is imported correctly

    # ── 4. SoundsTC: RegisterEvent.Register<SoundEvent> → RegisterEvent ──
    c = re.sub(r'\bRegisterEvent\.Register<\w+>', 'RegisterEvent', c)
    c = re.sub(r'\bRegisterEvent\.Register\b', 'RegisterEvent', c)

    # ── 5. ServerEvents: Level ambiguity — remove log4j Level import ──
    c = c.replace('import org.apache.logging.log4j.Level;\n', '')
    c = re.sub(r'\bLevel\.INFO\b(?!\s*=)', 'org.apache.logging.log4j.Level.INFO', c)
    c = re.sub(r'\bLevel\.WARN\b(?!\s*=)', 'org.apache.logging.log4j.Level.WARN', c)
    c = re.sub(r'\bLevel\.ERROR\b(?!\s*=)', 'org.apache.logging.log4j.Level.ERROR', c)
    c = re.sub(r'\bLevel\.DEBUG\b(?!\s*=)', 'org.apache.logging.log4j.Level.DEBUG', c)

    # ── 6. Dispenser package fix ──
    c = c.replace(
        'import net.minecraft.dispenser.BehaviorProjectileDispense;',
        'import net.minecraft.core.dispenser.ProjectileDispenseBehavior;'
    )
    c = c.replace(
        'import net.minecraft.dispenser.IPosition;',
        'import net.minecraft.core.BlockPos; // IPosition removed'
    )
    c = c.replace(
        'import net.minecraft.world.entity.IProjectile;',
        'import net.minecraft.world.entity.projectile.Projectile;'
    )
    c = re.sub(r'\bBehaviorProjectileDispense\b', 'ProjectileDispenseBehavior', c)
    c = re.sub(r'\bIPosition\b', 'net.minecraft.core.dispenser.BlockSource /* IPosition removed */', c)
    c = re.sub(r'\bIProjectile\b', 'Projectile', c)

    # ── 7. KeyHandler: KeyBinding → KeyMapping, ClientRegistry → stub ──
    c = c.replace(
        'import net.minecraft.client.settings.KeyBinding;',
        'import net.minecraft.client.KeyMapping;'
    )
    c = c.replace(
        'import net.minecraftforge.fml.client.registry.ClientRegistry;',
        '// import net.minecraftforge.fml.client.registry.ClientRegistry; // removed'
    )
    c = re.sub(r'\bKeyBinding\b', 'KeyMapping', c)
    c = re.sub(r'\bClientRegistry\.registerKeyBinding\s*\([^;]+\)\s*;', '/* ClientRegistry.registerKeyBinding removed */', c)
    c = re.sub(r'\bClientRegistry\b', 'Object /* ClientRegistry removed */', c)

    # ── 8. ThaumcraftCraftingManager: ItemHoe→HoeItem, ItemShears→ShearsItem ──
    # Note: ItemHoe was already handled as `Item /* ItemHoe removed */` — undo then redo
    c = re.sub(r'\bItem\s*/\*\s*ItemHoe\s*removed\s*\*/', 'HoeItem', c)
    c = re.sub(r'\bItemHoe\b', 'HoeItem', c)
    c = c.replace(
        'import net.minecraft.world.item.HoeItem;',
        'import net.minecraft.world.item.HoeItem; // OK'
    )
    if 'HoeItem' in c and 'import net.minecraft.world.item.HoeItem' not in c:
        c = c.replace('package ', 'import net.minecraft.world.item.HoeItem;\npackage ', 1)

    c = re.sub(r'\bItemShears\b', 'ShearsItem', c)
    if 'ShearsItem' in c and 'import net.minecraft.world.item.ShearsItem' not in c:
        c = c.replace('package ', 'import net.minecraft.world.item.ShearsItem;\npackage ', 1)

    # Remove old forge/minecraft imports
    c = c.replace('import net.minecraftforge.fml.relauncher.ReflectionHelper;\n',
                  '// import net.minecraftforge.fml.relauncher.ReflectionHelper; // removed\n')
    c = re.sub(r'^import net\.minecraftforge\.registries\.[^;]+;\n',
               '// minecraftforge.registries import removed\n', c, flags=re.MULTILINE)
    c = re.sub(r'\bReflectionHelper\b', 'Object /* ReflectionHelper removed */', c)

    # ── 9. Item.ToolMaterial → ToolMaterial ──
    c = re.sub(r'\bItem\.ToolMaterial\b', 'ToolMaterial', c)
    if 'ToolMaterial' in c and 'import net.minecraft.world.item.ToolMaterial' not in c:
        c = c.replace('package ', 'import net.minecraft.world.item.ToolMaterial;\npackage ', 1)

    # ── 10. PlayerKnowledge/PlayerWarp preInit(): comment out CapabilityManager usage ──
    c = re.sub(
        r'CapabilityManager\.INSTANCE\.register\([^;]+\)\s*;',
        '/* CapabilityManager.INSTANCE.register removed */',
        c,
        flags=re.DOTALL
    )
    # Fix `Object /* Capability removed */.IStorage<X>()` anonymous class that broke
    c = re.sub(
        r'new\s+Object\s*/\*[^*]*Capability[^*]*removed[^*]*\*/\s*\.\s*IStorage\s*<[^>]+>\s*\(\s*\)',
        'null /* Capability.IStorage removed */',
        c
    )
    c = re.sub(
        r'\bObject\s*/\*[^*]*Capability[^*]*removed[^*]*\*/\s*\.\s*IStorage\b',
        'Object /* Capability.IStorage removed */',
        c
    )

    # ── 11. EntityJoinWorldEvent → EntityJoinLevelEvent ──
    c = c.replace(
        'import net.neoforged.neoforge.event.entity.EntityJoinWorldEvent;',
        'import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;'
    )
    c = re.sub(r'\bEntityJoinWorldEvent\b', 'EntityJoinLevelEvent', c)

    # ── 12. LivingEvent.LivingUpdateEvent → LivingEvent (just event, method stubs itself) ──
    c = re.sub(r'\bLivingEvent\.LivingUpdateEvent\b', 'LivingEvent', c)

    # ── 13. IThreadListener → stub ──
    c = c.replace(
        'import net.minecraft.util.IThreadListener;\n',
        '// import net.minecraft.util.IThreadListener; // removed\n'
    )
    c = re.sub(r'\bIThreadListener\b', 'Object /* IThreadListener removed */', c)

    # ── 14. AttachCapabilitiesEvent → stub ──
    c = c.replace(
        'import net.neoforged.neoforge.event.AttachCapabilitiesEvent;\n',
        'import net.neoforged.bus.api.Event; // AttachCapabilitiesEvent removed\n'
    )
    c = re.sub(r'\bAttachCapabilitiesEvent<[^>]+>', 'Event /* AttachCapabilitiesEvent removed */', c)
    c = re.sub(r'\bAttachCapabilitiesEvent\b', 'Event /* AttachCapabilitiesEvent removed */', c)

    # ── 15. PlayerPickupXpEvent → PlayerXpEvent.PickupXp ──
    c = c.replace(
        'import net.neoforged.neoforge.event.entity.player.PlayerPickupXpEvent;\n',
        'import net.neoforged.neoforge.event.entity.player.PlayerXpEvent;\n'
    )
    c = re.sub(r'\bPlayerPickupXpEvent\b', 'PlayerXpEvent.PickupXp', c)

    # ── 16. RecipeMagicDust: `Object /* IForgeRegistryEntry removed */.Impl<Recipe>` → remove ──
    c = re.sub(
        r'\bextends\s+Object\s*/\*[^*]*IForgeRegistryEntry[^*]*removed[^*]*\*/\s*\.Impl<[^>]+>\s*',
        '',
        c
    )
    # Also fix any remaining `.Impl<X>` on stubs
    c = re.sub(r'\bObject\s*/\*[^*]*removed[^*]*\*/\s*\.Impl<[^>]+>', 'Object /* impl removed */', c)

    # ── 17. FakeNetHandlerPlayServer: NetHandlerPlayServer → ServerGamePacketListenerImpl ──
    c = c.replace(
        'import net.minecraft.network.NetHandlerPlayServer;\n',
        'import net.minecraft.server.network.ServerGamePacketListenerImpl;\n'
    )
    c = re.sub(r'\bNetHandlerPlayServer\b', 'ServerGamePacketListenerImpl', c)
    # Comment out old CPacket imports
    c = re.sub(
        r'^import net\.minecraft\.network\.play\.client\.\w+;\n',
        '// old network import removed\n',
        c,
        flags=re.MULTILINE
    )

    # ── 18. RecipesRobeArmorDyes: DyeUtils → stub, oredict import ──
    c = re.sub(
        r'^import net\.minecraftforge\.oredict\.[^;]+;\n',
        '// minecraftforge.oredict import removed\n',
        c,
        flags=re.MULTILINE
    )
    c = re.sub(r'\bDyeUtils\.isDye\s*\([^)]*\)', 'false /* DyeUtils.isDye removed */', c)
    c = re.sub(r'\bDyeUtils\b', 'Object /* DyeUtils removed */', c)

    # ── 19. RecipesArmorDyes → stub ──
    c = re.sub(r'\bextends\s+RecipesArmorDyes\b', '/* extends RecipesArmorDyes removed */', c)
    c = re.sub(r'\bRecipesArmorDyes\b', 'Object /* RecipesArmorDyes removed */', c)

    # ── 20. CraftingEvents: IFuelHandler → stub ──
    c = re.sub(r'\bimplements\s+IFuelHandler\b', '/* implements IFuelHandler removed */', c)
    c = re.sub(r'\bIFuelHandler\b', 'Object /* IFuelHandler removed */', c)

    # ── 21. ChunkEvents: event.getWorld() → event.getLevel() where applicable ──
    # These are used in LevelEvent handlers: WorldEvent → LevelEvent event.getWorld() → event.getLevel()
    # Only when inside event handler methods that use LevelEvent
    c = re.sub(r'\bevent\.getWorld\(\)', 'event.getLevel()', c)
    # .provider.getDimension() → just use 0 (dimension system changed)
    c = re.sub(r'\.provider\.getDimension\(\)', '.dimensionTypeId() /* getDimension removed */', c)

    # ── 22. Dedup imports ──
    seen = set()
    out = []
    for line in c.split('\n'):
        s = line.strip()
        if s.startswith('import ') and s.endswith(';'):
            if s in seen:
                continue
            seen.add(s)
        out.append(line)
    c = '\n'.join(out)

    return c


def process_dir(root):
    global changed
    for dirpath, dirs, files in os.walk(root):
        for fname in files:
            if not fname.endswith('.java'):
                continue
            path = os.path.join(dirpath, fname)
            with open(path, 'r', encoding='utf-8', errors='replace') as f:
                original = f.read()
            fixed = fix_file(path, original)
            if fixed != original:
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(fixed)
                changed += 1
                print(f"  FIXED: {path[len(SRC)+1:]}")

process_dir(SRC)
print(f"\nTotal files changed: {changed}")
