package galaxyspace.systems.SolarSystem.planets.overworld.inventory.schematics

import micdoodle8.mods.galacticraft.core.inventory.InventorySchematic
import net.minecraft.inventory.Container
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

open class InventorySchematicTier2Rocket constructor(container: Container?) : InventorySchematic(container) {
    private val size = 26

    init {
        this.stacks = NonNullList.withSize<ItemStack>(size, ItemStack.EMPTY)
    }
}
