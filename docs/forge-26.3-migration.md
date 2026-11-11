# Forge 26.3 migration

## Current status

The migration is incomplete and this checkout cannot produce a working 26.3 release
The full shared source tree was compiled against the new target without excluding legacy implementations
Latest compilation failed with 59,667 diagnostics across 1,600 Kotlin files
The first reported blocker is an unported API in `asmodeuscore/api/space/IBookPage.kt`
Many subsequent diagnostics were cascading errors from missing legacy types

## Completed build changes

- Targeted Minecraft 26.3 and Forge 66.1.5
- Replaced ForgeGradle 4 with ForgeGradle 7.0.17
- Updated the Gradle wrapper to the official MDK's Gradle 9.7.1 wrapper
- Updated Kotlin to 2.4.21 and targeted Java 25
- Replaced MCP/SRG and reobfuscation tasks with the modern official-name dependency pipeline
- Added modern `mods.toml` entries for all four retained mod IDs
- Validated resource format 97.1 and data format 121.0
- Removed old LaunchWrapper manifest attributes and runtime extraction from the build
- Removed all nine obsolete dependency jars from `libs` and recorded their checksums
- Kept build, generated source, and run directories outside the project
- Replaced the Java 8 release checks with modern metadata, embedded Core inventory, bytecode, and legacy loader checks

Removing legacy loader configuration from the build did not replace its behavior
The old `FoundationLoadingPlugin` source remains unported and is explicitly rejected by the release verifier
The original Mixin configurations and access transformer rules also still require porting

## Completed source changes

Updated core Minecraft type imports to the verified 26.3 classes across the shared source tree
Retained Kotlin import aliases where callers still use the former type names
Replaced client-only annotations with `OnlyIn(Dist.CLIENT)` and kept logical-side handling separate
Updated straightforward identifier construction to `Identifier.parse` and `Identifier.fromNamespaceAndPath`
Updated default block-state access and direction enumeration and coordinate methods
These changes did not complete the lifecycle, block and item state, persistence, dimension, or rendering ports

## Validation

The external build selected the installed Java 25 runtime and successfully configured ForgeGradle and Kotlin
ForgeGradle downloaded and prepared the Forge 26.3 official-name dependencies
`compileKotlin` then failed on the legacy source APIs
No migrated release jar was produced

The shell script passed syntax validation
The metadata parsed successfully with Python 3.12 and declared all four containers
The build script rejected an attempted cache location inside the project
These checks do not establish runtime compatibility

## Verified shared migration units

The focused `verifyPortedApi` task compiled 267 implementation files against Forge 26.3 and passed the accompanying runtime checks
This task uses its own source set and leaves the entire main release source set intact
The full mod build still failed and startup has not been verified

- Ported integer and floating coordinates, dynamic height checks, unloaded chunk queries, and ray tracing to the current level APIs
- Ported dimension coordinate persistence to named resource keys with explicit legacy numeric-ID aliases
- Replaced custom gravity, oxygen, world generation, and schematic event classes with modern event buses
- Ported network and power interfaces and replaced recursive network traversal with an iterative search
- Verified a 20,000-node cyclic network search and early target termination
- Ported energy NBT persistence and verified simulation, capacity, and invalid-input behavior
- Ported public item, inventory, and schematic recipe interfaces
- Replaced the removed Forge configuration classes with Kotlin persistence supporting existing category, scalar, and list files
- Compiled the GalaxySpace configuration modules and verified preservation of a customized dimension ID
- Verified Mars, Asteroids, and Venus configuration merging, including persistence before removal and precedence of current settings
- Wired the four mod constructors to a shared modern lifecycle helper and declared internal container ordering
- Ported block-state property types, enum serialization, and state mutation calls
- Ported the bright-air block to current block properties with an ID assigned before construction

The per-file verification list and latest compiler counts are in `docs/forge-26.3-progress.json`

## Remaining source port

Port these systems together in the existing shared source tree
Preserve behavior, registry namespaces, and content coverage throughout the port

1. Replace the four legacy mod entrypoints, `@Mod.EventHandler` methods, proxies, configuration setup, and event subscriptions with Forge 66 lifecycle and event bus registration
2. Replace the LaunchWrapper and MicdoodleCore transformations with supported modern hooks and port the integrated Asmodeus Core and GalaxySpace Mixins
3. Replace legacy item and block registration, metadata variants, numeric IDs, creative tabs, and tile entities with modern registries, block states, items, and block entities
4. Port inventories, menus, recipes, capabilities, energy and oxygen networks, persistence, and networking
5. Replace numeric dimensions and WorldProvider implementations with dimension keys, dimension types, biome sources, and modern chunk generation while retaining celestial navigation and station behavior
6. Port rocket and other entity registrations, movement, server travel, player hooks, and world save handling
7. Replace fixed-function OpenGL, Tessellator, model loaders, renderers, GUI classes, and input hooks with the current client rendering and screen APIs
8. Port assets and data, including block models, recipes, loot, tags, translations, and dimension definitions
9. Replace or port optional integrations against compatible 26.3 APIs without reintroducing 1.12 dependency jars
10. Pass full compilation, packaging verification, dedicated server startup, client startup, and representative gameplay checks covering oxygen, machines, rockets, travel, dimensions, and stations

World compatibility requires deliberate save migration and gameplay testing
Retaining the old namespaces alone does not establish compatibility

## References

