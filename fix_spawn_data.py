#!/usr/bin/env python3
"""Fix dangling writeSpawnData/readSpawnData code left by batch12."""
import os, re

SRC = "/home/ash/geostrata/Thaumcraft/src/main/java"
changed = 0

def fix_file(path, content):
    c = content

    # Remove dangling body code after /* writeSpawnData removed */
    # The pattern is: /* writeSpawnData removed */ followed by stray lines ending with }
    # These look like:
    #   /* writeSpawnData removed */
    #       data.writeInt(id);
    #       ...
    #   }
    # We need to also remove those trailing lines up to the next } that closes the comment-stub

    # Strategy: find /* writeSpawnData removed */ and remove it plus any following lines
    # until we hit a blank line or a method/field declaration

    # More specifically: remove everything from /* writeSpawnData removed */ through
    # the closing } that would have ended the method body

    # Pattern: /* writeSpawnData removed */ (possibly with more content) then lines of code then }
    c = re.sub(
        r'\s*/\* writeSpawnData removed \*/[^\n]*\n(?:[ \t]+[^\n]*\n)*?[ \t]+\}',
        '',
        c
    )
    c = re.sub(
        r'\s*/\* readSpawnData removed \*/[^\n]*\n(?:[ \t]+[^\n]*\n)*?[ \t]+\}',
        '',
        c
    )

    # Simpler fallback: if the pattern above fails, just remove the comment line
    # and scan for orphaned code
    # The general pattern seen:
    # /* writeSpawnData removed */
    #         code line 1;
    #         code line 2;
    #     }
    # OR:
    # /* readSpawnData removed */
    #         try { ... } catch(Exception e){}
    #         code;
    #     }

    # Let's do a line-by-line approach
    lines = c.split('\n')
    result = []
    skip_until_close = False
    brace_depth = 0

    i = 0
    while i < len(lines):
        line = lines[i]
        stripped = line.strip()

        if '/* writeSpawnData removed */' in stripped or '/* readSpawnData removed */' in stripped:
            # Start skipping - find matching close brace
            # Count open/close braces in remaining lines
            skip_until_close = True
            brace_depth = 0
            i += 1
            continue

        if skip_until_close:
            # Count braces
            brace_depth += stripped.count('{') - stripped.count('}')
            if brace_depth < 0:
                # We hit the closing brace
                skip_until_close = False
                brace_depth = 0
                i += 1
                continue
            i += 1
            continue

        result.append(line)
        i += 1

    return '\n'.join(result)


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
                print(f"  FIXED: {path[len(root)+1:]}")

process_dir(SRC)
print(f"\nTotal files changed: {changed}")
