package foundation.verification

import foundation.item.FoundationItemVariants
import galaxyspace.core.prefab.inventory.GSPersistantInventoryCrafting
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.RocketAssemblyInventory
import micdoodle8.mods.galacticraft.core.inventory.PersistantInventoryCrafting
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack

fun verifyCraftingInventories() {
    var changes = 0
    val menu = object : AbstractContainerMenu(MenuType.CRAFTING, 0) {
        override fun quickMoveStack(player: Player, slot: Int): ItemStack = ItemStack.EMPTY
        override fun stillValid(player: Player): Boolean = true
        override fun slotsChanged(container: Container) { changes++ }
    }
    val moon = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse("galacticraftcore:item_basic_moon")))
    val core = PersistantInventoryCrafting()
    core.craftingEventHandler = menu
    core.setInventorySlotContentsNoUpdate(0, FoundationItemVariants.stack(moon, 2, 1))
    check(changes == 0)
    core.setItem(8, FoundationItemVariants.stack(moon, 3, 2))
    check(changes == 1 && core.width == 3 && core.height == 3 && core.containerSize == 9)
    check(FoundationItemVariants.metadata(core.getStackInRowAndColumn(2, 2)) == 2)
    check(core.getStackInRowAndColumn(3, 0).isEmpty)
    val input = core.asCraftInput()
    check(input.width() == 3 && input.height() == 3)
    check(FoundationItemVariants.metadata(input.getItem(8)) == 2)
    check(core.removeItem(0, 1).count == 1 && changes == 2)
    check(core.removeItemNoUpdate(0).count == 1 && changes == 2)
    val snapshot = core.items
    core.setItemNoUpdate(8, FoundationItemVariants.stack(moon, 1, 0))
    check(snapshot[8].count == 3 && core.getItem(8).count == 1 && changes == 2)
    core.clearContent()
    check(core.isEmpty && core.containerSize == 9 && changes == 3)
    core.setItemNoUpdate(8, FoundationItemVariants.stack(moon, 1, 2))
    val positioned = core.asPositionedCraftInput()
    check(positioned.left() == 2 && positioned.top() == 2)
    check(positioned.input().width() == 1 && positioned.input().height() == 1)
    check(FoundationItemVariants.metadata(positioned.input().getItem(0)) == 2)
    core.craftingEventHandler = null
    core.setItem(0, FoundationItemVariants.stack(moon, 1, 1))
    check(changes == 3)

    val galaxy = GSPersistantInventoryCrafting()
    galaxy.gs_eventHandler = menu
    galaxy.setItem(4, FoundationItemVariants.stack(moon, 1, 1))
    check(changes == 4 && galaxy.containerSize == 9)
    check(FoundationItemVariants.metadata(galaxy.getStackInRowAndColumn(1, 1)) == 1)
    val rocket = RocketAssemblyInventory()
    rocket.craftingEventHandler = menu
    rocket.setItem(0, FoundationItemVariants.stack(moon, 1, 0))
    rocket.setItem(15, FoundationItemVariants.stack(moon, 1, 2))
    check(changes == 6 && rocket.width == 2 && rocket.height == 8 && rocket.containerSize == 16)
    check(FoundationItemVariants.metadata(rocket.getStackInRowAndColumn(1, 7)) == 2)
    check(rocket.asCraftInput().size() == 16)
    check(FoundationItemVariants.metadata(rocket.asCraftInput().getItem(15)) == 2)
}
