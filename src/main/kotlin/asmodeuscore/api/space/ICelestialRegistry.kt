package asmodeuscore.api.space

import asmodeuscore.api.space.ICelestialRegistry
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem

interface ICelestialRegistry {
    fun canRegistry(): Boolean

    fun getParentSolarSystem(): SolarSystem?

    fun getParentBody(): CelestialBody? {
        return null
    }
}
