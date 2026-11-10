package micdoodle8.mods.galacticraft.api.entity

import micdoodle8.mods.galacticraft.api.tile.IFuelDock

/**
 * Implement into entities that are placed on fuel docks to load with cargo and
 * fuel
 */
interface IDockable : IFuelable, ICargoEntity {
    /**
     * Sets the current fuel dock for this entity
     */
    fun setPad(pad: IFuelDock?)

    /**
     * Gets the fuel dock when required. Must return the same one that was set.
     */
    fun getLandingPad(): IFuelDock?

    /**
     * When the fuel dock is destroyed. Most likely kills the entity and drops
     * it's containing items.
     */
    fun onPadDestroyed()

    /**
     * Whether or not the fuel dock this entity is on is valid.
     *
     *
     * Returning false will not let setPad be called.
     */
    fun isDockValid(dock: IFuelDock?): Boolean

    fun inFlight(): Boolean
}
