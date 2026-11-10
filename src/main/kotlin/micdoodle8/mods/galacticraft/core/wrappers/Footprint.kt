package micdoodle8.mods.galacticraft.core.wrappers

import micdoodle8.mods.galacticraft.api.vector.Vector3

open class Footprint
constructor(
    @JvmField val dimension: Int,
    @JvmField val position: Vector3?,
    @JvmField val rotation: Float,
    @JvmField var age: Short,
    @JvmField val owner: String?,
    @JvmField var lightmapVal: Int
) {
    constructor(dimension: Int, position: Vector3?, rotation: Float, ownerUUID: String?, lightmapVal: Int) : this(
        dimension,
        position,
        rotation,
        0.toShort(),
        ownerUUID,
        lightmapVal
    )

    companion object {
        const val MAX_AGE: Short = 3200
    }
}
