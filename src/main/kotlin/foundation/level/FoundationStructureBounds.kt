package foundation.level

import net.minecraft.world.level.levelgen.structure.BoundingBox

/** Bounds used for horizontal dungeon collision tests retained their original vertical range */
fun horizontalStructureBounds(minX: Int, minZ: Int, maxX: Int, maxZ: Int): BoundingBox =
    BoundingBox(minX, 1, minZ, maxX, 512, maxZ)

/** An empty accumulation has no native bounding box, rather than inverted extreme coordinates */
class StructureBoundsAccumulator {
    private var minX = Int.MAX_VALUE
    private var minY = Int.MAX_VALUE
    private var minZ = Int.MAX_VALUE
    private var maxX = Int.MIN_VALUE
    private var maxY = Int.MIN_VALUE
    private var maxZ = Int.MIN_VALUE
    private var present = false

    fun include(x: Int, y: Int, z: Int) {
        minX = minOf(minX, x)
        minY = minOf(minY, y)
        minZ = minOf(minZ, z)
        maxX = maxOf(maxX, x)
        maxY = maxOf(maxY, y)
        maxZ = maxOf(maxZ, z)
        present = true
    }

    fun bounds(): BoundingBox? = if (present) BoundingBox(minX, minY, minZ, maxX, maxY, maxZ) else null
}
