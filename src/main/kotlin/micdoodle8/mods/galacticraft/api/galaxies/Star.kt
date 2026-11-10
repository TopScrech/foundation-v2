package micdoodle8.mods.galacticraft.api.galaxies


open class Star constructor(planetName: String) : CelestialBody(planetName) {
    @JvmField
    protected var parentSolarSystem: SolarSystem? = null

    open fun getParentSolarSystem(): SolarSystem? {
        return this.parentSolarSystem
    }

    override fun getID(): Int {
        return this.parentSolarSystem!!.getID()
    }

    override fun getUnlocalizedNamePrefix(): String {
        return "star"
    }

    open fun setParentSolarSystem(galaxy: SolarSystem?): Star {
        this.parentSolarSystem = galaxy
        return this
    }
}
