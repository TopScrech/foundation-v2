package micdoodle8.mods.galacticraft.api.transmission.tile

import micdoodle8.mods.galacticraft.api.transmission.grid.IElectricityNetwork

interface IConductor : ITransmitter {
    /**
     * @return The tier of this conductor - must be 1 or 2
     */
    fun getTierGC(): Int

    /**
     * @return This conductor's electricity network.
     */
    override fun getNetwork(): IElectricityNetwork?
}
