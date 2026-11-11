package micdoodle8.mods.galacticraft.planets.mars.world.gen

import net.minecraft.world.level.biome.Biome
import net.minecraftforge.common.BiomeDictionary

open class BiomeGenFlatMars constructor(properties: BiomeProperties?) : BiomeMars(properties) {
    override fun registerTypes(b: Biome?) {
        BiomeDictionary.addTypes(
            b,
            BiomeDictionary.Type.COLD,
            BiomeDictionary.Type.DRY,
            BiomeDictionary.Type.DEAD,
            BiomeDictionary.Type.SANDY
        )
    }
}
