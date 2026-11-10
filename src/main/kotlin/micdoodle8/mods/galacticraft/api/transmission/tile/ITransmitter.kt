package micdoodle8.mods.galacticraft.api.transmission.tile

import micdoodle8.mods.galacticraft.api.transmission.NetworkType

interface ITransmitter : INetworkProvider, INetworkConnection {
    fun getNetworkType(): NetworkType?

    fun canTransmit(): Boolean
}
