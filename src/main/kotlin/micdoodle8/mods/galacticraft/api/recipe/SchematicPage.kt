package micdoodle8.mods.galacticraft.api.recipe


/**
 * Reference implementation of [ISchematicPage]. Use/extend this or
 * implement your own.
 */
abstract class SchematicPage : ISchematicPage {
    override fun compareTo(o: ISchematicPage): Int {
        if (this.getPageID() > o.getPageID()) {
            return 1
        } else {
            return -1
        }
    }
}
