package asmodeuscore.api.space

import asmodeuscore.api.dimension.IAdvancedSpace.ClassBody
import asmodeuscore.api.space.IExBody

interface IExBody {
    fun getClassPlanet(): ClassBody?
    fun getGravity(): Float
    fun getDayLength(): Long
    fun getAtmosphericPressure(): Float
    fun getWaterPressure(): Float
    fun getSolarRadiationMod(): Float

    fun isSolarRadiation(): Boolean {
        return getSolarRadiationMod() > 0.0f
    }

    fun getXOrbitEccentricity(): Float {
        return 0.0f
    }

    fun getYOrbitEccentricity(): Float {
        return 0.0f
    }

    fun getXOrbitOffset(): Float {
        return 0.0f
    }

    fun getYOrbitOffset(): Float {
        return 0.0f
    }
}
