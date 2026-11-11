package micdoodle8.mods.galacticraft.core.world.gen

import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeProviderSpace
import net.minecraft.world.level.biome.Biome

open class BiomeProviderOrbit : BiomeProviderSpace() {
    override fun getBiome(): Biome {
        return BiomeOrbit.Companion.space
    }
}
