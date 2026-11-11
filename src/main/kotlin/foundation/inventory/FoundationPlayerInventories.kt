package foundation.inventory

import net.minecraft.world.Container
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack

/** Fixed slot views preserve writes without exposing or copying the native backing storage */
class FoundationInventorySlots(container: Container, slots: IntArray) : AbstractMutableList<ItemStack>() {
    private val container = container
    private val slots = slots.copyOf()

    init {
        require(this.slots.toSet().size == this.slots.size) { "Duplicate inventory slot" }
        require(this.slots.all { it in 0 until container.containerSize }) { "Inventory slot outside container" }
    }

    override val size: Int get() = slots.size
    override fun get(index: Int): ItemStack = container.getItem(slots[index])
    override fun set(index: Int, element: ItemStack): ItemStack {
        val slot = slots[index]
        val previous = container.getItem(slot)
        container.setItem(slot, element)
        container.setChanged()
        return previous
    }
    override fun clear() {
        slots.forEach { container.setItem(it, ItemStack.EMPTY) }
        container.setChanged()
    }
    override fun add(index: Int, element: ItemStack): Unit = throw UnsupportedOperationException("Inventory slots have a fixed size")
    override fun removeAt(index: Int): ItemStack = throw UnsupportedOperationException("Inventory slots have a fixed size")
}

object FoundationPlayerInventories {
    private val mainSlots = IntArray(Inventory.INVENTORY_SIZE) { it }
    private val armorSlots = intArrayOf(
        EquipmentSlot.FEET.getIndex(Inventory.INVENTORY_SIZE),
        EquipmentSlot.LEGS.getIndex(Inventory.INVENTORY_SIZE),
        EquipmentSlot.CHEST.getIndex(Inventory.INVENTORY_SIZE),
        EquipmentSlot.HEAD.getIndex(Inventory.INVENTORY_SIZE)
    )
    private val offhandSlots = intArrayOf(Inventory.SLOT_OFFHAND)

    fun main(container: Container): MutableList<ItemStack> = FoundationInventorySlots(container, mainSlots)
    fun armor(container: Container): MutableList<ItemStack> = FoundationInventorySlots(container, armorSlots)
    fun offhand(container: Container): MutableList<ItemStack> = FoundationInventorySlots(container, offhandSlots)
}

val Inventory.mainItems: MutableList<ItemStack>
    get() = FoundationPlayerInventories.main(this)

val Inventory.armorItems: MutableList<ItemStack>
    get() = FoundationPlayerInventories.armor(this)

val Inventory.offhandItems: MutableList<ItemStack>
    get() = FoundationPlayerInventories.offhand(this)

fun Inventory.getArmorItem(index: Int): ItemStack = armorItems[index]
