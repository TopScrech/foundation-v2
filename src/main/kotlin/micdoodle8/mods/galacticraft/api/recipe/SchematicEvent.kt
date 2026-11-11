package micdoodle8.mods.galacticraft.api.recipe

import net.minecraft.client.gui.screens.Screen
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.api.distmarker.OnlyIn
import net.minecraftforge.eventbus.api.bus.EventBus
import net.minecraftforge.eventbus.api.event.MutableEvent

/** Internal notifications used to unlock blueprints and change NASA workbench pages */
abstract class SchematicEvent(@JvmField var page: ISchematicPage?) : MutableEvent() {
    abstract fun post(): Boolean

    class Unlock(@JvmField var player: ServerPlayer?, page: ISchematicPage?) : SchematicEvent(page) {
        override fun post(): Boolean = BUS.post(this)
        companion object {
            @JvmField val BUS: EventBus<Unlock> = EventBus.create(Unlock::class.java)
        }
    }

    @OnlyIn(Dist.CLIENT)
    class FlipPage(
        @JvmField var currentGui: Screen?,
        page: ISchematicPage?,
        @JvmField var index: Int,
        @JvmField var direction: Int
    ) : SchematicEvent(page) {
        override fun post(): Boolean = BUS.post(this)
        companion object {
            @JvmField val BUS: EventBus<FlipPage> = EventBus.create(FlipPage::class.java)
        }
    }
}
