package micdoodle8.mods.galacticraft.api.transmission.tile

import net.minecraft.tileentity.TileEntity

/**
 * Applied to TileEntities.
 *
 * @author Calclavia
 */
interface INetworkConnection : IConnector {
    /**
     * Gets a list of all the connected TileEntities that this conductor is
     * connected to. The array's length should be always the 6 adjacent wires.
     *
     * @return
     */
    fun getAdjacentConnections(): Array<TileEntity?>?

    /**
     * Refreshes the conductor
     */
    fun refresh()

    fun onNetworkChanged()
}
