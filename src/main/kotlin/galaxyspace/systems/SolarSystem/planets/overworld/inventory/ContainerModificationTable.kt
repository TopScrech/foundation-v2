package galaxyspace.systems.SolarSystem.planets.overworld.inventory

import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityModificationTable
import galaxyspace.api.item.IModificationItem
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

open class ContainerModificationTable constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityModificationTable
) : Container() {
    init {
        this.addSlot(SlotSpecific(tileEntity, 0, 10, 55, IModificationItem::class.java))

        var var3: Int

        var3 = 0
        while (var3 < 3) {
            for (var4: Int in 0..8) {
                this.addSlot(Slot(par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 127 + var3 * 18))
            }
            ++var3
        }

        var3 = 0
        while (var3 < 9) {
            this.addSlot(Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 185))
            ++var3
        }
    }

    override fun removed(entityplayer: EntityPlayer) {
        super.removed(entityplayer)
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.tileEntity.stillValid(par1EntityPlayer)
    }

    override fun quickMoveStack(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val var3: Slot? = this.inventorySlots.get(par1)

        if (var3 != null && var3.hasItem()) {
            val var4: ItemStack = var3.getItem()
            var2 = var4.copy()

            if (par1 <= 0) {
                if (!this.moveItemStackTo(var4, 0, 35, true)) {
                    return ItemStack.EMPTY
                }

                if (par1 == 1) {
                    var3.onQuickCraft(var4, var2)
                }
            } else {
                if (var4.getItem() is IModificationItem) {
                    if (!this.moveItemStackTo(var4, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                }
            }

            if (var4.getCount() == 0) {
                var3.set(ItemStack.EMPTY)
            } else {
                var3.setChanged()
            }

            if (var4.getCount() == var2.getCount()) {
                return ItemStack.EMPTY
            }

            var3.onTake(par1EntityPlayer, var4)
        }

        return var2
    }
}
