package galaxyspace.systems.SolarSystem.moons.enceladus.world.gen

import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeProviderSpace
import net.minecraft.world.level.biome.Biome

open class BiomeProviderEnceladus : BiomeProviderSpace() {
    override fun getBiome(): Biome {
        return ACBiome.ACSpace
    }
}
