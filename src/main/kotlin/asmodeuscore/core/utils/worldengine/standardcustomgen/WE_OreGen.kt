package asmodeuscore.core.utils.worldengine.standardcustomgen

import java.util.ArrayList
import java.util.Random

import com.google.common.base.Predicate
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraft.world.gen.feature.WorldGenMinable
import net.minecraftforge.fml.common.IWorldGenerator
import java.util.*

open class WE_OreGen : IWorldGenerator {
    @JvmField
    var fieldMinY: MutableList<Int> = ArrayList<Int>()

    @JvmField
    var fieldMaxY: MutableList<Int> = ArrayList<Int>()

    @JvmField
    var fieldMinRadius: MutableList<Int> = ArrayList<Int>()

    @JvmField
    var fieldMaxRadius: MutableList<Int> = ArrayList<Int>()

    @JvmField
    var oreInMaxFieldDensity: MutableList<Int> = ArrayList<Int>()

    @JvmField
    var chunksForField: MutableList<Int> = ArrayList<Int>()

    @JvmField
    var blockCount: MutableList<Int> = ArrayList<Int>()

    @JvmField
    var oreGen: MutableList<WorldGenMinable?> = ArrayList<WorldGenMinable?>()

    open fun add(
        oreBlock: IBlockState,
        replacingBlock: IBlockState?,
        blockCount: Int,
        minY: Int,
        maxY: Int,
        chunksForField: Int
    ) {
        this.oreGen.add(WorldGenMinable(oreBlock, blockCount, BlockPredicate(replacingBlock)))
        this.fieldMinY.add(minY)
        this.fieldMaxY.add(maxY)
        this.blockCount.add(blockCount)
        this.chunksForField.add(chunksForField)
    }

    override fun generate(
        random: Random,
        chunkX: Int,
        chunkZ: Int,
        world: World,
        chunkGenerator: IChunkGenerator?,
        chunkProvider: IChunkProvider?
    ) {
        for (i: Int in this.oreGen.indices) {
            for (j: Int in 0..<this.chunksForField.get(i)!!) {
                val sy: Int =
                    this.fieldMinY.get(i)!! + random.nextInt(this.fieldMaxY.get(i)!! - this.fieldMinY.get(i)!!)
                val sx: Int = chunkX + random.nextInt(16)
                val sz: Int = chunkZ + random.nextInt(16)
                this.oreGen.get(i)!!.generate(world, random, BlockPos(sx, sy, sz))
            }
        }
    }

    open class BlockPredicate constructor(vararg state: IBlockState?) : Predicate<IBlockState> {
        @JvmField
        var states: MutableList<IBlockState?> = ArrayList<IBlockState?>()

        init {
            for (st: IBlockState? in state) {
                this.states.add(st)
            }
        }

        override fun apply(p_apply_1_: IBlockState?): Boolean {
            return if (p_apply_1_ != null && this.states.contains(p_apply_1_)) p_apply_1_.isNormalCube() else false
        }
    }
}
