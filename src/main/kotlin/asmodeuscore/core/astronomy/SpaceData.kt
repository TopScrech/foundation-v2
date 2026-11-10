package asmodeuscore.core.astronomy

import java.util.Map

import asmodeuscore.core.astronomy.SpaceData
import asmodeuscore.core.astronomy.SpaceData.Engine_Type
import asmodeuscore.api.space.ICelestialRegistry
import com.google.common.collect.ImmutableList
import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableMap
import com.google.common.collect.Maps
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry
import micdoodle8.mods.galacticraft.api.galaxies.IChildBody
import micdoodle8.mods.galacticraft.api.galaxies.Planet
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem
import java.util.ArrayList
import java.util.HashMap

open class SpaceData constructor() {
    enum class Engine_Type(@JvmField var id: Int, @JvmField var serializedName: String) {
        FUEL_ENGINE(0, "fuel_engine"),
        ION_ENGINE(1, "ion_engine"),
        PLASMA_ENGINE(2, "plasma_engine"),
        SUBLIGHT_ENGINE(3, "sublight_engine"),
        BLACKHOLE_ENGINE(4, "blackhole_engine");

        fun getName(): String {
            return this.serializedName
        }

        fun getID(): Int {
            return this.id
        }

        companion object {
            private val values: Array<Engine_Type> = entries.toTypedArray()

            @JvmStatic
            fun byID(id: Int): Engine_Type? {
                return values[id % values.size]
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////
    @JvmField
    val rocket_tier: String = "rocket_tier"

    @JvmField
    val rocket_engine: String = "rocket_engine"

    @JvmField
    val rocket_count_fuel: String = "rocket_count_fuel"

    @JvmField
    val second_passenger: String = "second_passenger"

    open fun addToMap(name: String?, obj: Any?) {
        map.put(name, obj)
    }

    open fun getMap(): MutableMap<*, *> {
        return map
    }

    open fun getValue(name: String?): Any? {
        return map.get(name)
    }

    companion object {
        private val map: MutableMap<String?, Any?> = HashMap<String?, Any?>()

        /////////////////////////////////////////////////////////////////////////////////////
        @JvmField
        var solarSystemList: HashMap<SolarSystem?, MutableList<CelestialBody?>?> =
            Maps.newHashMap<SolarSystem?, MutableList<CelestialBody?>?>()

        @JvmField
        var moonList: HashMap<Planet?, MutableList<CelestialBody?>?> =
            Maps.newHashMap<Planet?, MutableList<CelestialBody?>?>()

        @JvmStatic
        fun refreshGalaxies() {
            GalaxyRegistry.refreshGalaxies()

            moonList.clear()
            solarSystemList.clear()

            for (body: CelestialBody? in getRegisteredCelestialBodies()!!.values) {
                if (body is ICelestialRegistry) {
                    if (body is IChildBody) {
                        val planet: Planet? = (body as IChildBody).getParentPlanet()
                        var listOfMoons: MutableList<CelestialBody?>? = moonList.get(planet)
                        if (listOfMoons == null) {
                            listOfMoons = ArrayList<CelestialBody?>()
                        }
                        listOfMoons.add(body)
                        moonList.put(planet, listOfMoons)
                    } else {
                        val solarSystem: SolarSystem? = (body as ICelestialRegistry).getParentSolarSystem()
                        var planetList: MutableList<CelestialBody?>? = solarSystemList.get(solarSystem)
                        if (planetList == null) {
                            planetList = ArrayList<CelestialBody?>()
                        }
                        planetList.add(body)
                        solarSystemList.put(solarSystem, planetList)
                    }
                }
            }
        }

        @JvmStatic
        fun getBodiesForSolarSystem(solarSystem: SolarSystem?): MutableList<CelestialBody?>? {
            val solarSystemListLocal: MutableList<CelestialBody?>? = solarSystemList.get(solarSystem)

            if (solarSystemListLocal == null) {
                return ArrayList<CelestialBody?>()
            }

            return ImmutableList.copyOf<CelestialBody?>(solarSystemListLocal)
        }

        @JvmStatic
        fun getMoonsForPlanet(planet: Planet?): MutableList<CelestialBody?>? {
            val moonListLocal: MutableList<CelestialBody?>? = moonList.get(planet)

            if (moonListLocal == null) {
                return ArrayList<CelestialBody?>()
            }

            return ImmutableList.copyOf<CelestialBody?>(moonListLocal)
        }

        @JvmField
        var maxCelestialBodiesID: Int = 0

        //static int maxCometID = 0;
        @JvmField
        var celestialbodies: HashMap<String?, CelestialBody?> = Maps.newHashMap<String?, CelestialBody?>()

        @JvmField
        var celestialbodiesIDs: BiMap<String?,Int> = HashBiMap.create<String?,Int>()

        /*
    static Map<String, Comet> comets = Maps.newHashMap();
    static BiMap<String, Integer> cometsIDs = HashBiMap.create();
    */
        ////////////////////////////////////////////////////////////////////////////////////
        @JvmStatic
        fun registerCelestialBody(body: CelestialBody): Boolean {
            if (celestialbodiesIDs.containsKey(body.getName())) {
                return false
            }

            celestialbodies.put(body.getName(), body)
            celestialbodiesIDs.put(body.getName(), ++maxCelestialBodiesID)

            return true
        }

        @JvmStatic
        fun getRegisteredCelestialBodies(): MutableMap<String?, CelestialBody?>? {
            return ImmutableMap.copyOf<String?, CelestialBody?>(celestialbodies)
        }

        @JvmStatic
        fun getRegisteredCelestialBodiesIDs(): MutableMap<String?,Int>? {
            return ImmutableMap.copyOf<String?,Int>(celestialbodiesIDs)
        }

        @JvmStatic
        fun getCelestialBodyID(bodyName: String?): Int {
            return celestialbodiesIDs.get(bodyName)!!
        } ////////////////////////////////////////////////////////////////////////////////////
        /*
    public static boolean registerComet(Comet comet)
    {
        if (cometsIDs.containsKey(comet.getName()))        
            return false;
        

        comets.put(comet.getName(), comet);
        cometsIDs.put(comet.getName(), ++maxCometID);

        return true;
    }
    
    public static Map<String, Comet> getRegisteredComets()
    {
        return ImmutableMap.copyOf(comets);
    }
    
    public static Map<String, Integer> getRegisteredCometsIDs()
    {
        return ImmutableMap.copyOf(cometsIDs);
    }
    
    public static int getCometID(String cometName)
    {
        return cometsIDs.get(cometName);
    }
    */
    }
}
