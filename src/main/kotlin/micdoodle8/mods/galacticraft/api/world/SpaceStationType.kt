package micdoodle8.mods.galacticraft.api.world

import micdoodle8.mods.galacticraft.api.recipe.SpaceStationRecipe

/**
 * Currently only used internally, not guaranteed to work.
 */
open class SpaceStationType

/**
 * The planet ID is the home planet.  Only one SpaceStationType is allowed per home planet.
 * The dimension number for the planet ID should match what it will be on the CLIENT
 * even if the server has assigned different dimension IDs.  For example, for the Overworld
 * the planet ID should be 0 (it should be 0, even if the server has set ConfigManagerCore.idDimensionOverworld
 * to a different value than 0).
 */ constructor(
    private val spaceStationID: Int,
    private val planetID: Int,
    private var recipe: SpaceStationRecipe?
) {
    /**
     * Dimension ID of the space station
     */
    open fun getSpaceStationID(): Int {
        return this.spaceStationID
    }

    /**
     * Dimension ID of the planet this space station is orbiting
     */
    open fun getWorldToOrbitID(): Int {
        return this.planetID
    }

    /**
     * The recipe to create this space station
     */
    open fun getRecipeForSpaceStation(): SpaceStationRecipe? {
        return this.recipe
    }

    open fun setRecipeForSpaceStation(newRecipe: SpaceStationRecipe?) {
        this.recipe = newRecipe
    }
}
