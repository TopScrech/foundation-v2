package asmodeuscore.core.utils;

import net.minecraft.entity.ai.attributes.BaseAttribute;
import net.minecraft.entity.ai.attributes.IAttribute;

public class ACAttributePlayer {

	public static final IAttribute RADIATION_LVL = new BaseAttribute(null, "radiation", 0)
	{
		@Override
		public double clampValue(double value)
		{
			return value;
		}
	}.setShouldWatch(true);
	
	public static final IAttribute TEST = new BaseAttribute(null, "test", 0)
	{
		@Override
		public double clampValue(double value)
		{
			return value;
		}
	}.setShouldWatch(true);
}
