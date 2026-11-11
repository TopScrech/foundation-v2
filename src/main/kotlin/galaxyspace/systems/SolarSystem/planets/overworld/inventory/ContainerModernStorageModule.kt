package galaxyspace.systems.SolarSystem.planets.overworld.inventory

import galaxyspace.systems.SolarSystem.planets.overworld.tile.TileEntityModernStorageModule
import micdoodle8.mods.galacticraft.api.item.IItemElectric
import micdoodle8.mods.galacticraft.core.energy.EnergyUtil
import micdoodle8.mods.galacticraft.core.inventory.SlotSpecific
import net.minecraft.world.entity.player.Player as EntityPlayer
import net.minecraft.world.entity.player.Inventory as InventoryPlayer
import net.minecraft.world.inventory.AbstractContainerMenu as Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack

open class ContainerModernStorageModule constructor(
    par1InventoryPlayer: InventoryPlayer,
    private val tileEntity: TileEntityModernStorageModule
) : Container() {
    init {
        // Top slot for battery output
        this.addSlot(SlotSpecific(tileEntity, 0, 23, 24, IItemElectric::class.java))
        // Bottom slot for batter input
        this.addSlot(SlotSpecific(tileEntity, 1, 23, 48, IItemElectric::class.java))
        var var3: Int

        var3 = 0
        while (var3 < 3) {
            for (var4: Int in 0..8) {
                this.addSlot(Slot(par1InventoryPlayer, var4 + var3 * 9 + 9, 8 + var4 * 18, 99 + var3 * 18))
            }
            ++var3
        }

        var3 = 0
        while (var3 < 9) {
            this.addSlot(Slot(par1InventoryPlayer, var3, 8 + var3 * 18, 156))
            ++var3
        }

        this.tileEntity.playersUsing.add(par1InventoryPlayer.player)
    }

    override fun removed(entityplayer: EntityPlayer) {
        super.removed(entityplayer)
        this.tileEntity.playersUsing.remove(entityplayer)
    }

    override fun stillValid(par1EntityPlayer: EntityPlayer): Boolean {
        return this.tileEntity.stillValid(par1EntityPlayer)
    }

    /**
     * Called to transfer a stack from one inventory to the other eg. when shift
     * clicking.
     */
    override fun quickMoveStack(par1EntityPlayer: EntityPlayer, slotID: Int): ItemStack {
        var returnStack: ItemStack = ItemStack.EMPTY
        val slot: Slot? = this.inventorySlots.get(slotID)
        val b: Int = this.inventorySlots.size

        if (slot != null && slot.hasItem()) {
            val itemStack: ItemStack = slot.getItem()
            returnStack = itemStack.copy()
            var movedToMachineSlot: Boolean = false

            if (slotID != 0 && slotID != 1) {
                if (EnergyUtil.isElectricItem(itemStack.getItem())) {
                    if (EnergyUtil.isChargedElectricItem(itemStack)) {
                        if (!this.moveItemStackTo(itemStack, 1, 2, false)) {
                            if (EnergyUtil.isFillableElectricItem(itemStack) && !this.moveItemStackTo(
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
                        if (!this.moveItemStackTo(itemStack, 0, 1, false)) {
                            return ItemStack.EMPTY
                        }
                        movedToMachineSlot = true
                    }
                } else {
                    if (slotID < b - 9) {
                        if (!this.moveItemStackTo(itemStack, b - 9, b, false)) {
                            return ItemStack.EMPTY
                        }
                    } else if (!this.moveItemStackTo(itemStack, b - 36, b - 9, false)) {
                        return ItemStack.EMPTY
                    }
                }
            } else if (!this.moveItemStackTo(itemStack, 2, 38, false)) {
                return ItemStack.EMPTY
            }

            if (itemStack.getCount() == 0) {
                // Needed where tile has inventoryStackLimit of 1
                if (movedToMachineSlot && returnStack.getCount() > 1) {
                    val remainder: ItemStack = returnStack.copy()
                    remainder.shrink(1)
                    slot.set(remainder)
                } else {
                    slot.set(ItemStack.EMPTY)
                }
            } else {
                slot.setChanged()
            }

            if (itemStack.getCount() == returnStack.getCount()) {
                return ItemStack.EMPTY
            }

            slot.onTake(par1EntityPlayer, itemStack)
        }

        return returnStack
    }
}
