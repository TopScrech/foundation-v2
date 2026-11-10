package micdoodle8.mods.galacticraft.api.recipe


/**
 * Implement into GUI classes for the unlocked pages
 */
interface ISchematicResultPage {
    /**
     * Sets the integer index in the set of pages the player has unlocked
     */
    fun setPageIndex(index: Int)
}
