package asmodeuscore.core.utils.worldengine

import net.minecraft.world.biome.Biome.SpawnListEntry
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Post
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Pre

import asmodeuscore.core.utils.worldengine.WE_WorldProvider
import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen_InXYZ
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_LakeGen
import asmodeuscore.core.utils.worldengine.perlinnoise.PerlinNoise
import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen
import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen_InXZ
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_CaveGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_RavineGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_OreGen
import kotlin.math.pow
import asmodeuscore.core.utils.worldengine.additions.WE_GeneratorData
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_TerrainGenerator
import net.minecraft.block.BlockFalling
import net.minecraft.entity.EnumCreatureType
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.WorldEntitySpawner
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.terraingen.PopulateChunkEvent
import net.minecraftforge.fml.common.FMLLog
import net.minecraftforge.fml.common.IWorldGenerator
import java.lang.Exception
import java.util.ArrayList
import java.util.Random

open class WE_ChunkProvider : IChunkGenerator {
    @JvmField
    var world: World? = null

    @JvmField
    @Deprecated("")
    var worldObj: World? = null

    @JvmField
    var rand: Random? = null

    @JvmField
    var createChunkGen_List: MutableList<WE_CreateChunkGen?> = ArrayList<WE_CreateChunkGen?>()

    @JvmField
    var createChunkGen_InXZ_List: MutableList<WE_CreateChunkGen_InXZ?> = ArrayList<WE_CreateChunkGen_InXZ?>()

    @JvmField
    var createChunkGen_InXYZ_List: MutableList<WE_CreateChunkGen_InXYZ?> = ArrayList<WE_CreateChunkGen_InXYZ?>()

    @JvmField
    var decorateChunkGen_List: MutableList<IWorldGenerator?> = ArrayList<IWorldGenerator?>()

    @JvmField
    var biomesList: MutableList<WE_Biome?> = ArrayList<WE_Biome?>()

    @JvmField
    var biomeNoise: MutableList<PerlinNoise?> = ArrayList<PerlinNoise?>()

    @JvmField
    var standardBiomeOnMap: WE_Biome? = null

    @JvmField
    var biomemapPersistence: Double = 1.0

    @JvmField
    var biomemapScaleX: Double = 1.0

    @JvmField
    var biomemapScaleY: Double = 1.0

    @JvmField
    var biomemapNumberOfOctaves: Int = 1

    @JvmField
    protected var wp: WE_WorldProvider? = null

    constructor()

    constructor(wp: WE_WorldProvider) {
        this.rand = Random(wp.getSeed())
        this.world = wp.world
        this.worldObj = wp.world
        this.wp = wp
        this.createChunkGen_List.add(WE_TerrainGenerator())
        this.createChunkGen_List.add(WE_CaveGen())
        this.createChunkGen_List.add(WE_RavineGen())
        val standardOres: WE_OreGen = WE_OreGen()
        standardOres.add(Blocks.GOLD_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 4, 0, 32, 16)
        standardOres.add(Blocks.IRON_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 6, 0, 64, 72)
        standardOres.add(Blocks.COAL_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 6, 32, 128, 56)
        standardOres.add(Blocks.LAPIS_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 4, 0, 16, 16)
        standardOres.add(Blocks.DIAMOND_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 3, 0, 16, 16)
        standardOres.add(Blocks.EMERALD_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 2, 0, 16, 24)
        this.decorateChunkGen_List.add(standardOres)
        val lavaLakes: WE_LakeGen = WE_LakeGen()
        lavaLakes.lakeBlock = Blocks.LAVA.getDefaultState()
        lavaLakes.iceGen = false
        lavaLakes.maxY = 32
        this.decorateChunkGen_List.add(lavaLakes)
        wp.genSettings(this)
    }

