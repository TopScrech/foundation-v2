package micdoodle8.mods.galacticraft.planets.mars.inventory

import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific
import micdoodle8.mods.galacticraft.planets.mars.tile.TileEntityLaunchController
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

open class ContainerLaunchController constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityLaunchController,
    player: EntityPlayer
) : Container() {
    init {
        tileEntity.checkDestFrequencyValid()

        this.addSlot(SlotSpecific(tileEntity, 0, 152, 105, IItemElectric::class.java))

        var var6: Int
        var var7: Int

        var6 = 0
        while (var6 < 3) {
            var7 = 0
            while (var7 < 9) {
                this.addSlot(Slot(par1InventoryPlayer, var7 + var6 * 9 + 9, 8 + var7 * 18, 127 + var6 * 18))
                ++var7
            }
            ++var6
        }

        var6 = 0
        while (var6 < 9) {
            this.addSlot(Slot(par1InventoryPlayer, var6, 8 + var6 * 18, 185))
            ++var6
        }

        tileEntity.openInventory(player)
    }

    override fun removed(entityplayer: EntityPlayer) {
        super.removed(entityplayer)
        this.tileEntity.closeInventory(entityplayer)
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.tileEntity.isUsableByPlayer(par1EntityPlayer)
    }

    override fun quickMoveStack(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(par1)
        val b: Int = this.inventorySlots.size

        if (slot != null && slot.hasItem()) {
            val stack: ItemStack = slot.getItem()
            var2 = stack.copy()

            if (par1 == 0) {
                if (!this.moveItemStackTo(stack, b - 36, b, true)) {
                    return ItemStack.EMPTY
                }
            } else {
                if (EnergyUtil.isElectricItem(stack.getItem())) {
                    if (!this.moveItemStackTo(stack, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else {
                    if (par1 < b - 9) {
                        if (!this.moveItemStackTo(stack, b - 9, b, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (!this.moveItemStackTo(stack, b - 36, b - 9, false)) {
                        return ItemStack.EMPTY
                    }
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }

            if (stack.getCount() == var2.getCount()) {
                return ItemStack.EMPTY
            }

            slot.onTake(par1EntityPlayer, stack)
        }

        return var2
    }
}
