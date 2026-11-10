package asmodeuscore.core.astronomy

import asmodeuscore.core.astronomy.WeatherData
import asmodeuscore.core.astronomy.WeatherData.WeatherType

open class WeatherData constructor(private val type: WeatherType?, frequence: Double) {
    enum class WeatherType {
        RAIN,
        METEORIC_RAIN,
        ACID_RAIN,
        DUST_STORM,
        LIGHTNING_STORM,
        SOLAR_STORM,
        FROZEN_STORM
    }

    private var frequence = 1.0

    init {
        this.frequence = frequence
    }

    open fun getWeatherType(): WeatherType? {
        return type
    }

    open fun getWeatherFrequence(): Double {
        return this.frequence
    }

    open fun withFrequence(freq: Double): WeatherData {
        this.frequence = freq
        return this
    }
}
