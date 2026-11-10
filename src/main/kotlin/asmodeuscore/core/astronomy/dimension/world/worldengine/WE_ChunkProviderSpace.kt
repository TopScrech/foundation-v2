//- By Vamig Aliev.
//- https://vk.com/win_vista.
package asmodeuscore.core.astronomy.dimension.world.worldengine

import java.util.Arrays
import asmodeuscore.core.utils.Utils
import net.minecraft.world.biome.Biome.SpawnListEntry

import kotlin.math.pow
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_WorldProviderSpace
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_CaveGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_RavineGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_OreGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_LakeGen
import asmodeuscore.core.utils.worldengine.additions.WE_GeneratorData
import asmodeuscore.core.astronomy.dimension.world.worldengine.WE_ChunkProviderSpace
import asmodeuscore.core.utils.worldengine.WE_Biome
import asmodeuscore.core.utils.worldengine.WE_ChunkProvider
import asmodeuscore.core.utils.worldengine.standardcustomgen.WE_TerrainGenerator
import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeDecoratorSpace
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta
import micdoodle8.mods.galacticraft.core.perlin.generator.Gradient
import micdoodle8.mods.galacticraft.core.world.gen.EnumCraterSize
import net.minecraft.entity.EnumCreatureType
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ChunkPrimer
import net.minecraftforge.fml.common.FMLLog
import java.lang.Exception
import java.util.ArrayList
import java.util.Random
import kotlin.math.abs

open class WE_ChunkProviderSpace constructor(wp: WE_WorldProviderSpace) : WE_ChunkProvider() {
    private val noiseGen5: Gradient

    //////////////////
    //- Generators -//
    //////////////////
    @JvmField
    var worldGenerators: MutableList<MapGenBaseMeta> = (ArrayList<Any?>() as MutableList<MapGenBaseMeta>)

    private val decorator: BiomeDecoratorSpace? = null

    //////////////////////
    //- Biome Map Info -//
    //////////////////////
    @JvmField
    var CRATER_PROB: Int = 0

    //private WE_WorldProviderSpace wp;
    /////
    //=//
    /////
    init {
        rand = Random(wp.getSeed())
        world = wp.world
        worldObj = wp.world
        this.wp = wp

        /////
        //=//
        /////
        createChunkGen_List.add(WE_TerrainGenerator())
        createChunkGen_List.add(WE_CaveGen())
        createChunkGen_List.add(WE_RavineGen())

        val standardOres: WE_OreGen = WE_OreGen()
        standardOres.add(Blocks.GOLD_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 4, 0, 32, 16)
        standardOres.add(Blocks.IRON_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 6, 0, 64, 72)
        standardOres.add(Blocks.COAL_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 6, 32, 128, 56)
        standardOres.add(Blocks.LAPIS_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 4, 0, 16, 16)
        standardOres.add(Blocks.DIAMOND_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 3, 0, 16, 16)
        standardOres.add(Blocks.EMERALD_ORE.getDefaultState(), Blocks.STONE.getDefaultState(), 2, 0, 16, 24)
        decorateChunkGen_List.add(standardOres)
        //-//
        val lavaLakes: WE_LakeGen = WE_LakeGen()
        lavaLakes.lakeBlock = Blocks.LAVA.getDefaultState()
        lavaLakes.iceGen = false
        lavaLakes.maxY = 32
        decorateChunkGen_List.add(lavaLakes)


        this.noiseGen5 = Gradient((this.rand)!!.nextLong(), 1, 0.25f)
        //decorator = wp.getDecorator();
        wp.genSettings(this)
    }

    override fun generateChunk(chunkX: Int, chunkZ: Int): Chunk {
        val primer: ChunkPrimer = ChunkPrimer()

        (rand)!!.setSeed(
            (world)!!.getSeed() * chunkX.toDouble().pow(3.0).toLong() + chunkZ.toDouble().pow(2.0)
                .toLong() * 9874L + 7684053L
        )
        try {
            this.noiseGen5.setFrequency(0.01f)

            val chunk_X: Long = chunkX.toLong() * 16L
            val chunk_Z: Long = chunkZ.toLong() * 16L


            val chunkBiomes: Array<Array<WE_Biome?>?> = Array<Array<WE_Biome?>?>(16) { arrayOfNulls<WE_Biome>(16) }
            for (x: Int in 0..15) for (z: Int in 0..15) {
                chunkBiomes[x]!![z] = WE_Biome.getBiomeAt(this, chunk_X + x.toLong(), chunk_Z + z.toLong())
            }


            /////
            //=//
            /////
            //-//
            for (i: Int in createChunkGen_List.indices) (createChunkGen_List.get(i))!!
                .gen(WE_GeneratorData(this, primer, chunk_X, chunk_Z, chunkBiomes, 0, 0, 0))

            if (this.CRATER_PROB > 0) this.createCraters(chunkX, chunkZ, primer)

            for (x: Int in 0..15) for (z: Int in 0..15) {
                for (i: Int in createChunkGen_InXZ_List.indices) (createChunkGen_InXZ_List.get(i))!!
                    .gen(WE_GeneratorData(this, primer, chunk_X, chunk_Z, chunkBiomes, x, 0, z))

                for (i: Int in chunkBiomes[x]!![z]!!.createChunkGen_InXZ_List.indices) {
                    (chunkBiomes[x]!![z]!!.createChunkGen_InXZ_List.get(i))!!
                        .gen(WE_GeneratorData(this, primer, chunk_X, chunk_Z, chunkBiomes, x, 0, z))
                }
                //-//
                for (y: Int in 255 downTo 0) {
                    for (i: Int in createChunkGen_InXYZ_List.indices) (createChunkGen_InXYZ_List.get(i))!!
                        .gen(WE_GeneratorData(this, primer, chunk_X, chunk_Z, chunkBiomes, x, y, z))
                    for (i: Int in chunkBiomes[x]!![z]!!.createChunkGen_InXYZ_List.indices) (chunkBiomes[x]!![z]!!.createChunkGen_InXYZ_List.get(
                        i
                    ))!!.gen(WE_GeneratorData(this, primer, chunk_X, chunk_Z, chunkBiomes, x, y, z))
                }
            }


            (wp)!!.onChunkProvider(chunkX, chunkZ, primer)

            for (generator: MapGenBaseMeta in this.worldGenerators) {
                generator.generate((this.world)!!, chunkX, chunkZ, primer)
            }
            /////
            //=//
            /////
        } catch (e: Exception) {
            FMLLog.warning("Error caught in planetary worldgen at coords " + chunkX + "," + chunkZ + ".")
            e.printStackTrace()
        }

        val chunk: Chunk = Chunk(this.world, primer, chunkX, chunkZ)
        /*byte[] arraybiome = new byte[256];
		Arrays.fill(arraybiome, (byte) Biome.getIdForBiome(Utils.WE_Biome));
		chunk.setBiomeArray(arraybiome);
		*/
        chunk.generateSkylightMap()
        return chunk
    }

