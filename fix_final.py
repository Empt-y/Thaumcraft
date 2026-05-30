#!/usr/bin/env python3
"""Comprehensive fix script for Thaumcraft NeoForge 26.1.2 port."""

import os
import re
import sys

SRC = "src/main/java"

def fix_file(path, content):
    orig = content

    # ── GuiGraphics → GuiGraphicsExtractor ──────────────────────────────
    content = content.replace(
        "import net.minecraft.client.gui.GuiGraphics;",
        "import net.minecraft.client.gui.GuiGraphicsExtractor;"
    )
    content = re.sub(r'\bGuiGraphics\b(?!Extractor)', 'GuiGraphicsExtractor', content)

    # ── FocusPackage: node.type → node.getType() ────────────────────────
    if 'FocusPackage.java' in path:
        content = re.sub(r'\bnode\.type\b', 'node.getType()', content)

    # ── AuraChunk: chunk.getBlockPos() → chunk.getPos() ─────────────────
    if 'AuraChunk.java' in path:
        content = content.replace("loc = chunk.getBlockPos();", "loc = chunk.getPos();")
        # fix chunkRef.get("") -> chunkRef.get()
        content = content.replace('chunkRef.get("")', 'chunkRef.get()')

    # ── FocusPackage.world → pack.world (level() calls on FocusPackage) ─
    # Any call `pack.level()` should be `pack.world` since FocusPackage has field `world`
    content = re.sub(r'\b(pack|fp)\s*\.\s*level\(\)', r'\1.world', content)

    # ── Entity.world field access → entity.level() ───────────────────────
    # Pattern: someEntityVar.world  (accessing entity's world field)
    # Patterns seen: p_i1625_1_.world, theEntity.world, golem.world, etc.
    # But NOT: FocusPackage.world (field is intentional)
    # We do: identifier.world where identifier is not 'this' context and 'world' is used as field
    content = re.sub(r'\b(p_i\d+_\d+_)\.world\b', r'\1.level()', content)

    # ── .getBlockPos() on entities → .blockPosition() ────────────────────
    # getBlockPos() on LevelChunk returns something different (it's removed or changed)
    # but on entities it maps to blockPosition()
    # Being careful not to break LevelChunk usage which is fixed separately above
    content = re.sub(r'(?<!\bchunk)\.getBlockPos\(\)', '.blockPosition()', content)
    # Also standalone getBlockPos() within entity methods
    content = re.sub(r'\bgetBlockPos\(\)', 'blockPosition()', content)

    # ── setHomePosAndDistance → setHomeTo ────────────────────────────────
    content = content.replace('setHomePosAndDistance(', 'setHomeTo(')

    # ── isWithinHomeDistanceFromPosition → isWithinHome ──────────────────
    content = content.replace('isWithinHomeDistanceFromPosition(', 'isWithinHome(')

    # ── detachHome() → clearHome() ────────────────────────────────────────
    content = content.replace('detachHome()', 'clearHome()')

    # ── pathfinder.clearPath() → pathfinder.stop() ────────────────────────
    content = re.sub(r'(petPathfinder|pathFinder|navigation|getNavigation\(\))\.clearPath\(\)',
                     lambda m: m.group(0).replace('.clearPath()', '.stop()'), content)
    content = re.sub(r'\.clearPath\(\)', '.stop()', content)

    # ── pathfinder.noPath() → pathfinder.isDone() ─────────────────────────
    content = re.sub(r'\.noPath\(\)', '.isDone()', content)

    # ── tryMoveToXYZ → moveTo ─────────────────────────────────────────────
    content = content.replace('tryMoveToXYZ(', 'moveTo(')
    content = content.replace('tryMoveToMob(', 'moveTo(')
    content = content.replace('tryMoveTo(', 'moveTo(')

    # ── markDirty() → setChanged() ────────────────────────────────────────
    content = content.replace('markDirty()', 'setChanged()')

    # ── swingArm( → swing( ────────────────────────────────────────────────
    content = content.replace('swingArm(', 'swing(')

    # ── setItemStackToSlot → setItemSlot ──────────────────────────────────
    content = content.replace('setItemStackToSlot(', 'setItemSlot(')

    # ── EntityDataSerializers.VARINT → EntityDataSerializers.INT ──────────
    content = content.replace('EntityDataSerializers.VARINT', 'EntityDataSerializers.INT')

    # ── SynchedEntityData.createKey → SynchedEntityData.defineId ─────────
    content = content.replace('SynchedEntityData.createKey(', 'SynchedEntityData.defineId(')

    # ── entityData.register → builder.define + entityInit → defineSynchedData
    # This is complex - need to transform the whole method
    # Pattern: @Override protected void entityInit() { super.entityInit(); entityData.register(X, Y); ... }
    # →       @Override protected void defineSynchedData(SynchedEntityData.Builder builder) { super.defineSynchedData(builder); builder.define(X, Y); ... }
    if 'entityInit()' in content and 'entityData.register(' in content:
        content = re.sub(
            r'protected void entityInit\(\)',
            'protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder)',
            content
        )
        content = re.sub(
            r'super\.entityInit\(\)',
            'super.defineSynchedData(builder)',
            content
        )
        content = re.sub(
            r'entityData\.register\(([^,]+),\s*(.+?)\)',
            r'builder.define(\1, \2)',
            content
        )

    # ── tasks.taskEntries.clear() / targetSelector.taskEntries.clear() ───
    content = re.sub(
        r'\btasks\.taskEntries\.clear\(\)',
        'goalSelector.removeAllGoals(g -> true)',
        content
    )
    content = re.sub(
        r'\btargetSelector\.taskEntries\.clear\(\)',
        'targetSelector.removeAllGoals(g -> true)',
        content
    )

    # ── EntityAIWatchClosest → LookAtPlayerGoal ───────────────────────────
    content = content.replace('EntityAIWatchClosest', 'LookAtPlayerGoal')
    content = content.replace('EntityAILookIdle', 'RandomLookAroundGoal')

    # ── swingProgressInt → swingTime ─────────────────────────────────────
    content = content.replace('swingProgressInt', 'swingTime')

    # ── isSwingInProgress → swinging ──────────────────────────────────────
    content = content.replace('isSwingInProgress', 'swinging')

    # ── isClientSide field access → isClientSide() method ────────────────
    # Pattern: .isClientSide without ()
    content = re.sub(r'\.isClientSide\b(?!\s*\()', '.isClientSide()', content)

    # ── yRot private field access ─────────────────────────────────────────
    # Read: entity.yRot → entity.getYRot()
    # Write patterns: entity.yRot = → entity.setYRot(  [but assignment end needs ) ]
    # Only fix known patterns carefully
    # For reading: someVar.yRot  where someVar is not 'this' (this.yRot may still work via method)
    content = re.sub(
        r'\b((?:entity|target|player|living|mob|golem|e|p|l|ent|theEntity|thePet|theOwner|attacker|defender|owner)(?:\([^)]*\))?)\s*\.\s*yRot\b(?!\s*=)',
        r'\1.getYRot()',
        content
    )
    # Also fix ((Entity)foo).yRot type patterns
    content = re.sub(r'\)\s*\.\s*yRot\b(?!\s*=)', r').getYRot()', content)

    # ── speed private in LivingEntity ────────────────────────────────────
    # this.speed = X → this.setSpeed(X)  (but careful with local vars named speed)
    content = re.sub(r'\bthis\s*\.\s*speed\b(?!\s*\()', 'this.getSpeed()', content)

    # ── inventory private in Player → player.getInventory() ──────────────
    content = re.sub(r'\b(player|p)\s*\.\s*inventory\b', r'\1.getInventory()', content)

    # ── stepHeight field → keep as stub for now (override maxUpStep instead)
    # For now just comment it out since we can't easily convert dynamic stepHeight
    content = re.sub(
        r'\bstepHeight\s*=\s*([^;]+);',
        r'/* stepHeight = \1; */ // TODO: override maxUpStep()',
        content
    )

    # ── navigator = / moveHelper = private fields ──────────────────────
    content = re.sub(
        r'\bnavigator\s*=\s*getGolemNavigator\(\)',
        '/* navigator field removed; navigation is set via constructor */',
        content
    )

    # ── LevelChunk.getPos() for ChunkPos ─────────────────────────────────
    # (handled above in AuraChunk section)

    # ── level().getActualHeight() → level().getMaxBuildHeight() ──────────
    content = content.replace('.getActualHeight()', '.getMaxBuildHeight()')

    # ── BlockState.isFullCube() → isCollisionShapeFullBlock() ────────────
    content = re.sub(r'\.isFullCube\(\)', '.isCollisionShapeFullBlock(null, null)', content)

    # ── getMaterial() on BlockState ───────────────────────────────────────
    # BlockState no longer has getMaterial() - use direct checks
    # For blocksMovement: state.getMaterial().blocksMovement() → state.isSolid()
    content = re.sub(r'\.getMaterial\(\)\.blocksMovement\(\)', '.isSolid()', content)
    content = re.sub(r'\.getMaterial\(\)\.isLiquid\(\)', '.liquid()', content)
    content = re.sub(r'\.getMaterial\(\)\.isSolid\(\)', '.isSolid()', content)

    # ── DamageSource method renames ───────────────────────────────────────
    content = content.replace('.isExplosion()', ".is(net.minecraft.tags.DamageTypeTags.IS_EXPLOSION)")
    content = content.replace('.isFireDamage()', ".is(net.minecraft.tags.DamageTypeTags.IS_FIRE)")
    # DamageSource static constants
    content = re.sub(r'\bDamageSource\.CACTUS\b', 'this.damageSources().cactus()', content)
    content = re.sub(r'\bDamageSource\.IN_WALL\b', 'this.damageSources().inWall()', content)
    content = re.sub(r'\bDamageSource\.OUT_OF_WORLD\b', 'this.damageSources().fellOutOfWorld()', content)

    # ── attackEntityFrom → hurt ───────────────────────────────────────────
    content = content.replace('.attackEntityFrom(', '.hurt(')

    # ── getBlockHardness → getDestroySpeed ───────────────────────────────
    content = re.sub(
        r'\.getBlockHardness\(([^,]+),\s*([^)]+)\)',
        r'.getDestroySpeed(\1, \2)',
        content
    )

    # ── isPotionActive → hasEffect ────────────────────────────────────────
    content = content.replace('.isPotionActive(', '.hasEffect(')
    content = content.replace('.getActiveMobEffectInstance(', '.getEffect(')

    # ── scheduleUpdate → scheduleTick ────────────────────────────────────
    content = content.replace('.scheduleUpdate(', '.scheduleTick(')

    # ── OreDictionary usage → stub/comment ───────────────────────────────
    # For now stub out OreDictionary calls since it's been removed
    content = re.sub(
        r'int\[\] ids = OreDictionary\.getOreIDs\([^)]+\);',
        'int[] ids = new int[0]; // OreDictionary removed - use item tags',
        content
    )
    content = re.sub(
        r'OreDictionary\.getOreName\([^)]+\)',
        '""  /* OreDictionary removed */',
        content
    )
    content = re.sub(
        r'OreDictionary\.itemMatches\([^)]+\)',
        'false /* OreDictionary removed */',
        content
    )

    # ── CreativeModeTab.SEARCH → null (or just stub) ─────────────────────
    content = re.sub(r'\bCreativeModeTab\.SEARCH\b', 'null /* SEARCH tab removed */', content)

    # ── setTagInfo on ItemStack → CustomData component ────────────────────
    # stack.setTagInfo("key", new LongTag(v)) →
    #   net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, stack, t -> t.put("key", net.minecraft.nbt.LongTag.valueOf(v)))
    content = re.sub(
        r'(\w+)\.setTagInfo\("(\w+)",\s*new LongTag\(([^)]+)\)\)',
        r'net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, \1, t -> t.putLong("\2", \3))',
        content
    )
    content = re.sub(
        r'(\w+)\.setTagInfo\("(\w+)",\s*new IntTag\(([^)]+)\)\)',
        r'net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, \1, t -> t.putInt("\2", \3))',
        content
    )
    content = re.sub(
        r'(\w+)\.setTagInfo\("(\w+)",\s*new ByteTag\(([^)]+)\)\)',
        r'net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, \1, t -> t.putByte("\2", \3))',
        content
    )
    # General fallback for remaining setTagInfo
    content = re.sub(
        r'(\w+)\.setTagInfo\(("[\w]+")\s*,\s*([^)]+)\)',
        r'net.minecraft.world.item.component.CustomData.update(net.minecraft.core.component.DataComponents.CUSTOM_DATA, \1, t -> t.put(\2, \3))',
        content
    )

    # ── getOrCreateTag() → use CustomData ────────────────────────────────
    # For reads: stack.getTagCompound() / stack.getOrCreateTag()
    # This is complex; for now just leave them and they'll produce errors we can fix later

    # ── Explosion constructor changes ─────────────────────────────────────
    # level.newExplosion(..., true) → level.explode(..., ExplosionInteraction.BLOCK)
    content = re.sub(
        r'createExplosion\(([^,]+),\s*([^,]+),\s*([^,]+),\s*([^,]+),\s*true\)',
        r'explode(\1, \2, \3, \4, net.minecraft.world.level.Explosion.BlockInteraction.DESTROY)',
        content
    )

    # ── Projectile.shoot parameter count fix ─────────────────────────────
    # Old: shoot(x, y, z, speed, inaccuracy)
    # Need to check if this is an issue

    # ── getEntitiesOfClass fixes ──────────────────────────────────────────
    # Old: level.getEntitiesOfClass(Class, entity, AABB) - wrong signature
    # New: level.getEntitiesOfClass(Class, AABB)
    # This is complex - skip for now

    # ── LongTag.valueOf / IntTag.valueOf ──────────────────────────────────
    content = re.sub(r'\bnew LongTag\(([^)]+)\)', r'net.minecraft.nbt.LongTag.valueOf(\1)', content)
    content = re.sub(r'\bnew IntTag\(([^)]+)\)', r'net.minecraft.nbt.IntTag.valueOf(\1)', content)
    content = re.sub(r'\bnew ByteTag\(([^)]+)\)', r'net.minecraft.nbt.ByteTag.valueOf(\1)', content)

    # ── setHasSubtypes / setMaxStackSize / setUnlocalizedName etc. ────────
    # These are old Item builder methods - stub them out
    content = re.sub(r'\.setHasSubtypes\([^)]*\)', '/* setHasSubtypes removed */', content)
    content = re.sub(r'\.setRegistryName\([^)]*\)', '/* setRegistryName removed */', content)
    content = re.sub(r'\.setUnlocalizedName\([^)]*\)', '/* setUnlocalizedName removed */', content)
    content = re.sub(r'\.setCreativeTab\([^)]*\)', '/* setCreativeTab removed */', content)
    content = re.sub(r'\.setMaxStackSize\([^)]*\)', '/* setMaxStackSize removed */', content)

    # ── maxStackSize field = int ──────────────────────────────────────────
    content = re.sub(r'\bmaxStackSize\s*=\s*\d+\s*;', '// maxStackSize removed - set in Item.Properties', content)

    # ── canRepair field ───────────────────────────────────────────────────
    content = re.sub(r'\bcanRepair\s*=\s*\w+\s*;', '// canRepair field removed', content)

    # ── damageReduceAmount / armor fields in ArmorItem ────────────────────
    # These are set in ArmorMaterial now; stub them
    content = re.sub(r'\bdamageReduceAmount\s*=\s*[^;]+;', '// damageReduceAmount removed', content)

    # ── getAttributeMap().registerAttribute() ────────────────────────────
    content = re.sub(r'getAttributeMap\(\)\.registerAttribute\([^)]+\);',
                     '// registerAttribute removed - attributes registered via AttributeSupplier',
                     content)

    # ── applyEntityAttributes() → registerAttributes() pattern ───────────
    content = re.sub(
        r'@Override\s+protected void applyEntityAttributes\(\)',
        '// applyEntityAttributes removed - use registerAttributes() static method or attribute events\n    @Override\n    protected void applyEntityAttributes()',
        content
    )

    # ── finalizeSpawn with old signature ─────────────────────────────────
    # Old: SpawnGroupData finalizeSpawn(DifficultyInstance, SpawnGroupData)
    # New: SpawnGroupData finalizeSpawn(ServerLevelAccessor, DifficultyInstance, EntitySpawnReason, SpawnGroupData)
    # Leave the override - will cause "does not override" error but that's ok for now

    # ── hurtServer abstract method stub ──────────────────────────────────
    # Entities that extend Entity (or Area subclasses) need to implement hurtServer
    # Add stub if class has @Override protected boolean hurt( but not hurtServer

    # ── defineSynchedData abstract method ────────────────────────────────
    # Some entities that call super.defineSynchedData() might already be fixed
    # If a class does NOT have defineSynchedData but extends Entity/LivingEntity,
    # we need to add it. Skip for now - will address remaining cases after first pass.

    # ── Vec3 method renames ───────────────────────────────────────────────
    content = re.sub(r'\.lengthVector\(\)', '.length()', content)
    content = re.sub(r'\.addVector\(([^)]+)\)', r'.add(\1)', content)
    content = re.sub(r'\.distanceSqToCenter\(([^,]+),\s*([^,]+),\s*([^,]+)\)',
                     r'.distToPoint(new net.minecraft.world.phys.Vec3(\1, \2, \3))', content)

    # ── AABB inflate(int) → inflate(double) ──────────────────────────────
    content = re.sub(r'\.inflate\((\d+)\)', r'.inflate((double)\1)', content)

    # ── ChunkPos.asLong() → ChunkPos.toLong() ────────────────────────────
    content = re.sub(r'\.asLong\(\)(?!\s*//)', '.toLong()', content)

    # ── rayTraceBlocks → clip ─────────────────────────────────────────────
    # complex, skip for now

    # ── setPos with teleport / setPositionAndUpdate ───────────────────────
    content = re.sub(r'setPositionAndUpdate\(([^)]+)\)', r'teleportTo(\1)', content)

    # ── getY() as double field increment ─────────────────────────────────
    # ++getY() is invalid. Leave it - it'll be an error to fix manually

    # ── GolemInteractionHelper: mcServer, FakeNetHandlerPlayServer, Connection(PacketFlow) ─
    # These are very complex server/networking changes; stub the method body if possible

    # ── getCompoundOrEmpty → getCompound ─────────────────────────────────
    content = content.replace('.getCompoundOrEmpty(', '.getCompound(')

    # ── BB.getIntOr → use contains/getInt ────────────────────────────────
    content = re.sub(r'\.getIntOr\(([^,]+),\s*([^)]+)\)', r'.getInt(\1)', content)

    # ── isFullCube() → isCollisionShapeFullBlock ─ already done above

    # ── LookControl.setLookPositionWithEntity signature ───────────────────
    # Old: setLookPositionWithEntity(entity, yawDelta, pitchDelta)
    # It should still exist but check - skip for now

    # ── getVerticalFaceSpeed() missing method ─────────────────────────────
    # This method doesn't exist on EntityThaumcraftGolem; use a constant
    content = re.sub(r'(?<!int )(?<!override\s)getVerticalFaceSpeed\(\)', '40', content)

    # ── translate/rotate on PoseStack ────────────────────────────────────
    # translate(double,double,double) → translate(float,float,float) or cast
    # Already should work as javac might auto-cast... skip

    # ── BlockTCDevice issues: Integer to boolean/Direction ────────────────
    # These are blockstate property issues - complex, skip for now

    # ── setSize() → remove (entity size is in EntityType registration) ────
    content = re.sub(r'\bsetSize\([^)]+\);', '// setSize removed - dimensions in EntityType', content)

    # ── experienceValue field → xpReward ─────────────────────────────────
    content = re.sub(r'\bexperienceValue\s*=', 'xpReward =', content)

    # ── spawnAtLocation on non-living entities ────────────────────────────
    # spawnAtLocation(ItemStack) exists on Entity; should be fine

    # ── recursive null-entity-type constructors ───────────────────────────
    # this((EntityType<? extends X>) null, worldIn) - these are wrong
    # Should just be super(entityType, worldIn) or we need to handle differently
    # For now: if the class has both EntityType constructor and Level-only constructor,
    # the Level-only should call the EntityType constructor, but we can't pass null
    # The fix: remove the Level-only constructor stub since entity types are always registered now
    # This is complex - leave for now

    # ── PacketFlow import ─────────────────────────────────────────────────
    if 'PacketFlow' in content and 'import net.minecraft.network.PacketFlow' not in content:
        content = content.replace(
            'package thaumcraft',
            'package thaumcraft'
        )
        # Try to add import after existing imports
        content = re.sub(
            r'(import net\.minecraft\.)',
            r'import net.minecraft.network.PacketFlow;\nimport net.minecraft.',
            content, count=1
        )

    # ── Fix instanceof/EntityType constructor recursive call ───────────────
    # Pattern: this((EntityType<? extends ClassName>) null, worldIn)
    # This is a common broken pattern - convert to super() call if parent is known
    # or just remove the Level-only constructor entirely as modern MC doesn't use it
    content = re.sub(
        r'this\s*\(\s*\(net\.minecraft\.world\.entity\.EntityType<\s*\?\s+extends\s+\w+>\s*\)\s*null\s*,\s*(\w+)\s*\)\s*;',
        r'// Removed broken null EntityType call; entity must be constructed via EntityType.create()',
        content
    )

    # ── FocusEffectAir: .yRot * 0.017... → .getYRot() * 0.017... ─────────
    content = re.sub(r'\.yRot\s*\*\s*0\.017453292f', '.getYRot() * 0.017453292f', content)

    # ── CasterManager: get(no args) on Optional/Reference ─────────────────
    # ChunkAccess.get("") issue in AuraChunk already handled

    # ── RayTracer: String->Thread constructor ─────────────────────────────
    if 'RayTracer.java' in path:
        content = re.sub(
            r'new Thread\s*\(\s*"([^"]+)"\s*\)',
            r'new Thread(\1)',
            content
        )
        # If it's Thread(String), it should be Thread(Runnable, String) or Thread(String)
        # In modern Java, Thread(String) doesn't exist as public constructor for arbitrary strings
        # Actually Thread has Thread(String name) constructor
        # The error is "String cannot be converted to Thread" which means someone is passing
        # a String where a Thread is expected. Let's check the context.

    # ── BrainyZombie etc: EntityType<T>.add(Holder, double, EntityType) ───
    # These are attribute builder calls with wrong signature
    # Pattern: .add(Attributes.X, value, EntityType.Y) - EntityType shouldn't be there
    content = re.sub(
        r'\.add\(([^,]+),\s*([\d.]+(?:E[-+]?\d+)?)\s*,\s*\([^)]+EntityType[^)]+\)\s*null\s*\)',
        r'.add(\1, \2)',
        content
    )

    # ── direction methods: HORIZONTALS → Plane.HORIZONTAL ─────────────────
    content = content.replace('Direction.HORIZONTALS', 'Direction.Plane.HORIZONTAL')

    # ── interactionresult cannot be instantiated (InteractionResult is enum now) ─
    # InteractionResult.SUCCESS etc should be fine; if someone does `new InteractionResult()` fix it

    return content

def process_files():
    changed = 0
    for root, dirs, files in os.walk(SRC):
        dirs[:] = [d for d in dirs if d not in ['.git', 'build', '.gradle']]
        for fn in files:
            if not fn.endswith('.java'):
                continue
            path = os.path.join(root, fn)
            with open(path, 'r', encoding='utf-8', errors='replace') as f:
                content = f.read()
            new_content = fix_file(path, content)
            if new_content != content:
                with open(path, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                changed += 1
                print(f"Fixed: {path[len(SRC)+1:]}")
    print(f"\nTotal files changed: {changed}")

if __name__ == '__main__':
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    process_files()
