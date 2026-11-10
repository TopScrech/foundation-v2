package micdoodle8.mods.galacticraft.api.world

/**
 * For world providers where you would like to specify height for spacecraft to
 * be teleported
 *
 *
 * Implement into world providers
 */
interface IExitHeight {
    /**
     * @return y-coordinate that spacecraft leaves the dimension
     */
    fun getYCoordinateToTeleport(): Double
}
