package micdoodle8.mods.galacticraft.planets.venus.world.gen

import java.util.Random

import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockBasicVenus
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.feature.WorldGenerator
import java.util.*

open class WorldGenVaporPool constructor() : WorldGenerator() {
    override fun generate(worldIn: World, rand: Random, position: BlockPos): Boolean {
        if (position.getY() <= 4) {
            return false
        }

        val venusSoft: IBlockState = (VenusBlocks.venusBlock)!!.getDefaultState()
            .withProperty(
                BlockBasicVenus.BASIC_TYPE_VENUS,
                BlockBasicVenus.EnumBlockBasicVenus.ROCK_SOFT
            )

        val radius: Int = 5 + rand.nextInt(4)
        val radiusSq: Int = radius * radius
        for (poolX: Int in -radius..radius) {
            for (poolY: Int in -radius..radius) {
                for (poolZ: Int in -radius..radius) {
                    val distance: Int = poolX * poolX + poolY * poolY + poolZ * poolZ

                    if (distance <= radiusSq) {
                        val pos: BlockPos =
                            BlockPos(poolX + position.getX(), poolY + position.getY(), poolZ + position.getZ())
                        worldIn.setBlockState(
                            pos,
                            if (distance >= radiusSq - 16) venusSoft else (if (poolY <= 0) (VenusBlocks.sulphuricAcid)!!.getDefaultState() else Blocks.AIR.getDefaultState()),
                            if (distance == radiusSq) 3 else 2
                        )
                    }
                }
            }
        }

        var firstSet: Boolean = false
        for (i: Int in 255 downTo position.getY() + 1) {
            val pos: BlockPos = BlockPos(position.getX(), i, position.getZ())
            if (worldIn.getBlockState(pos).getBlock() !== Blocks.AIR) {
                if (!firstSet) {
                    worldIn.setBlockState(pos, (VenusBlocks.spout)!!.getDefaultState(), 3)
                    firstSet = true
                } else {
                    worldIn.setBlockState(pos, Blocks.AIR.getDefaultState(), 2)
                }
            }
        }

        return true
    }
}
