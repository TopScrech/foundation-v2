package asmodeuscore.core.astronomy

import asmodeuscore.api.dimension.IAdvancedSpace.ClassBody
import asmodeuscore.api.dimension.IAdvancedSpace.TypeBody
import asmodeuscore.api.dimension.IAdvancedSpace.StarColor
import asmodeuscore.api.dimension.IAdvancedSpace.StarType
import asmodeuscore.core.astronomy.BodiesData
import asmodeuscore.api.dimension.IAdvancedSpace.StarClass

open class BodiesData
@JvmOverloads constructor(
    private val type: TypeBody?,
    private val classbody: ClassBody? = null,
    pressure: Int = 0,
    solarrad: Boolean = false
) {
    private var starcolor: StarColor? = null
    private var startype: StarType? = null
    private var habitableDistance = 0f
    private var habitableSize = 0f

    private var pressure = 0

    private var solarradiation = false

    constructor(classPlanet: ClassBody?, pressure: Int, solarrad: Boolean) : this(
        null,
        classPlanet,
        pressure,
        solarrad
    )

    //private List<ItemStack> itemsfortravel = new ArrayList();
    init {
        this.pressure = pressure
        this.solarradiation = solarrad
    }

    open fun setStarColor(color: StarColor?): BodiesData {
        this.starcolor = color
        return this
    }

    @Deprecated("")
    open fun setStarClass(starclass: StarClass): BodiesData {
        this.startype = StarType.valueOf(starclass.name)
        return this
    }

    open fun setStarType(starclass: StarType?): BodiesData {
        this.startype = starclass
        return this
    }

    open fun setStarHabitableZone(distance: Float, size: Float): BodiesData {
        this.habitableDistance = distance
        this.habitableSize = size
        return this
    }

    open fun getPressure(): Int {
        return pressure
    }

    open fun isSolarRadiation(): Boolean {
        return solarradiation
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
    open fun getStarColor(): StarColor? {
        return this.starcolor
    }

    open fun getType(): TypeBody? {
        return this.type
    }

    open fun getPlanetClass(): ClassBody? {
        return this.classbody
    }

    open fun getStarType(): StarType? {
        return this.startype
    }

    open fun getStarHZoneDistance(): Float {
        return this.habitableDistance
    }

    open fun getStarHZoneSize(): Float {
        return this.habitableSize
    }


    override fun toString(): String {
        if (getType() != null) {
            if (getType() == TypeBody.STAR) return "{ Type:" + getType()!!.name + ", StarClass: " + getStarType() + ", StarColor: " + getStarColor() + "}"
            else if (getPlanetClass() != null) return "{ Type:" + getType()!!.name + ", PlanetClass: " + getPlanetClass()!!.name + ", Pressure: " + getPressure() + ", SolarRad: " + isSolarRadiation() + "}"
            else return "{ Type:" + getType()!!.name + ", Pressure: " + getPressure() + ", SolarRad: " + isSolarRadiation() + "}"
        }
        return super.toString()
    }
}
