package asmodeuscore.core.astronomy.dimension.world.worldengine.biome

import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import asmodeuscore.core.astronomy.dimension.world.worldengine.biome.WE_BaseBiome
import asmodeuscore.core.utils.worldengine.WE_Biome
import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen_InXZ
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedEnderman
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie
import net.minecraftforge.fml.common.IWorldGenerator

open class WE_BaseBiome constructor(
    min: Double,
    max: Double,
    persistance: Double,
    octaves: Int,
    height: Int,
    intquility: Int,
    layers: WE_BiomeLayer?,
    vararg gens: IWorldGenerator?
) : WE_Biome(BiomeProperties("we_basebiome"), colors) {
    /**
     *
     * @param
     */
    constructor(
        max: Double,
        persistance: Double,
        octaves: Int,
        height: Int,
        intquility: Int,
        layers: WE_BiomeLayer?,
        vararg gens: IWorldGenerator?
    ) : this(0.0, max, persistance, octaves, height, intquility, layers, *gens) {
        setAbsoluteValue()
    }

    init {
        biomeMinValueOnMap = min
        biomeMaxValueOnMap = max
        biomePersistence = persistance
        biomeNumberOfOctaves = octaves
        biomeScaleX = 280.0
        biomeScaleY = 1.5
        biomeSurfaceHeight = height
        biomeInterpolateQuality = intquility


        //-//
        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()
        spawnableMonsterList.clear()
        spawnableCaveCreatureList.clear()
        spawnableCreatureList.clear()
        spawnableWaterCreatureList.clear()

        if (layers != null) createChunkGen_InXZ_List.add(layers)

        for (gen: IWorldGenerator? in gens) decorateChunkGen_List.add(gen)
    }

    open fun setColors(grass: Int, water: Int, leaves: Int): WE_BaseBiome {
        biomeBlockGrassColor = grass
        biomeBlockWaterColor = water
        biomeBlockLeavesColor = leaves
        return this
    }


    open fun setSize(x: Double, y: Double): WE_BaseBiome {
        biomeScaleX = x
        biomeScaleY = y
        return this
    }

    open fun addChunkGen(gen: WE_CreateChunkGen_InXZ?): WE_BaseBiome {
        createChunkGen_InXZ_List.add(gen)
        return this
    }

    open fun setPrelinNoiseID(id: Int): WE_BaseBiome {
        biomeNumNoise = id
        return this
    }


    open fun setBaseSpawn(): WE_BaseBiome {
        spawnableMonsterList.add(SpawnListEntry(EntityEvolvedSpider::class.java, 10, 1, 2))
        spawnableMonsterList.add(SpawnListEntry(EntityEvolvedSkeleton::class.java, 10, 1, 2))
        spawnableMonsterList.add(SpawnListEntry(EntityEvolvedZombie::class.java, 10, 1, 2))
        spawnableMonsterList.add(SpawnListEntry(EntityEvolvedEnderman::class.java, 10, 1, 2))
        return this
    }

    companion object {
        private val colors = intArrayOf(0x00CC00, 0xFFFFFF, 0x00CC00)
    }
}
