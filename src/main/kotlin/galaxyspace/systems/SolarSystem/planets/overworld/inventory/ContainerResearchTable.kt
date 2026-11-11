package galaxyspace.systems.SolarSystem.planets.overworld.inventory

import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityResearchTable
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.item.ItemStack

open class ContainerResearchTable /*
		int var3;
		for (var3 = 0; var3 < 3; ++var3) {
			for (int var4 = 0; var4 < 9; ++var4) {
				this.addSlot(
						new Slot(inventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 127 + var3 * 18));
			}
		}

		for (var3 = 0; var3 < 9; ++var3) {
			this.addSlot(new Slot(inventoryPlayer, var3, 8 + var3 * 18, 185));
		}*/ constructor(inventoryPlayer: InventoryPlayer?, private val tile: TileEntityResearchTable) :
    Container() {
    override fun removed(entityplayer: EntityPlayer) {
        super.removed(entityplayer)
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.tile.stillValid(par1EntityPlayer)
    }

    override fun quickMoveStack(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        return ItemStack.EMPTY
    }
}
