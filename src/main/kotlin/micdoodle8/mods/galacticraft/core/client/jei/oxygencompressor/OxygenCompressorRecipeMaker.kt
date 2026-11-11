package micdoodle8.mods.galacticraft.core.client.jei.oxygencompressor

import java.util.ArrayList

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry
import micdoodle8.mods.galacticraft.api.item.EnumExtendedInventorySlot
import micdoodle8.mods.galacticraft.core.items.ItemOxygenTank
import net.minecraft.world.item.ItemStack

object OxygenCompressorRecipeMaker {
    @JvmStatic
    fun getRecipesList(): MutableList<OxygenCompressorRecipeWrapper?> {
        val recipes: MutableList<OxygenCompressorRecipeWrapper?> = ArrayList<OxygenCompressorRecipeWrapper?>()

        for (stack: ItemStack? in GalacticraftRegistry.listAllGearForSlot(EnumExtendedInventorySlot.LEFT_TANK)) {
            if (stack != null && stack.getItem() is ItemOxygenTank) {
                recipes.add(OxygenCompressorRecipeWrapper(stack.copy()))
            }
        }

        return recipes
    }
}
