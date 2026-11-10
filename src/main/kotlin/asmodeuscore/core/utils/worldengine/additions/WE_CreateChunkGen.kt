package asmodeuscore.core.utils.worldengine.additions

import asmodeuscore.core.utils.worldengine.WE_Biome
import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen
import asmodeuscore.core.utils.worldengine.additions.WE_GeneratorData
import net.minecraft.block.state.IBlockState

abstract class WE_CreateChunkGen {
    abstract fun gen(var1: WE_GeneratorData?)

    fun setBlock(data: WE_GeneratorData, state: IBlockState, x_in_chunk: Int, y_in_chunk: Int, z_in_chunk: Int) {
        (data.primer)!!.setBlockState(x_in_chunk, y_in_chunk, z_in_chunk, state)
    }

    fun getBlock(data: WE_GeneratorData, x_in_chunk: Int, y_in_chunk: Int, z_in_chunk: Int): IBlockState {
        return (data.primer)!!.getBlockState(x_in_chunk, y_in_chunk, z_in_chunk)
    }

    fun getBiome(data: WE_GeneratorData, x_in_chunk: Int, z_in_chunk: Int): WE_Biome? {
        return (((data)!!.biomes)!!)!![x_in_chunk]!![z_in_chunk]
    }
}
