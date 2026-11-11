package galaxyspace.core.prefab.tile

import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity

/** Native storage and sided access shared by GalaxySpace machine families */
interface MachineInventoryDefaults : WorldlyContainer, IInventoryDefaults {
    fun getInventory(): NonNullList<ItemStack>
    fun getInventoryBlockEntity(): BlockEntity

    override fun isEmpty(): Boolean = getInventory().all { it.isEmpty }
    override fun getContainerSize(): Int = getInventory().size
    override fun getItem(slot: Int): ItemStack = getInventory().getOrNull(slot) ?: ItemStack.EMPTY
    override fun removeItem(slot: Int, count: Int): ItemStack = ContainerHelper.removeItem(getInventory(), slot, count).also {
        if (!it.isEmpty) setChanged()
    }
    override fun removeItemNoUpdate(slot: Int): ItemStack = ContainerHelper.takeItem(getInventory(), slot)
    override fun setItem(slot: Int, stack: ItemStack) {
        getInventory()[slot] = stack
        stack.limitSize(getMaxStackSize(stack))
        setChanged()
    }
    override fun canPlaceItem(slot: Int, stack: ItemStack): Boolean = slot in 0 until containerSize
    override fun getSlotsForFace(side: Direction): IntArray = IntArray(containerSize) { it }
    override fun canPlaceItemThroughFace(slot: Int, stack: ItemStack, side: Direction?): Boolean = canPlaceItem(slot, stack)
    override fun canTakeItemThroughFace(slot: Int, stack: ItemStack, side: Direction): Boolean = slot in 0 until containerSize
    override fun clearContent() {
        if (isEmpty) return
        getInventory().clear()
        setChanged()
    }
    override fun stillValid(player: Player): Boolean = Container.stillValidBlockEntity(getInventoryBlockEntity(), player)
}
