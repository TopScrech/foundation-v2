# Foundation v2

One Minecraft 1.12.2 project containing Galacticraft Core, Galacticraft Planets, GalaxySpace, and embedded Asmodeus Core

All implementation sources share `src/main/kotlin` and all assets share `src/main/resources`
The existing mod IDs and asset namespaces were retained for compatibility with worlds and other mods

## Build

Requires Java 8

```sh
./tools/build-external.sh
```

This compiles the whole project and produces one reobfuscated `Foundation-2.0.0.jar`
The wrapper stages the project and keeps Gradle caches, generated sources, classes, and jars outside the repository under `~/.cache/foundation-v2`
There are no separate Galacticraft or GalaxySpace builds

Kotlin 1.9.24 and the Mixin runtime were included in the jar
The old upstream Galacticraft archive in `libs` supplies only the MicdoodleCore classes omitted from its source checkout
Its Galacticraft implementation classes are excluded from compilation and packaging

## Migration status

Converted all 2,010 original implementation sources to Kotlin, including all 151 embedded Asmodeus Core sources
Retained 20 Java package annotation files for Forge API metadata

The unified project passed compilation, reobfuscation, and release packaging checks with zero errors
The release verifier checked all four mod containers, embedded Core contents, loader configuration, refmaps, and Java 8 bytecode
Game startup and gameplay have not been tested
See `docs/kotlin-migration.md` for details

Upstream history and notices were retained in Git and `docs/upstream`
The embedded Core provenance and inventory are in `docs/galaxyspace`
