package micdoodle8.mods.galacticraft.api.galaxies

import net.minecraft.world.biome.Biome.SpawnListEntry

import net.minecraft.world.biome.Biome

open class Planet constructor(planetName: String) : CelestialBody(planetName) {
    @JvmField
    protected var parentSolarSystem: SolarSystem? = null

    open fun getParentSolarSystem(): SolarSystem? {
        return this.parentSolarSystem
    }

    override fun getID(): Int {
        return GalaxyRegistry.getPlanetID(this.bodyName)
    }

    override fun getUnlocalizedNamePrefix(): String {
        return "planet"
    }

    open fun setParentSolarSystem(galaxy: SolarSystem?): Planet {
        this.parentSolarSystem = galaxy
        return this
    }

    companion object {
        @JvmStatic
        fun addMobToSpawn(planetName: String?, mobData: Biome.SpawnListEntry?) {
            (GalaxyRegistry.getCelestialBodyFromUnlocalizedName("planet." + planetName))!!.addMobInfo(mobData)
        }
    }
}
