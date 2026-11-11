package galaxyspace.core.prefab.entities

import galaxyspace.core.GSItems
import galaxyspace.core.prefab.entities.EntityMultiSeatRocket
import galaxyspace.core.prefab.entities.EntityMultiSeatRocketTest
import micdoodle8.mods.galacticraft.api.entity.IRocketType
import net.minecraft.world.item.ItemStack
import net.minecraft.nbt.CompoundTag as NBTTagCompound
import net.minecraft.core.NonNullList
import net.minecraft.util.math.RayTraceResult
import net.minecraft.world.level.Level as World

/**
 * RUS: Класс многоместной ракеты
 * EN: Multi-seats rocket class
 * @author gug2
 */
open class EntityMultiSeatRocketTest : EntityMultiSeatRocket {
    constructor(par1World: World?) : super(par1World) {
        this.setSize(2.0f, 10.0f)
    }

    constructor(
        par1World: World?,
        par2: Double,
        par4: Double,
        par6: Double,
        rocketType: IRocketType.EnumRocketType?
    ) : super(par1World, par2, par4, par6) {
        this.rocketType = rocketType
        this.stacks = NonNullList.withSize<ItemStack>(this.getContainerSize(), ItemStack.EMPTY)
    }

    constructor(
        par1World: World?,
        par2: Double,
        par4: Double,
        par6: Double,
        reversed: Boolean,
        rocketType: IRocketType.EnumRocketType?,
        inv: NonNullList<ItemStack>?
    ) : this(par1World, par2, par4, par6, rocketType) {
        this.stacks = inv
    }

    override fun getYOffset(): Double {
        return 1.5
    }

    override fun getPickedResult(target: RayTraceResult): ItemStack {
        return ItemStack(GSItems.ROCKET_MULTI_SEAT, 1, (this.rocketType)!!.getIndex())
    }

    override fun getRotateOffset(): Float {
        return 1.5f
    }

    override fun getOnPadYOffset(): Double {
        return 0.0
    }

    override fun getItemsDropped(droppedItems: MutableList<ItemStack?>?): MutableList<ItemStack>? {
        super.getItemsDropped(droppedItems)
        val rocket: ItemStack = ItemStack(GSItems.ROCKET_MULTI_SEAT, 1, (this.rocketType)!!.getIndex())
        rocket.setTagCompound(NBTTagCompound())
        rocket.getTagCompound()!!.putInt("RocketFuel", this.fuelTank.getFluidAmount())
        (droppedItems)!!.add(rocket)
        return ((droppedItems as MutableList<ItemStack>?))!!
    }

    override fun getRenderOffsetY(): Float {
        return -1f
    }

    override fun getMaxSeats(): Int {
        return 2
    }

    override fun getRocketTier(): Int {
        return 4
    }

    override fun getCameraZoom(): Float {
        return 15.0f
    }

    override fun defaultThirdPerson(): Boolean {
        return true
    }

    override fun getFuelTankCapacity(): Int {
        return 1500
    }

    override fun getPreLaunchWait(): Int {
        return 400
    }

    override fun writeEntityToNBT(par1NBTTagCompound: NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound)
    }

    override fun readEntityFromNBT(par1NBTTagCompound: NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound)
    }
}
