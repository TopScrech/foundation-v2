package asmodeuscore.core.utils.worldengine.standardcustomgen

import java.util.ArrayList

import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen_InXZ
import asmodeuscore.core.utils.worldengine.additions.WE_GeneratorData
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import kotlin.math.abs

open class WE_BiomeLayer : WE_CreateChunkGen_InXZ() {
    @JvmField
    var layerBlock: MutableList<IBlockState?> = ArrayList<IBlockState?>()

    @JvmField
    var layerReplacingBlock: MutableList<IBlockState?> = ArrayList<IBlockState?>()

    @JvmField
    var layerStart: MutableList<Int> = ArrayList<Int>()

    @JvmField
    var layerRandomStart: MutableList<Int> = ArrayList<Int>()

    @JvmField
    var layerEnd: MutableList<Int> = ArrayList<Int>()

    @JvmField
    var layerRandomEnd: MutableList<Int> = ArrayList<Int>()

    @JvmField
    var layerUnderWaterGen: MutableList<Boolean> = ArrayList<Boolean>()

    open fun add(
        block: IBlockState?,
        replacingBlock: IBlockState?,
        start: Int,
        r_start: Int,
        end: Int,
        r_end: Int,
        underWater: Boolean
    ) {
        this.layerBlock.add(block)
        this.layerReplacingBlock.add(replacingBlock)
        this.layerStart.add(start)
        this.layerRandomStart.add(r_start)
        this.layerEnd.add(end)
        this.layerRandomEnd.add(r_end)
        this.layerUnderWaterGen.add(underWater)
    }

    open fun add(block: IBlockState?, start: Int, r_start: Int, end: Int, r_end: Int, underWater: Boolean) {
        this.layerBlock.add(block)
        this.layerReplacingBlock.add(null)
        this.layerStart.add(start)
        this.layerRandomStart.add(r_start)
        this.layerEnd.add(end)
        this.layerRandomEnd.add(r_end)
        this.layerUnderWaterGen.add(underWater)
    }

    override fun gen(data: WE_GeneratorData?) {
        for (i: Int in this.layerBlock.indices) {
            var startPoint: Int = this.layerStart.get(i)!!
            var endPoint: Int = this.layerEnd.get(i)!!
            if (this.layerStart.get(i)!! < 0 || this.layerEnd.get(i)!! < 0) {
                if (this.layerStart.get(i)!! < -255) {
                    startPoint = abs(this.layerStart.get(i)!! % 256)
                }

                if (this.layerEnd.get(i)!! < -255) {
                    endPoint = abs(this.layerEnd.get(i)!! % 256)
                }

                for (y: Int in 255 downTo 0) {
                    if (this.getBlock((data)!!, y).getMaterial() !== Material.AIR && !this.getBlock((data)!!, y).getMaterial()
                            .isLiquid()
                    ) {
                        if (this.layerStart.get(i)!! < 0) {
                            startPoint += y
                        }

                        if (this.layerEnd.get(i)!! < 0) {
                            endPoint += y
                        }
                        break
                    }
                }
            }

            if (this.layerRandomStart.get(i)!! > 0) {
                startPoint += (((data)!!.chunkProvider)!!.rand)!!.nextInt(this.layerRandomStart.get(i)!! + 1)
            } else if (this.layerRandomStart.get(i)!! < 0) {
                startPoint -= (((data)!!.chunkProvider)!!.rand)!!.nextInt(abs(this.layerRandomStart.get(i)!!) + 1)
            }

            if (this.layerRandomEnd.get(i)!! > 0) {
                endPoint += (((data)!!.chunkProvider)!!.rand)!!.nextInt(this.layerRandomEnd.get(i)!! + 1)
            } else if (this.layerRandomEnd.get(i)!! < 0) {
                endPoint -= (((data)!!.chunkProvider)!!.rand)!!.nextInt(abs(this.layerRandomEnd.get(i)!!) + 1)
            }

            if (!this.layerUnderWaterGen.get(i)!! && this.getBlock(
                    (data)!!,
                    startPoint + 1
                ) !== Blocks.AIR.getDefaultState() && this.getBlock((data)!!, startPoint + 1).getMaterial().isLiquid()
            ) {
                return
            }

            if (this.layerReplacingBlock.get(i) != null) {
                for (yx: Int in startPoint downTo endPoint) {
                    if (yx >= 0 && this.getBlock((data)!!, yx) === this.layerReplacingBlock.get(i)) {
                        this.setBlock((data)!!, (this.layerBlock.get(i))!!, yx)
                    }
                }
            } else {
                for (yxx: Int in startPoint downTo endPoint) {
                    this.setBlock((data)!!, (this.layerBlock.get(i))!!, yxx)
                }
            }
        }
    }
}
