# Foundation v2

One shared project containing Galacticraft Core, Galacticraft Planets, GalaxySpace, and embedded Asmodeus Core
All implementation sources share `src/main/kotlin` and assets share `src/main/resources`

## Minecraft 26.3 migration in progress

Targeted Minecraft 26.3 with Forge 66.1.5, ForgeGradle 7.0.17, Gradle 9.7.1, Kotlin 2.4.21, and Java 25
Updated the build toolchain and loader metadata
The gameplay sources still require porting from the 1.12.2 Minecraft and Forge APIs
**This checkout is not a working Minecraft 26.3 mod yet**

The earlier successful 1.12.2 compilation documented in `docs/kotlin-migration.md` applies to the previous build only
See `docs/forge-26.3-migration.md` for the current work and remaining requirements

## Build

Requires Java 25 and Python 3.11 or newer for release verification

```sh
./tools/build-external.sh
```

The script stages the project and keeps generated sources, classes, runs, and jars outside the repository under `~/.cache/foundation-v2/forge-26.3`
Set `FOUNDATION_CACHE_ROOT` to an external directory to override this location
All integrated sources remain in compilation so unported features cannot silently disappear from a release
Compilation must succeed before the release verifier can accept an archive
There are no separate Galacticraft or GalaxySpace builds

Removed the obsolete 1.12 dependency jars from `libs`
Their checksums were recorded in `docs/retained-dependencies.json`
Upstream history, licenses, and notices were retained in Git and `docs/upstream`
