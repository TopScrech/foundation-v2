package micdoodle8.mods.galacticraft.planets.mars.world.gen

import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeProviderSpace
import net.minecraft.world.biome.Biome

open class BiomeProviderMars : BiomeProviderSpace() {
    override fun getBiome(): Biome {
        return BiomeMars.Companion.marsFlat
    }
}
