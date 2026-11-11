package asmodeuscore.core.utils.worldengine

import foundation.block.LegacyVanillaBlockStates
import foundation.block.LegacyVanillaBlockIds

import foundation.block.getStateFromMeta

import net.minecraft.world.biome.Biome.BiomeProperties

import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen_InXYZ
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_BiomeLayer
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_SnowGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_LakeGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_WorldTreeGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_GrassGen
import asmodeuscore.core.utils.worldengine.perlinnoise.PerlinNoise
import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen_InXZ
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.gen.feature.WorldGenerator
import net.minecraftforge.fml.common.IWorldGenerator
import java.util.ArrayList
import java.util.Random

open class WE_Biome constructor(prop: BiomeProperties) : Biome(prop) {
    @JvmField
    var id: Int = 0

    @JvmField
    var biomeMinValueOnMap: Double = -1.0

    @JvmField
    var biomeMaxValueOnMap: Double = 1.0

    @JvmField
    var biomePersistence: Double = 1.0

    @JvmField
    var biomeScaleX: Double = 1.0

    @JvmField
    var biomeScaleY: Double = 1.0

    @JvmField
    var biomeTemerature: Double = 0.0

    @JvmField
    var biomeNumberOfOctaves: Int = 1

    @JvmField
    var biomeSurfaceHeight: Int = 63

    @JvmField
    var biomeInterpolateQuality: Int = 16

    @JvmField
    var biomeBlockGrassColor: Int = 0

    @JvmField
    var biomeBlockWaterColor: Int = 0

    @JvmField
    var biomeBlockLeavesColor: Int = 0

    @JvmField
    var biomeNumNoise: Int = 0

    @JvmField
    var biomeAbs: Boolean = false

    @JvmField
    var createChunkGen_InXZ_List: MutableList<WE_CreateChunkGen_InXZ?> = ArrayList<WE_CreateChunkGen_InXZ?>()

    @JvmField
    var createChunkGen_InXYZ_List: MutableList<WE_CreateChunkGen_InXYZ?> = ArrayList<WE_CreateChunkGen_InXYZ?>()

    @JvmField
    var decorateChunkGen_List: MutableList<IWorldGenerator?> = ArrayList<IWorldGenerator?>()

    init {
        this.setRegistryName(prop.biomeName)
    }

    constructor(prop: BiomeProperties, colors: IntArray) : this(prop) {
        this.spawnableCaveCreatureList.clear()
        this.spawnableCreatureList.clear()
        this.spawnableMonsterList.clear()
        this.spawnableWaterCreatureList.clear()
        this.biomeBlockGrassColor = colors[0]
        this.biomeBlockWaterColor = colors[1]
        this.biomeBlockLeavesColor = colors[2]
        val standardBiomeLayers: WE_BiomeLayer = WE_BiomeLayer()
        standardBiomeLayers.add(Blocks.DIRT.defaultBlockState(), Blocks.STONE.defaultBlockState(), -256, 0, -4, -2, true)
        standardBiomeLayers.add(Blocks.GRASS.defaultBlockState(), Blocks.DIRT.defaultBlockState(), -256, 0, -256, 0, false)
        standardBiomeLayers.add(Blocks.BEDROCK.defaultBlockState(), 0, 2, 0, 0, true)
        this.createChunkGen_InXZ_List.add(standardBiomeLayers)
        this.createChunkGen_InXZ_List.add(WE_SnowGen())
        this.decorateChunkGen_List.add(WE_LakeGen())
        val treeGen: WE_WorldTreeGen = WE_WorldTreeGen()
        treeGen.add(
            Blocks.LOG,
            0,
            Blocks.LEAVES,
            0,
            Blocks.SAPLING,
            Blocks.VINE,
            Blocks.COCOA,
            8,
            1,
            8,
            4,
            false,
            2.toByte(),
            0.toByte(),
            0.toByte(),
            1.toByte(),
            2.toByte(),
            1.toByte(),
            1,
            12,
            4,
            0.618,
            0.381,
            1.0,
            1.0
        )
        this.decorateChunkGen_List.add(treeGen)
        val grassGen: WE_GrassGen = WE_GrassGen()
        grassGen.add(LegacyVanillaBlockStates.state(LegacyVanillaBlockIds.TALLGRASS, 1), 8, false, Blocks.GRASS.defaultBlockState())
        grassGen.add(LegacyVanillaBlockStates.state(LegacyVanillaBlockIds.TALLGRASS, 2), 16, false, Blocks.GRASS.defaultBlockState())
        grassGen.add(Blocks.RED_FLOWER.defaultBlockState(), 128, false, Blocks.GRASS.defaultBlockState())
        grassGen.add(Blocks.YELLOW_FLOWER.defaultBlockState(), 128, false, Blocks.GRASS.defaultBlockState())
        grassGen.add(Blocks.DEADBUSH.defaultBlockState(), 512, false, Blocks.GRASS.defaultBlockState())
        grassGen.add(Blocks.WATERLILY.defaultBlockState(), 256, false, Blocks.WATER.defaultBlockState())
        this.decorateChunkGen_List.add(grassGen)
    }

