package micdoodle8.mods.galacticraft.core.inventory

import foundation.persistence.FoundationItemStackPersistence
import micdoodle8.mods.galacticraft.api.inventory.IInventoryGC
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.network.chat.Component
import net.minecraft.util.Prediction
import net.minecraft.world.ContainerHelper
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

open class InventoryExtended : IInventoryGC {
    @JvmField var stacks: NonNullList<ItemStack> = NonNullList.withSize(11, ItemStack.EMPTY)
    private var failedRead = false
    private var unrecognizedSlots: List<CompoundTag> = emptyList()

    override fun getContainerSize(): Int = stacks.size
    override fun isEmpty(): Boolean = stacks.all { it.isEmpty }
    override fun getItem(slot: Int): ItemStack = stacks[slot]
    override fun removeItem(slot: Int, count: Int): ItemStack = ContainerHelper.removeItem(stacks, slot, count).also {
        if (!it.isEmpty) setChanged()
    }
    override fun removeItemNoUpdate(slot: Int): ItemStack = ContainerHelper.takeItem(stacks, slot)
    override fun setItem(slot: Int, stack: ItemStack) {
        stacks[slot] = stack
        if (stack.count > maxStackSize) stack.count = maxStackSize
        setChanged()
    }
    override fun getMaxStackSize(): Int = 64
    override fun setChanged() = Unit
    override fun stillValid(player: Player): Boolean = true
    override fun canPlaceItem(slot: Int, stack: ItemStack): Boolean = false
    override fun clearContent() {
        stacks.clear()
        unrecognizedSlots = emptyList()
        failedRead = false
        setChanged()
    }

    override fun dropExtendedItems(player: Player?) {
        if (isEmpty) return
        val owner = requireNotNull(player)
        if (owner.level().isClientSide) return
        for (slot in stacks.indices) {
            if (!stacks[slot].isEmpty) owner.drop(stacks[slot], true, Prediction.SERVER_ONLY)
            stacks[slot] = ItemStack.EMPTY
        }
        setChanged()
    }

    /** Legacy player Inventory reserved slots 200 through 209 for Galacticraft */
    open fun readFromNBTOld(tags: ListTag, registries: HolderLookup.Provider) = read(tags, registries, true)
    open fun readFromNBT(tags: ListTag, registries: HolderLookup.Provider) = read(tags, registries, false)

    private fun read(tags: ListTag, registries: HolderLookup.Provider, legacySlots: Boolean) {
        failedRead = true
        val decoded = NonNullList.withSize(11, ItemStack.EMPTY)
        val unrecognized = ArrayList<CompoundTag>()
        for (index in 0 until tags.size) {
            val entry = tags.getCompound(index).orElseThrow { IllegalArgumentException("Invalid equipment inventory entry $index") }
            val storedSlot = entry.getByteOr("Slot", 0).toInt() and 255
            val slot = if (legacySlots) {
                if (storedSlot !in 200..209) continue
                storedSlot - 200
            } else storedSlot
            if (slot !in decoded.indices) {
                unrecognized.add(entry.copy())
                continue
            }
            decoded[slot] = FoundationItemStackPersistence.read(entry, registries)
        }
        // A failed decode leaves the existing inventory intact
        stacks = decoded
        unrecognizedSlots = unrecognized
        failedRead = false
        setChanged()
    }

    open fun writeToNBT(tags: ListTag, registries: HolderLookup.Provider): ListTag {
        check(!failedRead) { "Cannot overwrite equipment after a failed import" }
        val encoded = ArrayList<CompoundTag>()
        for (slot in stacks.indices) {
            if (stacks[slot].isEmpty) continue
            val entry = FoundationItemStackPersistence.write(stacks[slot], registries)
            entry.putByte("Slot", slot.toByte())
            encoded.add(entry)
        }
        encoded.addAll(unrecognizedSlots.map { it.copy() })
        // Encode everything before changing the destination list
        encoded.forEach(tags::add)
        return tags
    }

    override fun copyInventory(other: IInventoryGC?) {
        requireNotNull(other)
        check(other !is InventoryExtended || !other.failedRead) { "Cannot copy equipment after a failed import" }
        val copied = NonNullList.withSize(11, ItemStack.EMPTY)
        for (slot in 0 until minOf(copied.size, other.containerSize)) copied[slot] = other.getItem(slot).copy()
        stacks = copied
        unrecognizedSlots = (other as? InventoryExtended)?.unrecognizedSlots?.map { it.copy() }.orEmpty()
        failedRead = false
        setChanged()
    }

    // Internal callers are retained while their menus and capability providers are migrated
    open fun getSizeInventory(): Int = containerSize
    open fun getStackInSlot(slot: Int): ItemStack = getItem(slot)
    open fun decrStackSize(slot: Int, count: Int): ItemStack = removeItem(slot, count)
    open fun removeStackFromSlot(slot: Int): ItemStack = removeItemNoUpdate(slot)
    open fun setInventorySlotContents(slot: Int, stack: ItemStack) = setItem(slot, stack)
    open fun getInventoryStackLimit(): Int = maxStackSize
    open fun markDirty() = setChanged()
    open fun isUsableByPlayer(player: Player): Boolean = stillValid(player)
    open fun isItemValidForSlot(slot: Int, stack: ItemStack): Boolean = canPlaceItem(slot, stack)
    open fun clear() = clearContent()
    open fun getName(): String = "Galacticraft Player Inventory"
    open fun hasCustomName(): Boolean = true
    open fun getDisplayName(): Component = Component.literal(getName())
}
