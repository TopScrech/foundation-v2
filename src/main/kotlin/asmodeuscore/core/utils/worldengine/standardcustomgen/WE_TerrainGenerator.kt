package asmodeuscore.core.utils.worldengine.standardcustomgen

import foundation.math.LegacyTerrainMath

import asmodeuscore.core.utils.worldengine.WE_Biome
import asmodeuscore.core.utils.worldengine.additions.WE_CreateChunkGen
import asmodeuscore.core.utils.worldengine.additions.WE_GeneratorData
import asmodeuscore.core.utils.worldengine.perlinnoise.PerlinNoise
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.util.Mth as MathHelper

open class WE_TerrainGenerator : WE_CreateChunkGen() {
    @JvmField
    var worldStoneBlock: IBlockState = Blocks.STONE.defaultBlockState()

    @JvmField
    var worldSeaGenBlock: IBlockState = Blocks.WATER.defaultBlockState()

    @JvmField
    var worldSeaGen: Boolean = true

    @JvmField
    var worldSeaGenMaxY: Int = 63

    override fun gen(data: WE_GeneratorData?) {
        val q: Int = WE_Biome.getBiggestInterpolationQuality(((data)!!.chunkProvider)!!)
        val bl: Array<Array<WE_Biome?>?> = Array<Array<WE_Biome?>?>(16 + q * 2) { arrayOfNulls<WE_Biome>(16 + q * 2) }
        var sb: WE_Biome? = null
        var ni: Boolean = false

        for (xm: Int in 0..<16 + q * 2) {
            for (zm: Int in 0..<16 + q * 2) {
                if (xm > q - 1 && zm > q - 1 && xm < 16 + q && zm < 16 + q) {
                    bl[xm]!![zm] = this.getBiome((data)!!, xm - q, zm - q)
                } else {
                    bl[xm]!![zm] = WE_Biome.getBiomeAt(
                        ((data)!!.chunkProvider)!!,
                        (data)!!.chunk_X - q.toLong() + xm.toLong(),
                        (data)!!.chunk_Z - q.toLong() + zm.toLong()
                    )
                }

                if (!ni) {
                    if (sb == null) {
                        sb = bl[xm]!![zm]
                    } else if (sb.id != bl[xm]!![zm]!!.id) {
                        ni = true
                    }
                }
            }
        }

        for (x: Int in 0..15) {
            for (z: Int in 0..15) {
                val n: Int = MathHelper.floor(
                    this.interpolatedBlock(
                        (((data)!!.chunkProvider)!!.world)!!.getSeed(),
                        (data)!!.chunk_X,
                        (data)!!.chunk_Z,
                        x,
                        z,
                        bl,
                        ni,
                        q
                    )
                )

                for (y: Int in 0..255) {
                    if (y != 0 && (y > 5 || (((data)!!.chunkProvider)!!.rand)!!.nextInt(y) != 0)) {
                        if (y <= n) {
                            this.setBlock((data)!!, this.worldStoneBlock, x, y, z)
                        } else if (this.worldSeaGen && y <= this.worldSeaGenMaxY) {
                            this.setBlock((data)!!, this.worldSeaGenBlock, x, y, z)
                        }
                    } else {
                        this.setBlock((data)!!, Blocks.BEDROCK.defaultBlockState(), x, y, z)
                    }
                }
            }
        }
    }

    open fun interpolatedBlock(
        seed: Long,
        chunk_X: Long,
        chunk_Z: Long,
        bcx: Int,
        bcz: Int,
        biomeListOfChunk: Array<Array<WE_Biome?>?>,
        doInterpolate: Boolean,
        iQuality: Int
    ): Double {
        val bx: Int = iQuality + bcx
        val bz: Int = iQuality + bcz
        if (!doInterpolate) {
            val noise: PerlinNoise = PerlinNoise(
                seed,
                biomeListOfChunk[bx]!![bz]!!.biomePersistence,
                biomeListOfChunk[bx]!![bz]!!.biomeNumberOfOctaves,
                biomeListOfChunk[bx]!![bz]!!.biomeScaleX,
                biomeListOfChunk[bx]!![bz]!!.biomeScaleY,
                biomeListOfChunk[bx]!![bz]!!.biomeSurfaceHeight
            )
            return noise.genNoise2d((chunk_X + bcx.toLong()).toDouble(), (chunk_Z + bcz.toLong()).toDouble())
        } else {
            var genPersistence: Double = 0.0
            var genScaleX: Double = 0.0
            var genScaleY: Double = 0.0
            var genNumberOfOctaves: Int = 0
            var genSurfaceHeight: Int = 0
            var c: Int = 0

            for (i: Int in 0..360) {
                for (i2: Int in 0..biomeListOfChunk[bx]!![bz]!!.biomeInterpolateQuality) {
                    val cbx: Int =
                        MathHelper.floor(bx.toFloat() + LegacyTerrainMath.cos(i.toFloat() * Math.PI.toFloat() / 180.0f) * i2.toFloat())
                    val cbz: Int =
                        MathHelper.floor(bz.toFloat() + LegacyTerrainMath.sin(i.toFloat() * Math.PI.toFloat() / 180.0f) * i2.toFloat())
                    c++
                    genPersistence += biomeListOfChunk[cbx]!![cbz]!!.biomePersistence
                    genNumberOfOctaves += biomeListOfChunk[cbx]!![cbz]!!.biomeNumberOfOctaves
                    genScaleX += biomeListOfChunk[cbx]!![cbz]!!.biomeScaleX
                    genScaleY += biomeListOfChunk[cbx]!![cbz]!!.biomeScaleY
                    genSurfaceHeight += biomeListOfChunk[cbx]!![cbz]!!.biomeSurfaceHeight
                }
            }

            genPersistence /= c.toDouble()
            genScaleX /= c.toDouble()
            genScaleY /= c.toDouble()
            genNumberOfOctaves = MathHelper.floor((genNumberOfOctaves / c).toFloat())
            genSurfaceHeight = MathHelper.floor((genSurfaceHeight / c).toFloat())
            val noise: PerlinNoise =
                PerlinNoise(seed, genPersistence, genNumberOfOctaves, genScaleX, genScaleY, genSurfaceHeight)
            return noise.genNoise2d((chunk_X + bcx.toLong()).toDouble(), (chunk_Z + bcz.toLong()).toDouble())
        }
    }
}
