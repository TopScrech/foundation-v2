package galaxyspace.core.client.gui.book.pages.bodies

import galaxyspace.core.client.gui.book.pages.Page_Systems
import galaxyspace.systems.ACentauriSystem.ACentauriSystemBodies
import galaxyspace.systems.ACentauriSystem.core.ACBlocks
import net.minecraft.item.ItemStack

open class Page_ProximaSystem constructor() : Page_Systems((ACentauriSystemBodies.ProximaSystem)!!) {
    init {
        this.setResources(
            ACentauriSystemBodies.proxima_b,
            ItemStack(ACBlocks.PROXIMA_B_BLOCKS, 1, 5),
            ItemStack(ACBlocks.PROXIMA_B_BLOCKS, 1, 6),
            ItemStack(ACBlocks.PROXIMA_B_BLOCKS, 1, 7),
            ItemStack(ACBlocks.PROXIMA_B_BLOCKS, 1, 8),
            ItemStack(ACBlocks.PROXIMA_B_BLOCKS, 1, 9),
            ItemStack(ACBlocks.PROXIMA_B_BLOCKS, 1, 10)
        )
    }
}
