package micdoodle8.mods.galacticraft.core.world.gen

import micdoodle8.mods.galacticraft.api.world.BiomeGenBaseGC
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie
import net.minecraft.world.biome.Biome
import net.minecraftforge.common.BiomeDictionary

open class BiomeOrbit private constructor(properties: BiomeProperties) : BiomeGenBaseGC(properties, true) {
    init {
        this.spawnableMonsterList.clear()
        this.spawnableWaterCreatureList.clear()
        this.spawnableCreatureList.clear()
        this.spawnableCaveCreatureList.clear()
        this.spawnableMonsterList.add(SpawnListEntry(EntityEvolvedZombie::class.java, 10, 4, 4))
        this.spawnableMonsterList.add(SpawnListEntry(EntityEvolvedSpider::class.java, 10, 4, 4))
    }

    override fun registerTypes(b: Biome?) {
        BiomeDictionary.addTypes(
            b,
            BiomeDictionary.Type.DRY,
            BiomeDictionary.Type.DEAD,
            BiomeDictionary.Type.SPARSE,
            BiomeDictionary.Type.SPOOKY
        )
    }

    override fun getSpawningChance(): Float {
        return 0.01f
    }

    companion object {
        @JvmField
        val space: Biome = BiomeOrbit(BiomeProperties("Space").setRainfall(0.0f))
    }
}
