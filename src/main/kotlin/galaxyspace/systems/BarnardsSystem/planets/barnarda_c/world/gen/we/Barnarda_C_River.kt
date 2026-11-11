package galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we

import foundation.block.getStateFromMeta

import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import galaxyspace.systems.BarnardsSystem.core.configs.BRConfigCore
import galaxyspace.core.prefab.world.gen.WorldGenCircleBlock
import galaxyspace.systems.BarnardsSystem.planets.barnarda_c.world.gen.we.Barnarda_C_River
import asmodeuscore.core.utils.worldengine.WE_Biome
import net.minecraft.entity.passive.EntitySquid
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import java.util.Random

open class Barnarda_C_River constructor(min: Double, max: Double) :
    WE_Biome(BiomeProperties("barnarda_c_river"), intArrayOf(grasscolor, 0x11FF66, 0x985cff)) {
    init {
        biomeMinValueOnMap = min
        biomeMaxValueOnMap = max
        biomePersistence = 1.0
        biomeNumberOfOctaves = 4
        biomeScaleX = 280.0
        biomeScaleY = 1.7
        biomeSurfaceHeight = 50
        biomeInterpolateQuality = 15
        biomeTemerature = 0.4
        biomeNumNoise = 1
        //-//
        decorateChunkGen_List.clear()
        createChunkGen_InXZ_List.clear()

        this.spawnableWaterCreatureList.add(SpawnListEntry(EntitySquid::class.java, 10, 1, 4))

        val standardBiomeLayers: WE_BiomeLayer = WE_BiomeLayer()
        standardBiomeLayers.add(
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(3),
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(1),
            -256,
            0,
            -5,
            -1,
            true
        )
        standardBiomeLayers.add(
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(0),
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(3),
            -256,
            0,
            -1,
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
        standardBiomeLayers.add(Blocks.BEDROCK.defaultBlockState(), 0, 2, 0, 0, true)
        createChunkGen_InXZ_List.add(standardBiomeLayers)
    }

    override fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
        var randPosX: Int = x + (rand)!!.nextInt(16) + 8
        var randPosZ: Int = z + (rand)!!.nextInt(16) + 8
        var pos: BlockPos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))

        var circle: WorldGenCircleBlock = WorldGenCircleBlock(
            Blocks.CLAY.defaultBlockState(),
            (rand)!!.nextInt(5) + 3,
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(0)
        )
        if ((rand)!!.nextInt(5) == 0) {
            randPosX = x + (rand)!!.nextInt(8) + 8
            randPosZ = z + (rand)!!.nextInt(8) + 8
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))


            //if(world.getBlockState(pos).getMaterial() == Material.WATER)
            circle.generate((world)!!, (rand)!!, pos.below())
        }
        circle = WorldGenCircleBlock(
            Blocks.GRAVEL.defaultBlockState(),
            (rand)!!.nextInt(8) + 5,
            BRBlocks.BARNARDA_C_BLOCKS.getStateFromMeta(0)
        )
        if ((rand)!!.nextInt(2) == 0) {
            randPosX = x + (rand)!!.nextInt(8) + 8
            randPosZ = z + (rand)!!.nextInt(8) + 8
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))
            circle.generate((world)!!, (rand)!!, pos.below())
        }

        circle = WorldGenCircleBlock(
            BRBlocks.BARNARDA_C_FALLING_BLOCKS.getStateFromMeta(0),
            (rand)!!.nextInt(8) + 5,
            BRBlocks.BARNARDA_C_GRASS.getStateFromMeta(0)
        )
        if ((rand)!!.nextInt(2) == 0) {
            randPosX = x + (rand)!!.nextInt(8) + 8
            randPosZ = z + (rand)!!.nextInt(8) + 8
            pos = (world)!!.getHeight(BlockPos(randPosX, 0, randPosZ))
            if (pos.getY() < 67) circle.generate((world)!!, (rand)!!, pos.below())
        }
    }

    companion object {
        private val grasscolor = if (BRConfigCore.enableGreenBarnardaC) 0x45AC33 else 0xdf73ff
    }
}
