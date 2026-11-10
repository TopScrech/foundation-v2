package asmodeuscore.core.event;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@Cancelable
public class RadiationEvent extends EntityEvent{

	private int radiationlevel, coefradiation;
	public RadiationEvent(Entity entity, int level, int coef) {
		super(entity);
		this.radiationlevel = level;
		this.coefradiation = coef;
	}
	
	public int getRadiationLevel()
	{
		return this.radiationlevel;
	}

	public int getRadiationSpeed()
	{
		return this.coefradiation;
	}
	
	public int setRadiationSpeed(int speed)
	{
		return this.coefradiation = speed;
	}
}
