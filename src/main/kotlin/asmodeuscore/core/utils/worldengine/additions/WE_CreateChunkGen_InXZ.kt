package asmodeuscore.core.utils.worldengine.additions

import asmodeuscore.core.utils.worldengine.WE_Biome
import net.minecraft.world.level.block.state.BlockState as IBlockState

abstract class WE_CreateChunkGen_InXZ {
    abstract fun gen(var1: WE_GeneratorData?)

    fun setBlock(data: WE_GeneratorData, state: IBlockState, y_in_chunk: Int) {
        (data.primer)!!.setBlockState(data.cr_x, y_in_chunk, data.cr_z, state)
    }

    fun getBlock(data: WE_GeneratorData, y_in_chunk: Int): IBlockState {
        return (data.primer)!!.getBlockState(data.cr_x, y_in_chunk, data.cr_z)
    }

    fun getBiome(data: WE_GeneratorData): WE_Biome? {
        return (((data)!!.biomes)!!)!![data.cr_x]!![data.cr_z]
    }
}
