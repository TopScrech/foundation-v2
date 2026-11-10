package micdoodle8.mods.galacticraft.api.galaxies


open class Moon constructor(moonName: String) : CelestialBody(moonName), IChildBody {
    @JvmField
    protected var parentPlanet: Planet? = null

    open fun setParentPlanet(planet: Planet?): Moon {
        this.parentPlanet = planet
        return this
    }

    override fun getID(): Int {
        return GalaxyRegistry.getMoonID(this.bodyName)
    }

    override fun getUnlocalizedNamePrefix(): String {
        return "moon"
    }

    override fun getParentPlanet(): Planet? {
        return this.parentPlanet
    }
}
