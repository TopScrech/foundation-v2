package asmodeuscore.core.astronomy

import asmodeuscore.core.astronomy.BodiesRegistry
import asmodeuscore.api.dimension.IAdvancedSpace.ClassBody
import asmodeuscore.api.dimension.IAdvancedSpace.TypeBody
import asmodeuscore.core.astronomy.BodiesData
import asmodeuscore.core.astronomy.BodiesRegistry.Galaxies
import asmodeuscore.core.astronomy.gui.screen.GuiCustomTeleporting
import asmodeuscore.core.astronomy.gui.screen.GuiCustomTeleporting.TeleportData
import asmodeuscore.core.prefab.celestialbody.ExPlanet
import asmodeuscore.core.prefab.celestialbody.ExMoon
import asmodeuscore.core.astronomy.dimension.world.gen.ACBiome
import asmodeuscore.api.dimension.IAdvancedSpace.TempBody
import asmodeuscore.api.space.IExBody
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody
import micdoodle8.mods.galacticraft.api.galaxies.IChildBody
import micdoodle8.mods.galacticraft.api.galaxies.Moon
import micdoodle8.mods.galacticraft.api.galaxies.Planet
import micdoodle8.mods.galacticraft.api.galaxies.Satellite
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem
import micdoodle8.mods.galacticraft.api.galaxies.Star
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace
import micdoodle8.mods.galacticraft.api.vector.Vector3
import micdoodle8.mods.galacticraft.api.world.AtmosphereInfo
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider
import micdoodle8.mods.galacticraft.core.GalacticraftCore
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedCreeper
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedEnderman
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSkeleton
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedSpider
import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedZombie
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraft.world.WorldProvider
import net.minecraft.world.biome.Biome
import net.minecraftforge.common.util.EnumHelper
import java.util.ArrayList
import java.util.HashMap
import java.util.Locale

object BodiesRegistry {
    @JvmStatic
    fun getGalaxy(name: String?): Galaxies? {
        for (gal: Galaxies in Galaxies.entries) if (gal.getName() == name) return gal

        return null
    }

    @JvmStatic
    fun getGalaxyFromBody(body: CelestialBody?): Galaxies? {
        if (body is Planet) {
            return getGalaxy((body.getParentSolarSystem())!!.getUnlocalizedParentGalaxyName().replace("galaxy.", ""))
        }

        if (body is IChildBody) {
            return getGalaxy(
                (((body as IChildBody).getParentPlanet())!!.getParentSolarSystem())!!.getUnlocalizedParentGalaxyName()
                    .replace("galaxy.", "")
            )
        }

        return null
    }

    //---------------------
    private val data: MutableMap<CelestialBody?, BodiesData?> = (HashMap<Any?, Any?>() as MutableMap<CelestialBody?, BodiesData?>)
    private var max_tier = 3

    @JvmField
    var getDefaultDergrees: Int = 30
    private val bodiesToRegister: MutableList<MutableMap<String?, out CelestialBody?>?> = (ArrayList<Any?>() as MutableList<MutableMap<String?, out CelestialBody?>?>)

    @JvmStatic
    fun setMaxTier(tier: Int) {
        if (getMaxTier() < tier) max_tier = tier
    }

    @JvmStatic
    fun getMaxTier(): Int {
        return max_tier
    }


    /**
     *
     * @param celestialbody
     * @return if there is data - BodiesData, else - default preset:
     * new BodiesData(TypeBody)
     */
    @JvmStatic
    fun getData(body: CelestialBody?): BodiesData? {
        if (body is Planet) return if (data.get(body) != null) data.get(body) else BodiesData(TypeBody.PLANET)

        if (body is Moon) return if (data.get(body) != null) data.get(body) else BodiesData(TypeBody.MOON)

        return data.get(body)
    }

    @JvmStatic
    fun getBodies(): MutableList<MutableMap<String?, out CelestialBody?>?> {
        return bodiesToRegister
    }

    @JvmStatic
    fun registerCelestialBody(body: MutableMap<String?, out CelestialBody?>?) {
        bodiesToRegister.add(body)
    }

    @JvmStatic
    fun registerGalaxy(galaxy: String, image: ResourceLocation?): Galaxies? {
        return EnumHelper.addEnum<Galaxies>(
            Galaxies::class.java,
            galaxy.uppercase(Locale.getDefault()),
            arrayOf<Class<*>>(String::class.java, ResourceLocation::class.java),
            galaxy.lowercase(Locale.getDefault()),
            image
        )
    }

