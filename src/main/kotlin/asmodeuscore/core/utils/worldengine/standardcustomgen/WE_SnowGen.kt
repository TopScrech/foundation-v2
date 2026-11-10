package asmodeuscore.core.utils.worldengine.standardcustomgen

import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen_InXZ
import asmodeuscore.core.utils.worldengine.additions.WE_GeneratorData
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks

open class WE_SnowGen : WE_CreateChunkGen_InXZ() {
    @JvmField
    var snowPoint: Int = 111

    @JvmField
    var randomSnowPoint: Int = 2

    @JvmField
    var snowBlock: IBlockState = Blocks.SNOW_LAYER.getDefaultState()

    @JvmField
    var genSnow: Boolean = true

    @JvmField
    var iceBlock: IBlockState = Blocks.ICE.getDefaultState()

    @JvmField
    var freezeMaterial: Material? = Material.WATER

    @JvmField
    var snowOnWaterRandom: Int = 2

    override fun gen(data: WE_GeneratorData?) {
        for (y: Int in 255 downTo this.snowPoint + (((data)!!.chunkProvider)!!.rand)!!.nextInt(this.randomSnowPoint + 1)) {
            if (this.getBlock((data)!!, y).getBlock() !== Blocks.AIR) {
                if (this.getBlock((data)!!, y).getMaterial() === this.freezeMaterial) {
                    this.setBlock((data)!!, this.iceBlock, y)
                    if (this.genSnow && (((data)!!.chunkProvider)!!.rand)!!.nextInt(this.snowOnWaterRandom + 1) == 0) {
                        this.setBlock((data)!!, this.snowBlock, y + 1)
                    }
                } else if (this.genSnow) {
                    this.setBlock((data)!!, this.snowBlock, y + 1)
                }
                break
            }
        }
    }
}
