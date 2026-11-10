package micdoodle8.mods.galacticraft.api.world

import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.Chunk
import net.minecraft.world.gen.IChunkGenerator

abstract class ChunkProviderBase : IChunkGenerator {
    override fun getNearestStructurePos(
        worldIn: World,
        structureName: String,
        position: BlockPos,
        findUnexplored: Boolean
    ): BlockPos? {
        return null
    }

    // This is something connected with Ocean Monuments, and not used in Galacticraft.
    override fun generateStructures(chunkIn: Chunk, x: Int, z: Int): Boolean {
        return false
    }

    /**
     * Override this if you want to implement an advancement using net.minecraft.advancements.critereon.LocationPredicate
     */
    override fun isInsideStructure(worldIn: World, structureName: String, pos: BlockPos): Boolean {
        return false
    }
}
