package micdoodle8.mods.galacticraft.core.world.gen

import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeAdaptive
import micdoodle8.mods.galacticraft.api.prefab.world.gen.MapGenBaseMeta
import micdoodle8.mods.galacticraft.api.world.ChunkProviderBase
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.blocks.BlockBasicMoon
import micdoodle8.mods.galacticraft.core.perlin.NoiseModule
import micdoodle8.mods.galacticraft.core.perlin.generator.Gradient
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.DungeonConfiguration
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.MapGenDungeon
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.RoomBoss
import micdoodle8.mods.galacticraft.core.world.gen.dungeon.RoomTreasure
import net.minecraft.block.BlockFalling
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EnumCreatureType
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ChunkPrimer
import java.util.Random
import kotlin.math.abs

open class ChunkProviderMoon constructor(private val world: World, par2: Long, par4: Boolean) :
    ChunkProviderBase() {
    private val rand: Random

    private val noiseGen1: NoiseModule
    private val noiseGen2: NoiseModule
    private val noiseGen3: NoiseModule
    private val noiseGen4: NoiseModule

    private val villageGenerator = MapGenVillageMoon()

    private val dungeonGeneratorMoon = MapGenDungeon(
        DungeonConfiguration(
            (GCBlocks.blockMoon)!!.getDefaultState()
                .withProperty(
                    BlockBasicMoon.BASIC_TYPE_MOON,
                    BlockBasicMoon.EnumBlockBasicMoon.MOON_DUNGEON_BRICK
                ), 25, 8, 16, 5, 6, RoomBoss::class.java, RoomTreasure::class.java
        )
    )

    private val biomesForGeneration = arrayOf<Biome?>(BiomeAdaptive.biomeDefault)

    private val caveGenerator: MapGenBaseMeta = MapGenCavesMoon()

    init {
        this.rand = Random(par2)
        this.noiseGen1 = Gradient(this.rand.nextLong(), 4, 0.25f)
        this.noiseGen2 = Gradient(this.rand.nextLong(), 4, 0.25f)
        this.noiseGen3 = Gradient(this.rand.nextLong(), 1, 0.25f)
        this.noiseGen4 = Gradient(this.rand.nextLong(), 1, 0.25f)
    }

    open fun setBlocksInChunk(chunkX: Int, chunkZ: Int, primer: ChunkPrimer) {
        this.noiseGen1.setFrequency(0.0125f)
        this.noiseGen2.setFrequency(0.015f)
        this.noiseGen3.setFrequency(0.01f)
        this.noiseGen4.setFrequency(0.02f)

        for (x: Int in 0..<CHUNK_SIZE_X) {
            for (z: Int in 0..<CHUNK_SIZE_Z) {
                val d: Double =
                    (this.noiseGen1.getNoise((x + chunkX * 16).toFloat(), (z + chunkZ * 16).toFloat()) * 8).toDouble()
                val d2: Double =
                    (this.noiseGen2.getNoise((x + chunkX * 16).toFloat(), (z + chunkZ * 16).toFloat()) * 24).toDouble()
                var d3: Double = this.noiseGen3.getNoise((x + chunkX * 16).toFloat(), (z + chunkZ * 16).toFloat()) - 0.1
                d3 *= 4.0

                val yDev: Double

                if (d3 < 0.0) {
                    yDev = d
                } else if (d3 > 1.0) {
                    yDev = d2
                } else {
                    yDev = d + (d2 - d) * d3
                }

                for (y: Int in 0..<CHUNK_SIZE_Y) {
                    if (y < MID_HEIGHT + yDev) {
                        primer.setBlockState(x, y, z, BLOCK_LOWER)
                    }
                }
            }
        }
    }

    open fun replaceBlocksForBiome(par1: Int, par2: Int, primer: ChunkPrimer, par4ArrayOfBiome: Array<Biome?>?) {
        val var5: Int = 20
        for (var8: Int in 0..15) {
            for (var9: Int in 0..15) {
                val var12: Int = (this.noiseGen4.getNoise(
                    (var8 + par1 * 16).toFloat(),
                    (var9 * par2 * 16).toFloat()
                ) / 3.0 + 3.0 + this.rand.nextDouble() * 0.25).toInt()
                var var13: Int = -1
                var state0: IBlockState = BLOCK_TOP
                var state1: IBlockState = BLOCK_FILL

                for (var16: Int in 127 downTo 0) {
                    val index: Int = this.getIndex(var8, var16, var9)

                    if (var16 <= this.rand.nextInt(5)) {
                        primer.setBlockState(var8, var16, var9, Blocks.BEDROCK.getDefaultState())
                    } else {
                        val var18: IBlockState = primer.getBlockState(var8, var16, var9)
                        if (Blocks.AIR === var18) {
                            var13 = -1
                        } else if (var18 === BLOCK_LOWER) {
                            if (var13 == -1) {
                                if (var12 <= 0) {
                                    state0 = Blocks.AIR.getDefaultState()
                                    state1 = BLOCK_LOWER
                                } else if (var16 >= var5 - -16 && var16 <= var5 + 1) {
                                    state0 = BLOCK_FILL
                                }

                                var13 = var12

                                if (var16 >= var5 - 1) {
                                    primer.setBlockState(var8, var16, var9, state0)
                                } else if (var16 < var5 - 1 && var16 >= var5 - 2) {
                                    primer.setBlockState(var8, var16, var9, state1)
                                }
                            } else if (var13 > 0) {
                                --var13
                                primer.setBlockState(var8, var16, var9, state1)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun generateChunk(x: Int, z: Int): Chunk {
        this.rand.setSeed(x.toLong() * 341873128712L + z.toLong() * 132897987541L)
        val chunkprimer: ChunkPrimer = ChunkPrimer()
        this.setBlocksInChunk(x, z, chunkprimer)
        this.createCraters(x, z, chunkprimer)
        this.replaceBlocksForBiome(x, z, chunkprimer, null)

        this.caveGenerator.generate(this.world, x, z, chunkprimer)

        this.dungeonGeneratorMoon.generate(this.world, x, z, chunkprimer)
        this.villageGenerator.generate(this.world, x, z, chunkprimer)

        val chunk: Chunk = Chunk(this.world, chunkprimer, x, z)
        val abyte: ByteArray = chunk.getBiomeArray()
        val b: Byte = Biome.getIdForBiome(BiomeAdaptive.biomeDefault).toByte()
        for (i: Int in abyte.indices) {
            abyte[i] = b
        }

        chunk.generateSkylightMap()
        return chunk
    }

    private fun createCraters(chunkX: Int, chunkZ: Int, primer: ChunkPrimer) {
        for (cx: Int in chunkX - 2..chunkX + 2) {
            for (cz: Int in chunkZ - 2..chunkZ + 2) {
                for (x: Int in 0..<CHUNK_SIZE_X) {
                    for (z: Int in 0..<CHUNK_SIZE_Z) {
                        if (abs(
                                this.randFromPoint(
                                    cx * 16 + x,
                                    (cz * 16 + z) * 1000
                                )
                            ) < this.noiseGen4.getNoise(
                                (x * CHUNK_SIZE_X + x).toFloat(),
                                (cz * CHUNK_SIZE_Z + z).toFloat()
                            ) / CRATER_PROB
                        ) {
                            val random: Random = Random((cx * 16 + x + (cz * 16 + z) * 5000).toLong())
                            val cSize: EnumCraterSize =
                                EnumCraterSize.Companion.sizeArray[random.nextInt(EnumCraterSize.Companion.sizeArray.size)]
                            val size: Int = random.nextInt(cSize.MAX_SIZE - cSize.MIN_SIZE) + cSize.MIN_SIZE
                            this.makeCrater(cx * 16 + x, cz * 16 + z, chunkX * 16, chunkZ * 16, size, primer)
                        }
                    }
                }
            }
        }
    }

    private fun makeCrater(craterX: Int, craterZ: Int, chunkX: Int, chunkZ: Int, size: Int, primer: ChunkPrimer) {
        for (x: Int in 0..<CHUNK_SIZE_X) {
            for (z: Int in 0..<CHUNK_SIZE_Z) {
                var xDev: Double = (craterX - (chunkX + x)).toDouble()
                var zDev: Double = (craterZ - (chunkZ + z)).toDouble()
                if (xDev * xDev + zDev * zDev < size * size) {
                    xDev /= size.toDouble()
                    zDev /= size.toDouble()
                    val sqrtY: Double = xDev * xDev + zDev * zDev
                    var yDev: Double = sqrtY * sqrtY * 6
                    yDev = 5 - yDev
                    var helper: Int = 0
                    for (y: Int in 127 downTo 1) {
                        if (Blocks.AIR !== primer.getBlockState(x, y, z).getBlock() && helper <= yDev) {
                            primer.setBlockState(x, y, z, Blocks.AIR.getDefaultState())
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

    private fun getIndex(x: Int, y: Int, z: Int): Int {
        return (x * 16 + z) * 256 + y
    }

    private fun randFromPoint(x: Int, z: Int): Double {
        var n: Int
        n = x + z * 57
        n = n shl 13 xor n
        return 1.0 - (n * (n * n * 15731 + 789221) + 1376312589 and 0x7fffffff) / 1073741824.0
    }

    override fun populate(x: Int, z: Int) {
        BlockFalling.fallInstantly = true
        val i: Int = x * 16
        val j: Int = z * 16
        val blockpos: BlockPos = BlockPos(i, 0, j)
        val biomegenbase: Biome = this.world.getBiome(blockpos.add(16, 0, 16))
        this.rand.setSeed(this.world.getSeed())
        val k: Long = this.rand.nextLong() / 2L * 2L + 1L
        val l: Long = this.rand.nextLong() / 2L * 2L + 1L
        this.rand.setSeed(x.toLong() * k + z.toLong() * l xor this.world.getSeed())

        if (!ConfigManagerCore.disableMoonVillageGen) {
            this.villageGenerator.generateStructure(this.world, this.rand, ChunkPos(x, z))
        }

        this.dungeonGeneratorMoon.generateStructure(this.world, this.rand, ChunkPos(x, z))

        biomegenbase.decorate(this.world, this.rand, BlockPos(i, 0, j))
        BlockFalling.fallInstantly = false
    }

    override fun getPossibleCreatures(
        creatureType: EnumCreatureType,
        pos: BlockPos
    ): MutableList<Biome.SpawnListEntry?> {
        val biomegenbase: Biome = this.world.getBiome(pos)
        return biomegenbase.getSpawnableList(creatureType)
    }

    override fun recreateStructures(chunk: Chunk, x: Int, z: Int) {
        if (!ConfigManagerCore.disableMoonVillageGen) {
            this.villageGenerator.generate(this.world, x, z, null)
        }

        this.dungeonGeneratorMoon.generate(this.world, x, z, null)
    }

    companion object {
        @JvmField
        val BLOCK_TOP: IBlockState = (GCBlocks.blockMoon)!!.getDefaultState()
            .withProperty(
                BlockBasicMoon.BASIC_TYPE_MOON,
                BlockBasicMoon.EnumBlockBasicMoon.MOON_TURF
            )

        @JvmField
        val BLOCK_FILL: IBlockState = (GCBlocks.blockMoon)!!.getDefaultState()
            .withProperty(
                BlockBasicMoon.BASIC_TYPE_MOON,
                BlockBasicMoon.EnumBlockBasicMoon.MOON_DIRT
            )

        @JvmField
        val BLOCK_LOWER: IBlockState = (GCBlocks.blockMoon)!!.getDefaultState()
            .withProperty(
                BlockBasicMoon.BASIC_TYPE_MOON,
                BlockBasicMoon.EnumBlockBasicMoon.MOON_STONE
            )

        private const val CRATER_PROB = 300

        // DO NOT CHANGE
        private const val MID_HEIGHT = 63
        private const val CHUNK_SIZE_X = 16
        private const val CHUNK_SIZE_Y = 128
        private const val CHUNK_SIZE_Z = 16
    }
}