- [Official Forge downloads](https://files.minecraftforge.net/net/minecraftforge/forge/)
- [Forge 26.3 MDK](https://maven.minecraftforge.net/net/minecraftforge/forge/26.3-66.1.5/forge-26.3-66.1.5-mdk.zip)
- [Kotlin Gradle configuration](https://kotlinlang.org/docs/gradle-configure-project.html)

## Latest runtime checks

- Validated both technical biome definitions with the native biome codec
- Verified optional biome classifications and supported pack formats
- Registered all three migrated air blocks through the Forge registry and verified lighting, thermal states, collision, replacement, and explosion resistance

These isolated checks passed, while full Forge mod startup and release export remained pending

## Persistence migration checkpoint

- Ported dust and frozen storm data to codec-backed saved data and per-level client mirrors
- Preserved storm timers, strengths, legacy file imports, and precedence of current saves
- Verified native storage reloads, dimension isolation, and preservation of malformed files
- Updated primitive NBT reads and writes throughout the shared source tree
- Preserved typed presence checks, numeric wildcard matching, and homogeneous-list validation
- Verified inventory record reads and rejection of mixed-list input without modifying the original data
- Required the replacement biome resources and optional classification packs in release verification

The focused checks passed for 100 implementation files
The full shared compiler still reported 73,791 diagnostics across 1,728 files
Full Forge startup and the Downloads jar export remained pending

## Shared save and level API checkpoint

- Ported asteroid and Kuiper Belt records to codec-backed server-wide saved data
- Retained imports from the legacy galacticraft directory beside data and kept original files
- Replaced station spin's changing dimension pointer with independent entries keyed by named dimensions
- Preserved numeric station entries, unloaded stations, and unknown saved fields
- Ported meteor-storm data and corrected the end-of-storm timer so later storms could start
- Limited meteor spawning to the dimension whose storm was active
- Updated 2,326 direct level API calls across 289 files
- Updated 1,636 compiler-identified position and block-entity calls across 155 files
- Updated another 611 compiler-identified level calls across 251 files
- Preserved fractional position translation using the legacy floor and zero-offset semantics
- Moved tracked runtime logs to the external build cache and ignored future project logs
- Stopped the isolated bootstrap's background executors after its native save checks completed

Native storage checks passed for asteroid, Kuiper Belt, weather, and station-spin records
Station checks verified that saving one dimension retained other dimensions and legacy records
The focused source set compiled 136 implementation files and passed its runtime checks
The full compiler still reported 69,564 diagnostics across 1,721 files
These checks did not establish full mod startup, meteor entity behavior, or release readiness

## Terrain and entity API checkpoint

- Preserved the legacy float trigonometric lookup arithmetic used by existing terrain algorithms
- Matched the cached Forge 1.12.2 implementation across 10,000 sampled angles
- Verified terrain cache results around negative coordinates and region boundaries
- Verified repeatable WorldEngine height samples under different query orders
- Compiled the Galacticraft and WorldEngine noise implementation modules against the new target
- Updated 1,626 compiler-identified entity accesses across 299 files
- Ported level reads, held items, riding state, position and velocity reads, rotation, counters, and messages
- Deferred direct position and velocity writes for migration through current movement APIs

The full compiler reported 68,368 diagnostics across 1,718 files
Full terrain generation, entity behavior, mod startup, and release export remained unverified
Legacy block-state metadata and item variants still required explicit migration to current state and component APIs

## First native item variant checkpoint

- Added a persistent, network-synchronized legacy metadata component separate from custom NBT and durability
- Installed its modern registration callback on the Core mod bus
- Ported the lunar material item to explicit variant definitions with retained names, categories, and blue formatting
- Ported the shared description item base to native tooltip components and input access
- Converted 23 lunar material stack construction sites across recipes, trades, and salvage paths
- Added a native component-based item model selector for all three lunar materials
- Converted 765 English Core language entries to the native JSON resource format
- Verified save and network round trips, independent copies, count preservation, invalid variant rejection, and durability/custom NBT isolation
- Decoded the selector through the native item-model codec and verified the selected variant models
- Verified that directly loaded metadata selected the lunar sapphire name and model

The native test harness ran the actual component initializer phase before constructing stacks
Production initialization and recipe registration still required migration to the appropriate registry and data-loading stages
The retained lunar smelting experience declaration still required integration with modern recipe data
Creative tab integration, the other legacy variants, full client rendering, full Forge startup, and jar export remained pending
The focused checks passed for 132 implementation files
The full compiler still reported 68,291 diagnostics across 1,716 files

## Block metadata checkpoint

- Retained explicit encoders and decoders through a shared contract in 121 custom block classes
- Restored thermal air metadata conversion through the same contract
- Converted vanilla states using the native flattening table and subsequent Minecraft data fixes
- Preserved distinct block identities for stone, wood, color, and slab variants
- Verified 1,679 canonical vanilla state round trips from a table of 1,695 entries
- Guarded 12 skull states requiring block entity data and four upper plant states requiring lower-plant context
- Added explicit upper-plant construction preserving the lower plant's block type
- Verified door half-specific metadata and ignored computed stair shapes and fence connections during encoding
- Verified all 16 metadata values across seven native color collections
- Matched all 254 original vanilla field IDs against the old Blocks declarations and the native migration table
- Converted 23 direct vanilla metadata calls to explicit original block IDs
- Added a reproducible snapshot generator and passed its consistency check

These API contracts did not complete the 121 block implementations
Their constructors, state definitions, registration, gameplay hooks, drops, and associated item stacks still required migration
Ambiguous vanilla families required explicit IDs instead of a guessed conversion
Flattened vanilla identity comparisons and contextual block entity and plant placement paths still required audit
The focused checks compiled 136 implementation files and passed
The full shared compiler still reported 66,287 diagnostics across 1,715 files
No complete Forge startup or working release jar was produced

## Equipment and crafting inventory checkpoint

- Migrated extended equipment storage to the native Container API and retained all 11 slots
- Added registry-aware item stack persistence and native data fixes for old item IDs, enchantments, and vanilla variants
- Preserved custom item variants, durability, custom NBT, and legacy Forge capability data through native components
- Preserved unknown inventory slot records and blocked saving after a failed known-slot import
- Retained legacy equipment slot indices without copying normal player inventory slots
- Migrated Core and GalaxySpace 3 by 3 grids and the 2 by 8 rocket assembly grid to the native CraftingContainer API
- Verified menu callbacks, item splitting, no-update operations, grid clearing, and positioned crafting input

Both focused verification tasks passed
The focused compilation checked 143 implementation files out of 2048
These counts represented focused API coverage, not completed gameplay files
The full compilation still failed with 66,186 diagnostics across 1,711 files
Player capability lifecycle, recipe registration, full menu behavior, Forge startup, and the release jar remained unverified
No jar was exported

## Schematic inventory and recipe checkpoint

- Migrated 14 schematic inventories to native Container storage, preserving rocket, buggy, astro miner, and GalaxySpace part slot counts
- Connected storage changes to native menu callbacks and retained explicit no-update removal behavior
- Verified component-preserving splits, recipe snapshots, per-item stack limits, detached inventories, and native Slot access
- Updated 442 compiler-confirmed native Slot calls and fields across 71 files
- Migrated native slot overrides and shared crafting menu callbacks
- Updated all 13 NASA ingredient slot validators to distinguish native item variants and propagate changes
- Migrated slot-addressed NASA recipe matching and protected stored recipe ingredients and output stacks from caller mutation
- Migrated circuit fabricator recipes with alternative ingredients, optional empty slots, replacement, removal, and valid-slot cache rebuilding
- Retained the isolated legacy NoPlacing custom-data exception during circuit recipe matching
- Rejected invalid recipe alternatives before modifying the registry

Both focused verification tasks passed for 161 implementation files out of 2050
The complete compiler still failed with 65,503 diagnostics across 1,692 files
No diagnostics remained with the exact native Slot receiver type in this compiler run
Recipe registration, recipe data loading, custom item families, menu synchronization, spark packets, and full gameplay still required migration and verification
The complete Forge startup and working release jar remained unverified
No jar was exported

## Native inventory and menu contract checkpoint

- Replaced shared inventory defaults with native Container and Nameable contracts
- Replaced the empty shared clear method with storage clearing through each implementation's setter
- Migrated 477 inventory declarations and their own dispatch calls across 79 files
- Migrated 50 sided inventory imports and aligned 76 native sided nullability signatures across 55 files
- Preserved concrete entity and block entity update methods through native setChanged overrides
- Rewrote shared GalaxySpace machine storage with explicit block entity ownership and native sided access
- Migrated 49 machine and entity names to native text components
- Migrated 56 native stack split and take operations across 28 files
- Updated 175 compiler-confirmed inventory accesses across 70 files
- Migrated native player inventory imports and text component type references across 161 files
- Added fixed slot views over native main, armor, and offhand storage and updated 110 compiler-confirmed player inventory accesses across 42 files
- Verified slot ordering and clearing boundaries without touching native body armor and saddle slots
- Migrated 192 native menu overrides and 767 own calls across 69 files
- Removed 26 obsolete empty inventory hooks and fixed four empty clear implementations
- Replaced combined treasure storage with the native compound container and retained custom-name precedence
- Prevented a missing or repeated chest half from being exposed twice
- Caught and fixed Java default-method forwarding through Kotlin container delegation
- Verified combined chest opening, closing, stack limits, and insertion policies
- Added dirty notifications to electric machine, modification table, treasure chest, and crashed probe clearing

Both focused verification tasks passed for 165 implementation files out of 2051
The complete compilation still failed with 63,384 diagnostics across 1,685 files
Native fixtures verified shared contract behavior and player slot mappings, not complete production machine and player lifecycles
Menu construction, registration, synchronization, recipe consumption, machine persistence, entity lifecycle, and full gameplay still required migration and verification
The complete Forge startup and working release jar remained unverified
No jar was exported

## Crafting consumption and rocket material checkpoint

- Migrated the NASA result slot to the native onTake callback and Forge crafting remainder hooks
- Captured all remainder stacks before removing ingredients, retaining the last ingredient's container
- Preserved partial inventory insertion and returned remaining containers to an emptied input slot or the player drop path
- Preserved full item components on returned stacks and protected all inputs when a remainder hook failed
- Restored the previous Forge crafting player context after completion or failure
- Migrated the memory crafting slot to the native ResultSlot superclass
- Migrated 10 asteroid materials, 20 rocket parts, and 11 rocket modules to declared component variants
- Preserved native names, rarity, tier tooltips, sorting categories, and historical model mappings
- Retained asteroid smelting experience declarations for the later native recipe data migration
- Verified all 41 new variants through legacy import, native save, and network round trips
- Decoded native item model selectors and verified all 41 selected model and texture references
- Converted 383 Planets and 602 GalaxySpace English entries to native JSON language resources
- Converted explicit stack constructions for the declared families and 32 shared assembly recipe input constructions
- Migrated rocket assembly slot restrictions for parts, modules, chests, and reserved slots
- Rejected undeclared part metadata instead of treating it as a supported tier

Both focused verification tasks passed for 171 implementation files out of 2053
The complete compiler still failed with 62,989 diagnostics across 1,677 files
The native fixtures exercised shared crafting consumption and slot policies, not complete player/menu gameplay
Production registry initialization, creative tabs, complete recipe registration, native smelting data, menu synchronization, model baking, and full client rendering still required migration and verification
The complete Forge startup and working release jar remained unverified
No jar was exported

## Metal families and native ingredient checkpoint

- Migrated 3 ingots, 3 nuggets, 5 compressed plates, and 3 heavy-duty plates to native component variants
- Preserved item names, categories, tier descriptions, and the existing models
- Shared native material name and creative stack handling across the passive material families
- Verified directly loaded metadata and retained explicit item name overrides
- Verified all 55 material variants through legacy imports, native saves, network packets, tooltip generation, and model selectors
- Verified referenced textures for all selected material models
- Converted 83 executable metal stack construction calls across 15 files
- Added a Forge stack ingredient serializer preserving item identity, variants, and durability while allowing unrelated custom data
- Registered the ingredient serializer through the Core container's native RegisterEvent handler
- Verified ingredient JSON and network dispatch through the native Forge serializer registry
- Kept custom ingredient holder views distinct from unrestricted vanilla item predicates during pattern symmetry checks
- Verified mirrored native shaped patterns containing mixed unrestricted and variant-specific predicates
- Added six native recipe data files for ingot and nugget conversion across cobalt, magnesium, and nickel
- Located the files through Minecraft's recipe registry file converter and verified native recipe decoding, assembly, and packet round trips
- Rejected wrong metal variants in all six recipes
- Removed the six superseded programmatic registrations

Both focused verification tasks passed for 178 implementation files out of 2056
The complete compilation still failed with 62,760 diagnostics across 1,671 files
The isolated tests created the required native Forge ingredient and holder-set registries through NewRegistryEvent
They did not verify the complete production loader lifecycle, recipe registry reload, player crafting, recipe book placement, or Forge startup
The remaining legacy recipe factories, ore dictionary integration, recipe registrations, item families, and complete gameplay still required migration and verification
No working release jar was produced or exported

## Mars, Venus, and advanced metal checkpoint

- Migrated seven Mars and seven Venus variants with their retained registry IDs, names, rarity, categories, models, and descriptions
- Retained Mars smelting experience declarations for the native smelting data migration
- Verified all 69 material variants through legacy imports, native saves, packet codecs, model selectors, and selected texture references
- Preserved shield description eligibility without requiring a client input subsystem in the isolated fixture
- Migrated the shield controller use handler to the native interaction API and existing extended inventory access API
- Equipped one controller per transfer and retained the rest of the held stack
- Verified component preservation, final-item transfer, invalid-slot rejection, and occupied-slot protection in native extended storage
- Replaced the removed advanced-metal factory interface with a native Forge ingredient serializer
- Retained all seven advanced-metal selector names and resolved them against the unified project's retained item IDs
- Read the legacy selector format and emitted canonical component-based ingredients
- Rejected unknown selectors instead of returning an unrelated infinite battery ingredient
- Migrated the Desh stick and thermal fabric recipes to native recipe data while retaining their recipe IDs, layouts, and outputs
- Verified both recipes through native decoding, exact variant matching, assembly, and network round trips
- Removed both superseded legacy asset recipe files after checking their native replacements
- Converted 73 executable Mars and Venus stack constructions across 14 files

Both focused verification tasks passed for 182 implementation files out of 2057
The complete compilation still failed with 62,545 diagnostics across 1,668 files
The equipment fixture verified storage transfer, not actual player capability attachment, interaction dispatch, synchronization, or gear rendering
Venus shift-tooltip wrapping and client input handling remained unverified in a running client
Production loading, recipe reload, player gameplay, the remaining source migration, and complete Forge startup still required verification
No working release jar was produced or exported

## Core basic items and frequency module checkpoint

- Migrated all 21 Core basic item variants to native components
- Retained the 17 visible creative entries and four hidden legacy food-material variants
- Preserved the generic canned-food names and specific green descriptions without adding edible behavior to the legacy materials
- Preserved sorting categories, rarity, frequency descriptions, and all historical model mappings
- Verified all 90 material variants through legacy imports, native saves, packet codecs, model selectors, and selected texture references
- Migrated frequency module use and entity-click callbacks to native APIs
- Equipped one unlinked frequency module through the existing extended inventory access API and retained the remaining held items
- Preserved entity UUID links and telemetry bindings in native custom data components
- Wrote named dimension keys for new telemetry bindings and retained numeric ID reads for old bindings
- Preserved unresolved numeric bindings without assigning an unrelated dimension
- Updated telemetry activation and equipped-module readers to use the component-based link schema
- Resolved equipped-module targets through the native server level lookup
- Verified UUID linking, target coordinates, legacy dimensions, copy isolation, equipment transfer, persistence, and network round trips
- Converted 126 executable Core basic stack constructions across 28 files

Both focused verification tasks passed for 184 implementation files out of 2058
The complete compilation still failed with 62,230 diagnostics across 1,667 files
The frequency fixture verified shared data and transfer behavior, not actual player interaction dispatch, capability attachment, cross-dimension telemetry gameplay, or gear rendering
The complete telemetry block/entity implementation, remaining source migration, production loader startup, and full gameplay still required migration and verification
No working release jar was produced or exported

## Native command registration

- Migrated `/gchelp` to Brigadier and `RegisterCommandsEvent` so the server rebuilds it with each command dispatcher
- Replaced legacy JSON chat construction with native localized components, hover text, and clickable URL events
- Migrated `/gckit` with the native game master permission check, optional online player argument, and Forge inventory insertion with overflow drops
- Deferred emergency kit item resolution until execution and retained preferred inventory slot zero
- Removed the old server-start registration calls for these two commands
- Verified public help access, operator-only kit access, optional target parsing, rejection of excess kit arguments, missing-player console validation, repeated dispatcher registration, and component codec round trips

These checks used Minecraft's native command compilation context and did not create a live server or grant a kit in gameplay
The emergency kit item itself, remaining commands, player capabilities, and full startup still require migration

## Native equipment archive command

- Migrated `/gcinv save|restore|drop|clear <player>` to Brigadier and native game master permissions
- Preserved case-insensitive operation names and player-name suggestions
- Replaced process-wide static archive maps with native server-owned codec-backed saved data
- Preserved deferred offline restoration and the original restart behavior of pending saves
- Saved all eleven equipment slots and retained native item components, including item variants
- Imported legacy six-slot archives with signed byte slot IDs 200 through 205 and preserved later equipment slots during restoration
- Retained unloaded records and unknown root data without decoding or rewriting their item stacks
- Prevented repeated saves from replacing a pending archive with empty equipment
- Decoded every saved item before changing live slots and retained malformed archives after failed restoration
- Used the inventory already being loaded during player statistics decoding, avoiding a second capability lookup before attachment
- Kept a failed automatic archive import from aborting the remainder of player statistics decoding
- Verified native saved-data disk import, save, close, and reopen, eleven-slot component round trips, pending flags, locale-independent names, command parsing, permissions, malformed items, duplicate slots, oversized archives, and independent archive instances

Focused checks did not exercise a live server, player capability lifecycle, item drops, or client inventory synchronization
Full compilation and startup remained incomplete and no release jar was exported

## Native oxygen equipment and parachutes

- Migrated oxygen masks, oxygen gear, three tank capacities, and sixteen parachute variants to native item APIs
- Shared single-item transfers between normal item use and the emergency-kit click contract, retained components, and left occupied slots unchanged
- Preserved tank slot preference two then three and parachute slot four
- Added native item definitions for all six equipment IDs and preserved parachute model and translation mappings
- Replaced tank tool damage with a persistent and networked oxygen usage component, while preserving capacity, tooltip, and gauge behavior
- Imported legacy tank damage into oxygen usage and removed obsolete tool damage components
- Verified that native tool repair recipes rejected tanks, preventing repair from creating free oxygen
- Retained exact oxygen usage in native recipe ingredient matching
- Updated compressor, decompressor, player oxygen consumption, recharge, canister refill, GUI status, and equipment synchronization callers to use oxygen usage or parachute metadata
- Converted twenty-four concrete tank stack constructors and four concrete parachute constructors, leaving wildcard recipe inputs for their separate ingredient migration
- Preserved configured GalaxySpace tank capacities and namespace in the inherited constructor path
- Verified all sixteen legacy dye metadata mappings and native color round trips
- Passed item persistence, packet codecs, transfer policies, legacy damage imports, model selector codecs, texture references, capacity bounds, and native repair rejection checks

The focused profile compiled 194 of 2,059 implementation files
GalaxySpace modification modules, creative tab registration, live player capability attachment, machine lifecycle, world rendering, and gameplay integration remained unverified and required further migration
The full build still failed and no working jar was exported

## Native food and empty canisters

- Migrated ten food variants and two empty canister variants to native items and component-based metadata
- Preserved exact nutrition and saturation modifiers, original food creative ordering, names, tooltips, models, and rarity
- Added variant-specific food components to factory creation and legacy imports
- Refreshed variant nutrition before native eating and consumption
- Used native consumable behavior for eating duration, hunger checks, nutrition, and eating effects
- Preserved the dropped tin canister definition for canned variants zero through three and nine, resolving the registered container before consuming food
- Kept edible food separate from the hidden non-edible legacy Core basic item variants
- Added strict registered item lookup so missing containers could not silently become air
- Converted eighteen concrete food and canister stack constructors, retaining wildcard recipe inputs for their later ingredient migration
- Passed nutrition, saturation, native eating component, legacy import, variant reassignment, remainder definition, model, texture, save, and packet checks

These checks did not run live eating or spawn dropped entities
The emergency kit itself, its remaining steel tool dependency, recipe integration, full compilation, and startup remained unfinished
No working jar was exported

## Native emergency kit and steel pickaxe

- Migrated the steel pickaxe to native tool components and retained durability 768, mining speed 5, attack bonus 2, enchantability 8, and the existing pickaxe attack modifiers
- Replaced its late harvest-level mutation with a native block tag reflecting the old configured tier four
- Kept the original lack of a material repair ingredient through an empty, extensible steel tool material tag
- Confirmed the original custom material returned no repair item by inspecting the cached Forge 1.12 bytecode
- Migrated the emergency kit to native item use and preserved all nine contents, food metadata, red parachute metadata, healing potion, and long night vision potion
- Resolved the complete content list before transferring any item and consumed one kit after distribution
- Preserved the clickable equipment extension contract and Forge insertion with inventory overflow drops
- Preserved the shared legacy recipe layout and component-bearing ingredient stacks while leaving the native component-sensitive recipe migration outstanding
- Registered actual migrated items in the focused fixture during the native registration phase
- Loaded vanilla and Foundation tag resources through the native tag loader and passed mining and drop predicates for stone, dirt, and obsidian
- Passed empty and occupied equipment dispatch, independent stack snapshots, exact potion contents, native save/load and packet codecs, model selectors, and texture reference checks

These checks did not start a live server, execute kit use with a real player, or verify world mining and the native kit recipe
Other steel tools and custom block harvest tags still required migration
The full source build failed and no working release jar was exported

## Native component-sensitive emergency kit recipe

- Extended the native stack ingredient serializer with optional exact component matching while preserving existing variant-only matching by default
- Included the matching mode in ingredient equality, hashing, JSON codecs, and native recipe packet codecs
- Added the emergency kit as a native shaped data recipe and removed its obsolete programmatic registration
- Preserved its original three-by-three pattern, horizontal mirroring, input count independence, item variants, full oxygen tanks, unused steel pickaxe, and distinct potion NBT through native components
- Generated and reviewed ingredient data from the actual migrated item prototypes with the native codec
- Confirmed the original raw damage comparisons through the cached Forge 1.12 `ItemStack.getMetadata` and `Item.getMetadata(ItemStack)` bytecode
- Passed normal and mirrored native pattern matching and matching after legacy ingredient imports
- Rejected missing items, wrong potions, wrong food and parachute metadata, used tanks, worn tools, names, and unexpected custom data
- Passed native shaped recipe and ingredient JSON and packet round trips, including preservation of exact matching on the receiving side
- Kept all eight previously verified native recipes passing

Nine native recipe data files passed focused checks
Live crafting, resource reload through the complete mod, and recipe-book placement remained unverified
The legacy NBT recipe wrapper remained necessary for two unported GalaxySpace kit recipes and their unported JEI integration
The full source build still failed and no working jar was exported

## Remaining native steel tools

- Migrated the steel axe, shovel, hoe, and sword to native item components and completed the five-tool steel family
- Preserved durability 768 and each tool's original attack damage and speed modifiers
- Confirmed the old hoe's speed calculation and one-point hit durability cost from cached Forge 1.12 bytecode
- Preserved the hoe's lack of enchanting-table value instead of inheriting the other tools' enchantability eight
- Added native item category tags so enchantment supported-item and primary-item checks recognized the steel tools
- Added a steel axe efficiency tag covering vanilla axe blocks and the original plants and vines
- Kept native axe, shovel, and hoe block transformer component keys
- Removed the obsolete late harvest-level registration after moving its behavior into native tool construction
- Passed native tag loading, five combat profiles, enchantment eligibility, axe wood and plant speeds, shovel dirt speed, legacy durability imports, save and packet codecs, and item model codecs

The focused profile did not verify live attacks, enchanting-table results, world block transformations, or the remaining custom block harvest tags
The full build still failed and startup remained unverified
No working jar was exported

## Native titanium tools

- Migrated all five asteroid titanium tools to native item components and item model definitions
- Preserved the pickaxe, shovel, hoe, and sword material durability 760, speed 14, damage bonus 4, and enchantability 16 where applicable
- Preserved the titanium axe's original steel material stats, including durability 768, speed 5, and enchantability 8
- Moved the original late pickaxe, axe, and shovel harvest-tier five overrides into native material block tags
- Preserved the titanium hoe's combat speed modifier one, lack of enchanting-table value, and one-point hit cost
- Preserved the sword's effective attack modifier seven and speed minus 2.4 rather than the ineffective duplicate attribute replacements
- Executed a separate reference using cached Forge 1.12 AttributeModifier, Guava 21, and original dependencies to confirm duplicate UUID modifiers were rejected
- Preserved the sword's legacy public damage accessor and its zero, one, or two-point block durability cost at the original hardness boundary
- Added native tool category tags for enchantment compatibility and retained the original lack of raw material repair ingredients
- Passed all five combat profiles, enchantment eligibility, native mining and drop predicates, axe plant efficiency, hardness boundary calculations, model and texture checks, legacy durability imports, save codecs, and packets
- Kept the steel tool checks passing after sharing the native hoe builder across materials

These checks did not verify live attacks, real world mining, enchantment rolls, or the remaining custom block harvest tags
The full source build still failed and startup remained unverified
No working jar was exported

## Remaining foods and shared food consumers

- Migrated cheese curds and Barnard yellow fruits to native food and consumable components
- Preserved cheese nutrition one and saturation modifier 0.1, yellow fruit nutrition four and saturation modifier 0.4, and their original names, rarity, and model references
- Added a shared native food query path that refreshed metadata-specific food properties before consumer checks
- Replaced the autofeeding module's removed food class checks with native food data, retaining first-main-inventory-food priority and direct nutrition without normal eating effects or container drops
- Replaced the eating oxygen rule with native food components while retaining the Core canned food and Asmodeus space-food exemptions
- Updated Astro Wolf food eligibility and healing nutrition through the native wolf-food tag and retained the cheese meat flag contract
- Updated Astro Wolf tank checks and oxygen drain to the migrated oxygen usage component
- Converted the live Barnard food drop constructor to retained component metadata
- Passed actual native FoodData hunger and saturation changes, empty and full-hunger behavior, inventory priority, no-food handling, component food profiles, oxygen exemptions, wolf food eligibility, invalid metadata, legacy imports, model selectors, saves, and packets

The autofeeding module and Astro Wolf remained part of the incomplete module and entity migration
These focused checks did not execute a live module tick, pet interaction, eating event, or dropped entity
The complete build still failed and no working jar was exported

## Native dungeon keys and item description keys

- Migrated Core, Mars, and Venus key items and the GalaxySpace tier four and five variants to native items
- Preserved the public key-tier interface, sorting, stack limits, declared creative entries, names, models, and tier tooltips
- Preserved GalaxySpace key metadata through the retained component and converted all five boss key drop constructors
- Returned no usable tier for malformed or unrelated GalaxySpace key stacks
- Replaced the removed Mars translation setter with constructor-time native description configuration
- Found that native item finalization overwrote manually supplied ITEM_NAME construction components
- Moved the shared item properties helper and twenty-five explicit description declarations to the native overrideDescription API
- Verified all twenty previously migrated plain-item translation keys against retained language entries and preserved custom names through saves
- Passed five key tiers, legacy key imports, names, custom data, model selectors, tooltip keys, saves, packets, and invalid variant handling

Dungeon chest opening and boss drops remained unverified in live gameplay
Schematic items remained unported because their placement behavior required a complete hanging-entity migration
The complete source build still failed, startup remained unverified, and no working jar was exported

## Native schematic items, entity, and rendering

- Migrated all twelve schematic variants while retaining the Core and Planets wall placement behavior and GalaxySpace's intentional placement prohibition
- Preserved generic names, tooltip labels, native item models, metadata, and the advanced rocket crafting creative-visibility setting
- Converted forty-five live schematic stack constructors across twenty-two files
- Replaced the legacy hanging entity base with the native HangingEntity, native type registration, direction data, and a synchronized schematic index
- Used native entity spawn and tracking packets and removed the obsolete custom spawn packet, deferred send flag, and duplicate legacy entity and renderer registrations
- Preserved 32-pixel square geometry, wall alignment, support checks, creative and entity-drop rules, and independent schematic pick/drop stacks
- Added native ValueInput/ValueOutput persistence and imported legacy TileX, TileY, TileZ, Facing, and schem fields
- Replaced immediate OpenGL rendering with native render state extraction and submitted geometry, retaining the original 96 vertices, UVs, normals, and four light samples
- Registered the renderer through the native client event and kept client installer loading behind a physical-client guard with a class-name boundary
- Preserved the engineering room spawn path through native entity tracking
- Corrected GalaxySpace texture references to existing named schematic images
- Restored metadata-only page lookup so renamed or tagged schematics still unlocked their original page, matching cached 1.12 registry bytecode
- Passed native entity type registration and tracking metadata, twelve item and texture mappings, item save and packet codecs, both creative settings, native and legacy state persistence, all four wall directions, bounds and mesh checks, independent stack copies, and page lookup semantics

These checks did not instantiate a live entity or verify placement, support removal, native tracking packets, entity drops, world reload, client renderer registration timing, or rendered frames in a running game
The complete mod still failed compilation and startup remained unverified
No working jar was exported

## Native common sounds and playback APIs

- Registered all fourteen Galacticraft sound events through native RegisterEvent on both physical sides and removed the obsolete client-only registration gate
- Preserved the existing sound IDs and variable-range behavior
- Migrated sound source imports and native sound instance types used by rockets, miners, UI clicks, and parachutes
- Migrated rocket and miner loops to AbstractTickableSoundInstance, native looping and delay fields, removal checks, double-precision positions, and stop propagation
- Replaced reflective volume and pitch field access, recursive playback, and duplicate-event bookkeeping with native sound wrappers
- Preserved the selected sound, source, position, pitch, looping, tick updates, stop state, custom streaming hook, and silent-start and playback predicates while reducing volume
- Corrected Kotlin delegation of Java default methods after a runtime regression check detected lost silent-start behavior
- Passed native registration, direct and registry-holder save and packet codecs, fourteen range profiles, fifteen sound definitions, and native decoding of twenty-six Ogg assets
- Passed positional sound attenuation, non-positional bypass, dynamic state updates, stream hook delegation, and stop propagation without starting an audio device
- Migrated the three Asmodeus Core storm commands to Brigadier and RegisterCommandsEvent, retained operator permission gates and ignored trailing arguments, and marked changed weather data dirty

The storm command execution paths still depended on the unported world provider API
Rocket and miner entities, the atmosphere and equipment consumers, legacy music ticker integration, client event installation, and live audible playback remained unverified
The focused checks passed, the full shared-source build still failed, startup remained unverified, and no working jar was exported

## Native effects, radiation attributes, and damage types

- Removed the unused legacy item registry event parameter from the celestial body item hook and retained both body implementations and their registration calls
- Migrated radiation, overheating, hypothermia, and anti-radiation to native MobEffect registration and registry holders on both physical sides
- Preserved all four harmful categories, packed colors, amplifier-dependent tick periods, player-only behavior, radiation configuration scaling, thermal damage amounts, and zero-amplifier behavior
- Registered the native synchronized radiation and test attributes and installed the radiation attribute on players through EntityAttributeModificationEvent
- Replaced construction-time attribute registration and migrated attribute access in radiation accumulation, HUD reads, and anti-radiation tablets
- Preserved anti-radiation's one-point reduction, fractional and negative values, and removal once the value was nonpositive
- Copied all four original icon images unchanged into native effect sprite locations and added native English effect names and Asmodeus Core translations
- Replaced static legacy damage source instances with nine native data-driven damage types and server-level source creation
- Preserved legacy death message IDs, difficulty scaling, exhaustion, armor and shield bypasses, absolute damage mitigation bypasses, and spaceship crash explosion classification
- Converted custom damage consumers and native effect instance creation, application, removal, immunity, and inventory visibility accessors
- Added a targeted native milk consume-effect mixin that used curative-item lists rather than unconditional removal, preserving the three uncurable Asmodeus effects while allowing anti-radiation and ordinary effects to be cured
- Registered the new mixin configuration in the release manifest and required its hook and helper in the release verifier
- Passed four effect registrations, six amplifier save and packet profiles per effect including hidden effects, tick schedules, damage formulas, names, unchanged icon pixels, player attribute installation, native attribute persistence and synchronization eligibility, anti-radiation reduction, and milk cure selection
- Verified the compiled milk injection signature, cancellation flag, HEAD selector, and exact native target method descriptor without claiming live mixin weaving
- Passed native codecs for all nine damage definitions, actual merged vanilla and Foundation mitigation tags, source identities, registry-holder saves, and packets

The native checks did not verify live health changes, milk drinking, mixin weaving, rendered effect icons, or multiplayer attribute synchronization in a running game
Legacy effect and radiation attribute imports remained unverified
The original Asmodeus Core and GalaxySpace mixins remained unported and were not replaced by the new milk hook
The full source build still failed, startup remained unverified, and no working jar was exported

## Native atmosphere attributes and storm command consumers

- Removed the stale WorldProvider import from the fog contract while retaining its public density and deprecated color methods
- Added stable native codec names for all eight atmospheric gases
- Added immutable atmosphere snapshots that preserved the legacy breathing override and gas order, precipitation, corrosion, temperature, wind, and density
- Preserved atmospheric sound reduction boundaries and double-precision meteor frequency calculations and routed the existing shared provider calculations through them
- Registered a native non-positional, synchronized space environment attribute and its value type through the common registry event
- Carried body identity, opaque legacy weather save names, atmosphere, and the Asmodeus weather eligibility marker in native dimension attributes
- Added native dimension-type declarations for the Moon, Mars, Venus, and Asteroids with their original atmosphere values, height range, ordinary bed prohibition, and applicable static colors
- Removed the obsolete WorldProvider dependency from the three Asmodeus storm commands and read the active native dimension environment instead
- Kept the legacy weather eligibility test, weather save names, permission gates, ignored trailing arguments, success feedback, counter-based storm transitions, and dirty-data marking
- Migrated sound attenuation's atmosphere lookup to native dimension environment attributes
- Passed native registry registration, immutable snapshots, explicit and composition-based breathing rules, gas and atmosphere codecs, sound and meteor boundaries, and native environment evaluation
- Passed all four dimension type codecs and their network codecs, preserved synchronized atmosphere values, and checked that server-only bed rules were filtered from client data
- Passed weather-enabled profile serialization with a save name distinct from its body ID, three command permission and parse checks on repeated registration, and all three saved storm start and stop transitions

These were dimension-type declarations, not playable planet dimension stems
Planet chunk generators, complete sky rendering, native clocks and day lengths, the special Galacticraft bed flow, remaining world-provider consumers, and the other celestial dimension declarations remained unported
The native checks did not load a live planet level, execute a storm command against a running server, or verify audible atmosphere changes in gameplay
The full shared-source build still failed, startup remained unverified, and no working jar was exported

## Native independent planet clocks

- Added native world clock definitions for the Moon, Mars, Venus, and Asteroids and assigned each dimension type its own default clock
- Added synchronized day length and star brightness parameters to space environment data
- Preserved the Moon's 192,000-tick day, Mars's 24,660-tick day, Venus's 720,000-tick day, and Asteroids' static sky
- Reused the original float trigonometry table and celestial angle easing and routed the shared legacy provider angle calculation through the same implementation
- Added native dynamic environment layers for sun, moon, and star angles, star brightness, sky light level, and sky light factor
- Preserved opposite sun and moon positions and corrected the static Asteroids moon angle
- Added the clock layers through a required native environment builder mixin while retaining dimension timelines and native weather layers where precipitation was supported
- Skipped rain and thunder layers for airless dimensions and retained Venus's native weather blending
- Corrected Asteroids' fixed-time metadata so its static sky still counted as daytime under the native level rules
- Required the clock hook, helper, and four clock and dimension definitions in release validation
- Added the existing focused migration checks to the standard Gradle check task and validated its task graph without claiming a successful full check
- Matched 3,746,688 angle and star brightness samples against an isolated JVM using cached Forge 1.12 MathHelper bytecode, including every tick in the three day periods, fractional ticks, negative times, and long boundaries
- Passed native environment evaluation at noon and midnight, native clock rate and pause behavior, independent clock instances, clock state codecs, compressed NBT clock data, native time packet codecs, and client clock updates
- Passed simulated rain blending for Venus and rain bypass for airless profiles
- Verified the compiled clock injection descriptor against the actual native environment builder method

The checks did not launch Forge or prove live mixin weaving, complete planet startup, rendered celestial bodies, server command broadcasts, native server save lifecycle, or legacy villages.dat clock imports
The native clock definitions did not provide planet dimension stems or terrain generators
The full shared-source build still failed, startup remained unverified, and no working jar was exported

## Shared item value reads

- Confirmed that cached Forge 1.12 ItemStack bytecode delegated both getItemDamage and stack getMetadata to the same raw item value, with no stack-level overrides in the shared source tree
- Added an explicit native read bridge for legacy consumers while retaining separate variant metadata, oxygen usage, and physical durability components
- Read declared variants from the variant component, oxygen tanks from the oxygen usage component, explicit retained metadata from its component, and ordinary durability from the native damage component
- Migrated 457 live getItemDamage reads and 23 compiler-confirmed ItemStack getMetadata reads across 179 files
- Left comments, archived examples, unrelated getMetadata methods, and item mutation APIs unchanged
- Removed all diagnostics for these two ItemStack getter APIs from the full compiler output
- Passed every declared variant, all ten migrated tool profiles, all three oxygen tank capacities, ordinary vanilla durability, explicit wildcard metadata, empty stacks, legacy imports, named stack saves, and native packet round trips

These source edits did not establish that all 179 consuming files were fully ported
Item mutation paths, fluid canisters, electric equipment, item construction, and other consuming gameplay APIs still required migration
The full shared-source build still failed, startup remained unverified, and no working jar was exported

## Native electric items

- Migrated the shared electric item base, energy configuration and display helpers, and five battery implementation classes
- Added native finite charge components, bounded and simulated transfers, legacy charge imports, charge bars, tooltips, model descriptors, and creative stack generation
- Preserved seven battery profiles, empty stack history, quantized recipe comparisons, and infinite generator output limits
- Prevented battery charging through vanilla repair crafting
- Passed native saves, packet codecs, copy isolation, Forge Energy item capabilities, disabled inputs and outputs, and capability invalidation checks
- Applied the official Forge capability token transformer through an external test-only instrumentation agent for isolated SDK checks
- Extended release validation to require electric item classes and descriptors and reject packaged verification helpers

These isolated SDK checks did not launch Forge or verify gameplay, live machine charging, client rendering, or creative tab registration
The full shared-source build still failed with 60,205 diagnostics across 1,605 files, startup remained unverified, and no working jar was exported

## Native messages and rocket type translations

- Replaced 101 legacy literal message constructors with native chat components across 52 files and removed obsolete literal and translated component imports
- Retained existing message text and localization calls in the consuming systems
- Migrated three launch warning paths to server player recipients and native translatable components
- Migrated the occupied-seat warning to native styled action bar text
- Migrated rocket type tooltip translation to native components and resolved text when requested so language changes did not retain an enum initialization snapshot
- Preserved all five rocket type indexes, inventory capacities, and prefueled settings
- Restored the atomic battery's direct electric base inheritance while preserving its infinite store, limited output, sorting category, creative stack, and display behavior
- Passed focused compilation, native rocket translation and language reload checks, and the existing battery and shared SDK checks

The message constructor edits did not establish that their 52 consuming files were fully ported
Live rocket interactions and client rendering remained unverified
The full shared-source build still failed with 60,045 diagnostics across 1,600 files, startup remained unverified, and no working jar was exported

## Native structure bounds

- Migrated removed structure bounding box imports and types across 67 dungeon, village, and abandoned base source files
- Migrated bounds accessors, inclusive spans, intersections, containment, in-place offsets, and oriented bounding box factories to native APIs
- Preserved thirteen horizontal collision box constructors with their original vertical range of 1 through 512, confirmed against cached Forge 1.12 bytecode
- Replaced the Venus boss room's inverted extreme-coordinate sentinel with an explicit empty accumulator and independent native bounding box snapshots
- Retained the boss room point selection, room origin, inclusive dimensions, and chest linkage
- Matched 2,048 horizontal boxes and 12,288 oriented boxes against the original Forge 1.12 classes, including all six directions, negative coordinates, offsets, and varied dimensions
- Recorded the reference corpus hash and passed it again without the optional reference jar
- Passed native inclusive intersection boundaries, empty and populated accumulation, snapshot independence, JSON codecs, and native packet codecs

These geometry migrations did not establish that all 67 consuming files were fully ported
Structure piece registration, serialization, terrain generation, and live dungeon and abandoned base generation remained unverified
The full shared-source build still failed with 59,667 diagnostics across 1,600 files, startup remained unverified, and no working jar was exported

## Paused checkpoint

Saved progress and paused work at the user's request
267 of 2,076 implementation files passed focused compilation, giving 12.86% compilation coverage
The latest full compile reported 59,667 diagnostics across 1,600 files
This coverage did not measure complete gameplay migration
No working release jar was built, startup remained unverified, and no jar was exported
The next work was shared structure piece registration, serialization, and generation APIs
