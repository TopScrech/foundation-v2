package asmodeuscore.core.astronomy;

public class WeatherData {

	public enum WeatherType {
		RAIN,
		METEORIC_RAIN,
		ACID_RAIN,
		DUST_STORM,
		LIGHTNING_STORM,
		SOLAR_STORM,
		FROZEN_STORM;
		
		WeatherType() {
			
		}
	}
	
	private final WeatherType type;
	private double frequence = 1.0D;
	
	public WeatherData(WeatherType type, double frequence) {
		this.type = type;
		this.frequence = frequence;
	}

	public WeatherType getWeatherType() {
		return type;
	}
	
	public double getWeatherFrequence() {
		return this.frequence;
	}
	
	public WeatherData withFrequence(double freq) {
		this.frequence = freq;
		return this;
	}
}
