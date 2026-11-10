# Foundation v2

Minecraft 1.12.2 monorepo combining Galacticraft Core, Galacticraft Planets, and GalaxySpace

Repository: https://github.com/TopScrech/foundation-v2

- `galacticraft/` contains Galacticraft Core and Galacticraft Planets, based on micdoodle8/Galacticraft
- `galaxyspace/` contains GalaxySpace, based on BlesseNtumble/GalaxySpace, with the used Asmodeus Core code embedded under `src/embeddedCore`

Both upstream histories were preserved along with the Foundation v2 changes
Both modules are ordinary tracked directories in one repository, with no Git submodules

BuildCraft, IndustrialCraft 2, Mekanism, and Biomes O’ Plenty integrations were removed where present
See each module’s `docs/removed-integrations.md` for compatibility and validation details

## Builds

The modules retain their individual Gradle builds because Galacticraft uses ForgeGradle 2.3 and GalaxySpace uses ForgeGradle 4

Build GalaxySpace from the repository root with `./galaxyspace/tools/build-external.sh`
This stages the build externally and keeps artifacts out of the repository
See `galaxyspace/docs/embedded-core.md` for build instructions and embedded Core provenance

The Galacticraft checkout’s original build references an upstream `dependencies` directory that was absent from the source repository
Its validation setup supplied MicdoodleCore classes and access-transformer rules externally, as documented in `galacticraft/docs/removed-integrations.md`

No Java sources were translated to Kotlin during the consolidation
