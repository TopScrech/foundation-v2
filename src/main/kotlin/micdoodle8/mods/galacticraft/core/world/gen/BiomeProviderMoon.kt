package micdoodle8.mods.galacticraft.core.world.gen

import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeProviderSpace
import net.minecraft.world.biome.Biome

open class BiomeProviderMoon : BiomeProviderSpace() {
    override fun getBiome(): Biome {
        return BiomeMoon.Companion.moonFlat
    }
}
