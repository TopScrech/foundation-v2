package asmodeuscore.core.utils

import asmodeuscore.core.utils.ACCompatibilityManager
import net.minecraftforge.fml.common.Loader

object ACCompatibilityManager {
    private val addonEPLoaded = Loader.isModLoaded("extraplanets")
    private val addonPPLoaded = Loader.isModLoaded("planetprogression")
    private val galacticraftLoaded = Loader.isModLoaded("galacticraftcore")
    private val beyondspaceLoaded = Loader.isModLoaded("beyondspace")
    private val galaxyspaceLoaded = Loader.isModLoaded("galaxyspace")

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
