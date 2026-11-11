package asmodeuscore.core.utils.worldengine.standardcustomgen

import java.util.ArrayList
import java.util.Random

import asmodeuscore.core.utils.worldengine.standardcustomgen.help.WE_BigTreeGen
import asmodeuscore.core.utils.worldengine.standardcustomgen.help.WE_TreeGen
import net.minecraft.world.level.block.Block
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.fml.common.IWorldGenerator
import java.util.*

open class WE_WorldTreeGen : IWorldGenerator {
    @JvmField
    var ug: MutableList<WE_TreeGen?> = ArrayList<WE_TreeGen?>()

    @JvmField
    var bg: MutableList<WE_BigTreeGen?> = ArrayList<WE_BigTreeGen?>()

    @JvmField
    var cwt: MutableList<Int> = ArrayList<Int>()

    @JvmField
    var tfc: MutableList<Int> = ArrayList<Int>()

    @JvmField
    var tfb: MutableList<Int> = ArrayList<Int>()

    open fun add(
        bWood: Block?,
        mWood: Int,
        bLeaves: Block?,
        mLeaves: Int,
        bSapling: Block?,
        bVine: Block?,
        bCocoa: Block?,
        v1: Int,
        v2: Int,
        v3: Int,
        minHeight: Int,
        vines: Boolean,
        ocp1: Byte,
        ocp2: Byte,
        ocp3: Byte,
        ocp4: Byte,
        ocp5: Byte,
        ocp6: Byte,
        ts: Int,
        hl: Int,
        ldl: Int,
        ha: Double,
        bs: Double,
        sw: Double,
        ld: Double
    ) {
        this.ug.add(WE_TreeGen(false))
        this.bg.add(WE_BigTreeGen(false))
        this.cwt.add(v1)
        this.tfc.add(v2)
        this.tfb.add(v3)
        this.ug.get(this.ug.size - 1)!!.bWood = (bWood)!!
        this.ug.get(this.ug.size - 1)!!.metaWood = mWood
        this.ug.get(this.ug.size - 1)!!.bLeaves = (bLeaves)!!
        this.ug.get(this.ug.size - 1)!!.metaLeaves = mLeaves
        this.ug.get(this.ug.size - 1)!!.bSapling = (bSapling)!!
        this.ug.get(this.ug.size - 1)!!.bVine = (bVine)!!
        this.ug.get(this.ug.size - 1)!!.bCocoa = bCocoa
        this.bg.get(this.bg.size - 1)!!.bWood = (bWood)!!
        this.bg.get(this.bg.size - 1)!!.bLeaves = (bLeaves)!!
        this.bg.get(this.bg.size - 1)!!.metaLeaves = mLeaves
        this.bg.get(this.bg.size - 1)!!.bSapling = (bSapling)!!
        this.ug.get(this.ug.size - 1)!!.minTreeHeight = minHeight
        this.ug.get(this.ug.size - 1)!!.vinesGrow = vines
        this.bg.get(this.bg.size - 1)!!.otherCoordPairs = byteArrayOf(ocp1, ocp2, ocp3, ocp4, ocp5, ocp6)
        this.bg.get(this.bg.size - 1)!!.trunkSize = ts
        this.bg.get(this.bg.size - 1)!!.heightLimitLimit = hl
        this.bg.get(this.bg.size - 1)!!.leafDistanceLimit = ldl
        this.bg.get(this.bg.size - 1)!!.heightAttenuation = ha
        this.bg.get(this.bg.size - 1)!!.branchSlope = bs
        this.bg.get(this.bg.size - 1)!!.scaleWidth = sw
        this.bg.get(this.bg.size - 1)!!.leafDensity = ld
    }

    open fun add(
        bWood: Block?,
        mWood: Int,
        bLeaves: Block?,
        mLeaves: Int,
        bSapling: Block?,
        bVine: Block?,
        bCocoa: Block?,
        v1: Int,
        v2: Int,
        minHeight: Int,
        vines: Boolean
    ) {
        this.ug.add(WE_TreeGen(false))
        this.bg.add(null)
        this.cwt.add(v1)
        this.tfc.add(v2)
        this.tfb.add(0)
        this.ug.get(this.ug.size - 1)!!.bWood = (bWood)!!
        this.ug.get(this.ug.size - 1)!!.metaWood = mWood
        this.ug.get(this.ug.size - 1)!!.bLeaves = (bLeaves)!!
        this.ug.get(this.ug.size - 1)!!.metaLeaves = mLeaves
        this.ug.get(this.ug.size - 1)!!.bSapling = (bSapling)!!
        this.ug.get(this.ug.size - 1)!!.bVine = (bVine)!!
        this.ug.get(this.ug.size - 1)!!.bCocoa = bCocoa
        this.ug.get(this.ug.size - 1)!!.minTreeHeight = minHeight
        this.ug.get(this.ug.size - 1)!!.vinesGrow = vines
    }

    override fun generate(
        random: Random,
        chunkX: Int,
        chunkZ: Int,
        world: World,
        chunkGenerator: IChunkGenerator?,
        chunkProvider: IChunkProvider?
    ) {
        for (i: Int in this.ug.indices) {
            for (j: Int in 0..<this.cwt.get(i)!!) {
                for (i2: Int in 0..<this.tfc.get(i)!!) {
                    val x: Int = chunkX + random.nextInt(16) + 8
                    val z: Int = chunkZ + random.nextInt(16) + 8
                    val y: Int = world.getTopSolidOrLiquidBlock(BlockPos(x, 0, z)).getY()
                    if (this.tfb.get(i)!! <= 0) {
                        this.ug.get(i)!!.generate(world, random, BlockPos(x, y, z))
                    } else if (random.nextInt(this.tfb.get(i)!!) == 0) {
                        this.bg.get(i)!!.generate(world, random, BlockPos(x, y, z))
                    } else {
                        this.ug.get(i)!!.generate(world, random, BlockPos(x, y, z))
                    }
                }
            }
        }
    }
}
