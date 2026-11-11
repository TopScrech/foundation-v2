package micdoodle8.mods.galacticraft.planets.asteroids.world.gen

import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeProviderSpace
import net.minecraft.world.level.biome.Biome

open class BiomeProviderAsteroids : BiomeProviderSpace() {
    override fun getBiome(): Biome {
        return BiomeAsteroids.Companion.asteroid
    }
}
