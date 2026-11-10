package galaxyspace.systems.SolarSystem.planets.overworld.inventory

import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityModernStorageModule
import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack

open class ContainerModernStorageModule constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityModernStorageModule
) : Container() {
    init {
        // Top slot for battery output
        this.addSlotToContainer(SlotSpecific(tileEntity, 0, 23, 24, IItemElectric::class.java))
        // Bottom slot for batter input
        this.addSlotToContainer(SlotSpecific(tileEntity, 1, 23, 48, IItemElectric::class.java))
        var var3: Int

        var3 = 0
        while (var3 < 3) {
            for (var4: Int in 0..8) {
                this.addSlotToContainer(Slot(par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 99 + var3 * 18))
            }
            ++var3
        }

        var3 = 0
        while (var3 < 9) {
            this.addSlotToContainer(Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 156))
            ++var3
        }

        this.tileEntity.playersUsing.add(par1InventoryPlayer.player)
    }

    override fun onContainerClosed(entityplayer: EntityPlayer) {
        super.onContainerClosed(entityplayer)
        this.tileEntity.playersUsing.remove(entityplayer)
    }

    override fun canInteractWith(par1EntityPlayer: EntityPlayer): Boolean {
        return this.tileEntity.isUsableByPlayer(par1EntityPlayer)
    }

    /**
     * Called to transfer a stack from one inventory to the other eg. when shift
     * clicking.
     */
    override fun transferStackInSlot(par1EntityPlayer: EntityPlayer, slotID: Int): ItemStack {
        var returnStack: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(slotID)
        val b: Int = this.inventorySlots.size

        if (slot != null && slot.getHasStack()) {
            val itemStack: ItemStack = slot.getStack()
            returnStack = itemStack.copy()
            var movedToMachineSlot: Boolean = false

            if (slotID != 0 && slotID != 1) {
                if (EnergyUtil.isElectricItem(itemStack.getItem())) {
                    if (EnergyUtil.isChargedElectricItem(itemStack)) {
                        if (!this.mergeItemStack(itemStack, 1, 2, false)) {
                            if (EnergyUtil.isFillableElectricItem(itemStack) && !this.mergeItemStack(
                                    itemStack,
                                    0,
                                    1,
                                    false
                                )
                            ) {
                                return ItemStack.EMPTY
                            }
                        }
                        movedToMachineSlot = true
                    } else {
                        if (!this.mergeItemStack(itemStack, 0, 1, false)) {
                            return ItemStack.EMPTY
                        }
                        movedToMachineSlot = true
                    }
                } else {
                    if (slotID < b - 9) {
                        if (!this.mergeItemStack(itemStack, b - 9, b, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (!this.mergeItemStack(itemStack, b - 36, b - 9, false)) {
                        return ItemStack.EMPTY
                    }
                }
            } else if (!this.mergeItemStack(itemStack, 2, 38, false)) {
                return ItemStack.EMPTY
            }

            if (itemStack.getCount() == 0) {
                // Needed where tile has inventoryStackLimit of 1
                if (movedToMachineSlot && returnStack.getCount() > 1) {
                    val remainder: ItemStack = returnStack.copy()
                    remainder.shrink(1)
                    slot.putStack(remainder)
                } else {
                    slot.putStack(ItemStack.EMPTY)
                }
            } else {
                slot.onSlotChanged()
            }

            if (itemStack.getCount() == returnStack.getCount()) {
                return ItemStack.EMPTY
            }

            slot.onTake(par1EntityPlayer, itemStack)
        }

        return returnStack
    }
}
