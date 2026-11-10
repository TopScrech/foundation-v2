package micdoodle8.mods.galacticraft.api.galaxies

import micdoodle8.mods.galacticraft.api.vector.Vector3
import net.minecraft.util.text.translation.I18n

open class SolarSystem
constructor(
    @JvmField protected var unlocalizedName: String,
    @JvmField protected var unlocalizedGalaxyName: String?
) {
    @JvmField
    protected val systemName: String

    @JvmField
    protected var mapPosition: Vector3? = null

    @JvmField
    protected var mainStar: Star? = null

    init {
        this.systemName = unlocalizedName.lowercase()
    }

    open fun getName(): String {
        return this.systemName
    }

    fun getID(): Int {
        return GalaxyRegistry.getSolarSystemID(this.systemName)
    }

    open fun getLocalizedName(): String {
        val s: String? = this.getTranslationKey()
        return if (s == null) "" else I18n.translateToLocal(s)
    }

    open fun getTranslationKey(): String {
        return "solarsystem." + this.unlocalizedName
    }

    open fun getMapPosition(): Vector3? {
        return this.mapPosition
    }

    open fun setMapPosition(mapPosition: Vector3): SolarSystem {
        mapPosition.scale(500.0)
        this.mapPosition = mapPosition
        return this
    }

    open fun getMainStar(): Star? {
        return this.mainStar
    }

    open fun setMainStar(star: Star?): SolarSystem {
        this.mainStar = star
        return this
    }

    open fun getLocalizedParentGalaxyName(): String {
        val s: String? = this.getUnlocalizedParentGalaxyName()
        return if (s == null) "" else I18n.translateToLocal(s)
    }

    open fun getUnlocalizedParentGalaxyName(): String {
        return "galaxy." + this.unlocalizedGalaxyName
    }
}
