package micdoodle8.mods.galacticraft.core.inventory

import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.core.GCItems
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.tile.TileEntityRefinery
import micdoodle8.mods.galacticraft.core.util.FluidUtil
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

open class ContainerRefinery constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityRefinery,
    player: EntityPlayer
) : Container() {
    init {
        // Electric Input Slot
        this.addSlot(SlotSpecific(tileEntity, 0, 38, 51, IItemElectric::class.java))

        // To be smelted
        this.addSlot(Slot(tileEntity, 1, 7, 7))

        // Smelting result
        this.addSlot(Slot(tileEntity, 2, 153, 7))
        var var3: Int

        var3 = 0
        while (var3 < 3) {
            for (var4: Int in 0..8) {
                this.addSlot(
                    Slot(
                        par1InventoryPlayer,
                        var4 + var3 * 9 + 9,
                        8 + var4 * 18,
                        104 + var3 * 18 - 18
                    )
                )
            }
            ++var3
        }

        var3 = 0
        while (var3 < 9) {
            this.addSlot(Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 144))
            ++var3
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

    /**
     * Called to transfer a stack from one inventory to the other eg. when shift
     * clicking.
     */
    override fun quickMoveStack(par1EntityPlayer: EntityPlayer, par1: Int): ItemStack {
        var var2: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(par1)

        if (slot != null && slot.hasItem()) {
            val var4: ItemStack = slot.getItem()
            var2 = var4.copy()

            if (par1 < 3) {
                if (!this.moveItemStackTo(var4, 3, 39, true)) {
                    return ItemStack.EMPTY
                }

                if (par1 == 2) {
                    slot.onQuickCraft(var4, var2)
                }
            } else {
                if (EnergyUtil.isElectricItem(var4.getItem())) {
                    if (!this.moveItemStackTo(var4, 0, 1, false)) {
                        return ItemStack.EMPTY
                    }
                } else {
                    if (FluidUtil.isOilContainerAny(var4)) {
                        if (!this.moveItemStackTo(var4, 1, 2, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (FluidUtil.isPartialContainer(var4, GCItems.fuelCanister)) {
                        if (!this.moveItemStackTo(var4, 2, 3, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (par1 < 30) {
                        if (!this.moveItemStackTo(var4, 30, 39, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (!this.moveItemStackTo(var4, 3, 30, false)) {
                        return ItemStack.EMPTY
                    }
                }
            }

            if (var4.getCount() == 0) {
                slot.set(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }

            if (var4.getCount() == var2.getCount()) {
                return ItemStack.EMPTY
            }

            slot.onTake(par1EntityPlayer, var4)
        }

        return var2
    }
}
