package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we

import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import galaxyspace.systems.BarnardsSystem.core.configs.BRConfigCore
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we.Barnarda_C_SnowPlains
import asmodeuscore.core.utils.worldengine.WE_Biome
import net.minecraft.entity.monster.EntityEnderman
import net.minecraft.entity.monster.EntityPolarBear
import net.minecraft.entity.monster.EntitySkeleton
import net.minecraft.entity.monster.EntitySpider
import net.minecraft.entity.monster.EntityZombie
import net.minecraft.entity.passive.EntityCow
import net.minecraft.init.Blocks
import net.minecraft.world.World
import java.util.Random

open class Barnarda_C_SnowPlains constructor(min: Double, max: Double, height: Int) :
    WE_Biome(BiomeProperties("barnarda_c_snowplains"), intArrayOf(grasscolor, 0x11FF66, 0x985cff)) {
    init {
        biomeMinValueOnMap = min
        biomeMaxValueOnMap = max
        biomePersistence = 1.4
        biomeNumberOfOctaves = 4
        biomeScaleX = 280.0
        biomeScaleY = 1.7
        biomeSurfaceHeight = height
        biomeInterpolateQuality = 25
        biomeTemerature = -0.1
        //-//
        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()

        this.spawnableCreatureList.add(SpawnListEntry(EntityCow::class.java, 10, 1, 4))
        this.spawnableCreatureList.add(SpawnListEntry(EntityPolarBear::class.java, 10, 1, 3))

        this.spawnableMonsterList.add(SpawnListEntry(EntityZombie::class.java, 10, 1, 4))
        this.spawnableMonsterList.add(SpawnListEntry(EntitySpider::class.java, 10, 1, 4))
        this.spawnableMonsterList.add(SpawnListEntry(EntitySkeleton::class.java, 10, 1, 4))
        this.spawnableMonsterList.add(SpawnListEntry(EntityEnderman::class.java, 10, 1, 4))

        val standardBiomeLayers: WE_BiomeLayer = WE_BiomeLayer()
        standardBiomeLayers.add(
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(0),
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(1),
            -256,
            0,
            -5,
            -1,
            true
        )
        standardBiomeLayers.add(
            BRBlocks.BARNARDA_C_GRASS.getStateFromMeta(0),
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(0),
            -256,
            0,
            -256,
            0,
            false
        )
        standardBiomeLayers.add(
            Blocks.SNOW_LAYER.getDefaultState(),
            BRBlocks.BARNARDA_C_GRASS.getStateFromMeta(0),
            -256,
            0,
            -1,
            0,
            false
        )
        standardBiomeLayers.add(Blocks.BEDROCK.getDefaultState(), 0, 2, 0, 0, true)
        createChunkGen_InXZ_List.add(standardBiomeLayers)
    }

    override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
    }

    companion object {
        private val grasscolor = if (BRConfigCore.enableGreenBarnardaC) 0x88BB44 else 0x822899
    }
}
