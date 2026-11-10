package galaxyspace.systems.BarnardsSystem.core

import galaxyspace.systems.BarnardsSystem.core.BRBlocks
import galaxyspace.systems.BarnardsSystem.core.BRItems
import galaxyspace.systems.BarnardsSystem.core.prefab.items.ItemBasicBR
import galaxyspace.systems.BarnardsSystem.core.prefab.items.ItemFoodBR
import galaxyspace.core.prefab.items.ItemDoorGS
import galaxyspace.core.GSItems
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

object BRItems {
    @JvmField
    val BASIC: Item = ItemBasicBR()

    @JvmField
    val FOODS: Item = ItemFoodBR()

    @JvmField
    val VIOLET_DOOR: Item = ItemDoorGS("barnarda_c_violet_door", BRBlocks.BARNARDA_C_VIOLET_DOOR)

    @JvmStatic
    fun initialize() {
        registerItem(BASIC)
        registerItem(FOODS)
        registerItem(VIOLET_DOOR)
    }

    @JvmStatic
    fun oreDictRegistration() {
        OreDictionary.registerOre("slimeball", ItemStack(BASIC, 1, 2))

        BRBlocks.BARNARDA_C_VIOLET_DOOR.setDoorItem(VIOLET_DOOR)
    }

    @JvmStatic
    private fun registerItem(item: Item) {
        GSItems.registerItem(item)
    }
}
