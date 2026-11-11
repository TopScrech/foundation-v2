package micdoodle8.mods.galacticraft.api.recipe


/**
 * Reference implementation of [ISchematicPage]. Use/extend this or
 * implement your own.
 */
abstract class SchematicPage : ISchematicPage {
    override fun compareTo(other: ISchematicPage): Int = getPageID().compareTo(other.getPageID())
}
