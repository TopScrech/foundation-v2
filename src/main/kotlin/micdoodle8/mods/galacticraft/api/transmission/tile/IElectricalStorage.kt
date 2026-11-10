package micdoodle8.mods.galacticraft.api.transmission.tile

/**
 * This interface is to be applied to all TileEntities which stores electricity
 * within them.
 *
 * @author Calclavia
 */
interface IElectricalStorage {
    /**
     * Sets the amount of joules this unit has stored.
     */
    fun setEnergyStored(energy: Float)

    /**
     * * @return Get the amount of energy currently stored in the block.
     */
    fun getEnergyStored(): Float

    /**
     * @return Get the max amount of energy that can be stored in the block.
     */
    fun getMaxEnergyStored(): Float
}
