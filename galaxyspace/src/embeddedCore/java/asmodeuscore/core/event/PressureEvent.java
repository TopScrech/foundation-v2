package asmodeuscore.core.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class PressureEvent extends EntityEvent{

	private float pressurelevel;
	public PressureEvent(EntityLivingBase e, float level)
	{
		super(e);
		this.pressurelevel = level;
	}
	
	public int getPressureLevel()
	{
		return (int) this.pressurelevel;
	}
	
	public float getPressureLevelF()
	{
		return this.pressurelevel;
	}
	
	public static class WaterPressure extends EntityEvent
	{
		private float water_pressure;
		public WaterPressure(EntityLivingBase e, float level) {
			super(e);
			this.water_pressure = level;
		}
		
		public float getWaterPressureLevel()
		{
			return this.water_pressure;
		}
	}
}
