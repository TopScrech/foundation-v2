package galaxyspace.api.item

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack

interface IJetpackArmor {
    fun consumeFuel(stack: ItemStack?, fuel: Int)

    fun decrementFuel(stack: ItemStack?)

    fun getFuel(stack: ItemStack?): Int

    fun canFly(stack: ItemStack?, player: EntityPlayer?): Boolean

    fun isActivated(stack: ItemStack?): Boolean

    fun switchState(stack: ItemStack?, state: Boolean)

    fun getFireStreams(stack: ItemStack?): Int

    companion object {
        const val TAG_FUEL: String = "jetpack:fuel"

        const val TAG_ACT: String = "jetpack:activated"
    }
}
