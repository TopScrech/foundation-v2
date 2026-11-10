package micdoodle8.mods.galacticraft.api.galaxies

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.eventhandler.Event
import java.lang.RuntimeException
import java.util.ArrayList
import java.util.HashMap
import java.util.TreeMap

/**
 * Credits to KingLemming and CovertJaguar, since this is based on the
 * Liquid/Fluid API
 */
object GalaxyRegistry {
    @JvmField
    var maxSolarSystemID: Int = 0

    @JvmField
    var maxPlanetID: Int = 0

    @JvmField
    var maxMoonID: Int = 0

    @JvmField
    var maxSatelliteID: Int = 0

    @JvmField
    var solarSystems: HashMap<String?, SolarSystem> = Maps.newHashMap<String?, SolarSystem>()

    @JvmField
    var solarSystemIDs: BiMap<String?, Int> = HashBiMap.create<String?, Int>()

    @JvmField
    var planets: TreeMap<String?, Planet> = Maps.newTreeMap<String?, Planet>()

    @JvmField
    var planetIDs: BiMap<String?, Int> = HashBiMap.create<String?, Int>()

    @JvmField
    var moons: TreeMap<String?, Moon> = Maps.newTreeMap<String?, Moon>()

    @JvmField
    var moonIDs: BiMap<String?, Int> = HashBiMap.create<String?, Int>()

    @JvmField
    var satellites: HashMap<String?, Satellite> = Maps.newHashMap<String?, Satellite>()

    @JvmField
    var satelliteIDs: BiMap<String?, Int> = HashBiMap.create<String?, Int>()

    @JvmField
    var moonList: HashMap<Planet?, MutableList<Moon?>?> = Maps.newHashMap<Planet?, MutableList<Moon?>?>()

    @JvmField
    var satelliteList: HashMap<CelestialBody?, MutableList<Satellite?>?> =
        Maps.newHashMap<CelestialBody?, MutableList<Satellite?>?>()

    @JvmField
    var solarSystemList: HashMap<SolarSystem?, MutableList<Planet?>?> =
        Maps.newHashMap<SolarSystem?, MutableList<Planet?>?>()

    @JvmStatic
    fun getCelestialBodyFromDimensionID(dimensionID: Int): CelestialBody? {
        for (planet: Planet in planets.values) {
            if (planet.getDimensionID() == dimensionID) {
                return planet
            }
        }

        for (moon: Moon in moons.values) {
            if (moon.getDimensionID() == dimensionID) {
                return moon
            }
        }

        for (satellite: Satellite in satellites.values) {
            if (satellite.getDimensionID() == dimensionID) {
                return satellite
            }
        }

        return null
    }

    @JvmStatic
    fun refreshGalaxies() {
        moonList.clear()
        satelliteList.clear()
        solarSystemList.clear()

        for (moon: Moon in getRegisteredMoons()!!.values) {
            val planet: Planet? = moon.getParentPlanet()
            var listOfMoons: MutableList<Moon?>? = moonList.get(planet)
            if (listOfMoons == null) {
                listOfMoons = ArrayList<Moon?>()
            }
            listOfMoons.add(moon)
            moonList.put(planet, listOfMoons)
        }

        for (satellite: Satellite in getRegisteredSatellites()!!.values) {
            val celestialBody: CelestialBody? = satellite.getParentPlanet()
            var satelliteList1: MutableList<Satellite?>? = satelliteList.get(celestialBody)
            if (satelliteList1 == null) {
                satelliteList1 = ArrayList<Satellite?>()
            }
            satelliteList1.add(satellite)
            satelliteList.put(celestialBody, satelliteList1)
        }

        for (planet: Planet in getRegisteredPlanets()!!.values) {
            val solarSystem: SolarSystem? = planet.getParentSolarSystem()
            var planetList: MutableList<Planet?>? = solarSystemList.get(solarSystem)
            if (planetList == null) {
                planetList = ArrayList<Planet?>()
            }
            planetList.add(planet)
            solarSystemList.put(solarSystem, planetList)
        }
    }

    @JvmStatic
    fun getPlanetsForSolarSystem(solarSystem: SolarSystem?): MutableList<Planet?>? {
        val solarSystemListLocal: MutableList<Planet?>? = solarSystemList.get(solarSystem)

        if (solarSystemListLocal == null) {
            return ArrayList<Planet?>()
        }

        return ImmutableList.copyOf<Planet?>(solarSystemListLocal)
    }

    @JvmStatic
    fun getMoonsForPlanet(planet: Planet?): MutableList<Moon?>? {
        val moonListLocal: MutableList<Moon?>? = moonList.get(planet)

        if (moonListLocal == null) {
            return ArrayList<Moon?>()
        }

        return ImmutableList.copyOf<Moon?>(moonListLocal)
    }

    @JvmStatic
    fun getSatellitesForCelestialBody(celestialBody: CelestialBody?): MutableList<Satellite?>? {
        val satelliteList1: MutableList<Satellite?>? = satelliteList.get(celestialBody)

        if (satelliteList1 == null) {
            return ArrayList<Satellite?>()
        }

        return ImmutableList.copyOf<Satellite?>(satelliteList1)
    }

