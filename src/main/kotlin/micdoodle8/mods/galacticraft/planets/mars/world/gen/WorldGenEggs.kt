package micdoodle8.mods.galacticraft.planets.mars.world.gen

import micdoodle8.mods.galacticraft.planets.mars.blocks.BlockBasicMars
import micdoodle8.mods.galacticraft.planets.mars.blocks.MarsBlocks
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.feature.WorldGenerator
import java.util.Random

open class WorldGenEggs constructor(private val eggBlock: Block) : WorldGenerator() {
    override fun generate(par1World: World, par2Random: Random, pos: BlockPos): Boolean {
        val i1: Int = pos.getX() + par2Random.nextInt(8) - par2Random.nextInt(8)
        val j1: Int = pos.getY() + par2Random.nextInt(4) - par2Random.nextInt(4)
        val k1: Int = pos.getZ() + par2Random.nextInt(8) - par2Random.nextInt(8)
        val newPos: BlockPos = BlockPos(i1, j1, k1)

        if (!par1World.isBlockLoaded(newPos.add(1, 0, 1))) return false

        if (par1World.isAirBlock(newPos) && (j1 < 127 || !par1World.provider.isNether())) {
            val below: IBlockState = par1World.getBlockState(newPos.down())
            if (below.getBlock() === MarsBlocks.marsBlock && below.getValue(
                    BlockBasicMars.BASIC_TYPE
                ) == BlockBasicMars.EnumBlockBasic.SURFACE
            ) {
                par1World.setBlockState(newPos, this.eggBlock.getStateFromMeta(par2Random.nextInt(3)), 2)
            }
        }

        return true
    }
}
