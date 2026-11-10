package galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics

import galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics.InventorySchematicEngine
import micdoodle8.mods.galacticraft.core.inventory.InventorySchematic
import net.minecraft.inventory.Container
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

open class InventorySchematicEngine constructor(container: Container?) : InventorySchematic(container) {
    private val size = 30

    init {
        this.stacks = NonNullList.withSize<ItemStack>(size, ItemStack.EMPTY)
    }
}
