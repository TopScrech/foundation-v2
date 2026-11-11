package micdoodle8.mods.galacticraft.api.client.tabs

import net.minecraft.world.level.block.Blocks
import net.minecraft.world.item.ItemStack

open class InventoryTabVanilla constructor() : AbstractTab(0, 0, 0, ItemStack(Blocks.CRAFTING_TABLE)) {
    override fun onTabClicked() {
        TabRegistry.Companion.openInventoryGui()
    }

    override fun shouldAddToList(): Boolean {
        return true
    }
}
