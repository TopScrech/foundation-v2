# Kotlin migration

Converted 1,129 Galacticraft, 730 GalaxySpace, and 151 embedded Asmodeus Core implementation source files to Kotlin
Retained 20 `package-info.java` files for Forge package annotations

Merged all implementations into `src/main/kotlin` and all resources into `src/main/resources`
Replaced the separate builds with one Gradle 6.8.3, ForgeGradle 4.0.23, Kotlin 1.9.24 build targeting Java 8
Removed the option to compile GalaxySpace against the bundled Galacticraft implementation

The standard build compiles every component and packages one Foundation jar
It includes Kotlin, Mixin, the used embedded Core runtime, and the MicdoodleCore classes omitted from the original source checkout
Only those omitted MicdoodleCore classes are read from the upstream Galacticraft archive, through a filtered dependency generated outside the repository
Retained dependency inputs and hashes were recorded in `retained-dependencies.json`

```sh
./tools/build-external.sh
```

Build artifacts, dependency extraction, compiler outputs, and staged workspaces are outside the repository

## Validation

Passed the unified Kotlin compilation and full Gradle build with zero errors
Passed Mixin annotation processing, refmap generation, reobfuscation, and release packaging
Passed the embedded Core inventory check for 190 named classes, 49 resources, both mixin configurations, and metadata
Passed the unified artifact check for all four mod containers, embedded Kotlin and Mixin runtimes, loader configuration, and Java 8 bytecode

Added one Kotlin Forge loading plugin that initialized both Mixin and MicdoodleCore
Removed the manifest TweakClass entry because Forge 1.12 skips FMLCorePlugin discovery when that entry is present
Excluded Mixin's unused Java 16 ModLauncher bindings from the Minecraft 1.12 LaunchWrapper distribution

Saved the single release artifact as `Foundation-2.0.0.jar` in Downloads after packaging validation
Set the primary jar metadata and Forge display name to Foundation 2.0.0 while retaining the legacy registry IDs and saved-world keys
Game startup and gameplay have not been tested
