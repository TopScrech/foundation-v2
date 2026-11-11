package foundation.verification

import foundation.config.FoundationConfiguration
import foundation.config.FoundationPlanetConfigOrder
import micdoodle8.mods.galacticraft.planets.asteroids.ConfigManagerAsteroids
import micdoodle8.mods.galacticraft.planets.mars.ConfigManagerMars
import micdoodle8.mods.galacticraft.planets.venus.ConfigManagerVenus
import java.nio.file.Files

fun verifyPlanetConfigurationMigration() {
    val directory = Files.createTempDirectory("foundation-planet-config-check-")
    val shared = directory.resolve("planets.conf")
    val asteroids = directory.resolve("asteroids.conf")
    val venus = directory.resolve("venus.conf")
    try {
        FoundationPlanetConfigOrder.properties.clear()
        Files.writeString(shared, "dimensions {\nI:dimensionIDMars=-204\n}\n")
        ConfigManagerMars(shared.toFile(), false)
        check(ConfigManagerMars.dimensionIDMars == -204)
        requireNotNull(ConfigManagerMars.config).save()

        Files.writeString(asteroids, "dimensions {\nI:dimensionIDAsteroids=-205\n}\n")
        ConfigManagerAsteroids(asteroids.toFile())
        check(!Files.exists(asteroids))
        check(ConfigManagerAsteroids.dimensionIDAsteroids == -205)
        check(FoundationConfiguration(shared.toFile()).get("dimensions", "dimensionIDAsteroids", 0).getInt() == -205)

        Files.writeString(venus, "dimensions {\nI:dimensionIDVenus=-206\n}\n")
        ConfigManagerVenus(venus.toFile())
        check(!Files.exists(venus))
        check(ConfigManagerVenus.dimensionIDVenus == -206)
        check(FoundationConfiguration(shared.toFile()).get("dimensions", "dimensionIDVenus", 0).getInt() == -206)

        // A remaining old file must not replace settings already present in the combined file
        Files.writeString(asteroids, "dimensions {\nI:dimensionIDAsteroids=-999\n}\n")
        ConfigManagerAsteroids.loaded = false
        ConfigManagerAsteroids(asteroids.toFile())
        check(ConfigManagerAsteroids.dimensionIDAsteroids == -205)
        check(FoundationConfiguration(shared.toFile()).get("dimensions", "dimensionIDAsteroids", 0).getInt() == -205)
        check(Files.list(directory).use { it.count() } == 1L)
    } finally {
        Files.deleteIfExists(asteroids)
        Files.deleteIfExists(venus)
        Files.deleteIfExists(shared)
        Files.deleteIfExists(directory)
        FoundationPlanetConfigOrder.properties.clear()
    }
}
