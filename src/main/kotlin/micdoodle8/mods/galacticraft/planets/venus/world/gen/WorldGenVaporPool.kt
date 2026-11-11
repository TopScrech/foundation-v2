package micdoodle8.mods.galacticraft.planets.venus.world.gen

import java.util.Random

import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import micdoodle8.mods.galacticraft.planets.venus.blocks.BlockBasicVenus
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.gen.feature.WorldGenerator
import java.util.*

open class WorldGenVaporPool constructor() : WorldGenerator() {
    override fun generate(worldIn: World, rand: Random, position: BlockPos): Boolean {
        if (position.getY() <= 4) {
            return false
        }

        val venusSoft: IBlockState = (VenusBlocks.venusBlock)!!.defaultBlockState()
            .setValue(
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
                        worldIn.setBlock(
                            pos,
                            if (distance >= radiusSq - 16) venusSoft else (if (poolY <= 0) (VenusBlocks.sulphuricAcid)!!.defaultBlockState() else Blocks.AIR.defaultBlockState()),
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
                    worldIn.setBlock(pos, (VenusBlocks.spout)!!.defaultBlockState(), 3)
                    firstSet = true
                } else {
                    worldIn.setBlock(pos, Blocks.AIR.defaultBlockState(), 2)
                }
            }
        }

        return true
    }
}
