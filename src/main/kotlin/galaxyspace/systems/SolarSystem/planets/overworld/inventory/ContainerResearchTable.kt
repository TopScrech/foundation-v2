package galaxyspace.systems.SolarSystem.planets.overworld.inventory

import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityResearchTable
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.item.ItemStack

open class ContainerResearchTable /*
		int var3;
		for (var3 = 0; var3 < 3; ++var3) {
			for (int var4 = 0; var4 < 9; ++var4) {
				this.addSlotToContainer(
						new Slot(inventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 127 + var3 * 18));
			}
		}

		for (var3 = 0; var3 < 9; ++var3) {
			this.addSlotToContainer(new Slot(inventoryPlayer, var3, 8 + var3 * 18, 185));
		}*/ constructor(inventoryPlayer: InventoryPlayer?, private val tile: TileEntityResearchTable) :
    Container() {
    override fun onContainerClosed(entityplayer: EntityPlayer) {
        super.onContainerClosed(entityplayer)
    }

    override fun canInteractWith(par1EntityPlayer: EntityPlayer): Boolean {
        return this.tile.isUsableByPlayer(par1EntityPlayer)
    }

    override fun transferStackInSlot(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        return ItemStack.EMPTY
    }
}
