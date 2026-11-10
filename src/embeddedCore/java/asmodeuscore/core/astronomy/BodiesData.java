package asmodeuscore.core.astronomy;

import javax.annotation.Nullable;

import asmodeuscore.api.dimension.IAdvancedSpace.ClassBody;
import asmodeuscore.api.dimension.IAdvancedSpace.StarClass;
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor;
import asmodeuscore.api.dimension.IAdvancedSpace.StarType;
import asmodeuscore.api.dimension.IAdvancedSpace.TypeBody;

public class BodiesData
{
	
	private TypeBody type;
	private ClassBody classbody;
	private StarColor starcolor;
	private StarType startype;
	private float habitableDistance = 0, habitableSize = 0;
	
	private int pressure = 0;

	private boolean solarradiation = false;
	
	//private List<ItemStack> itemsfortravel = new ArrayList();
	
	public BodiesData(TypeBody type)
	{
		this(type, null, 0, false);
	}
	
	public BodiesData(TypeBody type, ClassBody classPlanet)
	{
		this(type, classPlanet, 0, false);
	}
	
	public BodiesData(@Nullable ClassBody classPlanet, int pressure, boolean solarrad)
	{
		this(null, classPlanet, pressure, solarrad);
	}

	public BodiesData(@Nullable TypeBody type, @Nullable ClassBody classPlanet, int pressure, boolean solarrad)
	{
		this.type = type;
		this.classbody = classPlanet;
		this.pressure = pressure;
		this.solarradiation = solarrad;
	}
	
	public BodiesData setStarColor(@Nullable StarColor color)
	{
		this.starcolor = color;
		return this;
	}
	
	@Deprecated
	public BodiesData setStarClass(StarClass starclass)
	{
		this.startype = StarType.valueOf(starclass.name());
		return this;
	}
	
	public BodiesData setStarType(StarType starclass)
	{
		this.startype = starclass;
		return this;
	}
	
	public BodiesData setStarHabitableZone(float distance, float size)
	{
		this.habitableDistance = distance;
		this.habitableSize = size;
		return this;
	}
	
	public int getPressure()
	{
		return pressure;
	}
	
	public boolean isSolarRadiation()
	{
		return solarradiation;
	}
	/*
	public BodiesData addItemStackList(ItemStack[] itemStacks)
	{
		for(ItemStack stacks : itemStacks)
			this.itemsfortravel.add(stacks);
		
		return this;
	}
	
	public BodiesData addItemStack(ItemStack stack)
	{
		this.itemsfortravel.add(stack);
		return this;
	}
	
	public List<ItemStack> getItemStacks()
	{
		return this.itemsfortravel;
	}
	*/
	public StarColor getStarColor() {		
		return this.starcolor;
	}
	
	public TypeBody getType() {
		return this.type;
	}
	
	public ClassBody getPlanetClass() {
		return this.classbody;
	}
	
	public StarType getStarType() {
		return this.startype;
	}
	
	public float getStarHZoneDistance() {
		return this.habitableDistance;
	}
	
	public float getStarHZoneSize() {
		return this.habitableSize;
	}
	
	
	@Override
	public String toString()
	{
		if(getType() != null) {
			if(getType().equals(TypeBody.STAR))
				return "{ Type:" + getType().name() + ", StarClass: " + getStarType() + ", StarColor: " + getStarColor() + "}";
			else if(getPlanetClass() != null)
				return "{ Type:" + getType().name() + ", PlanetClass: " + getPlanetClass().name() + ", Pressure: " + getPressure() + ", SolarRad: " + isSolarRadiation() + "}";
			else
				return "{ Type:" + getType().name() + ", Pressure: " + getPressure() + ", SolarRad: " + isSolarRadiation() + "}";
			
		}
		return super.toString();
		
	}
}
