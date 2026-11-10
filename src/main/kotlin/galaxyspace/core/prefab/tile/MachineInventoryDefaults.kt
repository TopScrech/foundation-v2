package galaxyspace.core.prefab.tile

import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ISidedInventory
import net.minecraft.inventory.ItemStackHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList

/** Inventory operations shared by the project's advanced, oxygen, and generator tiles */
interface MachineInventoryDefaults : ISidedInventory, IInventoryDefaults {
    fun getInventory(): NonNullList<ItemStack>
    override fun isEmpty(): Boolean = getInventory().all { it.isEmpty }
    override fun getSizeInventory(): Int = getInventory().size
    override fun getStackInSlot(index: Int): ItemStack = getInventory()[index]
    override fun decrStackSize(index: Int, count: Int): ItemStack {
        val stack = ItemStackHelper.getAndSplit(getInventory(), index, count)
        if (!stack.isEmpty) markDirty()
        return stack
    }
    override fun removeStackFromSlot(index: Int): ItemStack {
        val stack = ItemStackHelper.getAndRemove(getInventory(), index)
        if (!stack.isEmpty) markDirty()
        return stack
    }
    override fun setInventorySlotContents(index: Int, stack: ItemStack) {
        getInventory()[index] = stack
        if (stack.count > getInventoryStackLimit()) stack.count = getInventoryStackLimit()
        markDirty()
    }
    override fun getInventoryStackLimit(): Int = 64
    override fun isItemValidForSlot(index: Int, stack: ItemStack): Boolean = true
    override fun getSlotsForFace(side: EnumFacing?): IntArray = IntArray(getSizeInventory()) { it }
    override fun canInsertItem(index: Int, stack: ItemStack, side: EnumFacing?): Boolean = isItemValidForSlot(index, stack)
    override fun canExtractItem(index: Int, stack: ItemStack, side: EnumFacing?): Boolean = true
    override fun clear() { getInventory().clear() }
    override fun isUsableByPlayer(player: EntityPlayer): Boolean {
        val tile = this as net.minecraft.tileentity.TileEntity
        return tile.world.getTileEntity(tile.pos) === tile && player.getDistanceSq(tile.pos.x + 0.5, tile.pos.y + 0.5, tile.pos.z + 0.5) <= 64.0
    }
}
