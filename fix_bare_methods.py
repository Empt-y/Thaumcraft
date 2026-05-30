#!/usr/bin/env python3
import glob, re, sys

files = glob.glob("src/main/java/**/*.java", recursive=True)
changed = 0
for path in files:
    with open(path) as f:
        content = f.read()
    original = content

    # Remove bare calls to old registration/builder methods (no leading dot)
    content = re.sub(r'\n\s*setMaxStackSize\([^)]*\);', '', content)
    content = re.sub(r'\n\s*setCreativeTab\([^)]*\);', '', content)
    content = re.sub(r'\n\s*setUnlocalizedName\([^)]*\);', '', content)
    content = re.sub(r'\n\s*setRegistryName\([^)]*\);', '', content)
    content = re.sub(r'\n\s*setHasSubtypes\([^)]*\);', '', content)

    # Fix: x has private access in Vec3i — use getX/getY/getZ
    content = re.sub(r'(\bBlockPos\b[^.]*)\b\.x\b', r'\1.getX()', content)
    content = re.sub(r'(\bBlockPos\b[^.]*)\b\.z\b', r'\1.getZ()', content)
    content = re.sub(r'(\bBlockPos\b[^.]*)\b\.y\b', r'\1.getY()', content)
    # Direct field access on pos variable
    content = re.sub(r'\b(pos|sealPos|blockPos|worldPosition)\b\.x\b(?!\w)', r'\1.getX()', content)
    content = re.sub(r'\b(pos|sealPos|blockPos|worldPosition)\b\.z\b(?!\w)', r'\1.getZ()', content)
    content = re.sub(r'\b(pos|sealPos|blockPos|worldPosition)\b\.y\b(?!\w)', r'\1.getY()', content)

    # distanceSq → distSqr
    content = content.replace('.distanceSq(', '.distSqr(')

    # getBlock() on BlockState → state.getBlock() (this should work in modern MC)
    # but if called on BlockPos: BlockPos doesn't have getBlock()
    # This is complex - skip

    if content != original:
        with open(path, 'w') as f:
            f.write(content)
        changed += 1
print(f"Fixed {changed} files")
