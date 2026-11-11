package micdoodle8.mods.galacticraft.core.wrappers

import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerHandler
import micdoodle8.mods.galacticraft.core.util.PlayerUtil
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.resources.Identifier as ResourceLocation

open class PlayerGearData
@JvmOverloads constructor(
    private val player: EntityPlayer?,
    private var mask: Int = GCPlayerHandler.GEAR_NOT_PRESENT,
    private var gear: Int = GCPlayerHandler.GEAR_NOT_PRESENT,
    private var leftTank: Int = GCPlayerHandler.GEAR_NOT_PRESENT,
    private var rightTank: Int = GCPlayerHandler.GEAR_NOT_PRESENT,
    private var frequencyModule: Int = GCPlayerHandler.GEAR_NOT_PRESENT,
    private val thermalPadding: IntArray = intArrayOf(
        GCPlayerHandler.GEAR_NOT_PRESENT,
        GCPlayerHandler.GEAR_NOT_PRESENT,
        GCPlayerHandler.GEAR_NOT_PRESENT,
        GCPlayerHandler.GEAR_NOT_PRESENT
    )
) {
    private var parachute: ResourceLocation? = null
    private var shieldController = 0

    open fun getMask(): Int {
        return this.mask
    }

    open fun setMask(mask: Int) {
        this.mask = mask
    }

    open fun getGear(): Int {
        return this.gear
    }

    open fun setGear(gear: Int) {
        this.gear = gear
    }

    open fun getLeftTank(): Int {
        return this.leftTank
    }

    open fun setLeftTank(leftTank: Int) {
        this.leftTank = leftTank
    }

    open fun getRightTank(): Int {
        return this.rightTank
    }

    open fun setRightTank(rightTank: Int) {
        this.rightTank = rightTank
    }

    open fun getPlayer(): EntityPlayer? {
        return this.player
    }

    open fun getParachute(): ResourceLocation? {
        return this.parachute
    }

    open fun setParachute(parachute: ResourceLocation?) {
        this.parachute = parachute
    }

    open fun getFrequencyModule(): Int {
        return this.frequencyModule
    }

    open fun setFrequencyModule(frequencyModule: Int) {
        this.frequencyModule = frequencyModule
    }

    open fun getThermalPadding(slot: Int): Int {
        if (slot >= 0 && slot < this.thermalPadding.size) {
            return this.thermalPadding[slot]
        }

        return -1
    }

    open fun setThermalPadding(slot: Int, thermalPadding: Int) {
        if (slot >= 0 && slot < this.thermalPadding.size) {
            this.thermalPadding[slot] = thermalPadding
        }
    }

    open fun getShieldController(): Int {
        return shieldController
    }

    open fun setShieldController(shieldController: Int) {
        this.shieldController = shieldController
    }

    override fun hashCode(): Int {
        return PlayerUtil.getName(this.player).hashCode()
    }

    override fun equals(obj: Any?): Boolean {
        if (obj is PlayerGearData) {
            return PlayerUtil.getName(obj.player) == PlayerUtil.getName(this.player)
        }

        return false
    }
}
