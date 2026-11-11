package foundation.verification

import foundation.item.FoundationItemVariants
import galaxyspace.core.prefab.tile.MachineInventoryDefaults
import micdoodle8.mods.galacticraft.core.inventory.IInventoryDefaults
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.NonNullList
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTypes

fun verifyMachineInventories() {
    class MachineFixture : BlockEntity(BlockEntityTypes.CHEST, BlockPos.ZERO, Blocks.CHEST.defaultBlockState()), MachineInventoryDefaults {
        private val storage = NonNullList.withSize(3, ItemStack.EMPTY)
        var changes = 0
        override fun getInventory(): NonNullList<ItemStack> = storage
        override fun getInventoryBlockEntity(): BlockEntity = this
        override fun getName(): Component = Component.translatable("container.inventory")
        override fun setChanged() { changes++ }
        override fun canPlaceItem(slot: Int, stack: ItemStack): Boolean = slot == 1
    }
    val machine = MachineFixture()
    val container: Container = machine
    val sided: WorldlyContainer = machine
    val moon = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse("galacticraftcore:item_basic_moon")))
    check(container.containerSize == 3 && container.isEmpty && container.maxStackSize == 64)
    check(machine.getInventoryBlockEntity() === machine && machine.customName == null)
    check(machine.displayName == machine.name)
    container.setItem(2, FoundationItemVariants.stack(moon, 99, 2))
    check(container.getItem(2).count == 64 && machine.changes == 1)
    val split = container.removeItem(2, 3)
    check(split.count == 3 && FoundationItemVariants.metadata(split) == 2 && machine.changes == 2)
    val removed = container.removeItemNoUpdate(2)
    check(removed.count == 61 && container.isEmpty && machine.changes == 2)
    container.removeItem(2, 1)
    check(machine.changes == 2)
    container.setItem(0, ItemStack(Items.DIAMOND_PICKAXE, 5))
    check(container.getItem(0).count == 1)
    check(sided.getSlotsForFace(Direction.NORTH).contentEquals(intArrayOf(0, 1, 2)))
    check(sided.canPlaceItemThroughFace(1, split, null))
    check(!sided.canPlaceItemThroughFace(0, split, Direction.UP))
    check(sided.canTakeItemThroughFace(2, removed, Direction.DOWN))
    check(!sided.canTakeItemThroughFace(3, removed, Direction.DOWN))
    check(container.getItem(-1).isEmpty && container.getItem(3).isEmpty)
    container.clearContent()
    check(container.isEmpty && container.containerSize == 3 && machine.changes == 4)
    container.clearContent()
    check(machine.changes == 4)
    class DefaultsFixture : IInventoryDefaults {
        private val storage = SimpleContainer(2)
        var changes = 0
        override fun getContainerSize(): Int = storage.containerSize
        override fun isEmpty(): Boolean = storage.isEmpty
        override fun getItem(slot: Int): ItemStack = storage.getItem(slot)
        override fun setItem(slot: Int, stack: ItemStack) { storage.setItem(slot, stack); setChanged() }
        override fun removeItem(slot: Int, count: Int): ItemStack = storage.removeItem(slot, count)
        override fun removeItemNoUpdate(slot: Int): ItemStack = storage.removeItemNoUpdate(slot)
        override fun setChanged() { changes++ }
        override fun stillValid(player: net.minecraft.world.entity.player.Player): Boolean = true
        override fun getName(): Component = Component.translatable("container.inventory")
    }
    val defaults = DefaultsFixture()
    defaults.setItem(1, removed)
    check(defaults.maxStackSize == 64 && defaults.changes == 1)
    defaults.clearContent()
    check(defaults.isEmpty && defaults.containerSize == 2 && defaults.changes == 2)
    defaults.clearContent()
    check(defaults.changes == 2)
    println("Passed native machine storage, sided dispatch, stack limits, clearing, and concrete BlockEntity update dispatch")
}
