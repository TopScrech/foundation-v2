package micdoodle8.mods.galacticraft.api.recipe

import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraftforge.fml.common.eventhandler.Event

/**
 * These events are used internally to perform actions when Galacticraft is
 * installed, without needing to include unnecessary classes. There is no need
 * to subscribe to these events.
 */
abstract class SchematicEvent(@JvmField var page: ISchematicPage?) : Event() {
    open class Unlock constructor(@JvmField var player: EntityPlayerMP?, page: ISchematicPage?) :
        SchematicEvent(page)

    open class FlipPage constructor(
        @JvmField var currentGui: GuiScreen?,
        page: ISchematicPage?,
        @JvmField var index: Int,
        @JvmField var direction: Int
    ) : SchematicEvent(page)
}
