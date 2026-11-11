package foundation.verification

import com.google.gson.JsonParser
import com.mojang.serialization.JsonOps
import foundation.biome.FoundationBiomeTags
import net.minecraft.SharedConstants
import net.minecraft.data.registries.VanillaRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.resources.RegistryOps
import net.minecraft.server.Bootstrap
import net.minecraft.server.packs.PackLocationInfo
import net.minecraft.server.packs.PackType
import net.minecraft.server.packs.PathPackResources
import net.minecraft.server.packs.metadata.pack.PackMetadataSection
import net.minecraft.server.packs.repository.Pack
import net.minecraft.server.packs.repository.PackCompatibility
import net.minecraft.server.packs.repository.PackSource
import net.minecraft.tags.TagFile
import net.minecraft.world.level.biome.Biome
import java.nio.file.Files
import java.nio.file.Path
import java.util.Optional

fun main() {
    try {
        verifyGameBootstrap()
    } finally {
        net.minecraft.util.Util.shutdownExecutors()
    }
}

private fun verifyGameBootstrap() {
    SharedConstants.tryDetectVersion()
    Bootstrap.bootStrap()
    verifyAirBlocks()
    verifyStructureBounds()
    verifyLegacyBlockMetadata()
    val resourcesRoot = Path.of(requireNotNull(System.getProperty("foundation.resourcesRoot")))
    verifyItemVariants(resourcesRoot)
    val location = PackLocationInfo("foundation-check", Component.literal("Foundation"), PackSource.BUILT_IN, Optional.empty())
    PathPackResources(location, resourcesRoot).use { resources ->
        for (type in PackType.values()) {
            val metadata = requireNotNull(resources.getMetadataSection(PackMetadataSection.forPackType(type)))
            check(metadata.supportedFormats().isValueInRange(SharedConstants.getCurrentVersion().packVersion(type))) {
                "Foundation pack metadata does not support $type"
            }
        }
    }
    val lookup = VanillaRegistries.createWorldLookup()
    verifyMaterialVariants(resourcesRoot, lookup)
    verifyStackIngredients(resourcesRoot, lookup)
    verifyWeatherPersistence(lookup)
    verifyAsteroidPersistence(lookup)
    verifySpinPersistence(lookup)
    verifyInventoryPersistence(lookup)
    verifyCraftingInventories()
    verifyBenchInventories()
    verifyNasaRecipes()
    verifyCircuitRecipes()
    verifyMachineInventories()
    verifyTreasureInventories()
    verifyPlayerInventorySlots()
    verifyCraftingConsumption()
    verifyRocketAssemblySlots()
    verifyRocketTypes(resourcesRoot)
    verifyEquipmentTransfer()
    verifyFrequencyModules(lookup)
    verifyHelpCommand()
    verifyEquipmentArchives(lookup)
    verifyOxygenEquipment(resourcesRoot, lookup)
    verifyFood(lookup)
    verifyEmergencyKit(resourcesRoot, lookup)
    verifyEmergencyKitRecipe(resourcesRoot, lookup)
    verifySteelTools(resourcesRoot, lookup)
    verifyTitaniumTools(resourcesRoot, lookup)
    verifyFoodConsumers(resourcesRoot, lookup)
    verifyDungeonKeys(resourcesRoot, lookup)
    verifyPlainItemNames(resourcesRoot, lookup)
    verifySchematics(resourcesRoot, lookup)
    verifySounds(resourcesRoot)
    verifyEffects(resourcesRoot, lookup)
    verifySpaceEnvironment(resourcesRoot, lookup)
    verifyPlanetClocks(resourcesRoot, lookup)
    verifyItemValues(lookup)
    verifyElectricItems(resourcesRoot, lookup)
    val ops = RegistryOps.create(JsonOps.INSTANCE, lookup)
    for (name in listOf("world_engine", "world_engine_2")) {
        val file = resourcesRoot.resolve("data/asmodeuscore/worldgen/biome/$name.json")
        val json = JsonParser.parseString(Files.readString(file))
        val biome = Biome.DIRECT_CODEC.parse(ops, json).getOrThrow()
        check(biome.hasPrecipitation())
        check(biome.baseTemperature == 0.5f)
        check(biome.waterColor and 0xffffff == 0x3f76e4)
        val encoded = Biome.DIRECT_CODEC.encodeStart(ops, biome).getOrThrow().asJsonObject
        val spawns = encoded.getAsJsonObject("attributes")
            .getAsJsonObject("minecraft:gameplay/natural_mob_spawns")
            .getAsJsonObject("argument").getAsJsonObject("spawns_by_category")
        if (name == "world_engine") {
            val zombie = spawns.getAsJsonArray("monster").single { it.asJsonObject["type"].asString == "minecraft:zombie" }.asJsonObject
            check(zombie["weight"].asInt == 95)
            check(spawns.getAsJsonArray("creature").size() == 4)
        } else check(spawns.entrySet().all { it.value.asJsonArray.isEmpty })
    }
    val packs = ArrayList<Pack>()
    for (enabled in listOf(true, false)) {
        FoundationBiomeTags.repositorySource(resourcesRoot.resolve("biome-types"), enabled).loadPacks(packs::add)
        val pack = packs.last()
        check(pack.compatibility == PackCompatibility.COMPATIBLE)
        check(pack.isRequired)
        var found = 0
        pack.open().use { resources -> resources.forEach { content -> content.use {
            for (name in listOf("is_cold", "is_dry", "is_dead")) {
                val resource = content.getResource(PackType.SERVER_DATA, Identifier.fromNamespaceAndPath("c", "tags/worldgen/biome/$name.json"))
                if (enabled) {
                    val json = requireNotNull(resource).get().bufferedReader().use { JsonParser.parseReader(it) }
                    TagFile.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow()
                    check(json.asJsonObject.getAsJsonArray("values").map { it.asString } == listOf("asmodeuscore:world_engine"))
                    found++
                } else check(resource == null)
            }
        } } }
        check(found == if (enabled) 3 else 0)
    }
    check(packs[0].id == packs[1].id)
    println("Passed isolated registry bootstrap, biome codec round trips, resource/data formats, and optional biome classifications, air-block behavior, weather persistence, asteroid persistence, station-spin persistence, and item variant codecs")
}
