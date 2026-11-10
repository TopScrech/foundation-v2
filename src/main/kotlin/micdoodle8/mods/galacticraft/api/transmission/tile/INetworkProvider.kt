package micdoodle8.mods.galacticraft.api.transmission.tile

import micdoodle8.mods.galacticraft.api.transmission.grid.IGridNetwork

interface INetworkProvider {
    fun getNetwork(): IGridNetwork<*, *, *>?

    fun hasNetwork(): Boolean

    fun setNetwork(network: IGridNetwork<*, *, *>?)
}