    @JvmStatic
    fun getCelestialBodyFromUnlocalizedName(unlocalizedName: String?): CelestialBody? {
        for (planet: Planet in planets.values) {
            if (planet.getTranslationKey() == unlocalizedName) {
                return planet
            }
        }

        for (moon: Moon in moons.values) {
            if (moon.getTranslationKey() == unlocalizedName) {
                return moon
            }
        }

        return null
    }

    @JvmStatic
    fun registerSolarSystem(solarSystem: SolarSystem): Boolean {
        if (solarSystemIDs.containsKey(solarSystem.getName())) {
            return false
        }

        solarSystems.put(solarSystem.getName(), solarSystem)
        solarSystemIDs.put(solarSystem.getName(), ++maxSolarSystemID)

        MinecraftForge.EVENT_BUS.post(SolarSystemRegisterEvent(solarSystem.getName(), maxSolarSystemID))
        return true
    }

    @JvmStatic
    fun registerPlanet(planet: Planet): Boolean {
        if (planetIDs.containsKey(planet.getName())) {
            return false
        }

        planets.put(planet.getName(), planet)
        planetIDs.put(planet.getName(), ++maxPlanetID)

        MinecraftForge.EVENT_BUS.post(PlanetRegisterEvent(planet.getName(), maxPlanetID))
        return true
    }

    @JvmStatic
    fun registerMoon(moon: Moon): Boolean {
        if (moonIDs.containsKey(moon.getName())) {
            return false
        }

        moons.put(moon.getName(), moon)
        moonIDs.put(moon.getName(), ++maxMoonID)

        MinecraftForge.EVENT_BUS.post(MoonRegisterEvent(moon.getName(), maxMoonID))
        return true
    }

    @JvmStatic
    fun registerSatellite(satellite: Satellite): Boolean {
        if (satelliteIDs.containsKey(satellite.getName())) {
            return false
        }

        if (satellite.getParentPlanet() == null) {
            throw RuntimeException("Registering satellite without a parent!!!")
        }

        satellites.put(satellite.getName(), satellite)
        satelliteIDs.put(satellite.getName(), ++maxSatelliteID)

        MinecraftForge.EVENT_BUS.post(SatelliteRegisterEvent(satellite.getName(), maxSatelliteID))
        return true
    }

    /**
     * Returns a read-only map containing Solar System Names and their
     * associated Solar Systems.
     */
    @JvmStatic
    fun getRegisteredSolarSystems(): MutableMap<String?, SolarSystem?>? {
        return ImmutableMap.copyOf<String?, SolarSystem?>(solarSystems)
    }

    /**
     * Returns a read-only map containing Solar System Names and their
     * associated IDs.
     */
    @JvmStatic
    fun getRegisteredSolarSystemIDs(): MutableMap<String?,Int>? {
        return ImmutableMap.copyOf<String?,Int>(solarSystemIDs)
    }

    /**
     * Returns a read-only map containing Planet Names and their associated
     * Planets.
     */
    @JvmStatic
    fun getRegisteredPlanets(): MutableMap<String?, Planet>? {
        return planets.clone() as MutableMap<String?, Planet>?
    }

    /**
     * Returns a read-only map containing Planet Names and their associated IDs.
     */
    @JvmStatic
    fun getRegisteredPlanetIDs(): MutableMap<String?,Int>? {
        return ImmutableMap.copyOf<String?,Int>(planetIDs)
    }

    /**
     * Returns a read-only map containing Moon Names and their associated Moons.
     */
    @JvmStatic
    fun getRegisteredMoons(): MutableMap<String?, Moon>? {
        return moons.clone() as MutableMap<String?, Moon>?
    }

    /**
     * Returns a read-only map containing Moon Names and their associated IDs.
     */
    @JvmStatic
    fun getRegisteredMoonIDs(): MutableMap<String?,Int>? {
        return ImmutableMap.copyOf<String?,Int>(moonIDs)
    }

    /**
     * Returns a read-only map containing Satellite Names and their associated Satellite.
     */
    @JvmStatic
    fun getRegisteredSatellites(): MutableMap<String?, Satellite>? {
        return ImmutableMap.copyOf<String?, Satellite>(satellites)
    }

    /**
     * Returns a read-only map containing Satellite Names and their associated IDs.
     */
    @JvmStatic
    fun getRegisteredSatelliteIDs(): MutableMap<String?,Int>? {
        return ImmutableMap.copyOf<String?,Int>(satelliteIDs)
    }

    @JvmStatic
    fun getSolarSystemID(solarSystemName: String?): Int {
        return solarSystemIDs.get(solarSystemName)!!
    }

    @JvmStatic
    fun getPlanetID(planetName: String?): Int {
        return planetIDs.get(planetName)!!
    }

    @JvmStatic
    fun getMoonID(moonName: String?): Int {
        return moonIDs.get(moonName)!!
    }

    @JvmStatic
    fun getSatelliteID(satelliteName: String?): Int {
        return satelliteIDs.get(satelliteName)!!
    }

    open class SolarSystemRegisterEvent constructor(
        @JvmField val solarSystemName: String?,
        @JvmField val solarSystemID: Int
    ) : Event()

    open class PlanetRegisterEvent constructor(@JvmField val planetName: String?, @JvmField val planetID: Int) :
        Event()

    open class MoonRegisterEvent constructor(@JvmField val moonName: String?, @JvmField val moonID: Int) : Event()

    open class SatelliteRegisterEvent constructor(
        @JvmField val satelliteName: String?,
        @JvmField val satelliteID: Int
    ) : Event()
}
