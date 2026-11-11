package galaxyspace.systems.SolarSystem.planets.ceres.world.gen

import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeProviderSpace
import net.minecraft.world.level.biome.Biome

open class BiomeProviderCeres : BiomeProviderSpace() {
    override fun getBiome(): Biome {
        return ACBiome.ACSpace
    }
}
