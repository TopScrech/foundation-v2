package micdoodle8.mods.galacticraft.api.galaxies

import net.minecraft.world.WorldProvider
import java.lang.UnsupportedOperationException

open class Satellite constructor(satelliteName: String) : CelestialBody(satelliteName), IChildBody {
    @JvmField
    protected var parentCelestialBody: Planet? = null

    @JvmField
    protected var dimensionIdStatic: Int = 0

    override fun getParentPlanet(): Planet? {
        return this.parentCelestialBody
    }

    open fun setParentBody(parentCelestialBody: Planet?): Satellite {
        this.parentCelestialBody = parentCelestialBody
        return this
    }

    @Deprecated("")
    override fun setDimensionInfo(
        providerId: Int,
        providerClass: Class<out WorldProvider>?,
        autoRegister: Boolean
    ): CelestialBody {
        // Since satellites need a static ID, the subclass method will not work correctly.
        throw UnsupportedOperationException("Satellite registered using an outdated method (setDimensionInfo)! Tell Galacticraft addon authors to update to the latest API.")
    }

    open fun setDimensionInfo(
        providerIdDynamic: Int,
        providerIdStatic: Int,
        providerClass: Class<out WorldProvider>?
    ): CelestialBody {
        this.dimensionID = providerIdDynamic
        this.dimensionIdStatic = providerIdStatic
        this.providerClass = providerClass
        this.autoRegisterDimension = false // Addons need to register satellites manually
        this.isReachable = true
        return this
    }

    override fun getID(): Int {
        return GalaxyRegistry.getSatelliteID(this.bodyName)
    }

    override fun getUnlocalizedNamePrefix(): String {
        return "satellite"
    }

    open fun getDimensionIdStatic(): Int {
        return dimensionIdStatic
    }
}
