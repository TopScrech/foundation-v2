package galaxyspace.systems.SolarSystem.satellites.jupiter.dimension

import net.minecraft.world.biome.Biome.SpawnListEntry

import galaxyspace.systems.SolarSystem.satellites.jupiter.dimension.ChunkProviderJupiterSS
import galaxyspace.systems.SolarSystem.satellites.mars.world.gen.WorldGenSpaceStation
import micdoodle8.mods.galacticraft.api.world.ChunkProviderBase
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.tile.IMultiBlock
import micdoodle8.mods.galacticraft.core.world.gen.BiomeOrbit
import net.minecraft.block.BlockFalling
import net.minecraft.entity.EnumCreatureType
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.chunk.LevelChunk as Chunk
import net.minecraft.world.chunk.ChunkPrimer
import java.util.Random

open class ChunkProviderJupiterSS constructor(private val world: World, par2: Long, par4: Boolean) :
    ChunkProviderBase() {
    private val rand: Random

    init {
        this.rand = Random(par2)
    }

    override fun generateChunk(par1: Int, par2: Int): Chunk {
        val chunkprimer: ChunkPrimer = ChunkPrimer()
        this.rand.setSeed(par1 * 341873128712L + par2 * 132897987541L)

        val var4: Chunk = Chunk(this.world, chunkprimer, par1, par2)

        val b: Byte = Biome.getIdForBiome(BiomeOrbit.space).toByte()
        val biomesArray: ByteArray = var4.getBiomeArray()
        for (i: Int in biomesArray.indices) {
            biomesArray[i] = b
        }

        var4.generateSkylightMap()
        return var4
    }

    override fun populate(x: Int, z: Int) {
        BlockFalling.fallInstantly = true
        val k: Int = x * 16
        val l: Int = z * 16
        this.rand.setSeed(this.world.getSeed())
        val i1: Long = this.rand.nextLong() / 2L * 2L + 1L
        val j1: Long = this.rand.nextLong() / 2L * 2L + 1L
        this.rand.setSeed(x * i1 + z * j1 xor this.world.getSeed())
        if (k == 0 && l == 0) {
            val pos: BlockPos = BlockPos(k, 64, l)
            this.world.setBlock(pos, (GCBlocks.spaceStationBase)!!.defaultBlockState(), 2)

            val var8: TileEntity? = this.world.getBlockEntity(pos)

            if (var8 is IMultiBlock) {
                (var8 as IMultiBlock).onCreate(this.world, pos)
            }

            WorldGenSpaceStation().generate(this.world, this.rand, BlockPos(k - 10, 62, l - 3))
        }
        BlockFalling.fallInstantly = false
    }

    override fun getPossibleCreatures(
        creatureType: EnumCreatureType,
        pos: BlockPos
    ): MutableList<Biome.SpawnListEntry?>? {
        return null
    }

    override fun recreateStructures(p_180514_1_: Chunk, p_180514_2_: Int, p_180514_3_: Int) {
    }
}
