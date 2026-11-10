package micdoodle8.mods.galacticraft.api.world

import java.util.ArrayList

open class AtmosphereInfo

/**
 * @param breathable - supply null here to have the AtmosphereInfo object test the atmospheric composition for oxygen and CO2, or true or false to override
 * @param precipitation - true for rain etc, false for none
 * @param corrosive - true for atmosphere which can corrode armor
 * @param relativeTemperature - the thermal level relative to Overworld: 0.0F is default
 * @param windLevel - the wind level
 * @param density - the atmospheric density (affects sounds and meteor frequency): 1.0F is like Overworld
 */ constructor(
    private val isBreathable: Boolean?,
    private val hasPrecipitation: Boolean,
    private val isCorrosive: Boolean,
    private val thermalLevel: Float,
    private val windLevel: Float,
    private val density: Float
) {
    @JvmField
    var composition: ArrayList<EnumAtmosphericGas> = ArrayList<EnumAtmosphericGas>()

    open fun isBreathable(): Boolean {
        if (this.isBreathable == null) {
            return this.isGasPresent(EnumAtmosphericGas.OXYGEN) && !this.isGasPresent(EnumAtmosphericGas.CO2)
        }
        return this.isBreathable
    }

    open fun hasPrecipitation(): Boolean {
        return this.hasPrecipitation
    }

    open fun isCorrosive(): Boolean {
        return this.isCorrosive
    }

    open fun thermalLevel(): Float {
        return this.thermalLevel
    }

    open fun windLevel(): Float {
        return this.windLevel
    }

    open fun relativeDensity(): Float {
        return this.density
    }

    open fun isGasPresent(gas: EnumAtmosphericGas?): Boolean {
        return this.composition.contains(gas)
    }

    open fun hasNoGases(): Boolean {
        return this.composition.isEmpty()
    }
}
