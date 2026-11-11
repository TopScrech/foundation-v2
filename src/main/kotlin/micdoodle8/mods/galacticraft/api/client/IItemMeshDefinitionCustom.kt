package micdoodle8.mods.galacticraft.api.client

import net.minecraft.client.renderer.ItemMeshDefinition
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.world.item.ItemStack

fun interface IItemMeshDefinitionCustom : ItemMeshDefinition {
    fun getLocation(stack: ItemStack?): ModelResourceLocation

    override fun getModelLocation(stack: ItemStack): ModelResourceLocation {
        return getLocation(stack)
    }

    companion object {
        @JvmStatic
        fun create(lambda: IItemMeshDefinitionCustom?): ItemMeshDefinition? {
            return lambda
        }
    }
}
