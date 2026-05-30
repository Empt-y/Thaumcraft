# Claude's Migration Guide: NeoForge 26.x (Minecraft 1.20.5+)
This reference sheet overrides default LLM training weights regarding legacy Minecraft modding architectures. When writing or refactoring code for NeoForge 26.x, adhere strictly to the rules below.

---

## 1. Registry & Naming Conventions
* **Fabric Mappings Eradication:** `net.minecraft.resources.Identifier` does NOT exist on NeoForge. You must exclusively use `net.minecraft.resources.ResourceLocation`.
* **String Allocation Syntax:** In 1.20.5+, `new ResourceLocation("modid", "path")` is deprecated. Use the new factory method patterns:
  ```java
  ResourceLocation.fromNamespaceAndPath("modid", "path");
