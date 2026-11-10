package galaxyspace.systems.SolarSystem.moons.io.world.gen.feature

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.io.blocks.IoBlocks.EnumIoBlocks
import galaxyspace.systems.SolarSystem.moons.io.blocks.IoBlocks
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.gen.feature.WorldGenerator
import net.minecraftforge.fluids.FluidRegistry
import java.util.Random

open class WorldGenVaporPool : WorldGenerator() {
    override fun generate(world: World, rand: Random, position: BlockPos): Boolean {
        if (position.getY() <= 4) {
            return false
        }

        val ioSoft: IBlockState = GSBlocks.IO_BLOCKS.getDefaultState()
            .withProperty(IoBlocks.BASIC_TYPE, EnumIoBlocks.IO_GRUNT)

        val radius: Int = 5 + rand.nextInt(4)
        val radiusSq: Int = radius * radius
        for (poolX: Int in -radius..radius) {
            for (poolY: Int in -radius..radius) {
                for (poolZ: Int in -radius..radius) {
                    val distance: Int = poolX * poolX + poolY * poolY + poolZ * poolZ

                    if (distance <= radiusSq) {
                        val pos: BlockPos =
                            BlockPos(poolX + position.getX(), poolY + position.getY(), poolZ + position.getZ())
                        world.setBlockState(
                            pos,
                            if (distance >= radiusSq - 16) ioSoft else (if (poolY <= 0) FluidRegistry.getFluid("sulphuricacid")
                                .getBlock().getDefaultState() else Blocks.AIR.getDefaultState()),
                            if (distance == radiusSq) 3 else 2
                        )
                    }
                }
            }
        }

        var firstSet: Boolean = false
        for (i: Int in 255 downTo position.getY() + 1) {
            val pos: BlockPos = BlockPos(position.getX(), i, position.getZ())
            if (world.getBlockState(pos).getBlock() !== Blocks.AIR) {
                if (!firstSet) {
                    world.setBlockState(
                        pos,
                        GSBlocks.IO_BLOCKS.getDefaultState().withProperty(
                            IoBlocks.BASIC_TYPE,
                            EnumIoBlocks.IO_SULFUR_GEYSER
                        ),
                        3
                    )
                    world.scheduleBlockUpdate(
                        pos,
                        GSBlocks.IO_BLOCKS.getDefaultState().withProperty(
                            IoBlocks.BASIC_TYPE,
                            EnumIoBlocks.IO_SULFUR_GEYSER
                        ).getBlock(),
                        0,
                        0
                    )
                    firstSet = true
                } else {
                    world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2)
                }
            }
        }

        return true
    }
}