    override fun generateChunk(chunkX: Int, chunkZ: Int): Chunk {
        val primer: ChunkPrimer = ChunkPrimer()
        this.rand!!.setSeed(
            this.world!!.getSeed() * chunkX.toDouble().pow(3.0).toLong() + chunkZ.toDouble().pow(2.0)
                .toLong() * 9874L + 7684053L
        )

        try {
            val chunk_X: Long = chunkX.toLong() * 16L
            val chunk_Z: Long = chunkZ.toLong() * 16L
            val chunkBiomes: Array<Array<WE_Biome?>?> = Array<Array<WE_Biome?>?>(16) { arrayOfNulls<WE_Biome>(16) }

            for (x: Int in 0..15) {
                for (z: Int in 0..15) {
                    chunkBiomes[x]!![z] =
                        WE_Biome.Companion.getBiomeAt(this, chunk_X + x.toLong(), chunk_Z + z.toLong())
                }
            }

            for (i: Int in this.createChunkGen_List.indices) {
                this.createChunkGen_List.get(i)!!
                    .gen(WE_GeneratorData(this, primer, chunk_X, chunk_Z, chunkBiomes, 0, 0, 0))
            }

            for (x: Int in 0..15) {
                for (z: Int in 0..15) {
                    for (i: Int in this.createChunkGen_InXZ_List.indices) {
                        this.createChunkGen_InXZ_List.get(i)!!
                            .gen(WE_GeneratorData(this, primer, chunk_X, chunk_Z, chunkBiomes, x, 0, z))
                    }

                    for (i: Int in chunkBiomes[x]!![z]!!.createChunkGen_InXZ_List.indices) {
                        (chunkBiomes[x]!![z]!!.createChunkGen_InXZ_List.get(i))!!
                            .gen(WE_GeneratorData(this, primer, chunk_X, chunk_Z, chunkBiomes, x, 0, z))
                    }

                    for (y: Int in 255 downTo 0) {
                        for (i: Int in this.createChunkGen_InXYZ_List.indices) {
                            this.createChunkGen_InXYZ_List.get(i)!!
                                .gen(WE_GeneratorData(this, primer, chunk_X, chunk_Z, chunkBiomes, x, y, z))
                        }

                        for (i: Int in chunkBiomes[x]!![z]!!.createChunkGen_InXYZ_List.indices) {
                            (chunkBiomes[x]!![z]!!.createChunkGen_InXYZ_List.get(i))!!
                                .gen(WE_GeneratorData(this, primer, chunk_X, chunk_Z, chunkBiomes, x, y, z))
                        }
                    }
                }
            }

            this.wp!!.onChunkProvider(chunkX, chunkZ, primer)
        } catch (var13: Exception) {
            FMLLog.warning(
                "Error caught in planetary worldgen at coords " + chunkX + "," + chunkZ + ".",
                *arrayOfNulls<Any>(0)
            )
            var13.printStackTrace()
        }

        val chunk: Chunk = Chunk(this.world, primer, chunkX, chunkZ)
        chunk.generateSkylightMap()
        return chunk
    }

    override fun populate(chunkX: Int, chunkZ: Int) {
        BlockFalling.fallInstantly = true
        val x: Int = chunkX shl 4
        val z: Int = chunkZ shl 4
        val flag: Boolean = false
        this.rand!!.setSeed(
            this.world!!.getSeed() * chunkX.toDouble().pow(3.0).toLong() + chunkZ.toDouble().pow(2.0)
                .toLong() * 9874L + 7684053L
        )
        MinecraftForge.EVENT_BUS.post(PopulateChunkEvent.Pre(this, this.world, this.rand, chunkX, chunkZ, flag))

        for (i: Int in this.decorateChunkGen_List.indices) {
            this.decorateChunkGen_List.get(i)!!
                .generate(this.rand, x, z, this.world, this, this.world!!.getChunkProvider())
        }

        val b: WE_Biome = (WE_Biome.Companion.getBiomeAt(this, x.toLong(), z.toLong()))!!

        for (i: Int in b.decorateChunkGen_List.indices) {
            (b.decorateChunkGen_List.get(i))!!.generate(this.rand, x, z, this.world, this, this.world!!.getChunkProvider())
        }

        b.decorateBiome(this.world, this.rand, x, z)
        this.wp!!.onPopulate(chunkX, chunkZ)
        WorldEntitySpawner.performWorldGenSpawning(this.world, b, x + 8, z + 8, 16, 16, this.rand)
        MinecraftForge.EVENT_BUS.post(PopulateChunkEvent.Post(this, this.world, this.rand, chunkX, chunkZ, flag))
        BlockFalling.fallInstantly = false
    }

    override fun getPossibleCreatures(
        creatureType: EnumCreatureType,
        pos: BlockPos
    ): MutableList<Biome.SpawnListEntry?> {
        val biome: WE_Biome? = WE_Biome.Companion.getBiomeAt(this, pos.getX().toLong(), pos.getZ().toLong())
        return (if (biome != null) biome.getSpawnableList(creatureType) else null)!!
    }

    override fun recreateStructures(chunkIn: Chunk, x: Int, z: Int) {
        this.wp!!.recreateStructures(chunkIn, x, z)
    }

    override fun generateStructures(chunkIn: Chunk, x: Int, z: Int): Boolean {
        return false
    }

    override fun getNearestStructurePos(
        worldIn: World,
        structureName: String,
        position: BlockPos,
        findUnexplored: Boolean
    ): BlockPos? {
        return null
    }

    override fun isInsideStructure(worldIn: World, structureName: String, pos: BlockPos): Boolean {
        return false
    }
}
