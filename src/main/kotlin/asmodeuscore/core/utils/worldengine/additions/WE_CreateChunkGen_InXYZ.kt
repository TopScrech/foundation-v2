package asmodeuscore.core.utils.worldengine.additions

import asmodeuscore.core.utils.worldengine.WE_Biome
import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen_InXYZ
import asmodeuscore.core.utils.worldengine.additions.WE_GeneratorData
import net.minecraft.block.state.IBlockState

abstract class WE_CreateChunkGen_InXYZ {
    abstract fun gen(var1: WE_GeneratorData?)

    fun setBlock(data: WE_GeneratorData, state: IBlockState) {
        (data.primer)!!.setBlockState(data.cr_x, data.cr_y, data.cr_z, state)
    }

    fun getBlock(data: WE_GeneratorData): IBlockState {
        return (data.primer)!!.getBlockState(data.cr_x, data.cr_y, data.cr_z)
    }

    fun getBiome(data: WE_GeneratorData): WE_Biome? {
        return (((data)!!.biomes)!!)!![data.cr_x]!![data.cr_z]
    }
}
