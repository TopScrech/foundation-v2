package micdoodle8.mods.galacticraft.core.inventory

import net.minecraft.network.chat.Component
import net.minecraft.world.CompoundContainer
import net.minecraft.world.Container
import net.minecraft.world.Nameable
import net.minecraft.world.entity.ContainerUser
import net.minecraft.world.item.ItemStack

/** A named view over the native single or combined treasure chest storage */
open class InventoryTreasureChest private constructor(
    private val translationKey: String,
    private val upper: Container?,
    private val lower: Container?,
    private val storage: Container
) : Container by storage, Nameable {
    constructor(name: String, upper: Container?, lower: Container?) : this(name, upper, lower, combine(upper, lower))

    override fun getCustomName(): Component? =
        (upper as? Nameable)?.customName ?: (lower as? Nameable)?.customName

    override fun getName(): Component = customName ?: Component.translatable(translationKey)

    // Kotlin delegation does not forward inherited Java interface default methods
    override fun getMaxStackSize(): Int = storage.maxStackSize
    override fun getMaxStackSize(stack: ItemStack): Int = storage.getMaxStackSize(stack)
    override fun startOpen(user: ContainerUser) = storage.startOpen(user)
    override fun stopOpen(user: ContainerUser) = storage.stopOpen(user)
    override fun canPlaceItem(slot: Int, stack: ItemStack): Boolean = storage.canPlaceItem(slot, stack)
    override fun canTakeItem(into: Container, slot: Int, stack: ItemStack): Boolean = storage.canTakeItem(into, slot, stack)
    override fun getEntitiesWithContainerOpen(): List<ContainerUser> = storage.entitiesWithContainerOpen

    fun contains(container: Container): Boolean = upper === container || lower === container

    companion object {
        private fun combine(upper: Container?, lower: Container?): Container = when {
            upper == null -> requireNotNull(lower) { "A treasure chest requires at least one container" }
            lower == null || lower === upper -> upper
            else -> CompoundContainer(upper, lower)
        }
    }
}
