package micdoodle8.mods.galacticraft.core.client.jei

import java.util.ArrayList

import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo
import micdoodle8.mods.galacticraft.core.inventory.ContainerCrafting
import net.minecraft.world.inventory.Slot

open class MagneticCraftingTransferInfo : IRecipeTransferInfo<ContainerCrafting> {
    override fun getContainerClass(): Class<ContainerCrafting> {
        return ContainerCrafting::class.java
    }

    override fun getRecipeCategoryUid(): String {
        return VanillaRecipeCategoryUid.CRAFTING
    }

    // Crafting matrix inventory slots
    override fun getRecipeSlots(container: ContainerCrafting): MutableList<Slot?> {
        val slots: MutableList<Slot?> = ArrayList<Slot?>()
        for (i: Int in 1..9) {
            slots.add(container.getSlot(i))
        }
        return slots
    }

    // Player inventory slots
    override fun getInventorySlots(container: ContainerCrafting): MutableList<Slot?> {
        val slots: MutableList<Slot?> = ArrayList<Slot?>()
        for (i: Int in 10..<container.inventorySlots.size) {
            slots.add(container.getSlot(i))
        }
        return slots
    }

    override fun canHandle(container: ContainerCrafting): Boolean {
        return true
    }
}
