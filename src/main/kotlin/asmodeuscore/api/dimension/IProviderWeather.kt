package asmodeuscore.api.dimension

import asmodeuscore.core.astronomy.WeatherData
import asmodeuscore.core.astronomy.WeatherData.WeatherType
import asmodeuscore.api.dimension.IProviderWeather

interface IProviderWeather {
    fun getWeather(): WeatherData?

    companion object {
        @JvmField
        val rain: WeatherData = WeatherData(WeatherType.RAIN, 1.0)

        @JvmField
        val acid_rain: WeatherData = WeatherData(WeatherType.ACID_RAIN, 1.0)

        @JvmField
        val dust_storm: WeatherData = WeatherData(WeatherType.DUST_STORM, 1.0)

        @JvmField
        val lightning_storm: WeatherData = WeatherData(WeatherType.LIGHTNING_STORM, 1.0)

        @JvmField
        val meteoric_rain: WeatherData = WeatherData(WeatherType.METEORIC_RAIN, 1.0)

        @JvmField
        val frozen_storm: WeatherData = WeatherData(WeatherType.FROZEN_STORM, 1.0)
    }
}
