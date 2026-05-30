#!/usr/bin/env python3
"""Remove orphaned catch/close blocks that misalign class braces."""
import os, re

SRC = "/home/ash/geostrata/Thaumcraft/src/main/java"
changed = 0

def fix_file(path, content):
    c = content

    # Remove orphaned: catch (Exception ex) {}\n    }
    # that appears immediately after a correctly-closed method
    # Pattern: }  (closing a method)
    #              catch (Exception ex) {}  (orphaned catch)
    #          }  (spurious class close)
    c = re.sub(
        r'(\n    \})\n        catch \(Exception ex\) \{\}\n    \}',
        r'\1',
        c
    )
    # Also handle: catch ... \n    red = data.readBoolean();\n    }
    c = re.sub(
        r'\n        catch \(Exception ex\) \{\}\n        red = data\.readBoolean\(\);\n    \}',
        '',
        c
    )

    # Remove double }} that close a class early (when a method already ends with })
    # Pattern seen: method body ends with }, then another } closes class, but there are
    # more methods after.
    # We can't safely fix this generically; handle the specific patterns:

    # Pattern: "    }\n    }\n    \n    public" → "    }\n    \n    public"
    # but ONLY when the double }} is incorrect
    # Actually this is risky - some double }} are correct (nested class, etc.)
    # Let's just do the specific files

    return c


def process_file_homingshard():
    path = os.path.join(SRC, 'thaumcraft/common/entities/projectile/EntityHomingShard.java')
    with open(path) as f: c = f.read()
    # Remove orphaned catch after getGravityVelocity
    c = c.replace(
        '    protected float getGravityVelocity() {\n        return 0.0f;\n    }\n        catch (Exception ex) {}\n    }',
        '    protected float getGravityVelocity() {\n        return 0.0f;\n    }'
    )
    with open(path, 'w') as f: f.write(c)
    print(f"  FIXED: EntityHomingShard.java")

def process_file_riftblast():
    path = os.path.join(SRC, 'thaumcraft/common/entities/projectile/EntityRiftBlast.java')
    with open(path) as f: c = f.read()
    c = c.replace(
        '    protected float getGravityVelocity() {\n        return 0.0f;\n    }\n        catch (Exception ex) {}\n        red = data.readBoolean();\n    }',
        '    protected float getGravityVelocity() {\n        return 0.0f;\n    }'
    )
    with open(path, 'w') as f: f.write(c)
    print(f"  FIXED: EntityRiftBlast.java")

def process_file_grapple():
    path = os.path.join(SRC, 'thaumcraft/common/entities/projectile/EntityGrapple.java')
    with open(path) as f: c = f.read()
    c = c.replace(
        '        // FIXME: setSize removed; dimensions set in EntityType builder\n    }\n        catch (Exception ex) {}\n    }',
        '        // FIXME: setSize removed; dimensions set in EntityType builder\n    }'
    )
    with open(path, 'w') as f: f.write(c)
    print(f"  FIXED: EntityGrapple.java")

def process_file_focuscloud():
    path = os.path.join(SRC, 'thaumcraft/common/entities/projectile/EntityFocusCloud.java')
    with open(path) as f: c = f.read()
    c = c.replace(
        '        return (float) getDataManager().get((EntityDataAccessor)EntityFocusCloud.RADIUS);\n    }\n    }\n    \n    public void addAdditionalSaveData',
        '        return (float) getDataManager().get((EntityDataAccessor)EntityFocusCloud.RADIUS);\n    }\n\n    public void addAdditionalSaveData'
    )
    with open(path, 'w') as f: f.write(c)
    print(f"  FIXED: EntityFocusCloud.java")

def process_file_focusmine():
    path = os.path.join(SRC, 'thaumcraft/common/entities/projectile/EntityFocusMine.java')
    with open(path) as f: c = f.read()
    c = c.replace(
        '    protected float getGravityVelocity() {\n        return 0.01f;\n    }\n    }\n    \n    public void addAdditionalSaveData',
        '    protected float getGravityVelocity() {\n        return 0.01f;\n    }\n\n    public void addAdditionalSaveData'
    )
    with open(path, 'w') as f: f.write(c)
    print(f"  FIXED: EntityFocusMine.java")

process_file_homingshard()
process_file_riftblast()
process_file_grapple()
process_file_focuscloud()
process_file_focusmine()
print("Done")
