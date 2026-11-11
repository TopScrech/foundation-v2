package foundation.level

import foundation.math.LegacyTerrainMath
import net.minecraft.world.attribute.EnvironmentAttributeSystem
import net.minecraft.world.attribute.EnvironmentAttributes
import net.minecraft.world.attribute.WeatherAttributes
import net.minecraft.world.clock.ClockInstance
import net.minecraft.world.level.Level

object FoundationPlanetClocks {
    /** Build native dynamic layers without reading a partially constructed level's attribute system */
    fun install(builder: EnvironmentAttributeSystem.Builder, level: Level): Boolean {
        val type = level.dimensionType()
        val environment = type.attributes().applyModifier(FoundationSpaceEnvironment.ATTRIBUTE, FoundationSpaceEnvironment.DEFAULT)
        if (environment.body.isEmpty) return false
        if (environment.dayLength > 0L) {
            val clock = type.defaultClock().orElseThrow { IllegalStateException("Missing planet clock for ${environment.body.orElseThrow()}") }
            addLayers(builder, environment) { level.clockManager().getInstance(clock) }
        }
        type.timelines().forEach { builder.addTimelineLayer(it, level.clockManager()) }
        if (level.canHaveWeather()) addWeather(builder, environment, WeatherAttributes.WeatherAccess.from(level))
        return true
    }

    fun addWeather(builder: EnvironmentAttributeSystem.Builder, environment: FoundationSpaceEnvironment, weather: WeatherAttributes.WeatherAccess) {
        if (environment.atmosphere.precipitation) WeatherAttributes.addBuiltinLayers(builder, weather)
    }

    fun addLayers(builder: EnvironmentAttributeSystem.Builder, environment: FoundationSpaceEnvironment, clock: () -> ClockInstance) {
        require(environment.dayLength > 0L)
        fun angle(): Float = celestialAngle(clock().totalTicks(), 0f, environment.dayLength)
        builder.addTimeBasedLayer(EnvironmentAttributes.SUN_ANGLE) { _, _ -> angle() * 360f }
        builder.addTimeBasedLayer(EnvironmentAttributes.MOON_ANGLE) { _, _ -> (angle() * 360f + 180f) % 360f }
        builder.addTimeBasedLayer(EnvironmentAttributes.STAR_ANGLE) { _, _ -> angle() * 360f }
        builder.addTimeBasedLayer(EnvironmentAttributes.STAR_BRIGHTNESS) { _, _ -> starBrightness(angle(), environment.starScale, environment.starOffset) }
        builder.addTimeBasedLayer(EnvironmentAttributes.SKY_LIGHT_LEVEL) { _, _ -> 15f - darkness(angle()) * 11f }
        builder.addTimeBasedLayer(EnvironmentAttributes.SKY_LIGHT_FACTOR) { _, _ -> 1f - darkness(angle()) * 0.8f }
    }

    /** Retain the original float lookup arithmetic, modulo, wrap, and easing */
    fun celestialAngle(time: Long, partial: Float, dayLength: Long): Float {
        require(dayLength > 0L)
        val tick = (time % dayLength).toInt()
        var angle = (tick + partial) / dayLength - 0.25f
        if (angle < 0f) ++angle
        if (angle > 1f) --angle
        val linear = angle
        angle = 0.5f - LegacyTerrainMath.cos(angle * 3.1415927f) / 2f
        return linear + (angle - linear) / 3f
    }

    fun starBrightness(angle: Float, scale: Float, offset: Float): Float {
        val light = (1f - (LegacyTerrainMath.cos(angle * (Math.PI.toFloat() * 2f)) * 2f + 0.25f)).coerceIn(0f, 1f)
        return light * light * scale + offset
    }

    private fun darkness(angle: Float): Float =
        (1f - (LegacyTerrainMath.cos(angle * (Math.PI.toFloat() * 2f)) * 2f + 0.5f)).coerceIn(0f, 1f)
}
