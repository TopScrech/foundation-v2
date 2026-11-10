# Embedded Asmodeus Core

Added the Core runtime used by GalaxySpace to `src/embeddedCore` and removed `libs/AsmodeusCore-1.12.2-1.0.4-dev.jar` from the build and repository

Retained upstream Java as requested, without translating it to Kotlin

## Selection and provenance

The source was the exact Asmodeus Core 1.0.4 development archive previously committed by upstream GalaxySpace, identified by SHA-256 in `embedded-core-inventory.json`

Traced GalaxySpace references plus the Core mod lifecycle, its client proxy, registry subscriber, and asteroid mixin, then followed class constant-pool references and nested source units transitively

Retained 204 of 226 class entries, represented by 151 Java source units, and excluded the 22 class entries listed in the inventory

Kept complete reachable classes, including their inherited and lifecycle behavior, rather than removing individual methods

Recovered 122 source units directly from the archive and 29 missing source units from its bytecode using Vineflower 1.10.1

Removed an unused StarMaker import from `AsmodeusClientEvent`, removed two inaccessible inherited enum imports from recovered celestial GUI classes, and removed a decompiler-added invalid override annotation from `Page_WithCraftMatrix`

Retained the archive's 49 unique resource entries because the reachable sky and GUI code constructs resource names dynamically, including galaxy and star-color textures

Retained the embedded `asmodeuscore` mod ID, registry names, configuration location, lifecycle ordering, capabilities, networking, and asset namespace for compatibility

GalaxySpace's existing `required-after:asmodeuscore` declaration orders the two mod containers inside the same distribution, without requiring a separate jar

Included the asteroid mixin and its configuration in the distribution, sharing the generated GalaxySpace refmap with the GalaxySpace mixins

## Building

Requires Java 8 and uses the existing Gradle wrapper

Run from this repository

```sh
./tools/build-external.sh
```

The wrapper stages sources outside the project before invoking ForgeGradle, whose internal cache also writes relative to the staged checkout

Build artifacts are under `~/.cache/foundation-v2/galaxyspace/build/libs`

The default tasks clean previous artifacts and produce `GalaxySpace-1.12.2.jar` plus `GalaxySpace-1.12.2-dev.jar`

Verify the release artifact

```sh
python3 tools/verify_embedded_core.py ~/.cache/foundation-v2/galaxyspace/build/libs/GalaxySpace-1.12.2.jar
```

The inventory can be reproduced against the original archive if available externally

```sh
python3 tools/core_inventory.py /outside/project/AsmodeusCore-1.12.2-1.0.4-dev.jar
```

Do not install a separate Asmodeus Core jar alongside this GalaxySpace build because the mod container is already embedded

## Validation

Passed the original upstream GalaxySpace compile as a baseline

Passed the integrated Java compile and release and development packaging

Verified the packaged class closure, excluded classes, resources, mixin configurations and refmap, mod metadata, access transformer, and absence of nested or leftover Core jars

Matched all selected Core public class, method, and field signatures against the original dev jar using `javap -public`

Game startup and gameplay have not been validated
