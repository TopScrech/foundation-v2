package micdoodle8.mods.galacticraft.core.world.gen

import net.minecraft.world.biome.Biome
import net.minecraftforge.common.BiomeDictionary

open class BiomeFlatMoon constructor(properties: BiomeProperties?) : BiomeMoon(properties) {
    override fun registerTypes(b: Biome?) {
        BiomeDictionary.addTypes(b, BiomeDictionary.Type.COLD, BiomeDictionary.Type.DRY, BiomeDictionary.Type.DEAD)
    }
}
