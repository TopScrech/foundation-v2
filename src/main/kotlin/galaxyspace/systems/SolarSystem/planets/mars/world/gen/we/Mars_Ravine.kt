package galaxyspace.systems.SolarSystem.planets.mars.world.gen.we

import foundation.block.getStateFromMeta

import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import galaxyspace.core.prefab.entities.EntityEvolvedColdBlaze
import asmodeuscore.core.utils.worldengine.WE_Biome
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedEnderman
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import net.minecraft.world.level.block.Blocks

open class Mars_Ravine constructor(min: Double, max: Double) :
    WE_Biome(BiomeProperties("mars_ravine"), intArrayOf(0x00CC00, 0xFFFFFF, 0x00CC00)) {
    init {
        biomeMinValueOnMap = min
        biomeMaxValueOnMap = max
        biomePersistence = 1.2
        biomeNumberOfOctaves = 4
        biomeScaleX = 280.0
        biomeScaleY = 1.7
        biomeSurfaceHeight = 45
        biomeInterpolateQuality = 8


        //-//
        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()
        spawnableMonsterList.add(SpawnListEntry(EntityEvolvedSpider::class.java, 10, 1, 2))
        spawnableMonsterList.add(SpawnListEntry(EntityEvolvedSkeleton::class.java, 10, 1, 2))
        spawnableMonsterList.add(SpawnListEntry(EntityEvolvedZombie::class.java, 10, 1, 2))
        spawnableMonsterList.add(SpawnListEntry(EntityEvolvedEnderman::class.java, 10, 1, 2))
        spawnableMonsterList.add(SpawnListEntry(EntityEvolvedColdBlaze::class.java, 10, 1, 2))

        val standardBiomeLayers: WE_BiomeLayer = WE_BiomeLayer()
        standardBiomeLayers.add(
            (MarsBlocks.marsBlock)!!.getStateFromMeta(6),
            (MarsBlocks.marsBlock)!!.getStateFromMeta(9),
            -256,
            0,
            -4,
            -6,
            true
        )
        standardBiomeLayers.add(
            (MarsBlocks.marsBlock)!!.getStateFromMeta(5),
            (MarsBlocks.marsBlock)!!.getStateFromMeta(6),
            -256,
            0,
            -2,
            -1,
            true
        )
        standardBiomeLayers.add(Blocks.BEDROCK.defaultBlockState(), 0, 2, 0, 0, true)
        createChunkGen_InXZ_List.add(standardBiomeLayers)
    }
}
