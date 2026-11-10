package micdoodle8.mods.galacticraft.core.world.gen

import net.minecraft.world.biome.Biome.SpawnListEntry

import micdoodle8.mods.galacticraft.api.prefab.world.gen.BiomeAdaptive
import micdoodle8.mods.galacticraft.api.world.ChunkProviderBase
import micdoodle8.mods.galacticraft.core.GCBlocks
import micdoodle8.mods.galacticraft.core.tile.IMultiBlock
import net.minecraft.block.BlockFalling
import net.minecraft.entity.EnumCreatureType
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.biome.Biome
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.chunk.ChunkPrimer
import java.util.Random

open class ChunkProviderOrbit constructor(private val world: World, par2: Long, par4: Boolean) :
    ChunkProviderBase() {
    private val rand: Random

    init {
        this.rand = Random(par2)
    }

    override fun generateChunk(par1: Int, par2: Int): Chunk {
        val chunkprimer: ChunkPrimer = ChunkPrimer()
        this.rand.setSeed(par1 * 341873128712L + par2 * 132897987541L)

        val var4: Chunk = Chunk(this.world, chunkprimer, par1, par2)

        val b: Byte = Biome.getIdForBiome(BiomeAdaptive.biomeDefault).toByte()
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
            this.world.setBlockState(pos, (GCBlocks.spaceStationBase)!!.getDefaultState(), 2)

            val var8: TileEntity? = this.world.getTileEntity(pos)

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
