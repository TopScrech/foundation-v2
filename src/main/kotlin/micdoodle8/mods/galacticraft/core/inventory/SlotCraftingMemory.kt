package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.core.tile.TileEntityCrafting
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.Container as IInventory
import net.minecraft.world.inventory.CraftingContainer as InventoryCrafting
import foundation.inventory.getSizeInventory
import foundation.inventory.getStackInSlot
import foundation.inventory.getStackInRowAndColumn
import net.minecraft.world.inventory.ResultSlot as SlotCrafting
import net.minecraft.world.item.ItemStack

open class SlotCraftingMemory constructor(
    player: EntityPlayer,
    craftingInventory: InventoryCrafting,
    p_i45790_3_: IInventory,
    slotIndex: Int,
    xPosition: Int,
    yPosition: Int,
    @JvmField var tileEntity: TileEntityCrafting
) : SlotCrafting(player, craftingInventory, p_i45790_3_, slotIndex, xPosition, yPosition) {
    override fun onTake(thePlayer: EntityPlayer, stack: ItemStack) {
        if (!stack.isEmpty()) this.tileEntity.updateMemory()
        super.onTake(thePlayer, stack)
    }
}
