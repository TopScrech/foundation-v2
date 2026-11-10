package galaxyspace.systems.SolarSystem.moons.miranda.world.gen

import galaxyspace.systems.SolarSystem.SolarSystemBodies
import galaxyspace.systems.SolarSystem.moons.miranda.world.gen.BiomeProviderMiranda
import galaxyspace.systems.SolarSystem.moons.miranda.world.gen.layer.GenLayerMirandaBiomes
import net.minecraft.world.World
import net.minecraft.world.WorldType
import net.minecraft.world.biome.BiomeProvider
import net.minecraft.world.gen.layer.GenLayer
import net.minecraft.world.gen.layer.GenLayerVoronoiZoom
import net.minecraft.world.gen.layer.GenLayerZoom

open class BiomeProviderMiranda constructor(world: World) : BiomeProvider(world.getWorldInfo()) {
    init {
        allowedBiomes.clear()
        allowedBiomes = (SolarSystemBodies.mirandaUranus)!!.getBiomes()
    }

    override fun getModdedBiomeGenerators(
        worldType: WorldType,
        seed: Long,
        original: Array<GenLayer?>
    ): Array<GenLayer?> {
        var biomes: GenLayer = GenLayerMirandaBiomes(seed)
        biomes = GenLayerZoom(1000L, biomes)
        biomes = GenLayerZoom(1001L, biomes)
        biomes = GenLayerZoom(1002L, biomes)
        biomes = GenLayerZoom(1003L, biomes)
        biomes = GenLayerZoom(1004L, biomes)
        val genLayerVeronoiZoom: GenLayer = GenLayerVoronoiZoom(10L, biomes)

        biomes.initWorldGenSeed(seed)
        genLayerVeronoiZoom.initWorldGenSeed(seed)

        return ((arrayOf<GenLayer>(biomes, genLayerVeronoiZoom) as Array<GenLayer?>?))!!
    }
}
