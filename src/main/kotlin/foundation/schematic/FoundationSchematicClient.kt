package foundation.schematic

import micdoodle8.mods.galacticraft.core.client.render.entities.RenderSchematic
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import java.util.function.Consumer

/** Loaded only by name from the common bootstrap on a physical client */
class FoundationSchematicClient : Consumer<FMLJavaModLoadingContext> {
    override fun accept(context: FMLJavaModLoadingContext) {
        EntityRenderersEvent.RegisterRenderers.BUS.addListener { event ->
            event.registerEntityRenderer(FoundationSchematicEntities.TYPE, ::RenderSchematic)
        }
    }
}
