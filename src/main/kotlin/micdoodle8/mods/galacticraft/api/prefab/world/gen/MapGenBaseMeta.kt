package micdoodle8.mods.galacticraft.api.prefab.world.gen

import net.minecraft.world.level.Level as World
import net.minecraft.world.chunk.ChunkPrimer
import java.util.Random

/**
 * Do not include this prefab class in your released mod download.
 *
 *
 * This is a base class for World Gen such as caves. Override and generate features in recursiveGenerate
 */
abstract class MapGenBaseMeta {
    /**
     * The number of Chunks to gen-check in any given direction.
     */
    @JvmField
    protected var range: Int = 8

    /**
     * The RNG used by the MapGen classes.
     */
    @JvmField
    protected var rand: Random = Random()

    /**
     * This world object.
     */
    @JvmField
    protected var world: World? = null

    open fun generate(world: World, chunkX: Int, chunkZ: Int, primer: ChunkPrimer?) {
        this.world = world
        this.rand.setSeed(world.getSeed())
        val r0: Long = this.rand.nextLong()
        val r1: Long = this.rand.nextLong()

        for (x0: Int in chunkX - this.range..chunkX + this.range) {
            for (y0: Int in chunkZ - this.range..chunkZ + this.range) {
                val randX: Long = x0 * r0
                val randZ: Long = y0 * r1
                this.rand.setSeed(randX xor randZ xor world.getSeed())
                this.recursiveGenerate(world, x0, y0, chunkX, chunkZ, primer)
            }
        }
    }

    /**
     * Recursively called by generate() (generate) and optionally by itself.
     */
    protected open fun recursiveGenerate(
        world: World?,
        xChunkCoord: Int,
        zChunkCoord: Int,
        origXChunkCoord: Int,
        origZChunkCoord: Int,
        primer: ChunkPrimer?
    ) {
    }
}
