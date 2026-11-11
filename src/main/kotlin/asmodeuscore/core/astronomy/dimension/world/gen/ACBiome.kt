package asmodeuscore.core.astronomy.dimension.world.gen

import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import asmodeuscore.core.astronomy.dimension.world.gen.biome.ACBiomeGen
import micdoodle8.mods.galacticraft.api.world.BiomeGenBaseGC
import net.minecraft.world.level.biome.Biome

abstract class ACBiome(properties: BiomeProperties?) : BiomeGenBaseGC((properties)!!) {
    @JvmField
    var topMeta: Byte = 0

    @JvmField
    var fillerMeta: Byte = 0

    @JvmField
    var stoneMeta: Byte = 0

    override fun getSpawningChance(): Float {
        return 0.1f
    }

    companion object {
        @JvmField
        var ACSpace: Biome =
            ACBiomeGen(BiomeProperties("Space").setBaseHeight(1.0f).setHeightVariation(0.5f).setRainfall(0.0f))

        @JvmField
        var ACSpaceLowPlains: Biome = ACBiomeGen(
            BiomeProperties("SpaceLowPlains").setBaseHeight(0.125f).setHeightVariation(0.5f).setRainfall(0.0f)
        )

        @JvmField
        var ACSpaceMidPlains: Biome =
            ACBiomeGen(BiomeProperties("SpaceMidPlains").setBaseHeight(0.2f).setHeightVariation(0.5f).setRainfall(0.0f))

        @JvmField
        var ACSpaceLowHills: Biome =
            ACBiomeGen(BiomeProperties("SpaceLowHills").setBaseHeight(0.45f).setHeightVariation(0.5f).setRainfall(0.0f))

        @JvmField
        var ACSpaceMidHills: Biome =
            ACBiomeGen(BiomeProperties("SpaceMidHills").setBaseHeight(1.0f).setHeightVariation(0.5f).setRainfall(0.0f))
    }
}
