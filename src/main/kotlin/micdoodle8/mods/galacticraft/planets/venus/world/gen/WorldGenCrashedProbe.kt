package micdoodle8.mods.galacticraft.planets.venus.world.gen

import foundation.level.offset

import java.util.Random

import micdoodle8.mods.galacticraft.core.event.LootHandlerGC
import micdoodle8.mods.galacticraft.planets.venus.VenusBlocks
import micdoodle8.mods.galacticraft.planets.venus.tile.TileEntityCrashedProbe
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.ItemStack
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level as World
import net.minecraft.world.gen.feature.WorldGenerator
import java.util.*
import kotlin.math.min

open class WorldGenCrashedProbe constructor() : WorldGenerator() {
    override fun generate(worldIn: World, rand: Random, position: BlockPos): Boolean {
        var position: BlockPos = position
        position = position.offset(-8, 0, -8)
        while (position.getY() > 5 && worldIn.isEmptyBlock(position)) {
            position = position.below()
        }

        if (position.getY() <= 4) {
            return false
        }

        val radius: Int = 5 + rand.nextInt(4)
        val radiusSq: Int = radius * radius

        // Check this crater doesn't cut into a mountain
        for (poolX: Int in -radius..radius) {
            for (poolY: Int in -radius..min(256 - position.getY(), 50)) {
                for (poolZ: Int in -radius..radius) {
                    val pos: BlockPos =
                        BlockPos(poolX + position.getX(), poolY + position.getY(), poolZ + position.getZ())
                    if (poolY > 15 && !worldIn.getBlockState(pos).getBlock()
                            .isAir(worldIn.getBlockState(pos), worldIn, pos)
                    ) {
                        return false
                    }
                }
            }
        }

        for (poolX: Int in -radius - 1..radius + 1) {
            for (poolY: Int in -radius - 1..256 - position.getY()) {
                for (poolZ: Int in -radius - 1..radius + 1) {
                    val distance: Int = poolX * poolX + min(0, poolY) * min(0, poolY) + poolZ * poolZ

                    val pos: BlockPos =
                        BlockPos(poolX + position.getX(), poolY + position.getY(), poolZ + position.getZ())
                    if (distance <= radiusSq) {
                        worldIn.setBlock(pos, Blocks.AIR.defaultBlockState(), 2)
                    } else if (worldIn.getBlockState(pos)
                            .getBlock() === VenusBlocks.venusBlock && poolY < 0 && rand.nextInt(5) == 0
                    ) {
                        worldIn.setBlock(pos, (VenusBlocks.scorchedRock)!!.defaultBlockState(), 2)
                    }
                }
            }
        }

        val blockpos: BlockPos = position.offset(0, -radius + 1, 0)
        worldIn.setBlock(blockpos, (VenusBlocks.crashedProbe)!!.defaultBlockState(), 3)
        val probe: TileEntityCrashedProbe? = worldIn.getBlockEntity(blockpos) as TileEntityCrashedProbe?

        if (probe != null) {
            for (i: Int in 0..<probe.getContainerSize()) {
                // Clear contents
                probe.setItem(i, ItemStack.EMPTY)
            }

            probe.setLootTable(LootHandlerGC.TABLE_CRASHED_PROBE, rand.nextLong())
            probe.setDropCore()
        }

        return true
    }
}
