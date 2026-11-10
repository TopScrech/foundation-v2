package micdoodle8.mods.galacticraft.api.transmission.tile

interface IBufferTransmitter<N> : ITransmitter {
    fun getBuffer(): N?

    fun getCapacity(): Int
}