    @JvmStatic
    fun registerClass(classname: String): ClassBody? {
        return EnumHelper.addEnum<ClassBody>(
            ClassBody::class.java,
            classname.uppercase(Locale.getDefault()),
            arrayOf<Class<*>?>()
        )
    }

    @JvmStatic
    fun registerType(typename: String): TypeBody? {
        return EnumHelper.addEnum<TypeBody>(
            TypeBody::class.java,
            typename.uppercase(Locale.getDefault()),
            arrayOf<Class<*>?>()
        )
    }

    @JvmStatic
    fun addTeleportData(dimID: Int, image: ResourceLocation?) {
        val possibleStrings: Array<String?> =
            ((arrayOf<String>("Taking one small step", "Taking one giant leap", "Prepare for entry!") as Array<String?>?))!!
        addTeleportData(dimID, image, possibleStrings)
    }

    @JvmStatic
    fun addTeleportData(dimID: Int, image: ResourceLocation?, text: Array<String?>?) {
        GuiCustomTeleporting.getList().add(TeleportData((image)!!, ((text)!! as Array<String>), dimID))
    }

    @JvmStatic
    fun registerSolarSystem(
        prefix: String,
        name: String,
        galaxy: Galaxies,
        pos: Vector3,
        starname: String?,
        size: Float
    ): SolarSystem {
        return registerSolarSystem(prefix, name, galaxy, pos, starname, size, null)
    }

    @JvmStatic
    fun registerSolarSystem(
        prefix: String,
        name: String,
        galaxy: Galaxies,
        pos: Vector3,
        starname: String?,
        size: Float,
        icon: ResourceLocation?
    ): SolarSystem {
        val body: SolarSystem = SolarSystem(name, galaxy.getName())
        body.setMapPosition(pos)
        val main: Star = Star((starname)!!)
        main.setParentSolarSystem(body)
        main.setTierRequired(-1)
        main.setRelativeSize(size)
        main.setBodyIcon(
            if (icon != null) icon else ResourceLocation(
                prefix,
                "textures/gui/celestialbodies/" + name + "/" + starname + ".png"
            )
        )

        body.setMainStar(main)

        return body
    }

    @JvmStatic
    fun registerExPlanet(system: SolarSystem, name: String?, prefix: String, distancefromcenter: Float): ExPlanet {
        val body: ExPlanet = (ExPlanet(name).setParentSolarSystem(system))!!
        val path: String? = system.getTranslationKey().replace("solarsystem.", "")
        body.setRingColorRGB(0.0f, 0.4f, 0.9f)
        body.setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(distancefromcenter, distancefromcenter))
        body.setBodyIcon(ResourceLocation(prefix, "textures/gui/celestialbodies/" + path + "/" + name + ".png"))