    override fun populate(chunkX: Int, chunkZ: Int) {
        super.populate(chunkX, chunkZ)
    }

    /*
	public void decoratePlanet(World world, Random rand, int x, int z)
	{
		if(getBiomeGenerator() != null)
			this.getBiomeGenerator().decorate(world, rand, x, z);
	}
	
	public void setBiomeGenerator(BiomeDecoratorSpace decorator)
	{
		this.decorator = decorator; 
	}
	
	protected BiomeDecoratorSpace getBiomeGenerator()
	{
		return this.decorator;		
	}*/
    override fun getPossibleCreatures(
        creatureType: EnumCreatureType,
        pos: BlockPos
    ): MutableList<Biome.SpawnListEntry?> {
        return super.getPossibleCreatures(creatureType, pos)
    }

    override fun recreateStructures(chunkIn: Chunk, x: Int, z: Int) {
        (wp)!!.recreateStructures(chunkIn, x, z)
    }

    override fun generateStructures(chunkIn: Chunk, x: Int, z: Int): Boolean {
        return false
    }

    override fun getNearestStructurePos(
        worldIn: World, structureName: String, position: BlockPos,
        findUnexplored: Boolean
    ): BlockPos? {
        return null
    }

    override fun isInsideStructure(worldIn: World, structureName: String, pos: BlockPos): Boolean {
        return false
    }

    open fun createCraters(chunkX: Int, chunkZ: Int, primer: ChunkPrimer) {
        this.noiseGen5.setFrequency(0.015f)
        for (cx: Int in chunkX - 2..chunkX + 2) {
            for (cz: Int in chunkZ - 2..chunkZ + 2) {
                for (x: Int in 0..15) {
                    for (z: Int in 0..15) {
                        if (abs(
                                this.randFromPoint(
                                    cx * 16 + x,
                                    (cz * 16 + z) * 1000
                                )
                            ) < this.noiseGen5.getNoise(
                                (cx * 16 + x).toFloat(),
                                (cz * 16 + z).toFloat()
                            ) / this.CRATER_PROB
                        ) {
                            val random: Random = Random((cx * 16 + x + (cz * 16 + z) * 5000).toLong())
                            val cSize: EnumCraterSize =
                                EnumCraterSize.sizeArray[random.nextInt(EnumCraterSize.sizeArray.size)]
                            val size: Int = random.nextInt(cSize.MAX_SIZE - cSize.MIN_SIZE) + cSize.MIN_SIZE + 15
                            this.makeCrater(cx * 16 + x, cz * 16 + z, chunkX * 16, chunkZ * 16, size, primer)
                        }
                    }
                }
            }
        }
    }

    open fun makeCrater(craterX: Int, craterZ: Int, chunkX: Int, chunkZ: Int, size: Int, primer: ChunkPrimer) {
        for (x: Int in 0..15) {
            for (z: Int in 0..15) {
                var xDev: Double = (craterX - (chunkX + x)).toDouble()
                var zDev: Double = (craterZ - (chunkZ + z)).toDouble()
                if (xDev * xDev + zDev * zDev < size * size) {
                    xDev /= size.toDouble()
                    zDev /= size.toDouble()
                    val sqrtY: Double = xDev * xDev + zDev * zDev
                    var yDev: Double = sqrtY * sqrtY * 6
                    yDev = 5 - yDev
                    var helper: Int = 0
                    for (y: Int in 180 downTo 51) {
                        if (!primer.getBlockState(x, y, z).getMaterial()
                                .isLiquid() && Blocks.AIR !== primer.getBlockState(x, y, z).getBlock() && helper <= yDev
                        ) {
                            primer.setBlockState(x, y, z, Blocks.AIR.getDefaultState())
                            //                            chunkArray[this.getIndex(x, y, z)] = Blocks.AIR;
//                            metaArray[this.getIndex(x, y, z)] = 0;
                            helper++
                        }

                        if (helper > yDev) {
                            break
                        }
                    }
                }
            }
        }
    }

    private fun randFromPoint(x: Int, z: Int): Double {
        var n: Int
        n = x + z * 57
        n = n shl 13 xor n
        return 1.0 - (n * (n * n * 15731 + 789221) + 1376312589 and 0x7fffffff) / 1073741824.0
    }
}
