package asmodeuscore.core.prefab.celestialbody

import asmodeuscore.api.space.IExBody
import asmodeuscore.api.dimension.IAdvancedSpace.ClassBody
import asmodeuscore.api.dimension.IAdvancedSpace.TypeBody
import asmodeuscore.core.prefab.celestialbody.ExPlanet
import micdoodle8.mods.galacticraft.api.galaxies.Planet
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem

open class ExPlanet constructor(planetName: String?) : Planet((planetName)!!), IExBody {
    private var classplanet: ClassBody? = ClassBody.SELENA
    private var gravity = 0f
    private var day: Long = 0
    private var solarradiation = 0f
    private var atm_pressure = 0f
    private var water_pressure = 0f
    private var orbit_eccentricityX = 1.0f
    private var orbit_eccentricityY = 1.0f
    private var orbit_offsetX = 0.0f
    private var orbit_offsetY = 0.0f

    override fun setParentSolarSystem(system: SolarSystem?): ExPlanet {
        return (super.setParentSolarSystem(system) as ExPlanet?)!!
    }

    open fun setOrbitEccentricity(eccentricityX: Float, eccentricityY: Float): ExPlanet {
        this.orbit_eccentricityX = eccentricityX
        this.orbit_eccentricityY = eccentricityY
        return this
    }

    open fun setOrbitOffset(x: Float, y: Float): ExPlanet {
        this.orbit_offsetX = x
        this.orbit_offsetY = y
        return this
    }

    open fun setClassPlanet(classbody: ClassBody?): ExPlanet {
        this.classplanet = classbody
        return this
    }

    open fun setGravity(gravity: Float): ExPlanet {
        this.gravity = gravity
        return this
    }

    open fun setDayLength(lenght: Long): ExPlanet {
        this.day = lenght
        return this
    }

    open fun setAtmosphericPressure(value: Float): ExPlanet {
        this.atm_pressure = value
        return this
    }

    open fun setWaterPressure(value: Float): ExPlanet {
        this.water_pressure = value
        return this
    }

    open fun setSolarRadiation(mod: Float): ExPlanet {
        this.solarradiation = mod
        return this
    }

    override fun getSolarRadiationMod(): Float {
        return this.solarradiation
    }

    override fun getClassPlanet(): ClassBody? {
        return this.classplanet
    }

    override fun getGravity(): Float {
        return this.gravity
    }

    override fun getDayLength(): Long {
        return this.day
    }

    override fun getAtmosphericPressure(): Float {
        return this.atm_pressure
    }

    open fun getType(): TypeBody {
        return TypeBody.PLANET
    }

    override fun getXOrbitEccentricity(): Float {
        return this.orbit_eccentricityX
    }

    override fun getYOrbitEccentricity(): Float {
        return this.orbit_eccentricityY
    }

    override fun getXOrbitOffset(): Float {
        return this.orbit_offsetX
    }

    override fun getYOrbitOffset(): Float {
        return this.orbit_offsetY
    }

    override fun getWaterPressure(): Float {
        return this.water_pressure
    }
}