    constructor(
        prop: BiomeProperties,
        minMapValue: Double,
        maxMapValue: Double,
        persistence: Double,
        numOctaves: Int,
        sx: Double,
        sy: Double,
        height: Int,
        interpolateQuality: Int,
        colors: IntArray
    ) : this(prop, colors) {
        this.biomeMinValueOnMap = minMapValue
        this.biomeMaxValueOnMap = maxMapValue
        this.biomePersistence = persistence
        this.biomeNumberOfOctaves = numOctaves
        this.biomeScaleX = sx
        this.biomeScaleY = sy
        this.biomeSurfaceHeight = height
        this.biomeInterpolateQuality = interpolateQuality
    }

    open fun setAbsoluteValue(): WE_Biome {
        this.biomeAbs = true
        return this
    }

    open fun decorateBiome(world: World?, rand: Random?, x: Int, z: Int) {
    }

    protected open fun generateOre(
        world: World,
        rand: Random,
        pos: BlockPos,
        worldGenerator: WorldGenerator,
        amountPerChunk: Int,
        minY: Int,
        maxY: Int
    ) {
        for (var5x: Int in 0..<amountPerChunk) {
            val var7x: Int = rand.nextInt(maxY - minY) + minY
            worldGenerator.generate(world, rand, BlockPos(pos.getX(), var7x, pos.getZ()))
        }
    }

    companion object {
        @JvmStatic
        fun setBiomeMap(cp: WE_ChunkProvider, persistence: Double, numOctaves: Int, sx: Double, sy: Double) {
            cp.biomemapPersistence = persistence
            cp.biomemapNumberOfOctaves = numOctaves
            cp.biomemapScaleX = sx
            cp.biomemapScaleY = sy
        }

        @JvmStatic
        fun addPrelinNoise(cp: WE_ChunkProvider, pn: PerlinNoise?) {
            cp.biomeNoise.add(pn)
        }

        @JvmStatic
        fun addBiomeToGeneration(cp: WE_ChunkProvider, biome: WE_Biome): Int {
            if (biome.biomeMaxValueOnMap < biome.biomeMinValueOnMap) {
                val t: Double = biome.biomeMaxValueOnMap
                biome.biomeMaxValueOnMap = biome.biomeMinValueOnMap
                biome.biomeMinValueOnMap = t
            }

            cp.biomesList.add(biome)
            biome.id = cp.biomesList.size
            if (cp.standardBiomeOnMap == null) {
                cp.standardBiomeOnMap = biome
            }

            return biome.id
        }

        @JvmStatic
        fun getBiomeAt(cp: WE_ChunkProvider, x: Long, z: Long): WE_Biome? {
            var r: WE_Biome? = null
            val biomeMapData: Double = PerlinNoise(
                (cp.world)!!.getSeed(),
                cp.biomemapPersistence,
                cp.biomemapNumberOfOctaves,
                cp.biomemapScaleX,
                cp.biomemapScaleY,
                0
            )
                .genNoise2d(x.toDouble(), z.toDouble())

            for (i: Int in cp.biomesList.indices) {
                val getBiome: WE_Biome = (cp.biomesList.get(i))!!
                if (getBiome.biomeAbs) {
                    getBiome.biomeMinValueOnMap = -getBiome.biomeMaxValueOnMap - 0.5
                }

                if (biomeMapData >= (cp.biomesList.get(i))!!.biomeMinValueOnMap && biomeMapData <= (cp.biomesList.get(i))!!.biomeMaxValueOnMap) {
                    if (r != null) {
                        if ((cp.biomesList.get(i))!!.biomeMaxValueOnMap - (cp.biomesList.get(i))!!.biomeMinValueOnMap < r.biomeMaxValueOnMap - r.biomeMinValueOnMap) {
                            r = cp.biomesList.get(i)
                        }
                    } else {
                        r = cp.biomesList.get(i)
                    }
                }
            }

            if (r == null) {
                r = cp.standardBiomeOnMap
            }

            return r
        }

        @JvmStatic
        fun getBiggestInterpolationQuality(cp: WE_ChunkProvider): Int {
            var r: Int = 0

            for (i: Int in cp.biomesList.indices) {
                if ((cp.biomesList.get(i))!!.biomeInterpolateQuality > r) {
                    r = (cp.biomesList.get(i))!!.biomeInterpolateQuality
                }
            }

            return r
        }
    }
}
