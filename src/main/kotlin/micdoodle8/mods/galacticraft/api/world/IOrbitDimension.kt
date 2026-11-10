package micdoodle8.mods.galacticraft.api.world

interface IOrbitDimension : IGalacticraftWorldProvider {
    /**
     * @return the name of the world that this dimension is orbiting. For the
     * overworld it returns "Overworld"
     */
    fun getPlanetToOrbit(): String?

    /**
     * @return the y-coordinate that entities will fall back into the world we
     * are orbiting
     */
    fun getYCoordToTeleportToPlanet(): Int
}
