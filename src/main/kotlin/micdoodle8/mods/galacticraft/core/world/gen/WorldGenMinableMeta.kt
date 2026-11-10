package micdoodle8.mods.galacticraft.core.world.gen

import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.block.state.pattern.BlockMatcher
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.MathHelper
import net.minecraft.world.World
import net.minecraft.world.gen.feature.WorldGenMinable
import java.util.Random

open class WorldGenMinableMeta constructor(
    private val minableBlockId: Block,
    private val numberOfBlocks: Int,
    private val metadata: Int,
    metaActive: Boolean,
    replaceBlock: Block,
    replaceMeta: Int
) : WorldGenMinable(minableBlockId.getStateFromMeta(metadata), numberOfBlocks, BlockMatcher.forBlock(replaceBlock)) {
    private var usingMetadata = false

    private val fillerID: Block

    private val fillerMetadata: Int

    init {
        this.usingMetadata = metaActive
        this.fillerID = replaceBlock
        this.fillerMetadata = replaceMeta
    }

    override fun generate(worldIn: World, rand: Random, position: BlockPos): Boolean {
        val f: Float = rand.nextFloat() * Math.PI.toFloat()
        val sinFvalue: Float = MathHelper.sin(f) * this.numberOfBlocks.toFloat() / 8.0f
        val cosFvalue: Float = MathHelper.cos(f) * this.numberOfBlocks.toFloat() / 8.0f
        val chunkCentreX: Float = (position.getX() + 8).toFloat()
        val chunkCentreZ: Float = (position.getZ() + 8).toFloat()
        val clumpXa: Double = (chunkCentreX + sinFvalue).toDouble()
        val clumpXb: Double = (chunkCentreX - sinFvalue).toDouble() - clumpXa
        val clumpZa: Double = (chunkCentreZ + cosFvalue).toDouble()
        val clumpZb: Double = (chunkCentreZ - cosFvalue).toDouble() - clumpZa
        val clumpYa: Double = (position.getY() + rand.nextInt(3) - 2).toDouble()
        val clumpYb: Double = (position.getY() + rand.nextInt(3) - 2).toDouble() - clumpYa

        val oreState: IBlockState = this.minableBlockId.getStateFromMeta(if (this.usingMetadata) this.metadata else 0)
        val concentricRadius: Float = this.numberOfBlocks.toFloat()
        val size: Double = (rand.nextDouble() * this.numberOfBlocks.toDouble() + 1.0) / 16.0

        for (i: Int in 0..<this.numberOfBlocks) {
            val f1: Float = i.toFloat() / concentricRadius
            var centreX: Double = clumpXa + clumpXb * f1.toDouble()
            var centreY: Double = clumpYa + clumpYb * f1.toDouble()
            var centreZ: Double = clumpZa + clumpZb * f1.toDouble()
            val sizeXZ: Double = ((MathHelper.sin(Math.PI.toFloat() * f1) + 1.0f).toDouble() * size + 1.0) / 2.0
            val sizeY: Double = sizeXZ
            val xmin: Int = MathHelper.floor(centreX - sizeXZ)
            val xmax: Int = MathHelper.floor(centreX + sizeXZ)
            val ymin: Int = MathHelper.floor(centreY - sizeY)
            val ymax: Int = MathHelper.floor(centreY + sizeY)
            val zmin: Int = MathHelper.floor(centreZ - sizeXZ)
            val zmax: Int = MathHelper.floor(centreZ + sizeXZ)
            centreX -= 0.5
            centreY -= 0.5
            centreZ -= 0.5

            for (x: Int in xmin..xmax) {
                val dx: Double = (x.toDouble() - centreX) / sizeXZ

                if (dx * dx < 1.0) {
                    for (y: Int in ymin..ymax) {
                        val dy: Double = (y.toDouble() - centreY) / sizeY
                        val xySquared: Double = dx * dx + dy * dy

                        if (xySquared < 1.0) {
                            for (z: Int in zmin..zmax) {
                                val dz: Double = (z.toDouble() - centreZ) / sizeXZ

                                if (xySquared + dz * dz < 1.0) {
                                    val blockpos: BlockPos = BlockPos(x, y, z)
                                    val state: IBlockState = worldIn.getBlockState(blockpos)

                                    if (state.getBlock() === this.fillerID && state.getBlock()
                                            .getMetaFromState(state) == this.fillerMetadata
                                    ) {
                                        worldIn.setBlockState(blockpos, oreState, 2)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return true
    }
}
