package asmodeuscore.api.dimension;

import asmodeuscore.core.astronomy.WeatherData;
import asmodeuscore.core.astronomy.WeatherData.WeatherType;

public interface IProviderWeather
{
	public static final WeatherData rain = new WeatherData(WeatherType.RAIN, 1.0F);
	public static final WeatherData acid_rain = new WeatherData(WeatherType.ACID_RAIN, 1.0F);
	public static final WeatherData dust_storm = new WeatherData(WeatherType.DUST_STORM, 1.0F);
	public static final WeatherData lightning_storm = new WeatherData(WeatherType.LIGHTNING_STORM, 1.0F);
	public static final WeatherData meteoric_rain = new WeatherData(WeatherType.METEORIC_RAIN, 1.0F);
	public static final WeatherData frozen_storm = new WeatherData(WeatherType.FROZEN_STORM, 1.0F);

	public WeatherData getWeather();
}