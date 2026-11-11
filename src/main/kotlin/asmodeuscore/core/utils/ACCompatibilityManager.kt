package asmodeuscore.core.utils

import asmodeuscore.core.utils.ACCompatibilityManager
import net.minecraftforge.fml.ModList

object ACCompatibilityManager {
    private val addonEPLoaded = ModList.isLoaded("extraplanets")
    private val addonPPLoaded = ModList.isLoaded("planetprogression")
    private val galacticraftLoaded = ModList.isLoaded("galacticraftcore")
    private val beyondspaceLoaded = ModList.isLoaded("beyondspace")
    private val galaxyspaceLoaded = ModList.isLoaded("galaxyspace")

    @JvmStatic
    fun isExtraPlanetsLoaded(): Boolean {
        return addonEPLoaded
    }

    @JvmStatic
    fun isPlanetProgressionLoaded(): Boolean {
        return addonPPLoaded
    }

    @JvmStatic
    fun isGalacticraftLoaded(): Boolean {
        return galacticraftLoaded
    }

    @JvmStatic
    fun isBeyondSpaceLoaded(): Boolean {
        return beyondspaceLoaded
    }

    @JvmStatic
    fun isGalaxySpaceLoaded(): Boolean {
        return galaxyspaceLoaded
    }
}
