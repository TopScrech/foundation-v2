package galaxyspace.core.prefab.world.gen

import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.gen.feature.WorldGenerator
import java.util.Random

open class WorldGenCircleBlock constructor(gen_block: IBlockState, radius: Int, replace: IBlockState?) :
    WorldGenerator() {
    private val block: IBlockState?
    private val replace: IBlockState?
    private val radius: Int

    init {
        this.block = gen_block
        this.radius = radius
        this.replace = replace
    }

    override fun generate(worldIn: World, rand: Random, position: BlockPos): Boolean {
        run {
            val i: Int = rand.nextInt(this.radius - 2) + 2
            //int j = 2;
            for (k: Int in position.getX() - i..position.getX() + i) {
                for (l: Int in position.getZ() - i..position.getZ() + i) {
                    val i1: Int = k - position.getX()
                    val j1: Int = l - position.getZ()

                    if (i1 * i1 + j1 * j1 <= i * i) {
                        for (k1: Int in position.getY() - 2..position.getY() + 2) {
                            val blockpos: BlockPos = BlockPos(k, k1, l)
                            val block: IBlockState = worldIn.getBlockState(blockpos)

                            if (block === this.replace) {
                                worldIn.setBlock(blockpos, this.block, 2)
                            }
                        }
                    }
                }
            }
        }
        return true
    }
}
