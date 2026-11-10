# Removed integrations

Removed BuildCraft, IndustrialCraft 2, Mekanism, and Biomes O’ Plenty integration from Foundation v2

- Removed API dependencies and compatibility detection
- Removed energy, battery, gas, wrench, recipe, fuel, paint, and biome hooks for those mods where present
- Removed the tracked BuildCraft, IndustrialCraft 2, and Mekanism API jars from GalaxySpace
- Preserved Galacticraft energy, Forge Energy, Redstone Flux, and unrelated integrations
- Preserved enclosed block metadata IDs and blockstate names so existing variants were not reassigned
- Preserved the legacy `ic2compat` aluminum dust registry ID for remaining AE2, Thermal Expansion, Ender IO, and Actually Additions recipes, with IndustrialCraft processing variants disabled
- Preserved reserved energy configuration packet slots and ignored their removed conversion values
- Removed the default IndustrialCraft quantum armor entries from GalaxySpace’s configurable radiation armor list

Existing enclosed BuildCraft pipes and IndustrialCraft cables became inert legacy blocks without their former tile entities

Asmodeus Core remained embedded in GalaxySpace and no upstream Java was translated to Kotlin

## Validation

Built GalaxySpace without the three removed API jars and checked that the compiled classes contained no references to the removed API packages

Compiled Galacticraft in an external workspace using Gradle 4.10.3, ForgeGradle 2.3, and Java 8
The original checkout lacked its referenced `dependencies` directory, so validation supplied existing MicdoodleCore classes from the upstream Galacticraft jar and access-transformer rules externally
Validation also used Maven Central and the current Forge Maven URL in the external workspace
No build artifacts were saved in either repository

Game startup, world migration, and gameplay were not tested
