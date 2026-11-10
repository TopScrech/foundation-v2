package micdoodle8.mods.galacticraft.api.transmission.grid

import micdoodle8.mods.galacticraft.api.vector.Vector3

interface IReflectorNode {
    fun getInputPoint(): Vector3?

    fun getOutputPoint(offset: Boolean): Vector3?
}
