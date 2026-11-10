package micdoodle8.mods.galacticraft.api.event.client

import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.common.eventhandler.Cancelable
import net.minecraftforge.fml.common.eventhandler.Event
import org.lwjgl.util.vector.Vector3f

abstract class CelestialBodyRenderEvent(@JvmField val celestialBody: CelestialBody?) : Event() {
    open class CelestialRingRenderEvent constructor(celestialBody: CelestialBody?) :
        CelestialBodyRenderEvent(celestialBody) {
        @Cancelable
        open class Pre constructor(celestialBody: CelestialBody?, @JvmField val parentOffset: Vector3f?) :
            CelestialBodyRenderEvent(celestialBody)

        open class Post constructor(celestialBody: CelestialBody?) : CelestialBodyRenderEvent(celestialBody)
    }

    @Cancelable
    open class Pre constructor(
        celestialBody: CelestialBody?,
        @JvmField var celestialBodyTexture: ResourceLocation?,
        @JvmField var textureSize: Int
    ) : CelestialBodyRenderEvent(celestialBody)

    open class Post constructor(celestialBody: CelestialBody?) : CelestialBodyRenderEvent(celestialBody)
}
