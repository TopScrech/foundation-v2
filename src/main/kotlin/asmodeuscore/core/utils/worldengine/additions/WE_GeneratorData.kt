package asmodeuscore.core.utils.worldengine.additions

import asmodeuscore.core.utils.worldengine.WE_Biome
import asmodeuscore.core.utils.worldengine.WE_ChunkProvider
import net.minecraft.world.chunk.ChunkPrimer

open class WE_GeneratorData constructor(
    @JvmField var chunkProvider: WE_ChunkProvider?,
    @JvmField var primer: ChunkPrimer?,
    @JvmField var chunk_X: Long,
    @JvmField var chunk_Z: Long,
    @JvmField var biomes: Array<Array<WE_Biome?>?>?,
    @JvmField var cr_x: Int,
    @JvmField var cr_y: Int,
    @JvmField var cr_z: Int
)
