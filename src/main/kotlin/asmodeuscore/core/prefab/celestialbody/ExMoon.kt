package asmodeuscore.core.prefab.celestialbody

import asmodeuscore.api.space.IExBody
import asmodeuscore.api.dimension.IAdvancedSpace.ClassBody
import asmodeuscore.core.prefab.celestialbody.ExMoon
import asmodeuscore.api.dimension.IAdvancedSpace.TypeBody
import micdoodle8.mods.galacticraft.api.galaxies.Moon
import micdoodle8.mods.galacticraft.api.galaxies.Planet

open class ExMoon constructor(moonName: String?) : Moon((moonName)!!), IExBody {
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


    override fun setParentPlanet(planet: Planet?): ExMoon {
        this.parentPlanet = planet
        return this
    }

    open fun setClassPlanet(classbody: ClassBody?): ExMoon {
        this.classplanet = classbody
        return this
    }

    open fun setGravity(gravity: Float): ExMoon {
        this.gravity = gravity
        return this
    }

    open fun setDayLength(lenght: Long): ExMoon {
        this.day = lenght
        return this
    }

    open fun setAtmosphericPressure(value: Float): ExMoon {
        this.atm_pressure = value
        return this
    }

    open fun setWaterPressure(value: Float): ExMoon {
        this.water_pressure = value
        return this
    }

    open fun setSolarRadiation(mod: Float): ExMoon {
        this.solarradiation = mod
        return this
    }

    open fun setOrbitEccentricity(eccentricityX: Float, eccentricityY: Float): ExMoon {
        this.orbit_eccentricityX = eccentricityX
        this.orbit_eccentricityY = eccentricityY
        return this
    }

    open fun setOrbitOffset(x: Float, y: Float): ExMoon {
        this.orbit_offsetX = x
        this.orbit_offsetY = y
        return this
    }


    open fun getType(): TypeBody {
        return TypeBody.MOON
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
