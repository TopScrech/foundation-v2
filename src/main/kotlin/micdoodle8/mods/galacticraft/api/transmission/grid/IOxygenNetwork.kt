package micdoodle8.mods.galacticraft.api.transmission.grid

import micdoodle8.mods.galacticraft.api.transmission.tile.ITransmitter
import net.minecraft.world.level.block.entity.BlockEntity as TileEntity

/**
 * The Oxygen Network in interface form.
 *
 * @author Calclavia
 */
interface IOxygenNetwork : IGridNetwork<IOxygenNetwork?, ITransmitter?, TileEntity?> {
    /**
     * Produces oxygen in this oxygen network.
     *
     * @return Rejected energy in Joules.
     */
    fun produce(sendAmount: Float, vararg ignoreTiles: TileEntity?): Float

    /**
     * Gets the total amount of oxygen requested/needed in the electricity
     * network.
     *
     * @param ignoreTiles The TileEntities to ignore during this calculation (optional).
     */
    fun getRequest(vararg ignoreTiles: TileEntity?): Float
}
