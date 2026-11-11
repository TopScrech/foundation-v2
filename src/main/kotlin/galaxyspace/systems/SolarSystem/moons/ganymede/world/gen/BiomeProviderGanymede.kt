package galaxyspace.systems.SolarSystem.moons.ganymede.world.gen

import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import galaxyspace.systems.SolarSystem.moons.ganymede.world.gen.BiomeProviderGanymede
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeProviderSpace
import net.minecraft.world.level.biome.Biome

open class BiomeProviderGanymede : BiomeProviderSpace() {
    override fun getBiome(): Biome {
        return ACBiome.ACSpace
    }
}
