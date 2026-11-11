package galaxyspace.core.prefab.world.gen

import galaxyspace.core.GSBlocks
import galaxyspace.systems.SolarSystem.moons.io.blocks.IoBlocks.EnumIoBlocks
import galaxyspace.systems.SolarSystem.moons.io.blocks.IoBlocks
import galaxyspace.core.prefab.world.gen.WorldGenPool
import net.minecraft.world.level.block.state.BlockState as IBlockState
import net.minecraft.world.level.block.Blocks
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.gen.feature.WorldGenerator
import java.util.Random

open class WorldGenPool constructor(
    private val block: IBlockState,
    private val fluid: IBlockState,
    private val range: Int
) : WorldGenerator() {
    override fun generate(world: World, rand: Random, position: BlockPos): Boolean {
        if (position.getY() <= 4) {
            return false
        }

        // IBlockState ioSoft = GSBlocks.IO_BLOCKS.defaultBlockState().setValue(IoBlocks.BASIC_TYPE, IoBlocks.EnumIoBlocks.IO_GRUNT);
        val radius: Int = this.range + rand.nextInt(4)
        val radiusSq: Int = radius * radius
        for (poolX: Int in -radius..radius) {
            for (poolY: Int in -radius..radius) {
                for (poolZ: Int in -radius..radius) {
                    val distance: Int = poolX * poolX + poolY * poolY + poolZ * poolZ

                    if (distance <= radiusSq) {
                        val pos: BlockPos =
                            BlockPos(poolX + position.getX(), poolY + position.getY(), poolZ + position.getZ())
                        world.setBlock(
                            pos,
                            if (distance >= radiusSq - 16) this.block else (if (poolY <= 0) this.fluid else Blocks.AIR.defaultBlockState()),
                            if (distance == radiusSq) 3 else 2
                        )
                    }
                }
            }
        }
        /*
        boolean firstSet = false;
        for (int i = 255; i >= position.getY() + 1; --i)
        {
            BlockPos pos = new BlockPos(position.getX(), i, position.getZ());
            if (world.getBlockState(pos).getBlock() != Blocks.AIR)
            {
                if (!firstSet)
                {
                    world.setBlock(pos, GSBlocks.IO_BLOCKS.defaultBlockState().setValue(IoBlocks.BASIC_TYPE, IoBlocks.EnumIoBlocks.IO_SULFUR_GEYSER), 3);
                    world.scheduleBlockUpdate(pos, GSBlocks.IO_BLOCKS.defaultBlockState().setValue(IoBlocks.BASIC_TYPE, IoBlocks.EnumIoBlocks.IO_SULFUR_GEYSER).getBlock(), 0, 0);
                    firstSet = true;
                }
                else
                {
                    world.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }
*/
        return true
    }
}
