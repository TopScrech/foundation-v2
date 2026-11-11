package foundation.verification

import foundation.inventory.FoundationBenchInventory
import foundation.item.FoundationItemVariants
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics.InventorySchematicBody
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics.InventorySchematicBooster
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics.InventorySchematicCone
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics.InventorySchematicEngine
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics.InventorySchematicFins
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics.InventorySchematicOxTank
import galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics.InventorySchematicTier2Rocket as GalaxyTier2
import micdoodle8.mods.galacticraft.core.inventory.InventoryBuggyBench
import micdoodle8.mods.galacticraft.core.inventory.InventoryRocketBench
import micdoodle8.mods.galacticraft.core.inventory.InventorySchematic
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.InventorySchematicAstroMiner
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.InventorySchematicTier3Rocket
import micdoodle8.mods.galacticraft.planets.mars.inventory.InventorySchematicCargoRocket
import micdoodle8.mods.galacticraft.planets.mars.inventory.InventorySchematicTier2Rocket
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

fun verifyBenchInventories() {
    var changes = 0
    var changedContainer: Container? = null
    val menu = object : AbstractContainerMenu(MenuType.CRAFTING, 0) {
        override fun quickMoveStack(player: Player, slot: Int): ItemStack = ItemStack.EMPTY
        override fun stillValid(player: Player): Boolean = true
        override fun slotsChanged(container: Container) { changes++; changedContainer = container }
    }
    val moon = requireNotNull(BuiltInRegistries.ITEM.getValue(Identifier.parse("galacticraftcore:item_basic_moon")))
    val inventories = listOf(
        InventoryRocketBench(menu) to 18, InventoryBuggyBench(menu) to 32,
        InventorySchematicTier2Rocket(menu) to 22, InventorySchematicCargoRocket(menu) to 17,
        InventorySchematicTier3Rocket(menu) to 22, InventorySchematicAstroMiner(menu) to 15,
        InventorySchematic(menu) to 1, InventorySchematicBody(menu) to 30,
        InventorySchematicBooster(menu) to 30, InventorySchematicCone(menu) to 30,
        InventorySchematicEngine(menu) to 30, InventorySchematicFins(menu) to 30,
        InventorySchematicOxTank(menu) to 14, GalaxyTier2(menu) to 26
    )
    for ((inventory, size) in inventories) {
        val container: Container = inventory
        check(container.containerSize == size && container.isEmpty && container.maxStackSize == 64)
        val last = size - 1
        val before = changes
        container.setItem(last, FoundationItemVariants.stack(moon, 7, 2))
        check(changes == before + 1 && changedContainer === container)
        check(FoundationItemVariants.metadata(container.getItem(last)) == 2)
        check(inventory.getStackInRowAndColumn(last % 5, last / 5) === container.getItem(last))
        check(inventory.getStackInRowAndColumn(5, 0).isEmpty)
        check(inventory.getStackInRowAndColumn(0, -1).isEmpty)
        check(container.getItem(-1).isEmpty && container.getItem(size).isEmpty)
        val removed = container.removeItem(last, 2)
        check(removed.count == 2 && container.getItem(last).count == 5)
        check(FoundationItemVariants.metadata(removed) == 2 && changes == before + 2)
        check(container.removeItemNoUpdate(last).count == 5 && changes == before + 2)
        container.setItem(0, FoundationItemVariants.stack(moon, 99, 1))
        check(container.getItem(0).count == 64)
        check(inventory.removeStackFromSlot(0).count == 64 && changes == before + 4)
        container.setItem(0, ItemStack(Items.DIAMOND_PICKAXE, 7))
        check(container.getItem(0).count == 1)
        container.clearContent()
        check(container.isEmpty && container.containerSize == size && changes == before + 6)
        val slot = Slot(container, last, 0, 0)
        slot.set(FoundationItemVariants.stack(moon, 3, 1))
        check(FoundationItemVariants.metadata(slot.item) == 1)
        check(!container.canPlaceItem(last, slot.item))
        inventory.clear()
        check(inventory.isEmpty)
    }
    val detached: FoundationBenchInventory = InventorySchematicEngine(null)
    detached.setItem(29, FoundationItemVariants.stack(moon, 1, 2))
    check(detached.getItem(29).count == 1)
    detached.clearContent()
    check(detached.isEmpty && detached.containerSize == 30)
    println("Passed 14 native schematic inventories, slot counts, menu callbacks, stack limits, and component preservation")
}
