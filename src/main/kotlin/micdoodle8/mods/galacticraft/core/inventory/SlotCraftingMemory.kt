package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.core.tile.TileEntityCrafting
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.IInventory
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.inventory.SlotCrafting
import net.minecraft.item.ItemStack

open class SlotCraftingMemory constructor(
    player: EntityPlayer,
    craftingInventory: InventoryCrafting,
    p_i45790_3_: IInventory,
    slotIndex: Int,
    xPosition: Int,
    yPosition: Int,
    @JvmField var tileEntity: TileEntityCrafting
) : SlotCrafting(player, craftingInventory, p_i45790_3_, slotIndex, xPosition, yPosition) {
    override fun onTake(thePlayer: EntityPlayer, stack: ItemStack): ItemStack {
        if (!stack.isEmpty()) this.tileEntity.updateMemory()
        return super.onTake(thePlayer, stack)
    }
}