        return body
    }

    @JvmStatic
    fun registerExMoon(parent: Planet, name: String?, prefix: String, distancefromcenter: Float): ExMoon {
        val body: ExMoon = ExMoon(name).setParentPlanet(parent)
        val path: String? = (parent.getParentSolarSystem())!!.getTranslationKey().replace("solarsystem.", "")
        body.setRingColorRGB(0.0f, 0.4f, 0.9f)
        body.setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(distancefromcenter, distancefromcenter))
        body.setBodyIcon(ResourceLocation(prefix, "textures/gui/celestialbodies/" + path + "/moons/" + name + ".png"))

        return body
    }

    @JvmStatic
    fun setOrbitData(body: CelestialBody, phase: Float, size: Float, relativetime: Float) {
        setOrbitData(body, phase, size, relativetime, 0f, 0f, 0f, 0f)
    }

    @JvmStatic
    fun setOrbitData(
        body: CelestialBody,
        phase: Float,
        size: Float,
        relativetime: Float,
        eccentricityX: Float,
        eccentricityY: Float,
        orbitOffsetX: Float,
        orbitOffsetY: Float
    ) {
        body.setPhaseShift(phase)
        body.setRelativeSize(size)
        body.setRelativeOrbitTime(relativetime)
        if (body is ExPlanet) {
            body.setOrbitEccentricity(eccentricityX, eccentricityY)
            body.setOrbitOffset(orbitOffsetX, orbitOffsetY)
        }
        if (body is ExMoon) {
            body.setOrbitEccentricity(eccentricityX, eccentricityY)
            body.setOrbitOffset(orbitOffsetX, orbitOffsetY)
        }
    }

    @JvmStatic
    @Deprecated("")
    fun setPlanetData(
        body: CelestialBody?,
        atm_pressure: Int,
        day_length: Long,
        gravity: Float,
        isSolarRadiation: Boolean
    ) {
        setPlanetData(body, atm_pressure.toFloat(), day_length, gravity, isSolarRadiation)
    }

    @JvmStatic
    fun setPlanetData(
        body: CelestialBody?,
        water_pressure: Float,
        atm_pressure: Float,
        day_length: Long,
        gravity: Float,
        isSolarRadiation: Boolean
    ) {
        if (body is ExPlanet) body.setWaterPressure(water_pressure)
        if (body is ExMoon) body.setWaterPressure(water_pressure)

        setPlanetData(body, atm_pressure, day_length, gravity, isSolarRadiation)
    }

    @JvmStatic
    fun setPlanetData(
        body: CelestialBody?,
        atm_pressure: Float,
        day_length: Long,
        gravity: Float,
        isSolarRadiation: Boolean
    ) {
        if (body is ExPlanet) {
            body.setGravity(gravity)
            body.setAtmosphericPressure(atm_pressure)
            body.setDayLength(day_length)
            body.setSolarRadiation(if (isSolarRadiation) 1.0f else 0.0f)
        }

        if (body is ExMoon) {
            body.setGravity(gravity)
            body.setAtmosphericPressure(atm_pressure)
            body.setDayLength(day_length)
            body.setSolarRadiation(if (isSolarRadiation) 1.0f else 0.0f)
        }
    }


    @JvmStatic
    fun setAtmosphere(
        body: CelestialBody,
        breathable: Boolean,
        precipitation: Boolean,
        corrosive: Boolean,
        temperature: Float,
        windLevel: Float,
        density: Float
    ) {
        body.setAtmosphere(AtmosphereInfo(breathable, precipitation, corrosive, temperature, windLevel, density))
    }

    @JvmStatic
    fun setProviderData(
        body: CelestialBody,
        provider: Class<out WorldProvider>?,
        dimID: Int,
        tier: Int,
        vararg biome: Biome?
    ) {
        body.setTierRequired(tier)
        body.setDimensionInfo(dimID, provider)
        if (biome != null) body.setBiomeInfo(*biome)
        body.addMobInfo(Biome.SpawnListEntry(EntityEvolvedZombie::class.java, 8, 2, 3))
        body.addMobInfo(Biome.SpawnListEntry(EntityEvolvedSpider::class.java, 8, 2, 3))
        body.addMobInfo(Biome.SpawnListEntry(EntityEvolvedSkeleton::class.java, 8, 2, 3))
        body.addMobInfo(Biome.SpawnListEntry(EntityEvolvedCreeper::class.java, 8, 2, 3))
        body.addMobInfo(Biome.SpawnListEntry(EntityEvolvedEnderman::class.java, 10, 1, 4))
    }

    @JvmStatic
    fun setClassBody(body: CelestialBody?, classbody: ClassBody?) {
        if (body is ExPlanet) body.setClassPlanet(classbody)
        if (body is ExMoon) body.setClassPlanet(classbody)
    }

    @JvmStatic
    fun registerPlanet(
        name: String?,
        prefix: String,
        provider: Class<out WorldProvider>?,
        dimID: Int,
        tier: Int,
        phase: Float,
        size: Float,
        distancefromcenter: Float,
        relativetime: Float,
        vararg biome: Biome?
    ): Planet {
        return registerPlanet(
            (GalacticraftCore.solarSystemSol)!!,
            name,
            prefix,
            provider,
            dimID,
            tier,
            phase,
            size,
            distancefromcenter,
            relativetime,
            *biome
        )
    }

    @JvmStatic
    fun registerPlanet(
        system: SolarSystem,
        name: String?,
        prefix: String,
        provider: Class<out WorldProvider>?,
        dimID: Int,
        tier: Int,
        phase: Float,
        size: Float,
        distancefromcenter: Float,
        relativetime: Float,
        vararg biome: Biome?
    ): Planet {
        val body: Planet = Planet((name)!!).setParentSolarSystem(system)
        val path: String? = system.getTranslationKey().replace("solarsystem.", "")
        body.setRingColorRGB(0.0f, 0.4f, 0.9f)
        body.setPhaseShift(phase)
        body.setRelativeSize(size)
        body.setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(distancefromcenter, distancefromcenter))
        body.setRelativeOrbitTime(relativetime)
        body.setBodyIcon(ResourceLocation(prefix, "textures/gui/celestialbodies/" + path + "/" + name + ".png"))

        //body.setAtmosphere(new AtmosphereInfo(breathable, precipitation, corrosive, relativeTemperature, windLevel, density))
        if (provider != null) {
            body.setTierRequired(tier)
            body.setDimensionInfo(dimID, provider)
            if (biome != null) body.setBiomeInfo(*biome)
            body.addMobInfo(Biome.SpawnListEntry(EntityEvolvedZombie::class.java, 8, 2, 3))
            body.addMobInfo(Biome.SpawnListEntry(EntityEvolvedSpider::class.java, 8, 2, 3))
            body.addMobInfo(Biome.SpawnListEntry(EntityEvolvedSkeleton::class.java, 8, 2, 3))
            body.addMobInfo(Biome.SpawnListEntry(EntityEvolvedCreeper::class.java, 8, 2, 3))
            body.addMobInfo(Biome.SpawnListEntry(EntityEvolvedEnderman::class.java, 10, 1, 4))
        }

        return body
    }

    @JvmStatic
    fun registerMoon(
        parent: Planet,
        name: String?,
        prefix: String,
        provider: Class<out WorldProvider>?,
        dimID: Int,
        tier: Int,
        phase: Float,
        size: Float,
        distancefromcenter: Float,
        relativetime: Float,
        vararg biome: Biome?
    ): Moon {
        val body: Moon = Moon((name)!!).setParentPlanet(parent)
        val path: String? = (parent.getParentSolarSystem())!!.getTranslationKey().replace("solarsystem.", "")

        body.setRingColorRGB(0.0f, 0.4f, 0.9f)
        body.setPhaseShift(phase)
        body.setRelativeSize(size)
        body.setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(distancefromcenter, distancefromcenter))
        body.setRelativeOrbitTime(relativetime)
        body.setBodyIcon(ResourceLocation(prefix, "textures/gui/celestialbodies/" + path + "/moons/" + name + ".png"))

        if (provider != null) {
            body.setTierRequired(tier)
            body.setDimensionInfo(dimID, provider)
            if (biome != null) body.setBiomeInfo(*biome)
            body.addMobInfo(Biome.SpawnListEntry(EntityEvolvedZombie::class.java, 8, 2, 3))
            body.addMobInfo(Biome.SpawnListEntry(EntityEvolvedSpider::class.java, 8, 2, 3))
            body.addMobInfo(Biome.SpawnListEntry(EntityEvolvedSkeleton::class.java, 8, 2, 3))
            body.addMobInfo(Biome.SpawnListEntry(EntityEvolvedCreeper::class.java, 8, 2, 3))
            body.addMobInfo(Biome.SpawnListEntry(EntityEvolvedEnderman::class.java, 10, 1, 4))
        }

        return body
    }

    @JvmStatic
    fun registerSatellite(
        parent: Planet,
        provider: Class<out WorldProvider>?,
        dimID: Int,
        dimIDStatic: Int
    ): Satellite {
        val body: Satellite = Satellite("spacestation." + parent.getTranslationKey().replace("planet.", ""))
        body.setParentBody(parent)
        body.setRelativeDistanceFromCenter(CelestialBody.ScalableDistance(5.5f, 5.5f))
        body.setRingColorRGB(0.0f, 0.4f, 0.9f)
        body.setBodyIcon(ResourceLocation("galacticraftcore:textures/gui/celestialbodies/space_station.png"))

        if (provider != null) {
            if (parent.getReachable()) body.setTierRequired(parent.getTierRequirement())
            else body.setTierRequired(max_tier)

            body.setDimensionInfo(dimID, dimIDStatic, provider)
            body.setBiomeInfo(ACBiome.Companion.ACSpace)
        }
        return body
    }

    /**
     *
     * @param body - Celestial body
     * @param bodydata - data with additions info.
     */
    @JvmStatic
    fun registerBodyData(body: CelestialBody?, bodydata: BodiesData?) {
        data.put(body, bodydata)
        //System.out.println("Successful register data: " + body.getName() + " | " + bodydata.toString());
    }

    /**
     *
     * @param Si - real gravity value.
     * @return Transformed value for Galacticraft gravity system.
     */
    @JvmStatic
    fun calculateGravity(Si: Float): Float {
        val i: Float = (9.81f - Si) * (0.085f / 9.81f)
        val k: Float = Math.round(i * 1000).toFloat()
        return k / 1000
    }

    @JvmStatic
    fun getLocale(temp: Any): String? {
        return GCCoreUtil.translate("gui.info." + temp.toString().lowercase(Locale.getDefault()) + ".name")
    }

    @JvmStatic
    fun classPlanet(body: CelestialBody, dim: WorldProvider?): String? {
        val data: BodiesData? = getData(body)
        var type: String? = ""

        if (body is Star || data!!.getType() == TypeBody.STAR) {
            if (data != null && data.getStarType() != null) {
                if (data.getStarColor() != null) return getLocale((data.getStarColor())!!) + " " + getLocale(
                    (data.getStarType())!!.name.lowercase(
                        Locale.getDefault()
                    )
                )

                return getLocale((data.getStarType())!!.name.lowercase(Locale.getDefault()))
            }
        } else if (body.getReachable()) {
            if (body === GalacticraftCore.planetOverworld) return getLocale(TempBody.COMFORT) + " " + getLocale(
                ClassBody.TERRA
            )

            val temp: Float = body.atmosphere.thermalLevel()
            val breath: Boolean = body.atmosphere.isBreathable()
            var tempbody: TempBody = TempBody.COMFORT


            // 2.0 --- +oo
            if (temp >= 2.0f) tempbody = TempBody.HOT
            // 1.0 --- 1.9
            if (temp >= 1.0f && temp < 2.0) tempbody = TempBody.WARM
            // -0.9 --- 0.9 T
            if (temp > -1.0f && temp < 1.0f) tempbody = TempBody.COMFORT
            // -1.9 --- -1.0F
            if (temp > -2.0 && temp <= -1.0f) tempbody = TempBody.COOL
            // -oo --- -2.0F
            if (temp <= -2.0f) tempbody = TempBody.COLD

            type = getLocale(tempbody)

            if (body is IExBody) {
                val planet: IExBody = body as IExBody
                if (planet.getClassPlanet() != null) return type + " " + getLocale((planet.getClassPlanet())!!)
            }
            if (dim is WorldProviderSpace && dim.getDayLength().toDouble() == 0.0) return getLocale(ClassBody.ASTEROID)

            if (breath) return type + " " + getLocale(ClassBody.TERRA)

            if (!breath) {
                if (body.atmosphere.hasNoGases()) return type + " " + getLocale(ClassBody.SELENA)


                /*	if (dim instanceof IGalacticraftWorldProvider && ((IGalacticraftWorldProvider) dim).getCelestialBody() instanceof IExBody && ((IExBody) ((IGalacticraftWorldProvider) dim).getCelestialBody()).getClassPlanet() == ClassBody.TITAN) {
					if (temp > 2.5F)
						type = BodiesRegistry.getLocale(TempBody.HOT);
					else if (temp < -2.5F)
						type = BodiesRegistry.getLocale(TempBody.ICY);
					else
						type = BodiesRegistry.getLocale(TempBody.COMFORT);
					return type + " " + BodiesRegistry.getLocale(ClassBody.TITAN);
				}*/
                return type + " " + getLocale(ClassBody.DESERT)
            }

            if (temp < -3.0f) return getLocale(ClassBody.ICEWORLD)
        }

        if (data != null && data.getPlanetClass() != null) return getLocale((data.getPlanetClass())!!.name.lowercase(Locale.getDefault()))


        return GCCoreUtil.translate("gui.message.unknown")
    }

    @JvmStatic
    fun getDistanceFromCenter(world: World): Float {
        if (world.provider is IGalacticraftWorldProvider) {
            val current_body: CelestialBody? = (world.provider as IGalacticraftWorldProvider).getCelestialBody()
            var distance: Float = 0.0f
            if (current_body is Planet) {
                distance = current_body.getRelativeDistanceFromCenter().unScaledDistance
            }
            if (current_body is IChildBody) {
                distance =
                    ((current_body as IChildBody).getParentPlanet())!!.getRelativeDistanceFromCenter().unScaledDistance
            }
            return distance
        }
        return 0f
    }

    enum class Galaxies
        (
        private val serializedName: String //LMC("lmc");
        , image: ResourceLocation?
    ) {
        /** Our Galaxy. Milky Way   */
        MILKYWAY("milky_way", null),

        /** Andromeda   */
        ANDROMEDA("andromeda", null);
        /**** Small Magellanic Cloud  **/
        // SMC("smc"),
        /**** Large Magellanic Cloud  **/
        private val image: ResourceLocation?

        init {
            this.image = image
        }

        fun getName(): String {
            return this.serializedName
        }

        fun getImage(): ResourceLocation? {
            return this.image
        }
    }
}
