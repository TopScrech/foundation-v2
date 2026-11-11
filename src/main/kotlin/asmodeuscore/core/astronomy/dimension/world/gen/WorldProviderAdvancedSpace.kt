package asmodeuscore.core.astronomy.dimension.world.gen

import asmodeuscore.core.astronomy.BodiesRegistry
import asmodeuscore.core.astronomy.BodiesData
import asmodeuscore.api.space.IExBody
import asmodeuscore.api.dimension.IAdvancedSpace
import galaxyspace.core.configs.GSConfigCore
import micdoodle8.mods.galacticraft.api.galaxies.IChildBody
import micdoodle8.mods.galacticraft.api.galaxies.Moon
import micdoodle8.mods.galacticraft.api.prefab.world.gen.WorldProviderSpace
import micdoodle8.mods.galacticraft.api.world.IExitHeight
import micdoodle8.mods.galacticraft.api.world.ISolarLevel
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore
import net.minecraft.util.Mth as MathHelper

abstract class WorldProviderAdvancedSpace : WorldProviderSpace(), ISolarLevel, IExitHeight, IAdvancedSpace {
    private val data: BodiesData? = BodiesRegistry.getData(this.getCelestialBody())

    override fun getGravity(): Float {
        if (this.getCelestialBody() is IExBody) return (this.getCelestialBody() as IExBody).getGravity()

        return 0.0058f
    }

    override fun getThermalLevelModifier(): Float {
        if (enableAdvancedThermalLevel() && this.world != null) {
            val angle: Float = this.world.getCelestialAngle(this.getDayLength().toFloat())
            var value: Float = MathHelper.cos(angle * Math.PI.toFloat() * 2.0f) * getThermalValueMod()

            val def: Float = (this.getCelestialBody())!!.atmosphere.thermalLevel()
            if (def < 0.0) value *= -1f

            if (def == 0f) return value
            if (value * def + def < -9.102f) return -9.102f

            return if (def == 0f) value else value * def + def
        }
        return (this.getCelestialBody())!!.atmosphere.thermalLevel()
    }

    protected open fun getThermalValueMod(): Float {
        return 0.5f
    }

    override fun getDayLength(): Long {
        if (this.getCelestialBody() is IExBody) return (this.getCelestialBody() as IExBody).getDayLength()

        return 24000L
    }

    override fun canSpaceshipTierPass(tier: Int): Boolean {
        return tier >= (this.getCelestialBody())!!.getTierRequirement()
    }

    fun getSolarSizeForMoon(): Float {
        return 1.0f / ((this.getCelestialBody() as Moon).getParentPlanet())!!
            .getRelativeDistanceFromCenter().unScaledDistance
    }

    override fun getSolarEnergyMultiplier(): Double {
        var solarMultiplier: Double = -0.5
        if (solarMultiplier < 0.0) {
            val s: Double =
                (if (this.getCelestialBody() is IChildBody) this.getSolarSizeForMoon() else this.getSolarSize()).toDouble()
            solarMultiplier += s * s * ConfigManagerCore.spaceStationEnergyScalar
        }
        return solarMultiplier
    }

    override fun getSolarWindMultiplier(): Double {
        var solarMultiplier: Double = -1.0
        if (solarMultiplier < 0.0) {
            val s: Double =
                (if (this.getCelestialBody() is IChildBody) this.getSolarSizeForMoon() else this.getSolarSize()).toDouble()
            solarMultiplier = s * s
        }
        return solarMultiplier
    }

    override fun getYCoordinateToTeleport(): Double {
        return 1000.0
    }

    override fun getMoonPhase(time: Long): Int {
        val timeday: Long = if (this.getDayLength() > 0) this.getDayLength() else 1
        return (time / timeday % 8L + 8L).toInt() % 8
    }

    open fun enableAdvancedThermalLevel(): Boolean {
        return GSConfigCore.enableAdvancedThermalSystem
    }
}
