package foundation.verification

import foundation.item.FoundationItemVariants
import micdoodle8.mods.galacticraft.core.inventory.InventoryTreasureChest
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.Nameable
import net.minecraft.world.SimpleContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.ContainerUser
import net.minecraft.world.level.block.entity.ContainerOpenersCounter

fun verifyTreasureInventories() {
    class NamedFixture(size: Int, var label: Component?) : SimpleContainer(size), Nameable {
        var changes = 0
        var opens = 0
        var closes = 0
        override fun getName(): Component = label ?: Component.translatable("container.chest")
        override fun getCustomName(): Component? = label
        override fun setChanged() { changes++ }
        override fun getMaxStackSize(): Int = 64
        override fun canPlaceItem(slot: Int, stack: ItemStack): Boolean = slot == 1
        override fun startOpen(user: ContainerUser) { opens++ }
        override fun stopOpen(user: ContainerUser) { closes++ }
    }
    val first = NamedFixture(2, null)
    val second = NamedFixture(3, Component.literal("Lower chest"))
    val chest = InventoryTreasureChest("container.treasurechest.name", first, second)
    check(chest.containerSize == 5 && chest.isEmpty)
    check(chest.maxStackSize == 64)
    check(chest.hasCustomName() && chest.name.string == "Lower chest")
    first.label = Component.literal("Upper chest")
    check(chest.name.string == "Upper chest" && chest.displayName == chest.name)
    check(chest.contains(first) && chest.contains(second) && !chest.contains(SimpleContainer(1)))
    val moon = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse("galacticraftcore:item_basic_moon")))
    chest.setItem(4, FoundationItemVariants.stack(moon, 7, 2))
    check(first.isEmpty && second.getItem(2).count == 7)
    val removed = chest.removeItem(4, 3)
    check(!chest.canPlaceItem(2, removed) && chest.canPlaceItem(3, removed))
    check(removed.count == 3 && FoundationItemVariants.metadata(removed) == 2)
    check(chest.removeItemNoUpdate(4).count == 4 && second.isEmpty)
    first.changes = 0
    second.changes = 0
    chest.setChanged()
    check(first.changes == 1 && second.changes == 1)
    val user = object : ContainerUser {
        override fun hasContainerOpen(counter: ContainerOpenersCounter, pos: BlockPos): Boolean = false
        override fun getContainerInteractionRange(): Double = 4.5
    }
    chest.startOpen(user)
    chest.stopOpen(user)
    check(first.opens == 1 && second.opens == 1 && first.closes == 1 && second.closes == 1)
    chest.setItem(0, removed)
    chest.setItem(4, removed.copy())
    chest.clearContent()
    check(first.isEmpty && second.isEmpty && chest.containerSize == 5)
    check(InventoryTreasureChest("container.chest", first, null).containerSize == 2)
    check(InventoryTreasureChest("container.chest", null, second).containerSize == 3)
    check(InventoryTreasureChest("container.chest", first, first).containerSize == 2)
    check(runCatching { InventoryTreasureChest("container.chest", null, null) }.isFailure)
    first.label = null
    second.label = null
    check(!chest.hasCustomName() && chest.customName == null)
    println("Passed native combined treasure storage, slot boundaries, component preservation, name precedence, and missing-half handling")
}
