package foundation.level

import net.minecraft.core.BlockPos

/** Retains floor-based translations when legacy generators supply fractional offsets */
fun BlockPos.offset(x: Number, y: Number, z: Number): BlockPos {
    val dx = x.toDouble()
    val dy = y.toDouble()
    val dz = z.toDouble()
    return if (dx == 0.0 && dy == 0.0 && dz == 0.0) this
    else BlockPos.containing(this.x + dx, this.y + dy, this.z + dz)
}
